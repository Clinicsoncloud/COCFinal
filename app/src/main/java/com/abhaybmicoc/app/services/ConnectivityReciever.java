package com.abhaybmicoc.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.abhaybmicoc.app.database.DataBaseHelper;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.abhaybmicoc.app.utils.Constant.Fields.internetIntent;

public class ConnectivityReciever extends BroadcastReceiver {

    DataBaseHelper dataBaseHelper;
    Context mContext;
    SharedPreferences sharedPreferencesOffline;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        dataBaseHelper = new DataBaseHelper(mContext);
        sharedPreferencesOffline = mContext.getSharedPreferences(ApiUtils.PREFERENCE_OFFLINE, MODE_PRIVATE);

        String action = intent.getAction();
        if (action.equalsIgnoreCase(internetIntent)) {

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            }
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                Log.e("receiver_Log_Receiver", ":True:" + networkInfo.isConnectedOrConnecting());
                getOfflineRecords();
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Log.e("receiver_Log_Receiver", ":FAlse:");
            }
        }
    }

    private void getOfflineRecords() {

        try {
            JSONArray dataArray = dataBaseHelper.getOfflineData();
            if (dataArray != null && dataArray.length() > 0) {
                uploadOfflineRecords(dataArray);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadOfflineRecords(JSONArray dataArray) {

        try {
            JSONObject dataObject = new JSONObject();
            dataObject.put("data", dataArray);

            HttpService.accessWebServicesJSONNoDialog(
                    mContext, ApiUtils.SYNC_OFFLINE_DATA_URL, dataObject,
                    (response, error, status) -> handleAPIResponse(response, error, status));
        } catch (Exception e) {
        }
    }

    private void handleAPIResponse(String response, VolleyError error, String status) {

        try {
            updateLocalStatus(response);
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    private void updateLocalStatus(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray patientIdArray = jsonObject.getJSONArray("patient_ids");
            JSONArray parameterIdArray = jsonObject.getJSONArray("parameter_ids");

            String patientId = "";
            String parameterId = "";

            for (int i = 0; i < patientIdArray.length(); i++) {
                if (patientId.equals("")) {
                    patientId = patientIdArray.getString(i);
                } else {
                    patientId = patientId + "," + patientIdArray.getString(i);
                }
            }

            for (int j = 0; j < parameterIdArray.length(); j++) {
                if (parameterId.equals("")) {
                    parameterId = parameterIdArray.getString(j);
                } else {
                    parameterId = parameterId + "," + parameterIdArray.getString(j);
                }
            }

            SharedPreferences.Editor editor = sharedPreferencesOffline.edit();

            editor.putString(Constant.Fields.UPLOADED_RECORDS_COUNT, DateService.getCurrentDateTime(DateService.DATE_FORMAT));

            editor.commit();

            dataBaseHelper.deleteTable_data(Constant.TableNames.PATIENTS, Constant.Fields.PATIENT_ID, patientId);

            dataBaseHelper.deleteTable_data(Constant.TableNames.PARAMETERS, Constant.Fields.PARAMETER_ID, parameterId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
