/*
package com.abhaybmicoc.app.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

*/
/**
 * Class Name        :  <b>DataBaseHelper.java<b/>
 * Purpose           : DataBaseHelper is class related of database operation.
 * Developed By      :  <b>@Vaibhav Vadnere</b>
 * Created Date      :  <b>08-08-2019</b>
 *//*



public class DataBaseHelper {
    public static SQLiteDatabase db;
    public Context context;
    private Cursor cursor;
    private SQLiteHelper sqliteopenhelper;
    private JSONArray jArray;
    private JSONObject json_data;
    private boolean isExist = false;
    private String str_column_name;
    private String TAG = "DataBaseHelper";

    public DataBaseHelper(Context context) {
        this.context = context;
        sqliteopenhelper = new SQLiteHelper(context);
        db = sqliteopenhelper.getWritableDatabase();
        db = sqliteopenhelper.getReadableDatabase();
    }

    //Used for insert downloaded data
    public void insertDownloadedData(String table, String result) {
        // TODO Auto-generated method stub only to insert new data update existing  @ 23.04.16
        isExist = false;
        try {
            jArray = new JSONArray(result);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        for (int i = 0; i < jArray.length(); i++) {
            isExist = false;
            try {
                json_data = jArray.getJSONObject(i);
                Cursor c;

                c = db.rawQuery("Select * from " + table + " where  phpid='" + json_data.getString("id") + "' ", null);

                int count = c.getColumnCount();
                if (c != null) {
                    if (c.moveToFirst()) {
                        isExist = true;
                        c.close();
                    } else {
                        c.close();
                    }
                } else {

                }
                if (!isExist) {
                    ContentValues conV = new ContentValues();
                    conV.put("phpid", json_data.getString("id"));
                    for (int k = 1; k < count - 1; k++) {

                        if (json_data.has(c.getColumnName(k))) {

                            if (json_data.has(c.getColumnName(k))) {
                                str_column_name = c.getColumnName(k);

                                if (!str_column_name.equals("is_uploaded") && !str_column_name.equals("phpid")) {
                                    conV.put("" + str_column_name, json_data.getString(str_column_name).trim());
                                } else {
                                    if (!str_column_name.equals("phpid"))
                                        conV.put("" + str_column_name, "0");
                                }
                            }
                        }
                    }
                    conV.put("is_uploaded", "true");

                    long count1 = db.insert(table, null, conV);
                    if (count1 != -1) {
                        Log.v("DataHelp_Log", "Insert :-" + table + " Details Successfully");
                    } else {
                        Log.v("DataHelp_Log", "Insert :-" + table + " Details Fail");
                    }

                } else {
                    ContentValues conV = new ContentValues();
                    for (int k = 1; k < count - 1; k++) {

                        if (json_data.has(c.getColumnName(k))) {
                            str_column_name = c.getColumnName(k);
                            if (!str_column_name.equals("is_uploaded") && !str_column_name.equals("phpid")) {
                                conV.put("" + str_column_name, json_data.getString(str_column_name).trim());
                            } else {
                                if (!str_column_name.equals("phpid"))
                                    conV.put("" + str_column_name, "0");
                            }

                        }
                    }
                    conV.put("is_uploaded", "true");

                    long count1 = db.update(table, conV, "phpid='" + json_data.getString("id") + "' ", null);
                    if (count1 != -1) {
                        Log.v("DataHelp", "Update " + table + " Details Successfully");
                    } else {
                        Log.v("DataHelp", "Update " + table + " Details Fail");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //Used for insert downloaded data
    public String insertLog_Day_ListData(String table, String result) {
        // TODO Auto-generated method stub only to insert new data update existing  @ 23.04.16
        isExist = false;
        try {
            jArray = new JSONArray(result);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        for (int i = 0; i < jArray.length(); i++) {
            isExist = false;
            try {
                json_data = jArray.getJSONObject(i);
                Cursor c;
                String day_id;

                Log.e("json_data_INSLOg", "" + json_data);
//                Log.e("json_data_INSLOgQuery", "Select * from " + table + " where date='" + json_data.getString("date") + "' ");

                c = db.rawQuery("Select * from " + table + " where date='" + json_data.getString("date") + "' ", null);

//                Log.e("Querry_CursorCount", ":" + json_data.getString("date") + "  :  " + c.getColumnName(0));

                int count = c.getColumnCount();
                if (c != null) {

//                    Log.e("Querry_CursorNotNull", ":" + json_data.getString("date") + "  :  " + c.getColumnName(0));
                    if (c.moveToFirst()) {
//                        Log.e("Querry_CursorIfMove", ":" + json_data.getString("date") + "  :  " + c.getColumnName(0));

                        isExist = true;
                        c.close();
                    } else {

//                        Log.e("Querry_CursorElseMove", ":" + json_data.getString("date") + "  :  " + c.getColumnName(0) + " : Move : " + c.moveToFirst());

                        c.close();
                    }
                } else {

                }
                if (!isExist) {
                    ContentValues conV = new ContentValues();
                    conV.put("phpid", json_data.getString("id"));
                    for (int k = 1; k < count - 1; k++) {

                        if (json_data.has(c.getColumnName(k))) {
                            str_column_name = c.getColumnName(k);


                            if (!str_column_name.equals("is_uploaded") && !str_column_name.equals("phpid")) {
                                conV.put("" + str_column_name, json_data.getString(str_column_name).trim());
                            } else {
                                if (!str_column_name.equals("phpid"))
                                    conV.put("" + str_column_name, "0");
                            }
                        }
                    }
                    conV.put("is_uploaded", "true");

                    long count1 = db.insert(table, null, conV);
                    if (count1 != -1) {
                        Log.v("DataHelp_Log_LOG_DAY_LIST", "Insert :-" + table + " Details Successfully");
                    } else {
                        Log.v("DataHelp_Log_LOG_DAY_LIST", "Insert :-" + table + " Details Fail");
                    }


                    day_id = lastInsertID(DataBaseConstants.TableNames.LOG_DAY_LIST);

                    return day_id;


                } else {
                    ContentValues conV = new ContentValues();
                    for (int k = 1; k < count - 1; k++) {
                        str_column_name = c.getColumnName(k);
                        if (!str_column_name.equals("is_uploaded") && !str_column_name.equals("phpid")) {
                            conV.put("" + str_column_name, json_data.getString(str_column_name).trim());
                        } else {
                            if (!str_column_name.equals("phpid"))
                                conV.put("" + str_column_name, "0");
                        }
                    }
                    conV.put("is_uploaded", "true");

//                    day_id = c.getColumnName(0);
                    day_id = lastLOG_DAY_LIST_ID(json_data.getString("date"));

//                    day_id = c.getString(c.getColumnIndex(c.getColumnName(0)));

                    long count1 = db.update(table, conV, "date='" + json_data.getString("date") + "' ", null);

                    if (count1 != -1) {
                        Log.v("DataHelp_LOG_DAY_LIST", "Update " + table + " Details Successfully");
                    } else {
                        Log.v("DataHelp_LOG_DAY_LIST", "Update " + table + " Details Fail");
                    }

                    return day_id;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "";
    }


    //Used for insert downloaded data
    public void insertUploadData(String table, String result) {
        // TODO Auto-generated method stub only to insert new data update existing  @ 23.04.16
        isExist = false;
        JSONObject json_data;
        try {
            jArray = new JSONArray(result);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        for (int i = 0; i < jArray.length(); i++) {
            isExist = false;

            try {
                json_data = jArray.getJSONObject(i);
                Cursor c;
                Log.e("PHPId_11", json_data.getString("phpid"));

                Log.e("Inside:-", "ELSE");

                c = db.rawQuery("Select * from " + table + " where  phpid='" + json_data.getString("phpid") + "'", null);

                int count = c.getColumnCount();
//                Log.e("count_DB", ":-" + count);
                if (c != null) {
                    if (c.moveToFirst()) {
                        if (!json_data.getString("phpid").equals("")) {
                            Log.e("json_data_PHPID", ":-" + json_data.getString("phpid"));
                            isExist = true;
                        }
                        c.close();
                    } else {
                        c.close();
                    }
                } else {

                }
                if (!isExist) {
                    Log.e("insert", "insert");
                    ContentValues conV = new ContentValues();
                    conV.put("phpid", "");
                    for (int k = 1; k < count - 1; k++) {
                        str_column_name = c.getColumnName(k);
                        if (!str_column_name.equals("is_uploaded") && !str_column_name.equals("phpid")) {

                            if (json_data.has(str_column_name)) {
                                conV.put("" + str_column_name, json_data.getString(str_column_name));
                            } else {
                                conV.put("" + str_column_name, "");
                            }
                        } else {
                            if (!str_column_name.equals("phpid"))
                                conV.put("" + str_column_name, "0");
                        }
                    }
                    conV.put("is_uploaded", "false");
                    Log.e("conVinsert", "" + conV);

                    long count1 = db.insert(table, null, conV);
                    if (count1 != -1) {
                        Log.v("DataHelp_Log", "Insert " + table + " Details Successfully");
                    } else {
                        Log.v("DataHelp_Log", "Insert " + table + " Details Fail");
                    }
                } else {
                    Log.e("update", "update");
                    ContentValues conV = new ContentValues();
                    for (int k = 1; k < count - 1; k++) {
                        str_column_name = c.getColumnName(k);
                        if (!str_column_name.equals("is_uploaded") && !str_column_name.equals("phpid")) {
                            if (json_data.has(str_column_name)) {
                                conV.put("" + str_column_name, json_data.getString(str_column_name));
                            } else {
//                                conV.put("" + str_column_name, "");
                            }
                            //conV.put("" + str_column_name, json_data.getString(str_column_name));
                            //conV.put("" + str_column_name, json_data.getString(str_column_name));
                        } else {
                            if (!str_column_name.equals("phpid"))
                                conV.put("" + str_column_name, "0");
                        }
                    }
                    conV.put("is_uploaded", "false");

                    Log.e("conVupdate", "" + conV);
                    long count1 = db.update(table, conV, "phpid='" + json_data.getString("phpid") + "' ", null);

                    if (count1 != -1) {
                        Log.v("DataHelp", "Update " + table + " Details Successfully");
                    } else {
                        Log.v("DataHelp", "Update " + table + " Details Fail");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Used for insert downloaded data
    public void insertUploadMACHINE_SCHEDULE_REMARKS(String table, String result) {
        // TODO Auto-generated method stub only to insert new data update existing  @ 23.04.16
        isExist = false;
        JSONObject json_data;
        try {
            jArray = new JSONArray(result);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        for (int i = 0; i < jArray.length(); i++) {
            isExist = false;
            try {
                json_data = jArray.getJSONObject(i);
                Cursor c;
                Log.e("PHPId_11", json_data.getString("phpid"));

                Log.e("Inside:-", "ELSE");
                Log.e("CheckData_Queary", "Select * from " + table + " where  dp_id='" + json_data.getString("dp_id") + "' and machine_schedule_template_id='" + json_data.getString("machine_schedule_template_id") + "' and question_id='" + json_data.getString("question_id") + "'");
                c = db.rawQuery("Select * from " + table + " where  dp_id='" + json_data.getString("dp_id") + "' and machine_schedule_template_id='" + json_data.getString("machine_schedule_template_id") + "' and question_id='" + json_data.getString("question_id") + "' ", null);

                int count = c.getColumnCount();
//                Log.e("count_DB", ":-" + count);
                if (c != null) {
                    if (c.moveToFirst()) {
//                        if (!json_data.getString("phpid").equals("")) {
                        Log.e("json_data_PHPID", ":-" + json_data.getString("phpid"));
                        isExist = true;
//                        }
                        c.close();
                    } else {
                        c.close();
                    }
                } else {

                }
                if (!isExist) {
                    Log.e("insert", "insert");
                    ContentValues conV = new ContentValues();
                    conV.put("phpid", "");
                    for (int k = 1; k < count - 1; k++) {
                        str_column_name = c.getColumnName(k);
                        if (!str_column_name.equals("is_uploaded") && !str_column_name.equals("phpid")) {

                            if (json_data.has(str_column_name)) {
                                conV.put("" + str_column_name, json_data.getString(str_column_name));
                            } else {
                                conV.put("" + str_column_name, "");
                            }
                        } else {
                            if (!str_column_name.equals("phpid"))
                                conV.put("" + str_column_name, "0");
                        }
                    }
                    conV.put("is_uploaded", "false");
                    Log.e("conVinsert", "" + conV);
                    long count1 = db.insert(table, null, conV);
                    if (count1 != -1) {
                        Log.v("DataHelpRemark_Log", "Insert " + table + " Details Successfully");
                    } else {
                        Log.v("DataHelpRemark_Log", "Insert " + table + " Details Fail");
                    }
                } else {
                    Log.e("update", "update");
                    ContentValues conV = new ContentValues();
                    for (int k = 1; k < count - 1; k++) {
                        str_column_name = c.getColumnName(k);
                        if (!str_column_name.equals("is_uploaded") && !str_column_name.equals("phpid")) {
                            if (json_data.has(str_column_name)) {
                                conV.put("" + str_column_name, json_data.getString(str_column_name));
                            } else {
//                                conV.put("" + str_column_name, "");
                            }
                            //conV.put("" + str_column_name, json_data.getString(str_column_name));
                            //conV.put("" + str_column_name, json_data.getString(str_column_name));
                        } else {
                            if (!str_column_name.equals("phpid"))
                                conV.put("" + str_column_name, "0");
                        }
                    }
                    conV.put("is_uploaded", "false");

                    Log.e("conVupdate", "" + conV);
                    long count1 = db.update(table, conV, "phpid='" + json_data.getString("phpid") + "' ", null);
                    if (count1 != -1) {
                        Log.v("DataUpdateRemark_Log", "Update " + table + " Details Successfully");
                    } else {
                        Log.v("DataUpdateRemark_Log", "Update " + table + " Details Fail");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //for all
    public Cursor getDataToUpload(String table) {
        // TODO Auto-generated method stub
        Cursor c = null;
        c = db.rawQuery("select * from " + table + " where IS_UPLOADED='false'", null);
        return c;
    }

    public int updateStatusAll(String table, String id, String code) {
        // TODO Auto-generated method stub
        int count;
        ContentValues conV = new ContentValues();
        conV.put("is_uploaded", "true");
        conV.put("phpid", code);
        count = db.update(table, conV, "id='" + id + "'", null);

        if (count != -1) {
            Log.v("updateStatusAll_Log ", "Updated :" + table + " Details Successfully");
        } else {
            Log.v("updateStatusAll_Log ", "Updated :" + table + " Details Fail");
        }
        return count;
    }

    public static JSONArray get_logDay_List() {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
        Log.e("get_logDay_List_Query", "SELECT * FROM log_day_list ORDER BY id DESC ");
        cursor = db.rawQuery("SELECT * FROM log_day_list ORDER BY id DESC", null);

        Log.e("get_logDay_List_COUNT", " " + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("get_logDay_List_Array", " " + array.toString());

            return array;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ShiftStarted_logDay_List() {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
        Log.e("get_ShiftStarted_logDay_Query", "SELECT * FROM day_log WHERE shift_started = '1' ORDER BY id DESC ");
        cursor = db.rawQuery("SELECT * FROM day_log WHERE shift_started = '1' ORDER BY id DESC", null);

        Log.e("get_ShiftStarted_logDay_COUNT", " " + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("get_ShiftStarted_logDay_Array", " " + array.toString());

            return array;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_CycleStarted_logDay_List() {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
        Log.e("get_CycleStarted_logDay_Query", "SELECT * FROM day_log WHERE cycle_started = '1' ORDER BY id DESC ");
        cursor = db.rawQuery("SELECT * FROM day_log WHERE cycle_started = '1' ORDER BY id DESC", null);

        Log.e("get_CycleStarted_logDay_COUNT", " " + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("get_CycleStarted_logDay_Array", " " + array.toString());

            return array;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ShiftStartedday_log_list(String id, String driverid) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
        Log.e("get_ShiftStartedday_log_list_Query", "SELECT * FROM day_log WHERE id >= '" + id + "' AND driverid = '" + driverid + "' ORDER BY start_time_epoch ASC ");
        cursor = db.rawQuery("SELECT * FROM day_log WHERE id >= '" + id + "' AND driverid = '" + driverid + "' ORDER BY start_time_epoch ASC", null);

        Log.e("get_ShiftStartedday_log_list_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("get_ShiftStartedday_log_list_Array", " " + array.toString());

            return array;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONArray get_day_log_list(String day_id, String driverid) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
        Log.e("get_day_log_list_Query", "SELECT * FROM day_log WHERE day_id = '" + day_id + "' AND driverid = '" + driverid + "' ORDER BY id ASC ");
        cursor = db.rawQuery("SELECT * FROM day_log WHERE day_id = '" + day_id + "' AND driverid = '" + driverid + "' ORDER BY id ASC", null);

        Log.e("get_day_log_list_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("get_day_log_list_Array", " " + array.toString());

            return array;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray get_last_day_log_list(String day_id, String driverid) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
        Log.e("get_Lastday_log_list_Query", "SELECT * FROM day_log WHERE day_id = '" + day_id + "' AND driverid = '" + driverid + "' ORDER BY id DESC LIMIT 0,1");
        cursor = db.rawQuery("SELECT * FROM day_log WHERE day_id = '" + day_id + "' AND driverid = '" + driverid + "' ORDER BY id DESC LIMIT 0,1", null);

        Log.e("get_Lastday_log_list_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("get_Lastday_log_list_Array", " " + array.toString());

            return array;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray get_last_day_log(String driverid) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
        Log.e("get_day_log_list_Query", "SELECT * FROM day_log WHERE driverid = '" + driverid + "' ORDER BY id DESC LIMIT 0,1");
        cursor = db.rawQuery("SELECT * FROM day_log WHERE driverid = '" + driverid + "' ORDER BY id DESC LIMIT 0,1", null);

        Log.e("get_day_log_list_COUNT", " " + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("get_day_log_list_Array", " " + array.toString());

            return array;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray get_log_data(String log_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
        Log.e("get_day_log_list_Query", "SELECT * FROM day_log WHERE phpid = '" + log_id + "'");
        cursor = db.rawQuery("SELECT * FROM day_log WHERE phpid = '" + log_id + "'", null);

        Log.e("get_day_log_list_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("get_day_log_list_Array", " " + array.toString());

            return array;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }


    @SuppressLint("LongLogTag")
    public static JSONArray get_shift_log_list(String start_time_epoch, String day_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
//        Log.e("get_shift_log_list_Query", "SELECT * FROM day_log WHERE day_id = '" + day_id + "' AND ctime ='" + DTU.getCurrentDateTimeStamp_GMT(DTU.DMY) + "' and start_time_epoch >= '" + start_time_epoch + "'  ORDER BY start_time_epoch ASC ");
        Log.e("get_shift_log_list_Query", "SELECT * FROM day_log WHERE id >= '" + day_id + "' and start_time_epoch >= '" + start_time_epoch + "'  ORDER BY start_time_epoch ASC ");
        cursor = db.rawQuery("SELECT * FROM day_log WHERE id >= '" + day_id + "' ORDER BY start_time_epoch ASC", null);

        Log.e("get_shift_log_list_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("get_shift_log_list_Array", " " + array.toString());

            return array;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_cycle_log_list(String start_time_epoch, String log_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
//        Log.e("get_cycle_log_list_Query", "SELECT * FROM day_log WHERE id >= '" + log_id + "' AND ctime ='" + DTU.getCurrentDateTimeStamp_GMT(DTU.DMY) + "' and start_time_epoch >= '" + start_time_epoch + "'  ORDER BY start_time_epoch DESC ");
        Log.e("get_cycle_log_list_Query", "SELECT * FROM day_log WHERE id >= '" + log_id + "' ORDER BY start_time_epoch ASC ");
        cursor = db.rawQuery("SELECT * FROM day_log WHERE id >= '" + log_id + "' ORDER BY start_time_epoch ASC", null);

        Log.e("get_cycle_log_list_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }

            Log.e("get_cycle_log_list_Array", " " + array.toString());
            return array;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_StaffPresentList() {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_StaffPresentList", "SELECT * FROM `employee` WHERE is_deleted = '0' ORDER BY id ASC");
        cursor = db.rawQuery("SELECT * FROM `employee` WHERE is_deleted = '0' ORDER BY id ASC", null);

        Log.e("get_StaffPresentList_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_StaffPresentList_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONArray get_UnitsList() {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_UnitsList_query", "SELECT * FROM machine_schedule_unit WHERE is_deleted = '0' ORDER BY id ASC");
        cursor = db.rawQuery("SELECT * FROM machine_schedule_unit WHERE is_deleted = '0' ORDER BY id ASC", null);

        Log.e("get_UnitsList_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_UnitsList_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONArray get_stationsList() {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_stationsList", "SELECT * FROM `stations` WHERE is_deleted = '0'");
        cursor = db.rawQuery("SELECT * FROM `stations` WHERE is_deleted = '0'", null);

        Log.e("get_stationsList_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_MachinesName_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_BreakdownTypeLiost() {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_BreakdownTypeLiost", "SELECT * FROM `breakdown_types` ORDER BY id ASC");
        cursor = db.rawQuery("SELECT * FROM `breakdown_types` ORDER BY id ASC", null);

        Log.e("get_BreakdownTypeLiost_COUNT", " " + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_BreakdownTypeLiost_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray get_ScheduledTypeIdList(String machine_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_stationsList", "SELECT ms.id,ms.phpid,ms.machine_id,ms.schedule_type_id,st.schedule_types,ms.schedule_name as schedule_name_id,sn.schedule_name  FROM `machine_schedules` as ms JOIN schedule_types as st on ms.schedule_type_id = st.id  JOIN schedule_name as sn on ms.schedule_name = sn.id  WHERE ms.machine_id = '" + machine_id + "' AND ms.is_deleted = '0' GROUP BY ms.schedule_type_id ORDER BY ms.id ASC");
        cursor = db.rawQuery("SELECT ms.id,ms.phpid,ms.machine_id,ms.schedule_type_id,st.schedule_types,ms.schedule_name as schedule_name_id,sn.schedule_name  FROM `machine_schedules` as ms JOIN schedule_types as st on ms.schedule_type_id = st.id  JOIN schedule_name as sn on ms.schedule_name = sn.id  WHERE ms.machine_id = '" + machine_id + "' AND ms.is_deleted = '0' GROUP BY ms.schedule_type_id ORDER BY ms.id ASC", null);

        Log.e("get_stationsList_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_MachinesName_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get_TemplateID(String machine_id, String schedule_name_id, String schedule_type_id) {

        Cursor cursor = null;

        */
