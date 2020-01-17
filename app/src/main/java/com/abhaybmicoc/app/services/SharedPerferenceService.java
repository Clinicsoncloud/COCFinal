package com.abhaybmicoc.app.services;

import android.content.Context;

import static android.content.Context.MODE_PRIVATE;

public class SharedPerferenceService {
    /**
     *
     * @param context
     * @param sharedPreferenceName
     * @param key
     * @return
     */
    public static boolean isAvailable(Context context, String sharedPreferenceName, String key) {
        return context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE).getString(key, "").isEmpty();
    }

    /**
     *
     * @param context
     * @param sharedPreferenceName
     * @param key
     * @return
     */
    public static String getString(Context context, String sharedPreferenceName, String key) {
        return context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE).getString(key, "");
    }

    /**
     *
     * @param context
     * @param sharedPreferenceName
     * @param key
     * @return
     */
    public static int getInteger(Context context, String sharedPreferenceName, String key) throws NumberFormatException{
        try {

            return Integer.parseInt(context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE).getString(key, ""));
        }catch (Exception e){
            return 0;
        }
    }

    /**
     *
     * @param context
     * @param sharedPreferenceName
     * @param key
     * @return
     */
    public static double getDouble(Context context, String sharedPreferenceName, String key) throws NumberFormatException{
        try {
            return Double.parseDouble(context.getSharedPreferences(sharedPreferenceName, MODE_PRIVATE).getString(key, ""));
        }catch (Exception e){
            return 0;
        }
    }
}
