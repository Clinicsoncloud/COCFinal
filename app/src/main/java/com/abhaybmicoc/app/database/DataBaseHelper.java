package com.abhaybmicoc.app.database;

import android.database.Cursor;
import android.util.Log;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.ErrorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataBaseHelper {
    public static SQLiteDatabase sqLiteDatabase;
    private SQLiteHelper sqLiteHelper;
    public Context context;

    public DataBaseHelper(Context context) {
        this.context = context;
        sqLiteHelper = new SQLiteHelper(context);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
    }

    public Long saveToLocalTable(String table, ContentValues contentValues, String mobile_No) {
        long count = 0;
        try {
            count = sqLiteDatabase.insert(table, null, contentValues);

            if (count != -1) {
                Log.v("DataHelp_Log", "Insert " + table + " Details Successfully");
            } else {
                Log.v("DataHelp_Log", "Insert " + table + " Details Fail");
            }

        } catch (Exception e) {
            ErrorUtils.logErrors(context, e, "DataBaseHelper", "saveToLocalTable", "" + e.getMessage());
        }
        return count;
    }

    public void updatePatientInfo(String table, ContentValues contentValues, String patient_id) {
        try {
            long count1 = sqLiteDatabase.update(table, contentValues, "patient_id ='" + patient_id + "' ", null);

            if (count1 != -1) {
                Log.v("DataHelp", "Update " + table + " Details Successfully");
            } else {
                Log.v("DataHelp", "Update " + table + " Details Fail");
            }

        } catch (Exception e) {
            ErrorUtils.logErrors(context, e, "DataBaseHelper", "", "" + e.getMessage());
        }
    }

    public String getLastInsertPatientID() {

        Cursor cursor = null;

        cursor = sqLiteDatabase.rawQuery(SQLiteQueries.QUERY_GET_LAST_INSERTED_PATIENT_ID, null);

        String id = "0";

        if (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i)));
            }
        }

        return id;
    }

    public JSONArray getOfflineData() {

        Cursor cursor = null;
        JSONArray jArray = new JSONArray();

        cursor = sqLiteDatabase.rawQuery(SQLiteQueries.QUERY_GET_OFFLINE_DATA, null);

        JSONObject json = null;

        if (cursor.getCount() != 0) {
            try {
                while (cursor.moveToNext()) {
                    json = new JSONObject();

                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                    }

                    jArray.put(json);
                }
                Log.e("GEtOfflineData_Log", ":" + jArray);
                return jArray;
            } catch (JSONException e) {
                ErrorUtils.logErrors(context, e, "DataBaseHelper", "getOfflineData", "" + e.getMessage());
            }
        }

        if (jArray.length() > 0)
            return jArray;

        return null;
    }


    public JSONArray getErrorLogsData() {

        Cursor cursor = null;
        JSONArray jArray = new JSONArray();
        Log.e("ErrorLogsData_Query", "Select * from " + Constant.TableNames.ERROR_LOGS + "");
        cursor = sqLiteDatabase.rawQuery("Select * from " + Constant.TableNames.ERROR_LOGS + "", null);

        JSONObject json = null;

        if (cursor.getCount() != 0) {

            try {
                while (cursor.moveToNext()) {
                    json = new JSONObject();

                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                    }
                    jArray.put(json);

                }
                return jArray;
            } catch (JSONException e) {
                ErrorUtils.logErrors(context, e, "DataBaseHelper", "getOfflineData", "" + e.getMessage());
            }
        }
        Log.e("ErrorLogsData_Array", ":" + jArray.length());

        if (jArray.length() > 0)
            return jArray;

        return null;
    }

    public void deleteTable_data(String tbl_name, String key, String ids) {

        sqLiteDatabase.execSQL("delete from " + tbl_name + " WHERE " + key + " IN (" + ids + ")");

    }

    public void deleteErrorLogs(String tbl_name, String key) {
        sqLiteDatabase.execSQL("delete from " + tbl_name);
    }

}