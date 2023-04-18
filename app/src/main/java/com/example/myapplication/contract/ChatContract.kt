package com.example.myapplication.contract

import com.hyphenate.chat.EMMessage

/**
 * @author 杨景
 * @description:
 * @date :2021/1/4 17:25
 */
interface ChatContract {
    interface Presenter : BasePresenter{
        fun sendMessage(contact:String,message:String)
        fun addMessage(username: String, messages: MutableList<EMMessage>?)
        fun loadData(username: String)
        fun loadMoreData(username: String)
    }
    interface View {
        fun onStartSend()
        fun onSendSuccess()
        fun onSendFailed()
        fun onMessageLoad()
        fun onMoreMessageLoad(size: Int)
        fun onErrorLogin()
    }
}