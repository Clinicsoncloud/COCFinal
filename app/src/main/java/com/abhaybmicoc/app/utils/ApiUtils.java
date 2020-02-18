package com.abhaybmicoc.app.utils;

public class ApiUtils {
    private static String PROTOCOL = "http://";
    private static String SERVER_URL = "45.252.190.29";

    public static String LOGIN_URL = PROTOCOL + SERVER_URL + "/api/v1/login";
    public static String PROFILE_URL = PROTOCOL + SERVER_URL + "/api/v1/profile";
    public static String PRINT_POST_URL = PROTOCOL + SERVER_URL + "/api/v1/parameter";
    public static String VERIFY_OTP_URL = PROTOCOL + SERVER_URL + "/api/v1/login/verify";
    public static String SYNC_OFFLINE_DATA_URL = PROTOCOL + SERVER_URL + "/api/v1/offline/sync";

    /* api for downloading pdf */
    public static String DOWNLOAD_PDF_URL = PROTOCOL + SERVER_URL + "/pdfs/";

    public static String PREFERENCE_PULSE = "pulse";
    public static String PREFERENCE_URL = "abhayBmi";
    public static String AUTO_CONNECT = "hcbluetooth";
    public static String GLUCOSE_DATA = "glucoseData";
    public static String PREFERENCE_ACTOFIT = "actofit";
    public static String PREFERENCE_HEIGHTDATA = "height";
    public static String PREFERENCE_ACTIVATOR = "Activator";
    public static String PREFERENCE_BLOODPRESSURE = "bpdata";
    public static String PREFERENCE_BIOSENSE = "toucHBShared";
    public static String PREFERENCE_PERSONALDATA = "personal";
    public static String PREFERENCE_HEMOGLOBIN = "Hemoglobin";
    public static String PREFERENCE_NEWRECORD = "actofit_auto";
    public static String PREFERENCE_THERMOMETERDATA = "thermometer";
    public static String PREFERENCE_LANGUAGE = "language";
    public static String PREFERENCE_OFFLINE = "offline";
}
