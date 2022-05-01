package com.rahul.womensafetyapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    val permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.FOREGROUND_SERVICE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermissions()
        createNotificationChannel()

        val intent = Intent(this, AlertService::class.java) // Build the intent for the service
        applicationContext.startForegroundService(intent)


    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_MIN
            val channel = NotificationChannel("service_notification", "Background", importance)
            channel.description = "Required for app to function in the background"
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkPermissions( ){
        for ( i in 0..(permissions.size-1)){
           if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
             //  Toast.makeText(this, permissions[i], Toast.LENGTH_SHORT).show()
               ActivityCompat.requestPermissions(this, arrayOf(permissions[i]), i)
           }
       }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //if ( requestCode != permissions.size-1) {
           // Toast.makeText(this, permissions[requestCode], Toast.LENGTH_SHORT).show()
           //checkPermissions()
       // }
    }
}


