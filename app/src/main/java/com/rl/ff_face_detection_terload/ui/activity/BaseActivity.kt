package com.rl.ff_face_detection_terload.ui.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rl.ff_face_detection_terload.R


abstract class BaseActivity : AppCompatActivity() {
    private var message: TextView? = null
    private var confirm: Button? = null
    private val dialog by lazy {
        Dialog(this).apply {
            setCancelable(false)
            setContentView(R.layout.dialog_loading)
            window!!.setBackgroundDrawableResource(R.drawable.bg_loading)
            window!!.attributes.apply {
                gravity = Gravity.CENTER
                width = 500
                height = 500
                alpha = 0.7f
            }
        }
    }

    private val bottomDialog by lazy {
        BottomSheetDialog(this).apply {
            setContentView(R.layout.dialog_bottom)
        }
    }

    private val imm by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResID())
        inits()
    }

    open fun inits() {
    }

    open fun hideSoftKeyboard() {
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0) //获取当前焦点
    }

    open fun showProgress(message: String) {
        dialog.show()
    }

    open fun showBottomDialog(msg: String?, bt_tips: String?, onClickListener: OnClickListener) {
        if (message == null || confirm == null) {
            message = bottomDialog.findViewById<TextView>(R.id.tv_message)
            confirm = bottomDialog.findViewById<Button>(R.id.bt_confirm)
        }
        bottomDialog.apply {
            if (!msg.isNullOrEmpty())
                message!!.text = msg
            if (!bt_tips.isNullOrEmpty())
                confirm!!.text = bt_tips
            confirm!!.setOnClickListener {
                onClickListener.onClick(it)
            }
            show()
        }

    }

    interface OnClickListener {
        fun onClick(v: View?)
    }

    open fun dismissBottomDialog() {
        bottomDialog.dismiss()
    }

    open fun dismissProgress() {
        dialog.dismiss()
    }

    abstract fun getLayoutResID(): Int

}