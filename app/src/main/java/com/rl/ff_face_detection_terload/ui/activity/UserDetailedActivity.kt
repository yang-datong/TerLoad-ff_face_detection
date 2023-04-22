package com.rl.ff_face_detection_terload.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.view.isInvisible
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.database.DB
import com.rl.ff_face_detection_terload.database.User
import com.rl.ff_face_detection_terload.database.UserStatusAndCheckTime
import com.rl.ff_face_detection_terload.extensions.formatTimestamp
import com.rl.ff_face_detection_terload.extensions.pullUpdateOtherUserDataIntoDatabaseByServer
import kotlinx.android.synthetic.main.activity_user_detailed.*
import kotlinx.android.synthetic.main.title_bar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.util.*


class UserDetailedActivity : BaseActivity() {

    private var afterRemindingNeedFinish = false
    private val TAG = "UserDetailedActivity"
    override fun getLayoutResID() = R.layout.activity_user_detailed

    companion object {
        const val REMIND_MESSAGE = "管理员提醒你尽快完成考勤~"
    }

    override fun inits() {
        val username = intent.getStringExtra("username").toString()
        afterRemindingNeedFinish = intent.getBooleanExtra("afterRemindingNeedFinish", false)
        tv_user_name.text = getString(R.string.username, username)
        tv_title.isInvisible = true
        initView(username)
        pullUpdateOtherUserDataIntoDatabaseByServer(username, TAG, this, {
            setUserCheckStatus(username)
        }, { errorMsg ->
            Log.e(TAG, "errorMsg: $errorMsg")
        })
    }

    private fun initView(username: String) {
        img_ret.setOnClickListener {
            finish()
        }
        bt_send_message.setOnClickListener {
            if (afterRemindingNeedFinish)
                finish()
            else
                startActivity<ChatActivity>("username" to username)
        }
        bt_remind.setOnClickListener {
            if (afterRemindingNeedFinish) {
                val resultIntent = Intent().apply {
                    putExtra("message", REMIND_MESSAGE)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else
                startActivity<ChatActivity>("username" to username, "message" to REMIND_MESSAGE)
        }
        bt_delete_friend.setOnClickListener {
            showBottomDialog("同时会屏蔽对方的临时对话，不再接收此人的消息，是否继续?", "确认删除", R.color.wechat_red) {
                deleteFriend(username, it)
                dismissBottomDialog()
            }
        }
    }


    private fun setUserCheckStatus(username: String?) {
        GlobalScope.launch {
            var userStatusAndCheckTime: UserStatusAndCheckTime? = null
            var user: User? = null
            if (!username.isNullOrEmpty()) {
//                userStatusAndCheckTime = DB.getInstance(this@UserDetailedActivity).userDao().getStatusAndCheckTimeByUsername(username)
                user = DB.getInstance(this@UserDetailedActivity).userDao().getUserByUsername(username)
                if (user == null) {
                    Log.e(TAG, "updateUIByUserStatus: error")
                    return@launch
                }
                Log.d(TAG, "updateUIByUserStatus: $user")
                userStatusAndCheckTime = UserStatusAndCheckTime(user.status, user.checkin_time, user.checkout_time)
            } else {
                Log.e(TAG, "initUserView: username == null")
            }
            runOnUiThread {
                tv_user_attendance.apply {
                    user?.let {
                        tv_name.text = if (it.name.isNullOrEmpty()) username else it.name
                        tv_email.text = getString(R.string.email, it.email)
                        tv_phone.text = getString(R.string.phone, it.phone)
                    }
                    tv_checkin_time2.text = getString(R.string.checkin_time, "待签到 ")
                    tv_checkout_time2.text = getString(R.string.checkout_time, "待签退 ")
                    if (userStatusAndCheckTime != null && userStatusAndCheckTime.status == 2 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 6) {
                        // 当前时间是早上6点之前
                        text = "已完成考勤"
                        setTextColor(Color.GREEN)
                        if (userStatusAndCheckTime.checkin_time != 0L)
                            tv_checkin_time2.text = getString(R.string.checkin_time, formatTimestamp(userStatusAndCheckTime.checkin_time))
                        if (userStatusAndCheckTime.checkout_time != 0L)
                            tv_checkout_time2.text = getString(R.string.checkout_time, formatTimestamp(userStatusAndCheckTime.checkout_time))
                    } else if (userStatusAndCheckTime != null && userStatusAndCheckTime.status == 1) {
                        text = "开始考勤中..."
                        setTextColor(Color.YELLOW)
                        if (userStatusAndCheckTime.checkin_time != 0L)
                            tv_checkin_time2.text = getString(R.string.checkin_time, formatTimestamp(userStatusAndCheckTime.checkin_time))
                    } else {
                        text = "未考勤"
                        setTextColor(Color.RED)
                    }
                }
            }
        }
    }

    private fun deleteFriend(username: String, view: View) {
        doAsync {
            EMClient.getInstance().contactManager().deleteContact(username)
//            uiThread { Snackbar.make(view, "删除成功", Snackbar.LENGTH_LONG).show() }
            uiThread { finish() }
        }
    }
}
