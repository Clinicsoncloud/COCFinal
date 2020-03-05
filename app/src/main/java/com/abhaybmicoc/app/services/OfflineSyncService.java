package com.abhaybmicoc.app.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;

import static com.abhaybmicoc.app.utils.Constant.Fields.internetIntent;

public class OfflineSyncService extends Service {

    OfflineSyncBroadcastReciever connectivityReciever;
    private static Timer timer = new Timer();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        timer.scheduleAtFixedRate(new sendLocationDataTask(), 1000, 1000 * 5);
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    /**
     * Called when the service is being created.
     */

    @Override
    public void onCreate() {
        connectivityReciever = new OfflineSyncBroadcastReciever();

        IntentFilter filter = new IntentFilter();
        filter.addAction(internetIntent);
        registerReceiver(connectivityReciever, filter);
    }


    /**
     * Called when The service is no longer used and is being destroyed
     */
    @Override
    public void onDestroy() {
        Log.e("onDestroy", "service");
		/*if(updateLocationReceiver!=null)
		this.unregisterReceiver(updateLocationReceiver);*/
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}