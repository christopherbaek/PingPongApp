package com.cbaek.pingpongapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private final class PingPongServiceStartedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            appendConsoleMessage("PingPongService started");
        }

    }

    private final class PingPongServiceDestroyedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            appendConsoleMessage("PingPongService destroyed");
        }

    }

    private final class PingPongServicePingSentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            appendConsoleMessage("PingPongService sent ping");
        }

    }

    private final class PingPongServicePongReceivedBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final Bundle extras = intent.getExtras();
            final String message = extras.getString(PingPongService.PING_PONG_SERVICE_PONG_MESSAGE_KEY);
            appendConsoleMessage(String.format("PingPongService received pong: %s", message));
        }

    }

    private final class PingPongServiceExceptionBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final Bundle extras = intent.getExtras();
            final String message = extras.getString(PingPongService.PING_PONG_SERVICE_EXCEPTION_MESSAGE_KEY);
            appendConsoleMessage(String.format("PingPongService exception: %s", message));
        }

    }

    private final class MyFirebaseInstanceIDServiceTokenSent extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final Bundle extras = intent.getExtras();
            final String token = extras.getString(MyFirebaseInstanceIDService.MY_FIREBASE_INSTANCE_ID_SERVICE_TOKEN_KEY);
            appendConsoleMessage(String.format("Registration token sent: %s", token));
        }

    }

    private final PingPongServiceStartedBroadcastReceiver pingPongServiceStartedBroadcastReceiver = new PingPongServiceStartedBroadcastReceiver();
    private final PingPongServiceDestroyedBroadcastReceiver pingPongServiceDestroyedBroadcastReceiver = new PingPongServiceDestroyedBroadcastReceiver();
    private final PingPongServicePingSentBroadcastReceiver pingPongServicePingSentBroadcastReceiver = new PingPongServicePingSentBroadcastReceiver();
    private final PingPongServicePongReceivedBroadcastReceiver pingPongServicePongReceivedBroadcastReceiver = new PingPongServicePongReceivedBroadcastReceiver();
    private final PingPongServiceExceptionBroadcastReceiver pingPongServiceExceptionBroadcastReceiver = new PingPongServiceExceptionBroadcastReceiver();
    private final MyFirebaseInstanceIDServiceTokenSent myFirebaseInstanceIDServiceTokenSentBroadcastReceiver = new MyFirebaseInstanceIDServiceTokenSent();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeToggleButton();
        initializePingPongServiceStartedBroadcastReceiver();
        initializePingPongServiceDestroyedBroadcastReceiver();
        initializePingPongServicePingSentBroadcastReceiver();
        initializePingPongServicePongReceivedBroadcastReceiver();
        initializePingPongServiceExceptionBroadcastReceiver();
        initializeMyFirebaseInstanceIDServiceTokenSentBroadcastReceiver();

        appendConsoleMessage("Initialization complete");
    }

    private void initializeToggleButton() {
        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggle_button);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                final Intent pingPongServiceIntent = new Intent(MainActivity.this, PingPongService.class);

                if (isChecked) {
                    final int internetPermissionStatus = ContextCompat.checkSelfPermission(
                            MainActivity.this,
                            Manifest.permission.INTERNET);

                    if (internetPermissionStatus != PackageManager.PERMISSION_GRANTED) {
                        appendConsoleMessage("Internet permission not granted. Behavior unknown.");
                    } else {
                        appendConsoleMessage("Sending start PingPongService intent");
                        startService(pingPongServiceIntent);
                    }
                } else {
                    appendConsoleMessage("Sending setRunning PingPongService intent");
                    stopService(pingPongServiceIntent);
                }
            }

        });
    }

    private void initializePingPongServiceStartedBroadcastReceiver() {
        final IntentFilter pingPongServiceStartedIntentFilter =
                new IntentFilter(PingPongService.PING_PONG_SERVICE_STARTED_ACTION);

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .registerReceiver(
                        pingPongServiceStartedBroadcastReceiver,
                        pingPongServiceStartedIntentFilter);
    }

    private void initializePingPongServiceDestroyedBroadcastReceiver() {
        final IntentFilter pingPongServiceDestroyedIntentFilter =
                new IntentFilter(PingPongService.PING_PONG_SERVICE_DESTROYED_ACTION);

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .registerReceiver(
                        pingPongServiceDestroyedBroadcastReceiver,
                        pingPongServiceDestroyedIntentFilter);
    }

    private void initializePingPongServicePingSentBroadcastReceiver() {
        final IntentFilter pingPongServicePingSentIntentFilter =
                new IntentFilter(PingPongService.PING_PONG_SERVICE_PING_SENT_ACTION);

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .registerReceiver(
                        pingPongServicePingSentBroadcastReceiver,
                        pingPongServicePingSentIntentFilter);
    }

    private void initializePingPongServicePongReceivedBroadcastReceiver() {
        final IntentFilter pingPongServicePongReceivedIntentFilter =
                new IntentFilter(PingPongService.PING_PONG_SERVICE_PONG_RECEIVED_ACTION);

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .registerReceiver(
                        pingPongServicePongReceivedBroadcastReceiver,
                        pingPongServicePongReceivedIntentFilter);
    }

    private void initializePingPongServiceExceptionBroadcastReceiver() {
        final IntentFilter pingPongServiceExceptionIntentFilter =
                new IntentFilter(PingPongService.PING_PONG_SERVICE_EXCEPTION_ACTION);

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .registerReceiver(
                        pingPongServiceExceptionBroadcastReceiver,
                        pingPongServiceExceptionIntentFilter);
    }

    private void initializeMyFirebaseInstanceIDServiceTokenSentBroadcastReceiver() {
        final IntentFilter myFirebaseInstanceIdServiceTokenSentBroadcastReceiver =
                new IntentFilter(MyFirebaseInstanceIDService.MY_FIREBASE_INSTANCE_ID_SERVICE_TOKEN_SENT_ACTION);

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .registerReceiver(
                        myFirebaseInstanceIDServiceTokenSentBroadcastReceiver,
                        myFirebaseInstanceIdServiceTokenSentBroadcastReceiver);
    }

    private void appendConsoleMessage(final String message) {
        final String consoleMessage = new StringBuilder()
                .append(System.currentTimeMillis())
                .append(": ")
                .append(message)
                .append("\n")
                .toString();

        final TextView textView = (TextView) findViewById(R.id.console_contents);
        textView.append(consoleMessage);

        scrollConsoleViewToBottom();
    }

    private void scrollConsoleViewToBottom() {
        final ScrollView scrollView = (ScrollView) findViewById(R.id.console_view);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .unregisterReceiver(pingPongServiceStartedBroadcastReceiver);

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .unregisterReceiver(pingPongServiceDestroyedBroadcastReceiver);
    }

}
