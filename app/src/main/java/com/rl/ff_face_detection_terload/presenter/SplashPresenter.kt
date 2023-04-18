package com.rl.ff_face_detection_terload.presenter

import com.rl.ff_face_detection_terload.contract.SplashContract
import com.hyphenate.chat.EMClient

class SplashPresenter(var view: SplashContract.View) : SplashContract.Presenter {
    override fun checkLoginStatus() {
        if (isLoggedIn()) view.onLoggedIn() else view.onNotLoggedIn()
    }

    private fun isLoggedIn()  = EMClient.getInstance().isConnected && EMClient.getInstance().isLoggedInBefore
}