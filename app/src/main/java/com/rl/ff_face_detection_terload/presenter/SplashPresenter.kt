package com.rl.ff_face_detection_terload.presenter

import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.contract.SplashContract

class SplashPresenter(var view: SplashContract.View) : SplashContract.Presenter {
    override fun checkLoginStatus() {
        if (isLoggedIn()) view.onLoggedIn() else view.onNotLoggedIn()
    }

//        private fun isLoggedIn() = EMClient.getInstance().isConnected && EMClient.getInstance().isLoggedInBefore
    private fun isLoggedIn() = EMClient.getInstance().isLoggedInBefore
}