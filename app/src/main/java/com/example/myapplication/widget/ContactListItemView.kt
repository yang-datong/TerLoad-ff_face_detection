package com.example.myapplication.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.example.myapplication.R

/**
 * @author 杨景
 * @description:
 * @date :2021/1/2 23:39
 */
class ContactListItemView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, R.layout.view_contact_item,this)
    }
}