/*machine_id = "1";
        schedule_name_id = "25";
        schedule_type_id = "25";*//*


        Log.e("get_TemplateID_Query", "SELECT phpid FROM machine_schedule_template WHERE machine_id = '" + machine_id + "' AND schedule_name = '" + schedule_name_id + "' AND schedule_type_id = '" + schedule_type_id + "' ORDER BY id DESC limit 1");
        cursor = db.rawQuery("SELECT phpid FROM machine_schedule_template WHERE machine_id = '" + machine_id + "' AND schedule_name = '" + schedule_name_id + "' AND schedule_type_id = '" + schedule_type_id + "' ORDER BY id DESC limit 1", null);
//        cursor = db.rawQuery("SELECT * FROM machine_schedule_template WHERE machine_id = '" + machine_id + "' AND schedule_name = '" + schedule_name_id + "' AND schedule_type_id = '" + schedule_type_id + "' and is_deleted = '0' limit 1", null);
//        String id = "0";
        String phpid = "0";
        JSONArray array = new JSONArray();
        JSONObject json = null;

        while (cursor.moveToNext()) {
            json = new JSONObject();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                json = new JSONObject();
                phpid = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i)));

               */
/* try {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                    array.put(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*//*

            }
//            Log.e("get_TemplateID_Array", " " + array.toString());
            array.put(json);
        }
//        Log.e("get_MachinesName_Array", " " + array.toString());
        return phpid;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_MaintainanceLables(String schedule_template_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_MaintainanceLables", "SELECT * FROM `machine_schedule_labels` WHERE schedule_template_id = '" + schedule_template_id + "' AND is_deleted = '0' ORDER BY id ASC");
        cursor = db.rawQuery("SELECT * FROM `machine_schedule_labels` WHERE schedule_template_id = '" + schedule_template_id + "' AND is_deleted = '0' ORDER BY id ASC", null);

        Log.e("get_MaintainncLbls_CNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_MaintainanceLables_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_MaintainanceDetails(String machine_id, String schedule_type_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        */
