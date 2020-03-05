package com.abhaybmicoc.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.abhaybmicoc.app.database.DataBaseHelper;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.ErrorUtils;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.abhaybmicoc.app.utils.Constant.Fields.internetIntent;

public class OfflineSyncBroadcastReciever extends BroadcastReceiver {

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
                getErrorLogs();

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
            ErrorUtils.logErrors(mContext, e, "OfflineSyncBroadcastReciever", "getOfflineRecords", "" + e.getMessage());
        }
    }

    private void getErrorLogs() {
        try {
            JSONArray logsArray = dataBaseHelper.getErrorLogsData();
            if (logsArray != null && logsArray.length() > 0) {
                uploadErrorLogsRecord(logsArray);
            }
        } catch (Exception e) {
            ErrorUtils.logErrors(mContext, e, "OfflineSyncBroadcastReciever", "getOfflineRecords", "" + e.getMessage());
        }
    }

    private void uploadOfflineRecords(JSONArray dataArray) {

        try {
            JSONObject dataObject = new JSONObject();
            dataObject.put("data", dataArray);

            Log.e("Error_dataArray", ":" + dataArray);
            HttpService.accessWebServicesJSONNoDialog(
                    mContext, ApiUtils.SYNC_OFFLINE_DATA_URL, dataObject,
                    (response, error, status) -> handleAPIResponse(response, error, status));
        } catch (Exception e) {
            ErrorUtils.logErrors(mContext, e, "OfflineSyncBroadcastReciever", "uploadOfflineRecords", "" + e.getMessage());
        }
    }

    private void handleAPIResponse(String response, VolleyError error, String status) {

        try {
            updateLocalStatus(response);
        } catch (Exception e) {
            // TODO: Handle exception
            ErrorUtils.logErrors(mContext, e, "OfflineSyncBroadcastReciever", "handleAPIResponse", "" + e.getMessage());
        }
    }

    private void updateLocalStatus(String response) {
        try {

            Toast.makeText(mContext, "Data Sync successfully", Toast.LENGTH_SHORT).show();

            JSONObject jsonObject = new JSONObject(response);

            JSONArray resultArray = jsonObject.getJSONArray("result");

            String patientId = "";
            String parameterId = "";

            for (int i = 0; i < resultArray.length(); i++) {

                if (patientId.equals("")) {
                    patientId = resultArray.getJSONObject(i).getString("patient_id");
                } else {
                    patientId = patientId + "," + resultArray.getJSONObject(i).getString("patient_id");
                }


                if (parameterId.equals("")) {
                    parameterId = resultArray.getJSONObject(i).getString("parameter_id");
                } else {
                    parameterId = parameterId + "," + resultArray.getJSONObject(i).getString("parameter_id");
                }
            }

            SharedPreferences.Editor editor = sharedPreferencesOffline.edit();

            editor.putString(Constant.Fields.UPLOADED_RECORDS_COUNT, DateService.getCurrentDateTime(DateService.DATE_FORMAT));

            editor.commit();

            dataBaseHelper.deleteTable_data(Constant.TableNames.PATIENTS, Constant.Fields.PATIENT_ID, patientId);

            dataBaseHelper.deleteTable_data(Constant.TableNames.PARAMETERS, Constant.Fields.PARAMETER_ID, parameterId);
        } catch (Exception e) {
            ErrorUtils.logErrors(mContext, e, "OfflineSyncBroadcastReciever", "updateLocalStatus", "" + e.getMessage());
        }
    }


    private void uploadErrorLogsRecord(JSONArray dataArray) {

        try {
            JSONObject dataObject = new JSONObject();
            dataObject.put("data", dataArray);

            HttpService.accessWebServicesJSONNoDialog(
                    mContext, ApiUtils.SYNC_ERROR_SAVE, dataObject,
                    (response, error, status) -> handleErrorAPIResponse(response, error, status));
        } catch (Exception e) {
            ErrorUtils.logErrors(mContext, e, "OfflineSyncBroadcastReciever", "uploadOfflineRecords", "" + e.getMessage());
        }
    }

    private void handleErrorAPIResponse(String response, VolleyError error, String status) {

        try {
            Log.e("Error_response", ":" + response);
            dataBaseHelper.deleteErrorLogs(Constant.TableNames.ERROR_LOGS, Constant.Fields.ID);

        } catch (Exception e) {
            // TODO: Handle exception
            ErrorUtils.logErrors(mContext, e, "OfflineSyncBroadcastReciever", "handleAPIResponse", "" + e.getMessage());
        }
    }
}