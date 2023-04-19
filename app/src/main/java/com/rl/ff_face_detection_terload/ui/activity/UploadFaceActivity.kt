package com.rl.ff_face_detection_terload.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.faceRecognize.FaceRecognize

class UploadFaceActivity : BaseActivity() {

    private var faceRecognize: FaceRecognize? = null
    override fun getLayoutResID() = R.layout.activity_upload_face

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            statusBarColor = Color.TRANSPARENT
        }
    }

    override fun inits() {
        // 隐藏状态栏和顶部菜单栏
        val mTextureView = findViewById<TextureView>(R.id.texture_view)
        val mImageView = findViewById<ImageView>(R.id.image_view)
        val mButtonView = findViewById<Button>(R.id.button_capture)
        faceRecognize = FaceRecognize()
        faceRecognize?.onCreate(mTextureView, this)
        faceRecognize?.uploadFaceImage(mImageView)
        mButtonView.setOnClickListener { v: View? -> faceRecognize!!.takePicture() }
    }


    override fun onResume() {
        super.onResume()
        faceRecognize?.onResume()
    }

    override fun onPause() {
        faceRecognize?.onPause()
        super.onPause()
    }
}