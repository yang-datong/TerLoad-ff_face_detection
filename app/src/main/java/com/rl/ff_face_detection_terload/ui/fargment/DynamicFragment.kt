package com.rl.ff_face_detection_terload.ui.fargment

import android.content.ContextWrapper
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.ui.activity.LoginActivity
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.database.DataBaseOperation
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

    override fun inits() {
        tv_user_nmae.text = EMClient.getInstance().currentUser
        bt_logout.setOnClickListener {
            context?.alert("退出后将接受不到信息！") {
                positiveButton("退出登录") {
                    logout()
                }
                noButton { }
            }?.show()
        }
        bt_upload.setOnClickListener {
            context?.startActivity<UploadFaceActivity>()
        }

        bt_database_backup.setOnClickListener {
            showBottomDialog("将要备份数据库到\"${ContextWrapper(context?.applicationContext).dataDir}\"会覆盖原来的备份文件，是否继续？",
                    "继续", object : OnClickListener {
                override fun onClick(v: View) {
                    //SHM文件通常用于存储缓存数据或者共享数据结构,SHM文件只有在Memory Mapping模式下才会存在
                    //当应用程序执行修改操作时，Room会先将操作记录到WAL文件中，然后在后台异步地将这些操作应用到数据库文件中,WAL文件只有在WAL模式下才会存在。
                    val status = DataBaseOperation.backupData(arrayOf("user.db", "user.db-shm", "user.db-wal"), context?.applicationContext)
                    if (status == 0) {
                        Snackbar.make(it, "备份数据库完成", Snackbar.LENGTH_LONG).show()
                    } else {
                        Snackbar.make(it, "备份数据库失败", Snackbar.LENGTH_LONG).show()
                    }
                    dismissBottomDialog()
                }
            })
        }

        bt_database_restore.setOnClickListener {
            showBottomDialog("将要从\"${ContextWrapper(context?.applicationContext).dataDir}\"备份文件恢复到数据库，是否继续？", "继续", object : OnClickListener {
                override fun onClick(v: View) {
                    val status = DataBaseOperation.restoreData(arrayOf("user.db", "user.db-shm", "user.db-wal"), context?.applicationContext)
                    when (status) {
                        0 -> {
                            Snackbar.make(it, "恢复数据库完成", Snackbar.LENGTH_LONG).show()
                        }
                        1 -> {
                            Snackbar.make(it, "当前没有可恢复的数据库文件", Snackbar.LENGTH_LONG).show()
                        }
                        else -> {
                            Snackbar.make(it, "恢复数据库失败", Snackbar.LENGTH_LONG).show()
                        }
                    }
                    dismissBottomDialog()
                }
            })
        }
    }

    private fun logout() {
        EMClient.getInstance().logout(true, object : EMCallBack {
            override fun onSuccess() {
                context?.startActivity<LoginActivity>()
                activity?.finish()
            }

            override fun onProgress(progress: Int, status: String?) {
                showProgress("退出中...")
            }

            override fun onError(code: Int, error: String?) {
                context?.toast("退出异常")
            }
        })
    }
}