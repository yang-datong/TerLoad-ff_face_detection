package com.example.myapplication.presenter

import android.content.Context
import com.example.myapplication.contract.LoginContract
import com.example.myapplication.extensions.isValidPassword
import com.example.myapplication.extensions.isValidUserName
import com.example.myapplication.ui.activity.LoginActivity
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginPresenter(val view: LoginContract.View) : LoginContract.Presenter {
    override fun login(userName: String, passWord: String, context: Context) {
        if (userName.isValidUserName() && passWord.isValidPassword()) {
            view.onStartLogin()
            loginEaseMob(userName, passWord, true, context)  //登录到服务器
        } else if (userName.isValidUserName() || userName.isEmpty())
            view.onPassWordError()
        else if (passWord.isValidPassword() || passWord.isEmpty())
            view.onUserNameError()
    }

    private fun loginEaseMob(userName: String, passWord: String, isAutoLogin: Boolean, context: Context) {
        EMClient.getInstance().login(userName, passWord, object : EMCallBack {
            override fun onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups()
                EMClient.getInstance().chatManager().loadAllConversations()//这是子线程
//                if (isAutoLogin)options.setAutoLogin(false)
                //返回主线程控制UI
                uiThread { view.onLoggedInSuccess() }
            }

            override fun onProgress(progress: Int, status: String?) {}

            override fun onError(code: Int, error: String?) {
                println(code)
                if (code == 200) {
                    logout()
                    return
                }
                //返回主线程控制UI
                uiThread {
                    view.onLoggedInFailed(error)
                }
            }
        })
    }
    private fun logout() {
        EMClient.getInstance().logout(true,object : EMCallBack {
            override fun onSuccess() {
                uiThread {
                    view.onLoggedInFailed("已在另一台设备中退出，尝试重新登陆")
                }
            }

            override fun onProgress(progress: Int, status: String?) {
            }

            override fun onError(code: Int, error: String?) {
                uiThread {
                    view.onLoggedInFailed("账号异常")
                }
            }
        })
    }
}