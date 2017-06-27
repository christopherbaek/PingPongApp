package com.cbaek.pingpongapp;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    private static final String TAG = FirebaseInstanceIdService.class.getSimpleName();

    @Inject
    PingPongClient pingPongClient;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerPingPongAppComponent.builder().build().inject(this);

        final String token = FirebaseInstanceId.getInstance().getToken();
        startService(newPingPongServiceIntent(token));

        MainActivity.logConsoleMessage(FirebaseInstanceIdService.this, "FirebaseInstanceIdService created");
    }

    /**
     * On initial startup of your app, the FCM SDK generates a registration
     * token for the client app instance. If you want to target single
     * devices, or create device groups, you'll need to access this token.
     * <p>
     * You can access the token's value by creating a new class which
     * extends FirebaseInstanceIdService . In that class, call getToken
     * within onTokenRefresh, and log the value as shown:
     * <p>
     * The onTokenRefresh callback fires whenever a new token is generated,
     * so calling getToken in its context ensures that you are accessing a
     * current, available registration token. FirebaseInstanceID.getToken()
     * returns null if the token has not yet been generated.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        MainActivity.logConsoleMessage(FirebaseInstanceIdService.this, String.format("FirebaseInstanceIdService refreshed token: %s", refreshedToken));

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        startService(newPingPongServiceIntent(refreshedToken));
    }

    private Intent newPingPongServiceIntent(final String token) {
        final Intent intent = new Intent(FirebaseInstanceIdService.this, PingPongService.class);
        intent.setAction(PingPongService.PING_PONG_SERVICE_FIREBASE_REGISTRATION_ACTION);
        intent.putExtra(PingPongService.FIREBASE_TOKEN_KEY, token);
        return intent;
    }

}
