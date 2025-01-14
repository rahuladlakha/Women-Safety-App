package com.rahul.womensafetyapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    lateinit var sp : SharedPreferences
    var origin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        tncCheckBox.setOnCheckedChangeListener{button:CompoundButton?, state : Boolean ->
            saveButton.isEnabled = state
        }
        tncTextView.setOnClickListener {
            openLink(this, Uri.parse("https://akira-app.blogspot.com/2022/09/terms-and-conditions-akira-mobile.html"))
        }
        privacyPolicyTextView.setOnClickListener {
            openLink(this, Uri.parse("https://akira-app.blogspot.com/2022/09/privacy-policy-akira-mobile-application.html"))
        }

        sp = this@SignUpActivity.getSharedPreferences("com.rahul.womenSafetyApp", Context.MODE_PRIVATE)
        val name = sp.getString("user name", "")
        val contacts = sp.getString("emergency contacts",null)?.trim()?.split("*")
        if (intent.getStringExtra("origin") == null && name != null && !name.isEmpty() && contacts != null && !contacts.isEmpty()) {
            startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
            this@SignUpActivity.finish()
        } else if (intent.getStringExtra("origin") == "MainActivity") {

            origin = "MainActivity"
            tncLinearLayout.visibility = View.GONE
            saveButton.isEnabled = true

            NameEditText.setText(name, TextView.BufferType.EDITABLE)
            val codes = sp.getString("countryCodes", null)?.trim()?.split("*")
            if (codes != null) {
                //Toast.makeText(this, codes.toString(),Toast.LENGTH_SHORT).show()
                ccp1.setCountryForPhoneCode(codes[0].toInt())
                ccp2.setCountryForPhoneCode(codes[1].toInt())
                ccp3.setCountryForPhoneCode(codes[2].toInt())
            }
            if (contacts != null) {
                emergencyContact1.setText(contacts[0], TextView.BufferType.EDITABLE)
                if (contacts.size > 0)
                emergencyContact2.setText(contacts[1], TextView.BufferType.EDITABLE)
                if (contacts.size > 1)
                emergencyContact3.setText(contacts[2], TextView.BufferType.EDITABLE)
            }
        }
        saveButton.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                val name = NameEditText.text.toString().trim()
                var emContacts = arrayOf<String>( emergencyContact1.text.toString().trim(),  emergencyContact2.text.toString().trim(), emergencyContact3.text.toString().trim())
                var countryCodes = arrayOf<String>(ccp1.selectedCountryCode.trim(), ccp2.selectedCountryCode.trim(), ccp3.selectedCountryCode.trim() )

                if (name.isEmpty()) {
                        Toast.makeText(
                            this@SignUpActivity,
                            getString(R.string.name_cant_empty),
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                } else if (emContacts[0].isEmpty()) {
                        Toast.makeText(
                            this@SignUpActivity,
                            getString(R.string.provide_atleast_one_em),
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                }
                sp.edit().putString("user name", name)
                    .putString("emergency contacts", "${emContacts[0]}*${emContacts[1]}*${emContacts[2]}")
                    .putString("countryCodes", "${countryCodes[0]}*${countryCodes[1]}*${countryCodes[2]}")
                    .apply()

                if (origin  == null){
                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                }
                this@SignUpActivity.finish()
            }
        })
    }
}