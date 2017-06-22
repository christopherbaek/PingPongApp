package com.cbaek.pingpongapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    /**
     * On initial startup of your app, the FCM SDK generates a registration
     * token for the client app instance. If you want to target single
     * devices, or create device groups, you'll need to access this token.
     *
     * You can access the token's value by creating a new class which
     * extends FirebaseInstanceIdService . In that class, call getToken
     * within onTokenRefresh, and log the value as shown:
     *
     * The onTokenRefresh callback fires whenever a new token is generated,
     * so calling getToken in its context ensures that you are accessing a
     * current, available registration token. FirebaseInstanceID.getToken()
     * returns null if the token has not yet been generated.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        // sendRegistrationToServer(refreshedToken);
    }

}
