package com.cbaek.pingpongapp;


import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class PingPongService extends Service {

    public static final String START_PING_PONG_SERVICE_ACTION = "com.cbaek.pingpongapp.START_PING_PONG_SERVICE_ACTION";
    public static final String PING_PONG_SERVICE_FIREBASE_REGISTRATION_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_FIREBASE_REGISTRATION_ACTION";
    public static final String FIREBASE_TOKEN_KEY = "com.cbaek.pingpongapp.FIREBASE_TOKEN_KEY";

    @Inject
    PingPongClient pingPongClient;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private PingPongServicePingTask pingPongServicePingTask;
    private ScheduledFuture<?> pingPongServicePingTaskFuture;

    private ScheduledFuture<?> pingPongServiceFirebaseRegistrationTaskFuture;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerPingPongAppComponent.builder().build().inject(this);
        this.pingPongServicePingTask = new PingPongServicePingTask(pingPongClient);

        MainActivity.logConsoleMessage(PingPongService.this, "PingPongService created");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        final String action = intent.getAction();

        if (action.equals(START_PING_PONG_SERVICE_ACTION)) {
            pingPongServicePingTaskFuture = scheduledExecutorService.scheduleAtFixedRate(
                    pingPongServicePingTask,
                    0,
                    PingPongServicePingTask.PING_PONG_SERVICE_PING_INTERVAL_SECONDS,
                    TimeUnit.SECONDS);

            MainActivity.logConsoleMessage(PingPongService.this, "PingPongService scheduled ping task");
        } else if (action.equals(PING_PONG_SERVICE_FIREBASE_REGISTRATION_ACTION)) {
            final Bundle extras = intent.getExtras();
            final String token = extras.getString(FIREBASE_TOKEN_KEY);
            pingPongServiceFirebaseRegistrationTaskFuture = scheduledExecutorService.schedule(
                    new PingPongServiceFirebaseRegistrationTask(pingPongClient, token),
                    0,
                    TimeUnit.SECONDS);

            MainActivity.logConsoleMessage(PingPongService.this, "PingPongService scheduled firebase registration task");
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        pingPongServicePingTaskFuture.cancel(true);

        if (pingPongServiceFirebaseRegistrationTaskFuture != null) {
            pingPongServiceFirebaseRegistrationTaskFuture.cancel(true);
        }

        MainActivity.logConsoleMessage(PingPongService.this, "PingPongService destroyed");
    }

    private final class PingPongServicePingTask implements Runnable {

        private static final int PING_PONG_SERVICE_PING_INTERVAL_SECONDS = 3;

        private final PingPongClient pingPongClient;

        private PingPongServicePingTask(final PingPongClient pingPongClient) {
            this.pingPongClient = pingPongClient;
        }

        @Override
        public void run() {
            sendPing();
            readPong();
        }

        private void sendPing() {
            try {
                pingPongClient.sendPingMessage();
                MainActivity.logConsoleMessage(PingPongService.this, "PingPongService sent ping");
            } catch (final IOException e) {
                e.printStackTrace();
                MainActivity.logConsoleMessage(PingPongService.this, String.format("PingPingService encountered exception sending ping: %s", e.getMessage()));
            }
        }

        private void readPong() {
            try {
                final String message = pingPongClient.readServerMessage();
                MainActivity.logConsoleMessage(PingPongService.this, String.format("PingPongService received pong: %s", message));
            } catch (final IOException e) {
                e.printStackTrace();
                MainActivity.logConsoleMessage(PingPongService.this, String.format("PingPingService encountered exception reading pong: %s", e.getMessage()));
            }
        }

    }

    private final class PingPongServiceFirebaseRegistrationTask implements Runnable {

        private final PingPongClient pingPongClient;
        private final String token;

        private PingPongServiceFirebaseRegistrationTask(final PingPongClient pingPongClient, final String token) {
            this.pingPongClient = pingPongClient;
            this.token = token;
        }

        @Override
        public void run() {
            if (token == null) {
                MainActivity.logConsoleMessage(PingPongService.this, String.format("PingPongService not sending null token"));
            }

            try {
                pingPongClient.sendRegistrationMessage(token);
                MainActivity.logConsoleMessage(PingPongService.this, String.format("PingPongService sent firebase token: %s", token));
            } catch (final IOException e) {
                e.printStackTrace();
                MainActivity.logConsoleMessage(PingPongService.this, String.format("PingPingService encountered exception sending firebase token: %s", e.getMessage()));
            }
        }

    }

}
