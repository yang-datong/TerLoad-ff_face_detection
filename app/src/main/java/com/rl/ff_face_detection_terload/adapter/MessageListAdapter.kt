package com.rl.ff_face_detection_terload.adapter

import android.content.ContentResolver
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.chat.EMVoiceMessageBody
import com.hyphenate.util.DateUtils
import com.rl.ff_face_detection_terload.R
import kotlinx.android.synthetic.main.view_send_message_item.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * @author 杨景
 * @description:
 * @date :2023/1/2 23:54
 */
class MessageListAdapter(var context: Context, var messages: MutableList<EMMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var messageListViewHolder: RecyclerView.ViewHolder

    companion object {
        const val ITEM_TYPE_SEND_MESSAGE = 0;
        const val ITEM_TYPE_RECEIVE_MESSAGE = 1;
        const val TAG = "MessageListAdapter"
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].direct() == EMMessage.Direct.SEND)
            ITEM_TYPE_SEND_MESSAGE
        else
            ITEM_TYPE_RECEIVE_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val sendInflate = LayoutInflater.from(context).inflate(R.layout.view_send_message_item, parent, false)
        val receiveInflate = LayoutInflater.from(context).inflate(R.layout.view_receive_message_item, parent, false)
        return if (viewType == ITEM_TYPE_SEND_MESSAGE)
            MessageSendListViewHolder(sendInflate)
        else
            MessageReceiveListViewHolder(receiveInflate)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_TYPE_SEND_MESSAGE) {
            messageListViewHolder = holder as MessageSendListViewHolder
            messages[position].status()?.let {
                messageListViewHolder.itemView.progressBar.visibility = if (it == EMMessage.Status.INPROGRESS) View.VISIBLE else View.GONE
                messageListViewHolder.itemView.imageView5.visibility = if (it == EMMessage.Status.SUCCESS) View.GONE else View.VISIBLE
            }
        } else messageListViewHolder = holder as MessageReceiveListViewHolder

        messageListViewHolder.itemView.textView2.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(messages[position].msgTime)
        when (messages[position].type) {
            EMMessage.Type.TXT //如果消息为文本类型
            -> messageListViewHolder.itemView.textView3.text = (messages[position].body as EMTextMessageBody).message
            EMMessage.Type.VOICE -> { //如果消息为语音类型
                messageListViewHolder.itemView.textView3.text = "${(messages[position].body as EMVoiceMessageBody).length}'' "
                messageListViewHolder.itemView.textView3.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_bar_chart_24, 0)
            }
            else -> messageListViewHolder.itemView.textView3.text = "非文本消息！"
        }

        messageListViewHolder.itemView.textView3.setOnClickListener {
            if (messages[position].type == EMMessage.Type.VOICE) {
                val em = messages[position].body as EMVoiceMessageBody
                Log.d(TAG, "duration: ${em.length}, uri path: ${em.localUri}, file: ${em.fileName}")
                playVoice(em.localUri)
            }
        }

//        messageListViewHolder.itemView.imageView3.setOnClickListener {
//            val intent = Intent(context, UserDetailedActivity::class.java).apply {
//                putExtra("username", "username")
//                putExtra("afterRemindingNeedFinish", true)
//            }
//            context.startActivityForResult(intent, 11)
//        }

        messageListViewHolder.itemView.textView2.visibility = if (isShowTimestamp(position)) View.VISIBLE else View.GONE
    }

    private fun isUriExists(uri: Uri): Boolean {
        return if (uri.scheme == "file") {
            val file = File(uri.path)
            file.exists()
        } else {
            val contentResolver: ContentResolver = context.contentResolver
            val type = contentResolver.getType(uri)
            type != null
        }
    }

    private fun playVoice(uri: Uri?) {
        Log.d(TAG, "playVoice: uri path=${uri}")
        if (uri != null && isUriExists(uri)) {
            try {
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(context, uri)
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer.setOnCompletionListener {
                    Log.d(TAG, "playVoice: release()")
                    mediaPlayer.release()
                }
                mediaPlayer.prepare()
                mediaPlayer.start()
                Log.d(TAG, "playVoice: start()")
            } catch (e: Exception) {
                Log.e(TAG, "playVoice -> ", e)
            }
        } else {
            Log.e(TAG, "playVoice: isUriExists = false")
        }

    }

    private fun isShowTimestamp(position: Int): Boolean {
        var isShow = true
        if (position > 0)
            isShow = !DateUtils.isCloseEnough(messages[position].msgTime, messages[position - 1].msgTime)
        return isShow
    }

    class MessageSendListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class MessageReceiveListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}