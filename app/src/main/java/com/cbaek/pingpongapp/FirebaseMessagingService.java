package com.cbaek.pingpongapp;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by baekc on 6/22/17.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getSimpleName();

    /**
     *
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // start the service if it hasn't already been started
        MainActivity.logConsoleMessage(FirebaseMessagingService.this, String.format("FirebaseMessagingService received message from: %s", remoteMessage.getFrom()));
        MainActivity.logConsoleMessage(FirebaseMessagingService.this, String.format("FirebaseMessagingService received message data: %s", remoteMessage.getData()));
        startService(new Intent(this, PingPongService.class).setAction(PingPongService.START_PING_PONG_SERVICE_ACTION));

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//        Toast.makeText(this, "From: " + remoteMessage.getFrom(), Toast.LENGTH_LONG).show();

        // Check if message contains a data payload.
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//            Toast.makeText(this, "Message data payload: " + remoteMessage.getData(), Toast.LENGTH_LONG).show();
//        }

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//            Toast.makeText(this, "Message Notification Body: " + remoteMessage.getNotification().getBody(), Toast.LENGTH_LONG).show();
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

}
