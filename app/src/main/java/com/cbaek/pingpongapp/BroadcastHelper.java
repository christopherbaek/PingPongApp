package com.cbaek.pingpongapp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastHelper {

    public static void sendLocalBroadcast(final Context context, final Intent intent) {
        LocalBroadcastManager
                .getInstance(context)
                .sendBroadcast(intent);
    }

}
