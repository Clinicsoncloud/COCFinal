package com.abhaybmicoc.app;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abhaybmicoc.app.actofitheight.ActofitMainActivity;
import com.abhaybmicoc.app.glucose.Activity_ScanList;
import com.abhaybmicoc.app.heightweight.Principal;
import com.abhaybmicoc.app.oximeter.MainActivity;
import com.abhaybmicoc.app.slidemenu.SlideMenu;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import com.abhaybmicoc.app.base.ADGattService;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;
import com.abhaybmicoc.app.entities.DataBase;
import com.abhaybmicoc.app.entities.Lifetrack_infobean;
import com.abhaybmicoc.app.gatt.ADGattUUID;
import com.abhaybmicoc.app.gatt.BleReceivedService;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.utilities.ADSharedPreferences;
import com.abhaybmicoc.app.utilities.ANDMedicalUtilities;
import com.abhaybmicoc.app.utilities.ScanRecordParser;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.view.ActivityMonitorDisplayDataLayout;
import com.abhaybmicoc.app.view.BloodPressureDispalyDataLayout;
import com.abhaybmicoc.app.view.ThermometerDisplayDataLayout;
import com.abhaybmicoc.app.view.WeightScaleDisplayDataLayout;
import com.kaopiz.kprogresshud.KProgressHUD;

public class DashboardActivity extends ADBaseActivity implements OnRefreshListener, TextToSpeech.OnInitListener, OnClickListener {


    private static final int REQUEST_ENABLE_BLUETOOTH = 1000;

    private boolean mIsBindBleReceivedServivce = false;
    private boolean isScanning = false;
    private boolean shouldStartConnectDevice = false;

    private boolean mIsBleReceiver = false;

    private ActivityMonitorDisplayDataLayout activitymonitor;
    private BloodPressureDispalyDataLayout bloodpressure;
    private WeightScaleDisplayDataLayout weightscale;
    private ThermometerDisplayDataLayout thermometer;
    private FrameLayout rightArrow;
    private FrameLayout leftArrow;
    private DataBase db;
    private SlideMenu mSlideMenu;
    //    private ProgressDialog pd;
    private KProgressHUD pd;
    private LinearLayout linearContainer;
    private boolean mIsSendCancel = false;
    private Button btnstart, btnnext, btnrepeat;
    private ArrayList<BluetoothDevice> deviceList;
    private TextView disp_data_bp_value_textview;
    private SharedPreferences BpObject, shared;
    ArrayList<String> pairedDeviceList = new ArrayList<String>(); //ACGS-10
    private TextView txtName, txtAge, txtGender, txtMobile;
    private BluetoothAdapter mBluetoothAdapter;
    private TextToSpeech tts;
    private String txt = "";
    private TextView txtHeight,txtWeight,txtTemprature,txtOximeter;

    Context context;

