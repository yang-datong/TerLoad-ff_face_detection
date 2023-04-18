package com.example.myapplication.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.ui.activity.ChatActivity
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMConversation
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import kotlinx.android.synthetic.main.view_conversion_item.view.*
import org.jetbrains.anko.startActivity
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author 杨景
 * @description:
 * @date :2021/1/5 21:49
 */
class ConversationListAdapter(val context: Context, private val conversations: MutableList<EMConversation>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val activity = context as Activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(context).inflate(R.layout.view_conversion_item, parent, false)
        return ConversationListItemViewHolder(inflate)

    }

    override fun getItemCount() = conversations.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val conversationListItemViewHolder = holder as ConversationListItemViewHolder
        conversationListItemViewHolder.itemView.textView6.text = conversations[position].conversationId()
        if (conversations[position].lastMessage.type == EMMessage.Type.TXT) {
            val emMessageBody = conversations[position].lastMessage.body as EMTextMessageBody
            conversationListItemViewHolder.itemView.textView7.text = emMessageBody.message
        }else
            conversationListItemViewHolder.itemView.textView7.text = "当前消息不为文本消息"

        conversationListItemViewHolder.itemView.textView8.text = SimpleDateFormat("HH:mm").format(conversations[position].lastMessage.msgTime)
        conversationListItemViewHolder.itemView.textView9.visibility = if (conversations[position].unreadMsgCount>0) View.VISIBLE else View.GONE
        conversationListItemViewHolder.itemView.textView9.text = conversations[position].unreadMsgCount.toString()

        conversationListItemViewHolder.itemView.layout_item.setOnClickListener {
            context.startActivity<ChatActivity>("username" to conversations[position].conversationId())
        }

        conversationListItemViewHolder.itemView.textView11.setOnClickListener {
            Collections.swap(conversations,position,0)
            notifyDataSetChanged()
        }

        conversationListItemViewHolder.itemView.textView12.setOnClickListener {
            EMClient.getInstance().chatManager().deleteConversation(conversations[position].conversationId(), false);
            conversations.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
        }

    }

    class ConversationListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}