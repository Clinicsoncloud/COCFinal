package com.abhaybmicoc.app.activity;

import android.Manifest;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.os.IBinder;
import android.os.Handler;
import android.app.Dialog;
import android.view.Window;
import android.app.Activity;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.ImageView;
import android.app.ProgressDialog;
import android.view.WindowManager;
import android.app.FragmentManager;
import android.widget.LinearLayout;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.content.DialogInterface;
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

import com.abhaybmicoc.app.services.TextToSpeechService;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.gatt.ADGattUUID;
import com.abhaybmicoc.app.entities.DataBase;
import com.abhaybmicoc.app.base.ADGattService;
import com.abhaybmicoc.app.gatt.BleReceivedService;
import com.abhaybmicoc.app.utilities.ScanRecordParser;
import com.abhaybmicoc.app.entities.Lifetrack_infobean;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.utilities.ADSharedPreferences;
import com.abhaybmicoc.app.utilities.ANDMedicalUtilities;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;
import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.view.BloodPressureDispalyDataLayout;

public class BloodPressureActivity extends Activity {
    // region Variables

    private Context context = BloodPressureActivity.this;

    private String txt = "";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1000;
    private static final int REQUEST_FINE_LOCATION = 2;

    private boolean isTryingScan;
    private boolean isScanning = false;
    private boolean mIsSendCancel = false;
    private boolean mIsBleReceiver = false;
    private boolean isBluetoothEnabled = false;
    private boolean shouldStartConnectDevice = false;
    private boolean isBindBleReceivedService = false;

    private long setDateTimeDelay = Long.MIN_VALUE;
    private long indicationDelay = Long.MIN_VALUE;

    private BloodPressureDispalyDataLayout layoutBloodPressure;

    private LinearLayout linearContainer;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvOximeter;
    private TextView tvTemperature;
    private TextView tvMobileNumber;

    private Dialog progress;

    private Button btnNext;

    private Handler uiThreadHandler = new Handler();

    private ProgressDialog pd;

    private DataBase db;
    private SharedPreferences sharedPreferencesPersonalData;
    private SharedPreferences sharedPreferencesBloodPressure;

    private ArrayList<BluetoothDevice> deviceList;
    ArrayList<String> pairedDeviceList = new ArrayList<String>();

    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private LinearLayout layoutSteps;

    private TextView tvResultMsg;

    private BluetoothAdapter bluetoothAdapter;

    private Handler featchingDataTimeoutHandler;
    private int FEATCHING_DATA_TIME = 1000 * 15;

    private String BLOOD_PRESSURE_MSG = "";

    TextToSpeechService textToSpeechService;

    private ProgressDialog progressDialog;

    // endregion

    // region Events

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setupUI();

        setupEvents();

        initializeData();

        requestPermission();

        requestGPSPermission();

        turnOnBluetooth();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        clearDataAndServices();

