package com.rl.ff_face_detection_terload.ui.activity

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rl.ff_face_detection_terload.R


abstract class BaseActivity : AppCompatActivity() {
    private val progressDialog by lazy { ProgressDialog(this).apply { setContentView(R.layout.dialog_loading) } }

    private val dialog by lazy {
        Dialog(this).apply {
            setContentView(R.layout.dialog_loading)
            setCancelable(false)
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
//        progressDialog.setCanceledOnTouchOutside(false)
//        progressDialog.setMessage(message)
//        progressDialog.show()
        dialog.show()
    }

    open fun dismissProgress() {
//        progressDialog.dismiss()
        dialog.dismiss()
    }

    abstract fun getLayoutResID(): Int

}