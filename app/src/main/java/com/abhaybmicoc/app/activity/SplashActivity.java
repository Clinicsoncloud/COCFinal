package com.abhaybmicoc.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.services.ConnectivityService;
import com.abhaybmicoc.app.utils.ApiUtils;

import java.util.Locale;

/*
 * Splash Activity
 */
public class SplashActivity extends Activity {
    // region Variables

    private Handler splashHandler;
    private Context context = SplashActivity.this;

    private SharedPreferences sharedPreferencesLanguage;

    // endregion

    // region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

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

        sharedPreferencesLanguage = getSharedPreferences(ApiUtils.PREFERENCE_LANGUAGE, MODE_PRIVATE);
        setLocale(sharedPreferencesLanguage.getString("language", ""));

        startServices();

        splashHandler = new Handler();
        splashHandler.postDelayed(() -> {
            final Intent mainIntent = new Intent(context, OtpLoginScreen.class);
            startActivity(mainIntent);
            finish();
        }, 2000);
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
