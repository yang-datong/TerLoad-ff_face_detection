package com.rl.ff_face_detection_terload.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.rl.ff_face_detection_terload.R

/**
 * @author 杨景
 * @description:
 * @date :2023/1/2 23:39
 */
class ContactListItemView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, R.layout.view_contact_item,this)
    }
}