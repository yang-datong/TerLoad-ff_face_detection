package com.rl.ff_face_detection_terload.ui.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rl.ff_face_detection_terload.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast


abstract class BaseActivity : AppCompatActivity() {
    private var showTime: Int = 0
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
        Dialog(this).apply {
            setContentView(R.layout.dialog_bottom)
            window?.let {
                it.setBackgroundDrawableResource(R.drawable.bottom_dialog_backcolor)
                it.attributes.apply {
                    gravity = Gravity.BOTTOM
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    windowAnimations = R.style.BottomSheetDialogAnimation
                }
            }
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

    open fun inits() {}

    open fun hideSoftKeyboard() {
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0) //获取当前焦点
    }

    open fun showProgress() {
        showTime = 0
        dialog.show()
        GlobalScope.launch {
            while (true) {
                delay(1000)
                showTime++
                if (showTime == 5) {
                    if (dialog.isShowing)
                        runOnUiThread {
                            dismissProgress()
                            toast("加载超时，稍后再试试!")
                        }
                    break
                }
            }
        }
    }

    open fun showBottomDialog(msg: String?, bt_tips: String?, onConfirm: () -> Unit) {
        showBottomDialog(msg, bt_tips, null, onConfirm)
    }

    open fun showBottomDialog(msg: String?, bt_tips: String?, bt_tips_color: Int?, onConfirm: () -> Unit) {
        if (message == null || confirm == null) {
            message = bottomDialog.findViewById(R.id.tv_message)
            confirm = bottomDialog.findViewById(R.id.bt_confirm)
            confirm?.let {
                bt_tips_color?.let { _ ->
                    it.setTextColor(bt_tips_color)
                }
            }
        }
        bottomDialog.apply {
            if (!msg.isNullOrEmpty())
                message!!.text = msg
            if (!bt_tips.isNullOrEmpty())
                confirm!!.text = bt_tips
            confirm!!.setOnClickListener {
                onConfirm()
                dismiss()
            }
            show()
        }
    }

    open fun dismissBottomDialog() {
        if (bottomDialog.isShowing)
            bottomDialog.dismiss()
    }

    open fun dismissProgress() {
        if (dialog.isShowing)
            dialog.dismiss()
    }

    abstract fun getLayoutResID(): Int
}
