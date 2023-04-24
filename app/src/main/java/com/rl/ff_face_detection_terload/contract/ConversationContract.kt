package com.rl.ff_face_detection_terload.contract

/**
 * @author 杨景
 * @description:
 * @date :2023/1/5 21:43
 */
interface ConversationContract {
    interface Presenter : BasePresenter {
        fun onConversations()
    }
    interface View {
        fun onUpdate()
    }
}