package com.example.myapplication.contract

import android.os.Handler
import android.os.Looper

interface BasePresenter {

    companion object{
        val handler by lazy {
            Handler(Looper.getMainLooper())
        }
    }
    fun uiThread(func :() -> Unit){
        handler.post {
            func()
        }
    }

}