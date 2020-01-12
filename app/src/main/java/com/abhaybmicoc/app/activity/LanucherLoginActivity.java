package com.abhaybmicoc.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.view.WindowManager;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.view.View.OnClickListener;
import android.bluetooth.BluetoothManager;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.entities.DataBase;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.utilities.ADSharedPreferences;
import com.abhaybmicoc.app.utilities.ANDMedicalUtilities;

import java.util.Set;

/*
 * Class for Login 
 */
public class LanucherLoginActivity extends Activity {
    final Context context = this;

    private TextView tvContinueGuest;

    private DataBase data;
    private DataBase databaseGroup;

    private String rememberChecked;
    private SharedPreferences prefs1;

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    protected void onResume() {
        super.onResume();

        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            return;
        }
        prefs1 = getSharedPreferences("ANDMEDICAL", MODE_PRIVATE);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI(savedInstanceState);
        setupEvents();
    }

    private void setupUI(Bundle savedInstanceState){
        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            createStandAlone(savedInstanceState);
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        prefs1 = getSharedPreferences("ANDMEDICAL", MODE_PRIVATE);

        tvContinueGuest = findViewById(R.id.continue_guest);

        String loginUsername = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "");
        rememberChecked = prefs1.getString("rememberme", "yes");

        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            data = new DataBase(context);
        } else {
            data = new DataBase(context, loginUsername);        // ISSUE: what is the use of data?
        }

        databaseGroup = new DataBase(context, "Allaccount.db");
    }

    private void setupEvents(){
        tvContinueGuest.setOnClickListener(view -> {
            continueScreen();
        });
    }

    /**
     * Create StandAlone UI
     *
     * @param savedInstanceState
     */
    private void createStandAlone(Bundle savedInstanceState) {      // ISSUE: What is the use of savedInstanceState here?
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.activity_login_stand_alone);

        ViewGroup tvContinueGuest = findViewById(R.id.continue_layout);

        tvContinueGuest.setOnClickListener(view -> {
            if (ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "").equalsIgnoreCase("")) {
                ADSharedPreferences.putString(ADSharedPreferences.KEY_USER_ID, "");
                ADSharedPreferences.putString(ADSharedPreferences.KEY_AUTH_TOKEN, "");
                ADSharedPreferences.putString(ADSharedPreferences.KEY_LOGIN_USER_NAME, getString(R.string.text_guest));
                ADSharedPreferences.putString(ADSharedPreferences.KEY_LOGIN_EMAIL, "guest@gmail.com");
            }
            Intent intent = new Intent(getApplicationContext(),
                    OtpLoginScreen.class);
            startActivity(intent);
        });
    }

    /**
     *
     */
    private void continueScreen(){
        updateSharedPreferences();

        Intent intent = new Intent(getApplicationContext(), OtpLoginScreen.class);
        startActivity(intent);

        if(isBluetoothAvailable()) {
            intent = new Intent(getApplicationContext(), OtpLoginScreen.class);
            startActivity(intent);
        }
    }

    /**
     *
     * @return
     */
    private boolean isBluetoothAvailable(){
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        if (bluetoothManager.getAdapter() != null) {
            Set<BluetoothDevice> pairingDevices = bluetoothManager.getAdapter().getBondedDevices();
            boolean isPairing = false;
            for (BluetoothDevice device : pairingDevices) {
                if (device.getName() != null) {
                    // ISSUE: Why static values compared?
                    if (device.getName().startsWith("A&D") || device.getName().startsWith("UW")) {
                        isPairing = true;
                        break;
                    }
                }
            }

            return isPairing;
        }

        return false;
    }

    /**
     *
     */
    private void updateSharedPreferences(){
        ADSharedPreferences.putString(ADSharedPreferences.KEY_USER_ID, "");
        ADSharedPreferences.putString(ADSharedPreferences.KEY_AUTH_TOKEN, "");
        ADSharedPreferences.putString(ADSharedPreferences.KEY_LOGIN_USER_NAME, getString(R.string.text_guest));         // ISSUE: Why static value?
        ADSharedPreferences.putString(ADSharedPreferences.KEY_LOGIN_EMAIL, "guest@gmail.com");  // ISSUE: Why static email?
    }
}
