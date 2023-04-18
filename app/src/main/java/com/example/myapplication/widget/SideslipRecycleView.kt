//package com.example.myapplication.widget
//
//import android.content.Context
//import android.graphics.Rect
//import android.util.AttributeSet
//import android.view.MotionEvent
//import android.view.VelocityTracker
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//
///**
// * @author 杨景
// * @description:
// * @date :2021/1/7 13:25
// */
//class SideslipRecycleView @JvmOverloads constructor(
//        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : RecyclerView(context, attrs, defStyleAttr) {
//    companion object{
//        const val INVALID_POSITION = -1;  //触摸点的位置
//    }
//    private val frame by lazy { Rect() }
//    private  var mFlingView : ViewGroup? = null
//    private var mMenuViewWidth = INVALID_POSITION
//    private val mVelocityTracker by lazy {
//        VelocityTracker.obtain()
//    }
//
//    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
//        obtainVelocity(e)
//        when(e?.action){
//            MotionEvent.ACTION_DOWN -> {
//                val mPosition = pointPosition(e.x, e.y)
//                if (mPosition != INVALID_POSITION) {
//                    val linearLayoutManager = layoutManager as LinearLayoutManager
//                    val view  = mFlingView
//                     mFlingView = (getChildAt(mPosition - linearLayoutManager.findFirstVisibleItemPosition())) as ViewGroup
//                    view?.let { if (mFlingView != view && view.scaleX != 0f) view.scrollTo(0, 0) }
//                    if (mFlingView?.childCount == 2)
//                        mMenuViewWidth = mFlingView?.getChildAt(1)?.width!!
//                }
//            }
//            MotionEvent.ACTION_MOVE ->{
//                mVelocityTracker.computeCurrentVelocity(1000)
//                val yVelocity = mVelocityTracker.yVelocity
//                val xVelocity = mVelocityTracker.xVelocity
//                if (Math.abs(xVelocity) > 1000 && Math.abs(xVelocity) > Math.abs(yVelocity)
//                        || Math.abs(x - ))
//            }
//
//        }
//        return super.onInterceptTouchEvent(e)
//    }
//
//    private fun obtainVelocity(e: MotionEvent?) {
//        mVelocityTracker.addMovement(e);    // 添加触摸点MotionEvent
//    }
//
//
//    private fun pointPosition(x: Float, y: Float): Int {
//        val linearLayoutManager = layoutManager as LinearLayoutManager
//        val firstPosition = linearLayoutManager.findFirstVisibleItemPosition()
//        for (i in 0 until childCount){
//            val childAt = getChildAt(i)
//            if (childAt.visibility == View.VISIBLE){
//                childAt.getHitRect(frame)
//                if (frame.contains(x.toInt(), y.toInt()))
//                    return firstPosition + i
//            }
//        }
//        return INVALID_POSITION
//    }
//
//}