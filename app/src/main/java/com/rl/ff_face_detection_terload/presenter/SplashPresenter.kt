package com.rl.ff_face_detection_terload.presenter

import android.content.Context
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.contract.SplashContract
import org.jetbrains.anko.defaultSharedPreferences

class SplashPresenter(var view: SplashContract.View) : SplashContract.Presenter {
    override fun checkLoginStatus(context: Context) {
        if (isLoggedIn() && context.defaultSharedPreferences.getBoolean("isAuto", false))
            view.onLoggedIn()
        else
            view.onNotLoggedIn()
    }

    //        private fun isLoggedIn() = EMClient.getInstance().isConnected && EMClient.getInstance().isLoggedInBefore
    private fun isLoggedIn() = EMClient.getInstance().isLoggedInBefore
}