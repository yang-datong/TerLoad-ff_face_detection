package com.rl.ff_face_detection_terload.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.hyphenate.EMCallBack
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMUserInfo
import com.rl.ff_face_detection_terload.database.DB
import com.rl.ff_face_detection_terload.database.User
import com.rl.ff_face_detection_terload.database.UserStatusAndCheckTime
import com.rl.ff_face_detection_terload.network.Api
import com.rl.ff_face_detection_terload.network.ApiService
import com.rl.ff_face_detection_terload.ui.activity.LoginActivity
import com.rl.ff_face_detection_terload.ui.activity.SplashActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.system.exitProcess


fun String.isValidUserName() = this.matches(Regex("^[a-zA-Z]\\w{2,15}$"))
fun String.isValidPassword() = this.matches(Regex("^.{3,20}$"))


fun zipDirectory(directoryPath: String, zipPath: String) {
    val directory = File(directoryPath)
    val zipFile = File(zipPath)

    ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zipOut ->
        zip(directory, directory.name, zipOut)
    }
}

private fun zip(fileToZip: File, fileName: String, zipOut: ZipOutputStream) {
    if (fileToZip.isHidden) {
        return
    }
    if (fileToZip.isDirectory) {
        if (fileName.endsWith("/")) {
            zipOut.putNextEntry(ZipEntry(fileName))
            zipOut.closeEntry()
        } else {
            zipOut.putNextEntry(ZipEntry("$fileName/"))
            zipOut.closeEntry()
        }
        val children = fileToZip.listFiles()
        for (childFile in children) {
            zip(childFile, fileName + "/" + childFile.name, zipOut)
        }
        return
    }
    FileInputStream(fileToZip).use { fi ->
        BufferedInputStream(fi).use { origin ->
            val entry = ZipEntry(fileName)
            zipOut.putNextEntry(entry)
            origin.copyTo(zipOut, 1024)
        }
    }
}


fun unZipToDirectory(zipFilePath: String, destDirectoryPath: String) {
    val destDir = File(destDirectoryPath)
    if (!destDir.exists()) {
        destDir.mkdir()
    }

    ZipInputStream(BufferedInputStream(FileInputStream(zipFilePath))).use { zipIn ->
        var entry: ZipEntry? = zipIn.nextEntry
        while (entry != null) {
            val filePath = destDirectoryPath + File.separator + entry.name
            if (!entry.isDirectory) {
                extractFile(zipIn, filePath)
            } else {
                val dir = File(filePath)
                dir.mkdir()
            }
            zipIn.closeEntry()
            entry = zipIn.nextEntry
        }
    }
}

private fun extractFile(zipIn: ZipInputStream, filePath: String) {
    BufferedOutputStream(FileOutputStream(filePath)).use { bos ->
        val bytesIn = ByteArray(1024)
        var read = 0
        while (zipIn.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
    }
}


fun formatTimestamp(timestamp: Long): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return format.format(Date(timestamp))
}


fun userObjToEMUserObj(user: User): EMUserInfo {
    return EMUserInfo().apply {
        userId = user.username
        nickname = user.name
        email = user.email
        phoneNumber = user.phone
        val json = JSONObject().apply {
            put("status", user.status)
            put("checkin_time", user.checkin_time)
            put("checkout_time", user.checkout_time)
        }
        ext = json.toString()
    }
}


fun emUserObjToUserObj(emUser: EMUserInfo, TAG: String): User {
    val user = User(username = emUser.userId, password = "", name = emUser.nickname, email = emUser.email, phone = emUser.phoneNumber)
    //解析自定义数据: 考勤状态、签到时间、签退时间
    val solutionCustomData = solutionCustomData(emUser.ext, TAG)
    solutionCustomData?.let { u ->
        user.status = u.status
        user.checkin_time = u.checkin_time
        user.checkout_time = u.checkout_time
    }
    return user
}

private fun solutionCustomData(ext: String?, TAG: String): UserStatusAndCheckTime? {
    if (!ext.isNullOrEmpty()) {
        try {
            val json = JSONObject(ext)
            return UserStatusAndCheckTime(json.getInt("status")
                    , json.getLong("checkin_time")
                    , json.getLong("checkout_time"))
        } catch (e: Exception) {
            Log.e(TAG, "Solution JSON error", e)
        }
    }
    return null
}


fun pullUpdateOtherUserDataByServer(otherUsername: String, TAG: String, _onSuccess: (user: User) -> Unit, _onError: (errorMsg: String?) -> Unit) {
    GlobalScope.launch {
        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(arrayOf(otherUsername), object : EMValueCallBack<Map<String?, EMUserInfo?>?> {
            override fun onSuccess(value: Map<String?, EMUserInfo?>?) {
                value?.let { v ->
                    v.values.first()?.let {
                        val user = emUserObjToUserObj(it, TAG)
                        Log.d(TAG, "onSuccess: $user")
                        _onSuccess(user)
                    }
                }
            }

            override fun onError(error: Int, errorMsg: String?) {
                Log.e(TAG, "fetchServerUserInfo-> onError: ${error},errorMsg: $errorMsg")
                _onError(errorMsg)
            }

        })
    }
}

