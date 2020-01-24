package com.abhaybmicoc.app.activity;

import android.app.Activity;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.os.IBinder;
import android.os.Handler;
import android.app.Dialog;
import android.view.Window;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.app.FragmentManager;
import android.widget.LinearLayout;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothGatt;
import android.speech.tts.TextToSpeech;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.view.View.OnClickListener;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.graphics.drawable.ColorDrawable;
import android.bluetooth.BluetoothAdapter.LeScanCallback;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.MeasuDataManager;
import com.abhaybmicoc.app.oximeter.MainActivity;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.glucose.GlucoseScanListActivity;

import java.util.Set;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.ArrayList;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.gatt.ADGattUUID;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.abhaybmicoc.app.entities.DataBase;
import com.abhaybmicoc.app.base.ADGattService;
import com.abhaybmicoc.app.gatt.BleReceivedService;
import com.abhaybmicoc.app.utilities.ScanRecordParser;
import com.abhaybmicoc.app.entities.Lifetrack_infobean;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.utilities.ADSharedPreferences;
import com.abhaybmicoc.app.utilities.ANDMedicalUtilities;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;
import com.abhaybmicoc.app.view.WeightScaleDisplayDataLayout;
import com.abhaybmicoc.app.view.ThermometerDisplayDataLayout;
import com.abhaybmicoc.app.view.BloodPressureDispalyDataLayout;
import com.abhaybmicoc.app.view.ActivityMonitorDisplayDataLayout;

public class BloodPressureActivity extends Activity implements TextToSpeech.OnInitListener {
    // region Variables

    private Context context = BloodPressureActivity.this;

    private String message;

    private static final int REQUEST_ENABLE_BLUETOOTH = 1000;

    private boolean isTryingScan;
    private boolean isScanning = false;
    private boolean mIsSendCancel = false;
    private boolean mIsBleReceiver = false;
    private boolean isBluetoothEnabled = false;
    private boolean shouldStartConnectDevice = false;
    private boolean isBindBleReceivedService = false;

    private long setDateTimeDelay = Long.MIN_VALUE;
    private long indicationDelay = Long.MIN_VALUE;

    private ThermometerDisplayDataLayout thermometer;
    private WeightScaleDisplayDataLayout weightscale;
    private ActivityMonitorDisplayDataLayout activitymonitor;
    private BloodPressureDispalyDataLayout layoutBloodPressure;

    private FrameLayout leftArrow;
    private FrameLayout rightArrow;
    private LinearLayout linearContainer;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvOximeter;
    private TextView tvTemperature;
    private TextView tvMobileNumber;
    private TextView tvLabel;

    private Dialog progress;

    private Button btnNext;

    private Handler uiThreadHandler = new Handler();

    private DataBase db;
    private SharedPreferences sharedPreferencesPersonalData;
    private SharedPreferences sharedPreferencesBloodPressure;

    private ArrayList<BluetoothDevice> deviceList;
    ArrayList<String> pairedDeviceList = new ArrayList<String>();

    private TextToSpeech textToSpeech;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private FrameLayout frameLayout;

    // endregion

