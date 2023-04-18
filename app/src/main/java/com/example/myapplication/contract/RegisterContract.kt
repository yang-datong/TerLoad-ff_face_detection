package com.example.myapplication.contract

import android.content.Context

interface RegisterContract {
    interface Presenter : BasePresenter{
        fun register(username: String, pswd: String, confirmPawd: String, context: Context)
    }

    interface View{
        fun onUserNameError()
        fun onPassWordError()
        fun onConfirmPassWordError()
        fun onStartRegister()
        fun onRegisterInSuccess()
        fun onRegisterInFailed(mes:String?)
    }
}