package com.abhaybmicoc.app.database;

import android.database.Cursor;
import android.util.Log;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.abhaybmicoc.app.utils.Constant;

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

    public void saveToLocalTable(String table, ContentValues contentValues, String mobile_No) {
        try {
            if (table.equals(Constant.TableNames.TBL_PATIENTS)) {
                Cursor c;
                c = sqLiteDatabase.rawQuery("Select * from " + table + " where  mobile ='" + mobile_No + "' ", null);

                int count = c.getColumnCount();
                if (c != null) {
                    if (c.moveToFirst()) {
                        c.close();
                        return;
                    } else {
                        c.close();
                    }
                }
            }
            long count1 = sqLiteDatabase.insert(table, null, contentValues);
            if (count1 != -1) {
                Log.v("DataHelp_Log", "Insert " + table + " Details Successfully");
            } else {
                Log.v("DataHelp_Log", "Insert " + table + " Details Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updatePatientInfo(String table, ContentValues contentValues, String mobile_no) {
        try {
            long count1 = sqLiteDatabase.update(table, contentValues, "mobile ='" + mobile_no + "' ", null);
            if (count1 != -1) {
                Log.v("DataHelp", "Update " + table + " Details Successfully");
            } else {
                Log.v("DataHelp", "Update " + table + " Details Fail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String lastInsertID(String key, String tbl_name) {

        Cursor cursor = null;
        cursor = sqLiteDatabase.rawQuery("SELECT " + key + " from " + tbl_name + " order by " + key + " desc limit 1", null);
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
        Log.e("OfflineData_Query", "SELECT tp.* , tparm.* from `" + Constant.TableNames.TBL_PATIENTS + "` AS tp LEFT JOIN `" + Constant.TableNames.TBL_PARAMETERS + "` as tparm ON tp.patient_id = tparm.patient_id");
        cursor = sqLiteDatabase.rawQuery("SELECT tp.* , tparm.* from `" + Constant.TableNames.TBL_PATIENTS + "` AS tp LEFT JOIN `" + Constant.TableNames.TBL_PARAMETERS + "` as tparm ON tp.patient_id = tparm.patient_id", null);

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
                e.printStackTrace();
            }
        }
        Log.e("OfflineData_Array", ":" + jArray.length());

        if (jArray.length() > 0)
            return jArray;

        return null;
    }


    public void deleteTable_data(String tbl_name, String key, String ids) {
        sqLiteDatabase.execSQL("delete from " + tbl_name + " WHERE " + key + " IN (" + ids + ")");
    }

}