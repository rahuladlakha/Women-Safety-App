package com.rahul.womensafetyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_tutorial.*

class TutorialActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        supportActionBar?.hide()

        val movArray = arrayOf(150f to 500L,-300f to 1000L,150f to 500L)
        fun animation(count : Int){
            if (count%3 == 0 && count != 0)
                shake_count.text = (shake_count.text.toString().toInt() + 1).toString()
            if (count >= 9){
                Handler().postDelayed({
                    shake_count.text = 0.toString()
                    animation(0)
                                      }, 1000)
                return
            }
            frameImageView.animate().translationYBy(movArray[count%3].first)
                .setDuration(movArray[count%3].second). withEndAction{ animation(count+1)}
        }
        animation(0)
        skipTutorialButton.setOnClickListener{
            this@TutorialActivity.finish()
        }
    }
}