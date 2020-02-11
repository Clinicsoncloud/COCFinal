package com.abhaybmicoc.app.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
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

public class HeightActivity extends Activity implements TextToSpeech.OnInitListener {
    // region Variables

    private Context context;

    private String str = "";
    private String message = "";
    private String strHeight = "";
    private String state = "Connect";

    private String state2;
    private String txtSpeak;

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
    private Button btnGetHeight;

    private EditText etManualHeight;
    private EditText etBluetoothLogs;

    private InputStream inputStreamHeightReceiver;
    private OutputStream outputStreamHeightReceiver;

    private BluetoothSocket socket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;

    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferencePersonalData;
    private SharedPreferences sharedPreferenceBluetoothAddress;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvMobileNumber;

    private TextToSpeech textToSpeech;

    private int CONNECTION_TRY_COUNT = 0;
    private int ALLOWED_BLUETOOTH_CONNECT_TRY_COUNT = 1;

    //milliseconds declaration
    //It will take break of 5s while trying to reconnect
    private int CONNECT_TRY_PAUSE_MILLISECONDS = 5000;

    // endregion

    @Override
    public void onInit(int status) {
        startTextToSpeech(status);
    }

    @Override
    public void onBackPressed() {

        startActivity(new Intent(HeightActivity.this, OtpLoginScreen.class));
        finish();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
    }

    private String getDeviceAddress() {
        return sharedPreferenceBluetoothAddress.getString("hcbluetooth", "");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            initializeBluetooth();

            Log.e("ACtivity_Result", ":" + strBluetoothTurnedOn);
            Toast.makeText(this, strBluetoothTurnedOn, Toast.LENGTH_LONG).show();
            return;
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();


//        connectToDevice();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();

        /* close the textToSpeech engine to avoide the runtime exception from it */
        stopTextToSpeech();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();

        if (state.equals(strDisconnect)) {

            state = strConnect;
            strEnabled = "false";
            btnConnect.setText(strConnect);

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
    }

    /* Request enabling bluetooth if not enabled */
    private void turnOnBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnConnect.setClickable(false);
        btnConnect.setBackground(getResources().getDrawable(R.drawable.grayback));
        btnConnect.setText("Connecting...");

//        if (!bluetoothAdapter.isEnabled()) {
        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
//        }
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
        btnGetHeight = findViewById(R.id.btn_get_height);

        // TODO: Rename from AUTO_CONNECT
        sharedPreferenceBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);
        sharedPreferencePersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

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

    private void initializeData() {
        btnClean.setText("Clean");

        etBluetoothLogs.setFocusable(false);
        etBluetoothLogs.setText(">:Bluetooth Terminal\n");
        etBluetoothLogs.setTextColor(getResources().getColor(R.color.white));
        etBluetoothLogs.setBackgroundColor(getResources().getColor(R.color.black));

        tvName.setText("Name : " + sharedPreferencePersonalData.getString(Constant.Fields.NAME, ""));
        tvGender.setText("Gender : " + sharedPreferencePersonalData.getString(Constant.Fields.GENDER, ""));
        tvAge.setText("DOB : " + sharedPreferencePersonalData.getString(Constant.Fields.DATE_OF_BIRTH, ""));
        tvMobileNumber.setText("Phone : " + sharedPreferencePersonalData.getString(Constant.Fields.MOBILE_NUMBER, ""));

        /*if (state.equals("Connect")) {
            btnConnect.setText(strConnect);
        }*/

        textToSpeech = new TextToSpeech(getApplicationContext(), this);

        turnOnBluetooth();
    }

    private void initializeBluetooth() {
        Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();

        if (bluetoothDevices.size() > 0) {
            for (BluetoothDevice device : bluetoothDevices) {
                if (device.getName().equals("HC-05")) {
                    storeBluetoothInformation(device);

                    Log.e("Device_Found", ":" + device.getAddress() + "   :Name:   " + device.getName());
                    new Connect().execute(new String[]{device.getAddress()});
                }
            }
        } else {
            btnConnect.setClickable(true);
            btnConnect.setText("Connect");
            btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
            Toast.makeText(context, "Cannot connect to bluetooth device", Toast.LENGTH_SHORT);
            // TODO: Handle device not found
        }
    }