    // region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        clearDataAndServices();
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableBluetooth();
    }

    @Override
    protected void onPause() {
        super.onPause();

        doStopLeScan();
        doUnbindBleReceivedService();

        if (!mIsSendCancel) {
            mIsSendCancel = true;
        }

        stopTextToSpeech();
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
    public void onInit(int status) {
        startTextToSpeech(status);
    }

    // endregion


    // region Initialization methods

    private void setupUI(){
        Log.e("setupUI","firstLog");
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.and_dashboard_new);

        tvHeight = findViewById(R.id.tv_header_height);
        tvWeight = findViewById(R.id.tv_header_weight);
        tvTemperature = findViewById(R.id.tv_header_tempreture);
        tvOximeter = findViewById(R.id.tv_header_pulseoximeter);

        tvLabel = findViewById(R.id.tv_label);
        
        frameLayout = findViewById(R.id.frame_result);

        registerReceiver(mMeasudataUpdateReceiver, MeasuDataManager.MeasuDataUpdateIntentFilter());

        //Call function to get paired device
        checkIfDeviceIsPaired();
        doStartService();

        layoutBloodPressure = findViewById(R.id.layout_bp);

        // Arrows
        rightArrow = findViewById(R.id.right_arrow);
        rightArrow.setVisibility(View.INVISIBLE);

        leftArrow = findViewById(R.id.left_arrow);
        leftArrow.setVisibility(View.INVISIBLE);

        TextView header = findViewById(R.id.header);
        header.setText(R.string.header_dashboard);

        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            db = new DataBase(this);
        }

        btnNext = findViewById(R.id.btn_next);

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);

        linearContainer = findViewById(R.id.linearContainer);

        sharedPreferencesPersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        db.deleteBpData(this);

        textToSpeech = new TextToSpeech(getApplicationContext(),this);
    }

    /**
     *
     */
    private void setupEvents(){
        tvHeight.setOnClickListener(view -> context.startActivity(new Intent(this, HeightActivity.class)));
        tvOximeter.setOnClickListener(view -> context.startActivity(new Intent(this, MainActivity.class)));
        tvWeight.setOnClickListener(view -> context.startActivity(new Intent(this, ActofitMainActivity.class)));
        tvTemperature.setOnClickListener(view -> context.startActivity(new Intent(this, ThermometerScreen.class)));

        btnNext.setOnClickListener(v -> {
            try{
                Intent objIntent = new Intent(getApplicationContext(), GlucoseScanListActivity.class);
                startActivity(objIntent);
                finish();
            }catch (Exception e){}
        });

        rightArrow.setOnClickListener(view -> {
            AndMedical_App_Global appGlobal = (AndMedical_App_Global) getApplication();
            MeasuDataManager manager = appGlobal.getMeasuDataManager();

            if (manager != null) {
                manager.moveDatasToThePast();
                refreshDisplay();
            }
        });

        leftArrow.setOnClickListener(view -> {
            AndMedical_App_Global appGlobal = (AndMedical_App_Global) getApplication();
            MeasuDataManager manager = appGlobal.getMeasuDataManager();

            if (manager != null) {
                manager.moveDatasToTheFuture();
                refreshDisplay();
            }
        });
    }

    /**
     *
     */
    private void initializeData(){

        btnNext.setText("Skip Test");

        message = "Wear the cuff and press device start button";
        speakOut(message);

        tvLabel.setText(message);

        tvName.setText("Name : " + sharedPreferencesPersonalData.getString(Constant.Fields.NAME, ""));
        tvGender.setText("Gender : " + sharedPreferencesPersonalData.getString(Constant.Fields.GENDER, ""));
        tvAge.setText("DOB : " + sharedPreferencesPersonalData.getString(Constant.Fields.DATE_OF_BIRTH, ""));
        tvMobileNumber.setText("Phone : " + sharedPreferencesPersonalData.getString(Constant.Fields.MOBILE_NUMBER, ""));

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.commit();

        deviceList = new ArrayList<BluetoothDevice>();
    }

    // endregion

    // region Logical methods

    /**
     *
     * @param text
     */
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

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
                speakOut(message);
            }

        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    /**
     *
     */
    private void stopTextToSpeech(){
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
     * @param bytes
     * @return
     */
    public static String byte2hex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    /**
     * 
     * @param date
     * @return
     */
    public long convertDateToMilliSeconds(String date) {
        long timestamp = 0;

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        Date convertedDate = null;
        try {
            convertedDate = formatter.parse(date);
            timestamp = convertedDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }

    /**
     *
     */
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

    /**
     *
     */
    private void doStopService() {
        Log.e("inside","doStopService");
        if (mIsBleReceiver) {
            unregisterReceiver(bleServiceReceiver);
            mIsBleReceiver = false;
        }

        Intent intent1 = new Intent(this, BleReceivedService.class);
        stopService(intent1);
    }

    /**
     *
     */
    private boolean checkIfDeviceIsPaired() {
        Log.e("checkIfDeviceIsPaired","firstLog");
        final BluetoothManager bluetoothManager = (BluetoothManager) BloodPressureActivity.this.getSystemService(Context.BLUETOOTH_SERVICE);

        Set<BluetoothDevice> pairingDevices = bluetoothManager.getAdapter().getBondedDevices();

        /* Clear and initialize the list */
        pairedDeviceList.clear();

        if (pairingDevices == null || pairingDevices.isEmpty()) {
            return false;
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
                            Log.e("bpDevice_condition",""+pairedDeviceList);
                        }
                    }
                }
            }
            return true;
        }
    }

    /**
     *
     */
    private void turnBluetoothOff() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    /**
     *
     */
    private void enableBluetooth(){
        Log.e("enableBluetooth","log");
        if(checkIfDeviceIsPaired()) {

            Log.e("enableBluetooth","inside_if");

            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();

            if (measuDataManager == null) {
                measuDataManager = new MeasuDataManager(this);
                ((AndMedical_App_Global) getApplication()).setMeasuDataManager(measuDataManager);
                measuDataManager.syncAllMeasuDatas(true);
            } else {
                measuDataManager.syncAllMeasuDatas(true);
                refreshDisplay();
            }

            if (mIsSendCancel)
                mIsSendCancel = false;

            BluetoothManager bluetoothManager = getBluetoothManager();
            if (bluetoothManager != null) {
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter != null) {
                    if (!bluetoothAdapter.isEnabled()) {
                        if (!isBluetoothEnabled) {
                            isBluetoothEnabled = true;
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
                            return;
                        }
                    } else {
                        doBindBleReceivedService();
                    }
                }
            }

            isBluetoothEnabled = false;
        }
    }


    private void refreshBloodPressureLayout() {

        Log.e("refreshBloodPressure","firstLog");

        MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();

        Lifetrack_infobean data = measuDataManager.getCurrentDispData(MeasuDataManager.MEASU_DATA_TYPE_BP);

        boolean isExistData = (data != null);

        if (isExistData) {
            frameLayout.setVisibility(View.VISIBLE);
            layoutBloodPressure.setData(data);
        }
        layoutBloodPressure.setHide(!isExistData);
    }




    private void refreshArrowVisible() {
        MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();

        leftArrow.setVisibility((measuDataManager.isExistPastDatas()) ? View.VISIBLE : View.INVISIBLE);
        rightArrow.setVisibility((measuDataManager.isExistFutureDatas()) ? View.VISIBLE : View.INVISIBLE);
    }

    private void refreshDisplay(int dataType) {
        if (dataType == MeasuDataManager.MEASU_DATA_TYPE_BP) {
            refreshBloodPressureLayout();
        }
    }

    private void refreshDisplay() {
        refreshBloodPressureLayout();
        refreshArrowVisible();
    }

    // 対象の項目の次のデータを表示
    private void moveDatasToTheFuture(int dataType) {
        AndMedical_App_Global appGlobal = (AndMedical_App_Global) getApplication();

        MeasuDataManager manager = appGlobal.getMeasuDataManager();

        if (manager != null)
            manager.moveDatasToTheFuture(dataType);

        refreshDisplay(dataType);
    }


    public BluetoothManager getBluetoothManager() {
        return (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
    }

    private void doBindBleReceivedService() {
        if (!isBindBleReceivedService) {
            bindService(new Intent(BloodPressureActivity.this,
                    BleReceivedService.class), mBleReceivedServiceConnection, Context.BIND_AUTO_CREATE);
            isBindBleReceivedService = true;
            Log.e("inside_condition","dobindBleReceivedService");
        }
    }

    private void doUnbindBleReceivedService() {
        if (isBindBleReceivedService) {
            Log.e("doUnbindBleReceived","firstLog");
            unbindService(mBleReceivedServiceConnection);
            isBindBleReceivedService = false;
        }
    }


    private void showIndicator(String message) {
        if (!(BloodPressureActivity.this).isFinishing()) {
            if (progress == null) {
                progress = new Dialog(BloodPressureActivity.this);
                progress.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progress.setContentView(R.layout.custom_alert);
                progress.setCancelable(false);
            }

            setIndicatorMessage(message);

            Log.e("showIndicator","firstLog");

            if (!progress.isShowing()) {
                progress.show();
            }
        }
    }

    private void setIndicatorMessage(String message) {
        if (progress == null)
            return;

        TextView syncMessages = (TextView) progress.findViewById(R.id.syncMessages1);

        if (message == null)
            message = "";


        if (syncMessages != null)
            syncMessages.setText(message);
    }

    private void dismissIndicator() {
        if (progress == null)
            return;

        if (progress.isShowing())
            progress.dismiss();


        progress = null;
    }

    private void startScan() {
        Log.e("startScan","firstLog");
        if (shouldStartConnectDevice)
            return;

        if (BleReceivedService.getInstance() != null) {
            if (BleReceivedService.getInstance().isConnectedDevice())
                BleReceivedService.getInstance().disconnectDevice();

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
        Log.e("stopScan","firstLog");
        if (BleReceivedService.getInstance() != null) {
            isScanning = false;

            runOnUiThread(() -> {
                if (BleReceivedService.getInstance().getBluetoothManager().getAdapter() != null)
                    BleReceivedService.getInstance().getBluetoothManager().getAdapter().stopLeScan(mLeScanCallback);
            });
        }
    }

    private void doStartLeScan() {
        Log.e("doStartLeScan","firstLog");
        isTryingScan = true;

        doTryLeScan();
    }

    private void doTryLeScan() {
        Log.e("doTryLeScan","firstLog");
        if (!isTryingScan)
            return;

        if (!isScanning)
            startScan();

        isTryingScan = false;
    }

    private void doStopLeScan() {
        if (isScanning)
            stopScan();
    }

    private void clearDataAndServices(){
        Log.e("clearDataAndServices","firstLog");
        dismissIndicator();
        doStopService();
        unregisterReceiver(mMeasudataUpdateReceiver);
    }

    private boolean isAbleToConnectDevice(BluetoothDevice device, byte[] scanRecord) {
        if (BleReceivedService.getInstance().isConnectedDevice())
            return false;

        BluetoothAdapter bluetoothAdapter = BleReceivedService.getInstance().getBluetoothManager().getAdapter();

        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> pairingDevices = bluetoothAdapter.getBondedDevices();

            if (device.getName() != null)
                return pairingDevices.contains(device) && device.getName().contains("A&D");
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

                    if (manufacturerSpecificData != null && manufacturerSpecificData.length == 3) {
                        int value = manufacturerSpecificData[2];

                        // 1:Paired, 3:Paired but need to set time
                        if (value == 1 || value == 3)
                            return true;
                        else
                            return false;
                    } else {
                        return false;
                    }
                } else
                    return false;
            } else
                return false;
        } else
            return false;
    }

    private void receivedData(String characteristicUuidString, Bundle bundle) {
        Log.e("receivedData","firstLog");
        if (ADGattUUID.BloodPressureMeasurement.toString().equals(characteristicUuidString)) {

            Log.e("receivedBloodPresure","firstLog");

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

            sharedPreferencesBloodPressure = getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesBloodPressure.edit();
            editor.putString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, String.valueOf(sys));
            editor.putString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, String.valueOf(dia));
            editor.commit();

            infoBeanObj.setIsSynced("no");
            infoBeanObj.setPulseUnit("bpm");
            infoBeanObj.setSystolicUnit("mmhg");
            infoBeanObj.setDiastolicUnit("mmhg");
            infoBeanObj.setIrregularPulseDetection(String.valueOf(irregularPulseDetection));

            long dateValue = convertDateToMilliSeconds(finalTimeStamp);
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

        }
    }

    // endregion

    // region Broadcasters

    private ServiceConnection mBleReceivedServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            doStartLeScan();
        }
    };

    private LeScanCallback mLeScanCallback = new LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.e("LeScanCallback","firstLog");
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

                showIndicator(getResources().getString(R.string.indicator_start_receive));
                BleReceivedService.getGatt().discoverServices();

                setDateTimeDelay = Long.MIN_VALUE;
                indicationDelay = Long.MIN_VALUE;
            } else if (BleReceivedService.TYPE_GATT_DISCONNECTED.equals(type)) {
                Log.e("inside","onReceive_GATT_DISCONNECTED");
                dismissIndicator();
                if (shouldStartConnectDevice) {
                    linearContainer.setVisibility(View.VISIBLE);

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

    private final BroadcastReceiver mMeasudataUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (MeasuDataManager.ACTION_BP_DATA_UPDATE.equals(action)) {
                refreshBloodPressureLayout();
            }

            refreshArrowVisible();
        }
    };

    // endregion
}