/*machine_id = "1";
        schedule_type_id = "1";*//*


        Log.e("get_MaintainanceDetails", "SELECT ms.id,ms.machine_id,mt.name as machine_name,ms.schedule_type_id,st.schedule_types," +
                "ms.schedule_name as schedule_name_id,sn.schedule_name  FROM `machine_schedules` as ms JOIN schedule_types as st on ms.schedule_type_id = st.id " +
                " JOIN schedule_name as sn on ms.schedule_name = sn.id JOIN machine_types as mt on ms.machine_id = mt.id WHERE ms.machine_id = '" + machine_id + "' " +
                "AND ms.schedule_type_id = '" + schedule_type_id + "' AND ms.is_deleted = '0' GROUP BY ms.schedule_type_id ORDER BY ms.id ASC");
        cursor = db.rawQuery("SELECT ms.id,ms.machine_id,mt.name as machine_name,ms.schedule_type_id,st.schedule_types,ms.schedule_name as schedule_name_id,sn.schedule_name  FROM `machine_schedules` as ms JOIN schedule_types as st on ms.schedule_type_id = st.id  JOIN schedule_name as sn on ms.schedule_name = sn.id JOIN machine_types as mt on ms.machine_id = mt.id WHERE ms.machine_id = '" + machine_id + "' AND ms.schedule_type_id = '" + schedule_type_id + "' AND ms.is_deleted = '0' GROUP BY ms.schedule_type_id ORDER BY ms.id ASC", null);

        Log.e("get_MaintainanceDetails_CNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_MaintainanceDetails_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray get_QuestionList(String label_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_QuestionList_Query", "SELECT * FROM `machine_schedule_questions` WHERE label_id ='" + label_id + "' AND is_deleted = '0' ORDER BY id ASC");
        cursor = db.rawQuery("SELECT * FROM `machine_schedule_questions` WHERE label_id ='" + label_id + "' AND is_deleted = '0' ORDER BY id ASC", null);

        Log.e("get_QuestionList_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_QuestionList_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ConsumableList() {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_ConsumableList", "SELECT * FROM `consumables` WHERE is_deleted = '0' ORDER BY `consumables`.`id` ASC");
        cursor = db.rawQuery("SELECT * FROM `consumables` WHERE is_deleted = '0' ORDER BY `consumables`.`id` ASC", null);

        Log.e("get_ConsumableList_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_ConsumableList_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ActionTakenList(String question_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_ActionTakenList_Query", "SELECT * FROM `machine_schedule_panswers` WHERE question_id = '" + question_id + "' AND is_deleted = '0' ORDER BY id ASC");
        cursor = db.rawQuery("SELECT * FROM `machine_schedule_panswers` WHERE question_id = '" + question_id + "' AND is_deleted = '0' ORDER BY id ASC", null);

        Log.e("get_ActionTakenList_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_ActionTakenList_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_BreakdownReportdata(String user_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        */
