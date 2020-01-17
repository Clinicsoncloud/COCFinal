package com.abhaybmicoc.app.services;

import android.content.Context;

import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceService {
    /**
     * @param context
     * @param sharedPreferenceName
     * @param key
     * @return
     */
    public static boolean isAvailable(Context context, String sharedPreferenceName, String key) {
        String value = context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE).getString(key, "");
        return value != null && value.trim().length() > 0;
    }

    /**
     * @param context
     * @param sharedPreferenceName
     * @param key
     * @return
     */
    public static String getString(Context context, String sharedPreferenceName, String key) {
        return context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE).getString(key, "");
    }

    /**
     * @param context
     * @param sharedPreferenceName
     * @param key
     * @return
     */
    public static int getInteger(Context context, String sharedPreferenceName, String key) throws NumberFormatException {
        try {
            return Integer.parseInt(context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE).getString(key, ""));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param context
     * @param sharedPreferenceName
     * @param key
     * @return
     */
    public static double getDouble(Context context, String sharedPreferenceName, String key) throws NumberFormatException {
        try {
            return Double.parseDouble(context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE).getString(key, ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isMalePatient(Context context){
        return SharedPreferenceService.getString(context, ApiUtils.PREFERENCE_PERSONALDATA, Constant.Fields.GENDER).equalsIgnoreCase("male");
    }
}
