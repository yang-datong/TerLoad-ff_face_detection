package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.hyphenate.util.DateUtils
import kotlinx.android.synthetic.main.view_send_message_item.view.*
import java.text.SimpleDateFormat

/**
 * @author 杨景
 * @description:
 * @date :2021/1/2 23:54
 */
class MessageListAdapter(var context: Context, var messages: MutableList<EMMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var messageListViewHolder: RecyclerView.ViewHolder

    companion object {
        const val ITEM_TYPE_SEND_MESSAGE = 0;
        const val ITEM_TYPE_RECEIVE_MESSAGE = 1;
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
        }
        else messageListViewHolder = holder as MessageReceiveListViewHolder

        messageListViewHolder.itemView.textView2.text = SimpleDateFormat("HH:mm").format(messages[position].msgTime)
        if (messages[position].type == EMMessage.Type.TXT) //如果消息为文本类型
            messageListViewHolder.itemView.textView3.text = (messages[position].body as EMTextMessageBody).message
        else messageListViewHolder.itemView.textView3.text = "非文本消息！"

        messageListViewHolder.itemView.textView2.visibility = if (isShowTimestamp(position)) View.VISIBLE else View.GONE
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