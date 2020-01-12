package com.abhaybmicoc.app.activity;

import android.os.Bundle;
import android.view.Window;
import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.view.WindowManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.SharedPreferences;

import java.util.Set;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.entities.DataBase;
import com.abhaybmicoc.app.heightweight.HeightScreen;
import com.abhaybmicoc.app.utilities.ADSharedPreferences;
import com.abhaybmicoc.app.utilities.ANDMedicalUtilities;
import com.abhaybmicoc.app.DeviceSetUpActivityListDesign;

/*
 * Class for Login 
 */
public class LoginActivity extends Activity {
    final Context context = this;

    private TextView tvContinueGuest;
    private ProgressDialog progressDialog;

    private DataBase data;
    private DataBase databaseGroup;

    private String rememberChecked;
    private SharedPreferences sharedPreferences;

    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            return;
        }

        sharedPreferences = getSharedPreferences("ANDMEDICAL", MODE_PRIVATE);
    }

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI(savedInstanceState);
        setupEvents();
    }

    /**
     *
     */
    private void setupUI(Bundle savedInstanceState){
        progressDialog = Tools.progressDialog(LoginActivity.this);

        progressDialog.dismiss();
        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            createStandAlone(savedInstanceState);
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sharedPreferences = getSharedPreferences("ANDMEDICAL", MODE_PRIVATE);

        tvContinueGuest = findViewById(R.id.continue_guest);        // ISSUE: Wrong name continue_guest, why this is TextView

        String login_username = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "");
        rememberChecked = sharedPreferences.getString("rememberme", "yes");

        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            data = new DataBase(context);
        } else {
            data = new DataBase(context, login_username);
        }

        databaseGroup = new DataBase(context, "Allaccount.db");     // what is its use?
    }

    /**
     *
     */
    private void setupEvents(){
        tvContinueGuest.setOnClickListener(view ->  {
            updateSharedPreferences();

            Intent intent = new Intent(getApplicationContext(), DeviceSetUpActivityListDesign.class);
            startActivity(intent);
        });
    }

    /**
     * Crate StandAlone UI
     *
     * @param savedInstanceState
     */
    private void createStandAlone(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_login_stand_alone);

        ViewGroup tvContinueGuest = findViewById(R.id.continue_layout);

        tvContinueGuest.setOnClickListener(view ->  {
            continueScreen();
        });
    }

    /**
     *
     */
    private void continueScreen(){
        ADSharedPreferences.putString(ADSharedPreferences.KEY_USER_ID, "");
        ADSharedPreferences.putString(ADSharedPreferences.KEY_AUTH_TOKEN, "");
        ADSharedPreferences.putString(ADSharedPreferences.KEY_LOGIN_USER_NAME, getString(R.string.text_guest));
        ADSharedPreferences.putString(ADSharedPreferences.KEY_LOGIN_EMAIL, "guest@gmail.com");

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        if (bluetoothManager.getAdapter() != null) {
            Set<BluetoothDevice> pairingDevices = bluetoothManager.getAdapter().getBondedDevices();
            boolean isPairing = false;
            for (BluetoothDevice device : pairingDevices) {
                if (device.getName() != null) {
                    if (device.getName().startsWith("A&D") || device.getName().startsWith("UW")) {
                        isPairing = true;
                        break;
                    }
                }
            }

            if (isPairing) {
                Intent intent1 = new Intent(getApplicationContext(),
                        HeightScreen.class);
                startActivity(intent1);
                return;
            }
        }

        Intent intent1 = new Intent(getApplicationContext(), HeightScreen.class);
        startActivity(intent1);
    }

    /**
     *
     */
    private void updateSharedPreferences(){
        if (ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "").equalsIgnoreCase("")) {
            ADSharedPreferences.putString(ADSharedPreferences.KEY_USER_ID, "");
            ADSharedPreferences.putString(ADSharedPreferences.KEY_AUTH_TOKEN, "");
            ADSharedPreferences.putString(ADSharedPreferences.KEY_LOGIN_USER_NAME, getString(R.string.text_guest));
            ADSharedPreferences.putString(ADSharedPreferences.KEY_LOGIN_EMAIL, "guest@gmail.com");
        }
    }
}
