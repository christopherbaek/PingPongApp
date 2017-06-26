package com.cbaek.pingpongapp;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

import dagger.Component;
import dagger.Module;

public class PingPongService extends Service {

    public static final String PING_PONG_SERVICE_STARTED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_STARTED";
    public static final String PING_PONG_SERVICE_DESTROYED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_DESTROYED";
    public static final String PING_PONG_SERVICE_PING_SENT_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PING_SENT";
    public static final String PING_PONG_SERVICE_PONG_RECEIVED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PONG_RECEIVED";
    public static final String PING_PONG_SERVICE_PONG_MESSAGE_KEY = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PONG_MESSAGE";
    public static final String PING_PONG_SERVICE_EXCEPTION_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_EXCEPTION";
    public static final String PING_PONG_SERVICE_EXCEPTION_MESSAGE_KEY = "com.cbaek.pingpongapp.PING_PONG_SERVICE_EXCEPTION_MESSAGE";

    private final class PingPongServiceTask implements Runnable {

        private static final int MILLISECONDS_PER_SECOND = 1000;
        private static final int PING_PONG_SERVICE_PING_INTERVAL_SECONDS = 3;

        private final PingPongClient pingPongClient;

        private boolean running = true;

        private PingPongServiceTask(final PingPongClient pingPongClient) {
            this.pingPongClient = pingPongClient;
        }

        @Override
        public void run() {
            try {
                pingPongClient.initialize();
            } catch (final IOException e) {
                e.printStackTrace();
                broadcastPingPongServiceExceptionIntent(e.getMessage());
            }

            while (running) {
                try {
                    doWork();
                    Thread.sleep(PING_PONG_SERVICE_PING_INTERVAL_SECONDS * MILLISECONDS_PER_SECOND);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                    broadcastPingPongServiceExceptionIntent(e.getMessage());
                }
            }
        }

        private void doWork() {
            try {
                pingPongClient.sendPingMessage();
                broadcastPingPongServicePingSentIntent();

                final String message = pingPongClient.readServerMessage();
                broadcastPingPongServicePongReceivedIntent(message);
            } catch (final IOException e) {
                e.printStackTrace();
                broadcastPingPongServiceExceptionIntent(e.getMessage());
            }
        }

        private void setRunning(final boolean running) {
            this.running = running;
        }

    }

    @Inject
    PingPongClient pingPongClient;

    private PingPongServiceTask pingPongServiceTask;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerPingPongAppComponent.builder().build().inject(this);
        this.pingPongServiceTask = new PingPongServiceTask(pingPongClient);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        pingPongServiceTask.setRunning(true);
        new Thread(pingPongServiceTask).start();
        broadcastPingPongServiceStartedIntent();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        pingPongServiceTask.setRunning(false);
        broadcastPingPongServiceDestroyedIntent();
    }

    private void broadcastPingPongServiceStartedIntent() {
        BroadcastHelper.sendLocalBroadcast(
                getApplicationContext(),
                new Intent(PING_PONG_SERVICE_STARTED_ACTION));
    }

    private void broadcastPingPongServiceDestroyedIntent() {
        BroadcastHelper.sendLocalBroadcast(
                getApplicationContext(),
                new Intent(PING_PONG_SERVICE_DESTROYED_ACTION));
    }

    private void broadcastPingPongServicePingSentIntent() {
        BroadcastHelper.sendLocalBroadcast(
                getApplicationContext(),
                new Intent(PING_PONG_SERVICE_PING_SENT_ACTION));
    }

    private void broadcastPingPongServicePongReceivedIntent(final String message) {
        final Intent pingPongServicePongReceivedIntent = new Intent(PING_PONG_SERVICE_PONG_RECEIVED_ACTION);
        pingPongServicePongReceivedIntent.putExtra(PING_PONG_SERVICE_PONG_MESSAGE_KEY, message);

        BroadcastHelper.sendLocalBroadcast(
                getApplicationContext(),
                pingPongServicePongReceivedIntent);
    }

    private void broadcastPingPongServiceExceptionIntent(final String message) {
        final Intent pingPongServiceExceptionIntent = new Intent(PING_PONG_SERVICE_EXCEPTION_ACTION);
        pingPongServiceExceptionIntent.putExtra(PING_PONG_SERVICE_EXCEPTION_MESSAGE_KEY, message);

        BroadcastHelper.sendLocalBroadcast(
                getApplicationContext(),
                pingPongServiceExceptionIntent);
    }

}
