package com.rahul.womensafetyapp
//this file contains a few necessary functions and classes used often in the app
import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.channels.InterruptedByTimeoutException

//class UserData {
//    val sp : SharedPreferences
//    init {
//        this.sp = this.getSharedPreferences("com.rahul.womenSafetyApp", Context.MODE_PRIVATE);
//    }
//    companion object {
//        val name : String
//    }
//}
private val permissions = arrayOf( Manifest.permission.ACCESS_FINE_LOCATION ,
    Manifest.permission.ACCESS_COARSE_LOCATION ,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.FOREGROUND_SERVICE
 )
private val permissionDes = arrayOf(
    "Location permission" to "This permission is required to access Locaton service on your device.",
    "Location permission" to "This permission is required to access Locaton service on your device.",
    "Background Location permission" to "Allows the app to access this device's location in background. Necessary for the app to function.\n\n Note: If tapping the below button doesn't open the permissions dialog, manually provide permission by going to app permission settings and selecting \"Allow all the time\" for location permisssion",
    "SMS permission" to "SMS permission is required to send SMS to your selected emergency contacts.",
    "Foreground Service permission" to "Allows the app to run in background."
)

fun checkIfPermissionsGranted(activity: AppCompatActivity) : Boolean =
    permissions.all{ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED}

fun checkPermissions( activity: AppCompatActivity) {

    fun requestPermission( perIndex : Int){
        AlertDialog.Builder(activity)
            .setIcon(R.drawable.ic_info_outline)
            .setTitle("Please provide the ${permissionDes[perIndex].first}")
            .setMessage(permissionDes[perIndex].second)
            .setPositiveButton("Proceed to grant", object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    if (permissions[perIndex] == Manifest.permission.ACCESS_BACKGROUND_LOCATION) {
                        Toast.makeText(activity, "Select \"Allow all the time\" in Location Permissions", Toast.LENGTH_LONG).show()
//                         if (activity.shouldShowRequestPermissionRationale(permissions[perIndex]))
//                             activity.startActivity(
//                                Intent().setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                    .setData(Uri.fromParts("package", activity.packageName, null))
//                            )
//
                    }
                    ActivityCompat.requestPermissions(activity, arrayOf(permissions[perIndex]), perIndex)
                }
            })
            .show()
    }

    val i = permissions.indexOfFirst { ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED }
    if (i in 0..(permissions.size-1) )
        requestPermission(i)
        // ActivityCompat.requestPermissions(activity, arrayOf(permissions[i]), i)
}

fun enableProtection(activity: AppCompatActivity,b : Boolean){
    activity.material_button.backgroundTintList = ColorStateList.valueOf(activity.getColor(if (b) R.color.green else R.color.red))
    activity.protectionStatus.text = if (b) "ON" else "OFF"
    if (b) {
        activity.startForegroundService(Intent(activity, AlertService::class.java))
    } else {
        activity.stopService(Intent(activity, AlertService::class.java))
    }
}
