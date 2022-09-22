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
//        val animation = {count : Int, func : (Int, Float) -> Unit ->
//            frameImageView.animate().translationYBy(movArray[count%3].first)
//            .setDuration(movArray[count%3].second). withEndAction{ }}

//        object : CountDownTimer(50000, 5500) {
//            override fun onTick(p0: Long) {
//                object : CountDownTimer(4500, 1500) {
//                    override fun onTick(p0: Long) {
//                        if (shake_count.text.toString().toInt() >= 3)
//                            shake_count.setText("0", TextView.BufferType.EDITABLE)
//                        frameImageView.animate().translationYBy(150f).setDuration(500)
//                            .withEndAction {
//                                frameImageView.animate().translationYBy(-300f).setDuration(1000)
//                                    .withEndAction {
//                                        frameImageView.animate().translationYBy(150f)
//                                            .setDuration(500)
//                                            .withEndAction{
//                                                var count = this@TutorialActivity.shake_count.text.toString().toInt()+1
//                                                if (count > 3) count = 0;
//                                                this@TutorialActivity.shake_count.setText(count.toString(),
//                                                    TextView.BufferType.EDITABLE)
//                                            }
//                                    }
//                            }
//                    }
//                    override fun onFinish() {}
//                }.start()
//            }
//            override fun onFinish() {}
//        }.start()

        skipTutorialButton.setOnClickListener{
            this@TutorialActivity.finish()
        }
    }
}