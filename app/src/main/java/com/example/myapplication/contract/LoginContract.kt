package com.example.myapplication.contract

import android.content.Context

interface LoginContract {
    interface Presenter: BasePresenter{
        fun login(userName: String, passWord: String, context: Context)
    }

    interface View{
        fun onUserNameError()
        fun onPassWordError()
        fun onStartLogin()
        fun onLoggedInSuccess()
        fun onLoggedInFailed(mes:String?)
    }


}