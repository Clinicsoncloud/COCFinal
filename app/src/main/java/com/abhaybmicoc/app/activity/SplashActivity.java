package com.abhaybmicoc.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.services.ConnectivityService;
import com.abhaybmicoc.app.services.HttpService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Utils;
import com.abhaybmicoc.app.utils.ValidationUtil;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
 * Splash Activity
 */
public class SplashActivity extends Activity {
    // region Variables

    private Handler splashHandler;
    private Context context = SplashActivity.this;

    private SharedPreferences sharedPreferencesLanguage;

    public static String currentVersion = "0";

    final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    Dialog versionUpdateDialog;

    // endregion

    // region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        if (Utils.isOnline(context)) {
            getLatestAPKVersion();
        }

    }

    private void getLatestAPKVersion() {
        try {
            Map<String, String> headerParams = new HashMap<>();
            Map<String, String> requestBodyParams = new HashMap<>();

            HttpService.accessWebServices(
                    context,
                    ApiUtils.CONFIG_URL,
                    Request.Method.GET,
                    requestBodyParams,
                    headerParams,
                    (response, error, status) -> handleCheckAPKResponse(response, error, status));
        } catch (Exception e) {
        }
    }

    private void handleCheckAPKResponse(String response, VolleyError error, String status) {

        Log.e("response_APKVersion", ":" + response);
        try {
            if (status.equals("response")) {
                JSONObject resultObj = new JSONObject(response);
                if (resultObj != null) {
                    validateAppVersion(resultObj);
                }
            }
        } catch (Exception e) {
        }
    }

    private void validateAppVersion(JSONObject resultObject) {
        try {
            Log.e("currentVersion_Log", ":" + currentVersion);
            Log.e("forced_update_Log", ":" + resultObject.getString("APP_VERSION_FORCED"));
            Log.e("latestVision_Log", ":" + resultObject.getString("APP_VERSION_LATEST") + "    :    " +
                    ValidationUtil.versionCompare(currentVersion, resultObject.getString("APP_VERSION_LATEST")));

            Log.e("Compare_version_Log", ":" + ValidationUtil.versionCompare(currentVersion, resultObject.getString("APP_VERSION_FORCED")));

            if (ValidationUtil.versionCompare(currentVersion, resultObject.getString("APP_VERSION_LATEST")) > 0) {
                Log.e("Compare_version_Log_Large", ":" +
                        ValidationUtil.versionCompare(currentVersion, resultObject.getString("APP_VERSION_FORCED")));

                showUpdateAppPopUp(resultObject);

            } else {
                Log.e("", "");

            }


            if (ValidationUtil.versionCompare(currentVersion, resultObject.getString("APP_VERSION_FORCED")) < 0)
                Log.e("Smaller_Version__OneLog", "" + currentVersion + " is smaller");
            else if (ValidationUtil.versionCompare(resultObject.getString("APP_VERSION_FORCED"),
                    resultObject.getString("APP_VERSION_LATEST")) > 0)

                Log.e("Smaller_Version__TwoLog", "" + resultObject.getString("APP_VERSION_LATEST") + " is smaller");
            else
                Log.e("Equal_Version_Log", "Both version are equal");


        } catch (Exception e) {
        }
    }

    private void showUpdateAppPopUp(JSONObject resultObject) {
        versionUpdateDialog.setContentView(R.layout.update_app_version_dialog);
        layoutParams.copyFrom(versionUpdateDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        versionUpdateDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        versionUpdateDialog.setCanceledOnTouchOutside(false);

        final TextView tv_msg = versionUpdateDialog.findViewById(R.id.tv_msg);
        final Button btnUpdate = versionUpdateDialog.findViewById(R.id.btn_Update);
        final ImageView ic_CloseDialog = versionUpdateDialog.findViewById(R.id.ic_CloseDilog);

        tv_msg.setTextColor(context.getResources().getColor(R.color.white));

        try {
            String[] currentAppVersionArray = currentVersion.split(".");
            String[] app_version_forcedArray = resultObject.getString("APP_VERSION_FORCED").split(".");
            String[] app_version_latestArray = resultObject.getString("APP_VERSION_LATEST").split(".");

            Log.e("ForcedApp_Major", "" + app_version_forcedArray[0] + "   :   " + resultObject.getString("APP_VERSION_FORCED"));
            Log.e("InstalledApp_Major", "" + currentAppVersionArray[0]);

            if (Integer.parseInt(app_version_forcedArray[0]) > Integer.parseInt(currentAppVersionArray[0])) {
                tv_msg.setText("COC recomends that you update to the latest version.\nPlease Update to latest version!");
                ic_CloseDialog.setVisibility(View.GONE);
            } else {
                tv_msg.setText("COC recomends that you update to the latest version.");
                ic_CloseDialog.setVisibility(View.VISIBLE);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        ic_CloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                versionUpdateDialog.dismiss();
            }
        });


        versionUpdateDialog.show();
        versionUpdateDialog.getWindow().setAttributes(layoutParams);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashHandler.removeCallbacksAndMessages(null);
    }

    // endregion

    // region Initialization methods

    /**
     * Method to initialize the activity
     *
     * @author Ashutosh Pandey
     */
    private void initialize() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.and_splash);

        versionUpdateDialog = new android.app.Dialog(context);
        versionUpdateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        sharedPreferencesLanguage = getSharedPreferences(ApiUtils.PREFERENCE_LANGUAGE, MODE_PRIVATE);
        setLocale(sharedPreferencesLanguage.getString("language", ""));

        startServices();


        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = pInfo.versionName;

            Log.e("currentVersion_Log", ":" + currentVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        /*splashHandler = new Handler();
        splashHandler.postDelayed(() -> {
            final Intent mainIntent = new Intent(context, OtpLoginScreen.class);
            startActivity(mainIntent);
            finish();
        }, 2000);*/
    }

    private void startServices() {
        startService(new Intent(context, ConnectivityService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * setLocale Method for changing the language accent in android
     *
     * @param lang
     */
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //saving data to shared preference
        SharedPreferences.Editor editor = sharedPreferencesLanguage.edit();
        editor.putString("language", lang);
        editor.apply();
    }

    // endregion
}
