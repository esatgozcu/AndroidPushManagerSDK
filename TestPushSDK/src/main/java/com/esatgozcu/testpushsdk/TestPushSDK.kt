package com.esatgozcu.testpushsdk;

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging

class TestPushSDK(private val context: Context, private val activity: AppCompatActivity) {
    fun getRegistrationToken(listener: GetRegisterTokenCompleteListener){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful){
                listener.onSuccess(token = task.result)
            }
            else{
                listener.onFailure(error = task.exception.toString())
            }
        }
    }
    fun checkGooglePlayServices(): Boolean {
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        return status == ConnectionResult.SUCCESS
    }
    @RequiresApi(33)
    fun checkNotificationPermission(listener: CheckPermissionCompleteListener){
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
                listener.onGranted()
            } else if (activity.shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
                listener.onRationale()
            } else {
                // Directly ask for the permission
                listener.onPermissionAvailable()
            }
        }
        else{
            listener.onGranted()
        }
    }
    @RequiresApi(33)
    fun askNotificationPermission(listener: AskPermissionCompleteListener){
        // Declare the launcher at the top of your Activity/Fragment:
        val requestPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            if (isGranted) {
                // FCM SDK (and your app) can post notifications.
                listener.onGranted()
            } else {
                // TODO: Inform user that that your app will not show notifications.
                listener.onRejected()
            }
        }
        requestPermissionLauncher.launch(POST_NOTIFICATIONS)
    }
}
interface GetRegisterTokenCompleteListener {
    fun onSuccess(token: String)
    fun onFailure(error: String)
}
interface CheckPermissionCompleteListener {
    fun onGranted()
    fun onRationale()
    fun onPermissionAvailable()
}
interface AskPermissionCompleteListener {
    fun onGranted()
    fun onRejected()
}
