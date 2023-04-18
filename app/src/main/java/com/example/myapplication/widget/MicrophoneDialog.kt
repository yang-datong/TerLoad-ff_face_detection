package com.example.myapplication.widget

import android.app.Dialog
import android.content.Context
import com.example.myapplication.R
import kotlinx.android.synthetic.main.microphone_dialog.view.*

class MicrophoneDialog(context: Context) : Dialog(context) {
    interface OnSendVoiceListener {
        fun sendVoice()
    }
    var onSendVoiceListener : OnSendVoiceListener ?= null
    init {
        val inflate = layoutInflater.inflate(R.layout.microphone_dialog, null)
        setContentView(inflate)
        inflate.button6.setOnClickListener { dismiss() }
        inflate.button5.setOnClickListener { onSendVoiceListener?.sendVoice() }


    }
}