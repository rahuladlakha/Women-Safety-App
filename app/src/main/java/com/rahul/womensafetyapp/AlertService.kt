package com.rahul.womensafetyapp

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.util.*

class AlertService : Service() {

    var last_acc = 10f
    private var shake = 0
    private var lastShakeTime = 0L

    private fun detectThriceShake(){
        // Toast.makeText(this, "Shake was detected", Toast.LENGTH_SHORT).show()
        if (shake >= 17){
            Toast.makeText(this, "Shake was detected" + shake, Toast.LENGTH_SHORT).show()
            shake = 0
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
        }, sensorShake, SensorManager.SENSOR_DELAY_NORMAL )

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

        activateSensors()

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