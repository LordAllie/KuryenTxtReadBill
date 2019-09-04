package com.xesi.xenuser.kuryentxtreadbill.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xesi.xenuser.kuryentxtreadbill.Homepage;

/**
 * Created by Daryll Sabate on 4/10/2018.
 */

public class BootService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            Intent serviceIntent = new Intent();
            serviceIntent.setClass(context, Homepage.class);
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(serviceIntent);
            ((Activity) context).finish();
        } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            Intent serviceIntent = new Intent();
            serviceIntent.setClass(context, Homepage.class);
            serviceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(serviceIntent);
            ((Activity) context).finish();
        }
    }
}