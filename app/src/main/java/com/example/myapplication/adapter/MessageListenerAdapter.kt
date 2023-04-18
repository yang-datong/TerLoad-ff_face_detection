package com.example.myapplication.adapter

import com.hyphenate.EMMessageListener
import com.hyphenate.chat.EMMessage

/**
 * @author 杨景
 * @description:
 * @date :2021/1/5 15:43
 */
open class MessageListenerAdapter : EMMessageListener {
    override fun onMessageRecalled(messages: MutableList<EMMessage>?) {
        TODO("Not yet implemented")
    }

    override fun onMessageChanged(message: EMMessage?, change: Any?) {
        TODO("Not yet implemented")
    }

    override fun onCmdMessageReceived(messages: MutableList<EMMessage>?) {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(messages: MutableList<EMMessage>?) {
        TODO("Not yet implemented")
    }

    override fun onMessageDelivered(messages: MutableList<EMMessage>?) {
        TODO("Not yet implemented")
    }

    override fun onMessageRead(messages: MutableList<EMMessage>?) {
        TODO("Not yet implemented")
    }
}