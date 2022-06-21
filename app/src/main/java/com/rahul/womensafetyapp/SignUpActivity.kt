package com.rahul.womensafetyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()


        saveButton.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                val name = NameEditText.text.toString().trim()
                val emContacts = arrayOf<String>(emergencyContact1.text.toString().trim(), emergencyContact2.text.toString().trim(), emergencyContact3.text.toString().trim())
                if (name.isEmpty()) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Name field can't be empty!",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                } else if (emContacts[0].isEmpty()) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Please provide atleast one emergency contact !",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                }
                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                this@SignUpActivity.finish()
            }
        })
    }
}