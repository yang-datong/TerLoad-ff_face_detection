package com.example.myapplication.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.emp.ContactEmp
import com.example.myapplication.ui.activity.ChatActivity
import com.example.myapplication.widget.ContactListItemView
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.view_contact_item.view.*
import org.jetbrains.anko.*

/**
 * @author 杨景
 * @description:
 * @date :2021/1/2 23:54
 */
class ContactListAdapter(var context: Context, val contactListItems: MutableList<ContactEmp>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ContactListViewHolder(ContactListItemView(context))
    }

    override fun getItemCount() = contactListItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val contactListItemView = holder.itemView as ContactListItemView
        if (contactListItems[position].isShowFirst){
            contactListItemView.firstLetter.visibility  = View.VISIBLE
            contactListItemView.firstLetter.text  = contactListItems[position].firstLetter+""
        }else
            contactListItemView.firstLetter.visibility  = View.GONE

        contactListItemView.userNmae.text = contactListItems[position].userName
        val username = contactListItems[position].userName
        contactListItemView.setOnClickListener { context.startActivity<ChatActivity>("username" to username) }
        contactListItemView.setOnLongClickListener {
            context.alert("同时会屏蔽对方的临时对话，不再接收此人的消息","删除好友"){
                positiveButton("确认删除") {deleteFriend(username)}
                negativeButton("取消") {  }
            }.show()
            true
        }
    }

    private fun deleteFriend(username: String) {
        doAsync {
            EMClient.getInstance().contactManager().deleteContact(username)
            uiThread {
                context.toast("删除成功！")
            }
        }
    }

    class ContactListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}