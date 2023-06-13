package com.esatgozcu.testpushsdk

import com.google.firebase.messaging.FirebaseMessagingService

class TestMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
