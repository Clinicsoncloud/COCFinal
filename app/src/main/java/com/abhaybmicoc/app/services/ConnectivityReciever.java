package com.abhaybmicoc.app.services;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.abhaybmicoc.app.database.DataBaseHelper;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

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

                uploadOfflineRecords();
//                getOfflineRecords();

            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Log.e("receiver_Log_Receiver", ":False:");
            }
        }
    }

    private void uploadOfflineRecords() {
        try {
            Log.e("OfflineStored_Data_Volley", ":" + dataBaseHelper.getOfflineData());

            JSONArray dataArray = dataBaseHelper.getOfflineData();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", dataArray);

            Log.e("jsonObject_Data_Volley", ":" + dataArray);
            Log.e("Synch_URL", ":" + ApiUtils.SYNC_OFFLINE_DATA_URL);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiUtils.SYNC_OFFLINE_DATA_URL, jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("onResponse_Success", ":" + response);

                            updateLocalStatus(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("onErrorResponse", ":" + error);

                }
            }) {

                protected JSONObject getJsonObject() {
                    return jsonObject;
                }
            };

            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                    //Toast.makeText(context, "" + error, Toast.LENGTH_SHORT).show();
                    Log.e("onErrorResponse", "" + error);
//                responseListner.onError(error);
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            requestQueue.add(jsonObjectRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLocalStatus(JSONObject response) {


        try {
            JSONArray patientIdArray = response.getJSONArray("patient_ids");
            JSONArray parameterIdArray = response.getJSONArray("parameter_ids");
            Log.e("response_Ids_Log", ":" + patientIdArray + " : " + patientIdArray.getString(0));

            String patientId = "";
            String parameterId = "";


            for (int i = 0; i < patientIdArray.length(); i++) {


                if (patientId.equals("")) {
                    patientId = patientIdArray.getString(i);
                    parameterId = parameterIdArray.getString(i);
                } else {
                    patientId = patientId + "," + patientIdArray.getString(i);
                    parameterId = parameterId + "," + parameterIdArray.getString(i);
                }
            }

            Log.e("patientId_Log", ":" + patientId + "    :parameterId_Log:   " + parameterId);


            dataBaseHelper.deleteTable_data(Constant.TableNames.TBL_PATIENTS, Constant.Fields.PATIENT_ID, patientId);

            dataBaseHelper.deleteTable_data(Constant.TableNames.TBL_PARAMETERS, Constant.Fields.PARAMETER_ID, parameterId);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
