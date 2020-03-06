package com.abhaybmicoc.app.interfaces;


import androidx.annotation.RequiresApi;

import com.android.volley.VolleyError;

public interface VolleyResponse {

    void onProcessFinish(String response, VolleyError error, String status);


//    void onProcessFinish(String response);

//    void onError(VolleyError error);
}
