package com.abhaybmicoc.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.services.ConnectivityService;

/*
 * Splash Activity
 */
public class SplashActivity extends Activity {
    // region Variables

    private Handler splashHandler;
    private Context context = SplashActivity.this;

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

    // endregion
}
