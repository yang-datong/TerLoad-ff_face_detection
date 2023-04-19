package com.rl.ff_face_detection_terload.ui.activity

import android.graphics.Color
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.faceRecognize.FaceRecognize
import org.jetbrains.anko.toast
import java.io.File

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
        faceRecognize?.onCreate(mTextureView, this) { _: Int, _: String -> }
        faceRecognize?.uploadFaceImage(mImageView)
        mButtonView.setOnClickListener {
            val takePictureTag = EMClient.getInstance().currentUser
            val file = File(filesDir.absolutePath, "/$takePictureTag.jpg")
            if (file.exists()) {
                showBottomDialog("当前已有可用的人脸识别模型，是否继续上传？", "继续", object : OnClickListener {
                    override fun onClick(v: View?) {
                        faceRecognize?.takePicture(takePictureTag)
//                        saveUserNameAndPassWord()
                        dismissBottomDialog()
                    }
                })
            } else {
                faceRecognize?.takePicture(takePictureTag)
            }
        }
    }

//    private fun saveUserNameAndPassWord() {
//         TODO
//    }


    override fun onResume() {
        super.onResume()
        faceRecognize?.onResume()
    }

    override fun onPause() {
        faceRecognize?.onPause()
        super.onPause()
    }
}