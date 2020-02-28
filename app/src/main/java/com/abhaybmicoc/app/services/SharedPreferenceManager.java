package com.abhaybmicoc.app.services;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toolbar;

import com.abhaybmicoc.app.activity.SettingsActivity;

public class SharedPreferenceManager {

    public static final String PREF_NAME = "COC_Prefrence";

    public static final String PATIENT_ID = "patient_id";
    public static final String PATIENT_NAME = "patient_name";
    public static final String PATIENT_EMAIL = "patient_email";
    public static final String PATIENT_GENDER = "patient_gender";
    public static final String DATE_OF_BIRTH = "date_of_birth";
    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String TOKEN = "token";

    public final static String LAST_SYNC_DATE_TIME = "last_sync_date_time";

    public final static String SELECTED_LANGUAGE = "selected_language";

    public final static String HEIGHT_BT_DEVICE_ADDRESS = "height_bt_device_address";
    public final static String HEIGHT_BT_DEVICE_NAME = "height_bt_device_name";

    public final static String HEIGHT = "height";


    private static SharedPreferences preference;
    private static SharedPreferenceManager preferenceManager = null;
    public Context context;

    private SharedPreferenceManager(Context context) {
        this.context = context;
        preference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferenceManager getInstance(Context context) {
        if (preferenceManager == null) {
            preferenceManager = new SharedPreferenceManager(context);
        }
        return preferenceManager;
    }

    public void clear() {
        preference.edit().clear().apply();
    }

    /*public boolean getIsLoggedIn() {
        return preference.getBoolean(IS_LOGGED_IN, false);
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        preference.edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply();
    }*/


    public void setPatientId(String patientId) {
        preference.edit().putString(PATIENT_ID, patientId).apply();
    }

    public String getPatientId() {
        return preference.getString(PATIENT_ID, "0");
    }

    public void setPatientName(String patientName) {
        preference.edit().putString(PATIENT_NAME, patientName).apply();
    }

    public String getPatientName() {
        return preference.getString(PATIENT_NAME, "");
    }

    public void setPatientEmail(String patientEmail) {
        preference.edit().putString(PATIENT_EMAIL, patientEmail).apply();
    }

    public String getPatientEmail() {
        return preference.getString(PATIENT_EMAIL, "");
    }

    public void setPatientGender(String patientGender) {
        preference.edit().putString(PATIENT_GENDER, patientGender).apply();
    }

    public String getPatientGender() {
        return preference.getString(PATIENT_GENDER, "");
    }


    public void setDateOfBirth(String dateOfBirth) {
        preference.edit().putString(DATE_OF_BIRTH, dateOfBirth).apply();
    }

    public String getDateOfBirth() {
        return preference.getString(DATE_OF_BIRTH, "");
    }

    public void setMobileNumber(String mobileNumber) {
        preference.edit().putString(MOBILE_NUMBER, mobileNumber).apply();
    }

    public String getMobileNumber() {
        return preference.getString(MOBILE_NUMBER, "");
    }

    public void setToken(String token) {
        preference.edit().putString(TOKEN, token).apply();
    }

    public String getToken() {
        return preference.getString(TOKEN, "");
    }

    public void setLastSyncDateTime(String lastSyncDateTime) {
        preference.edit().putString(LAST_SYNC_DATE_TIME, lastSyncDateTime).apply();
    }

    public String getLastSyncDateTime() {
        return preference.getString(LAST_SYNC_DATE_TIME, "");
    }

    public void setSelectedLanguage(String selectedLanguage) {
        preference.edit().putString(SELECTED_LANGUAGE, selectedLanguage).apply();
    }

    public String getSelectedLanguage() {
        return preference.getString(SELECTED_LANGUAGE, "");
    }


    public void setHeightBtDeviceAddress(String heightBtDeviceAddress) {
        preference.edit().putString(HEIGHT_BT_DEVICE_ADDRESS, heightBtDeviceAddress).apply();
    }

    public String getHeightBtDeviceAddress() {
        return preference.getString(HEIGHT_BT_DEVICE_ADDRESS, "");
    }

    public void setHeightBtDeviceName(String heightBtDeviceName) {
        preference.edit().putString(HEIGHT_BT_DEVICE_NAME, heightBtDeviceName).apply();
    }

    public String getHeightBtDeviceName() {
        return preference.getString(HEIGHT_BT_DEVICE_NAME, "");
    }

    public void setHeight(String height) {
        preference.edit().putString(HEIGHT, height).apply();
    }

    public String getHeight() {
        return preference.getString(HEIGHT, "");
    }


}
