package com.rl.ff_face_detection_terload.app

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.adapter.MessageListenerAdapter
import com.rl.ff_face_detection_terload.faceRecognize.FaceRecognize
import com.rl.ff_face_detection_terload.ui.activity.ChatActivity
import com.hyphenate.chat.*
import org.litepal.LitePal

class MyApplication : Application() {
    private val TAG = "MyApplication"
    private var isBackground = true


    private val sp by lazy {
        getSharedPreferences("isAutoLogin", Context.MODE_PRIVATE)
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
//                playMediaPlayer()//播放音频
                showNotification(messages)//显示通知栏
            } else {
                playVibrator() //震动
            }
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
                    .setSmallIcon(R.mipmap.ic_login_3party_wechat)
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

    override fun onCreate() {
        super.onCreate()
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) //夜间模式
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)//日间模式
        val isAuto = sp.getBoolean("isAuto", true)
        val emOptions = EMOptions()
        emOptions.autoLogin = isAuto
        val emc = EMClient.getInstance()
        emc.init(applicationContext, emOptions)
//        EMClient.getInstance().setDebugMode(BuildConfig.DEBUG)
        emc.chatManager().addMessageListener(msgListener)

//        Bmob.initialize(applicationContext, "c063550ad7c3587f4fae8ff7f68deef1");
        LitePal.initialize(applicationContext)

        registerActivityLifecycleCallbacks(mCallbacks)
        FaceRecognize.loadJNIFaceModel(this.applicationContext)
    }

    override fun onTerminate() {
        FaceRecognize.onDestroy()
        super.onTerminate()
    }
}

