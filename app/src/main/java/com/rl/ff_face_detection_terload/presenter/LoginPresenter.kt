package com.rl.ff_face_detection_terload.presenter

import android.content.Context
import android.util.Log
import com.hyphenate.EMCallBack
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMUserInfo
import com.rl.ff_face_detection_terload.contract.LoginContract
import com.rl.ff_face_detection_terload.database.DB
import com.rl.ff_face_detection_terload.database.User
import com.rl.ff_face_detection_terload.extensions.emUserObjToUserObj
import com.rl.ff_face_detection_terload.extensions.isValidPassword
import com.rl.ff_face_detection_terload.extensions.isValidUserName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync

class LoginPresenter(val view: LoginContract.View) : LoginContract.Presenter {
    private val TAG = "LoginPresenter"
    private var user: User? = null
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

                //获取服务器数据，然后保存在本地数据库
                fetchServerUserInfo(userName, passWord, context)
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

    private fun fetchServerUserInfo(username: String, passWord: String, context: Context) {
        doAsync {
            //如果传入一个username 则只有一个map返回
            EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(arrayOf(username), object : EMValueCallBack<Map<String?, EMUserInfo?>?> {
                override fun onSuccess(value: Map<String?, EMUserInfo?>?) {
                    value?.let { v ->
                        v.values.first()?.let {
                            if (user == null) {
                                user = emUserObjToUserObj(it, TAG)
//                                user = User(username = it.userId, password = "", name = it.nickname, email = it.email, phone = it.phoneNumber)
//                                //解析自定义数据: 考勤状态、签到时间、签退时间
//                                val solutionCustomData = solutionCustomData(it.ext)
//                                solutionCustomData?.let { u ->
//                                    user!!.status = u.status
//                                    user!!.checkin_time = u.checkin_time
//                                    user!!.checkout_time = u.checkout_time
//                                }
                                Log.d(TAG, "fetchServerUserInfo-> onSuccess: $user")
                                saveIntoDataBase(username, passWord, context)
                            }
                        }
                    }
                }

                override fun onError(error: Int, errorMsg: String?) {
                    Log.e(TAG, "fetchServerUserInfo-> onError: ${error},errorMsg: $errorMsg")
                    saveIntoDataBase(username, passWord, context)
                }
            })
        }
    }

//    private fun solutionCustomData(ext: String?): UserStatusAndCheckTime? {
//        if (!ext.isNullOrEmpty()) {
//            try {
//                val json = JSONObject(ext)
//                return UserStatusAndCheckTime(json.getInt("status")
//                        , json.getLong("checkin_time")
//                        , json.getLong("checkout_time"))
//            } catch (e: Exception) {
//                Log.e(TAG, "Solution JSON error", e)
//            }
//        }
//        return null
//    }

    private fun saveIntoDataBase(username: String, pswd: String, context: Context) {
        GlobalScope.launch {
            val userDao = DB.getInstance(context).userDao()
            //如果在服务器里获取到了数据那么就新增一个密码、登录时间到本地数据库
            if (user != null && user!!.username != "") {
                user!!.password = pswd
                user!!.create_time = System.currentTimeMillis()
            } else  //没有在服务器获取到数据则保存一个最基本的用户数据
                user = User(null, username, pswd, create_time = System.currentTimeMillis())

            //如果本地数据库存在该用户则进行更新，反之新增
            val localUser = userDao.getUserByUsername(username)
            var ret = -1L
            if (localUser != null) {
                user!!.id = userDao.getIdByUsername(username)
                ret = userDao.updateUser(user!!).toLong()
                Log.d(TAG, "更新本地数据库")
            } else {
                ret = userDao.addUser(user!!)
                Log.d(TAG, "新增本地数据库")
            }
            if (ret == 1L) {
                Log.d(TAG, "saveIntoDataBase-> onSuccess: $user")
                val userId = userDao.getIdByUsername(username)
                uiThread {
                    context.defaultSharedPreferences.edit().putString("username", username).apply()
                    context.defaultSharedPreferences.edit().putInt("id", userId).apply()
                    view.onLoggedInSuccess()
                }
            } else {
                Log.e(TAG, "用户账号添加到数据库错误，数据库添加:已${ret}个")
                view.onLoggedInFailed("添加数据失败")
            }
        }
    }

    private fun saveIntoSharedPreferences(context: Context, it: EMUserInfo) {
        context.defaultSharedPreferences.edit()
                .putString("nickname", it.nickname)
                .putString("avatarUrl", it.avatarUrl)
                .putString("email", it.email)
                .putString("phoneNumber", it.phoneNumber)
                .putInt("gender", it.gender)
                .putString("signature", it.signature)
                .putString("birth", it.birth)
                .putString("ext", it.ext).apply()
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