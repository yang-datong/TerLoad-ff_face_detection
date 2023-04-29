package com.rl.ff_face_detection_terload.ui.fargment

import android.app.Dialog
import android.content.ContextWrapper
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import com.google.android.material.snackbar.Snackbar
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.database.DB
import com.rl.ff_face_detection_terload.database.DataOperation
import com.rl.ff_face_detection_terload.database.User
import com.rl.ff_face_detection_terload.extensions.logout
import com.rl.ff_face_detection_terload.extensions.restartApp
import com.rl.ff_face_detection_terload.ui.activity.UpdateInfoActivity
import com.rl.ff_face_detection_terload.ui.activity.UploadFaceActivity
import kotlinx.android.synthetic.main.fragment_dynamic.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.startActivity
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter


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
//        uploadFile("${requireActivity().filesDir}/1.png", TAG, requireContext(), { uuid ->
////            downloadFile(uuid, imageView7, TAG, requireContext(), { showProgress() }, { dismissProgress() })
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
            showDataBaseOperationBottomDialog(onCSVClick = {
                bt_data_backup.isClickable = false
                val csvFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "users.csv")
                exportCSVCData(csvFile)
            }, onEXCELClick = {
                bt_data_backup.isClickable = false
                val excelFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "users.xlsx")
                exportEXCELData(excelFile)
            }, onDataSave = {
                doAsync {
                    //备份数据库文件
                    val status = arrayOf(-1, -1)
                    //SHM文件通常用于存储缓存数据或者共享数据结构,SHM文件只有在Memory Mapping模式下才会存在
                    //当应用程序执行修改操作时，Room会先将操作记录到WAL文件中，然后在后台异步地将这些操作应用到数据文件中,WAL文件只有在WAL模式下才会存在。
                    status[0] = DataOperation.backupDataBase(arrayOf("user.db", "user.db-shm", "user.db-wal"), context?.applicationContext)
                    status[1] = DataOperation.backupFaceData(arrayOf("${EMClient.getInstance().currentUser}.jpg", "model/"), context?.applicationContext)
                    requireContext().runOnUiThread {
                        dismissDataBottomDialog()
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
            })
        }
        bt_data_restore.setOnClickListener {
            showBottomDialog("将要从\"${ContextWrapper(context?.applicationContext).externalCacheDir}\"备份数据库(.db)恢复到数据，是否继续？", "继续") {
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

    private fun exportEXCELData(excelFile: File) {
        GlobalScope.launch {
            val userList: List<User> = DB.getInstance(requireActivity()).userDao().getAllUser()
            requireActivity().runOnUiThread {
                doAsync {
                    val workbook = XSSFWorkbook()
                    val sheet: XSSFSheet = workbook.createSheet("Users")
                    val headerRow: XSSFRow = sheet.createRow(0)
                    headerRow.apply {
                        createCell(0).setCellValue("id")
                        createCell(1).setCellValue("username")
                        createCell(2).setCellValue("password")
                        createCell(3).setCellValue("name")
                        createCell(4).setCellValue("email")
                        createCell(5).setCellValue("phone")
                        createCell(6).setCellValue("avatar")
                        createCell(7).setCellValue("status")
                        createCell(8).setCellValue("checkin_time")
                        createCell(9).setCellValue("checkout_time")
                        createCell(10).setCellValue("create_time")
                    }
                    var rowNum = 1
                    for (user in userList) {
                        val row: XSSFRow = sheet.createRow(rowNum++)
                        row.apply {
                            createCell(0).setCellValue(user.id.toString())
                            createCell(1).setCellValue(user.username)
                            createCell(2).setCellValue(user.password)
                            createCell(3).setCellValue(user.name)
                            createCell(4).setCellValue(user.email)
                            createCell(5).setCellValue(user.phone)
                            createCell(6).setCellValue(user.avatar)
                            createCell(7).setCellValue(user.status.toString())
                            createCell(8).setCellValue(user.checkin_time.toString())
                            createCell(9).setCellValue(user.checkout_time.toString())
                            createCell(10).setCellValue(user.create_time.toString())
                        }
                    }
                    val outputStream = FileOutputStream(excelFile)
                    workbook.write(outputStream)
                    workbook.close()
                    outputStream.close()
                    Log.d(TAG, "exportEXCELData: ${excelFile.absolutePath} Done")
                }
            }
        }
    }

    private fun exportCSVCData(csvFile: File) {
        GlobalScope.launch {
            val userList: List<User> = DB.getInstance(requireActivity()).userDao().getAllUser()
            val writer = FileWriter(csvFile)
            writer.append("id,username,password,name,email,phone,avatar,status,checkin_time,checkout_time,create_time\n")
            for (user in userList) {
                writer.append("${user.id},"
                        + user.username + ","
                        + user.password + ","
                        + user.name + ","
                        + user.email + ","
                        + user.phone + ","
                        + user.avatar + ","
                        + user.status + ","
                        + user.checkin_time + ","
                        + user.checkout_time + ","
                        + user.create_time + "\n")
            }
            writer.flush()
            writer.close()
            Log.d(TAG, "exportCSVCData: ${csvFile.absolutePath} Done")
        }
    }


    private val dataBaseOperationBottomDialog by lazy {
        Dialog(requireContext()).apply {
            setContentView(R.layout.database_export_dialog_bottom)
            window?.let {
                it.setBackgroundDrawableResource(R.drawable.bottom_dialog_backcolor)
                it.attributes.apply {
                    gravity = Gravity.BOTTOM
                    width = ViewGroup.LayoutParams.MATCH_PARENT
                    windowAnimations = R.style.BottomSheetDialogAnimation
                }
            }
            findViewById<TextView>(R.id.tv_message).text = "将要备份数据到\"${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}\"会覆盖原来的备份文件，是否继续？"
        }
    }

    private fun showDataBaseOperationBottomDialog(onCSVClick: () -> Unit, onEXCELClick: () -> Unit, onDataSave: () -> Unit) {
        dataBaseOperationBottomDialog.apply {
            findViewById<View>(R.id.bt_csv_format).setOnClickListener {
                onCSVClick()
                onDataSave()
            }
            findViewById<View>(R.id.bt_excel_format).setOnClickListener {
                onEXCELClick()
                onDataSave()
            }
            if (!isShowing)
                show()
        }
    }

    private fun dismissDataBottomDialog() {
        if (dataBaseOperationBottomDialog.isShowing)
            dataBaseOperationBottomDialog.dismiss()
    }
}
