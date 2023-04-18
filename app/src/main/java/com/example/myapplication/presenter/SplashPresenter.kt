package com.example.myapplication.presenter

import com.example.myapplication.contract.SplashContract
import com.hyphenate.chat.EMClient

class SplashPresenter(var view: SplashContract.View) : SplashContract.Presenter {
    override fun checkLoginStatus() {
        if (isLoggedIn()) view.onLoggedIn() else view.onNotLoggedIn()
    }

    private fun isLoggedIn()  = EMClient.getInstance().isConnected && EMClient.getInstance().isLoggedInBefore
}