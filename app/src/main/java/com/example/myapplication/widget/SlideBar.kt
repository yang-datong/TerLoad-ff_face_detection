package com.example.myapplication.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import org.jetbrains.anko.sp

/**
 * @author 杨景
 * @description:
 * @date :2021/1/3 19:52
 */
class SlideBar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val letter = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

    private val textPaint = Paint()
    init {
        textPaint.apply {
            textAlign  = Paint.Align.CENTER
            textSize = sp(12).toFloat()
            color = Color.GRAY
        }
    }

    var onSectionChangeListener:OnSectionChangeListener ?= null

    override fun onDraw(canvas: Canvas) {
        var mesaHeight = 0
        letter.forEach {
            mesaHeight += height/letter.size
            canvas.drawText(it, (width/2).toFloat(), mesaHeight.toFloat(),textPaint)
        }
    }

    interface OnSectionChangeListener{
        fun onSectionChange(firstLetter : String)
        fun onFinishChange()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
            when(event!!.action){
                MotionEvent.ACTION_DOWN -> {
                    val index = event.y / (height / letter.size)
                    if (index<letter.size && index > 0)
                        onSectionChangeListener?.onSectionChange(letter[index.toInt()])
                    setBackgroundColor(Color.parseColor("#e1e1e1"))
                }
                MotionEvent.ACTION_MOVE -> {
                    val index = event.y / (height / letter.size)
                    if (index<letter.size && index > 0)
                        onSectionChangeListener?.onSectionChange(letter[index.toInt()])
                    setBackgroundColor(Color.parseColor("#e1e1e1"))
                }
                MotionEvent.ACTION_UP -> {
                    setBackgroundColor(Color.TRANSPARENT)
                    onSectionChangeListener?.onFinishChange()
                }
            }
        return true
    }

}