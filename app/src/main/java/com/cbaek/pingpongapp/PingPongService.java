package com.cbaek.pingpongapp;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

public class PingPongService extends Service {

    static final String PING_PONG_SERVICE_STARTED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_STARTED";
    static final String PING_PONG_SERVICE_DESTROYED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_DESTROYED";
    static final String PING_PONG_SERVICE_PING_SENT_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PING_SENT";
    static final String PING_PONG_SERVICE_PONG_RECEIVED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PONG_RECEIVED";

    @Override
    public void onCreate() {
        // TODO: start a ping thread
    }

    private void broadcastPingPongServicePingSentIntent() {
        final Intent pingPongServicePingSentIntent = new Intent();
        pingPongServicePingSentIntent.setAction(PING_PONG_SERVICE_PING_SENT_ACTION);

        sendLocalBroadcast(pingPongServicePingSentIntent);
    }

    private void broadcastPingPongServicePongReceivedIntent() {
        final Intent pingPongServicePongReceivedIntent = new Intent();
        pingPongServicePongReceivedIntent.setAction(PING_PONG_SERVICE_PONG_RECEIVED_ACTION);

        sendLocalBroadcast(pingPongServicePongReceivedIntent);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        broadcastPingPongServiceStartedIntent();

        return START_STICKY;
    }

    private void broadcastPingPongServiceStartedIntent() {
        final Intent pingPongServiceStartedIntent = new Intent();
        pingPongServiceStartedIntent.setAction(PING_PONG_SERVICE_STARTED_ACTION);

        sendLocalBroadcast(pingPongServiceStartedIntent);
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
        broadcastPingPongServiceDestroyedIntent();
    }

    private void broadcastPingPongServiceDestroyedIntent() {
        final Intent pingPonServiceDestroyedIntent = new Intent();
        pingPonServiceDestroyedIntent.setAction(PING_PONG_SERVICE_DESTROYED_ACTION);

        sendLocalBroadcast(pingPonServiceDestroyedIntent);
    }

    private void sendLocalBroadcast(final Intent intent) {
        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

}
