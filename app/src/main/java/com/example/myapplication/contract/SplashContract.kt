package com.example.myapplication.contract

interface SplashContract {
    interface Presenter : BasePresenter{
        fun checkLoginStatus () //登录状态判断
    }

    interface View{
        fun onNotLoggedIn();//没登陆
        fun onLoggedIn();//登陆
    }
}