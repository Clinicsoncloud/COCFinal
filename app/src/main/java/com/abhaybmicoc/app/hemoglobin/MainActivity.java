package com.abhaybmicoc.app.hemoglobin;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.activity.BloodPressureActivity;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.activity.VisionActivity;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;
import com.abhaybmicoc.app.glucose.GlucoseScanListActivity;
import com.abhaybmicoc.app.hemoglobin.util.BluetoothUtils;
import com.abhaybmicoc.app.hemoglobin.util.StringUtils;
import com.abhaybmicoc.app.printer.esys.pridedemoapp.Act_GlobalPool;
import com.abhaybmicoc.app.printer.esys.pridedemoapp.Act_Main;
import com.abhaybmicoc.app.printer.evolute.bluetooth.BluetoothComm;
import com.abhaybmicoc.app.services.TextToSpeechService;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.Utils;
import com.prowesspride.api.Printer_GEN;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.abhaybmicoc.app.hemoglobin.Constants.SCAN_PERIOD;
import static com.abhaybmicoc.app.hemoglobin.Constants.SERVICE_UUID;

public class MainActivity extends AppCompatActivity implements GattClientActionListener {
    // region Variables

    private Context context = MainActivity.this;

    private int COUNT_CONNECTION_TRY = 0;
    private int COUNT_CONNECTION_MAXIMUM_TRY = 3;
    private int DEVICE_CONNECTION_WAITING_TIME = 1000 * 10;
    private int STATR_TEST_ACTIVATION_TIME = 2000;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;


    private BluetoothGatt mGatt;
    private ScanCallback mScanCallback;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private Map<String, BluetoothDevice> mapBluetoothScanResults;

    private Handler mHandler;
    private Handler deviceConnectionTimeoutHandler;
    private Handler deviceConnectionUpdate;

    private boolean mScanning;
    private boolean isDeviceConnected;
    private boolean isEchoInitialized;

    private String txt;

    private TextView tvName;
    private TextView tvGender;
    private TextView tvMainSugar;
    private TextView tvViewDevice;
    private TextView tvMainHeight;
    private TextView tvMainWeight;
    private TextView tvViewDisplay;
    private TextView tvMobileNumber;
    private TextView tvMainOximeter;
    private TextView textDateOfBirth;
    private TextView tvMainBpMonitor;
    private TextView tvMainTemperature;
    private TextView tvMainVision;

    private SharedPreferences sharedPreferencesDevice;
    private SharedPreferences sharedPreferencesPersonal;
    private SharedPreferences sharedPreferencesDeviceHemoglobin;

    private Button btnNext;
    private Button btnScan;
    private Button btnTest;
    private Button btnConnect;
    private Spinner spinnerDevice;

    private ProgressDialog dialogProgress;
    private ProgressDialog dialogScanProgress;
    private ProgressDialog dialogConnectionProgress;

    ArrayList<String> deviceArrayList;

    private String HEMOGLOBIN_MSG = "";
    private String START_TEST_MSG = "";

    TextToSpeechService textToSpeechService;

    public static AndMedical_App_Global mGP = null;
    public static BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
    public static BluetoothDevice mBDevice = null;

    public static int iWidth;
    public static Printer_GEN ptrGen;
    String is_PrinterConnected = "false";

    // endregion

