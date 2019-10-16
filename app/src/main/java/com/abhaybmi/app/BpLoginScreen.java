package com.abhaybmi.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.abhaybmi.app.DashboardActivity;
import com.abhaybmi.app.DeviceSetUpActivityListDesign;
import com.abhaybmi.app.R;
import com.abhaybmi.app.entities.DataBase;
import com.abhaybmi.app.hemoglobin.MainActivity;
import com.abhaybmi.app.utilities.ADSharedPreferences;
import com.abhaybmi.app.utilities.ANDMedicalUtilities;

import java.util.Set;


/*
 * Class for Login
 */
public class BpLoginScreen extends Activity implements OnClickListener {

    private TextView continue_guest;
    DataBase data, databaseGroup;
    final Context context = this;
    String rememberChecked;
    SharedPreferences prefs1;

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

        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            createStandAlone(savedInstanceState);
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        prefs1 = getSharedPreferences("ANDMEDICAL", MODE_PRIVATE);

        continue_guest = (TextView) findViewById(R.id.continue_guest);
        String login_username = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "");
        rememberChecked = prefs1.getString("rememberme", "yes");


        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            data = new DataBase(context);
        } else {
            data = new DataBase(context, login_username);
        }


        databaseGroup = new DataBase(context, "Allaccount.db");

        continue_guest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "").equalsIgnoreCase("")) {
                    ADSharedPreferences.putString(ADSharedPreferences.KEY_USER_ID, "");
                    ADSharedPreferences.putString(ADSharedPreferences.KEY_AUTH_TOKEN, "");
                    ADSharedPreferences.putString(ADSharedPreferences.KEY_LOGIN_USER_NAME, getString(R.string.text_guest));
                    ADSharedPreferences.putString(ADSharedPreferences.KEY_LOGIN_EMAIL, "guest@gmail.com");
                }
                Intent intent = new Intent(getApplicationContext(),
                        DeviceSetUpActivityListDesign.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Crate StandAlone UI
     *
     * @param savedInstanceState
     */
    private void createStandAlone(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_bp_login_screen);

        TextView txtnext = findViewById(R.id.txtnext);
        txtnext.setOnClickListener(v -> {
            Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(objIntent);
        });

        ViewGroup continue_guest = (ViewGroup) findViewById(R.id.continue_layout);

        continue_guest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                                DashboardActivity.class);
                        startActivity(intent1);
                        return;
                    }
                }

                Intent intent1 = new Intent(getApplicationContext(),
                        DashboardActivity.class);
                startActivity(intent1);
            }
        });
    }


    @Override
    public void onClick(View v) {

    }
}
