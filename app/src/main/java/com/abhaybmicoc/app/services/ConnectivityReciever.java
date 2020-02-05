package com.abhaybmicoc.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.abhaybmicoc.app.database.DataBaseHelper;

import static com.abhaybmicoc.app.utils.Constant.Fields.internetIntent;


public class ConnectivityReciever extends BroadcastReceiver {

    DataBaseHelper dataBaseHelper;
    Context mContext;


    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        dataBaseHelper = new DataBaseHelper(mContext);

        String action = intent.getAction();
        if (action.equalsIgnoreCase(internetIntent)) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                Log.e("receiver_Log_Receiver", ":True:" + networkInfo.isConnectedOrConnecting());
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Log.e("receiver_Log_Receiver", ":FAlse:");
            }
        }
    }
}
