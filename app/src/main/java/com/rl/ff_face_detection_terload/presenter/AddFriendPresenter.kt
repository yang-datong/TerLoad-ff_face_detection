package com.rl.ff_face_detection_terload.presenter

import android.content.Context
import android.util.Log
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.contract.AddFriendContract
import com.rl.ff_face_detection_terload.emp.AddFriendItem
import com.rl.ff_face_detection_terload.database.DB
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast


/**
 * @author 杨景
 * @description:
 * @date :2023/1/3 0:15
 */
class AddFriendPresenter(val view: AddFriendContract.View) : AddFriendContract.Presenter {
    val addFriendItems = mutableListOf<AddFriendItem>()

    override fun search(key: String, context: Context) {
        addFriendItems.clear()
        GlobalScope.launch {
            val userDao = DB.getInstance(context).userDao()
            val allUser = userDao.getAllUser()
            if (allUser.size > 0) {
                Log.d("AddFriendPresenter", "search: " + allUser.size)
                doAsync {
                    allUser.forEach {
                        addFriendItems.add(AddFriendItem(it.username, "a", false))
                    }
                    uiThread {
                        view.onSearchSuccess()
                    }
                }
            } else view.onSearchFailed()
            Log.d("AddFriendPresenter", "search: $key")
            if (key.isNotEmpty())
                doAsync {
                    addFriend(key, context)
                }
        }
    }

    private fun addFriend(userName: String, context: Context) {
        EMClient.getInstance().contactManager().aysncAddContact(userName, "收到一条用户请求", object : EMCallBack {
            override fun onSuccess() {
                context.applicationContext.runOnUiThread { toast("已发送添加用户申请") }
                view.onSearchSuccess()
            }

            override fun onProgress(progress: Int, status: String?) {
            }

            override fun onError(code: Int, error: String?) {
                context.applicationContext.runOnUiThread { toast("添加用户失败：${error}") }
            }
        })
    }

//    override fun search(key: String) {
//        addFriendItems.clear()
//        val categoryBmobQuery = BmobQuery<MyUser>()
//        categoryBmobQuery.addWhereContains("userName", key).addWhereNotEqualTo("userName", EMClient.getInstance().currentUser)
//        categoryBmobQuery.findObjects(object : FindListener<MyUser>() {
//            override fun done(p0: MutableList<MyUser>, p1: BmobException?) {
//                if (p1 == null) {
//                    doAsync {
//                        p0.forEach { zoomIt ->
//                            var isFriend = false
//                            LitePal.findAll(Person::class.java).forEach {
//                                if (zoomIt.userName == it.username)
//                                    isFriend = true
//                            }
//                                addFriendItems.add(AddFriendItem(zoomIt.userName, zoomIt.createdAt, isFriend))
//                        }
//                    uiThread {
//                        view.onSearchSuccess()
//                    }
//                    }
//                } else view.onSearchFailed()
//            }
//        })
//    }


}