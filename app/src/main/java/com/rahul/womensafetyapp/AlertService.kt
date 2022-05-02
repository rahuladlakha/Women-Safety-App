package com.rahul.womensafetyapp

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import java.util.*

class AlertService : Service() {

    var last_acc = 10f
    private var shake = 0
    private var lastShakeTime = 0L
    lateinit var notificationTone : Ringtone
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    private fun detectThriceShake(){
        // Toast.makeText(this, "Shake was detected", Toast.LENGTH_SHORT).show()
        if (shake >= 17){
            Toast.makeText(this, "Shake was detected" + shake, Toast.LENGTH_SHORT).show()
            notificationTone.play()
            shake = 0
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationClient.lastLocation.addOnSuccessListener{
                Log.i("location", "${it?.longitude}, ${it?.latitude}")
                val smsManager: SmsManager = SmsManager.getDefault()
                smsManager.sendTextMessage("+918570962219", null, "sms message sent from Women safety app: http://maps.google.com/maps?q=loc:${it?.latitude},${it?.longitude}", null, null)
            }
        }
        if (Date().time - lastShakeTime < 700 || lastShakeTime == 0L){
            shake++
            lastShakeTime = Date().time
            //Toast.makeText(this, "" + shake, Toast.LENGTH_SHORT).show()
            Log.i("Shake ", "" +shake)
        }
        else {
            lastShakeTime = 0
            shake = 0
        }
    }

    private fun activateSensors(){
        val sensorManager: SensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensorShake = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {
                if (p0 == null) return
                var x = p0.values[0]
                var y = p0.values[1]
                var z = p0.values[2]
                if (y - last_acc  > 4 || y - last_acc < -4) {
                    this@AlertService.detectThriceShake()
                    Log.i("acc", "" + (y - last_acc))
                }

//                    Toast.makeText(this@MainActivity, "Shake event detected", Toast.LENGTH_SHORT).show()
                last_acc = y

            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                return
            }
        }, sensorShake, SensorManager.SENSOR_DELAY_FASTEST )

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent: PendingIntent =
            Intent(this, AlertService::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification : Notification = NotificationCompat.Builder(this, "service_notification")
            .setContentTitle("Women Safety App is running")
            .setContentText("Protection is on")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setTicker("tickerText")
            .build()

        val notif = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationTone = RingtoneManager.getRingtone(this, notif)
        activateSensors()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) // For accessing location in the
        //background we use the FusedLocation api


// Notification ID cannot be 0.
        startForeground(98, notification)

//        Handler().postDelayed(Runnable {
////            val smsManager: SmsManager = SmsManager.getDefault()
////            smsManager.sendTextMessage("+918570962219", null, "sms message sent from Women safety app", null, null)
//            this@AlertService.stopForeground(true)
//            stopSelf() //stopForeground stops the service but it is still running in background.
////          //  stopSelf stops the service itself
//        }, 5000)
        return super.onStartCommand(intent, flags, startId)

    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}