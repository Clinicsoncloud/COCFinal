package com.abhaybmicoc.app.utils;

public class ApiUtils {

    private static String PROTOCOL = "http://";
    //        private static String SERVER_URL = "45.252.190.29";
//    private static String SERVER_URL = "45.252.190.200";            // Test server  vv- 27-03-2020
    private static String SERVER_URL = "45.252.190.217";            // Updated server  vv- 31-03-2020
//    private static String SERVER_URL = "192.168.43.40/medicine-backend/public";

    public static String KIOSK = PROTOCOL + SERVER_URL + "/api/v1/kiosk";
    public static String FIND_BY_TOKEN = PROTOCOL + SERVER_URL + "/api/v1/clinic/find-by-token";
    public static String LOGIN_URL = PROTOCOL + SERVER_URL + "/api/v1/login";
    public static String PROFILE_URL = PROTOCOL + SERVER_URL + "/api/v1/patient";
    public static String PRINT_POST_URL = PROTOCOL + SERVER_URL + "/api/v1/parameter";
    public static String VERIFY_OTP_URL = PROTOCOL + SERVER_URL + "/api/v1/login/verify";
    //    public static String SYNC_OFFLINE_DATA_URL = PROTOCOL + SERVER_URL + "/api/v1/offline/sync";
    public static String SYNC_OFFLINE_DATA_URL = PROTOCOL + SERVER_URL + "/api/v1/offline-sync";
    public static String LOCATION_URL = PROTOCOL + SERVER_URL + "/api/v1/location";
    public static String UPDATE_CLINIC_URL = PROTOCOL + SERVER_URL + "/api/v1/clinic/";

    /* api for downloading pdf */
//    public static String DOWNLOAD_PDF_URL = PROTOCOL + SERVER_URL + "/pdfs/";
    public static String DOWNLOAD_PDF_URL = PROTOCOL + SERVER_URL + "/api/v1/cdn/pdf/";

    public static String PREFERENCE_PULSE = "pulse";
    public static String PREFERENCE_URL = "abhayBmi";
    public static String AUTO_CONNECT = "hcbluetooth";
    public static String THERMOMETER_AUTO_CONNECT = "thermometerbluetooth";
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
    //    public static String PREFERENCE_LEFTVISION = "leftvision";
//    public static String PREFERENCE_RIGHTVISION = "rightvision";
    public static String PREFERENCE_VISION_RESULT = "vision_result";
}
