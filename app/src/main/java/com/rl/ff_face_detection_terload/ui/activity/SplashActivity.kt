package com.rl.ff_face_detection_terload.ui.activity

import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.rl.ff_face_detection_terload.R
import com.rl.ff_face_detection_terload.contract.SplashContract
import com.rl.ff_face_detection_terload.presenter.SplashPresenter
import kotlinx.android.synthetic.main.activity_splash.*
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity(), SplashContract.View {
    companion object {
        const val DELAY = 2000L
        const val NO_DELAY = 0L
//        const val NO_DELAY = 500L
    }

    override fun getLayoutResID() = R.layout.activity_splash

    var presenter = SplashPresenter(this)

    override fun inits() {
        val hasDark = defaultSharedPreferences.getBoolean("dark", false)
        if (!hasDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)//日间模式
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.log2))
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) //夜间模式
            imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.log2_night))
        }
        presenter.checkLoginStatus(this)
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
        handler.postDelayed({
            startActivity<MainActivity>()
            finish()
        }, NO_DELAY)
    }
}