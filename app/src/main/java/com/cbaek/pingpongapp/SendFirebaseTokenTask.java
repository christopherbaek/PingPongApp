package com.cbaek.pingpongapp;

import android.content.Context;
import android.os.AsyncTask;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SendFirebaseTokenTask extends AsyncTask<Void, Void, Void> {

    private final Context context;

    public SendFirebaseTokenTask(final Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(final Void... params) {
        sendFirebaseToken();
        return null;
    }

    public void sendFirebaseToken() {
        final String token = FirebaseInstanceId.getInstance().getToken();

        MainActivity.logConsoleMessage(context, String.format("SendFirebaseTokenTask sending token: %s", token));

        try {
            final URL url = new URL("https://www.cbaek.com/pingpongweb/firebasetoken");
            final HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) url.openConnection();
            httpsUrlConnection.setRequestMethod("POST");
            httpsUrlConnection.setDoOutput(true);

            final byte[] postParams = new StringBuilder("firebaseToken=").append(token).toString().getBytes();
            httpsUrlConnection. setFixedLengthStreamingMode(postParams.length);
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpsUrlConnection.getOutputStream());
            bufferedOutputStream.write(postParams);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();

            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsUrlConnection.getInputStream()));
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            bufferedReader.close();

            httpsUrlConnection.disconnect();
        } catch (final IOException e) {
            e.printStackTrace();
            MainActivity.logConsoleMessage(context, String.format("SendFirebaseTokenTask encountered exception sending token: %s", e.getMessage()));
        }
    }

    @Override
    protected void onPostExecute(final Void aVoid) {
        MainActivity.logConsoleMessage(context, "SendFirebaseTokenTask sent token successfully");
    }

}
