package com.rl.ff_face_detection_terload.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hyphenate.EMConnectionListener
import com.hyphenate.EMError
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.R.layout.activity_main
import com.rl.ff_face_detection_terload.adapter.MessageListenerAdapter
import com.rl.ff_face_detection_terload.ui.fargment.ContactFragment
import com.rl.ff_face_detection_terload.ui.fargment.ConversationFragment
import com.rl.ff_face_detection_terload.ui.fargment.DynamicFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private val homeFragment by lazy { ConversationFragment() }
    private val dashboardFragment by lazy { ContactFragment() }
    private val notificationsFragment by lazy { DynamicFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        initBottomNavigationView()
        EMClient.getInstance().chatManager().addMessageListener(msgListener)
        EMClient.getInstance().addConnectionListener(connectionListener)
    }

    private fun initBottomNavigationView() {
        supportFragmentManager.beginTransaction().apply {
            add(R.id.fragment_container, homeFragment)
            add(R.id.fragment_container, dashboardFragment)
            add(R.id.fragment_container, notificationsFragment)
            hide(dashboardFragment)
            hide(notificationsFragment)
            commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.conversationFragment -> {
                    loadFragment(homeFragment)
                    true
                }
                R.id.contactFragment -> {
                    loadFragment(dashboardFragment)
                    true
                }
                R.id.dynamicFragment -> {
                    loadFragment(notificationsFragment)
                    true
                }
                else -> false
            }
        }

//        if (defaultSharedPreferences.getBoolean("updateTheme", false)) {
//            bottomNavigationView.selectedItemId = R.id.dynamicFragment
//            fragment_container.invalidate()
//            defaultSharedPreferences.edit().putBoolean("updateTheme", false).apply()
//        }
    }

    override fun onResume() {
        super.onResume()
        angleNumber()
    }

    override fun onDestroy() {
        super.onDestroy()
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }


    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            hide(homeFragment)
            hide(dashboardFragment)
            hide(notificationsFragment)
            show(fragment)
            commit()
        }
    }

    private fun angleNumber() {
        GlobalScope.launch {
            val messageNum = EMClient.getInstance().chatManager().unreadMessageCount
            runOnUiThread {
                if (messageNum > 0)
                    bottomNavigationView.getOrCreateBadge(R.id.conversationFragment).number = messageNum
                else
                    bottomNavigationView.removeBadge(R.id.conversationFragment)
            }
        }
    }

    private val msgListener = object : MessageListenerAdapter() {
        override fun onMessageReceived(messages: MutableList<EMMessage>?) {
            angleNumber()
        }
    }
    private val connectionListener = object : EMConnectionListener {
        override fun onConnected() {
            Log.d(TAG, "onConnected: 服务已连接")
        }

        override fun onDisconnected(errorCode: Int) { //当服务器状态断开连接时
            if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {//当报错代码为 在另一台设备上登录导致被断开连接
                alert("", "当前账号已在其他设备上登录!") {
                    yesButton {
                        startActivity<LoginActivity>()
                        finish()
                    }
                }
                Log.e(TAG, "onDisconnected:${errorCode}")
            }

        }
    }
}