package com.example.myapplication.presenter

import com.example.myapplication.contract.ConversationContract
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * @author 杨景
 * @description:
 * @date :2021/1/5 21:45
 */
class ConversationPresenter(val view: ConversationContract.View) : ConversationContract.Presenter {
    val conversations  = mutableListOf<EMConversation>()
    override fun onConversations() {
        doAsync {
            conversations.clear()
            val allConversations = EMClient.getInstance().chatManager().allConversations
            conversations.addAll(allConversations.values)
            uiThread { view.onUpdate() }
        }
    }

}