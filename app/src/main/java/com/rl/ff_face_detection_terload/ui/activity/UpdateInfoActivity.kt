package com.rl.ff_face_detection_terload.ui.activity

import android.util.Log
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.database.DB
import com.rl.ff_face_detection_terload.database.User
import com.rl.ff_face_detection_terload.extensions.isValidPassword
import kotlinx.android.synthetic.main.activity_update_info.*
import kotlinx.android.synthetic.main.title_bar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast

class UpdateInfoActivity : BaseActivity() {
    override fun getLayoutResID() = R.layout.activity_update_info
    private val TAG = "UpdateInfoActivity"

    private var fetchDataDone = false
    private var name: String = ""
    private var email: String = ""
    private var phone: String = ""
    private var password: String = ""

    override fun inits() {
        initView()
        val username = defaultSharedPreferences.getString("username", "")
        val userId = defaultSharedPreferences.getInt("id", -1)
        if (username.isNullOrEmpty()) {
            Log.e(TAG, "inits: username.isNullOrEmpty")
            toast("当前用户名获取识别")
            finish()
        }
        tv_username.text = getString(R.string.username, username)
        initData(username)
        bt_update.setOnClickListener {
            val ret = commitUserInfoData(username, userId, it)
            if (ret == -1)
                return@setOnClickListener
        }
        img_option.setOnClickListener {
            val ret = commitUserInfoData(username, userId, it)
            if (ret == -1)
                return@setOnClickListener
        }
    }

    private fun commitUserInfoData(username: String?, userId: Int, it: View): Int {
        if (!fetchDataDone)
            return -1
        hideSoftKeyboard()
        var user: User? = null
        //1. 信息发生改变就直接构造用户对象
        if (ev_name.text.toString() != this.name || ev_email.text.toString() != this.email || ev_phone.text.toString() != this.phone) {
            user = User(userId, username = username!!, password = this.password, name = ev_name.text.toString(), email = ev_email.text.toString(), phone = ev_phone.text.toString())
        }

        //2. 判断密码是否改变或者新密码是否可用
        if (ev_password.text.toString() != "" || ev_password.text.toString() != "") {
            if (ev_password.text.toString() != this.password) {
                Log.d(TAG, "inits: 原密码错误")
                Snackbar.make(it, "原密码错误", Snackbar.LENGTH_LONG).show()
                return -1
            } else if (!ev_password_confirm.text.toString().isValidPassword()) {
                Log.d(TAG, "inits: 无效密码")
                Snackbar.make(it, "无效密码", Snackbar.LENGTH_LONG).show()
                return -1
            } else {
                if (user != null)  //追加用户密码更新信息
                    user.password = ev_password_confirm.text.toString()
                else               //只修改了密码
                    user = User(userId, username = username!!, password = ev_password_confirm.text.toString(), name = ev_name.text.toString(), email = ev_email.text.toString(), phone = ev_phone.text.toString())
                Log.d(TAG, "img_option.setOnClickListener: update password")
            }
        }

        //3. 是否需要提交信息变更
        user?.let { u ->
            updateUserInfoData(u, it)
        }
        return 0
    }

    private fun updateUserInfoData(user: User, view: View) {
        showProgress("")

//        doAsync {
//      //TODO 上传服务器更新密码
//        }

        //save DataBase
        GlobalScope.launch {
            Log.d(TAG, "img_option.setOnClickListener: update user info , $user")
            val ret = DB.getInstance(this@UpdateInfoActivity).userDao().updateUser(user)
            runOnUiThread { dismissProgress() }
            if (ret == 1) {
                Log.d(TAG, "updateUser success")
                this@UpdateInfoActivity.name = user.name.toString()
                this@UpdateInfoActivity.email = user.email.toString()
                this@UpdateInfoActivity.phone = user.phone.toString()
                this@UpdateInfoActivity.password = user.password
                runOnUiThread {
                    ev_password.text.clear()
                    ev_password_confirm.text.clear()
                    Snackbar.make(view, "updateUser success", Snackbar.LENGTH_LONG).show()
                }
            } else {
                Log.e(TAG, "updateUser failed ret:${ret}")
                runOnUiThread { Snackbar.make(view, "updateUser failed", Snackbar.LENGTH_LONG).show() }
            }
        }


    }

    private fun initData(username: String?) {
        showProgress("")
        GlobalScope.launch {
            Log.d(TAG, "initData start: $fetchDataDone")
            fetchDataDone = false
            val user = DB.getInstance(this@UpdateInfoActivity).userDao().getUserByUsername(username!!)
            user?.let {
                runOnUiThread {
                    it.name?.let {
                        ev_name.setText(it)
                        this@UpdateInfoActivity.name = it
                    }
                    it.email?.let {
                        ev_email.setText(it)
                        this@UpdateInfoActivity.email = it
                    }
                    it.phone?.let {
                        ev_phone.setText(it)
                        this@UpdateInfoActivity.phone = it
                    }
                    it.password.let {
                        this@UpdateInfoActivity.password = it
                    }
                    dismissProgress()
                }
            }
            fetchDataDone = true
            Log.d(TAG, "initData end: $fetchDataDone")
        }
    }

    private fun initView() {
        img_ret.setOnClickListener {
            finish()
        }
        tv_title.isInvisible = true
        img_option.isVisible = true
        img_option.setImageDrawable(getDrawable(R.drawable.ic_baseline_check_24))
    }

}
