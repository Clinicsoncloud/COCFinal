package com.abhaybmicoc.app.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingletone {

    private static VolleySingletone mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private VolleySingletone(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized VolleySingletone getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingletone(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
