package com.rl.ff_face_detection_terload.adapter

import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMMessage

/**
 * @author 杨景
 * @description:
 * @date :2021/1/5 15:43
 */
open class MessageListenerAdapter : EMMessageListener {
    override fun onMessageRecalled(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageChanged(message: EMMessage?, change: Any?) {
    }

    override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageReceived(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
    }

    override fun onMessageRead(messages: MutableList<EMMessage>?) {
    }
}