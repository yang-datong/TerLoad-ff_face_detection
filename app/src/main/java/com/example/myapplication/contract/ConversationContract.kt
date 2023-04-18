package com.example.myapplication.contract

/**
 * @author 杨景
 * @description:
 * @date :2021/1/5 21:43
 */
interface ConversationContract {
    interface Presenter : BasePresenter {
        fun onConversations()
    }
    interface View {
        fun onUpdate()
    }
}