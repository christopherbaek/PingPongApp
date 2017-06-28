package com.cbaek.pingpongapp;

public class FirebaseInstanceIdService extends com.google.firebase.iid.FirebaseInstanceIdService {

    @Override
    public void onCreate() {
        super.onCreate();

        MainActivity.logConsoleMessage(this, "FirebaseInstanceIdService created");

        new SendFirebaseTokenTask(this).execute();
    }

    @Override
    public void onTokenRefresh() {
        MainActivity.logConsoleMessage(this, "FirebaseInstanceIdService read refreshed token");
        new SendFirebaseTokenTask(this).sendFirebaseToken();
    }

}
