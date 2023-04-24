package com.rl.ff_face_detection_terload.ui.activity

import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.adapter.AddFriendListAdapter
import com.rl.ff_face_detection_terload.contract.AddFriendContract
import com.rl.ff_face_detection_terload.presenter.AddFriendPresenter
import kotlinx.android.synthetic.main.activity_addfriend.*
import org.jetbrains.anko.toast

/**
 * @author 杨景
 * @description:
 * @date :2023/1/4 16:34
 */
class AddFriendActivity : BaseActivity(), AddFriendContract.View {
    override fun getLayoutResID() = R.layout.activity_addfriend

    val presenter by lazy { AddFriendPresenter(this) }

    override fun inits() {
        recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = AddFriendListAdapter(context, presenter.addFriendItems)
        }
        presenter.search("", this)
        searchview.isSubmitButtonEnabled = true
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { presenter.search(it, this@AddFriendActivity) }
                hideSoftKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    override fun onSearchSuccess() {
        dismissProgress()
        recyclerview.adapter?.notifyDataSetChanged()
//        toast("搜索成功")
    }

    override fun onSearchFailed() {
        dismissProgress()
        toast("搜索失败，没有该用户")
    }
}