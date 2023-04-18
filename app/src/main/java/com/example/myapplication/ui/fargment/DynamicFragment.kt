package com.example.myapplication.ui.fargment

import com.example.myapplication.R
import com.example.myapplication.ui.activity.LoginActivity
import com.hyphenate.EMCallBack
import com.hyphenate.chat.EMClient
import kotlinx.android.synthetic.main.fragment_dynamic.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

/**
 * @author 杨景
 * @description:
 * @date :2021/1/2 22:24
 */
//动态页面
class DynamicFragment : BaseFragment() {
    override fun getLayoutResID() = R.layout.fragment_dynamic

    override fun inits() {
        textView14.text = EMClient.getInstance().currentUser
        button.setOnClickListener {
            context?.alert("退出后将接受不到信息！") {
                positiveButton("退出登录") {
                    logout()
                }
                noButton {  }
            }?.show()
        }
    }

    private fun logout() {
        EMClient.getInstance().logout(true,object : EMCallBack {
            override fun onSuccess() {
                context?.startActivity<LoginActivity>()
                activity?.finish()
            }

            override fun onProgress(progress: Int, status: String?) {
                showProgress("退出中...")
            }

            override fun onError(code: Int, error: String?) {
                context?.toast("退出异常")
            }
        })
    }
}