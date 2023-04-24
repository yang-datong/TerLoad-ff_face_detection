package com.rl.ff_face_detection_terload.presenter

import android.util.Log
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.rl.ff_face_detection_terload.contract.ConversationContract
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * @author 杨景
 * @description:
 * @date :2021/1/5 21:45
 */
class ConversationPresenter(val view: ConversationContract.View) : ConversationContract.Presenter {
    companion object {
        private const val TAG = "ConversationPresenter"
    }
    val conversations = mutableListOf<EMConversation>()

    override fun onConversations() {
        doAsync {
            val allConversations = EMClient.getInstance().chatManager().allConversations
            if (allConversations.size != conversations.size) {
                conversations.clear()
                conversations.addAll(allConversations.values)
                Log.d(TAG, "onConversations: 刷新会话列表")
            }
            uiThread { view.onUpdate() }
        }
    }

}