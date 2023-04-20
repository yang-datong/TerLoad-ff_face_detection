package com.rl.ff_face_detection_terload.ui.fargment

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.ui.activity.LoginActivity
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.database.DataOperation
import com.rl.ff_face_detection_terload.extensions.zipDirectory
import com.rl.ff_face_detection_terload.ui.activity.UploadFaceActivity
import kotlinx.android.synthetic.main.fragment_dynamic.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * @author 杨景
 * @description:
 * @date :2021/1/2 22:24
 */
//动态页面
class DynamicFragment : BaseFragment() {

    override fun getLayoutResID() = R.layout.fragment_dynamic

    private val sp by lazy {
        context?.getSharedPreferences("theme_model", Context.MODE_PRIVATE)
    }

    override fun inits() {
        tv_user_nmae.text = EMClient.getInstance().currentUser
        bt_logout.setOnClickListener {
            showBottomDialog("退出后将接受不到信息！", "退出登录") {
                showProgress()
                logout()
            }
        }
        bt_upload.setOnClickListener {
            context?.startActivity<UploadFaceActivity>()
        }
        bt_data_backup.setOnClickListener {
            val status = arrayOf(-1, -1)
            showBottomDialog("将要备份数据到\"${ContextWrapper(context?.applicationContext).dataDir}\"会覆盖原来的备份文件，是否继续？",
                    "继续") {
                dismissBottomDialog()
                showProgress()
                //SHM文件通常用于存储缓存数据或者共享数据结构,SHM文件只有在Memory Mapping模式下才会存在
                //当应用程序执行修改操作时，Room会先将操作记录到WAL文件中，然后在后台异步地将这些操作应用到数据文件中,WAL文件只有在WAL模式下才会存在。
                status[0] = DataOperation.backupDataBase(arrayOf("user.db", "user.db-shm", "user.db-wal"), context?.applicationContext)
                status[1] = DataOperation.backupFaceData(arrayOf("${EMClient.getInstance().currentUser}.jpg", "model/"), context?.applicationContext)
                if (status[0] == 0 && status[1] == 0) {
                    Snackbar.make(it, "备份数据完成", Snackbar.LENGTH_LONG).show()
                } else if (status[0] == 0 && status[1] != 0) {
                    Snackbar.make(it, "备份用户数据完成", Snackbar.LENGTH_LONG).show()
                } else {
                    Snackbar.make(it, "备份数据失败", Snackbar.LENGTH_LONG).show()
                }
                dismissProgress()
            }
        }
        bt_data_restore.setOnClickListener {
            val status = arrayOf(-1, -1)
            showBottomDialog("将要从\"${ContextWrapper(context?.applicationContext).dataDir}\"备份文件恢复到数据，是否继续？", "继续") {
                dismissBottomDialog()
                showProgress()
                status[0] = DataOperation.restoreDataBase(arrayOf("user.db", "user.db-shm", "user.db-wal"), context?.applicationContext)
                status[1] = DataOperation.restoreFaceData(arrayOf("${EMClient.getInstance().currentUser}.jpg", "model/"), context?.applicationContext)
                if (status[0] == 0 && status[1] == 0)
                    Snackbar.make(it, "恢复数据完成", Snackbar.LENGTH_LONG).show()
                else if (status[0] == 0 && status[1] != 0)
                    Snackbar.make(it, "恢复用户数据完成", Snackbar.LENGTH_LONG).show()
                else if (status[0] == 1 || status[1] != 1)
                    Snackbar.make(it, "当前没有可恢复的数据文件", Snackbar.LENGTH_LONG).show()
                else
                    Snackbar.make(it, "恢复数据失败", Snackbar.LENGTH_LONG).show()
                dismissProgress()
            }
        }

        bt_theme_model.setOnClickListener {
            sp?.let {
                val hasDark = it.getBoolean("dark", false)
                if (hasDark)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)//日间模式
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) //夜间模式

                it.edit().putBoolean("dark", !hasDark).apply()
            }
        }
    }

    private fun logout() {
        EMClient.getInstance().logout(true, object : EMCallBack {
            override fun onSuccess() {
                dismissProgress()
                context?.startActivity<LoginActivity>()
                activity?.finish()
            }

            override fun onProgress(progress: Int, status: String?) {
            }

            override fun onError(code: Int, error: String?) {
                context?.toast("退出异常")
            }
        })
    }
}