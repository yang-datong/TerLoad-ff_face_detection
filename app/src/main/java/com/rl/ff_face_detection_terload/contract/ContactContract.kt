package com.rl.ff_face_detection_terload.contract

import com.hyphenate.exceptions.HyphenateException

/**
 * @author 杨景
 * @description:
 * @date :2023/1/3 0:05
 */
interface ContactContract {
    interface Presenter : BasePresenter{
        fun loadData()
    }
    interface View {
        fun onLoadSuccess()
        fun onLoadFailed(e: HyphenateException)
    }
}