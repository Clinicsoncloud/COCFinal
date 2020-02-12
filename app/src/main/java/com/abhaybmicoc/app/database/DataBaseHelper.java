package com.abhaybmicoc.app.database;

import android.database.Cursor;
import android.util.Log;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

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

    public void saveToLocalTable(String table, ContentValues contentValues) {
        try {
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

        Log.e("Last_Insert_query :-   ", "SELECT " + key + " from " + tbl_name + " order by id desc limit 1");
        cursor = sqLiteDatabase.rawQuery("SELECT " + key + " from " + tbl_name + " order by id desc limit 1", null);
        String id = "0";
        if (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i)));
            }
        }
        return id;
    }


}