package com.cbaek.pingpongapp;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class PingPongService extends Service {

    private final class PingPongServiceTask implements Runnable {

        private final String hostName;
        private final int port;

        private final char[] buffer = new char[25];

        private Socket socket;
        private OutputStreamWriter output;
        private InputStreamReader input;
        private boolean running = true;

        private PingPongServiceTask(final String hostName, final int port) {
            this.hostName = hostName;
            this.port = port;
        }

        public void initialize() {
            try {
                socket = new Socket(hostName, port);
                output = new OutputStreamWriter(socket.getOutputStream());
                input = new InputStreamReader(socket.getInputStream());
            } catch (final IOException e) {
                e.printStackTrace();
                broadcastPingPongServiceExceptionIntent(e.getMessage());
            }
        }

        @Override
        public void run() {
            initialize();

            while (running) {
                try {
                    doWork();
                    Thread.sleep(PING_PONG_SERVICE_PING_INTERVAL_SECONDS * MILLISECONDS_PER_SECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    broadcastPingPongServiceExceptionIntent(e.getMessage());
                }
            }
        }

        private void doWork() {
            try {
                output.write(PING_PONG_SERVER_MESSAGE);
                output.flush();
                broadcastPingPongServicePingSentIntent();

                final int charactersRead = input.read(buffer, 0, buffer.length);
                final String message = new String(buffer, 0, charactersRead);
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

    static final String PING_PONG_SERVICE_STARTED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_STARTED";
    static final String PING_PONG_SERVICE_DESTROYED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_DESTROYED";
    static final String PING_PONG_SERVICE_PING_SENT_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PING_SENT";
    static final String PING_PONG_SERVICE_PONG_RECEIVED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PONG_RECEIVED";
    static final String PING_PONG_SERVICE_PONG_MESSAGE_KEY = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PONG_MESSAGE";
    static final String PING_PONG_SERVICE_EXCEPTION_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_EXCEPTION";
    static final String PING_PONG_SERVICE_EXCEPTION_MESSAGE_KEY = "com.cbaek.pingpongapp.PING_PONG_SERVICE_EXCEPTION_MESSAGE";

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int PING_PONG_SERVICE_PING_INTERVAL_SECONDS = 3;
    private static final String PING_PONG_SERVER_HOST = "cbaek.com";
    private static final int PING_PONG_SERVER_PORT = 9999;
    private static final String PING_PONG_SERVER_MESSAGE = "ping";

    private final PingPongServiceTask pingPongServiceTask = new PingPongServiceTask(PING_PONG_SERVER_HOST, PING_PONG_SERVER_PORT);

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        pingPongServiceTask.setRunning(true);
        new Thread(pingPongServiceTask).start();
        broadcastPingPongServiceStartedIntent();

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
        pingPongServiceTask.setRunning(false);
        broadcastPingPongServiceDestroyedIntent();
    }

    private void broadcastPingPongServiceStartedIntent() {
        sendLocalBroadcast(new Intent(PING_PONG_SERVICE_STARTED_ACTION));
    }

    private void broadcastPingPongServiceDestroyedIntent() {
        sendLocalBroadcast(new Intent(PING_PONG_SERVICE_DESTROYED_ACTION));
    }

    private void broadcastPingPongServicePingSentIntent() {
        sendLocalBroadcast(new Intent(PING_PONG_SERVICE_PING_SENT_ACTION));
    }

    private void broadcastPingPongServicePongReceivedIntent(final String message) {
        final Intent pingPongServicePongReceivedIntent = new Intent(PING_PONG_SERVICE_PONG_RECEIVED_ACTION);
        pingPongServicePongReceivedIntent.putExtra(PING_PONG_SERVICE_PONG_MESSAGE_KEY, message);

        sendLocalBroadcast(pingPongServicePongReceivedIntent);
    }

    private void broadcastPingPongServiceExceptionIntent(final String message) {
        final Intent pingPongServiceExceptionIntent = new Intent(PING_PONG_SERVICE_EXCEPTION_ACTION);
        pingPongServiceExceptionIntent.putExtra(PING_PONG_SERVICE_EXCEPTION_MESSAGE_KEY, message);

        sendLocalBroadcast(pingPongServiceExceptionIntent);
    }

    private void sendLocalBroadcast(final Intent intent) {
        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

}
