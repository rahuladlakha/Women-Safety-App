package com.rahul.womensafetyapp

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.util.*
import kotlin.collections.ArrayList

class AlertService : Service() {
    lateinit var smsManager: SmsManager
    companion object {
        var isServiceRunning = false;
        var mainActivity: AppCompatActivity? = null;
    }
    private var shake = 0
    private var lastShakeTime = 0L
    lateinit var notificationTone : Ringtone
    private lateinit var fusedLocationClient : FusedLocationProviderClient
    private var isAccPositive = false //this variable will be used by detecteThriceshake method to detect the up-down movements

    private fun detectThriceShake(){
        // Toast.makeText(this, "Shake was detected", Toast.LENGTH_SHORT).show()
        if (shake >= 5){
            Toast.makeText(this, mainActivity?.getString(R.string.em_ges_detected)?:"Emergency gesture was detected!", Toast.LENGTH_SHORT).show()
            val sp = this.getSharedPreferences("com.rahul.womenSafetyApp", Context.MODE_PRIVATE);
            val name = sp.getString("user name", " ")?: " "
            val contacts : List<String> = (sp.getString("emergency contacts", " ") ?: " ").trim().split("*")
            val codes = sp.getString("countryCodes","")?.trim()?.split("*")

            var smsDes = mainActivity?.getString(R.string.sos_des)?:"HELP!\nThis SMS was sent from Akira app to you since you are an emergency contact in this user's contact list. Kindly call this contact soon."
            for (i in 0..(contacts.size-1))
                if (codes != null && contacts[i] != null && !contacts[i].trim().isEmpty() ) {
                    smsManager.sendTextMessage(
                        "+${codes[i]}${contacts[i]}",
                        null,
                        smsDes,
                        null,
                        null
                    )
                    Toast.makeText(this,(mainActivity?.getString(R.string.sendind_sos_to)?:"Sending SOS to emergency contact : ") + "${codes[i]}${contacts[i]}",Toast.LENGTH_SHORT).show()
                }
            // Note that the maximum length for sending sms with sendTextMessage is 160 characters. If text exceeds this range no msg will be sent.
            //If the length is greater, we use sendMultiparttextMessage
            //Toast.makeText(this, contacts.toString(), Toast.LENGTH_SHORT).show()

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
            fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
                object : CancellationToken() {
                    override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken = CancellationTokenSource().token

                    override fun isCancellationRequested(): Boolean = false

                }).addOnSuccessListener {
                Log.i("location", "${it?.longitude}, ${it?.latitude}")
                if (it?.longitude == null || it?.latitude == null) return@addOnSuccessListener

                for (i in 0..(contacts.size-1)) {
                    if (codes != null && contacts[i] != null && !contacts[i].trim().isEmpty() ) {
                        Toast.makeText(
                            this@AlertService,
                            (mainActivity?.getString(R.string.sending_loc_to)?:"Sending location to emergency contact: ")+"${codes[i]}${contacts[i]}",
                            Toast.LENGTH_SHORT
                        ).show()

                        smsManager.sendTextMessage(
                            "+${codes[i]}${contacts[i]}",
                            null,
                            mainActivity?.getString(R.string.sos_des)?:"User's current location is : "+" http://maps.google.com/maps?q=loc:${it?.latitude},${it?.longitude}",
                            null,
                            null
                        )
                    }
                // Note that the maximum length for sending sms with sendTextMessage is 160 characters. If text exceeds this range no msg will be sent.
                //If the length is greater, we use sendMultiparttextMessage
//                for (contact in contacts){
//                    Log.i("Sending sms", "to ${contacts[0]}")
//                    smsDes += " http://maps.google.com/maps?q=loc:${it?.latitude},${it?.longitude}"
//                    val msgs = smsManager.divideMessage(smsDes) as ArrayList<String>
//                    Log.i("SMSDes", smsDes)
//                    smsManager.sendMultipartTextMessage("+918570962219", null, msgs, null, null)
                    // Note that the maximum length for sending sms with sendTextMessage is 160 characters. If text exceeds this range no msg will be sent.
                }
//                Toast.makeText(this@AlertService, "Sms sent",Toast.LENGTH_SHORT).show()

//                }
            }
        }
        if (Date().time - lastShakeTime < 800 || lastShakeTime == 0L){
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
                if (y  > 25.0 || y  < -5.0 ) {
                    if ( isAccPositive != (y > 0) ) {
                        isAccPositive = !isAccPositive
                        this@AlertService.detectThriceShake()
                        Log.i("acc", "" + (y))
                    }
                }

            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                return
            }
        }, sensorShake, 0 )

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isServiceRunning = true
        smsManager =  SmsManager.getDefault()
        val pendingIntent: PendingIntent =
            Intent(this, AlertService::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification : Notification = NotificationCompat.Builder(this, "service_notification")
            .setContentTitle(mainActivity?.getString(R.string.not_title)?:"Protection is ON")
            .setContentText(mainActivity?.getString(R.string.not_des)?:"Shake the device vertically 3 times to send SOS")
            .setSmallIcon(R.drawable.ic_women_safety_app_outline)
            .setContentIntent(pendingIntent)
            .build()

        val notif = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationTone = RingtoneManager.getRingtone(this, notif)
        activateSensors()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) // For accessing location in the
        //background we use the FusedLocation api

// Notification ID cannot be 0.
        startForeground(98, notification)

        return super.onStartCommand(intent, flags, startId)

    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        isServiceRunning = false
        this@AlertService.stopForeground(true)
        stopSelf() //stopForeground stops the service but it is still running in background.
          //  stopSelf stops the service itself
    }
}