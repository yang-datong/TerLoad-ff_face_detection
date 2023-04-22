package com.rl.ff_face_detection_terload.extensions

import android.content.Context
import android.util.Log
import com.hyphenate.EMValueCallBack
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMUserInfo
import com.rl.ff_face_detection_terload.database.DB
import com.rl.ff_face_detection_terload.database.User
import com.rl.ff_face_detection_terload.database.UserStatusAndCheckTime
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.defaultSharedPreferences
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


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
//                user.password  //TODO
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




