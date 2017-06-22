package com.cbaek.pingpongapp;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class PingPongService extends Service {

    static final String PING_PONG_SERVICE_STARTED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_STARTED";
    static final String PING_PONG_SERVICE_DESTROYED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_DESTROYED";
    static final String PING_PONG_SERVICE_PING_SENT_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PING_SENT";
    static final String PING_PONG_SERVICE_PONG_RECEIVED_ACTION = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PONG_RECEIVED";
    static final String PING_PONG_SERVICE_PONG_MESSAGE_KEY = "com.cbaek.pingpongapp.PING_PONG_SERVICE_PONG_MESSAGE";

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int PING_PONG_SERVICE_PING_INTERVAL_SECONDS = 3;
    private static final String PING_PONG_SERVER_HOST = "cbaek.com";
    private static final int PING_PONG_SERVER_PORT = 9999;
    private static final String PING_PONG_SERVER_MESSAGE = "ping";

    private final class PingPongServiceTask implements Runnable {

        private final String hostName;
        private final int port;

        private Socket socket;
        private OutputStreamWriter output;
        private InputStreamReader input;

        private final char[] buffer = new char[25];

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
            }
        }

        @Override
        public void run() {
            initialize();

            while (true) {
                try {
                    doWork();
                    Thread.sleep(PING_PONG_SERVICE_PING_INTERVAL_SECONDS * MILLISECONDS_PER_SECOND);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void doWork() {
            try {
                output.write(PING_PONG_SERVER_MESSAGE);
                output.flush();
                broadcastPingPongServicePingSentIntent();
            } catch (final IOException e) {
                e.printStackTrace();
            }


            try {
                final int charactersRead = input.read(buffer, 0, buffer.length);
                final String message = new String(buffer, 0, charactersRead);
                broadcastPingPongServicePongReceivedIntent(message);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void broadcastPingPongServicePingSentIntent() {
        final Intent pingPongServicePingSentIntent = new Intent();
        pingPongServicePingSentIntent.setAction(PING_PONG_SERVICE_PING_SENT_ACTION);

        sendLocalBroadcast(pingPongServicePingSentIntent);
    }

    private void broadcastPingPongServicePongReceivedIntent(final String message) {
        final Intent pingPongServicePongReceivedIntent = new Intent();
        pingPongServicePongReceivedIntent.setAction(PING_PONG_SERVICE_PONG_RECEIVED_ACTION);
        pingPongServicePongReceivedIntent.putExtra(PING_PONG_SERVICE_PONG_MESSAGE_KEY, message);

        sendLocalBroadcast(pingPongServicePongReceivedIntent);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        new Thread(new PingPongServiceTask(PING_PONG_SERVER_HOST, PING_PONG_SERVER_PORT)).start();
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
