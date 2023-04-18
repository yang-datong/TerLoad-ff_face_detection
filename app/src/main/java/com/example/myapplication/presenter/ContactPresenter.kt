package com.example.myapplication.presenter

import com.example.myapplication.contract.ContactContract
import com.example.myapplication.emp.ContactEmp
import com.example.myapplication.emp.Person
import com.hyphenate.chat.EMClient
import com.hyphenate.exceptions.HyphenateException
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.litepal.LitePal

/**
 * @author 杨景
 * @description:
 * @date :2021/1/3 0:15
 */
class ContactPresenter(val view: ContactContract.View) : ContactContract.Presenter {
    var contactListItems = mutableListOf<ContactEmp>()
    override fun loadData() {
        contactListItems.clear()
        LitePal.deleteAll(Person::class.java)
        doAsync {
            try {
                val allContactsFromServer = EMClient.getInstance().contactManager().allContactsFromServer
//                allContactsFromServer.sortWith(Comparator { o1, o2 -> o1[0].compareTo(o2[0]) })
                allContactsFromServer.sortBy { it[0] }
                allContactsFromServer.forEachIndexed { index, s ->
                    val isShow = index == 0 || s[0] != allContactsFromServer[index - 1][0]
                    contactListItems.add(ContactEmp(s, s[0].toUpperCase(), isShow))
                    Person(Math.random().toInt(),s).save()
                }
                uiThread { view.onLoadSuccess() }
            } catch (e: HyphenateException) {
                uiThread { view.onLoadFailed() }
            }

        }
    }
}