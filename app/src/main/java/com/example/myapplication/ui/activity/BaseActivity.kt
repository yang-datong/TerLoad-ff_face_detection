package com.example.myapplication.ui.activity

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    private val progressDialog by lazy { ProgressDialog(this) }

    private val imm by lazy {
         getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResID())
        inits();
    }

    open fun inits() {
    }

    open fun hideSoftKeyboard(){
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0) //获取当前焦点
    }

    open fun showProgress(message : String){
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setMessage(message)
        progressDialog.show()
    }
    open fun dismissProgress(){
        progressDialog.dismiss()
    }

    abstract  fun getLayoutResID(): Int

}