package com.rl.ff_face_detection_terload.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.TextureView
import android.view.View
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.faceRecognize.FaceRecognize
import org.jetbrains.anko.toast

class FaceRecognizeActivity : BaseActivity() {
    private var faceRecognize: FaceRecognize? = null
    override fun getLayoutResID() = R.layout.activity_face_recognize

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window.apply {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            statusBarColor = Color.TRANSPARENT
        }
    }


    override fun inits() {
        val mTextureView = findViewById<TextureView>(R.id.texture_view)
        faceRecognize = FaceRecognize()
        faceRecognize?.onCreate(mTextureView, this) { status, faceRecognizeUserName ->
            if (status == 0) {
                val resultIntent = Intent()
                if (!faceRecognizeUserName.isNullOrBlank()) {
                    resultIntent.putExtra("faceRecognizeUserName", faceRecognizeUserName)
                    setResult(Activity.RESULT_OK, resultIntent)
                } else {
                    setResult(Activity.RESULT_CANCELED, resultIntent)
                }
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        faceRecognize!!.onResume()
    }

    override fun onPause() {
        faceRecognize!!.onPause()
        super.onPause()
    }
}