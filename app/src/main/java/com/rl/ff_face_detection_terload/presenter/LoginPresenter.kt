package com.rl.ff_face_detection_terload.presenter

import android.content.Context
import android.util.Log
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.contract.LoginContract
import com.rl.ff_face_detection_terload.database.DB
import com.rl.ff_face_detection_terload.database.User
import com.rl.ff_face_detection_terload.extensions.isValidPassword
import com.rl.ff_face_detection_terload.extensions.isValidUserName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.defaultSharedPreferences

class LoginPresenter(val view: LoginContract.View) : LoginContract.Presenter {
    private val TAG = "LoginPresenter"
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
                saveIntoDataBase(userName, passWord, context)
            }

            override fun onProgress(progress: Int, status: String?) {}

            override fun onError(code: Int, error: String?) {
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

    override fun rootUserIsExist(context: Context): Boolean {
        var ret = true
        runBlocking {
            val job = GlobalScope.launch {
                val userDao = DB.getInstance(context).userDao()
                val user = userDao.getUserByUsername("root")
                if (user == null) ret = false
            }
            job.join() // 等待协程执行完成
        }
        return ret
    }

    override fun getUserPasswordByUserName(userName: String, context: Context): String? {
        var password: String? = null;
        runBlocking {
            val job = GlobalScope.launch {
                val userDao = DB.getInstance(context).userDao()
                val user = userDao.getUserByUsername(userName)
                Log.d(TAG, "getUserPasswordByUserName: ${user?.password}")
                password = user?.password
            }
            job.join() // 等待协程执行完成
        }
        return password
    }

    private fun saveIntoDataBase(username: String, pswd: String, context: Context) {
        GlobalScope.launch {
            val userDao = DB.getInstance(context).userDao()
            val user = userDao.getUserByUsername(username)
            if (user == null) {
                val ret = userDao.addUser(User(null, username, pswd, create_time = System.currentTimeMillis()))
                if (ret > 0L) {
                    Log.d(TAG, "用户账号已添加到数据库")
                    val userId = userDao.getIdByUsername(username)
                    uiThread {
                        context.defaultSharedPreferences.edit().putString("username", username).apply()
                        context.defaultSharedPreferences.edit().putInt("id", userId).apply()
                        view.onLoggedInSuccess()
                    }
                } else {
                    Log.e(TAG, "用户账号添加到数据库错误，数据库添加:已${ret}个")
                }
            } else {
                val ret = userDao.updateCreateTimeByUsername(username, System.currentTimeMillis())
                if (ret > 0) {
                    Log.d(TAG, "用户账号已更新到数据库")
                    uiThread {
                        context.defaultSharedPreferences.edit().putString("username", username).apply()
                        context.defaultSharedPreferences.edit().putInt("id", user.id!!).apply()
                        view.onLoggedInSuccess()
                    }
                } else {
                    Log.e(TAG, "用户账号不需要更新，数据库更新:已${ret}个")
                }
            }
        }
    }

    private fun logout() {
        EMClient.getInstance().logout(true, object : EMCallBack {
            override fun onSuccess() {
                uiThread {
                    view.onLoggedInFailed("已在另一台设备中退出，尝试重新登录")
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