fun pullUpdateOtherUserDataIntoDatabaseByServer(otherUsername: String, TAG: String, context: Context, _onSuccess: () -> Unit, _onError: (errorMsg: String?) -> Unit) {
    GlobalScope.launch {
        EMClient.getInstance().userInfoManager().fetchUserInfoByUserId(arrayOf(otherUsername), object : EMValueCallBack<Map<String?, EMUserInfo?>?> {
            override fun onSuccess(value: Map<String?, EMUserInfo?>?) {
                value?.let { v ->
                    v.values.first()?.let {
                        val user = emUserObjToUserObj(it, TAG)
                        Log.d(TAG, "onSuccess: $user")
                        GlobalScope.launch {
                            val userDao = DB.getInstance(context).userDao()
                            val id = userDao.getIdByUsername(otherUsername)
                            if (id == null) {
                                userDao.addUser(user)
                            } else {
                                user.id = id
                                userDao.updateUser(user)
                            }
                            _onSuccess()
                        }
                    }
                }
            }

            override fun onError(error: Int, errorMsg: String?) {
                Log.e(TAG, "fetchServerUserInfo-> onError: ${error},errorMsg: $errorMsg")
                _onError(errorMsg)
            }
        })
    }
}

fun updateCommonUserData(context: Context, id: Int, username: String, name: String) {
    context.defaultSharedPreferences.edit()
            .putInt("id", id)
            .putString("username", username)
            .putString("name", name).apply()
}

fun saveIntoSharedPreferencesEMUser(context: Context, it: EMUserInfo) {
    context.defaultSharedPreferences.edit()
            .putString("nickname", it.nickname)
            .putString("avatarUrl", it.avatarUrl)
            .putString("email", it.email)
            .putString("phoneNumber", it.phoneNumber)
            .putInt("gender", it.gender)
            .putString("signature", it.signature)
            .putString("birth", it.birth)
            .putString("ext", it.ext).apply()
}


fun checkIsCurrentDay(checkTime: Long): Boolean {
    val calendar = Calendar.getInstance()
    val phoneNowDay = calendar.get(Calendar.DAY_OF_YEAR)
    calendar.timeInMillis = checkTime
    val checkOutTimeDay = calendar.get(Calendar.DAY_OF_YEAR)
    return phoneNowDay == checkOutTimeDay
}


fun restartApp(context: Context) {
    val intent = Intent(context.applicationContext, SplashActivity::class.java)
// 设置 Intent 的 Flag，让 Activity 以一种"新"的方式启动
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_CLEAR_TASK or
            Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    exitProcess(0)
}


fun logout(activity: Activity, dismissProgress: () -> Unit) {
    EMClient.getInstance().logout(true, object : EMCallBack {
        override fun onSuccess() {
            dismissProgress()
            // 结束所有Activity并打开LoginActivity
            val intent = Intent(activity, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)
            activity.finish()
        }

        override fun onProgress(progress: Int, status: String?) {
        }

        override fun onError(code: Int, error: String?) {
            activity.toast("退出异常")
        }
    })
}


fun downloadFile(file_uuid: String, imageView: ImageView, TAG: String, context: Context, showProgress: () -> Unit, dismissProgress: () -> Unit) {
    showProgress()
    GlobalScope.launch {
        val apiService: ApiService = Api.retrofit.create(ApiService::class.java)
        val call: Call<ResponseBody?>? = apiService.downloadFile(
                "Bearer YWMtbfRn6uKwEe2GJEFppWaE_2PD2rdcAz8QsxFDvusmk8E0KLNYrxlOIotxj40nACBuAgMAAAGHs8Wx2AAPoABZ68C3-cSVkEnZN0-oQiTvGqt4PI2xwamTGOR3oSfpeA",
                file_uuid)//"cc0696a0-e2e7-11ed-88b6-f98a29f3b73f"

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
            }

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.code() == 200) {
                    Log.d(TAG, "onResponse: 下载文件成功")
                    GlobalScope.launch {
                        val responseBody: ResponseBody? = response.body()
                        val bytes: ByteArray? = responseBody?.bytes()
                        val bitmap: Bitmap? = BitmapFactory.decodeByteArray(bytes, 0, bytes?.size
                                ?: 0)
                        context.runOnUiThread {
                            imageView.setImageBitmap(bitmap)
                            dismissProgress()
                        }
                    }
                } else
                    Log.e(TAG, "onResponse: ${response.code()}  url : ${call.request().url}")
            }
        })
    }
}


fun uploadFile(filePath: String, TAG: String, context: Context, success: (uuid: String) -> Unit, showProgress: () -> Unit, dismissProgress: () -> Unit) {
    showProgress()
    GlobalScope.launch {
        val apiService: ApiService = Api.retrofit.create(ApiService::class.java)

        val requestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), filePath)
        val filePart = MultipartBody.Part.createFormData("file", filePath, requestBody)

        val call: Call<ResponseBody?>? = apiService.uploadFile(
                "Bearer YWMtbfRn6uKwEe2GJEFppWaE_2PD2rdcAz8QsxFDvusmk8E0KLNYrxlOIotxj40nACBuAgMAAAGHs8Wx2AAPoABZ68C3-cSVkEnZN0-oQiTvGqt4PI2xwamTGOR3oSfpeA",
                filePart)

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
            }

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.code() == 200) {
                    Log.d(TAG, "onResponse: 上传成功 ${response.raw()}")
                    val jsonObject = JSONObject(response.body()?.string())
                    val entities = jsonObject.optJSONArray("entities")

                    if (entities != null && entities.length() > 0) {
                        val entity = entities.optJSONObject(0)
                        val uuid = entity.optString("uuid")
                        val shareSecret = entity.optString("share-secret")
                        val fileType = entity.optString("type")
                        Log.d(TAG, "onResponse: uuid:${uuid}")
                        success(uuid)
                    }

                    GlobalScope.launch {
                        context.runOnUiThread {
                            dismissProgress()
                        }
                    }
                } else
                    Log.e(TAG, "onResponse: ${response.code()}  url : ${call.request().url}")
            }
        })
    }
}
