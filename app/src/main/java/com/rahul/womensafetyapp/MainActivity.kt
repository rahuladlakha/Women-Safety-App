package com.rahul.womensafetyapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hbb20.CCPCountry
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class MainActivity : AppCompatActivity() {
    /*
    App icon is a modification of icon available at :
    Pls copy paste below link in the app description on playstore
    <a href="https://www.flaticon.com/free-icons/shield" title="shield icons">Shield icons created by Freepik - Flaticon</a>
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = null

        material_button.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                enableProtection(this@MainActivity, protectionStatus.text != getString(R.string.on))
            }
        })
        checkPermissions(this)
        createNotificationChannel()
        enableProtection(this, AlertService.isServiceRunning)

    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel("service_notification", getString(R.string.not_channel_name), importance)
            channel.description = getString(R.string.not_channel_des)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.tutorial_item ->
                startActivity(Intent(this,TutorialActivity::class.java))
            R.id.tnc_item ->
                openLink(this, Uri.parse("https://akira-app.blogspot.com/2022/09/terms-and-conditions-akira-mobile.html"))
            R.id.policy_item ->
                openLink(this, Uri.parse("https://akira-app.blogspot.com/2022/09/privacy-policy-akira-mobile-application.html"))
            R.id.emergeny_contacts_item ->
                startActivity(Intent(this,SignUpActivity::class.java).putExtra("origin","MainActivity"))
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!checkIfPermissionsGranted(this))
            checkPermissions(this)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

}


