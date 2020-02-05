package com.abhaybmicoc.app.database;

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
}