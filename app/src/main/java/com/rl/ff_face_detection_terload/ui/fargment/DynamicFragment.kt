package com.rl.ff_face_detection_terload.ui.fargment

import android.content.ContextWrapper
import android.util.Log
import androidx.core.view.isGone
import com.google.android.material.snackbar.Snackbar
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.database.DataOperation
import com.rl.ff_face_detection_terload.extensions.logout
import com.rl.ff_face_detection_terload.extensions.restartApp
import com.rl.ff_face_detection_terload.ui.activity.UpdateInfoActivity
import com.rl.ff_face_detection_terload.ui.activity.UploadFaceActivity
import kotlinx.android.synthetic.main.fragment_dynamic.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity


/**
 * @author 杨景
 * @description:
 * @date :2023/1/2 22:24
 */
//动态页面
class DynamicFragment : BaseFragment() {


    companion object {
        private const val TAG = "DynamicFragment"
    }

    override fun getLayoutResID() = R.layout.fragment_dynamic

    override fun inits() {
        Log.d("DynamicFragment", "inits: ")
        initView()

        //TODO 写好了文件上传下载工具函数
//        uploadFile("${requireActivity().filesDir}/1.png", TAG, requireContext(), { uuid ->
//            downloadFile(uuid, imageView7, TAG, requireContext(), { showProgress() }, { dismissProgress() })
//        }, {
//            showProgress()
//        }, { dismissProgress() })
    }

    private fun initView() {
        val username = requireContext().defaultSharedPreferences.getString("username", "")
        val name = requireContext().defaultSharedPreferences.getString("name", "")
        tv_user_nmae.text = if (name != "") name else username
        if (username != "root") {
            bt_update_info.text = getString(R.string.update_info)
            bt_data_backup.isGone = true
            bt_data_restore.isGone = true
        }
        bt_logout.setOnClickListener {
            showBottomDialog("退出后将接受不到信息！", "退出登录", requireContext().getColor(R.color.wechat_red)) {
                showProgress()
                logout(requireActivity()) { dismissProgress() }
            }
        }
        bt_upload.setOnClickListener {
            context?.startActivity<UploadFaceActivity>()
        }
        bt_data_backup.setOnClickListener {
            showBottomDialog("将要备份数据到\"${ContextWrapper(context?.applicationContext).externalCacheDir}\"会覆盖原来的备份文件，是否继续？",
                    "继续") {
                bt_data_backup.isClickable = false
                doAsync {
                    val status = arrayOf(-1, -1)
                    //SHM文件通常用于存储缓存数据或者共享数据结构,SHM文件只有在Memory Mapping模式下才会存在
                    //当应用程序执行修改操作时，Room会先将操作记录到WAL文件中，然后在后台异步地将这些操作应用到数据文件中,WAL文件只有在WAL模式下才会存在。
                    status[0] = DataOperation.backupDataBase(arrayOf("user.db", "user.db-shm", "user.db-wal"), context?.applicationContext)
                    status[1] = DataOperation.backupFaceData(arrayOf("${EMClient.getInstance().currentUser}.jpg", "model/"), context?.applicationContext)
                    requireContext().runOnUiThread {
                        dismissBottomDialog()
                        bt_data_backup.isClickable = true
                        if (status[0] == 0 && status[1] == 0) {
                            Snackbar.make(it, "备份数据完成", Snackbar.LENGTH_LONG).show()
                        } else if (status[0] == 0 && status[1] != 0) {
                            Snackbar.make(it, "备份用户数据完成", Snackbar.LENGTH_LONG).show()
                        } else {
                            Snackbar.make(it, "备份数据失败", Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
        bt_data_restore.setOnClickListener {
            showBottomDialog("将要从\"${ContextWrapper(context?.applicationContext).externalCacheDir}\"备份文件恢复到数据，是否继续？", "继续") {
                bt_data_restore.isClickable = false
                doAsync {
                    val status = arrayOf(-1, -1)
                    status[0] = DataOperation.restoreDataBase(arrayOf("user.db", "user.db-shm", "user.db-wal"), context?.applicationContext)
                    status[1] = DataOperation.restoreFaceData(arrayOf("${EMClient.getInstance().currentUser}.jpg", "model/"), context?.applicationContext)
                    requireActivity().runOnUiThread {
                        dismissBottomDialog()
                        bt_data_restore.isClickable = true
                        if (status[0] == 0 && status[1] == 0)
                            Snackbar.make(it, "恢复数据完成", Snackbar.LENGTH_LONG).show()
                        else if (status[0] == 0 && status[1] != 0)
                            Snackbar.make(it, "恢复用户数据完成", Snackbar.LENGTH_LONG).show()
                        else if (status[0] == 1 || status[1] != 1)
                            Snackbar.make(it, "当前没有可恢复的数据文件", Snackbar.LENGTH_LONG).show()
                        else
                            Snackbar.make(it, "恢复数据失败", Snackbar.LENGTH_LONG).show()
                    }
                }

            }
        }
        bt_theme_model.setOnClickListener {
            showBottomDialog("将重启App是否继续？", "继续") {
                requireActivity().defaultSharedPreferences.apply {
                    val commit = edit().putBoolean("dark", !getBoolean("dark", false)).commit()
                    if (commit)
                        restartApp(requireContext())
                }
            }
        }
        bt_update_info.setOnClickListener {
            requireActivity().startActivity<UpdateInfoActivity>()
        }
    }
}