/*Log.e("get_BreakdownReportdata", "SELECT * FROM `failure_transaction` WHERE reported_by  = '" + user_id + "' ORDER BY id DESC LIMIT 5");
        cursor = db.rawQuery("SELECT * FROM `failure_transaction` WHERE reported_by  = '" + user_id + "' ORDER BY id DESC LIMIT 5", null);*//*


        Log.e("get_BreakdownReportdata", "SELECT ft.*,mt.name FROM `failure_transaction` AS ft JOIN machine_types AS mt ON ft.machine_name = mt.id WHERE ft.reported_by  = '" + user_id + "' ORDER BY ft.phpid ASC");
        cursor = db.rawQuery("SELECT ft.*,mt.name FROM `failure_transaction` AS ft JOIN machine_types AS mt ON ft.machine_name = mt.id WHERE ft.reported_by  = '" + user_id + "' ORDER BY ft.phpid ASC", null);

        */
/*Log.e("get_BreakdownReportdata", "SELECT ft.*,st.station_name as block_section_from_city,st1.station_name as block_section_to_city,cmb.part_name as name_of_part  FROM `failure_transaction` as ft JOIN stations as st on ft.block_section_from = st.id  JOIN stations as st1 on ft.block_section_to = st1.id  JOIN consumables cmb on ft.partname = cmb.id   WHERE ft.reported_by  = '" + user_id + "' ORDER BY ft.id DESC LIMIT 5");
        cursor = db.rawQuery("SELECT ft.*,st.station_name as block_section_from_city,st1.station_name as block_section_to_city,cmb.part_name as name_of_part FROM `failure_transaction` as ft JOIN stations as st on ft.block_section_from = st.id  JOIN stations as st1 on ft.block_section_to = st1.id  JOIN consumables cmb on ft.partname = cmb.id   WHERE ft.reported_by  = '" + user_id + "' ORDER BY ft.id DESC LIMIT 5", null);*//*


        Log.e("get_BreakdownReportdata", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_BreakdownReportdata_Array", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_DailyProgressData(String user_id, String machine_mode) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_DailyProgressData", "SELECT dp.*, mt.name FROM `daily_progress` as dp JOIN machine_types as mt on dp.machine_id = mt.id    WHERE dp.user_id = '" + user_id + "' and dp.machine_mode = '" + machine_mode + "' ORDER by dp.id DESC");
        cursor = db.rawQuery("SELECT dp.*, mt.name FROM `daily_progress` as dp JOIN machine_types as mt on dp.machine_id = mt.id    WHERE dp.user_id = '" + user_id + "' and dp.machine_mode = '" + machine_mode + "' ORDER by dp.id DESC", null);

        Log.e("get_DailyProgressData_COUNT", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_DailyProgressData", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_DailyProgressBreakDownData(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_DailyProgressBreakDownData", "SELECT * FROM `daily_progress_breakdown` WHERE dp_id ='" + dp_id + "'");
        cursor = db.rawQuery("SELECT * FROM `daily_progress_breakdown` WHERE dp_id ='" + dp_id + "'", null);

        Log.e("get_DailyProgressBreakDownData", " " + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }

                array.put(json);
            }
            Log.e("get_DailyProgressBreakDownData", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_DailyProgressMovmentData(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
//        dp_id = "391";
        Log.e("get_DailyProgressMovmentData_Query", "SELECT dpm.*,st1.station_name as section_from_city,st2.station_name as section_to_city FROM `daily_progress_movement` as dpm JOIN stations st1 on dpm.section_from = st1.id JOIN stations st2 on dpm.section_to = st2.id WHERE dpm.dp_id = '" + dp_id + "' ORDER BY dpm.id DESC");
        cursor = db.rawQuery("SELECT dpm.*,st1.station_name as section_from_city,st2.station_name as section_to_city FROM `daily_progress_movement` as dpm JOIN stations st1 on dpm.section_from = st1.id JOIN stations st2 on dpm.section_to = st2.id WHERE dpm.dp_id = '" + dp_id + "' ORDER BY dpm.id DESC", null);

        Log.e("get_DailyProgressMovmentData", " " + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }

                array.put(json);
            }
            Log.e("get_DailyProgressMovmentData", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_DailyProgressWorkingData(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("get_DailyProgressWorkingData_Query", "SELECT * FROM `daily_progress_working` WHERE dp_id = '" + dp_id + "' ORDER BY id DESC");
        cursor = db.rawQuery("SELECT * FROM `daily_progress_working` WHERE dp_id = '" + dp_id + "' ORDER BY id DESC", null);

        Log.e("get_DailyProgressWorkingData", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_DailyProgressWorkingData", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_DailyProgressMaintenanceData(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
//        dp_id = "484";

        Log.e("get_DailyProgressMaintenanceData_Query", "SELECT dpm.*,st.schedule_types, sts.station_name FROM `daily_progress_maintainance` as dpm JOIN schedule_types as st on dpm.type_of_maintainance = st.id JOIN stations as sts on dpm.maintainance_carried_out_at = sts.id WHERE dpm.dp_id = '" + dp_id + "'");
        cursor = db.rawQuery("SELECT dpm.*,st.schedule_types, sts.station_name FROM `daily_progress_maintainance` as dpm JOIN schedule_types as st on dpm.type_of_maintainance = st.id JOIN stations as sts on dpm.maintainance_carried_out_at = sts.id WHERE dpm.dp_id = '" + dp_id + "'", null);
        */
