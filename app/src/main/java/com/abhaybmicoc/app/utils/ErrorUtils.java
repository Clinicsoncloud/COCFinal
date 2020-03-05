package com.abhaybmicoc.app.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.abhaybmicoc.app.database.DataBaseHelper;
import com.abhaybmicoc.app.services.DateService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class ErrorUtils {

    public static void logErrors(Context context, Exception e, String fileName, String methodName, String msg) {
        String path = Environment.getExternalStorageDirectory() + "/" + "COC/";
    // Create the parent path
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fullName = path + "log";
        File logFile = new File (fullName);
//        File logFile = new File(Environment.DIRECTORY_DOCUMENTS + "/log.file");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException ex) {
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(msg);
            buf.newLine();
            buf.close();

            saveDataToLocal(context, fileName, methodName, e.getMessage());

        } catch (IOException ex) {

            saveDataToLocal(context, fileName, methodName, ex.getMessage());
        }
    }

    private static void saveDataToLocal(Context context, String fileName, String methodName, String message) {
        try {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(context);

            SharedPreferences sharedPreferences = context.getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
            SharedPreferences sharePreferenceActivator = context.getSharedPreferences(ApiUtils.PREFERENCE_ACTIVATOR, MODE_PRIVATE);

            String kiosk_id = sharePreferenceActivator.getString(Constant.Fields.KIOSK_ID,"");
            String mobile_number = sharedPreferences.getString(Constant.Fields.MOBILE_NUMBER,"");

            ContentValues paramsContentValues = new ContentValues();
            paramsContentValues.put(Constant.Fields.FILE_NAME, fileName);
            paramsContentValues.put(Constant.Fields.METHOD_NAME, methodName);
            paramsContentValues.put(Constant.Fields.MESSAGE, message);
            paramsContentValues.put(Constant.Fields.KIOSK_ID, kiosk_id);
            paramsContentValues.put(Constant.Fields.MOBILE_NUMBER, mobile_number);

            Log.e("paramsContentValues_Err", ":" + paramsContentValues);

            dataBaseHelper.saveToLocalTable(Constant.TableNames.ERROR_LOGS, paramsContentValues, "");
        } catch (Exception e) {
            saveDataToLocal(context,fileName,methodName,message);
        }
    }
}
