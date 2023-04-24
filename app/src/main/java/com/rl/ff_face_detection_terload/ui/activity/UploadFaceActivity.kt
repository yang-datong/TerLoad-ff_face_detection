package com.rl.ff_face_detection_terload.ui.activity

import android.graphics.Color
import android.view.View
import com.hyphenate.chat.EMClient
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.faceRecognize.FaceRecognize
import kotlinx.android.synthetic.main.activity_upload_face.*
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
        faceRecognize = FaceRecognize()
        faceRecognize?.onCreate(texture_view, this) { status, faceRecognizeUserName ->
            if (status == 0) {
                finish()
            }
        }
        faceRecognize?.uploadFaceImage(image_view)
        img_capture.setOnClickListener {
            val takePictureTag = EMClient.getInstance().currentUser
            val file = File(filesDir.absolutePath, "/$takePictureTag.jpg")
            if (file.exists()) {
                showBottomDialog("当前已有可用的人脸识别模型，是否继续上传？", "继续") {
                    faceRecognize?.takePicture(takePictureTag)
                    dismissBottomDialog()
                }
            } else {
                faceRecognize?.takePicture(takePictureTag)
            }
        }
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