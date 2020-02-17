package com.abhaybmicoc.app.thermometer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.AsyncTask;
import android.content.Intent;
import android.content.Context;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.bluetooth.BluetoothAdapter;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.activity.BloodPressureActivity;
import com.abhaybmicoc.app.glucose.GlucoseScanListActivity;
import com.abhaybmicoc.app.services.TextToSpeechService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.oximeter.MainActivity;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.ErrorUtils;

import java.util.Set;
import java.util.UUID;
import java.util.Locale;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static com.abhaybmicoc.app.utils.ApiUtils.PREFERENCE_THERMOMETERDATA;

public class ThermometerScreen extends AppCompatActivity {
    // region Variables

    Context context = ThermometerScreen.this;

    private String str = "";
    private String strReceiveData = "";

    private String message;
    private String txtSpeak;
    private String strConnect;
    private String strEnabled;
    private String strConnected;
    private String strCannotSend;
    private String strDisconnect;
    private String estadoBoton2;
    private String strBluetoothTurnedOn;

    private ArrayList listLinkedDevices;
    private ArrayList listDeviceAddresses;

    private ArrayAdapter<String> adapterDevices;

    private BluetoothSocket socket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;

    private Button btnBaud;
    private Button btnConnect;
    private Button btnGetTemperature;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvMobile;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvPulseOximeter;
    private TextView tvBloodPressure;

    private EditText etTemperature;

    private Spinner spinner;

    private InputStream inputStreamHeightReceiver;
    private OutputStream outputStreamHeightReceiver;

    private ProgressDialog progressDialog;

    private SharedPreferences sharePreferenceThermometer;
    private SharedPreferences sharedPreferencePersonalData;
    private SharedPreferences sharedPreferenceBluetoothAddress;

    private TextToSpeech textToSpeech;

    private Handler deviceConnectionTimeoutHandler;

    private int DEVICE_CONNECTION_WAITING_TIME = 10000;

    private String SUCCESS_MSG = "";
    private String FAILURE_MSG = "";
    private String MANUAL_MSG = "";

    TextToSpeechService textToSpeechService;

    // endregion

