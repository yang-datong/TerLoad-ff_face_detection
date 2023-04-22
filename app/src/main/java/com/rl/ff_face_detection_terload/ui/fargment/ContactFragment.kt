package com.rl.ff_face_detection_terload.ui.fargment

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.adapter.ContactListAdapter
import com.rl.ff_face_detection_terload.adapter.EMContactListenerAdapter
import com.rl.ff_face_detection_terload.contract.ContactContract
import com.rl.ff_face_detection_terload.database.DB
import com.rl.ff_face_detection_terload.database.User
import com.rl.ff_face_detection_terload.database.UserStatusAndCheckTime
import com.rl.ff_face_detection_terload.extensions.formatTimestamp
import com.rl.ff_face_detection_terload.extensions.userObjToEMUserObj
import com.rl.ff_face_detection_terload.presenter.ContactPresenter
import com.rl.ff_face_detection_terload.ui.activity.AddFriendActivity
import com.rl.ff_face_detection_terload.ui.activity.FaceRecognizeActivity
import com.rl.ff_face_detection_terload.ui.activity.UploadFaceActivity
import com.rl.ff_face_detection_terload.widget.SlideBar
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.title_bar.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*

/**
 * @author 杨景
 * @description:
 * @date :2021/1/2 22:24
 */
//联系人 //TODO 不需要每次进入界面就刷新适配器
class ContactFragment : BaseFragment(), ContactContract.View {
    override fun getLayoutResID() = R.layout.fragment_contact

    private val TAG = "ContactFragment"
    private val REQUEST_CODE = 0x111
    private var username: String? = null
    val presenter by lazy { ContactPresenter(this) }

    override fun inits() {
        tv_title.text = getString(R.string.page_two_root)
        img_ret.isGone = true
        username = requireContext().defaultSharedPreferences.getString("username", "")
        if (username != "root") {
            tv_title.text = getString(R.string.page_two)
            initUserView()
        } else {
            initRootView()
        }
    }

    private fun showUI(hasRoot: Boolean) {
        card_recognition.isVisible = !hasRoot
        card_colletion.isVisible = !hasRoot
        tv_user_attendance2.isVisible = !hasRoot
        tv_checkin_time.isVisible = !hasRoot
        tv_checkout_time.isVisible = !hasRoot

        img_option.isVisible = hasRoot
        recyclerview.isVisible = hasRoot
        slideBar_view.isVisible = hasRoot
    }

    private fun initUserView() {
        showUI(false)
        initUIByUserStatus()
        card_recognition.setOnClickListener {
            startActivityForResult(Intent(requireActivity(), FaceRecognizeActivity::class.java), REQUEST_CODE)
        }
        card_colletion.setOnClickListener {
            requireActivity().startActivity<UploadFaceActivity>()
        }
    }

    private fun initUIByUserStatus() {
        GlobalScope.launch {
            var userStatusAndCheckTime: UserStatusAndCheckTime? = null
            if (!username.isNullOrEmpty()) {
                userStatusAndCheckTime = DB.getInstance(requireContext()).userDao().getStatusAndCheckTimeByUsername(username!!)
            } else {
                Log.e(TAG, "initUserView: username == null")
            }
            context?.runOnUiThread {
                tv_checkin_time.text = getString(R.string.checkin_time, "待签到 ")
                tv_checkout_time.text = getString(R.string.checkout_time, "待签退 ")
                tv_user_attendance2.apply {
                    if (userStatusAndCheckTime != null && userStatusAndCheckTime.status == 2 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 6) {
                        // 当前时间是早上6点之前
                        text = "已完成考勤"
                        setTextColor(Color.GREEN)
                        if (userStatusAndCheckTime.checkin_time != 0L)
                            tv_checkin_time.text = getString(R.string.checkin_time, formatTimestamp(userStatusAndCheckTime.checkin_time))
                        if (userStatusAndCheckTime.checkout_time != 0L)
                            tv_checkout_time.text = getString(R.string.checkout_time, formatTimestamp(userStatusAndCheckTime.checkout_time))
                    } else if (userStatusAndCheckTime != null && userStatusAndCheckTime.status == 1) {
                        text = "开始考勤中..."
                        setTextColor(Color.YELLOW)
                        if (userStatusAndCheckTime.checkin_time != 0L)
                            tv_checkin_time.text = getString(R.string.checkin_time, formatTimestamp(userStatusAndCheckTime.checkin_time))
                    } else {
                        text = "未考勤"
                        setTextColor(Color.RED)
                    }
                }
            }
        }
    }

