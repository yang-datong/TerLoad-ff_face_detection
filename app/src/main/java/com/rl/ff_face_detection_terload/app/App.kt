package com.rl.ff_face_detection_terload.app

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.hyphenate.EMCallBack
import com.hyphenate.chat.*
import com.hyphenate.exceptions.HyphenateException
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.adapter.MessageListenerAdapter
import com.rl.ff_face_detection_terload.database.DB
import com.rl.ff_face_detection_terload.database.User
import com.rl.ff_face_detection_terload.faceRecognize.FaceRecognize
import com.rl.ff_face_detection_terload.ui.activity.ChatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class App : Application() {
    companion object {
        private const val TAG = "MyApplication"
    }

    private var isBackground = true

    override fun onCreate() {
        super.onCreate()
        val emOptions = EMOptions()
        emOptions.appKey = "1107210101040542#demo" //qq邮箱
//        emOptions.appKey = "1135230423163966#demo" //网易邮箱
        val emc = EMClient.getInstance()
        emc.init(applicationContext, emOptions)
        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG)
        emc.chatManager().addMessageListener(msgListener)

//        Bmob.initialize(applicationContext, "c063550ad7c3587f4fae8ff7f68deef1");
//        LitePal.initialize(applicationContext)

        registerActivityLifecycleCallbacks(mCallbacks)
//        setDefaultUser() //比较暴力，一般跑一次就可以了
        FaceRecognize.loadJNIFaceModel(this.applicationContext)
    }

    private val mCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity) {
            isBackground = true
        }

        override fun onActivityStarted(activity: Activity) {

        }

        override fun onActivityDestroyed(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        }

        override fun onActivityResumed(activity: Activity) {
            isBackground = false
        }
    }

    private val msgListener = object : MessageListenerAdapter() {
        override fun onMessageReceived(messages: MutableList<EMMessage>?) {
            if (isBackground) {
                playMediaPlayer()//播放音频
                showNotification(messages)//显示通知栏
            }
            playVibrator() //震动
        }
    }

    private fun showNotification(messages: MutableList<EMMessage>?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(NotificationChannel("id", "name", NotificationManager.IMPORTANCE_HIGH))
            var contentText = "非文本消息！"
            var userName = ""
            messages?.forEach {
                if (it.type == EMMessage.Type.TXT) {
                    userName = it.userName
                    contentText = (it.body as EMTextMessageBody).message
                }
            }
            val intent = Intent(applicationContext, ChatActivity::class.java)
            intent.putExtra("username", userName)

            val addNextIntent = TaskStackBuilder.create(applicationContext).addParentStack(ChatActivity::class.java).addNextIntent(intent)
            val pendingIntent = addNextIntent.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            val build = NotificationCompat.Builder(applicationContext, "id")
                    .setContentTitle(userName)
                    .setContentText(contentText)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.atom)
                    .setChannelId("id").build()
            notificationManager.notify(1, build)
        }
    }

    private fun playVibrator() {
        val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500)
    }

    private fun playMediaPlayer() {
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        RingtoneManager.getRingtone(applicationContext, defaultUri).play()
    }


    override fun onTerminate() {
        FaceRecognize.onDestroy()
        super.onTerminate()
    }


    private fun setDefaultUser() {
        GlobalScope.launch {
            val names = arrayOf("Atomu", "LiangZhaoyang", "WuYiming", "ZhangXiangyu", "ChenWeijie", "LiuJiahui", "SunQianying", "WangJianfeng", "ZhouXingyu", "HuangYifan", "LiMinghui", "DengYuhan", "TangZhengyang", "LinQingyang", "GaoXiaodong", "HuQianwen", "JinXinyi", "FengYunlong", "CaoXinran", "LiJiaming", "ZhouYifei", "WuYufei", "ChenJianyu", "XuYuhang", "ZhangXinyi", "WangMengjie", "LiuXiaowei", "HuangZhihao", "YangKaiwen", "ShenZhihui", "GuoYaqi", "TangXueqin", "DengYuting", "JiangYingjie", "HuShanshan", "YaoZhijun", "FanXiaojing", "MeiXiaochen", "CaiMengxuan")
            val userDao = DB.getInstance(this@App).userDao()
            val emc = EMClient.getInstance()
            emc.login("root", "123", object : EMCallBack {
                override fun onSuccess() {}
                override fun onProgress(progress: Int, status: String?) {}
                override fun onError(code: Int, error: String?) {}
            })
            userDao.addUser(User(0, "root", "123", "管理员", create_time = System.currentTimeMillis()))
            userDao.addUser(User(1, "yangjing", "123", "元老", create_time = System.currentTimeMillis()))
            for (i in names.indices) {
                userDao.addUser(User(i + 2, names[i], "111", create_time = System.currentTimeMillis()))
                try {
                    emc.createAccount(names[i], "111")
                } catch (e: HyphenateException) {
                    Log.d("setDefaultUser", "注册失败: " + e.message)
                }
                try {
                    emc.contactManager().addContact(names[i], "Initialization Contact")
                } catch (e: HyphenateException) {
                    Log.e("setDefaultUser", "添加用户失败: " + e.message)
                }
            }
            emc.logout(true)

            for (i in names.indices) {
                delay(200)
                try {
                    emc.login(names[i], "111", object : EMCallBack {
                        override fun onSuccess() {
                            emc.contactManager().addContact("root", "Initialization Contact")
                            emc.logout(true)
                        }

                        override fun onProgress(progress: Int, status: String?) {
                            Log.d("setDefaultUser", "onProgress: $status")
                        }

                        override fun onError(code: Int, error: String?) {
                            Log.d("setDefaultUser", "onError: $error")
                        }
                    })
                } catch (e: HyphenateException) {
                    Log.e("setDefaultUser", "同意好友失败: " + e.message)
                }
            }

            Log.d("setDefaultUser", "setDefaultUser: Done")
        }

    }

}

