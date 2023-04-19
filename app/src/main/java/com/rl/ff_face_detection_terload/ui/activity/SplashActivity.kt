package com.rl.ff_face_detection_terload.ui.activity

import android.os.Handler
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.contract.SplashContract
import com.rl.ff_face_detection_terload.presenter.SplashPresenter
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity(), SplashContract.View {
    override fun getLayoutResID() = R.layout.activity_splash

    companion object {
        const val DELAY = 2000L
    }

    var presenter = SplashPresenter(this)

    override fun inits() {
        presenter.checkLoginStatus()
    }

    private val handler by lazy {
        Handler()
    }

    override fun onNotLoggedIn() {
        handler.postDelayed({
            startActivity<LoginActivity>()
            finish()
        }, DELAY)
    }

    override fun onLoggedIn() {
        startActivity<MainActivity>()
        finish()
    }
}