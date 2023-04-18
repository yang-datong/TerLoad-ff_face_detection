package com.example.myapplication.adapter

import com.hyphenate.EMContactListener

/**
 * @author 杨景
 * @description:
 * @date :2021/1/3 2:02
 */
open class EMContactListenerAdapter : EMContactListener {
    override fun onContactInvited(username: String?, reason: String?) {

    }

    override fun onContactDeleted(username: String?) {
    }

    override fun onFriendRequestAccepted(username: String?) {
    }

    override fun onContactAdded(username: String?) {
    }

    override fun onFriendRequestDeclined(username: String?) {
    }
}