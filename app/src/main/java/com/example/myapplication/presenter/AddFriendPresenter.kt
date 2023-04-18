package com.example.myapplication.presenter

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.example.myapplication.contract.AddFriendContract
import com.example.myapplication.emp.AddFriendItem
import com.example.myapplication.emp.MyUser
import com.example.myapplication.emp.Person
import com.hyphenate.chat.EMClient
import org.jetbrains.anko.doAsync
import org.litepal.LitePal


/**
 * @author 杨景
 * @description:
 * @date :2021/1/3 0:15
 */
class AddFriendPresenter(val view: AddFriendContract.View) : AddFriendContract.Presenter {
    val addFriendItems  = mutableListOf<AddFriendItem>()

    override fun search(key: String) {
        addFriendItems.clear()
        val categoryBmobQuery = BmobQuery<MyUser>()
        categoryBmobQuery.addWhereContains("userName", key)
                .addWhereNotEqualTo("userName", EMClient.getInstance().currentUser)
        categoryBmobQuery.findObjects(object : FindListener<MyUser>() {
            override fun done(p0: MutableList<MyUser>, p1: BmobException?) {
                if (p1 == null) {
                    doAsync {
                        p0.forEach { zoomIt ->
                            var isFriend = false
                            LitePal.findAll(Person::class.java).forEach {
                                if (zoomIt.userName == it.username)
                                    isFriend = true
                            }
                                addFriendItems.add(AddFriendItem(zoomIt.userName, zoomIt.createdAt, isFriend))
                        }
                    uiThread {
                        view.onSearchSuccess()
                    }
                    }
                } else view.onSearchFailed()
            }
        })
    }


}