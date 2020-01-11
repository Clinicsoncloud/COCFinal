package com.abhaybmicoc.app.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.content.Intent;

import android.app.Activity;
import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.screen.OtpLoginScreen;

/*
 * Splash Activity
 */
public class SplashActivity extends Activity {

    private Handler splashHandler = null;

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

    /**
     *  Method to initialize the activity
     *
     * @author Ashutosh Pandey
     */
    private void initialize(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.and_splash);

        splashHandler = new Handler();
        splashHandler.postDelayed(() -> {
            final Intent mainIntent = new Intent(SplashActivity.this, OtpLoginScreen.class);
            SplashActivity.this.startActivity(mainIntent);
            finish();
        }, 2000);

    }

}
