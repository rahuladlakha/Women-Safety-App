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
private val permissions = arrayOf(
    Manifest.permission.SEND_SMS, Manifest.permission.FOREGROUND_SERVICE, Manifest.permission.ACCESS_FINE_LOCATION ,
    Manifest.permission.ACCESS_COARSE_LOCATION ,
    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
 )
private val permissionDes = arrayOf(
    "SMS permission" to "The app requires SMS permission to enable \"sending SOS message to your selected emergency contacts\".\n\nPlease note that you may be charged by your network operator for sending SMS as per tariff plans in your area",
    "Foreground Service permission" to "This permission allows the app to run in background to be able to detect emergency gesture and send SOS signals when the app is not in use.",
    "Location permission" to "Akira collects location data to enable \"tracking device's location\" even when the app is closed or not in use. Your location will only be shared with your own provided emergency contacts upon detection of emergency gesture. We NEVER store your location.",
    "Location permission" to "Akira collects location data to enable \"tracking device's location\" even when the app is closed or not in use. Your location will only be shared with your own provided emergency contacts upon detection of emergency gesture. We NEVER store your location.",
    "Background Location permission" to "Akira requires this permission to access this device's location when the app is running in background or is not in use.\nYour location will only be shared with your own provided emergency contacts upon detection of emergency gesture.\nWE NEVER STORE YOUR LOCATION.\n\n Note: If tapping the below button doesn't open the permissions dialog, manually provide permission by going to app permission settings and selecting \"Allow all the time\" for location permisssion"
)

fun checkIfPermissionsGranted(activity: AppCompatActivity) : Boolean =
    permissions.all{ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED}

fun checkPermissions( activity: AppCompatActivity) {

    fun requestPermission( perIndex : Int){
        AlertDialog.Builder(activity)
            .setIcon(R.drawable.ic_info_outline)
            .setTitle("Please provide the ${permissionDes[perIndex].first}")
            .setMessage(permissionDes[perIndex].second)
            .setNegativeButton("Deny", null)
            .setPositiveButton("Accept & grant", object : DialogInterface.OnClickListener{
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
            .setTitle("Please enable GPS !")
            .setMessage("GPS must be enabled when Protection Mode is on to allow the app to access this device's location even when the app is closed or not in use. We NEVER store your location and only access your location when emergency gesture is detected. \nWe understand the sensitivity of these permissions and take best measures to protect your privacy.")
            .setPositiveButton("Enable GPS", object : DialogInterface.OnClickListener {
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

