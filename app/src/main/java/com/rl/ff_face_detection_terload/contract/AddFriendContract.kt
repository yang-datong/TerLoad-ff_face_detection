package com.rl.ff_face_detection_terload.contract

import android.content.Context

/**
 * @author 杨景
 * @description:
 * @date :2023/1/4 17:25
 */
interface AddFriendContract {
    interface Presenter : BasePresenter{
        fun search(key: String, context: Context)
    }
    interface View {
        fun onSearchSuccess()
        fun onSearchFailed()
    }
}