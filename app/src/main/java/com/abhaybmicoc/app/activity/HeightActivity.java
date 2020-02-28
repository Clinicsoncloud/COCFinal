package com.abhaybmicoc.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.services.SharedPreferenceManager;
import com.abhaybmicoc.app.services.TextToSpeechService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.ErrorUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class HeightActivity extends Activity {
    // region Variables

    private Context context;

    private String str = "";
    private String message = "";
    private String strHeight = "";
    private String state = "Connect";

    private String state2;
    private String strEnabled;
    private String strConnect;
    private String strConnected;
    private String strCannotSend;
    private String strDisconnect;
    private String strMissedConnection;
    private String strBluetoothTurnedOn;

    private int adjustedHeight;

    private Button btnNext;
    private Button btnClean;
    private Button btnConnect;
    private TextView tv_ConnectedText;
    private Button btnGetHeight;

    private EditText etManualHeight;
    private EditText etBluetoothLogs;

    private InputStream inputStreamHeightReceiver;
    private OutputStream outputStreamHeightReceiver;

    private BluetoothSocket socket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;

    private ProgressDialog progressDialog;

    private SharedPreferenceManager sharedPreferenceManager;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvMobileNumber;

    private String SUCCESS_MSG = "";
    private String FAILURE_MSG = "";
    private String MANUAL_MSG = "";
    private String msg = "";
    private TextToSpeechService textToSpeechService;

    // endregion

    @Override
    public void onBackPressed() {

        startActivity(new Intent(HeightActivity.this, OtpLoginScreen.class));
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        initializeData();
        setupEvents();
    }


    private String getDeviceAddress() {
        return sharedPreferenceManager.getHeightBtDeviceAddress();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            initializeBluetooth();

            Toast.makeText(this, strBluetoothTurnedOn, Toast.LENGTH_LONG).show();
            return;
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }


    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();

        if (state.equals(strDisconnect)) {

            state = strConnect;
            strEnabled = "false";
            btnConnect.setText(strConnect);
            tv_ConnectedText.setText(strConnect);

            try {
                socket.close();
            } catch (IOException e) {
                ErrorUtils.logErrors(e, "HeightActivity.java", "onStop()", "Not connected");
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        turnOnBluetooth();
        super.onRestart();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();

        if (textToSpeechService != null)
            textToSpeechService.stopTextToSpeech();
    }

    /* Request enabling bluetooth if not enabled */
    private void turnOnBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnConnect.setClickable(false);
        btnConnect.setBackground(getResources().getDrawable(R.drawable.grayback));
        btnConnect.setText("Connecting...");
        tv_ConnectedText.setText("Connecting...");

        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
    }

    // region Initialization methods

    /**
     *
     */
    private void setupUI() {
        setContentView(R.layout.activity_height_screen);

        strConnect = (String) getText(R.string.connect);
        strConnected = (String) getText(R.string.connected);
        strCannotSend = (String) getText(R.string.cannotSend);
        strDisconnect = (String) getText(R.string.disconnect);
        etBluetoothLogs = findViewById(R.id.et_bluetooth_logs);
        strMissedConnection = (String) getText(R.string.missedConnection);
        strBluetoothTurnedOn = (String) getText(R.string.bluetoothTurnedOn);

        btnNext = findViewById(R.id.btn_skip);
        btnClean = findViewById(R.id.btn_clean);
        btnConnect = findViewById(R.id.btn_connect);
        tv_ConnectedText = findViewById(R.id.tv_ConnectedText);
        btnGetHeight = findViewById(R.id.btn_get_height);

        SUCCESS_MSG = getResources().getString(R.string.height_success);
        FAILURE_MSG = getResources().getString(R.string.height_fail);
        MANUAL_MSG = getResources().getString(R.string.height_manually);

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        etManualHeight = findViewById(R.id.et_manual_height);
    }

    private void setupEvents() {
        btnClean.setOnClickListener(view -> clean());
        btnNext.setOnClickListener(view -> gotoNext());
        btnConnect.setOnClickListener(view -> connect());
        btnGetHeight.setOnClickListener(view -> getHeight());
    }

    @SuppressLint("SetTextI18n")
    private void initializeData() {
        btnClean.setText("Clean");
        textToSpeechService = new TextToSpeechService(getApplicationContext(), "");

        sharedPreferenceManager = SharedPreferenceManager.getInstance(context);

        etBluetoothLogs.setFocusable(false);
        etBluetoothLogs.setText(">:Bluetooth Terminal\n");
        etBluetoothLogs.setTextColor(getResources().getColor(R.color.white));
        etBluetoothLogs.setBackgroundColor(getResources().getColor(R.color.black));

        tvName.setText("Name : " + sharedPreferenceManager.getPatientName());
        tvGender.setText("Gender : " + sharedPreferenceManager.getPatientGender());
        tvAge.setText("DOB : " + sharedPreferenceManager.getDateOfBirth());
        tvMobileNumber.setText("Phone : " + sharedPreferenceManager.getMobileNumber());

        turnOnBluetooth();
    }

    private void initializeBluetooth() {
        Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();

        if (bluetoothDevices.size() > 0) {
            for (BluetoothDevice device : bluetoothDevices) {
                if (device.getName().equals("HC-05")) {
                    storeBluetoothInformation(device);

                    new Connect().execute(new String[]{device.getAddress()});
                }
            }
        } else {
            // TODO: Handle device not found

            btnConnect.setClickable(true);
            btnConnect.setText("Connect");
            tv_ConnectedText.setText("Connect");
            btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
            Toast.makeText(context, "Cannot connect to bluetooth device", Toast.LENGTH_SHORT);
        }
    }

    private void storeBluetoothInformation(BluetoothDevice device) {

        sharedPreferenceManager.setHeightBtDeviceAddress(device.getAddress());
        sharedPreferenceManager.setHeightBtDeviceName(device.getName());
    }

    // region Logical methods

    /**
     *
     */
    private void connect() {
        turnOnBluetooth();
    }

    /**
     *
     */
    private void getHeight() {

        if (state.equals(strConnect)) {
            Toast.makeText(HeightActivity.this, "Connecting to device...", Toast.LENGTH_SHORT).show();
            new Connect().execute(new String[]{getDeviceAddress()});
            return;
        }

        etBluetoothLogs.append(">:" + "1" + "\n");

        strHeight = "";
        String env = "1";

        try {
            if (etManualHeight.getText().length() > 0) {
                etManualHeight.setText("");
                outputStreamHeightReceiver.write(env.getBytes(Charset.forName("UTF-8")));
            } else {
                outputStreamHeightReceiver.write(env.getBytes(Charset.forName("UTF-8")));
            }
        } catch (IOException e) {
            ErrorUtils.logErrors(e, "HeightActivity.java", "getHeight()", "Get height failed");
            Toast.makeText(HeightActivity.this, strCannotSend, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void clean() {
        etManualHeight.setText("");
    }

    /**
     *
     */
    private void gotoNext() {
        if (etManualHeight.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter Manual Height", Toast.LENGTH_SHORT).show();
            textToSpeechService.speakOut(MANUAL_MSG);

        } else {
            Intent objIntent = new Intent(getApplicationContext(), ActofitMainActivity.class);

            objIntent.putExtra(Constant.Fields.HEIGHT, etManualHeight.getText().toString());
            objIntent.putExtra(Constant.Fields.ID, sharedPreferenceManager.getPatientId());
            objIntent.putExtra(Constant.Fields.NAME, sharedPreferenceManager.getPatientName());
            objIntent.putExtra(Constant.Fields.GENDER, sharedPreferenceManager.getPatientGender());
            objIntent.putExtra(Constant.Fields.DATE_OF_BIRTH, sharedPreferenceManager.getDateOfBirth());

            writeToSharedPreferences(etManualHeight.getText().toString());

            startActivity(objIntent);
            finish();
        }
    }

    /**
     *
     */
    private void writeToSharedPreferences(String value) {
        sharedPreferenceManager.setHeight(value);
    }

    // endregion

    // region Nested classes

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
            str = params[0];
            str = str.replace("�", "");

            strHeight += str;

            try {
                if (!strHeight.equalsIgnoreCase("")) {
                    adjustedHeight = Integer.parseInt(strHeight) - 1;
                    etManualHeight.setText(String.valueOf(adjustedHeight));
                }
            } catch (Exception e) {
                ErrorUtils.logErrors(e, "HeightActivity.java", "onProgressUpdate()", "Failed to adjust height");
            }

            if (params[1].equals("false")) {
                state = strConnect;
                state2 = strConnect;
                strEnabled = "false";

                etBluetoothLogs.append(">:" + strMissedConnection + "\n");
            } else {
                strEnabled = "true";
                state2 = strDisconnect;
                state = strDisconnect;
            }
        }
    }

    private class Connect extends AsyncTask<String, String, String> {
        public Connect() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(HeightActivity.this);
            progressDialog.setMessage("Connecting..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... deviceAddresses) {
            String deviceAddress = deviceAddresses[0];

            if (deviceAddress.trim().length() == 0) {
                return "";
            }

            bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);

            try {
                socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

                if (!socket.isConnected()) {
                    socket.connect();
                }

                inputStreamHeightReceiver = socket.getInputStream();
                outputStreamHeightReceiver = socket.getOutputStream();

                return strConnected;
            } catch (Exception e) {
                ErrorUtils.logErrors(e, "HeightActivity.java", "doInBackground", "failed to connect with socket");
                return "";
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            Toast.makeText(HeightActivity.this, result, Toast.LENGTH_SHORT).show();

            Log.e("result_Logs", ":" + result);

            if (result.equals(strConnected)) {

                strEnabled = "true";
                state = strDisconnect;
                state2 = strDisconnect;

                btnConnect.setClickable(false);
                btnConnect.setText("Connected");
                tv_ConnectedText.setText("Connected");
                btnConnect.setBackground(getResources().getDrawable(R.drawable.greenback));

                textToSpeechService.speakOut(SUCCESS_MSG);

                new Receiver().execute(new String[]{strEnabled});
            } else {
                Toast.makeText(HeightActivity.this, "Unable to connect device, try again.", Toast.LENGTH_SHORT).show();

                textToSpeechService.speakOut(FAILURE_MSG);

                btnConnect.setClickable(true);
                btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
                btnConnect.setText("Connect");
                tv_ConnectedText.setText("Connect");
            }
        }
    }
// endregion
}
