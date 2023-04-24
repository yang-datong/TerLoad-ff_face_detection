package com.rl.ff_face_detection_terload.adapter

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rl.ff_face_detection_terload.emp.ContactEmp
import com.rl.ff_face_detection_terload.ui.activity.UserDetailedActivity
import com.rl.ff_face_detection_terload.widget.ContactListItemView
import kotlinx.android.synthetic.main.view_contact_item.view.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity

/**
 * @author 杨景
 * @description:
 * @date :2023/1/2 23:54
 */
class ContactListAdapter(var context: Context, private val contactListItems: MutableList<ContactEmp>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ContactListViewHolder(ContactListItemView(context))
    }

    override fun getItemCount() = contactListItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val contactListItemView = holder.itemView as ContactListItemView
        if (contactListItems[position].isShowFirst) {
            contactListItemView.firstLetter.visibility = View.VISIBLE
            contactListItemView.firstLetter.text = "${contactListItems[position].firstLetter}"
            if (context.defaultSharedPreferences.getBoolean("dark", false))
                contactListItemView.firstLetter.setBackgroundColor(Color.BLACK)
            else
                contactListItemView.firstLetter.setBackgroundColor(Color.parseColor("#f3f3f3"))

        } else
            contactListItemView.firstLetter.visibility = View.GONE

        contactListItemView.userNmae.text = contactListItems[position].userName

        contactListItemView.setOnClickListener {
            context.startActivity<UserDetailedActivity>("username" to contactListItems[position].userName)
        }
        contactListItemView.setOnLongClickListener {
//            context.alert("同时会屏蔽对方的临时对话，不再接收此人的消息", "删除好友") {
//                positiveButton("确认删除") { deleteFriend(username) }
//                negativeButton("取消") { }
//            }.show()
            true
        }
    }

//    private fun deleteFriend(username: String) {
//        doAsync {
//            EMClient.getInstance().contactManager().deleteContact(username)
//            uiThread {
//                context.toast("删除成功！")
//            }
//        }
//    }

    class ContactListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}