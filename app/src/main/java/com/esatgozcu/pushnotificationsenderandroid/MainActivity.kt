package com.esatgozcu.pushnotificationsenderandroid

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.esatgozcu.testpushsdk.AskPermissionCompleteListener
import com.esatgozcu.testpushsdk.CheckPermissionCompleteListener
import com.esatgozcu.testpushsdk.GetRegisterTokenCompleteListener
import com.esatgozcu.testpushsdk.TestPushSDK

var TAG = "LOG_AWESOME"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testSDK = TestPushSDK(applicationContext, this@MainActivity as AppCompatActivity)

        if (testSDK.checkGooglePlayServices()){
            Log.w(TAG, "Device have google play services")
        }
        else{
            Log.w(TAG, "Device doesn't have google play services!")
        }

        testSDK.getRegistrationToken(object: GetRegisterTokenCompleteListener{
            override fun onSuccess(token: String) {
                Log.d(TAG, "FCM registration token: $token")
            }
            override fun onFailure(error: String) {
                Log.w(TAG, "Fetching FCM registration token failed!$error")
            }
        })

        // TODO: This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= 33) {
            testSDK.checkNotificationPermission(object: CheckPermissionCompleteListener {
                override fun onGranted() {
                    Log.w(TAG, "Permission already granted, FCM SDK can post notifications.")
                }
                override fun onRationale() {
                    // TODO: display an educational UI explaining to the user the features that will be enabled
                    //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                    //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                    //       If the user selects "No thanks," allow the user to continue without notifications.
                    Log.w(TAG, "shouldShowRequestPermissionRationale")
                }
                override fun onPermissionAvailable() {
                    Log.w(TAG, "Ask Permission to user directly")
                    testSDK.askNotificationPermission(object: AskPermissionCompleteListener {
                        override fun onGranted() {
                            Log.w(TAG, "Permission granted, FCM SDK can post notifications.")
                        }
                        override fun onRejected() {
                            Log.w(TAG, "Inform user that that your app will not show notifications.")
                        }
                    })
                }
            })
        }
    }
}