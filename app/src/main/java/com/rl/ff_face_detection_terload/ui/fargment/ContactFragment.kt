package com.rl.ff_face_detection_terload.ui.fargment

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.adapter.ContactListAdapter
import com.rl.ff_face_detection_terload.adapter.EMContactListenerAdapter
import com.rl.ff_face_detection_terload.contract.ContactContract
import com.rl.ff_face_detection_terload.presenter.ContactPresenter
import com.rl.ff_face_detection_terload.widget.SlideBar
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.title_bar.*
import org.jetbrains.anko.toast

/**
 * @author 杨景
 * @description:
 * @date :2021/1/2 22:24
 */
//联系人
class ContactFragment : BaseFragment() ,ContactContract.View{
    override fun getLayoutResID() = R.layout.fragment_contact

    val presenter by lazy { ContactPresenter(this) }

    override fun inits() {
        tv_title.text = "联系人"
        img_ret.isGone = true
        setHasOptionsMenu(true)
        recyclerview.apply {
            setHasFixedSize(true)  //当大小被固定的情况下使用 、可以减少重绘次数、减少资源损耗\
            layoutManager = LinearLayoutManager(context)
            adapter = ContactListAdapter(context,presenter.contactListItems)
        }
            presenter.loadData()



        EMClient.getInstance().contactManager().setContactListener(object :EMContactListenerAdapter(){
            override fun onContactDeleted(username: String?) {
                super.onContactDeleted(username)
                presenter.loadData()
            }

            override fun onContactAdded(username: String?) {
                super.onContactAdded(username)
                presenter.loadData()
            }
        })

        slideBar_view.onSectionChangeListener = object : SlideBar.OnSectionChangeListener{
            override fun onSectionChange(firstLetter: String) {
                textView.text =  firstLetter
                textView.visibility = View.VISIBLE
                val position = getPosition(firstLetter)
                println(position)
                if (position!=-1)
                recyclerview.smoothScrollToPosition(position)
            }

            override fun onFinishChange() {
                textView.visibility = View.GONE
            }
        }

    }

    private fun getPosition(firstLetter: String) =
        presenter.contactListItems.binarySearch {
//            Log.i("TAGsss", "firstLetter:${it.firstLetter}")
//            Log.i("TAGsss", "firstLetter[0]:${firstLetter[0]}")
//            Log.i("TAGsss", "firstLetter.minus(firstLetter[0]):${it.firstLetter.minus(firstLetter[0])}")
            it.firstLetter.minus(firstLetter[0])
        }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.addmenu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.add) {
//            context?.startActivity<AddFriendActivity>()
            Toast.makeText(context, "暂不开放", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onLoadSuccess() {
        recyclerview?.let {
            it.adapter?.notifyDataSetChanged()
        }
    }

    override fun onLoadFailed() {
        context.let { it?.toast("数据加载失败") }
    }
}