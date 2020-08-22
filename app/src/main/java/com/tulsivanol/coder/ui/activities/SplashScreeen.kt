package com.tulsivanol.coder.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import com.tulsivanol.coder.R
import com.tulsivanol.coder.constants.Constants.Companion.SPLASH_TIME_OUT
import kotlinx.android.synthetic.main.activity_splash_screeen.*

class SplashScreeen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screeen)

        logo_src.setImageResource(R.drawable.logo)

        val r =  Runnable {
            startActivity(Intent(this@SplashScreeen,MainActivity::class.java))
            finish()
        }
        Handler().postDelayed(r, SPLASH_TIME_OUT)
    }
}