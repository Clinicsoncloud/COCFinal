package com.abhaybmi.app.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.abhaybmi.app.BuildConfig;
import com.abhaybmi.app.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.ContentValues.TAG;


/**
 * Created by Aikya on 16-Jan-18.
 */

public class Utils {
    public static final int REQUEST_PERMISSIONS_ALL = 600;
    public static final int REQUEST_BLUETOOTH = 601;
    public static final int REQUEST_WIFI = 602;
    public static int screenHeight;

    public static int screenWidth;

    public static Typeface regularFont;
    public static Typeface lightFont;
    public static Typeface boldFont;
    public static Typeface CHALKBOARD_SE_REGULAR_FONT;
    public static Typeface CHALKBOARD_SE_BOLD_FONT;
    public static Typeface CHALKBOARD_SE_LIGHT_FONT;

    public static PopupWindow mAddTaskPopupWindow;
    public static PopupWindow mPopupWindow;
    public int REQUEST_EXTERNAL_LOCATION = 592;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    public String[] PERMISSIONS_BLUETOOTH = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
    };
    public String[] PERMISSIONS_WIFI = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
    };
    public String[] PERMISSIONS_ALL = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE

    };
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;


    private static Utils utils;
    private int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 566;

    private Utils() {
    }

    public static Utils getInstance() {
        if (utils == null) {
            utils = new Utils();
        }
        return utils;
    }

    //Screen Size Setting For All Devices by HARI
    public static void setDimensions(Context _context) {
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) _context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            screenWidth = displaymetrics.widthPixels;
            screenHeight = displaymetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity, View view) {
        // Check if no view has focus:
        if (view == null) {
            view = activity.getCurrentFocus();
        }
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    //For InterNet Checking
    public static boolean isOnline(Context _Context) {
        ConnectivityManager cm = (ConnectivityManager) _Context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static boolean isValidMobile(String phone) {
        if (phone == null) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }
    }

    /**
     * Obtains the LayoutInflater from the given context.
     */
    private static LayoutInflater from_Context(Context context) {
        LayoutInflater layoutInflater = null;
        try {
            if (context != null) {
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            if (layoutInflater == null) {
                throw new AssertionError("LayoutInflater not found.");
            }
        } catch (Exception e) {
            Log.w("HARI-->DEBUG", e);
            layoutInflater = null;
        }
        return layoutInflater;
    }

    public static void statusBarSetup(Activity _activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decor = _activity.getWindow().getDecorView();
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                _activity.getWindow().setStatusBarColor(_activity.getResources().getColor(R.color.colorPrimaryDark));
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    _activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    _activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    _activity.getWindow().setStatusBarColor(_activity.getResources().getColor(R.color.colorPrimaryDark));
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }



    public boolean getCoarseLocationPermission(Activity context) {
// Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            return false;
        } else {
            return true;
        }
    }


    public static boolean verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        } else {
            return true;
        }
    }

    public static String getFilename(Context context, String extention) {
        String filepath = Environment.getExternalStorageDirectory().getPath() + "/"
                + context.getResources().getString(R.string.app_name);
        File file = new File(filepath);

        if (!file.exists()) {
            file.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("ddMMyyyy").format(Calendar.getInstance().getTime());

        return (file.getAbsolutePath() + "/" + timeStamp + extention);
    }

    public static void visibleLabelOfSpinner(Spinner spinner, TextView textView) {


        if (spinner.getSelectedItemPosition() == 0) {
            textView.setVisibility(View.INVISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
        }

    }

    public static void setLocale(Activity activity, String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
    }

    public static void visibleLabel(EditText editText, TextView textView, boolean hasFocus) {
        if (hasFocus) {
            textView.setVisibility(View.VISIBLE);
        } else {
            if (editText.getText().length() > 0) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void visibleLabelForTextView(TextView textView1, final TextView textView) {
        textView1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 > 0) {
                    textView.setVisibility(View.VISIBLE);
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public void saveToSharedPreference(Context context, String key, Object value) {
        String localValue;
        if (value != null && context != null) {
            localValue = value.toString();
        } else {
            localValue = null;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, localValue);
        editor.apply();
    }

    /**
     * Get String value from Shared Preference
     *
     * @param context The context of the current state
     * @param key     The name of the preference to retrieve
     * @return Value for the given preference if exist else null.
     */
    public String getFromSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, null);
    }

    public void deleteSharedPreference(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    public static boolean isSimSupport(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);

    }

    public boolean giveLocationPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_LOCATION,
                    REQUEST_EXTERNAL_LOCATION
            );
            return false;
        } else {
            return true;
        }
    }

    public boolean giveBluetoothPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_BLUETOOTH,
                    REQUEST_BLUETOOTH
            );
            return false;
        } else {
            return true;
        }
    }

    public boolean giveWifiPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_WIFI,
                    REQUEST_WIFI
            );
            return false;
        } else {
            return true;
        }
    }

    public static boolean getCameraPermission(Activity context) {
// Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        } else {
            return true;
        }
    }


    public static void hideDialog(Dialog mDialog) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public String getIMEI(Activity activity, int slotID) {
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager) activity
                    .getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return mTelephonyMgr.getDeviceId(slotID);
            } else {
                return mTelephonyMgr.getDeviceId();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }


   /* public static KProgressHUD showProgressDialog(Context _activity, String loadingText) {
        try {
            KProgressHUD hud = KProgressHUD.create(_activity)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(loadingText)
                    .setDimAmount(0.6f)
                    .setCancellable(false);
            hud.show();
            return hud;
        } catch (Exception ee) {
            ee.printStackTrace();
        }
        return null;
    }

    public static void hideProgressDialog(KProgressHUD hud) {
        if (hud != null && hud.isShowing()) {
            hud.dismiss();
        }
        if (hud != null) {
            hud = null;
        }
    }*/


    public static boolean isSDCardValid(Context context, boolean showToast) {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }

        if (Environment.MEDIA_REMOVED.equals(state)) {
            if (showToast) {
                Toast.makeText(context, "SD card not present",
                        Toast.LENGTH_LONG).show();
            }

            return false;
        }

        if (Environment.MEDIA_UNMOUNTED.equals(state)) {
            if (showToast) {
                Toast.makeText(context, "SD card not mounted",
                        Toast.LENGTH_LONG).show();
            }

            return false;
        }

        if (showToast) {
            Toast.makeText(
                    context,
                    "The SD card in the device is in '" + state
                            + "' state, and cannot be used.", Toast.LENGTH_LONG)
                    .show();
        }

        return false;
    }


    public void copyFile(Context context, String src, String dst) {
        if (TextUtils.equals(src, dst)) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Source (" + src + ") and destination (" + dst
                        + ") are the same. Skipping file copying.");
            }
            return;
        }

        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            Toast.makeText(
                    context,
                    "Failed to copy " + src + " to " + dst + ": "
                            + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG,
                                "Ignored the exception caught while closing input stream for "
                                        + src + ": " + e.getMessage(), e);
                    }
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    if (BuildConfig.DEBUG) {
                        Log.w(TAG,
                                "Ignored the exception caught while closing output stream for "
                                        + dst + ": " + e.getMessage(), e);
                    }
                }
            }
        }
    }

    public String getImageFullPath(Object path, String url) {
        String imagePath = null;
        if (path instanceof String) {
            imagePath = (String) path;
            if (!imagePath.contains("file:///")) {
                imagePath = "file://" + imagePath;
            }
        } else if (path instanceof Long) {
            imagePath = url + path;
        }
        return imagePath;
    }

    /**
     * Call to a given phone number
     *
     * @param context
     * @param phoneNo
     */



    /**
     * @param context
     * @param to      email id
     * @param subject
     * @param body
     */
    public void sendEmail(Context context, String to, String subject, String body) {
        if (TextUtils.isEmpty(subject)) {
            subject = "";
        }
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", to, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    /**
     * Check for network availability
     *
     * @param context Activity context
     * @return true if network available else false.
     */
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        try {
            Toast.makeText(context, "Network not available", Toast.LENGTH_LONG)
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static String convertToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }


}
