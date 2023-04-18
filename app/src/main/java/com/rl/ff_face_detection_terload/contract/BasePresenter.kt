package com.rl.ff_face_detection_terload.contract

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