package com.rl.ff_face_detection_terload.contract

import android.content.Context

interface SplashContract {
    interface Presenter : BasePresenter{
        fun checkLoginStatus(context: Context)
    }

    interface View{
        fun onNotLoggedIn();//没登录
        fun onLoggedIn();//登录
    }
}