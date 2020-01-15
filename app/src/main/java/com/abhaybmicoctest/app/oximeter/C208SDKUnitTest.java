package com.abhaybmicoctest.app.oximeter;

import com.choicemmed.c208blelibrary.utils.ByteUtils;

/**
 * Author：ZhengZhong on 2016/11/8 16:30
 */

public class C208SDKUnitTest {
    private static final String TAG = "C208SDKUnitTest";


    public static int parseSpo(String data) {
        return Integer.parseInt(data.substring(6, 8), 16);

    }

    public static int parsePR(String data) {
        return Integer.parseInt(data.substring(8, 10), 16);
    }

    public static String parseDeviceSN(String data) {
        int cmdLen = Integer.parseInt(data.substring(4, 6), 16);
        String deviceSN = data.substring(8, (8 + (cmdLen - 2) * 2));
        return deviceSN;
    }

    public static String parseDeviceID(String data) {
        String partID = ByteUtils.hexStringReverse(data.substring(12, 20));
        String deviceID = data.substring(8, 12) + partID;
        return deviceID;
    }

    public static boolean parseMatchResult(String data) {
        int code = Integer.parseInt(data.substring(8, 10), 16);
        if (code == 0)
            return true;
        return false;
    }


}