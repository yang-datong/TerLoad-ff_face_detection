package com.example.myapplication.ui.activity

import android.os.Handler
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.contract.SplashContract
import com.example.myapplication.presenter.SplashPresenter
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity() ,SplashContract.View{
    override fun getLayoutResID() = R.layout.activity_splash

    companion object{
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