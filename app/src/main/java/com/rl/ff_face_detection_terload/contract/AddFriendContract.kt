package com.rl.ff_face_detection_terload.contract

/**
 * @author 杨景
 * @description:
 * @date :2021/1/4 17:25
 */
interface AddFriendContract {
    interface Presenter : BasePresenter{
        fun search(key: String)
    }
    interface View {
        fun onSearchSuccess()
        fun onSearchFailed()
    }
}