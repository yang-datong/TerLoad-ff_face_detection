package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.myapplication.R.layout.activity_main
import com.example.myapplication.adapter.MessageListenerAdapter
import com.example.myapplication.ui.activity.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.hyphenate.EMConnectionListener
import com.hyphenate.EMError
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.angle_numver.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.yesButton


class MainActivity : AppCompatActivity() {
    private val msgListener  = object : MessageListenerAdapter(){
        override fun onMessageReceived(messages: MutableList<EMMessage>?) {
            runOnUiThread { angleNumber() }
        }
    }
    private val connectionListener = object : EMConnectionListener{
        override fun onConnected() {
            Log.i("TAG", "onConnected: 服务已连接")
        }

        override fun onDisconnected(errorCode: Int) { //当服务器状态断开连接时
            if (errorCode == EMError.USER_LOGIN_ANOTHER_DEVICE) {//当报错代码为 在另一台设备上登录导致被断开连接
                alert("","当前账号已在其他设备上登录!"){
                    yesButton { startActivity<LoginActivity>()
                        finish()
                    }
                }
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        init()
        angleNumber()//添加角标
        EMClient.getInstance().chatManager().addMessageListener(msgListener)//消息监听
        EMClient.getInstance().addConnectionListener(connectionListener)
    }

    private fun init() {
        val findNavController = Navigation.findNavController(this, R.id.fragment)
        val build = AppBarConfiguration.Builder(bottomNavigationView.menu).build()      //建立构造者
        NavigationUI.setupActionBarWithNavController(this, findNavController, build)//设置返回按钮
        NavigationUI.setupWithNavController(bottomNavigationView, findNavController) //设置联动
    }

    private fun angleNumber() {
        val menuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        val view = menuView.getChildAt(0)
        val inflate = layoutInflater.inflate(R.layout.angle_numver, null)
        (view as BottomNavigationItemView).addView(inflate)
        val messageNum = EMClient.getInstance().chatManager().unreadMessageCount.toString()
        if (messageNum.toInt() > 0) {
            inflate.textView10.text = messageNum
            inflate.textView10.visibility = View.VISIBLE
        } else {
            inflate.textView10.visibility = View.GONE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }

}