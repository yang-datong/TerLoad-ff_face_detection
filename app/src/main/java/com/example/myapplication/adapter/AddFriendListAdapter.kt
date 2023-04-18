package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.emp.AddFriendItem
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.view_add_friend_item.view.*
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast

/**
 * @author 杨景
 * @description:
 * @date :2021/1/2 23:54
 */
class AddFriendListAdapter(var context: Context, val addFriendListItems: MutableList<AddFriendItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(context)
        val view  = inflate.inflate(R.layout.view_add_friend_item, parent, false)
        return AddFriendListViewHolder(view)
    }

    override fun getItemCount() = addFriendListItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val addFriendListViewHolder = holder as AddFriendListViewHolder
        val itemView = addFriendListViewHolder.itemView
        itemView.textView4.text = addFriendListItems[position].userName
        itemView.textView5.text = addFriendListItems[position].createdAt
        itemView.button2.text = if (addFriendListItems[position].isFriend) "已添加" else "添加"
        itemView.button2.isEnabled = !addFriendListItems[position].isFriend
        itemView.button2.setOnClickListener {addFriend(addFriendListItems[position].userName)
            itemView.button2.text = "请求中"
        }
    }

    private fun addFriend(userName: String) {
        EMClient.getInstance().contactManager().aysncAddContact(userName,"收到一条好友请求",object : EMCallBack{
            override fun onSuccess() {
                context.runOnUiThread { toast("添加好友成功") }
            }

            override fun onProgress(progress: Int, status: String?) {
                TODO("Not yet implemented")
            }

            override fun onError(code: Int, error: String?) {
                context.runOnUiThread { toast("添加好友失败") }
            }
        })
    }

    class AddFriendListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}