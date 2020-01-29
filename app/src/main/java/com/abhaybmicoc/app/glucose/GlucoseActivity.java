package com.abhaybmicoc.app.glucose;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.os.Handler;
import android.widget.Toast;
import android.view.Gravity;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.speech.tts.TextToSpeech;
import android.content.DialogInterface;
import android.view.animation.Animation;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.content.SharedPreferences;
import android.view.animation.AlphaAnimation;
import android.support.v7.widget.RecyclerView;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.hemoglobin.GattClientActionListener;
import com.abhaybmicoc.app.hemoglobin.GattClientCallback;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.hemoglobin.MainActivity;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.activity.BloodPressureActivity;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.google.zxing.integration.android.IntentResult;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.glucose.adapters.ReadingAdapter;
import com.google.zxing.integration.android.IntentIntegrator;

import org.maniteja.com.synclib.helper.Util;
import org.maniteja.com.synclib.helper.SyncLib;
import org.maniteja.com.synclib.helper.HelperC;
import org.maniteja.com.synclib.helper.Communicator;
import org.maniteja.com.synclib.helper.SerializeUUID;

import java.util.Locale;
import java.util.ArrayList;
import java.io.InputStream;
import java.util.Collections;

public class GlucoseActivity extends AppCompatActivity implements Communicator, TextToSpeech.OnInitListener, View.OnClickListener,
        GattClientActionListener {
    // region Variables,

    private Context context = GlucoseActivity.this;

    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";

    private Util util;
    private Toolbar toolbar;
    private String mDeviceName;
    private Animation animation;
    private ActionBar mActionBar;
    private String mDeviceAddress;

    private ImageView ivSteps;
    private ImageView menuIcon;
    private ImageView ivGlucose;
    private ImageView batteryIcon;
    private ImageView bluetoothIcon;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvMobile;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView btnNext;
    private TextView tvBpMonitor;
    private TextView tvResultText;
    private TextView tvLogDisplay;
    private TextView offlineTitle;
    private TextView tvTemperature;
    private TextView tvResultTextNew;
    private TextView tvConnectionLabel;
    private TextView tvConnectionStatus;

    private LinearLayout layoutGlucose;
    private LinearLayout llReadingsLayout;
    private LinearLayout llConnectingStepsLayout;

    private View mView;

    private SharedPreferences glucoseData;

    private RadioGroup rgGlucose;

    private RadioButton rbRandom;
    private RadioButton rbFasting;
    private RadioButton rbPostMeal;
    private RadioButton radioButtonId;

    private int selectedId;

    public static boolean devTestStarted;
    public static boolean mConnected = false;

    private Button btnReadData;
    private Button btnStartTest;
    private Button btnWriteData;
    private Button btnRestartTest;

    private RecyclerView readingRecyclerView;

    private SyncLib syncLib;
    private InputStream inputStream;
    private TextToSpeech textToSpeech;
    private SerializeUUID serializeUUID;
    private Communicator communicator = this;
    private SharedPreferences sharedPreferencesPersonal;
    private SharedPreferences sharedPreferenceGlucoseDEvice;

    private SharedPreferences sharedPreferenceHBDevice;
    private SharedPreferences sharedPreferencesDeviceHemoglobin;

    private BluetoothGatt mGatt;

    private Handler deviceConnectionTimeoutHandler;
    private ProgressDialog dialogConnectionProgress;

    private int DEVICE_CONNECTION_WAITING_TIME = 10000;
    private int STATR_TEST_ACTIVATION_TIME = 2000;

    private String textToSpeak;
    private String resultOfGlucose;

    private boolean isTestStarted = false;

    private BluetoothAdapter bluetoothAdapter;

    private boolean mScanning;
    private boolean isDeviceConnected;
    private boolean isEchoInitialized;


    // endregion

    // region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setupUI();

        showProgressDialog();

        setupEvents();

        initializeData();

        turnOnBluetooth();

        connectionToHemoglobinDevice();
    }

    private void connectionToHemoglobinDevice() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        sharedPreferenceHBDevice = getSharedPreferences("device_data", MODE_PRIVATE);
        sharedPreferencesDeviceHemoglobin = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);

        connectOrShowScanDevice();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("device id");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result == null) {
                Toast.makeText(this, getResources().getString(R.string.canceled), Toast.LENGTH_LONG).show();
            } else {
                if (result.getContents() != null) {
                    syncLib.writeCalibData(result.getContents());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        ;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mConnected) {
            syncLib.startReceiver();
        } else if (mConnected) {
            syncLib.startReceiver();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        syncLib.stopReceiver();
        stopBluetoothConnection();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        syncLib.stopReceiver();
    }

    @Override
    public void onBackPressed() {
//        setSwitchActivity();
    }

    @Override
    public boolean go(String text) {
        //set the log of the go text
        tvLogDisplay.setText(text);

        //check the conditio of the go text if there is go then send voice command to user to click on the start test button
        if (text.equals("go")) {
            textToSpeak = "Click on start Test";
            speakOut(textToSpeak);
        }

        //already existed the return statement of the boolean method
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close the text to speach object to avoid run time exception
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        } catch (Exception e) {
            System.out.println("onPauseException" + e.getMessage());
        }

    }

    @Override
    public void setLog(String text) {
        tvLogDisplay.setText(text);

        if (text.equals("Insert Strip!")) {
            ivSteps.setVisibility(View.VISIBLE);
            ivGlucose.setVisibility(View.VISIBLE);
            readingRecyclerView.setVisibility(View.GONE);

            textToSpeak = "Please Insert Strip";
            speakOut(textToSpeak);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivSteps.setImageDrawable(getDrawable(R.drawable.insertstrip));
            }
        } else if (text.equals("Add Blood")) {
            ivSteps.setVisibility(View.VISIBLE);
            ivGlucose.setVisibility(View.VISIBLE);
            readingRecyclerView.setVisibility(View.GONE);

            textToSpeak = "Please Add Blood";
            speakOut(textToSpeak);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivSteps.setImageDrawable(getDrawable(R.drawable.addblood));
            }

        } else if (text.equals("Used Strip\n" + "Please remove the used strip and insert a new strip")) {
            ivSteps.setVisibility(View.VISIBLE);
            ivGlucose.setVisibility(View.VISIBLE);
            readingRecyclerView.setVisibility(View.GONE);

            textToSpeak = "Please insert the new strip";
            speakOut(textToSpeak);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivSteps.setImageDrawable(getDrawable(R.drawable.insertstrip));
            }

        } else {
            if (text.contains("Result")) {
                tvLogDisplay.setVisibility(View.VISIBLE);
                tvResultTextNew.setVisibility(View.GONE);
                tvResultText.setVisibility(View.VISIBLE);

                tvLogDisplay.setText("Blood Glucose Result is");

                text = text.replace("Result is", "");
                text = text.replace(" ", "");

                resultOfGlucose = text;

                tvResultText.setText(text);
                tvResultText.setVisibility(View.VISIBLE);
                tvResultTextNew.setVisibility(View.GONE);

                tvResultTextNew.setText(resultOfGlucose);

                mView.setVisibility(View.GONE);
                ivSteps.setVisibility(View.GONE);
                ivGlucose.setVisibility(View.GONE);
                layoutGlucose.setGravity(Gravity.CENTER);
                layoutGlucose.setVisibility(View.VISIBLE);
                readingRecyclerView.setVisibility(View.GONE);

                rgGlucose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {

                        selectedId = rgGlucose.getCheckedRadioButtonId();
                        radioButtonId = findViewById(selectedId);

                        writeSugarSharedPreference();
                    }
                });
            }

            tvResultText.setText(text);

            ivSteps.setVisibility(View.GONE);
            ivGlucose.setVisibility(View.GONE);
            tvLogDisplay.setVisibility(View.GONE);
            layoutGlucose.setGravity(Gravity.CENTER);
            layoutGlucose.setVisibility(View.VISIBLE);
            readingRecyclerView.setVisibility(View.GONE);
        }
    }


    /* Request enabling bluetooth if not enabled */
    private void turnOnBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }

    private void writeSugarSharedPreference() {
        glucoseData = getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
        SharedPreferences.Editor editor = glucoseData.edit();

        editor.putString(Constant.Fields.SUGAR, tvResultText.getText().toString());
        editor.putString(Constant.Fields.GLUCOSE_TYPE, radioButtonId.getText().toString());

        editor.commit();

    }

    @Override
    public void testStarted(boolean testStarted) {
        devTestStarted = testStarted;
    }

    @Override
    public void stopNotiFication() {
    }

    @Override
    public void setConnectionStatus(String s, boolean connectionStatus) {

        mConnected = connectionStatus;
        if (mConnected) {
            bluetoothIcon.setBackgroundResource(R.drawable.connect);
            batteryIcon.setVisibility(View.VISIBLE);

            setStartTestTimerHandler();


            saveDeviceInformation(mDeviceAddress, mDeviceName);


            if (dialogConnectionProgress != null && dialogConnectionProgress.isShowing()) {
                dialogConnectionProgress.dismiss();
            }
        } else {
            tvConnectionStatus.setText("Connecting");

            llConnectingStepsLayout.setVisibility(View.VISIBLE);
            llReadingsLayout.setVisibility(View.GONE);

            batteryIcon.setVisibility(View.INVISIBLE);
            bluetoothIcon.setBackgroundResource(R.drawable.disconnect);
        }
    }

    @Override
    public void setSwitchActivity() {
        Intent intent = new Intent(getApplicationContext(), GlucoseScanListActivity.class);
        intent.putExtra("flag", 2);
        startActivity(intent);
        finish();
    }

    @Override
    public void setBatteryLevel(int value) {
        setBatteryLevelValue(value);
    }

    @Override
    public void setManufacturerName(String s) {
    }

    @Override
    public void setSerialNumber(String s) {
    }

    @Override
    public void setModelNumber(String s) {
    }

    @Override
    public void getOfflineResults(ArrayList<String> arrayList) {
        if (arrayList.size() > 0) {
            Collections.reverse(arrayList);
            offlineTitle.setText("Offline Results");
            ReadingAdapter readingAdapter = new ReadingAdapter(arrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            readingRecyclerView.setLayoutManager(mLayoutManager);
            readingRecyclerView.setItemAnimator(new DefaultItemAnimator());
            readingRecyclerView.setAdapter(readingAdapter);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.US);

            textToSpeech.setSpeechRate(1);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
                speakOut(textToSpeak);
            }

        } else {
        }
    }

    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_header_height:
                startActivity(new Intent(context, HeightActivity.class));
                finish();
                break;

            case R.id.tv_header_weight:
                startActivity(new Intent(context, ActofitMainActivity.class));
                finish();
                break;

            case R.id.tv_header_tempreture:
                startActivity(new Intent(context, ThermometerScreen.class));
                finish();
                break;

            case R.id.tv_header_pulseoximeter:
                startActivity(new Intent(context, com.abhaybmicoc.app.oximeter.MainActivity.class));
                finish();
                break;

            case R.id.tv_header_bloodpressure:
                startActivity(new Intent(this, BloodPressureActivity.class));
                finish();
                break;
        }
    }

    // endregion

    // region Initialization methods

    private void setupUI() {
        setContentView(R.layout.activity_home);

        layoutGlucose = findViewById(R.id.layout_glucose_result);
        llReadingsLayout = findViewById(R.id.ll_readings_layout);
        llConnectingStepsLayout = findViewById(R.id.ll_connecting_steps_layout);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);

        toolbar = findViewById(R.id.layout_toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.setCustomView(mCustomView);

        tvLogDisplay = findViewById(R.id.tv_log_display);
        offlineTitle = findViewById(R.id.tv_offline_title);

        readingRecyclerView = findViewById(R.id.rv_reading);

        ivSteps = findViewById(R.id.iv_steps);
        ivGlucose = findViewById(R.id.iv_glucose);
        batteryIcon = mCustomView.findViewById(R.id.batteryIcon);
        bluetoothIcon = mCustomView.findViewById(R.id.bluetoothIcon);

        rgGlucose = layoutGlucose.findViewById(R.id.rg_glucose);
        rbFasting = layoutGlucose.findViewById(R.id.rb_fasting);
        rbPostMeal = layoutGlucose.findViewById(R.id.rb_post);
        rbRandom = layoutGlucose.findViewById(R.id.rb_random);

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvHeight = findViewById(R.id.tv_header_height);
        tvWeight = findViewById(R.id.tv_header_weight);
        tvMobile = findViewById(R.id.tv_mobile_number);
        btnNext = findViewById(R.id.tv_header_pulseoximeter);
        tvTemperature = findViewById(R.id.tv_header_tempreture);
        tvBpMonitor = findViewById(R.id.tv_header_bloodpressure);
        tvConnectionStatus = findViewById(R.id.tv_connection_status);
        tvResultText = layoutGlucose.findViewById(R.id.tv_resultText);
        tvResultTextNew = layoutGlucose.findViewById(R.id.tv_resultNew);
        tvConnectionLabel = mCustomView.findViewById(R.id.connectionLabel);

        menuIcon = mCustomView.findViewById(R.id.menuIcon);
        menuIcon.setVisibility(View.GONE);

        sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        mView = findViewById(R.id.view_custom);

        textToSpeech = new TextToSpeech(getApplicationContext(), this);

        btnReadData = findViewById(R.id.getData);
        btnWriteData = findViewById(R.id.btn_skip);
        btnStartTest = findViewById(R.id.btn_start_test);
        btnRestartTest = findViewById(R.id.restartTest);

        inputStream = getResources().openRawResource(R.raw.synclibserialize);

        serializeUUID = new SerializeUUID();
        serializeUUID.readFile(inputStream);

        tvName.setText("Name : " + sharedPreferencesPersonal.getString(Constant.Fields.NAME, ""));
        tvGender.setText("Gender : " + sharedPreferencesPersonal.getString(Constant.Fields.GENDER, ""));
        tvAge.setText("DOB : " + sharedPreferencesPersonal.getString(Constant.Fields.DATE_OF_BIRTH, ""));
        tvMobile.setText("Phone : " + sharedPreferencesPersonal.getString(Constant.Fields.MOBILE_NUMBER, ""));
    }


    private void showProgressDialog() {

        dialogConnectionProgress = new ProgressDialog(GlucoseActivity.this);
        dialogConnectionProgress.setMessage("Connecting...");
        dialogConnectionProgress.setCancelable(false);
    }

    private void setupEvents() {
        tvHeight.setOnClickListener(this);
        tvWeight.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        tvBpMonitor.setOnClickListener(this);
        tvTemperature.setOnClickListener(this);

        btnReadData.setOnClickListener(view -> readData());
        btnStartTest.setOnClickListener(view -> startTest());
        btnWriteData.setOnClickListener(view -> writeData());
        btnRestartTest.setOnClickListener(view -> restartTest());
        bluetoothIcon.setOnClickListener(view -> toggleBluetooth());

        toolbar.setOnTouchListener((v, event) -> {
            hideSoftInput();
            return false;
        });


        rgGlucose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                selectedId = rgGlucose.getCheckedRadioButtonId();
                radioButtonId = findViewById(selectedId);

                writeSugarSharedPreference();
            }
        });

    }

    /**
     *
     */
    private void initializeData() {
        util = new Util(this, this);

        mConnected = false;
        sharedPreferenceGlucoseDEvice = getSharedPreferences("glucose_device_data", MODE_PRIVATE);

        final Intent intent = getIntent();

        if (intent.getStringExtra(EXTRAS_DEVICE_ADDRESS) != null) {
            mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
            mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);

            util.putString(HelperC.key_mybluetoothaddress, mDeviceAddress);
        } else {
            mDeviceAddress = util.readString(HelperC.key_autoconnectaddress, "");
        }

        connectDevice();
    }

    private void connectDevice() {
//        dialogConnectionProgress.show();

        llConnectingStepsLayout.setVisibility(View.VISIBLE);
        llReadingsLayout.setVisibility(View.GONE);

        syncLib = new SyncLib(communicator, this, GlucoseActivity.this, serializeUUID, mDeviceAddress);
        syncLib.startReceiver();

        util.print("Scan List Address :Main " + mDeviceAddress + "::" + util.readString(HelperC.key_mybluetoothaddress, "") + " - " + mDeviceAddress.length());

        setDeviceConnectionTimeoutHandler();
    }

    private void setDeviceConnectionTimeoutHandler() {
        deviceConnectionTimeoutHandler = new Handler();

        deviceConnectionTimeoutHandler.postDelayed(() -> {

            if (!mConnected) {

                syncLib.stopReceiver();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle("Connectivity Lost!");
                alertDialogBuilder.setMessage("Device is not active, try again").setCancelable(false)
                        .setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                connectDevice();
                            }
                        });
                alertDialogBuilder.setNegativeButton("Skip Test", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        writeData();
                    }
                });

                /* create alert dialog */
                AlertDialog alertDialog = alertDialogBuilder.create();
                /* show alert dialog */
                if (!((Activity) context).isFinishing())
                    alertDialog.show();
                alertDialogBuilder.setCancelable(false);
            }
        }, DEVICE_CONNECTION_WAITING_TIME);
    }

    private void setStartTestTimerHandler() {
        deviceConnectionTimeoutHandler = new Handler();

        deviceConnectionTimeoutHandler.postDelayed(() -> {
            tvConnectionStatus.setText("Connected");

            llConnectingStepsLayout.setVisibility(View.GONE);
            llReadingsLayout.setVisibility(View.VISIBLE);

            btnStartTest.setBackground(getResources().getDrawable(R.drawable.greenback));
            textToSpeak = "Please click on start Test";
            speakOut(textToSpeak);
        }, STATR_TEST_ACTIVATION_TIME);
    }

    private void saveDeviceInformation(String deviceAddress, String deviceName) {
        SharedPreferences.Editor editor = sharedPreferenceGlucoseDEvice.edit();
        editor.putString("glucoseDeviceAddress", deviceAddress);
        editor.putString("glucoseDeviceName", deviceName);
        editor.commit();
    }

    // endregion

    // region Logical methods

    /**
     *
     */
    private void startTest() {

        if (mConnected) {
            try {
                syncLib.startTest();
                isTestStarted = true;
                btnWriteData.setText("Next");
            } catch (Exception e) {
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Connect to Device!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void restartTest() {
        if (mConnected) {

            if (devTestStarted) {
                try {
                    syncLib.stopTest();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Test Not Started!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Connect to Device!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void writeData() {
        if (mConnected) {
            syncLib.notifyGetData();
        }
        if (isTestStarted) {
            if (rgGlucose.getCheckedRadioButtonId() == -1) {
                // no radio buttons are checked
                Toast.makeText(context, "Please select any one type", Toast.LENGTH_SHORT).show();
            } else {
                // one of the radio buttons is checked
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     *
     */
    private void readData() {
        if (mConnected) {
            syncLib.notifyGetData();
        } else {
            Toast.makeText(getApplicationContext(), "Please Connect to Device!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void toggleBluetooth() {
        syncLib.setmDeviceAddress(mDeviceAddress);
        syncLib.connectOrDisconnect();
    }

    /**
     *
     */
    private void hideSoftInput() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception crap) {
            crap.printStackTrace();

        }
    }

    /**
     *
     */
    private void setBatteryLevelValue(int value) {
        if (value > 25 && value < 33) {
            if (animation != null)
                animation.cancel();
            batteryIcon.setBackgroundResource(R.drawable.battery1);
        } else if (value >= 33 && value < 50) {
            if (animation != null)
                animation.cancel();
            batteryIcon.setBackgroundResource(R.drawable.battery2);
        } else if (value >= 50) {
            if (animation != null)
                animation.cancel();
            batteryIcon.setBackgroundResource(R.drawable.battery3);
        } else if (value > 0 && value <= 25) {
            batteryIcon.setBackgroundResource(R.drawable.battery0);
            animation = new AlphaAnimation(1, 0);
            animation.setDuration(400);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            batteryIcon.startAnimation(animation);
        }
    }

    @Override
    public void log(String message) {

    }

    @Override
    public void logError(String message) {

    }

    @Override
    public void setConnected(boolean connected) {
        mScanning = false;

        updateConnectionStatus(connected);
    }

    @Override
    public void initializeTime() {
        isEchoInitialized = true;
    }

    @Override
    public void initializeEcho() {
        isEchoInitialized = true;
    }

    @Override
    public void disconnectGattServer() {
        stopBluetoothConnection();
    }

    @Override
    public void showToast(String msg) {

    }

    /**
     *
     */
    private void connectOrShowScanDevice() {
        /**
         * On load, check if device is stored in local storage
         * If yes, connect
         */

        if (savedDeviceAlreadyExists()) {
            connect();
        }
    }

    private void updateConnectionStatus(boolean connected) {
        /**
         * Store connection status in a variable
         * If connected, hide connect button, show connection message
         * If not connected
         *   - If we have tried maximum times, show scan button
         *   - Else try to connect again
         */
        isDeviceConnected = connected;

        if (!connected) {
            connect();
        }
    }

    private void stopBluetoothConnection() {

        isDeviceConnected = false;
        isEchoInitialized = false;

        if (mGatt != null) {
            try {
                mGatt.disconnect();
                mGatt.close();
            } catch (RuntimeException ex) {
            }
        }
    }


    /**
     *
     */
    private void connect() {
        disconnectGattServer();

        BluetoothDevice device = getDevice(getStoredDeviceAddress());

        GattClientCallback gattClientCallback = new GattClientCallback(this);

        mGatt = device.connectGatt(this, true, gattClientCallback);
        // TODO: Check state of connection
    }

    /**
     * @return
     */
    private boolean savedDeviceAlreadyExists() {
        return !sharedPreferenceHBDevice.getString("deviceName", "").equals("");
    }

    /**
     * @return
     */
    private String getStoredDeviceAddress() {
        return sharedPreferenceHBDevice.getString("deviceAddress", "");
    }

    /**
     * @return
     */
    private BluetoothDevice getDevice(String deviceName) {
        return bluetoothAdapter.getRemoteDevice(deviceName);
    }


    // endregion
}