    private void storeBluetoothInformation(BluetoothDevice device) {
        sharedPreferenceBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferenceBluetoothAddress.edit();
        if (sharedPreferenceBluetoothAddress.getString("hcbluetooth", "").equalsIgnoreCase("")) {
            editor.putString("hcbluetooth", device.getAddress());
            editor.commit();
        }
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

//            txtSpeak = "Please Enter Manual Height";
            txtSpeak = "कृपया स्वतः height टाका ";
            speakOut(txtSpeak);
        } else {
            Intent objIntent = new Intent(getApplicationContext(), ActofitMainActivity.class);

            objIntent.putExtra(Constant.Fields.HEIGHT, etManualHeight.getText().toString());
            objIntent.putExtra(Constant.Fields.ID, sharedPreferencePersonalData.getString(Constant.Fields.ID, ""));
            objIntent.putExtra(Constant.Fields.NAME, sharedPreferencePersonalData.getString(Constant.Fields.NAME, ""));
            objIntent.putExtra(Constant.Fields.GENDER, sharedPreferencePersonalData.getString(Constant.Fields.GENDER, ""));
            objIntent.putExtra(Constant.Fields.DATE_OF_BIRTH, sharedPreferencePersonalData.getString(Constant.Fields.DATE_OF_BIRTH, ""));

            writeToSharedPreferences(Constant.Fields.HEIGHT, etManualHeight.getText().toString());

            startActivity(objIntent);
            finish();
        }
    }

    private void showCannotConnectToDevice() {
        // Reset connect try count to 0
        CONNECTION_TRY_COUNT = 0;

        state = strConnect;
        state2 = strConnect;
        strEnabled = "false";

        btnConnect.setClickable(true);
        btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
        btnConnect.setText("Connect");

//        txtSpeak = "No Bluetooth Device Found Please Connect it Manually";
        txtSpeak = "हाइट सेन्सर कनेक्ट झाला नाही मॅनुअल्ली कनेक्ट करा";
        speakOut(txtSpeak);
    }

    /**
     * @param text
     */
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * @param status
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startTextToSpeech(int status) {
        if (status == TextToSpeech.SUCCESS) {
//            int result = textToSpeech.setLanguage(Locale.US);
            int result = textToSpeech.setLanguage(Locale.forLanguageTag("mar"));

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
                // TODO: Handle this instead of logging
            } else {
                speakOut(txtSpeak);
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
            // TODO: Handle this instead of logging
        }
    }

    /**
     *
     */
    private void stopTextToSpeech() {
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        } catch (Exception e) {
            Log.e("TTS", "Stopping text to speech Failed!");
        }
    }

    /**
     *
     */
    private void closeConnection() {
        if (inputStreamHeightReceiver != null) {
            try {
                inputStreamHeightReceiver.close();
            } catch (Exception e) {
                Log.e("", "" + e.getMessage());
            }

            inputStreamHeightReceiver = null;
        }

        if (outputStreamHeightReceiver != null) {
            try {
                outputStreamHeightReceiver.close();
            } catch (Exception e) {
                Log.e("", "" + e.getMessage());
            }

            outputStreamHeightReceiver = null;
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                Log.e("", "" + e.getMessage());
            }

            socket = null;
        }
    }

    /**
     *
     */
    private void writeToSharedPreferences(String key, String value){
        SharedPreferences sharedPreference = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(key, value);
        editor.commit();
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

            Log.e("Connect_result", ":" + result);

            Toast.makeText(HeightActivity.this, result, Toast.LENGTH_SHORT).show();

            if (result.equals(strConnected)) {

                strEnabled = "true";
                state = strDisconnect;
                state2 = strDisconnect;

                btnConnect.setClickable(false);
                btnConnect.setText("Connected");
                btnConnect.setBackground(getResources().getDrawable(R.drawable.greenback));
                speakOut("हाइट सेन्सर च्या खाली थांबा आणि गेट हाइट वर क्लिक करा");

                new Receiver().execute(new String[]{strEnabled});
            } else {
                Toast.makeText(HeightActivity.this, "Unable to connect device, try again.", Toast.LENGTH_SHORT).show();

                txtSpeak = "हाइट सेन्सर कनेक्ट झाला नाही मॅनुअल्ली कनेक्ट करा";
                speakOut(txtSpeak);

                btnConnect.setClickable(true);
                btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
                btnConnect.setText("Connect");
            }
        }
    }

// endregion
}
