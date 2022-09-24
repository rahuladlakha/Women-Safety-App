package com.rahul.womensafetyapp
//this file contains a few necessary functions and classes used often in the app

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.LocationManager
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*


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
    R.string.location_permission to R.string.location_permission_des,
    R.string.location_permission to R.string.location_permission_des,
    R.string.bg_location_permission to R.string.bg_location_permisssion_des,
    R.string.sms_permission to R.string.sms_permission_des,
    R.string.fg_permission to R.string.fg_permission_des
//    "Location permission" to "This permission is required to access Locaton service on your device.",
//    "Location permission" to "This permission is required to access Locaton service on your device.",
//    "Background Location permission" to "Allows the app to access this device's location in background. Necessary for the app to function.\n\n Note: If tapping the below button doesn't open the permissions dialog, manually provide permission by going to app permission settings and selecting \"Allow all the time\" for location permisssion",
//    "SMS permission" to "SMS permission is required to send SMS to your selected emergency contacts.",
//    "Foreground Service permission" to "Allows the app to run in background."
)

fun checkIfPermissionsGranted(activity: AppCompatActivity) : Boolean =
    permissions.all{ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED}

fun checkPermissions( activity: AppCompatActivity) {

    fun requestPermission( perIndex : Int){
        AlertDialog.Builder(activity)
            .setIcon(R.drawable.ic_info_outline)
            .setTitle("${activity.getString(R.string.please_provide_the)}${activity.getString(permissionDes[perIndex].first)}")
            .setMessage(activity.getString(permissionDes[perIndex].second))
            .setPositiveButton(activity.getString(R.string.proceed_to_grant), object : DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    if (permissions[perIndex] == Manifest.permission.ACCESS_BACKGROUND_LOCATION) {
                        Toast.makeText(activity, activity.getString(R.string.sel_allow_all_the_time), Toast.LENGTH_LONG).show()
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
    activity.protectionStatus.text = if (b) activity.getString(R.string.on) else activity.getString(R.string.off)
    if (b) {
        activity.startForegroundService(Intent(activity, AlertService::class.java))
        enableGPS(activity)
    } else {
        activity.stopService(Intent(activity, AlertService::class.java))
    }
}

fun enableGPS(activity: AppCompatActivity) {
    val mLocationManager: LocationManager =
        getSystemService(activity, LocationManager::class.java) as LocationManager
    if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        //If GPS is disabled, enable it
        AlertDialog.Builder(activity)
            .setIcon(R.drawable.ic_info_outline)
            .setTitle(activity.getString(R.string.enable_gps_dialog_title))
            .setMessage(activity.getString(R.string.enable_gps_dialog_des))
            .setPositiveButton(activity.getString(R.string.enable_gps_dialog_button), object : DialogInterface.OnClickListener {
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    //if location is not enabled, enable it
                    val locationRequest = LocationRequest.create().apply {
                        interval = 10000
                        fastestInterval = 5000
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    }

                    val builder = LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest)

                    val client: SettingsClient = LocationServices.getSettingsClient(activity)
                    val task: Task<LocationSettingsResponse> =
                        client.checkLocationSettings(builder.build())

                    task.addOnSuccessListener { locationSettingsResponse ->
                        // All location settings are satisfied. The client can initialize
                        // location requests here.
                        // ...
                        val state = locationSettingsResponse.locationSettingsStates

                        val label =
                            "GPS >> (Present: ${state?.isGpsPresent}  | Usable: ${state?.isGpsUsable} ) \n\n" +
                                    "Network >> ( Present: ${state?.isNetworkLocationPresent} | Usable: ${state?.isNetworkLocationUsable} ) \n\n" +
                                    "Location >> ( Present: ${state?.isLocationPresent} | Usable: ${state?.isLocationUsable} )"
                    }

                    task.addOnFailureListener { exception ->
                        if (exception is ResolvableApiException) {
                            // Location settings are not satisfied, but this can be fixed
                            // by showing the user a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                exception.startResolutionForResult(
                                    activity,
                                    100
                                )
                            } catch (sendEx: IntentSender.SendIntentException) {
                                // Ignore the error.
                            }
                        }

                    }
                }
            }).show()
    }
}
fun openLink(activity :AppCompatActivity,uri : Uri) {
    //This fun will be used to open privacy policy and terms and conditions
    val intent = Intent(Intent.ACTION_VIEW)
    intent.setData(uri)
    activity.startActivity(intent)
}

