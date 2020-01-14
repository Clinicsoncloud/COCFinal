package com.abhaybmicoc.app.oximeter;

import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.app.Activity;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.content.Context;
import android.app.ProgressDialog;
import android.annotation.SuppressLint;
import android.speech.tts.TextToSpeech;
import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.activity.DashboardActivity;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.choicemmed.c208blelibrary.Device.C208Device;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import com.choicemmed.c208blelibrary.cmd.invoker.C208Invoker;
import com.choicemmed.c208blelibrary.cmd.listener.C208BindDeviceListener;
import com.choicemmed.c208blelibrary.cmd.listener.C208ConnectDeviceListener;
import com.choicemmed.c208blelibrary.utils.LogUtils;

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

    @ViewInject(R.id.tv_pulse_rate) private TextView tvPulseRate;
    @ViewInject(R.id.tv_body_oxygen) private TextView tvBodyOxygen;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvMobile;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvTemperature;

    private ProgressDialog progressDialog;

    private Button btnNext;
    private Button btnSkip;
    private Button btnRepeat;
    
    @ViewInject(R.id.btn_start_test) private Button btnStartTest;
    @ViewInject(R.id.btn_connect_device) private Button btnConnectDevice;
    @ViewInject(R.id.btn_disconnect_device) private Button btnDisconnectDevice;

    private C208Invoker c208Invoker;
    private TextToSpeech textToSpeech;
    private BluetoothAdapter mBluetoothAdapter;

    private SharedPreferences shared;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECEIVE_SPO_PR:
                    tvPulseRate.setText("Pulse rate: " + msg.arg2);
                    tvBodyOxygen.setText("Body Oxygen：" + msg.arg1);

                    writeToSharedPreference(ApiUtils.PREFERENCE_PULSE, "pulse_rate", String.valueOf(msg.arg2));
                    writeToSharedPreference(ApiUtils.PREFERENCE_PULSE, "body_oxygen", String.valueOf(msg.arg1));
                    break;
            }
        }
    };

    // endregion

    // Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopTextToSpeech();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //reinitialize the textToSpeech engine

    }

    @Override
    public void onInit(int status) {
        startTextToSpeech(status);
    }

    // endregion

    // region Initialization methods

    private void setupUI(){
        setContentView(R.layout.activity_pulse_oximeter_main);

        ViewUtils.inject(this);

        c208Invoker = new C208Invoker(this);
        textToSpeech = new TextToSpeech(getApplicationContext(),this);
        macAddress = SharePreferenceUtil.get(this, MAC_ADDRESS_KEY, "").toString();

        txt = "Put Finger inside the Device and Click Start Test Button";
        speakOut(txt);

        this.setFinishOnTouchOutside(false);

        shared = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

//        enableBlutooth();

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.txtname);
        tvGender = findViewById(R.id.tv_gender);
        tvMobile = findViewById(R.id.txtmobile);

        tvHeight = findViewById(R.id.tv_header_height);
        tvWeight = findViewById(R.id.tv_header_weight);
        tvTemperature = findViewById(R.id.tv_header_tempreture);

        btnNext = findViewById(R.id.btn_next);
        btnSkip = findViewById(R.id.btn_skip);
        btnRepeat = findViewById(R.id.btn_repeat);
    }

    /**
     *
     */
    private void setupEvents(){
        tvHeight.setOnClickListener(view -> handleHeight());
        tvWeight.setOnClickListener(view -> handleWeight());
        tvTemperature.setOnClickListener(view -> handleTemperature());

        btnRepeat.setOnClickListener(view -> handleRepeat());
        btnNext.setOnClickListener(v -> {
            Intent objIntent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(objIntent);
            finish();
        });

        btnSkip.setOnClickListener(v -> {
            Intent objIntent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(objIntent);
            finish();
        });

        btnStartTest.setOnClickListener(view -> bindDevice());
        btnConnectDevice.setOnClickListener(view -> connectDevice());
        btnDisconnectDevice.setOnClickListener(view -> disconnectDevice());
    }

    private void initializeData(){
        tvAge.setText("DOB : " + shared.getString("dob", ""));
        tvName.setText("Name : " + shared.getString("name", ""));
        tvGender.setText("Gender : " + shared.getString("gender", ""));
        tvMobile.setText("Phone : " + shared.getString("mobile_number", ""));
    }

    // endregion

    // region Logical methods

    /**
     *
     * @param status
     */
    private void startTextToSpeech(int status){
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
    private void stopTextToSpeech(){
        /* close the textToSpeech engine to avoid the runtime exception from it */
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }
    }

    /**
     *
     * @param text
     */
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     *
     */
    private void handleHeight(){
        context.startActivity(new Intent(this, HeightActivity.class));
    }

    /**
     *
     */
    private void handleWeight(){
        context.startActivity(new Intent(this, ActofitMainActivity.class));
    }

    /**
     *
     */
    private void handleTemperature(){
        context.startActivity(new Intent(this, ThermometerScreen.class));
    }

    /**
     *
     */
    private void handleRepeat(){
        tvBodyOxygen.setText("spo");
        tvPulseRate.setText(R.string.pr);
    }

    /**
     *
     */
    private void bindDevice(){
        progressDialog = Tools.progressDialog(MainActivity.this);

        c208Invoker.bindDevice(new C208BindDeviceListener() {
            @Override
            public void onDataResponse(int spo, int pr) {
                LogUtils.d(TAG, "bindDevice---->" + "spo:" + spo + "pr:" + pr);
                progressDialog.dismiss();
                flag = false;
                Message message = new Message();
                message.arg1 = spo;
                message.arg2 = pr;
                message.what = RECEIVE_SPO_PR;
                handler.sendMessage(message);
            }

            @Override
            public void onError(String message) {
                LogUtils.d(TAG, "Bind device error message--->" + message);
                progressDialog.dismiss();
            }

            @Override
            public void onStateChanged(int oldState, int newState) {
                LogUtils.d(TAG, "oldState:" + oldState + "---->newState:" + newState);
            }

            @Override
            public void onBindDeviceSuccess(C208Device c208Device) {
                LogUtils.d(TAG, "deviceInfo-->" + c208Device.toString());
                macAddress = c208Device.getDeviceMacAddress();
                SharePreferenceUtil.put(MainActivity.this, MAC_ADDRESS_KEY, macAddress);
            }

            @Override
            public void onBindDeviceFail(String failMessage) {
                LogUtils.d(TAG, "Bind device failure information--->" + failMessage);
                progressDialog.dismiss();
            }
        });
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
    private void connectDevice(){
        if ("".equals(macAddress)) {
            LogUtils.d(TAG, "macAddress是空");
            Toast.makeText(this, "Please bind the device first！！", Toast.LENGTH_SHORT).show();
            return;
        }

        C208Device device = new C208Device();
        device.setDeviceMacAddress(macAddress);

        c208Invoker.connectDevice(device, new C208ConnectDeviceListener() {
            @Override
            public void onDataResponse(int spo, int pr) {
                LogUtils.d(TAG, "connectDevice---->" + "spo:" + spo + "pr:" + pr);
                Message message = new Message();
                message.arg1 = spo;
                message.arg2 = pr;
                message.what = RECEIVE_SPO_PR;
                handler.sendMessage(message);
            }

            @Override
            public void onError(String message) {
                LogUtils.d(TAG, "Connection device error message--->" + message);
            }

            @Override
            public void onStateChanged(int oldState, int newState) {
                LogUtils.d(TAG, "oldState:" + oldState + "---->newState:" + newState);
            }

            @Override
            public void onConnectedDeviceSuccess() {
                LogUtils.d(TAG, "onConnectedDeviceSuccess");
            }

            @Override
            public void onConnectedDeviceFail(String failMessage) {
                LogUtils.d(TAG, "Connection device failure message--->" + failMessage);
            }
        });
    }

    /**
     *
     */
    private void disconnectDevice(){
        c208Invoker.disconnectDevice(() -> {
            LogUtils.d(TAG, "Disconnect device！！！");
        });
    }

    private void writeToSharedPreference(String preferenceName, String key, String value){
        SharedPreferences sharedPreference = getSharedPreferences(preferenceName, MODE_PRIVATE);
        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // endregion
}

