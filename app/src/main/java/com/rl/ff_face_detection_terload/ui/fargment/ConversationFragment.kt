package com.rl.ff_face_detection_terload.ui.fargment

import android.util.Log
import androidx.core.view.isGone
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hyphenate.chat.EMClient
import com.hyphenate.chat.EMMessage
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.adapter.ConversationListAdapter
import com.rl.ff_face_detection_terload.adapter.MessageListenerAdapter
import com.rl.ff_face_detection_terload.contract.ConversationContract
import com.rl.ff_face_detection_terload.presenter.ConversationPresenter
import kotlinx.android.synthetic.main.fragment_contact.recyclerview
import kotlinx.android.synthetic.main.fragment_conversation.*
import kotlinx.android.synthetic.main.title_bar.*
import org.jetbrains.anko.defaultSharedPreferences
import java.util.*

/**
 * @author 杨景
 * @description:
 * @date :2021/1/2 22:24
 */
//对话页面
class ConversationFragment : BaseFragment(), ConversationContract.View {
    companion object {
        private const val TAG = "ConversationFragment"
    }

    override fun getLayoutResID() = R.layout.fragment_conversation

    //TODO 第二次登录会报连接错误
    val presenter by lazy { ConversationPresenter(this) }

    override fun inits() {
        Log.d("ConversationFragment", "inits: ")
        tv_title.text = getString(R.string.page_one_root)
        if (requireContext().defaultSharedPreferences.getString("username", "") != "root") {
            tv_title.text = getString(R.string.page_one)
        }
        img_ret.isGone = true
        recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = ConversationListAdapter(context, presenter.conversations)
            ItemTouchHelper(callback).attachToRecyclerView(this)
        }

        smartrefresh.setOnRefreshListener {
            it.finishRefresh(1000)
            presenter.onConversations()
        }
        presenter.onConversations()
        EMClient.getInstance().chatManager().addMessageListener(msgListener)
    }

    private val msgListener = object : MessageListenerAdapter() {
        override fun onMessageReceived(messages: MutableList<EMMessage>?) {
            presenter.onConversations()
        }
    }

    private val callback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)//监听上下左右移动Item
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            recyclerView.adapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            Collections.swap(presenter.conversations, viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {//处理左右移动item
        }

        override fun isLongPressDragEnabled() = true   // 拖动排序动画
    }


    override fun onUpdate() {
        if (presenter.conversations.size == 0) {
            tv_mes_empt.isGone = false
            recyclerview.isGone = true
        } else {
            tv_mes_empt.isGone = true
            recyclerview.isGone = false
        }
        recyclerview.adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        presenter.onConversations()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }
}