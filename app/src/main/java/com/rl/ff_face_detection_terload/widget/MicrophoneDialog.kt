package com.rl.ff_face_detection_terload.widget

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import com.rl.ff_face_detection_terload.R
import kotlinx.android.synthetic.main.microphone_dialog.view.*

class MicrophoneDialog(context: Context) : Dialog(context) {
    interface OnSendVoiceListener {
        fun sendVoice()
        fun stopVoice()
    }

    var onSendVoiceListener: OnSendVoiceListener? = null
    private var animation: AnimationDrawable? = null

    init {
        val inflate = layoutInflater.inflate(R.layout.microphone_dialog, null)
        setContentView(inflate)
        inflate.button6.setOnClickListener { onSendVoiceListener?.stopVoice() }
        inflate.button5.setOnClickListener { onSendVoiceListener?.sendVoice() }
        animation = inflate.imageView9.drawable as AnimationDrawable
        setCancelable(false)
    }

    fun showImgAnimation() {
        animation?.start()
    }

    fun dismissImgAnimation() {
        animation?.stop()
    }
}