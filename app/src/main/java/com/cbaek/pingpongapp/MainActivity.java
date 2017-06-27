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
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_CONSOLE_ACTION = "com.cbaek.pingpongapp.LOG_CONSOLE_ACTION";
    public static final String LOG_CONSOLE_MESSAGE_KEY = "com.cbaek.pingpongapp.LOG_CONSOLE_MESSAGE_KEY";

    public static void logConsoleMessage(final Context context, final String message) {
        final Intent intent = new Intent(LOG_CONSOLE_ACTION);
        intent.putExtra(LOG_CONSOLE_MESSAGE_KEY, message);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }

    private final class ConsoleMessageBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            final Bundle extras = intent.getExtras();
            final String message = extras.getString(LOG_CONSOLE_MESSAGE_KEY);
            appendConsoleMessage(message);
        }

    }

    private final ConsoleMessageBroadcastReceiver consoleMessageBroadcastReceiver = new ConsoleMessageBroadcastReceiver();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeStartServiceButton();
        initializeStopServiceButton();
        initializeConsoleMessageBroadcastReceiver();

        appendConsoleMessage("Initialization complete");
    }

    private void initializeStartServiceButton() {
        final Button button = (Button) findViewById(R.id.start_service_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                final int internetPermissionStatus = ContextCompat.checkSelfPermission(
                        MainActivity.this,
                        Manifest.permission.INTERNET);

                if (internetPermissionStatus != PackageManager.PERMISSION_GRANTED) {
                    appendConsoleMessage("Internet permission not granted");
                    return;
                }

                appendConsoleMessage("Sending intent to start PingPongService");
                startService(newPingPongServiceIntent().setAction(PingPongService.START_PING_PONG_SERVICE_ACTION));
            }

        });
    }

    private void initializeStopServiceButton() {
        final Button button = (Button) findViewById(R.id.stop_service_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                appendConsoleMessage("Sending intent to stop PingPongService");
                stopService(newPingPongServiceIntent());
            }

        });
    }

    private Intent newPingPongServiceIntent() {
        return new Intent(MainActivity.this, PingPongService.class);
    }

    private void initializeConsoleMessageBroadcastReceiver() {
        final IntentFilter intentFilter = new IntentFilter(LOG_CONSOLE_ACTION);
        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .registerReceiver(
                        consoleMessageBroadcastReceiver,
                        intentFilter);
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
                .unregisterReceiver(consoleMessageBroadcastReceiver);
    }

}