/*Log.e("get_DailyProgressMaintenanceData", "SELECT dpm.*, sts.station_name FROM `daily_progress_maintainance` as dpm JOIN stations as sts on dpm.maintainance_carried_out_at = sts.id WHERE dpm.dp_id = '" + dp_id + "'");
        cursor = db.rawQuery("SELECT dpm.*, sts.station_name FROM `daily_progress_maintainance` as dpm JOIN stations as sts on dpm.maintainance_carried_out_at = sts.id WHERE dpm.dp_id = '" + dp_id + "'", null);*//*


        Log.e("get_DailyProgressMaintenanceData", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_DailyProgressMaintenanceData", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_DailyProgressRestData(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
//        dp_id = "484";

        Log.e("get_DailyProgressRestData", "SELECT * FROM `daily_progress_rest` where dp_id = '" + dp_id + "' ORDER by id DESC");
        cursor = db.rawQuery("SELECT * FROM `daily_progress_rest` where dp_id = '" + dp_id + "' ORDER by id DESC", null);

        Log.e("get_DailyProgressRestData", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_DailyProgressRestData", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @SuppressLint("LongLogTag")
    public static JSONArray get_DailyProgressNoBlockData(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
//        dp_id = "484";

        Log.e("get_DailyProgressRestData", "SELECT * FROM `daily_progress_no_block` WHERE dp_id = '" + dp_id + "' ORDER BY phpid DESC");
        cursor = db.rawQuery("SELECT * FROM `daily_progress_no_block` WHERE dp_id = '" + dp_id + "' ORDER BY phpid DESC", null);

        Log.e("get_DailyProgressRestData", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_DailyProgressRestData", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_LoginCheck(String username, String password) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;
//        dp_id = "484";

        Log.e("get_DailyProgressRestData", "SELECT * FROM `user` WHERE username ='" + username + "' AND password = '" + password + "' AND is_deleted = '0'");
        cursor = db.rawQuery("SELECT * FROM `user` WHERE username ='" + username + "' AND password = '" + password + "' AND is_deleted = '0'", null);

        Log.e("get_DailyProgressRestData", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("get_DailyProgressRestData", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String lastInsertID(String tbl_name) {

        Cursor cursor = null;

        cursor = db.rawQuery("SELECT id from " + tbl_name + " order by id desc limit 1", null);
        String id = "0";
        if (cursor.moveToNext()) {

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i)));
            }
        }
        return id;
    }

    public String lastLOG_DAY_LIST_ID(String date) {

        Cursor cursor = null;

        cursor = db.rawQuery("SELECT id from log_day_list where  date='" + date + "'", null);
        String id = "0";
        if (cursor.moveToNext()) {

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i)));
            }
        }

        Log.e("Date_Id_Log", ":" + id);
        return id;
    }

    public void deleteTable_data(String tbl_name) {
        Log.e("deleteTable_data_Log", "delete from  " + tbl_name + "");
        db.execSQL("delete from " + tbl_name);
    }

    */
