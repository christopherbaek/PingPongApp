package com.cbaek.pingpongapp;

import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        startActivity(new Intent(this, MainActivity.class));

        MainActivity.logConsoleMessage(this, String.format("FirebaseMessagingService received message from: %s", remoteMessage.getFrom()));
        MainActivity.logConsoleMessage(this, String.format("FirebaseMessagingService received message data: %s", remoteMessage.getData()));

        MainActivity.logConsoleMessage(this, "FirebaseMessagingService sending intent to MainActivity");
    }

}
