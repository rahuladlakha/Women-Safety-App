package com.rahul.womensafetyapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    lateinit var sp : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()
        sp = this@SignUpActivity.getSharedPreferences("com.rahul.womenSafetyApp", Context.MODE_PRIVATE)
        val name = sp.getString("user name", "")
        val contacts = sp.getString("emergency contacts", "")
        if (name != null && !name.isEmpty() && contacts != null && !contacts.isEmpty()) {
            startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
            this@SignUpActivity.finish()
        }


        saveButton.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                val name = NameEditText.text.toString().trim()
                var emContacts = arrayOf<String>(ccp1.selectedCountryCode.trim() + emergencyContact1.text.toString().trim(), ccp2.selectedCountryCode.trim() + emergencyContact2.text.toString().trim(), ccp3.selectedCountryCode.trim() + emergencyContact3.text.toString().trim())
                for ( i in 0..(emContacts.size-1)){
                    if (!emContacts[i].startsWith("+"))
                        emContacts[i] = "+${emContacts[i]}"
                }
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
                sp.edit().putString("user name", name)
                    .putString("emergency contacts", "${emContacts[0]}*${emContacts[1]}*${emContacts[2]}")
                    .apply()

                startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                this@SignUpActivity.finish()
            }
        })
    }
}