    // region Events

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
    }

    @Override
    public void onBackPressed() {
    }


    @Override
    protected void onResume() {
        super.onResume();

        checkBluetoothLe();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (tvViewDisplay.getText().toString().contains("Test")) {
//            sendMessage("U370");
            disconnectGattServer();
        }
    }


    @Override
    public void showToast(final String msg) {
        new Thread() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (msg.contains("Test")) {
                            dialogProgress.show();
                        } else if (msg.contains("Hb = ")) {
                            dialogProgress.dismiss();

                            try {
                                String hbvalue = msg;

                                hbvalue = hbvalue.replaceAll("[^0-9.]", "");

                                String device_name = sharedPreferencesDevice.getString("devicename", "NA");
                                String device_address = sharedPreferencesDevice.getString("device", "NA");

                                SharedPreferences.Editor editor = sharedPreferencesDeviceHemoglobin.edit();
                                editor.putString("hemoglobin", hbvalue);
                                editor.commit();

                            } catch (Exception e) {
                                showToast(e.toString());
                            }
                        }
                    }
                });
            }
        }.start();

        //for toast msg n
        new Thread() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        tvViewDisplay.setText("");
                        tvViewDisplay.append(msg + "\n");
                    }
                });
            }
        }.start();
    }

    @Override
    public void setConnected(boolean connected) {
        mScanning = false;

        setStartTestTimerHandler(connected);

        connectToSavedPrinter();
    }


    private void setStartTestTimerHandler(boolean connected) {
        deviceConnectionUpdate = new Handler(Looper.getMainLooper());
        deviceConnectionUpdate.postDelayed(() -> {
            updateConnectionStatus(connected);
        }, STATR_TEST_ACTIVATION_TIME);
    }

    private void connectToSavedPrinter() {
        SharedPreferences data = getSharedPreferences("printer", MODE_PRIVATE);

        if (data.getString("NAME", "").length() > 0) {
            mBDevice = mBT.getRemoteDevice(data.getString("MAC", ""));
            autoConnectPrinter();
        }
    }

    private void autoConnectPrinter() {

        mGP.closeConn();
        new ConnSocketTask().execute(mBDevice.getAddress());
    }

    private class ConnSocketTask extends AsyncTask<String, String, Integer> {
        /**
         * Process waits prompt box
         */
        private ProgressDialog mpd = null;
        /**
         * Constants: connection fails
         */
        private static final int CONN_FAIL = 0x01;
        /**
         * Constant: the connection is established
         */
        private static final int CONN_SUCCESS = 0x02;

        /**
         * Thread start initialization
         */
        @Override
        public void onPreExecute() {
        }

        /* Task of  performing in the background*/

        @Override
        protected Integer doInBackground(String... arg0) {
            if (mGP.createConn(arg0[0])) {
                SystemClock.sleep(2000);
                return CONN_SUCCESS;
            } else {
                return CONN_FAIL;
            }
        }

        /* This displays the status messages of in the dialog box */
        @Override
        public void onPostExecute(Integer result) {
            if (CONN_SUCCESS == result) {

                is_PrinterConnected = "true";
                printerActivation();
            }
        }
    }

    private void printerActivation() {
        try {
            iWidth = getWindowManager().getDefaultDisplay().getWidth();
            InputStream input = BluetoothComm.misIn;
            OutputStream outstream = BluetoothComm.mosOut;
            ptrGen = new Printer_GEN(Act_GlobalPool.setup, outstream, input);

        } catch (Exception e) {
        }
    }

    @Override
    public void log(String message) {
    }

    @Override
    public void logError(String message) {
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
    protected void onStop() {
        super.onStop();
//        sendMessage("U370");
        disconnectGattServer();
    }

    // endregion

    // region Initialization

    /**
     *
     */
    private void setupUI() {
        setContentView(R.layout.new_try_hemoglobin);

        HEMOGLOBIN_MSG = getResources().getString(R.string.hemoglobin_msg);
        START_TEST_MSG = getResources().getString(R.string.hemoglobin_start_test_msg);

        btnNext = findViewById(R.id.btn_skip);
        btnScan = findViewById(R.id.btn_scan);
        btnTest = findViewById(R.id.btn_test);
        btnConnect = findViewById(R.id.btn_connect);

        btnConnect.setText("Connect");
        btnConnect.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect1));
        btnConnect.setClickable(true);

        spinnerDevice = findViewById(R.id.sp_device_list);

        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvViewDisplay = findViewById(R.id.display);
        textDateOfBirth = findViewById(R.id.tv_age);
        tvViewDevice = findViewById(R.id.textdevice);
        tvMainHeight = findViewById(R.id.tv_header_height);
        tvMainWeight = findViewById(R.id.tv_header_weight);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        tvMainSugar = findViewById(R.id.tv_header_sugar);
        tvMainTemperature = findViewById(R.id.tv_header_tempreture);
        tvMainVision = findViewById(R.id.tv_header_vision);
        tvMainOximeter = findViewById(R.id.tv_header_pulseoximeter);
        tvMainBpMonitor = findViewById(R.id.tv_header_bloodpressure);

        deviceArrayList = new ArrayList();
    }

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupEvents() {
        btnTest.setOnClickListener(view -> test());
        btnScan.setOnClickListener(view -> scanDevices());
        btnConnect.setOnClickListener(view -> connectToScannedDevice());
        btnNext.setOnClickListener(view -> switchOffDeviceAndMoveNext());

        tvMainHeight.setOnClickListener(view -> {
            context.startActivity(new Intent(MainActivity.this, HeightActivity.class));
        });

        tvMainWeight.setOnClickListener(view -> {
            context.startActivity(new Intent(MainActivity.this, ActofitMainActivity.class));
        });

        tvMainTemperature.setOnClickListener(view -> {
            context.startActivity(new Intent(MainActivity.this, ThermometerScreen.class));
        });

        tvMainOximeter.setOnClickListener(view -> {
            context.startActivity(new Intent(MainActivity.this, com.abhaybmicoc.app.oximeter.MainActivity.class));
        });

        tvMainBpMonitor.setOnClickListener(view -> {
            context.startActivity(new Intent(MainActivity.this, BloodPressureActivity.class));
        });

        tvMainVision.setOnClickListener(view -> {
            context.startActivity(new Intent(MainActivity.this, VisionActivity.class));
        });

        tvMainSugar.setOnClickListener(view -> {
            context.startActivity(new Intent(MainActivity.this, GlucoseScanListActivity.class));
        });
    }

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializeData() {
        setupTextToSpeech();

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        sharedPreferencesDevice = getSharedPreferences("device_data", MODE_PRIVATE);
        sharedPreferencesDeviceHemoglobin = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
        sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        mGP = ((AndMedical_App_Global) getApplicationContext());

        setUserInfo();

        showProgressDialog();

        turnOnBluetooth();

        requestPermission();

        // connection change and services discovered.
        connectOrShowScanDevice();
    }

    private void setupTextToSpeech() {
        if (Utils.isOnline(context))
            textToSpeechService = new TextToSpeechService(getApplicationContext(), HEMOGLOBIN_MSG);
    }

    private void setUserInfo() {
        tvName.setText("Name : " + sharedPreferencesPersonal.getString(Constant.Fields.NAME, ""));
        tvGender.setText("Gender : " + sharedPreferencesPersonal.getString(Constant.Fields.GENDER, ""));
        textDateOfBirth.setText("DOB : " + sharedPreferencesPersonal.getString(Constant.Fields.DATE_OF_BIRTH, ""));
        tvMobileNumber.setText("Phone : " + sharedPreferencesPersonal.getString(Constant.Fields.MOBILE_NUMBER, ""));
    }

    // endregion

    // region Logical methods

    /**
     * @param msg
     */
    private void sendMessage(String msg) {

        if (!isDeviceConnected || !isEchoInitialized) {
            showToast("Not Connected");
            return;
        }

        BluetoothGattCharacteristic characteristic = BluetoothUtils.findEchoCharacteristic(mGatt);
        if (characteristic == null) {
            logError("Unable to find echo characteristic.");
//            sendMessage("U370");
            disconnectGattServer();
            return;
        }

        byte[] messageBytes = StringUtils.bytesFromString(msg);
        if (messageBytes.length == 0) {
            logError("Unable to convert message to bytes");
            return;
        }

        characteristic.setValue(messageBytes);
        boolean success = mGatt.writeCharacteristic(characteristic);

        if (success)
            showToast("MSG SENT");
        else
            logError("Failed to write data");
    }

    /**
     * @param view
     */
    public void readBatchCode(View view) {
        sendMessage("U402");
    }

    /**
     * @param view
     */
    public void writeBatchCode(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText input = new EditText(MainActivity.this);
        input.setHint("Batch Code");

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        builder.setView(input);

        builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String code = input.getText().toString();
                if (code.isEmpty()) {
                    input.setError("Not Left Blank");
                } else {
                    sendMessage("U403" + code);
                }
            }
        });

        builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void switchOffDeviceAndMoveNext() {
//        sendMessage("U370");
        disconnectGattServer();

        try {
            AndMedical_App_Global.mBTcomm = null;
        } catch (NullPointerException e) {
        }

        Intent intent = new Intent(MainActivity.this, Act_Main.class);
        intent.putExtra("is_PrinterConnected", is_PrinterConnected);
        startActivity(intent);

//        startActivity(new Intent(MainActivity.this, Act_Main.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermissions() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            requestBluetoothEnable();
            return false;

        } else if (!hasLocationPermissions()) {
            requestPermission();
            return false;
        }

        return true;
    }

    /**
     *
     */
    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    public void test() {
        sendMessage("U401");
        sendMessage("U401");
    }

    private void checkBluetoothLe() {
        /* Check bluetooth low energy support */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // Get a newer device
            logError("No BLE Support.");
            showToast("No BLE Support.");
            finish();
        }
    }

    /**
     * @param view
     */
    public void readLastTest(View view) {
        sendMessage("U502");
    }

    /**
     *
     */
    private void connectOrShowScanDevice() {
        /**
         * On load, check if device is stored in local storage
         * If yes, connect
         * If no, show scan button
         */

        if (savedDeviceAlreadyExists()) {
            btnScan.setVisibility(View.GONE);
            btnConnect.setVisibility(View.VISIBLE);
//            connect();
        } else {
            btnScan.setVisibility(View.VISIBLE);
            btnConnect.setVisibility(View.GONE);
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

        if (connected) {

            COUNT_CONNECTION_TRY = 0;

            dialogConnectionProgress.dismiss();

            tvViewDevice.setText("Connected to : " + getStoredDeviceName());

            btnConnect.setText("Connected");
            btnConnect.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect2));
            btnConnect.setClickable(false);
            if (Utils.isOnline(context))
                textToSpeechService.speakOut(START_TEST_MSG);
        } else {
            COUNT_CONNECTION_TRY++;

            if (COUNT_CONNECTION_TRY == COUNT_CONNECTION_MAXIMUM_TRY) {
                tvViewDevice.setText("Cannot connect to device, scan the device again");

                btnScan.setVisibility(View.VISIBLE);
            } else {
                tvViewDevice.setText("Cannot connect to device, trying again");

                connect();
            }
        }
    }

    private boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        } catch (Exception localException) {
        }
        return false;
    }

    /**
     *
     */
    private void connect() {
//        disconnectGattServer();

        setDeviceConnectionTimeoutHandler();

        dialogConnectionProgress.show();

        BluetoothDevice device = getDevice(getStoredDeviceAddress());

        GattClientCallback gattClientCallback = new GattClientCallback(this);

        mGatt = device.connectGatt(MainActivity.this, false, gattClientCallback);

        // TODO: Check state of connection
    }


    /**
     *
     */
    private void connectToScannedDevice() {
        /**
         * Show progress dialog for connecting
         * Hide connect button
         * Save device information to local storage
         * Clear spinner
         * Connect to device
         */

        if (savedDeviceAlreadyExists()) {
            connect();
        } else {
            dialogConnectionProgress.show();
            btnScan.setVisibility(View.GONE);

            String deviceName = spinnerDevice.getSelectedItem().toString();
            String deviceAddress = deviceName.substring(deviceName.length() - 17);
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

            saveDeviceInformation(device.getName(), device.getAddress());

            deviceArrayList.clear();

            ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.text1, R.id.text1, deviceArrayList);
            adapter.notifyDataSetChanged();

            spinnerDevice.setAdapter(adapter);

            connect();
        }
    }

    private void setDeviceConnectionTimeoutHandler() {
        deviceConnectionTimeoutHandler = new Handler();

        deviceConnectionTimeoutHandler.postDelayed(() -> {
            if (dialogConnectionProgress != null && dialogConnectionProgress.isShowing()) {
                dialogConnectionProgress.dismiss();

//                sendMessage("U370");
                stopBluetoothConnection();
            }

        }, DEVICE_CONNECTION_WAITING_TIME);
    }

    private void stopBluetoothConnection() {
        if (dialogProgress != null)
            dialogProgress.dismiss();

        isDeviceConnected = false;
        isEchoInitialized = false;

        tvViewDevice.setText("Please check device and try again");
        btnConnect.setText("Connect");
        btnConnect.setVisibility(View.VISIBLE);

        btnConnect.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect1));
        btnConnect.setClickable(true);

        if (mGatt != null) {
            try {
                mGatt.disconnect();
                mGatt.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private String getStoredDeviceName() {
        return sharedPreferencesDevice.getString("deviceName", "");
    }

    /**
     * @return
     */
    private String getStoredDeviceAddress() {
        return sharedPreferencesDevice.getString("deviceAddress", "");
    }

    /**
     * @return
     */
    private boolean savedDeviceAlreadyExists() {
        return !sharedPreferencesDevice.getString("deviceName", "").equals("");
    }

    /**
     * @return
     */
    private BluetoothDevice getDevice(String deviceName) {
        return bluetoothAdapter.getRemoteDevice(deviceName);
    }

    /**
     *
     */
    private void showProgressDialog() {
        dialogProgress = new ProgressDialog(MainActivity.this);
        dialogProgress.setMessage("Processing...");
        dialogProgress.setCancelable(false);
        dialogProgress.setButton(ProgressDialog.BUTTON_POSITIVE, "Abort Test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendMessage("U371");
            }
        });

        dialogScanProgress = new ProgressDialog(MainActivity.this);
        dialogScanProgress.setCancelable(false);
        dialogScanProgress.setMessage("Scanning...");
        dialogScanProgress.setButton(ProgressDialog.BUTTON_POSITIVE, "Stop Scan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogConnectionProgress = new ProgressDialog(MainActivity.this);
        dialogConnectionProgress.setMessage("Connecting...");
        dialogConnectionProgress.setCancelable(true);


        dialogConnectionProgress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
    }

    /* Request enabling bluetooth if not enabled */
    private void turnOnBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestPermission() {
        try {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FINE_LOCATION);
        } catch (RuntimeException ex) {
            // TODO: Show message that we did not get permission to access bluetooth
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasLocationPermissions() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void scanDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions() || mScanning) {
                return;
            }
        }

        disconnectGattServer();

        dialogScanProgress.show();

        mapBluetoothScanResults = new HashMap<>();
        mScanCallback = new BluetoothLeScanCallback(mapBluetoothScanResults);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }

        // Note: Filtering does not work the same (or at all) on most devices. It also is unable to
        // search for a mask or anything less than a full UUID.
        // Unless the full UUID of the server is known, manual filtering may be necessary.
        // For example, when looking for a brand of device that contains a char sequence in the UUID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ScanFilter scanFilter = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(SERVICE_UUID))
                    .build();
        }

        List<ScanFilter> filters = new ArrayList<>();

        ScanSettings settings = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bluetoothLeScanner.startScan(filters, settings, mScanCallback);
        }

        mHandler = new Handler();
        mHandler.postDelayed(this::stopScan, SCAN_PERIOD);

        mScanning = true;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopScan() {
        if (mScanning && bluetoothAdapter != null && bluetoothAdapter.isEnabled() && bluetoothLeScanner != null) {
            bluetoothLeScanner.stopScan(mScanCallback);
            scanComplete();
        }

        mHandler = null;
        mScanning = false;
        mScanCallback = null;
    }

    private void scanComplete() {
        if (mapBluetoothScanResults.isEmpty())
            return;

        dialogScanProgress.dismiss();

        showDeviceList();
    }

    private void showDeviceList() {
        deviceArrayList.clear();

        for (String deviceAddress : mapBluetoothScanResults.keySet()) {
            BluetoothDevice device = mapBluetoothScanResults.get(deviceAddress);
            if (device.getName() != null && device.getName().contains("THB_W"))
                deviceArrayList.add(device.getName() + "\n" + device);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.text1, R.id.text1, deviceArrayList);
        adapter.notifyDataSetChanged();

        spinnerDevice.setAdapter(adapter);

        btnConnect.setVisibility(View.VISIBLE);
    }

    /**
     * @param deviceName
     * @param deviceAddress
     */
    private void saveDeviceInformation(String deviceName, String deviceAddress) {
        SharedPreferences.Editor editor = sharedPreferencesDevice.edit();

        editor.putString("deviceName", deviceName);
        editor.putString("deviceAddress", deviceAddress);

        editor.commit();
    }

    // endregion

    // region Nested classes

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class BluetoothLeScanCallback extends ScanCallback {

        private Map<String, BluetoothDevice> mapBluetoothScanResults;

        BluetoothLeScanCallback(Map<String, BluetoothDevice> scanResults) {
            mapBluetoothScanResults = scanResults;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            addScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                addScanResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            logError("BLE Scan Failed with code " + errorCode);
        }

        private void addScanResult(ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceAddress = device.getAddress();
            mapBluetoothScanResults.put(deviceAddress, device);
        }
    }

    // endregion
}
