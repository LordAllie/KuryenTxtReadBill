package com.xesi.xenuser.kuryentxtreadbill.network;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class Kuryentxt extends MultiDexApplication {

    private static Kuryentxt mInstance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized Kuryentxt getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(NetworkReceiver.ConnectivityReceiverListener listener) {
        NetworkReceiver.connectivityReceiverListener = listener;
    }
}