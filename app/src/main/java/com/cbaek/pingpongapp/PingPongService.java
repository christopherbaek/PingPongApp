package com.cbaek.pingpongapp;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class PingPongService extends Service {

    public static Intent newPingPongServiceIntent(final Context context) {
        return new Intent(context, PingPongService.class);
    }

    @Inject
    PingPongClient pingPongClient;

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private PingPongServicePingTask pingPongServicePingTask;
    private ScheduledFuture<?> pingPongServicePingTaskFuture;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerPingPongAppComponent.builder().build().inject(this);
        this.pingPongServicePingTask = new PingPongServicePingTask(pingPongClient);

        MainActivity.logConsoleMessage(this, "PingPongService created");
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        if (pingPongServicePingTaskFuture != null) {
            MainActivity.logConsoleMessage(this, "PingPongService already started");
        } else {
            pingPongServicePingTaskFuture = scheduledExecutorService.scheduleAtFixedRate(
                    pingPongServicePingTask,
                    0,
                    PingPongServicePingTask.PING_PONG_SERVICE_PING_INTERVAL_SECONDS,
                    TimeUnit.SECONDS);

            MainActivity.logConsoleMessage(this, "PingPongService started");
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

        MainActivity.logConsoleMessage(this, "PingPongService destroyed");
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

}
