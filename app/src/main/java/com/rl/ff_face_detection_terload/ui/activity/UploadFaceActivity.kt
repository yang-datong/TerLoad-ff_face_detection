package com.rl.ff_face_detection_terload.ui.activity

import android.graphics.Color
import android.view.View
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.faceRecognize.FaceRecognize
import kotlinx.android.synthetic.main.activity_upload_face.*
import org.jetbrains.anko.defaultSharedPreferences

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
        showProgress()
        faceRecognize = FaceRecognize()
        faceRecognize?.onCreate(texture_view, this) { status, _ ->
            if (status == 0) {
                finish()
            }
        }
        faceRecognize?.uploadFaceImage(image_view)

        var takePictureTAG = intent.getStringExtra("takePictureTAG")
        if (takePictureTAG.isNullOrEmpty())
            takePictureTAG = filesDir.absolutePath + "/" + defaultSharedPreferences.getString("username", "xxx") + ".jpg"

        img_capture.setOnClickListener {
            faceRecognize?.takePicture(takePictureTAG)
        }

        dismissProgress()
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