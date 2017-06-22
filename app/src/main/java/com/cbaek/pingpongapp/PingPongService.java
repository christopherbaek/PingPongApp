package com.cbaek.pingpongapp;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

public class PingPongService extends Service {

    static final String PING_PONG_SERVICE_STARTED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_STARTED";
    static final String PING_PONG_SERVICE_DESTROYED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_DESTROYED";

    @Override
    public void onCreate() {
        // TODO: start a ping thread
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final Intent pingPongServiceStartedIntent = new Intent();
        pingPongServiceStartedIntent.setAction(PING_PONG_SERVICE_STARTED_ACTION);

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .sendBroadcast(pingPongServiceStartedIntent);

        return START_STICKY;
    }

    /**
     * Return null since binding is not supported.
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        final Intent pingPonServiceDestroyedIntent = new Intent();
        pingPonServiceDestroyedIntent.setAction(PING_PONG_SERVICE_DESTROYED_ACTION);

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .sendBroadcast(pingPonServiceDestroyedIntent);
    }

}