    BluetoothDevice bluetoothDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.and_dashboard_new);

        //Initialization of the top boxes
        txtHeight = findViewById(R.id.txtmainheight);
        txtWeight = findViewById(R.id.txtmainweight);
        txtTemprature = findViewById(R.id.txtmaintempreture);
        txtOximeter = findViewById(R.id.txtmainpulseoximeter);

        context = DashboardActivity.this;

        disp_data_bp_value_textview = findViewById(R.id.disp_data_bp_value_textview);
        int val = ((0x07 & 0xff) << 8) | (0xE1 & 0xff);
        int sys = ((0x00 & 0xff) << 8) | (0x65 & 0xff);

        registerReceiver(mMeasudataUpdateReceiver, MeasuDataManager.MeasuDataUpdateIntentFilter());
        //Call function to get paired device
        isDevicePaired();
        doStartService();
        initializeUI_new();
        setListiner();
        bindEvents();

        String login_username = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "");

        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            db = new DataBase(this);
        }

        btnnext = findViewById(R.id.btnnext);
        btnstart = findViewById(R.id.btnstart);
        btnrepeat = findViewById(R.id.btnrepeat);
        linearContainer = findViewById(R.id.linearContainer);

        shared = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        db.deleteBpData(this);

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);

        tts = new TextToSpeech(getApplicationContext(),this);
        txt = "please insert hand to the cuf and tight it properly,and then start Machine and click start Button";
        speakOut(txt);

        txtName.setText("Name : " + shared.getString("name", ""));
        txtGender.setText("Gender : " + shared.getString("gender", ""));
        txtMobile.setText("Phone : " + shared.getString("mobile_number", ""));
        txtAge.setText("DOB : " + shared.getString("dob", ""));

        btnnext.setOnClickListener(v -> {
            try{
                Intent objIntent = new Intent(getApplicationContext(), Activity_ScanList.class);
                startActivity(objIntent);
                finish();
            }catch (Exception e){}
        });

        btnstart.setOnClickListener(v -> {
//            pd = Tools.progressDialog(DashboardActivity.this);
            pd = Tools.kHudDialog(DashboardActivity.this);
            pd.setProgress(40);
        });

        btnrepeat.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(BleReceivedService.TYPE_GATT_CONNECTED);
            sendBroadcast(intent);

        });


        mSlideMenu = (SlideMenu) findViewById(R.id.slideMenu);
        String addnewUserVisiblity = ADSharedPreferences.getString(ADSharedPreferences.KEY_ADD_NEW_USER_VISIBLITY, "");
        String manageuservisibility = ADSharedPreferences.getString(ADSharedPreferences.KEY_MANAGER_USER_VISIBILITY,
                "");
        String frommanagevisibility = ADSharedPreferences.getString(ADSharedPreferences.KEY_FROM_MANAGER_VISIBILITY,
                "");

        RelativeLayout mainlayout = (RelativeLayout) findViewById(R.id.root);
        mSlideMenu.init(this, this, 333, login_username, login_username,
                addnewUserVisiblity, manageuservisibility,
                frommanagevisibility,
                mainlayout);

        mSlideMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideMenu.show();
            }
        });

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.commit();

        deviceList = new ArrayList<BluetoothDevice>();
    }

    private void bindEvents() {

        //bind click events of top box
        txtHeight.setOnClickListener(this);
        txtWeight.setOnClickListener(this);
        txtTemprature.setOnClickListener(this);
        txtOximeter.setOnClickListener(this);

    }

    private void offBluetooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissIndicator();
        doStopService();
        unregisterReceiver(mMeasudataUpdateReceiver);
    }

    private boolean mIsCheckBleetoothEnabled = false;

    @Override
    protected void onResume() {
        super.onResume();


        isDevicePaired();
        MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
        if (measuDataManager == null) {
            Log.e("inside_if","onResume");
            measuDataManager = new MeasuDataManager(this);
            ((AndMedical_App_Global) getApplication()).setMeasuDataManager(measuDataManager);
            measuDataManager.syncAllMeasuDatas(true);
        } else {
            Log.e("inside_else","onResume");
            measuDataManager.syncAllMeasuDatas(true);
            refreshDisplay();
        }
        if (mIsSendCancel) {
            mIsSendCancel = false;
        }

        BluetoothManager bluetoothManager = getBluetoothManager();
        if (bluetoothManager != null) {
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter != null) {
                if (!bluetoothAdapter.isEnabled()) {
                    if (!mIsCheckBleetoothEnabled) {
                        mIsCheckBleetoothEnabled = true;
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
                        return;
                    }
                } else {
                    doBindBleReceivedService();
                }
            }
        }
        mIsCheckBleetoothEnabled = false;
    }

    @Override
    protected void onPause() {
        super.onPause();

        doStopLeScan();
        doUnbindBleReceivedService();
//        doStopService();

        if (!mIsSendCancel) {
            mIsSendCancel = true;
        }

        //closing the text to speech object in to avoid run time exception
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) {
                doBindBleReceivedService();
            }
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public SlideMenu getSlideMenu() {
        return mSlideMenu;
    }

    public BluetoothManager getBluetoothManager() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        return bluetoothManager;
    }

    // UIの初期化
    private void initializeUI_new() {

        bloodpressure = (BloodPressureDispalyDataLayout) findViewById(R.id.llinear_bp);
        weightscale = (WeightScaleDisplayDataLayout) findViewById(R.id.llinear_wt);
        thermometer = (ThermometerDisplayDataLayout) findViewById(R.id.llinear_tm);
        activitymonitor = (ActivityMonitorDisplayDataLayout) findViewById(R.id.llinear_am);

        // Arrows
        rightArrow = (FrameLayout) findViewById(R.id.right_arrow);
        rightArrow.setVisibility(View.INVISIBLE);

        leftArrow = (FrameLayout) findViewById(R.id.left_arrow);
        leftArrow.setVisibility(View.INVISIBLE);

        TextView header = (TextView) findViewById(R.id.header);
        header.setText(R.string.header_dashboard);
    }

    private void setListiner() {
        rightArrow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AndMedical_App_Global appGlobal = (AndMedical_App_Global) getApplication();
                MeasuDataManager manager = appGlobal.getMeasuDataManager();
                if (manager != null) {
                    manager.moveDatasToThePast();
                    refreshDisplay();
                }
            }
        });
        leftArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AndMedical_App_Global appGlobal = (AndMedical_App_Global) getApplication();
                MeasuDataManager manager = appGlobal.getMeasuDataManager();
                if (manager != null) {
                    manager.moveDatasToTheFuture();
                    refreshDisplay();
                }
            }
        });
    }

    private void doBindBleReceivedService() {
        Log.e("inside","dobindBleReceivedService");
        if (!mIsBindBleReceivedServivce) {
            bindService(new Intent(DashboardActivity.this,
                    BleReceivedService.class), mBleReceivedServiceConnection, Context.BIND_AUTO_CREATE);
            mIsBindBleReceivedServivce = true;
            Log.e("inside_condition","dobindBleReceivedService");
        }
    }

    private void doUnbindBleReceivedService() {
        Log.e("inside","dounbindBleReceivedService");
        if (mIsBindBleReceivedServivce) {
            unbindService(mBleReceivedServiceConnection);
            mIsBindBleReceivedServivce = false;
        }
    }

    private ServiceConnection mBleReceivedServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            doStartLeScan();
        }
    };

    private Dialog progress;

    private void showIndicator(String message) {
        if (!(DashboardActivity.this).isFinishing()) {
            if (progress == null) {
                progress = new Dialog(DashboardActivity.this);
                progress.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progress.setContentView(R.layout.custom_alert);
                progress.setCancelable(false);
            }

            setIndicatorMessage(message);

            if (!progress.isShowing()) {
                progress.show();
            }
        }
    }

    private void setIndicatorMessage(String message) {
        if (progress == null) {
            return;
        }
        TextView syncMessages = (TextView) progress.findViewById(R.id.syncMessages1);

        if (message == null) {
            message = "";
        }

        if (syncMessages != null) {
            syncMessages.setText(message);
        }
    }

    private void dismissIndicator() {
        if (progress == null) {
            return;
        }
        if (progress.isShowing()) {
            progress.dismiss();
        }

        progress = null;
    }

    private void startScan() {
        if (shouldStartConnectDevice) {
            return;
        }
        if (BleReceivedService.getInstance() != null) {
            if (BleReceivedService.getInstance().isConnectedDevice()) {
                BleReceivedService.getInstance().disconnectDevice();
            }
            isScanning = true;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // For UW-302BLE
            deviceList.clear();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean result = BleReceivedService.getInstance().getBluetoothManager().getAdapter().startLeScan(mLeScanCallback);
                }
            });
        }
    }

    private void stopScan() {
        if (BleReceivedService.getInstance() != null) {
            isScanning = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (BleReceivedService.getInstance().getBluetoothManager().getAdapter() != null) {
                        BleReceivedService.getInstance().getBluetoothManager().getAdapter().stopLeScan(mLeScanCallback);
                    }
                }
            });
        }
    }

    private boolean isTryScanning;

    private void doStartLeScan() {
        isTryScanning = true;
        doTryLeScan();
    }

    private void doTryLeScan() {
        if (!isTryScanning) {
            return;
        }
        if (!isScanning) {
            startScan();
        }
        isTryScanning = false;
    }

    private void doStopLeScan() {
        if (isScanning) {
            stopScan();
        }
    }

    private LeScanCallback mLeScanCallback = new LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (!isScanning) {
                return;
            }
            if (device.getName() != null) {
                if (isAbleToConnectDevice(device, scanRecord) && !shouldStartConnectDevice) {

                    shouldStartConnectDevice = true;
                    if (device.getName() != null) {
                        bluetoothDevice = device;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                doStopLeScan();
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                BleReceivedService.getInstance().connectDevice(device);
                            }
                        });
                    }

                } else if (device.getName().contains("UW-302")) {

                    if (isAbleToConnectDeviceUW(device, scanRecord) && !shouldStartConnectDevice) {

                        final ImageView syncImage = (ImageView) findViewById(R.id.dashboard_icon_display);
                        syncImage.setVisibility(View.VISIBLE);
                        syncImage.setImageResource(R.drawable.syncpurple);
                        syncImage.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        shouldStartConnectDevice = true;
                                        doStopLeScan();
                                        try {
                                            Thread.sleep(200);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        BleReceivedService.getInstance().connectDevice(device);

                                        // UW-302BLE sync may takes long time, so disable Android sleep.
                                        // When start again, reset sleep disabling.
                                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                                        syncImage.setImageResource(R.drawable.dashboard_walk_icon);
                                    }
                                });
                            }
                        });

                    }
                }
            }
        }
    };

    private boolean isAbleToConnectDevice(BluetoothDevice device, byte[] scanRecord) {

        // すでに何かが接続中ならFalse
        if (BleReceivedService.getInstance().isConnectedDevice()) {
            return false;
        }
        BluetoothAdapter bluetoothAdapter = BleReceivedService.getInstance().getBluetoothManager().getAdapter();

        // ペアリング済みのデバイスのみ許可
        if (bluetoothAdapter != null) {

            Set<BluetoothDevice> pairingDevices = bluetoothAdapter.getBondedDevices();

            if (device.getName() != null) {
                return pairingDevices.contains(device) && device.getName().contains("A&D");
            }
        }
        return false;
    }

    private boolean isAbleToConnectDeviceUW(BluetoothDevice device, byte[] scanRecord) {
        if (device.getName().contains("UW-302BLE")) { //Add Support for UW-302
            String deviceName = "";
            if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
                db = new DataBase(getApplicationContext());
                deviceName = db.getTrackerName();

            }
            if (deviceName != null) {
                if (deviceName.equalsIgnoreCase(device.getName())) {
                    ScanRecordParser.ScanRecordItem scanRecordItem = ScanRecordParser.getParser().parseString(scanRecord);
                    byte[] manufacturerSpecificData = scanRecordItem.getManufacturerSpecificData();
                    String print_activitydata = byte2hex(manufacturerSpecificData);
                    if (manufacturerSpecificData != null
                            && manufacturerSpecificData.length == 3) {
                        int value = manufacturerSpecificData[2];
                        if (value == 1 || value == 3) { // 1:Paired, 3:Paired but need to set time
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    private long setDateTimeDelay = Long.MIN_VALUE;
    private long indicationDelay = Long.MIN_VALUE;
    private Handler uiThreadHandler = new Handler();
    private final BroadcastReceiver bleServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle intentBundle = intent.getExtras();
            String type = intent.getExtras().getString(BleReceivedService.EXTRA_TYPE);
            Log.e("onReceive_intentAction",""+intent.getExtras().getString(BleReceivedService.EXTRA_TYPE));
            Log.e("onReceive_Type"," = "+BleReceivedService.EXTRA_TYPE);
            if (BleReceivedService.TYPE_GATT_CONNECTED.equals(type)) {
                linearContainer.setVisibility(View.VISIBLE);
                Log.e("inside","onReceive_GATT_CONNECTED");

                try {
                    pd.dismiss();
                } catch (Exception e) {

                }
                showIndicator(getResources().getString(R.string.indicator_start_receive));
                BleReceivedService.getGatt().discoverServices();

                setDateTimeDelay = Long.MIN_VALUE;
                indicationDelay = Long.MIN_VALUE;
            } else if (BleReceivedService.TYPE_GATT_DISCONNECTED.equals(type)) {
                Log.e("inside","onReceive_GATT_DISCONNECTED");
                dismissIndicator();
                if (shouldStartConnectDevice) {
                    linearContainer.setVisibility(View.VISIBLE);
                    try {
                        pd.dismiss();
                    } catch (Exception e) {

                    }
                    BleReceivedService.getInstance().disconnectDevice();
                    uiThreadHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shouldStartConnectDevice = false;
                            if (!isScanning) {
                                doStartLeScan();
                            }
                        }
                    }, 80L);
                }
            } else if (BleReceivedService.TYPE_GATT_ERROR.equals(type)) {
                Log.e("inside","onReceive_GATT_ERROR");
                int status = intent.getExtras().getInt(BleReceivedService.EXTRA_STATUS);
                if (status == 19) {
                    return;
                }
                if (shouldStartConnectDevice) {
                    if (BleReceivedService.getInstance() != null) {
                        if (!BleReceivedService.getInstance().isConnectedDevice()) {
                            shouldStartConnectDevice = false;
                            dismissIndicator();
                            doStartLeScan();
                            linearContainer.setVisibility(View.VISIBLE);
                            try {
                                pd.dismiss();
                            } catch (Exception e) {

                            }
                        } else {
                            BluetoothGatt gatt = BleReceivedService.getGatt();
                            if (gatt != null) {
                                gatt.connect();
                            }
                        }
                    }
                } else {
                    dismissIndicator();
                }
            } else {
                if (BleReceivedService.TYPE_GATT_SERVICES_DISCOVERED.equals(type)) {
                    Log.e("inside","onReceive_GATT_DISCOVERED");
                    if (shouldStartConnectDevice) {
                        if (BleReceivedService.getInstance() != null) {

                            String device_name = intent.getExtras().getString(BleReceivedService.EXTRA_DEVICE_NAME);
                            if (device_name.contains("UW-302BLE")) {
                                BleReceivedService.getInstance().setUW302Notfication();
                            } else {
                                uiThreadHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        BleReceivedService.getInstance().requestReadFirmRevision();
                                        linearContainer.setVisibility(View.VISIBLE);
                                        try {
                                            pd.dismiss();
                                        } catch (Exception e) {

                                        }
                                    }
                                }, 500L);
                            }

                        }
                    }
                } else if (BleReceivedService.TYPE_CHARACTERISTIC_READ.equals(type)) {
                    if (shouldStartConnectDevice) {
                        byte[] firmRevisionBytes = intent.getByteArrayExtra(BleReceivedService.EXTRA_VALUE);
                        String firmRevision = null;
                        if (firmRevisionBytes == null) {
                            return;
                        }
                        firmRevision = new String(firmRevisionBytes);
                        if (firmRevision == null || firmRevision.isEmpty()) {
                            return;
                        }
                        String[] firmRevisionArray = getResources().getStringArray(R.array.firm_revision_group1);
                        boolean isGroup1 = false;
                        for (String revision : firmRevisionArray) {
                            if (revision.contains(firmRevision)) {
                                isGroup1 = true;
                                break;
                            }
                        }

                        String[] cesFirmRevisionCesArray = getResources().getStringArray(R.array.firm_revision_ces);
                        boolean isCesGroup = false;
                        for (String cesRevision : cesFirmRevisionCesArray) {
                            if (cesRevision.contains(firmRevision)) {
                                isCesGroup = true;
                                break;
                            }
                        }


                        if (isGroup1) {
                            setDateTimeDelay = 40L;
                            indicationDelay = 40L;
                        } else if (isCesGroup) {
                            setDateTimeDelay = 0L;
                            indicationDelay = 0L;
                        } else {
                            setDateTimeDelay = 100L;
                            indicationDelay = 100L;
                        }
                        uiThreadHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                BluetoothGatt gatt = BleReceivedService.getGatt();
                                boolean settingResult;
                                if (gatt != null) {
                                    String deviceName = gatt.getDevice().getName();
                                    settingResult = BleReceivedService.getInstance().setupDateTime(gatt);
                                    if (!settingResult) {
                                        dismissIndicator();
                                    }
                                } else {
                                    dismissIndicator();
                                }
                            }
                        }, setDateTimeDelay);
                    }
                } else if (BleReceivedService.TYPE_CHARACTERISTIC_WRITE.equals(type)) {
                    String serviceUuidString = intent.getStringExtra(BleReceivedService.EXTRA_SERVICE_UUID);
                    String characteristicUuidString = intent.getExtras().getString(BleReceivedService.EXTRA_CHARACTERISTIC_UUID);
                    if (serviceUuidString.equals(ADGattUUID.CurrentTimeService.toString())
                            || characteristicUuidString.equals(ADGattUUID.DateTime.toString())) {
                        if (shouldStartConnectDevice) {
                            uiThreadHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    BluetoothGatt gatt = BleReceivedService.getGatt();
                                    boolean writeResult = BleReceivedService.getInstance().setIndication(gatt, true);
                                    if (writeResult == false) {
                                        dismissIndicator();
                                    }
                                }
                            }, indicationDelay);
                        }
                    }
                } else if (BleReceivedService.TYPE_INDICATION_VALUE.equals(type)) {
                    setIndicatorMessage(getResources().getString(R.string.indicator_during_receive));
                    Bundle bundle = intent.getBundleExtra(BleReceivedService.EXTRA_VALUE);
                    String uuidString = intent.getExtras().getString(BleReceivedService.EXTRA_CHARACTERISTIC_UUID);
                    receivedData(uuidString, bundle);
                    uiThreadHandler.removeCallbacks(disableIndicationRunnable);
                    uiThreadHandler.postDelayed(disableIndicationRunnable, 4000L);
                } else if (BleReceivedService.TYPE_DESCRIPTOR_WRITE.equals(type)) {
                    //For now do this only for the UW-302
                    String device_name = intent.getExtras().getString(BleReceivedService.EXTRA_DEVICE_NAME);
                    if (device_name.contains("UW-302BLE")) {

                    }
                }
            }
        }
    };

    Runnable disableIndicationRunnable = new Runnable() {
        @Override
        public void run() {
            setIndicatorMessage(getResources().getString(R.string.indicator_complete_receive));
            BluetoothGatt gatt = BleReceivedService.getGatt();
            if (BleReceivedService.getInstance() != null) {
                boolean writeResult = BleReceivedService.getInstance().setIndication(gatt, false);
                if (writeResult == false) {
                    dismissIndicator();
                }
                //Add disconnect from smartphone.
                if (gatt != null) {
                    gatt.disconnect();
                }

                dismissIndicator();
            }
        }
    };

    private void receivedData(String characteristicUuidString, Bundle bundle) {
        if (ADGattUUID.WeightScaleMeasurement.toString().equals(characteristicUuidString) ||
                ADGattUUID.AndCustomWeightScaleMeasurement.toString().equals(characteristicUuidString)) {

            double weight = bundle.getDouble(ADGattService.KEY_WEIGHT);
            String units = bundle.getString(ADGattService.KEY_UNIT, ADSharedPreferences.DEFAULT_WEIGHT_SCALE_UNITS);

            int year = bundle.getInt(ADGattService.KEY_YEAR);
            int month = bundle.getInt(ADGattService.KEY_MONTH);
            int day = bundle.getInt(ADGattService.KEY_DAY);
            int hours = bundle.getInt(ADGattService.KEY_HOURS);
            int minutes = bundle.getInt(ADGattService.KEY_MINUTES);
            int seconds = bundle.getInt(ADGattService.KEY_SECONDS);

            String weightString = String.format(Locale.getDefault(), "%.1f", weight);
            String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);

            Lifetrack_infobean infoBeanObj = new Lifetrack_infobean();
            infoBeanObj.setWeight(weightString);
            infoBeanObj.setDate(finaldate);
            infoBeanObj.setTime(finaltime);
            infoBeanObj.setWeightUnit(units);

            ADSharedPreferences.putString(ADSharedPreferences.KEY_WEIGHT_SCALE_UNITS, units);

            infoBeanObj.setIsSynced("no");
            long dateValue = convertDateintoMs(finalTimeStamp);
            infoBeanObj.setDateTimeStamp(String.valueOf(dateValue));

            String weightDeviceId = "9DEA020D-1795-3B89-D184-DE7CD609FAD0";

            infoBeanObj.setDeviceId(weightDeviceId);
            final ArrayList<Lifetrack_infobean> insertObjectList = new ArrayList<Lifetrack_infobean>();
            insertObjectList.add(infoBeanObj);

            db.weighttrackentry(insertObjectList);

            insertObjectList.clear();

            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_WS, true);
        } else if (ADGattUUID.BloodPressureMeasurement.toString().equals(characteristicUuidString)) {

            int sys = (int) bundle.getFloat(ADGattService.KEY_SYSTOLIC);
            int dia = (int) bundle.getFloat(ADGattService.KEY_DIASTOLIC);
            int pul = (int) bundle.getFloat(ADGattService.KEY_PULSE_RATE);
            int irregularPulseDetection = bundle.getInt(ADGattService.KEY_IRREGULAR_PULSE_DETECTION);

            int year = bundle.getInt(ADGattService.KEY_YEAR);
            int month = bundle.getInt(ADGattService.KEY_MONTH);
            int day = bundle.getInt(ADGattService.KEY_DAY);

            int hours = bundle.getInt(ADGattService.KEY_HOURS);
            int minutes = bundle.getInt(ADGattService.KEY_MINUTES);
            int seconds = bundle.getInt(ADGattService.KEY_SECONDS);

            String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);

            Lifetrack_infobean infoBeanObj = new Lifetrack_infobean();
            infoBeanObj.setDate(finaldate);
            infoBeanObj.setTime(finaltime);
            infoBeanObj.setPulse(String.valueOf(pul));
            infoBeanObj.setSystolic(String.valueOf(sys));
            infoBeanObj.setDiastolic(String.valueOf(dia));
            BpObject = getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
            // Writing data to SharedPreferences
            SharedPreferences.Editor editor = BpObject.edit();
            editor.putString("systolic", String.valueOf(sys));
            editor.putString("diastolic", String.valueOf(dia));
            editor.commit();

            infoBeanObj.setPulseUnit("bpm");
            infoBeanObj.setSystolicUnit("mmhg");
            infoBeanObj.setDiastolicUnit("mmhg");
            infoBeanObj.setIsSynced("no");
            infoBeanObj.setIrregularPulseDetection(String.valueOf(irregularPulseDetection));
            long dateValue = convertDateintoMs(finalTimeStamp);
            infoBeanObj.setDateTimeStamp(String.valueOf(dateValue));
            String weightDeviceId = "web." + ADSharedPreferences.getString(ADSharedPreferences.KEY_USER_ID, "");

            infoBeanObj.setDeviceId(weightDeviceId);
            final ArrayList<Lifetrack_infobean> insertObjectList = new ArrayList<Lifetrack_infobean>();

            insertObjectList.add(infoBeanObj);
            db.bpEntry(insertObjectList);

            setIndicatorMessage(getResources().getString(R.string.indicator_complete_receive));

            insertObjectList.clear();
            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_BP, true);

        } else if (ADGattUUID.TemperatureMeasurement.toString().equals(characteristicUuidString)) {
            BluetoothGatt gatt = BleReceivedService.getGatt();

            String deviceName = gatt.getDevice().getName();
            float value = (float) bundle.getFloat(ADGattService.KEY_TEMPERATURE_VALUE);
            String unit = (String) bundle.getString(ADGattService.KEY_TEMPERATURE_UNIT);

            int year = (int) bundle.getInt(ADGattService.KEY_YEAR);
            int month = (int) bundle.getInt(ADGattService.KEY_MONTH);
            int day = (int) bundle.getInt(ADGattService.KEY_DAY);

            int hours = (int) bundle.getInt(ADGattService.KEY_HOURS);
            int minutes = (int) bundle.getInt(ADGattService.KEY_MINUTES);
            int seconds = (int) bundle.getInt(ADGattService.KEY_SECONDS);

            String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);

            Lifetrack_infobean thermometerInfo = new Lifetrack_infobean();
            thermometerInfo.setDate(finaldate);
            thermometerInfo.setTime(finaltime);
            long dateValue = convertDateintoMs(finalTimeStamp);
            thermometerInfo.setDateTimeStamp(String.valueOf(dateValue));

            thermometerInfo.setThermometerDeviceName(deviceName);
            thermometerInfo.setThermometerValue(String.valueOf(value));
            thermometerInfo.setThermometerUnit(unit);

            thermometerInfo.setIsSynced("no");
            String weightDeviceId = "web." + ADSharedPreferences.getString(ADSharedPreferences.KEY_USER_ID, "");

            thermometerInfo.setDeviceId(weightDeviceId);
            final ArrayList<Lifetrack_infobean> insertObjectList = new ArrayList<Lifetrack_infobean>();

            if (unit.equalsIgnoreCase(ADSharedPreferences.VALUE_TEMPERATURE_UNIT_F)) {
                if (!Locale.getDefault().equals(Locale.JAPAN)) {
                    insertObjectList.add(thermometerInfo);
                } else {
                    dismissIndicator();
                    return;
                }
            } else {
                insertObjectList.add(thermometerInfo);
            }

            if (unit.equalsIgnoreCase(ADSharedPreferences.VALUE_TEMPERATURE_UNIT_C)) {
                ADSharedPreferences.putString(ADSharedPreferences.KEY_TEMPERATURE_UNITS, ADSharedPreferences.VALUE_TEMPERATURE_UNIT_C);
            } else {
                ADSharedPreferences.putString(ADSharedPreferences.KEY_TEMPERATURE_UNITS, ADSharedPreferences.VALUE_TEMPERATURE_UNIT_F);
            }

            db.entryThermometerInfo(insertObjectList);

            setIndicatorMessage(getResources().getString(R.string.indicator_complete_receive));


            insertObjectList.clear();
            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_TH, true);
        } else if (ADGattUUID.AndCustomtrackerService.toString().equals(characteristicUuidString)) {

            ArrayList<Lifetrack_infobean> hashmapList = (ArrayList<Lifetrack_infobean>) bundle.getSerializable("activity_data");

            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_AM, true);

            //Checking if there is any BP data
            ArrayList<HashMap> bpMapList = (ArrayList<HashMap>) bundle.getSerializable("bp_data");
            ArrayList<Lifetrack_infobean> insertObjectList = new ArrayList<Lifetrack_infobean>(0);
            for (int i = 0; i < bpMapList.size(); i++) {
                HashMap<String, Object> bpData = bpMapList.get(i);
                Lifetrack_infobean infoBeanObj = new Lifetrack_infobean();

                //Extracting values from the hashmap
                int sys = Integer.parseInt(bpData.get("systolic").toString());
                int dia = Integer.parseInt(bpData.get("diastolic").toString());
                int pul = Integer.parseInt(bpData.get("pulse").toString());
                int year = Integer.parseInt(bpData.get("year").toString());
                int month = Integer.parseInt(bpData.get("month").toString());
                int day = Integer.parseInt(bpData.get("day").toString());
                int hours = Integer.parseInt(bpData.get("hour").toString());
                int minutes = Integer.parseInt(bpData.get("minutes").toString());
                int seconds = Integer.parseInt(bpData.get("seconds").toString());

                String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
                String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);
                infoBeanObj.setDate(finaldate);
                infoBeanObj.setTime(finaltime);
                infoBeanObj.setPulse(String.valueOf(pul));
                infoBeanObj.setSystolic(String.valueOf(sys));
                infoBeanObj.setDiastolic(String.valueOf(dia));
                infoBeanObj.setPulseUnit("bpm");
                infoBeanObj.setSystolicUnit("mmhg");
                infoBeanObj.setDiastolicUnit("mmhg");
                infoBeanObj.setIsSynced("no");
                //infoBeanObj.setIrregularPulseDetection(String.valueOf(irregularPulseDetection));
                long dateValue = convertDateintoMs(finalTimeStamp);
                infoBeanObj.setDateTimeStamp(String.valueOf(dateValue));
                infoBeanObj.setDeviceId("UW-302");
                insertObjectList.add(infoBeanObj);

            } //End of for loop , now add to database
            db.bpEntry(insertObjectList);
            setIndicatorMessage(getResources().getString(R.string.indicator_complete_receive));
            insertObjectList.clear();
            bpMapList.clear();
            measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_BP, true);

            //Check if there is weight scale data
            ArrayList<HashMap> wsMapList = (ArrayList<HashMap>) bundle.getSerializable("weight_data");

            for (int i = 0; i < wsMapList.size(); i++) {
                HashMap<String, Object> wsData = wsMapList.get(i);
                Lifetrack_infobean infoBeanObj = new Lifetrack_infobean();

                //Extracting values from the hashmap

                double weight = Double.parseDouble(wsData.get("weight").toString());
                String weightString = String.format(Locale.getDefault(), "%.1f", weight);
                String unit = wsData.get("unit").toString();
                //Add the weight value to the shared preference
                ADSharedPreferences.putString(ADSharedPreferences.KEY_WEIGHT_SCALE_UNITS, unit);
                int year = Integer.parseInt(wsData.get("year").toString());
                int month = Integer.parseInt(wsData.get("month").toString());
                int day = Integer.parseInt(wsData.get("day").toString());
                int hours = Integer.parseInt(wsData.get("hour").toString());
                int minutes = Integer.parseInt(wsData.get("minutes").toString());
                int seconds = Integer.parseInt(wsData.get("seconds").toString());
                String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
                String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);

                infoBeanObj.setWeight(weightString);
                infoBeanObj.setDate(finaldate);
                infoBeanObj.setTime(finaltime);
                infoBeanObj.setWeightUnit(unit);
                infoBeanObj.setIsSynced("no");
                //infoBeanObj.setIrregularPulseDetection(String.valueOf(irregularPulseDetection));
                long dateValue = convertDateintoMs(finalTimeStamp);
                infoBeanObj.setDateTimeStamp(String.valueOf(dateValue));
                infoBeanObj.setDeviceId("UW-302");
                insertObjectList.add(infoBeanObj);

            } //End of for loop , now add to database
            db.weighttrackentry(insertObjectList);
            setIndicatorMessage(getResources().getString(R.string.indicator_complete_receive));
            insertObjectList.clear();
            wsMapList.clear();
            measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_WS, true);

        }
    }

    private void refreshActivityMonitorLayout() {
        // Data Sync
        MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
        Lifetrack_infobean data = measuDataManager.getCurrentDispData(MeasuDataManager.MEASU_DATA_TYPE_AM);
        boolean isExistData = (data != null);
        if (isExistData) {
            activitymonitor.setHide(false);
            activitymonitor.setData(data);
        } else {
            if ((pairedDeviceList.size() == 0) ||
                    !(pairedDeviceList.contains("activityDevice"))) {
                activitymonitor.setHide(!isExistData);
            } else {
                activitymonitor.setDataNull(); //Activity device has been paired
            }


        }
        //activitymonitor.setHide(!isExistData);
    }

    private void refreshBloodPressureLayout() {
        Log.e("inside","refreshBloodPressureLayout");
        MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
        Lifetrack_infobean data = measuDataManager.getCurrentDispData(MeasuDataManager.MEASU_DATA_TYPE_BP);
        boolean isExistData = (data != null);
        if (isExistData) {
            bloodpressure.setData(data);
            System.out.println("===============Ashok====" + data.getSystolic() + "===" + data.getDiastolic());
        }
        bloodpressure.setHide(!isExistData);
    }

    private void refreshWeightScaleLayout() {
        MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
        Lifetrack_infobean data = measuDataManager.getCurrentDispData(MeasuDataManager.MEASU_DATA_TYPE_WS);
        boolean isExistData = (data != null);
        if (isExistData) {
            weightscale.setData(data);
            weightscale.setVisibility(View.VISIBLE);
        }
        weightscale.setHide(!isExistData);
    }

    private void refreshThermometerLayout() {
        MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
        Lifetrack_infobean data = measuDataManager.getCurrentDispData(MeasuDataManager.MEASU_DATA_TYPE_TH);
        boolean isExistData = (data != null);
        if (isExistData) {
            thermometer.setData(data);
        }
        thermometer.setHide(!isExistData);
    }

    private void refreshArrowVisible() {
        MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
        rightArrow.setVisibility((measuDataManager.isExistFutureDatas()) ? View.VISIBLE : View.INVISIBLE);
        leftArrow.setVisibility((measuDataManager.isExistPastDatas()) ? View.VISIBLE : View.INVISIBLE);
    }

    private void refreshDisplay(int dataType) {
        if (dataType == MeasuDataManager.MEASU_DATA_TYPE_BP) {
            refreshBloodPressureLayout();
        } else if (dataType == MeasuDataManager.MEASU_DATA_TYPE_WS) {
            refreshWeightScaleLayout();
        } else if (dataType == MeasuDataManager.MEASU_DATA_TYPE_TH) {
            refreshThermometerLayout();
        } else if (dataType == MeasuDataManager.MEASU_DATA_TYPE_AM) {
            refreshActivityMonitorLayout();
        }
    }

    private void refreshDisplay() {
        refreshActivityMonitorLayout();
        refreshBloodPressureLayout();
        refreshWeightScaleLayout();
        refreshThermometerLayout();
        refreshArrowVisible();
    }

    // 対象の項目の次のデータを表示
    private void moveDatasToTheFuture(int dataType) {
        AndMedical_App_Global appGlobal = (AndMedical_App_Global) getApplication();
        MeasuDataManager manager = appGlobal.getMeasuDataManager();
        if (manager != null) {
            manager.moveDatasToTheFuture(dataType);
        }

        refreshDisplay(dataType);
    }

    private final BroadcastReceiver mMeasudataUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MeasuDataManager.ACTION_AM_DATA_UPDATE.equals(action)) {
                refreshActivityMonitorLayout();
            } else if (MeasuDataManager.ACTION_BP_DATA_UPDATE.equals(action)) {
                Log.e("inside","broadCastReceiverBp");
                refreshBloodPressureLayout();
            } else if (MeasuDataManager.ACTION_WS_DATA_UPDATE.equals(action)) {
                refreshWeightScaleLayout();
            } else if (MeasuDataManager.ACTION_TH_DATA_UPDATE.equals(action)) {
                refreshThermometerLayout();
            }
            refreshArrowVisible();
        }
    };

    public void onBackPressed() {
        //ANDMedicalUtilities.CreateDialog(this, "Confirm To Exit", this);
//        finish();
        //Disable back button in Blood pressure
    }

    public long convertDateintoMs(String date) {
        long final_birth_date_timestamp = 0;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date converteddate = null;
        try {
            converteddate = (Date) formatter.parse(date);
            final_birth_date_timestamp = converteddate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return final_birth_date_timestamp;
    }


    private void doStartService() {
        Log.e("inside","doStartService");
        Intent intent1 = new Intent(this, BleReceivedService.class);
        startService(intent1);
        if (!mIsBleReceiver) {
            IntentFilter filter = new IntentFilter(BleReceivedService.ACTION_BLE_SERVICE);
            registerReceiver(bleServiceReceiver, filter);
            mIsBleReceiver = true;
        }

    }

    private void doStopService() {
        Log.e("inside","doStopService");
        if (mIsBleReceiver) {
            unregisterReceiver(bleServiceReceiver);
            mIsBleReceiver = false;
        }

        Intent intent1 = new Intent(this, BleReceivedService.class);
        stopService(intent1);
    }

    void isDevicePaired() {
        Log.e("inside","isDevicePaired");
        final BluetoothManager bluetoothManager = (BluetoothManager) DashboardActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);
        Set<BluetoothDevice> pairingDevices = null;
        pairingDevices = bluetoothManager.getAdapter().getBondedDevices();
        BluetoothDevice deviceName;
        pairedDeviceList.clear();//Clear the list , then initialize the list
        if (pairingDevices == null) {
            pairedDeviceList.clear();
            return;
        } else {
            for (BluetoothDevice bdevice : pairingDevices) {
                String name = bdevice.getName();
                if (name.contains("A&D")) {
                    //Check if its 651 or 352
                    if (name.contains("651")) {
                        //This is BP
                        Log.e("bpDevice_condition","");
                        if (!pairingDevices.contains("bpDevice")) {
                            pairedDeviceList.add("bpDevice");
                            Log.e("bpDevice_condition","");
                        }

                    } else if (name.contains("352")) {
                        //This is weight scale
                        if (!pairingDevices.contains("wsDevice")) {
                            pairedDeviceList.add("wsDevice");
                        }

                    }
                } else if (name.contains("UW-302")) {
                    //This is activity tracker
                    if (!pairingDevices.contains("activityDevice")) {
                        pairedDeviceList.add("activityDevice");
                    }
                }
            }

        }

    }

    public static String byte2hex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            tts.setSpeechRate(1);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(txt);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }


    private void speakOut(String textToSpeech) {
        String text = textToSpeech;
//        String text = "StartActivity me aapka swagat hain kripaya next button click kre aur aage badhe";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onClick(View view) {

        //Click events listener
        switch (view.getId()){

            case R.id.txtmainheight:
                context.startActivity(new Intent(this, Principal.class));
                break;

            case R.id.txtmainweight:
                context.startActivity(new Intent(this, ActofitMainActivity.class));
                break;

            case R.id.txtmaintempreture:
                context.startActivity(new Intent(this, ThermometerScreen.class));
                break;

            case R.id.txtmainpulseoximeter:
                context.startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