/*public String deleteTable_data(String tbl_name) {

        Cursor cursor = null;
        Log.e("","");
        cursor = db.rawQuery("delete from " + tbl_name + "", null);
        String id = "0";
        if (cursor.moveToNext()) {

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                id = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i)));
            }
        }
        return id;
    }*//*


    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledProgressByDate(String date, String user_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledProgress_Query", "SELECT * FROM `daily_progress` WHERE DATE ='" + date + "' AND user_id = '" + user_id + "' ORDER BY id DESC LIMIT 1");
        cursor = db.rawQuery("SELECT * FROM `daily_progress` WHERE DATE ='" + date + "' ORDER BY id DESC LIMIT 1", null);

        Log.e("ScheduledProgress_Count", " " + cursor.getCount());

        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("ScheduledProgress_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledMaintainanceRecord(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledMaintainance_Query", "SELECT * FROM `daily_progress_maintainance` WHERE dp_id = '" + dp_id + "'");
        cursor = db.rawQuery("SELECT * FROM `daily_progress_maintainance` WHERE dp_id = '" + dp_id + "'", null);

        Log.e("ScheduledMaintainanceScheduledProgress_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledMaintainance_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledMovementRecord(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledMovement_Query", "SELECT * FROM `daily_progress_movement` WHERE dp_id = '" + dp_id + "'");
        cursor = db.rawQuery("SELECT * FROM `daily_progress_movement` WHERE dp_id = '" + dp_id + "'", null);

        Log.e("ScheduledMovement_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledMovement_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledWorkingRecord(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledWorking_Query", "SELECT * FROM `daily_progress_working` WHERE dp_id = '" + dp_id + "'");
        cursor = db.rawQuery("SELECT * FROM `daily_progress_working` WHERE dp_id = '" + dp_id + "'", null);

        Log.e("ScheduledWorking_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledWorking_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledWorkingDGS_MachineRecord(String w_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledWorking_Query", "SELECT * FROM `working_progress_dgs` WHERE w_id = '" + w_id + "'");
        cursor = db.rawQuery("SELECT * FROM `working_progress_dgs` WHERE w_id = '" + w_id + "'", null);

        Log.e("ScheduledWorking_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledWorking_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledWorkingUnimate_MachineRecord(String w_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledWorking_Query", "SELECT * FROM `working_progress_unimate` WHERE w_id = '" + w_id + "'");
        cursor = db.rawQuery("SELECT * FROM `working_progress_unimate` WHERE w_id = '" + w_id + "'", null);

        Log.e("ScheduledWorking_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledWorking_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledWorkingUTV_MachineRecord(String w_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledWorking_Query", "SELECT * FROM `working_progress_utv` WHERE w_id = '" + w_id + "'");
        cursor = db.rawQuery("SELECT * FROM `working_progress_utv` WHERE w_id = '" + w_id + "'", null);

        Log.e("ScheduledWorking_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledWorking_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledWorkingCSM_MachineRecord(String w_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledWorking_Query", "SELECT * FROM `working_progress_csm` WHERE w_id = '" + w_id + "'");
        cursor = db.rawQuery("SELECT * FROM `working_progress_csm` WHERE w_id = '" + w_id + "'", null);

        Log.e("ScheduledWorking_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledWorking_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledWorkingBCM_MachineRecord(String w_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledWorking_Query", "SELECT * FROM `working_progress_bcm` WHERE w_id = '" + w_id + "'");
        cursor = db.rawQuery("SELECT * FROM `working_progress_bcm` WHERE w_id = '" + w_id + "'", null);

        Log.e("ScheduledWorking_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledWorking_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledWorkingPBR_MachineRecord(String w_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledWorking_Query", "SELECT * FROM `working_progress_pbr` WHERE w_id = '" + w_id + "'");
        cursor = db.rawQuery("SELECT * FROM `working_progress_pbr` WHERE w_id = '" + w_id + "'", null);

        Log.e("ScheduledWorking_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledWorking_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledBreakdownRecord(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledMovement_Query", "SELECT * FROM `daily_progress_breakdown` WHERE dp_id = '" + dp_id + "'");
        cursor = db.rawQuery("SELECT * FROM `daily_progress_breakdown` WHERE dp_id = '" + dp_id + "'", null);

        Log.e("ScheduledMovement_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledMovement_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledRestRecord(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledMaintainance_Query", "SELECT * FROM `daily_progress_maintainance` WHERE dp_id = '" + dp_id + "'");
        cursor = db.rawQuery("SELECT * FROM `daily_progress_maintainance` WHERE dp_id = '" + dp_id + "'", null);

        Log.e("ScheduledMaintainanceScheduledProgress_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledMaintainance_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    public static JSONArray get_ScheduledNoBlockRecord(String dp_id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("ScheduledMaintainance_Query", "SELECT * FROM `daily_progress_maintainance` WHERE dp_id = '" + dp_id + "'");
        cursor = db.rawQuery("SELECT * FROM `daily_progress_maintainance` WHERE dp_id = '" + dp_id + "'", null);

        Log.e("ScheduledMaintainanceScheduledProgress_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);

            }
            Log.e("ScheduledMaintainance_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static JSONArray get_OperatorName(String id) {

        JSONArray array = new JSONArray();
        JSONObject json = null;
        Cursor cursor = null;

        Log.e("OperatorName_Query", "SELECT * FROM `employee` WHERE phpid = '" + id + "'");
        cursor = db.rawQuery("SELECT * FROM `employee` WHERE phpid = '" + id + "'", null);

        Log.e("OperatorName_Count", " " + cursor.getCount());
        try {
            while (cursor.moveToNext()) {
                json = new JSONObject();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    json.put(cursor.getColumnName(i), cursor.getString(cursor.getColumnIndex(cursor.getColumnName(i))));
                }
                array.put(json);
            }
            Log.e("OperatorName_Data", " " + array.toString());
            return array;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}*/
