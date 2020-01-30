package com.abhaybmicoc.app.oximeter;

import android.util.Log;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.Handler;
import android.app.Activity;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;

import com.abhaybmicoc.app.R;
import com.lidroid.xutils.ViewUtils;
import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.choicemmed.c208blelibrary.utils.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.abhaybmicoc.app.activity.BloodPressureActivity;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.choicemmed.c208blelibrary.Device.C208Device;
import com.choicemmed.c208blelibrary.cmd.invoker.C208Invoker;
import com.choicemmed.c208blelibrary.cmd.listener.C208BindDeviceListener;
import com.choicemmed.c208blelibrary.cmd.listener.C208ConnectDeviceListener;

import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    // region Variables

    Context context = MainActivity.this;

    private boolean flag = true;
    private static final int RECEIVE_SPO_PR = 1;

    private String txt = "";
    private String macAddress = "";
    private static final String TAG = "MainActivity";
    public static final String MAC_ADDRESS_KEY = "MAC_ADDRESS_KEY";

    @ViewInject(R.id.tv_pulse_rate)
    private TextView tvPulseRate;
    @ViewInject(R.id.tv_body_oxygen)
    private TextView tvBodyOxygen;
    @ViewInject(R.id.tv_body_oxygen_label)
    private TextView tvBodyOxygenLabel;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvTemperature;
    private TextView tvMobileNumber;

    private ProgressDialog progressDialog;

    private Button btnNext;
    private Button btnSkip;
    private Button btnRepeat;
    private Button btnConnect;

    @ViewInject(R.id.btn_start_test)
    private Button btnStartTest;
    @ViewInject(R.id.btn_connect_device)
    private Button btnConnectDevice;
    @ViewInject(R.id.btn_disconnect_device)
    private Button btnDisconnectDevice;

    private C208Invoker c208Invoker;
    private TextToSpeech textToSpeech;
    private BluetoothAdapter mBluetoothAdapter;

    private SharedPreferences shared;
    private AlertDialog timeOutAlertDialog;
    AlertDialog.Builder timeOutAlertDialogBuilder;

    Handler deviceConnectionTimeoutHandler;
    Runnable connectionTimeoutRunnable;
    private int DEVICE_CONNECTION_WAITING_TIME = 1000 * 25;

    private BluetoothAdapter bluetoothAdapter;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECEIVE_SPO_PR:

                    tvBodyOxygenLabel.setVisibility(View.VISIBLE);
                    tvPulseRate.setText("Pulse rate: " + msg.arg2);
                    tvBodyOxygen.setText(msg.arg1 + " %");

                    btnNext.setText("Next");

                    if (deviceConnectionTimeoutHandler != null)
                        deviceConnectionTimeoutHandler.removeCallbacks(connectionTimeoutRunnable);

                    btnConnect.setText("Connected");
                    btnConnect.setBackground(getResources().getDrawable(R.drawable.greenback));

                    progressDialog.dismiss();
                    writeToSharedPreference(ApiUtils.PREFERENCE_PULSE, Constant.Fields.PULSE_RATE, String.valueOf(msg.arg2));
                    writeToSharedPreference(ApiUtils.PREFERENCE_PULSE, Constant.Fields.BLOOD_OXYGEN, String.valueOf(msg.arg1));
                    break;
            }
        }

    };

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
    protected void onPause() {
        super.onPause();

        stopTextToSpeech();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onInit(int status) {
        startTextToSpeech(status);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStop() {
        super.onStop();
        c208Invoker = new C208Invoker(this);
    }

    // endregion

    // region Initialization methods

    /* Request enabling bluetooth if not enabled */
    private void turnOnBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }

    private void setupUI() {
        setContentView(R.layout.activity_pulse_oximeter_main);

        ViewUtils.inject(this);

        c208Invoker = new C208Invoker(this);
        textToSpeech = new TextToSpeech(getApplicationContext(), this);
        macAddress = SharePreferenceUtil.get(this, MAC_ADDRESS_KEY, "").toString();

        txt = "Put Finger inside the Device and Click Start Test Button";
        speakOut(txt);

        this.setFinishOnTouchOutside(false);

        shared = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);

        tvHeight = findViewById(R.id.tv_header_height);
        tvWeight = findViewById(R.id.tv_header_weight);
        tvTemperature = findViewById(R.id.tv_header_tempreture);

        btnNext = findViewById(R.id.btn_Next);
        btnSkip = findViewById(R.id.btn_skip);
        btnRepeat = findViewById(R.id.btn_repeat);
        btnConnect = findViewById(R.id.btn_connect);
    }

    /**
     *
     */
    private void setupEvents() {
        tvHeight.setOnClickListener(view -> handleHeight());
        tvWeight.setOnClickListener(view -> handleWeight());
        tvTemperature.setOnClickListener(view -> handleTemperature());

        btnRepeat.setOnClickListener(view -> handleRepeat());
        btnNext.setOnClickListener(v -> {
            writeData();
        });

        btnSkip.setOnClickListener(v -> {
            Intent objIntent = new Intent(getApplicationContext(), BloodPressureActivity.class);
            startActivity(objIntent);
            finish();
        });

        btnStartTest.setOnClickListener(view -> bindDevice());
        btnConnectDevice.setOnClickListener(view -> connectDevice());
        btnDisconnectDevice.setOnClickListener(view -> disconnectDevice());

        btnConnect.setOnClickListener(v -> {
            if (btnConnect.getText().toString().equals("Connect")) {
                bindDevice();
            }
        });
    }

    private void initializeData() {
        tvName.setText("Name : " + shared.getString(Constant.Fields.NAME, ""));
        tvGender.setText("Gender : " + shared.getString(Constant.Fields.GENDER, ""));
        tvAge.setText("DOB : " + shared.getString(Constant.Fields.DATE_OF_BIRTH, ""));
        tvMobileNumber.setText("Phone : " + shared.getString(Constant.Fields.MOBILE_NUMBER, ""));

        bindDevice();
    }

    // endregion

    // region Logical methods

    /**
     * @param status
     */
    private void startTextToSpeech(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(txt);
            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    /**
     *
     */
    private void stopTextToSpeech() {
        /* close the textToSpeech engine to avoid the runtime exception from it */
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        } catch (Exception e) {
            System.out.println("onPauseException" + e.getMessage());
        }
    }

    /**
     * @param text
     */
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     *
     */
    private void handleHeight() {
        context.startActivity(new Intent(this, HeightActivity.class));
    }

    /**
     *
     */
    private void handleWeight() {
        context.startActivity(new Intent(this, ActofitMainActivity.class));
    }

    /**
     *
     */
    private void handleTemperature() {
        context.startActivity(new Intent(this, ThermometerScreen.class));
    }

    /**
     *
     */
    private void handleRepeat() {
        tvBodyOxygenLabel.setVisibility(View.GONE);
        tvBodyOxygen.setText("spo");
        tvPulseRate.setText(R.string.pr);
    }

    /**
     *
     */
    private void bindDevice() {
        progressDialog = Tools.progressDialog(MainActivity.this);
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false);
        progressDialog.dismiss();

        tvPulseRate.setText("Pulse rate");
        tvBodyOxygen.setText("spo");
        tvBodyOxygenLabel.setVisibility(View.GONE);

        btnConnect.setText("Connecting...");
        btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));

        c208Invoker.bindDevice(new BindDeviceAdapter());
    }

    private void enableBlutooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }

    /**
     *
     */
    private void connectDevice() {
        if ("".equals(macAddress)) {
            Toast.makeText(this, "Please bind the device first！！", Toast.LENGTH_SHORT).show();
            return;
        }
        C208Device device = new C208Device();
        device.setDeviceMacAddress(macAddress);

        c208Invoker.connectDevice(device, new ConnectDeviceAdapter());
    }

    /**
     *
     */
    private void disconnectDevice() {
        c208Invoker.disconnectDevice(() -> {
        });
    }

    private void writeToSharedPreference(String preferenceName, String key, String value) {
        SharedPreferences sharedPreference = getSharedPreferences(preferenceName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(key, value);
        editor.commit();
    }


    private class BindDeviceAdapter implements C208BindDeviceListener {

        /* This method is called when we receive data from device */
        @Override
        public void onDataResponse(int spo, int pr) {
            flag = false;

            Log.e("data_Res_Log", "Received");

            Message message = new Message();
            message.arg1 = spo;
            message.arg2 = pr;
            message.what = RECEIVE_SPO_PR;

            if (timeOutAlertDialog != null && !timeOutAlertDialog.isShowing()) {
                handler.sendMessage(message);
            } else if (timeOutAlertDialog == null) {
                handler.sendMessage(message);
            }
        }

        @Override
        public void onError(String message) {

            showAlertDialog("Test Failure", "Test unsuccessful, try again.");

//            Toast.makeText(context, "Test unsuccessful, try again.", Toast.LENGTH_SHORT).show();
            btnNext.setText("Skip");
            progressDialog.dismiss();

            if (deviceConnectionTimeoutHandler != null)
                deviceConnectionTimeoutHandler.removeCallbacks(connectionTimeoutRunnable);

            btnConnect.setText("Connect");
            btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
        }

        @Override
        public void onStateChanged(int oldState, int newState) {
        }

        @Override
        public void onBindDeviceSuccess(C208Device c208Device) {
            macAddress = c208Device.getDeviceMacAddress();

            SharePreferenceUtil.put(MainActivity.this, MAC_ADDRESS_KEY, macAddress);
        }

        @Override
        public void onBindDeviceFail(String failMessage) {
            // This case will call when device is not active
            if (failMessage.equals("蓝牙绑定失败，请检查设备蓝牙是否可见！")) {
                if (tvBodyOxygenLabel.getVisibility() == View.GONE) {
                    showAlertDialog("Connection Fail", "Device is not active, Check device and try again...");

//                Toast.makeText(context, "Device is not active, Check device and try again...", Toast.LENGTH_SHORT).show();

                    btnNext.setText("Skip");
                    progressDialog.dismiss();

                    btnConnect.setText("Connect");
                    btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
                }
            } else if (failMessage.equals("蓝牙绑定失败，获取设备SN异常！")) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        btnConnect.setText("Connected");
                        btnConnect.setBackground(getResources().getDrawable(R.drawable.greenback));
                        progressDialog.show();
                    }
                });
                setDeviceConnectionTimeoutHandler();
            }
        }
    }

    private class ConnectDeviceAdapter implements C208ConnectDeviceListener {
        @Override
        public void onDataResponse(int spo, int pr) {
            Message message = new Message();
            message.arg1 = spo;
            message.arg2 = pr;
            message.what = RECEIVE_SPO_PR;

            if (!timeOutAlertDialog.isShowing())
                handler.sendMessage(message);
        }

        @Override
        public void onError(String message) {
        }

        @Override
        public void onStateChanged(int oldState, int newState) {
        }

        @Override
        public void onConnectedDeviceSuccess() {
            LogUtils.d(TAG, "onConnectedDeviceSuccess");
        }

        @Override
        public void onConnectedDeviceFail(String failMessage) {
            if (failMessage.equals("蓝牙绑定失败，请检查设备蓝牙是否可见！")) {
                btnNext.setText("Skip");
                progressDialog.dismiss();
            } else if (failMessage.equals("蓝牙绑定失败，获取设备SN异常！")) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        setDeviceConnectionTimeoutHandler();
                    }
                });
            }
        }
    }

    private void setDeviceConnectionTimeoutHandler() {
        try {
            deviceConnectionTimeoutHandler = new Handler(Looper.getMainLooper());
            connectionTimeoutRunnable = new Runnable() {

                @Override
                public void run() {
                    btnNext.setText("Skip");

                    btnConnect.setText("Connect");
                    btnConnect.setBackground(getResources().getDrawable(R.drawable.repeat));
                    progressDialog.dismiss();

                    showAlertDialog("Connectivity Lost!", "Device is not active, try again");
                }
            };

            deviceConnectionTimeoutHandler.postDelayed(connectionTimeoutRunnable, DEVICE_CONNECTION_WAITING_TIME);
        } catch (Exception e) {
        }
    }

    private void showAlertDialog(String title, String msg) {

        timeOutAlertDialogBuilder = new AlertDialog.Builder(context);
        timeOutAlertDialogBuilder.setTitle(title);
        timeOutAlertDialogBuilder.setMessage(msg).
                setCancelable(false)
                .setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        bindDevice();
                    }
                });

        timeOutAlertDialogBuilder.setNegativeButton("Skip Test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                writeData();
            }
        });

        /* create alert dialog */
        timeOutAlertDialog = timeOutAlertDialogBuilder.create();
        /* show alert dialog */


        if (tvBodyOxygenLabel.getVisibility() == View.GONE) {
            if (!((Activity) context).isFinishing())
                timeOutAlertDialog.show();

        }

    }

    private void writeData() {
        Intent objIntent = new Intent(getApplicationContext(), BloodPressureActivity.class);
        startActivity(objIntent);
        finish();
    }
// endregion
}