        if (textToSpeechService != null)
            textToSpeechService.stopTextToSpeech();
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableBluetooth();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestGPSPermission() {
        try {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            String provider = Settings.Secure.getString(getContentResolver(), LocationManager.GPS_PROVIDER);

            if (!statusOfGPS) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle("GPS Disabled");
                alertDialogBuilder.setMessage("Kindly make sure device location is on.")
                        .setCancelable(false)
                        .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 111);
                            }
                        });

                /* create alert dialog */
                AlertDialog alertDialog = alertDialogBuilder.create();
                /* show alert dialog */
                if (!((Activity) context).isFinishing())
                    alertDialog.show();
                alertDialogBuilder.setCancelable(false);
                // Notify users and show settings if they want to enable GPS
            }
        } catch (RuntimeException ex) {
            // TODO: Show message that we did not get permission to access bluetooth
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestPermission() {
        try {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FINE_LOCATION);
        } catch (RuntimeException ex) {
            // TODO: Show message that we did not get permission to access bluetooth
        }
    }

    /* Request enabling bluetooth if not enabled */
    private void turnOnBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }


    private void setStepsViewVisiblity() {
        layoutSteps.setVisibility(View.VISIBLE);
        layoutBloodPressure.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        doStopLeScan();
        doUnbindBleReceivedService();

        if (!mIsSendCancel) {
            mIsSendCancel = true;
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

    // endregion

    // region Initialization methods

    private void setupUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_blood_pressure);

        BLOOD_PRESSURE_MSG = getResources().getString(R.string.BloodPreeure_msg);

        tvHeight = findViewById(R.id.tv_header_height);
        tvWeight = findViewById(R.id.tv_header_weight);
        tvTemperature = findViewById(R.id.tv_header_tempreture);
        tvOximeter = findViewById(R.id.tv_header_pulseoximeter);

        layoutSteps = findViewById(R.id.layout_images);

        btnNext = findViewById(R.id.btn_skip);

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        tvResultMsg = findViewById(R.id.tv_result_msg);

        layoutBloodPressure = findViewById(R.id.layout_bp);
        linearContainer = findViewById(R.id.linearContainer);

        setStepsViewVisiblity();

        registerReceiver(mMeasudataUpdateReceiver, MeasuDataManager.MeasuDataUpdateIntentFilter());

        //Call function to get paired device
        checkIfDeviceIsPaired();
        doStartService();

        if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
            db = new DataBase(this);
        }

        sharedPreferencesPersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        db.deleteBpData(this);

    }

    /**
     *
     */
    private void setupEvents() {
        tvHeight.setOnClickListener(view -> context.startActivity(new Intent(this, HeightActivity.class)));
        tvWeight.setOnClickListener(view -> context.startActivity(new Intent(this, ActofitMainActivity.class)));
        tvOximeter.setOnClickListener(view -> context.startActivity(new Intent(this, MainActivity.class)));

        btnNext.setOnClickListener(v -> {
            try {
                Intent objIntent = new Intent(getApplicationContext(), ThermometerScreen.class);
                startActivity(objIntent);
                finish();
            } catch (Exception e) {
            }
        });
    }

    /**
     *
     */
    private void initializeData() {

        textToSpeechService = new TextToSpeechService(getApplicationContext(), BLOOD_PRESSURE_MSG);

        tvName.setText("Name : " + sharedPreferencesPersonalData.getString(Constant.Fields.NAME, ""));
        tvGender.setText("Gender : " + sharedPreferencesPersonalData.getString(Constant.Fields.GENDER, ""));
        tvAge.setText("DOB : " + sharedPreferencesPersonalData.getString(Constant.Fields.DATE_OF_BIRTH, ""));
        tvMobileNumber.setText("Phone : " + sharedPreferencesPersonalData.getString(Constant.Fields.MOBILE_NUMBER, ""));

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.commit();

        deviceList = new ArrayList<BluetoothDevice>();

        setDeviceConnectionTimeoutHandler();

    }

    private void setDeviceConnectionTimeoutHandler() {
        featchingDataTimeoutHandler = new Handler();

        featchingDataTimeoutHandler.postDelayed(() -> {
            if (!((Activity) context).isFinishing()) {
                progressDialog = Tools.progressDialog(BloodPressureActivity.this);
                progressDialog.setMessage("Fetching data...");
            }
        }, FEATCHING_DATA_TIME);
    }


    // endregion

    // region Logical methods

    /**
     * @param bytes
     * @return
     */
    public static String byte2hex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    /**
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
        Log.e("inside", "doStartService");
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
        Log.e("inside", "doStopService");
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
                        Log.e("bpDevice_condition", "");
                        if (!pairingDevices.contains("bpDevice")) {
                            pairedDeviceList.add("bpDevice");
                            Log.e("bpDevice_condition", "");
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
    private void enableBluetooth() {
        if (checkIfDeviceIsPaired()) {
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

        MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();

        Lifetrack_infobean data = measuDataManager.getCurrentDispData(MeasuDataManager.MEASU_DATA_TYPE_BP);

        boolean isExistData = (data != null);

        if (isExistData) {
            layoutBloodPressure.setVisibility(View.VISIBLE);
            layoutSteps.setVisibility(View.GONE);

            btnNext.setText("Next");
            btnNext.setBackground(getResources().getDrawable(R.drawable.greenback));

            layoutBloodPressure.setData(data);
        } else {
            layoutBloodPressure.setVisibility(View.GONE);
            layoutSteps.setVisibility(View.VISIBLE);

            btnNext.setText("Skip");
            btnNext.setBackground(getResources().getDrawable(R.drawable.repeat));
        }

        layoutBloodPressure.setHide(!isExistData);
    }


    private void refreshArrowVisible() {
        MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
    }

    private void refreshDisplay(int dataType) {
        if (dataType == MeasuDataManager.MEASU_DATA_TYPE_BP) {
            refreshBloodPressureLayout();
        }
    }

    private void refreshDisplay() {
        refreshBloodPressureLayout();
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
            Log.e("inside_condition", "dobindBleReceivedService");
        }
    }

    private void doUnbindBleReceivedService() {
        if (isBindBleReceivedService) {
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

            if (!progress.isShowing()) {
                progress.show();
            }
        }
    }

    private void setIndicatorMessage(String message) {
        if (progress == null)
            return;
        if (!((Activity) context).isFinishing() && progress.isShowing())
            progressDialog.dismiss();

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
        if (shouldStartConnectDevice)
            return;

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

            runOnUiThread(() -> {
                if (BleReceivedService.getInstance().getBluetoothManager().getAdapter() != null)
                    BleReceivedService.getInstance().getBluetoothManager().getAdapter().stopLeScan(mLeScanCallback);
            });
        }
    }

    private void doStartLeScan() {
        isTryingScan = true;

        doTryLeScan();
    }

    private void doTryLeScan() {
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

    private void clearDataAndServices() {
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
            String finalDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
            String finalTime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
            String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);

            Lifetrack_infobean infoBeanObj = new Lifetrack_infobean();
            infoBeanObj.setDate(finalDate);
            infoBeanObj.setTime(finalTime);
            infoBeanObj.setWeightUnit(units);
            infoBeanObj.setWeight(weightString);

            ADSharedPreferences.putString(ADSharedPreferences.KEY_WEIGHT_SCALE_UNITS, units);

            infoBeanObj.setIsSynced("no");
            long dateValue = convertDateToMilliSeconds(finalTimeStamp);
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

            sharedPreferencesBloodPressure = getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesBloodPressure.edit();
            editor.putString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, String.valueOf(sys));
            editor.putString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, String.valueOf(dia));
            editor.putString(Constant.Fields.PULSE_RATE, String.valueOf(pul));
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
            long dateValue = convertDateToMilliSeconds(finalTimeStamp);
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
                int day = Integer.parseInt(bpData.get("day").toString());
                int pul = Integer.parseInt(bpData.get("pulse").toString());
                int year = Integer.parseInt(bpData.get("year").toString());
                int hours = Integer.parseInt(bpData.get("hour").toString());
                int month = Integer.parseInt(bpData.get("month").toString());
                int sys = Integer.parseInt(bpData.get("systolic").toString());
                int dia = Integer.parseInt(bpData.get("diastolic").toString());
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
                long dateValue = convertDateToMilliSeconds(finalTimeStamp);
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

                int day = Integer.parseInt(wsData.get("day").toString());
                int year = Integer.parseInt(wsData.get("year").toString());
                int month = Integer.parseInt(wsData.get("month").toString());
                int hours = Integer.parseInt(wsData.get("hour").toString());
                int minutes = Integer.parseInt(wsData.get("minutes").toString());
                int seconds = Integer.parseInt(wsData.get("seconds").toString());

                String finalDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);
                String finalTime = String.format(Locale.getDefault(), "%02d:%02d", hours, minutes);
                String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", year, month, day, hours, minutes, seconds);

                infoBeanObj.setIsSynced("no");
                infoBeanObj.setDate(finalDate);
                infoBeanObj.setTime(finalTime);
                infoBeanObj.setWeightUnit(unit);
                infoBeanObj.setWeight(weightString);

                //infoBeanObj.setIrregularPulseDetection(String.valueOf(irregularPulseDetection));
                long dateValue = convertDateToMilliSeconds(finalTimeStamp);
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
            Log.e("onReceive_intentAction", "" + intent.getExtras().getString(BleReceivedService.EXTRA_TYPE));
            Log.e("onReceive_Type", " = " + BleReceivedService.EXTRA_TYPE);
            if (BleReceivedService.TYPE_GATT_CONNECTED.equals(type)) {
                linearContainer.setVisibility(View.VISIBLE);
                Log.e("inside", "onReceive_GATT_CONNECTED");
                try {
                    pd.dismiss();
                } catch (Exception e) {

                }

                showIndicator(getResources().getString(R.string.indicator_start_receive));
                BleReceivedService.getGatt().discoverServices();

                if (!((Activity) context).isFinishing() && progress.isShowing())
                    progressDialog.dismiss();

                setDateTimeDelay = Long.MIN_VALUE;
                indicationDelay = Long.MIN_VALUE;
            } else if (BleReceivedService.TYPE_GATT_DISCONNECTED.equals(type)) {
                Log.e("inside", "onReceive_GATT_DISCONNECTED");
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
                Log.e("inside", "onReceive_GATT_ERROR");
                int status = intent.getExtras().getInt(BleReceivedService.EXTRA_STATUS);
                if (status == 19) {
                    showAlertDialog();
                    return;
                }
                if (shouldStartConnectDevice) {
                    if (BleReceivedService.getInstance() != null) {
                        if (!BleReceivedService.getInstance().isConnectedDevice()) {
                            shouldStartConnectDevice = false;
                            Log.e("inside", "shouldStartConnectDevice" + shouldStartConnectDevice);
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
                    showAlertDialog();
                }
            } else {
                if (BleReceivedService.TYPE_GATT_SERVICES_DISCOVERED.equals(type)) {
                    Log.e("inside", "onReceive_GATT_DISCOVERED");
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

    private void showAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Not Connected");
        alertDialogBuilder.setMessage("Device is not connected, Please try again by clicking on Reconnect").setCancelable(false)
                .setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        layoutSteps.setVisibility(View.VISIBLE);
                        layoutBloodPressure.setVisibility(View.GONE);
                        tvResultMsg.setVisibility(View.VISIBLE);
                        btnNext.setText("Skip");
                    }
                });
        alertDialogBuilder.setNegativeButton("Skip Test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                skipNext();
            }
        });

        /* create alert dialog */
        AlertDialog alertDialog = alertDialogBuilder.create();
        /* show alert dialog */
        if (!((Activity) context).isFinishing())
            alertDialog.show();
        alertDialogBuilder.setCancelable(false);
    }

    /**
     *
     */
    private void skipNext() {
        context.startActivity(new Intent(BloodPressureActivity.this, GlucoseScanListActivity.class));
    }

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

    @Override
    public void onBackPressed() {
    }

    // endregion
}
