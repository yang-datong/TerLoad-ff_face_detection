package com.rl.ff_face_detection_terload.presenter

import android.net.Uri
import android.util.Log
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.rl.ff_face_detection_terload.contract.ChatContract
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


/**
 * @author 杨景
 * @description:
 * @date :2021/1/3 0:15
 */
class ChatPresenter(val view: ChatContract.View) : ChatContract.Presenter {
    private val pageSize: Int = 5
    val messages = mutableListOf<EMMessage>()
    private val handler by lazy { android.os.Handler() }

    override fun sendMessage(contact: String, message: String) {
        val createTxtSendMessage = EMMessage.createTxtSendMessage(message, contact)
        EMClient.getInstance().chatManager().sendMessage(createTxtSendMessage)
        uiThread {
            view.onStartSend()
        }
        messages.add(createTxtSendMessage)
        createTxtSendMessage.setMessageStatusCallback(object : EMCallBack {
            override fun onSuccess() {
                uiThread {
                    println("姓名：$contact 消息：$message")
                    view.onSendSuccess()
                }
            }

            override fun onProgress(progress: Int, status: String?) {}
            override fun onError(code: Int, error: String?) {
                uiThread {
                    view.onSendFailed(error)
                }
            }
        })
    }

    override fun sendAudioMessage(contact: String, voiceUri: Uri, duration: Int) {
        doAsync {
            val audioMessage: EMMessage = EMMessage.createVoiceSendMessage(voiceUri, duration, contact)
//            audioMessage.chatType = EMMessage.ChatType.Chat //`Chat`、`GroupChat` 和 `ChatRoom`，表示单聊、群聊或聊天室
            EMClient.getInstance().chatManager().sendMessage(audioMessage)
            uiThread {
                view.onStartSend()
            }
            messages.add(audioMessage)
            audioMessage.setMessageStatusCallback(object : EMCallBack {
                override fun onSuccess() {
                    uiThread {
                        println("姓名：$contact 语音消息：${voiceUri.path},时长： ${duration}(秒数)")
                        view.onSendSuccess()
                    }
                }

                override fun onProgress(progress: Int, status: String?) {}
                override fun onError(code: Int, error: String?) {
                    uiThread {
                        Log.e("ChatPresenter", "sendAudioMessage error")
                        view.onSendFailed(error)
                    }
                }
            })
        }
    }

    override fun addMessage(username: String, mes: MutableList<EMMessage>?) {
        mes?.let { messages.addAll(it) }
        val conversation = EMClient.getInstance().chatManager().getConversation(username)
        conversation.markAllMessagesAsRead()
    }

    override fun loadData(username: String) {
        doAsync {
            val conversation = EMClient.getInstance().chatManager().getConversation(username)
            if (conversation == null) {
                uiThread {
                    view.onErrorLogin()
                }
            }
            val allMessages = conversation.allMessages
            messages.addAll(allMessages)
            conversation.markAllMessagesAsRead()
            uiThread {
                view.onMessageLoad()
            }
        }
    }

    override fun loadMoreData(username: String) {
        val conversation = EMClient.getInstance().chatManager().getConversation(username)
        val startMsgId = messages[0].msgId
        val moreMessages: List<EMMessage> = conversation.loadMoreMsgFromDB(startMsgId, pageSize)
        messages.addAll(0, moreMessages)
        handler.postDelayed({
            uiThread { view.onMoreMessageLoad(moreMessages.size) }
        }, 500)

    }
}