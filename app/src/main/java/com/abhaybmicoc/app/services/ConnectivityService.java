package com.abhaybmicoc.app.services;

import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Timer;

import static com.abhaybmicoc.app.utils.Constant.Fields.internetIntent;

public class ConnectivityService extends Service {

    ConnectivityReciever connectivityReciever;
    private static Timer timer = new Timer();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * Called when the service is being created.
     */

    @Override
    public void onCreate() {
        connectivityReciever = new ConnectivityReciever();

        IntentFilter filter = new IntentFilter();
        filter.addAction(internetIntent);
        registerReceiver(connectivityReciever, filter);
    }


    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() { }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}