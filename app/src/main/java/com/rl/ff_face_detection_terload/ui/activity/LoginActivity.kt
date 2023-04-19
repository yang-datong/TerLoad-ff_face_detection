package com.rl.ff_face_detection_terload.ui.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.View
import androidx.core.app.ActivityCompat
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.contract.LoginContract
import com.rl.ff_face_detection_terload.presenter.LoginPresenter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : BaseActivity(), LoginContract.View {

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus);
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            statusBarColor = Color.TRANSPARENT
        }
    }

    override fun getLayoutResID() = R.layout.activity_main_login

    private val loginPresenter = LoginPresenter(this)

    private val sp by lazy {
        getSharedPreferences("isAutoLogin", Context.MODE_PRIVATE)
    }

    override fun inits() {
        login.setOnClickListener {
            hideSoftKeyboard()//隐藏软键盘
            if (hasWriteExternalStoragePermission())  //检查是否有权限
                loginPresenter.login(username.text.trim().toString(), password.text.trim().toString(), this)
            else
                applyWriteExternalStoragePermission()   //弹出请求权限对话框
        }
        //注册
        bt_login_register.setOnClickListener { startActivity<RegisterActivity>() }
        tv_login_forget_pwd.setOnClickListener {
            Snackbar.make(it, "忘记密码我也没办法啊...", Snackbar.LENGTH_LONG).setAction("行吧", null).show()
        }
        face_login.setOnClickListener {
            startActivity<FaceRecognizeActivity>()
        }
    }

    private fun applyWriteExternalStoragePermission() { //弹出请求权限对话框
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
        ActivityCompat.requestPermissions(this, permissions, 0)
    }

    //重写请求回调方法进行监听
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loginPresenter.login(username.text.trim().toString(), password.text.trim().toString(), this)
        else
            toast("没有权限，已被拒绝！")
    }

    private fun hasWriteExternalStoragePermission(): Boolean {  //检查是否有权限
        val checkSelfPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)//写入外部权限
        return checkSelfPermission == PackageManager.PERMISSION_GRANTED  //是否有自我许可证
    }

    override fun onUserNameError() {
        username.error = "用户名错误"
        println("用户名错误")
    }

    override fun onPassWordError() {
        password.error = "密码错误"
        println("密码错误")
    }

    override fun onStartLogin() {
        showProgress("正在登录中")
    }

    override fun onLoggedInSuccess() {
        dismissProgress()
        sp.edit().putBoolean("isAuto", cb_remember_login.isChecked).apply()
        startActivity<MainActivity>()
        finish()
    }

    override fun onLoggedInFailed(mes: String?) {
        dismissProgress()
        if (mes == null)
            toast("登陆失败")
        else
            toast(mes)
    }
}