package com.rahul.womensafetyapp

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat

class AlertService : Service() {
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

// Notification ID cannot be 0.
        startForeground(98, notification)

        Handler().postDelayed(Runnable {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage("+918570962219", null, "sms message", null, null)
        }, 5000)
        return super.onStartCommand(intent, flags, startId)

    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}