    private fun updateUIByUserStatus() {
        GlobalScope.launch {
            var userStatusAndCheckTime: UserStatusAndCheckTime? = null
            var user: User? = null
            if (!username.isNullOrEmpty()) {
                user = DB.getInstance(requireContext()).userDao().getUserByUsername(username!!)
                if (user == null) {
                    Log.e(TAG, "updateUIByUserStatus: error")
                    return@launch
                }
                Log.d(TAG, "updateUIByUserStatus: $user")
                userStatusAndCheckTime = UserStatusAndCheckTime(user.status, user.checkin_time, user.checkout_time)
            } else {
                Log.e(TAG, "initUserView: username == null")
            }

            //更新服务器数据
            EMClient.getInstance().userInfoManager().updateOwnInfo(userObjToEMUserObj(user!!), object : EMValueCallBack<String> {
                override fun onSuccess(value: String?) {
                    //更新UI显示
                    requireActivity().runOnUiThread {
                        tv_checkin_time.text = getString(R.string.checkin_time, "待签到 ")
                        tv_checkout_time.text = getString(R.string.checkout_time, "待签退 ")

                        tv_user_attendance2.apply {
                            if (userStatusAndCheckTime != null && userStatusAndCheckTime.status == 2 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 6) {
                                // 当前时间是早上6点之前
                                text = "已完成考勤"
                                setTextColor(Color.GREEN)
                                if (userStatusAndCheckTime.checkin_time != 0L)
                                    tv_checkin_time.text = getString(R.string.checkin_time, formatTimestamp(userStatusAndCheckTime.checkin_time))
                                if (userStatusAndCheckTime.checkout_time != 0L)
                                    tv_checkout_time.text = getString(R.string.checkout_time, formatTimestamp(userStatusAndCheckTime.checkout_time))
                            } else if (userStatusAndCheckTime != null && userStatusAndCheckTime.status == 1) {
                                text = "开始考勤中..."
                                setTextColor(Color.YELLOW)
                                if (userStatusAndCheckTime.checkin_time != 0L)
                                    tv_checkin_time.text = getString(R.string.checkin_time, formatTimestamp(userStatusAndCheckTime.checkin_time))
                            } else {
                                text = "未考勤"
                                setTextColor(Color.RED)
                            }
                        }
                    }
                }

                override fun onError(error: Int, errorMsg: String?) {
                    requireActivity().toast("当前网络错误")
                }
            })
        }
    }

    private fun initRootView() {
        showUI(true)
        img_option.setOnClickListener {
            requireActivity().startActivity<AddFriendActivity>()
        }
        recyclerview.apply {
            setHasFixedSize(true)  //当大小被固定的情况下使用 、可以减少重绘次数、减少资源损耗\
            layoutManager = LinearLayoutManager(context)
            adapter = ContactListAdapter(context, presenter.contactListItems)
        }
        presenter.loadData()
        EMClient.getInstance().contactManager().setContactListener(object : EMContactListenerAdapter() {
            override fun onContactDeleted(username: String?) {
                super.onContactDeleted(username)
                presenter.loadData()
            }

            override fun onContactAdded(username: String?) {
                super.onContactAdded(username)
                presenter.loadData()
            }
        })
        slideBar_view.onSectionChangeListener = object : SlideBar.OnSectionChangeListener {
            override fun onSectionChange(firstLetter: String) {
                textView.text = firstLetter
                textView.visibility = View.VISIBLE
                val position = getPosition(firstLetter)
                println(position)
                if (position != -1)
                    recyclerview.smoothScrollToPosition(position)
            }

            override fun onFinishChange() {
                textView.visibility = View.GONE
            }
        }
    }

