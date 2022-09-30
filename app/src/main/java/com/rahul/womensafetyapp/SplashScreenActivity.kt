package com.rahul.womensafetyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import java.util.*

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()


        /*
        //Simply change the String argument of Locale constructor in first line to change language for app.
        //The below code is there for testing purposes
        val locale = Locale("zh")
        Locale.setDefault(locale)
        val res = this.resources
        val config = res.configuration
        config.locale = locale
        config.setLayoutDirection(locale)
        res.updateConfiguration(config, res.displayMetrics)
         */



        Handler().postDelayed(Runnable{
            val intent = Intent(this@SplashScreenActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }, 800)

    }
}