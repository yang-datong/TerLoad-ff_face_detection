package com.example.myapplication.presenter

import android.content.Context
import android.util.Log
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.myapplication.contract.RegisterContract
import com.example.myapplication.database.DB
import com.example.myapplication.database.User
import com.example.myapplication.emp.MyUser
import com.example.myapplication.extensions.isValidPassword
import com.example.myapplication.extensions.isValidUserName
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.HyphenateException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class RegisterPresenter(val view: RegisterContract.View) : RegisterContract.Presenter {
    override fun register(username: String, pswd: String, confirmPawd: String, context: Context) {
        if (username.isValidUserName() && pswd.isValidPassword() && pswd == confirmPawd) {
            view.onStartRegister()
            //注册到数据库
            registerBmob(username, pswd, confirmPawd, context)

        } else if (!username.isValidUserName()) view.onUserNameError()
        else if (!pswd.isValidPassword()) view.onPassWordError()
        else if (pswd != confirmPawd) view.onConfirmPassWordError()
    }

    private fun registerEaseMob(username: String, pswd: String, confirmPawd: String) {
        doAsync {
            try {
                //注册失败会抛出HyphenateException
                EMClient.getInstance().createAccount(username, confirmPawd);//同步方法
                uiThread {
                    view.onRegisterInSuccess()
                }
            } catch (e: HyphenateException) {
                uiThread {
                    Log.i("TAG", "注册失败: " + e.message)
                    view.onRegisterInFailed(e.message)
                }
            }
        }
    }

    private fun registerBmob(username: String, pswd: String, confirmPawd: String, context: Context) {
//        val user = MyUser(username, confirmPawd)
//        user.save(object : SaveListener<String>() {
//            override fun done(objectId: String?, e: BmobException?) {
//                if (e == null) {
//                    注册到服务器
//                    registerEaseMob(username, pswd, confirmPawd)
//                } else {
//                    view.onRegisterInFailed()
//                }
//            }
//        })


        GlobalScope.launch {
            val ret = DB.getInstance(context).userDao().addUser(User(null, username, confirmPawd))
            if (ret > 0L) {
                uiThread {
                    registerEaseMob(username, pswd, confirmPawd)
                }
            } else {
                uiThread {
                    view.onRegisterInFailed("数据库添加:已${ret}个")
                }
            }
        }

    }

}