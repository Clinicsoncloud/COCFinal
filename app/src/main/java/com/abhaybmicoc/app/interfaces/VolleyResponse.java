package com.abhaybmicoc.app.interfaces;


import android.os.Build;
import android.support.annotation.RequiresApi;

import com.android.volley.VolleyError;

public interface VolleyResponse {

    void onProcessFinish(String response, VolleyError error, String status);


//    void onProcessFinish(String response);

//    void onError(VolleyError error);
}
