package com.example.myapplication.ui.activity

import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.AddFriendListAdapter
import com.example.myapplication.contract.AddFriendContract
import com.example.myapplication.presenter.AddFriendPresenter
import kotlinx.android.synthetic.main.activity_addfriend.*
import org.jetbrains.anko.toast

/**
 * @author 杨景
 * @description:
 * @date :2021/1/4 16:34
 */
class AddFriendActivity : BaseActivity() ,AddFriendContract.View{
    override fun getLayoutResID()  = R.layout.activity_addfriend
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    val presenter  by lazy { AddFriendPresenter(this) }

    override fun inits() {
        recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = AddFriendListAdapter(context,presenter.addFriendItems)
        }
        presenter.search("")
        searchview.isSubmitButtonEnabled = true
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { presenter.search(it) }
                hideSoftKeyboard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSearchSuccess() {
        dismissProgress()
        recyclerview.adapter?.notifyDataSetChanged()
        toast("搜索成功")
    }

    override fun onSearchFailed() {
        dismissProgress()
        toast("搜索失败，没有该用户")
    }
}