    // region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
        turnOnBluetooth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        handleTemperatureResult(requestCode, resultCode, data);
//        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        closeBluetooth();
    }

    // endregion

    // region Initialization data

    private void setupUI() {
        setContentView(R.layout.activity_main_temperature);

        sharedPreferencePersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        etTemperature = findViewById(R.id.et_temprature);

        SUCCESS_MSG = getResources().getString(R.string.temperature_success_msg);
        FAILURE_MSG = getResources().getString(R.string.temperature_fail_msg);
        MANUAL_MSG = getResources().getString(R.string.temperature_manually);

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvMobile = findViewById(R.id.tv_mobile_number);

        btnConnect = findViewById(R.id.btn_connect_temperature);
        btnGetTemperature = findViewById(R.id.btn_get_temperature);

        tvName.setText("Name : " + sharedPreferencePersonalData.getString(Constant.Fields.NAME, ""));
        tvGender.setText("Gender : " + sharedPreferencePersonalData.getString(Constant.Fields.GENDER, ""));
        tvAge.setText("DOB : " + sharedPreferencePersonalData.getString(Constant.Fields.DATE_OF_BIRTH, ""));
        tvMobile.setText("Phone : " + sharedPreferencePersonalData.getString(Constant.Fields.MOBILE_NUMBER, ""));

        tvHeight = findViewById(R.id.tv_header_height);
        tvWeight = findViewById(R.id.tv_header_weight);
        tvPulseOximeter = findViewById(R.id.tv_header_pulseoximeter);
        tvBloodPressure = findViewById(R.id.tv_header_bloodpressure);

        btnBaud = findViewById(R.id.btn_skip);

        etTemperature.setVisibility(View.VISIBLE);

        listDeviceAddresses = new ArrayList();
        listLinkedDevices = new ArrayList();

        spinner = findViewById(R.id.sp_temprature);
        adapterDevices = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listLinkedDevices);
        spinner.setAdapter(adapterDevices);
    }

    private void setupEvents() {
        /* Add event for top weight and height */
        tvHeight.setOnClickListener(view -> {
            context.startActivity(new Intent(this, HeightActivity.class));
            finish();
        });

        tvWeight.setOnClickListener(view -> {
            context.startActivity(new Intent(this, ActofitMainActivity.class));
            finish();
        });

        tvPulseOximeter.setOnClickListener(view -> {
            handleOximeter();
        });
        tvBloodPressure.setOnClickListener(view -> {
            handleBloodPressure();
        });

        btnBaud.setOnClickListener(view -> handleBaud());
        btnConnect.setOnClickListener(view -> turnOnBluetooth());

        // TODO: What this code is doing?
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void handleBloodPressure() {
        context.startActivity(new Intent(this, BloodPressureActivity.class));
        finish();
    }

    private void handleOximeter() {
        context.startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void initializeData() {

        textToSpeechService = new TextToSpeechService(getApplicationContext(), "");

        sharePreferenceThermometer = getSharedPreferences(PREFERENCE_THERMOMETERDATA, MODE_PRIVATE);

        strConnect = (String) getText(R.string.connect);
        strConnected = (String) getText(R.string.connected);
        strDisconnect = (String) getText(R.string.disconnect);
        strCannotSend = (String) getText(R.string.cannotSend);
        strBluetoothTurnedOn = (String) getText(R.string.bluetoothTurnedOn);

        sharedPreferenceBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);

        if (strConnect.equals("Connect")) {
            btnConnect.setText("Connect");
            btnConnect.setClickable(true);
            btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));


        }
    }

    // endregion

    // region Logical methods


    private void handleBaud() {
        if (etTemperature.getText().length() > 0) {
            Intent objpulse = new Intent(getApplicationContext(), GlucoseScanListActivity.class);

            SharedPreferences.Editor editor = sharePreferenceThermometer.edit();
            editor.putString(Constant.Fields.TEMPERATURE, etTemperature.getText().toString().trim());
            editor.commit();

            startActivity(objpulse);
            closeBluetooth();
            finish();
        } else {
            Toast.makeText(ThermometerScreen.this, "Enter Manual temperature", Toast.LENGTH_SHORT).show();
            textToSpeechService.speakOut(MANUAL_MSG);
        }
    }

    /**
     *
     */
    private void connectToDevice() {
        if (strConnect.equals("Connect")) {
            turnOnBluetooth();

            /*new Connect(ThermometerScreen.this, null).execute(new String[]{sharedPreferenceBluetoothAddress.getString("hcthermometer", "")});
            strEnabled = "false";*/
            return;
        }
    }

    /**
     *
     */
    private void turnOnBluetooth() {

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnConnect.setClickable(false);
        btnConnect.setBackground(getResources().getDrawable(R.drawable.grayback));
        btnConnect.setText("Connecting...");

        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
    }

    /**
     * @param devices
     */
    private void storeBluetoothDevices(Set<BluetoothDevice> devices) {

        if (devices != null && devices.size() > 0) {
            for (BluetoothDevice device : devices) {
                if (device.getName().contains("THERMOMETER")) {

                    sharedPreferenceBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferenceBluetoothAddress.edit();

                    if (sharedPreferenceBluetoothAddress.getString("hcthermometer", "").equalsIgnoreCase("")) {
                        editor.putString("hcthermometer", device.getAddress());
                        editor.commit();
                    }

                    new Connect().execute(new String[]{device.getAddress()});

                    setDeviceConnectionTimeoutHandler();
                }
            }
        }
    }

    private void setDeviceConnectionTimeoutHandler() {
        deviceConnectionTimeoutHandler = new Handler();

        deviceConnectionTimeoutHandler.postDelayed(() -> {
//            if (dialogConnectionProgress != null && dialogConnectionProgress.isShowing()) {
//                dialogConnectionProgress.dismiss();

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                bluetoothAdapter.cancelDiscovery();

                btnConnect.setText("Connect");
                btnConnect.setClickable(true);
                btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
            }
//            }
        }, DEVICE_CONNECTION_WAITING_TIME);
    }


    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleTemperatureResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();

            if (devices != null && devices.size() > 0) {

                storeBluetoothDevices(devices);

                Toast.makeText(this, strBluetoothTurnedOn, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     *
     */
    private void closeBluetooth() {

        strEnabled = "false";
        strConnect = "Connect";

        btnConnect.setText(strConnect);
        btnConnect.setClickable(true);
        btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));

        btnConnect.setClickable(true);
        btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));

        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    // endregion

    // region Nested classes

    private class Connect extends AsyncTask<String, String, String> {
        private Connect() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Connecting..");
            if (!((Activity) context).isFinishing())
                progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            if (params[0].equalsIgnoreCase("")) {
                return "";
            }

            bluetoothDevice = bluetoothAdapter.getRemoteDevice(params[0]);
            try {
                socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                if (!socket.isConnected()) {
                    socket.connect();
                }

                inputStreamHeightReceiver = socket.getInputStream();
                outputStreamHeightReceiver = socket.getOutputStream();

                return "Connected";
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {

            if (!((Activity) context).isFinishing()) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }
            String message = result;
            if (result.equals("Connected"))
                message = strConnected;

            Toast.makeText(ThermometerScreen.this, message, Toast.LENGTH_SHORT).show();

            if (message.equals("Connected")) {

                strConnect = "Disconnect";
                estadoBoton2 = strDisconnect;
                strEnabled = "true";

                btnConnect.setText("Connected");
                btnConnect.setClickable(false);
                btnConnect.setBackground(getResources().getDrawable(R.drawable.greenback));

                //success msg
                textToSpeechService.speakOut(SUCCESS_MSG);

                new Receiver().execute(new String[]{strEnabled});
            } else {

                if (!((Activity) context).isFinishing()) {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                }

                Toast.makeText(ThermometerScreen.this, "Unable to connect device, try again.", Toast.LENGTH_SHORT).show();

                //failure msg
                textToSpeechService.speakOut(FAILURE_MSG);

                btnConnect.setClickable(true);
                btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
                btnConnect.setText("Connect");
            }
        }
    }

    public class Receiver extends AsyncTask<String, String, String> {
        public Receiver() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            message = "";
            byte[] buffer = new byte[128];

            while (strEnabled.equals("true")) {
                try {
                    int bytes = inputStreamHeightReceiver.read(buffer);
                    message = new String(buffer, 0, bytes);
                } catch (IOException e) {
                    message = "";
                    strEnabled = "false";
                    ErrorUtils.logErrors(e, "HeightActivity.java", "doInBackground()", "Failed to read bytes data");
                }

                publishProgress(new String[]{message, strEnabled});
            }

            return message;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(String... params) {
            // TODO: Check this logic

            str = params[0];

            str = str.replace("�", "");

            strReceiveData += str;

            etTemperature.setText("" + strReceiveData);

            strReceiveData = "";

            if (params[1].equals("false")) {
                strEnabled = "false";
                strConnect = "Connect";
                estadoBoton2 = strConnect;

                btnConnect.setText("Connect");
                btnConnect.setClickable(true);
                btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
            } else {
                strEnabled = "true";
                strConnect = "Connected";
                estadoBoton2 = strDisconnect;

                btnConnect.setText("Connected");
                btnConnect.setClickable(false);
                btnConnect.setBackground(getResources().getDrawable(R.drawable.greenback));
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (textToSpeechService != null)
            textToSpeechService.stopTextToSpeech();
    }

    // endregion
}