    private fun getPosition(firstLetter: String) = presenter.contactListItems.binarySearch {
        it.firstLetter.minus(firstLetter[0])
    }

    override fun onLoadSuccess() {
        recyclerview?.let {
            it.adapter?.notifyDataSetChanged()
        }
    }

    override fun onLoadFailed() {
        context.let { it?.toast("数据加载失败") }
    }

    // 判断当前考勤是签到还是签退，（每天凌晨6点刷新状态为未考勤）
    private fun getLastCheckTime() {
        GlobalScope.launch {
            val userDao = DB.getInstance(requireContext()).userDao()
            val user = userDao.getStatusAndCheckTimeByUsername(username!!)
            val nowTime = System.currentTimeMillis()
            val halfHourMillis = 30 * 60 * 1000

            /*
            * 1. 签到时间为0           -> 签到
            * 2. 现在时间-签到时间 < 30 -> 签到
            * 3. 签退时间为0           -> 签退
            * 4. 现在时间-签退时间 < 30 -> 签退
            * 5. 签到时间>签退时间      -> 签退
            * 6. 签到
            * */
            when {
                user == null -> {
                    Log.e(TAG, "getLastCheckTime-> getStatusAndCheckTimeByUsername == null")
                }
                user.checkin_time == 0L -> {
                    Log.d(TAG, "getLastCheckTime-> 开始考勤")
                    val ret = userDao.updateStatusAndCheckinTimeByUsername(username!!, 1, nowTime)
                    if (ret == 1)
                        updateUIByUserStatus()
                    else
                        Log.e(TAG, "Failed onActivityResult->updateStatusByUsername->ret=${ret}")
                }
                (nowTime - user.checkin_time) < halfHourMillis -> {
                    Log.d(TAG, "getLastCheckTime-> 开始考勤")
                    val ret = userDao.updateStatusAndCheckinTimeByUsername(username!!, 1, nowTime)
                    if (ret == 1)
                        updateUIByUserStatus()
                    else
                        Log.e(TAG, "Failed onActivityResult->updateStatusByUsername->ret=${ret}")
                }
                user.checkout_time == 0L -> {
                    Log.d(TAG, "getLastCheckTime-> 结束考勤")
                    val ret = userDao.updateStatusAndCheckoutTimeByUsername(username!!, 2, nowTime)
                    if (ret == 1)
                        updateUIByUserStatus()
                    else
                        Log.e(TAG, "Failed onActivityResult->updateStatusByUsername->ret=${ret}")
                }
                (nowTime - user.checkout_time) < halfHourMillis -> {
                    Log.d(TAG, "getLastCheckTime-> 结束考勤")
                    val ret = userDao.updateStatusAndCheckoutTimeByUsername(username!!, 2, nowTime)
                    if (ret == 1)
                        updateUIByUserStatus()
                    else
                        Log.e(TAG, "Failed onActivityResult->updateStatusByUsername->ret=${ret}")
                }
                user.checkin_time > user.checkout_time -> {
                    Log.d(TAG, "getLastCheckTime-> 结束考勤")
                    val ret = userDao.updateStatusAndCheckoutTimeByUsername(username!!, 2, nowTime)
                    if (ret == 1)
                        updateUIByUserStatus()
                    else
                        Log.e(TAG, "Failed onActivityResult->updateStatusByUsername->ret=${ret}")
                }
                else -> {
                    Log.d(TAG, "getLastCheckTime-> 开始考勤")
                    val ret = userDao.updateStatusAndCheckinTimeByUsername(username!!, 1, nowTime)
                    if (ret == 1)
                        updateUIByUserStatus()
                    else
                        Log.e(TAG, "Failed onActivityResult->updateStatusByUsername->ret=${ret}")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val faceRecognizeUserName = data?.getStringExtra("faceRecognizeUserName")
                Log.d(TAG, "onActivityResult -> faceRecognizeUserName:${faceRecognizeUserName}")
                getLastCheckTime()
            } else {
                Log.e(TAG, "人脸识别失败")
            }
        }
    }
}
