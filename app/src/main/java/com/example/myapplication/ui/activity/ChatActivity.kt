package com.example.myapplication.ui.activity

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.adapter.MessageListAdapter
import com.example.myapplication.adapter.MessageListenerAdapter
import com.example.myapplication.contract.ChatContract
import com.example.myapplication.presenter.ChatPresenter
import com.example.myapplication.widget.MicrophoneDialog
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import kotlinx.android.synthetic.main.activity_chat.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

/**
 * @author 杨景
 * @description:
 * @date :2021/1/3 1:22
 */
class ChatActivity : BaseActivity() ,ChatContract.View {
    override fun getLayoutResID() = R.layout.activity_chat
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    val presenter by lazy { ChatPresenter(this) }
    lateinit var username : String

     private val msgListener = object : MessageListenerAdapter() {
         override fun onMessageReceived(messages: MutableList<EMMessage>?) {
                presenter.addMessage(username,messages)
                runOnUiThread {
                    recycleview.adapter?.notifyDataSetChanged()
                    scrollTOBottom()}
         }
     }

    val microphoneDialog by lazy { MicrophoneDialog(this) }

    override fun inits() {
        username = intent.getStringExtra("username").toString()
        title = "$username"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        sed_message.setOnClickListener { send() }
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        presenter.loadData(username)
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
            true
        }
        microphoneDialog.onSendVoiceListener = object : MicrophoneDialog.OnSendVoiceListener{
            override fun sendVoice() {
            }
        }



    }

    override fun onMessageLoad() {
        recycleview.adapter?.notifyDataSetChanged()
        scrollTOBottom()
    }

    private fun send() {
        presenter.sendMessage(username,input_message.text.toString())
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
        recycleview.scrollToPosition(presenter.messages.size-1)
    }

    override fun onSendFailed() {
        recycleview.adapter?.notifyDataSetChanged()
        input_message.text.clear()
        toast("发送失败")
    }

    override fun onMoreMessageLoad(size: Int) {
        val linearLayoutManager = recycleview.layoutManager as LinearLayoutManager
        linearLayoutManager.scrollToPositionWithOffset(size,0)
        recycleview.adapter?.notifyDataSetChanged()
    }

    override fun onErrorLogin() {
        alert("","当前账号已在其他设备上登录!"){
            yesButton { startActivity<LoginActivity>()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

}