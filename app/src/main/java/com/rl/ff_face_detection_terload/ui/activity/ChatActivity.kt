package com.rl.ff_face_detection_terload.ui.activity

import android.app.Activity
import android.content.Intent
import android.media.MediaRecorder
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.adapter.MessageListAdapter
import com.rl.ff_face_detection_terload.adapter.MessageListenerAdapter
import com.rl.ff_face_detection_terload.contract.ChatContract
import com.rl.ff_face_detection_terload.extensions.formatTimestamp
import com.rl.ff_face_detection_terload.presenter.ChatPresenter
import com.rl.ff_face_detection_terload.widget.MicrophoneDialog
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.title_bar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.io.File


/**
 * @author 杨景
 * @description:
 * @date :2023/1/3 1:22
 */
class ChatActivity : BaseActivity(), ChatContract.View {
    companion object {
        private const val TAG = "ChatActivity"
        private const val REQUEST_CODE = 0x111
    }

    override fun getLayoutResID() = R.layout.activity_chat

    private lateinit var username: String

    val presenter by lazy { ChatPresenter(this) }

    private val msgListener = object : MessageListenerAdapter() {
        override fun onMessageReceived(messages: MutableList<EMMessage>?) {
            presenter.addMessage(username, messages)
            runOnUiThread {
                recycleview.adapter?.notifyDataSetChanged()
                scrollTOBottom()
            }
        }
    }

    private val microphoneDialog by lazy { MicrophoneDialog(this) }

    override fun inits() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.bottom_nav_background_color)
        username = intent.getStringExtra("username").toString()
        initView()
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        presenter.loadData(username)
        val hasMessage = intent.getStringExtra("message")
        if (!hasMessage.isNullOrEmpty())
            send(hasMessage)
    }

    private fun initView() {
        title = username
        tv_title.text = username
        img_ret.setOnClickListener { finish() }
        img_option.isVisible = true
        img_option.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_more_horiz_24))
        img_option.setOnClickListener {
            val intent = Intent(this, UserDetailedActivity::class.java).apply {
                putExtra("username", username)
                putExtra("afterRemindingNeedFinish", true)
            }
            startActivityForResult(intent, REQUEST_CODE)
        }
        recycleview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = MessageListAdapter(context, presenter.messages)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    //当前状态没发生滚动时
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val linearLayoutManager = layoutManager as LinearLayoutManager
                        //如果当前可见条目的位置为第1个
                        if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                            presenter.loadMoreData(username)//加载更多数据
                        }
                    }
                }
            })
        }
        input_message.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                sed_message.isEnabled = !s.isNullOrEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        sed_message.setOnClickListener { send(null) }
        image_voice.setOnClickListener {
            it.visibility = View.GONE
            input_message.visibility = View.GONE
            image_text.visibility = View.VISIBLE
            long_voice_dialog.visibility = View.VISIBLE
        }
        image_text.setOnClickListener {
            it.visibility = View.GONE
            long_voice_dialog.visibility = View.GONE
            image_voice.visibility = View.VISIBLE
            input_message.visibility = View.VISIBLE
        }
        long_voice_dialog.setOnLongClickListener {
            microphoneDialog.show()
            microphoneDialog.showImgAnimation()
            startRecording()
            true
        }
        microphoneDialog.onSendVoiceListener = object : MicrophoneDialog.OnSendVoiceListener {
            override fun sendVoice() {
                microphoneDialog.dismissImgAnimation()
                microphoneDialog.dismiss()
                stopRecording()
                val duration = ((stopTime - startTime) / 1000).toInt()

                val voiceUri = FileProvider.getUriForFile(
                        this@ChatActivity,
                        "${packageName}.fileprovider",
                        File("${externalCacheDir?.absolutePath}/$audioFileName")
                )
                Log.d(TAG, "sendVoice-> audio duration: $duration , uri path : ${voiceUri.path}")
                presenter.sendAudioMessage(username, voiceUri, duration)
            }

            override fun stopVoice() {
                microphoneDialog.dismissImgAnimation()
                microphoneDialog.dismiss()
                stopRecording()
            }
        }

        //TODO 这里有问题，会导致语音时间戳错误
//        microphoneDialog.setOnDismissListener {
//            microphoneDialog.dismissImgAnimation()
//            stopRecording()
//            Log.d(TAG, "initView: setOnDismissListener")
//        }
    }


    private lateinit var recorder: MediaRecorder
    private var output: String? = null
    private var startTime: Long = 0
    private var stopTime: Long = 0
    private var audioFileName: String? = null

    private fun startRecording() {
        GlobalScope.launch {
            audioFileName = "recording_${System.currentTimeMillis()}.mp4"
            recorder = MediaRecorder()
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            output = "${externalCacheDir?.absolutePath}/$audioFileName"
            Log.d(TAG, "startRecording-> outputFile: $output")
            recorder.setOutputFile(output)
            recorder.prepare()
            recorder.start()
            startTime = System.currentTimeMillis()
            Log.d(TAG, "startRecording-> time: ${formatTimestamp(startTime)}")
        }
    }

    private fun stopRecording() {
        recorder.stop()
        recorder.release()
        stopTime = System.currentTimeMillis()
        Log.d(TAG, "stopRecording-> time: ${formatTimestamp(stopTime)}")
        // Do something with output and duration
    }


    override fun onMessageLoad() {
        recycleview.adapter?.notifyDataSetChanged()
        scrollTOBottom()
    }

    private fun send(hasMessage: String?) {
        if (!hasMessage.isNullOrEmpty())
            presenter.sendMessage(username, hasMessage)
        else
            presenter.sendMessage(username, input_message.text.toString())
    }

    override fun onStartSend() {
        recycleview.adapter?.notifyDataSetChanged()
    }

    override fun onSendSuccess() {
        recycleview.adapter?.notifyDataSetChanged()
        input_message.text.clear()
        scrollTOBottom()
    }

    private fun scrollTOBottom() {
        recycleview.scrollToPosition(presenter.messages.size - 1)
    }

    override fun onSendFailed(error: String?) {
        recycleview.adapter?.notifyDataSetChanged()
        input_message.text.clear()
        toast("发送失败:${error}")
    }

    override fun onMoreMessageLoad(size: Int) {
        val linearLayoutManager = recycleview.layoutManager as LinearLayoutManager
        linearLayoutManager.scrollToPositionWithOffset(size, 0)
        recycleview.adapter?.notifyDataSetChanged()
    }

    override fun onErrorLogin() {
        alert("", "当前账号已在其他设备上登录!") {
            yesButton {
                startActivity<LoginActivity>()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val hasMessage = data.getStringExtra("message")
            if (!hasMessage.isNullOrEmpty())
                send(hasMessage)
        }
    }
}
