package com.abhaybmicoc.app.hemoglobin;


import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.widget.Toast;
import android.os.ParcelUuid;
import android.widget.Button;
import android.content.Intent;
import android.widget.Spinner;
import android.app.AlertDialog;
import android.widget.TextView;
import android.content.Context;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.widget.LinearLayout;
import android.widget.ArrayAdapter;
import android.annotation.TargetApi;
import android.speech.tts.TextToSpeech;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.support.annotation.RequiresApi;
import android.bluetooth.le.BluetoothLeScanner;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothGattCharacteristic;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.oxygen.data.Const;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.activity.DashboardActivity;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.hemoglobin.util.StringUtils;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;
import com.abhaybmicoc.app.hemoglobin.util.BluetoothUtils;
import com.abhaybmicoc.app.glucose.GlucoseScanListActivity;
import com.abhaybmicoc.app.printer.esys.pridedemoapp.Act_Main;
import com.abhaybmicoc.app.utils.Constant;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.ArrayList;

import static com.abhaybmicoc.app.hemoglobin.Constants.SCAN_PERIOD;
import static com.abhaybmicoc.app.hemoglobin.Constants.SERVICE_UUID;

public class MainActivity extends AppCompatActivity implements GattClientActionListener, TextToSpeech.OnInitListener {
    // region Variables

    private Context context = MainActivity.this;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;

    private BluetoothGatt mGatt;
    private ScanCallback mScanCallback;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private Map<String, BluetoothDevice> mScanResults;
    
    private Handler mHandler;

    private boolean mScanning;
    private boolean mConnected;
    private boolean mEchoInitialized;

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

    private SharedPreferences sharedPreferencesDevice;
    private SharedPreferences sharedPreferencesPersonal;
    private SharedPreferences sharedPreferencesDeviceHemoglobin;

    private Button btnScan;
    private Button btnConnect;
    private Spinner spinnerDevice;

    private ProgressDialog dialogProgress;
    private ProgressDialog dialogScanProgress;
    private ProgressDialog dialogConnectionProgress;

    ArrayList<String> deviceArrayList;

    private TextToSpeech textToSpeech;

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
        super.onBackPressed();

        context.startActivity(new Intent(this, GlucoseScanListActivity.class));
    }

    @Override
    public void onInit(int status) {
        startTextToSpeech(status);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check low energy support
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            // Get a newer device
            logError("No BLE Support.");
            showToast("No BLE Support.");
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(tvViewDisplay.getText().toString().contains("Test")){
            sendMessage("U370");
            disconnectGattServer();
        }

        if(textToSpeech != null){
            textToSpeech.shutdown();
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

        //for toast msg
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
        dialogConnectionProgress.dismiss();

        mConnected = connected;

        btnConnect.setText("Connected");
        btnConnect.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect2));

        tvViewDevice.setText(getStoredDeviceName());
    }

    @Override
    public void log(String message) {

    }

    @Override
    public void logError(String message) {

    }

    @Override
    public void initializeTime() {

    }

    @Override
    public void initializeEcho() {
        mEchoInitialized = true;
    }

    @Override
    public void disconnectGattServer() {
        if (dialogProgress != null)
            dialogProgress.dismiss();

        mConnected = false;
        mEchoInitialized = false;

        tvViewDevice.setText("NA");
        btnConnect.setText("Connect");
        btnConnect.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect1));

        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectGattServer();
    }

    // endregion

    // region Initialization

    /**
     *
     */
    private void setupUI() {
        setContentView(R.layout.new_try_hemoglobin);

        btnScan = findViewById(R.id.btn_scan);
        btnConnect = findViewById(R.id.btnconnect);
        btnConnect.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect1));

        spinnerDevice = findViewById(R.id.spindevice);

        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvViewDisplay = findViewById(R.id.display);
        textDateOfBirth = findViewById(R.id.tv_age);
        tvViewDevice = findViewById(R.id.textdevice);
        tvMainHeight = findViewById(R.id.tv_header_height);
        tvMainWeight = findViewById(R.id.tv_header_weight);
        tvMobileNumber = findViewById(R.id.tv_mobile_number);
        tvMainSugar = findViewById(R.id.tv_header_bloodsugar);
        tvMainTemperature = findViewById(R.id.tv_header_tempreture);
        tvMainOximeter = findViewById(R.id.tv_header_pulseoximeter);
        tvMainBpMonitor = findViewById(R.id.tv_header_bloodpressure);

        deviceArrayList = new ArrayList();
    }

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupEvents() {
        btnScan.setOnClickListener(view -> scanDevices());

        btnConnect.setOnClickListener(view -> storeDeviceAndConnect());

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
            context.startActivity(new Intent(MainActivity.this, DashboardActivity.class));
        });

        tvMainSugar.setOnClickListener(view -> {
            context.startActivity(new Intent(MainActivity.this, GlucoseScanListActivity.class));
        });
    }

    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializeData(){
        textToSpeech = new TextToSpeech(getApplicationContext(),this);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        sharedPreferencesDevice = getSharedPreferences("device_data", MODE_PRIVATE);
        sharedPreferencesDeviceHemoglobin = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
        sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        txt = "Please press the power button of device and click on scan button";
        speakOut(txt);

        setUserInfo();

        showProgressDialog();

        requestPermission();

        connectToDevice();
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
     * @param msg
     */
    private void sendMessage(String msg) {
        if (!mConnected || !mEchoInitialized) {
            showToast("Not Connected");
            return;
        }

        BluetoothGattCharacteristic characteristic = BluetoothUtils.findEchoCharacteristic(mGatt);
        if (characteristic == null) {
            logError("Unable to find echo characteristic.");
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
     *
     * @param view
     */
    public void readBatchCode(View view) {
        sendMessage("U402");
    }

    /**
     *
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
                //  Toast.makeText(getApplicationContext(), "Text entered is " + input.getText().toString(), Toast.LENGTH_SHORT).show();
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

    /**
     *
     * @param view
     */
    public void device_off(View view) {
        sendMessage("U370");

        disconnectGattServer();

        try {
            AndMedical_App_Global.mBTcomm = null;
        } catch(NullPointerException e) { }

        startActivity(new Intent(MainActivity.this, Act_Main.class));
    }

    // Gatt connection
    private void connectDevice(BluetoothDevice device) {
        GattClientCallback gattClientCallback = new GattClientCallback(this);
        mGatt = device.connectGatt(this, true, gattClientCallback);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasPermissions() {
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
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

    /**
     *
     * @param view
     */
    public void start(View view) {
        sendMessage("U371");
    }

    /**
     *
     * @param view
     */
    public void test(View view) {
        //sendMessage("ON");
        sendMessage("U401");
        sendMessage("U401");
    }

    /**
     *
     * @param view
     */
    public void readLastTest(View view) {
        sendMessage("U502");
    }

    /**
     *
     */
    private void connectToDevice(){
        if(savedDeviceAlreadyExists()) {
            btnScan.setVisibility(View.GONE);
            connect();
        } else{
            btnScan.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     */
    public void connect() {
        disconnectGattServer();

        dialogConnectionProgress.show();

        BluetoothDevice device = getDevice(getStoredDeviceAddress());
        connectDevice(device);
    }

    /**
     *
     */
    private void storeDeviceAndConnect(){

        dialogConnectionProgress.show();

        String deviceName = spinnerDevice.getSelectedItem().toString();
        String deviceAddress = deviceName.substring(deviceName.length() - 17);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(deviceAddress);

        saveDeviceInformation(deviceName, deviceAddress);

        connect();

        deviceArrayList.clear();

        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.text1, R.id.text1, deviceArrayList);
        adapter.notifyDataSetChanged();

        spinnerDevice.setAdapter(adapter);
    }

    private String getStoredDeviceName(){
        return sharedPreferencesDevice.getString("deviceName", "");
    }

    private String getStoredDeviceAddress(){
        return sharedPreferencesDevice.getString("deviceAddress","");
    }

    /**
     *
     * @return
     */
    private boolean savedDeviceAlreadyExists(){
        return !sharedPreferencesDevice.getString("deviceName", "").equals("");
    }

    /**
     *
     * @return
     */
    private BluetoothDevice getDevice(String deviceName){
        return mBluetoothAdapter.getRemoteDevice(deviceName);
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
        dialogConnectionProgress.setCancelable(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FINE_LOCATION);
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

        mScanResults = new HashMap<>();
        mScanCallback = new BtleScanCallback(mScanResults);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
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
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
        }

        mHandler = new Handler();
        mHandler.postDelayed(this::stopScan, SCAN_PERIOD);

        mScanning = true;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopScan() {
        if (mScanning && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            scanComplete();
        }

        mHandler = null;
        mScanning = false;
        mScanCallback = null;
    }

    private void scanComplete() {
        if (mScanResults.isEmpty())
            return;

        dialogScanProgress.dismiss();

        showDeviceList();
    }

    private void showDeviceList() {
        deviceArrayList.clear();

        for (String deviceAddress : mScanResults.keySet()) {
            BluetoothDevice device = mScanResults.get(deviceAddress);
            if (device.getName() != null && device.getName().contains("THB_W"))
                deviceArrayList.add(device.getName() + "\n" + device);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.text1, R.id.text1, deviceArrayList);
        adapter.notifyDataSetChanged();
        
        spinnerDevice.setAdapter(adapter);
    }

    /**
     *
     * @param deviceName
     * @param deviceAddress
     */
    private void saveDeviceInformation(String deviceName, String deviceAddress){
        SharedPreferences.Editor editor = sharedPreferencesDevice.edit();

        editor.putString("deviceName", deviceName);
        editor.putString("deviceAddress", deviceAddress);

        editor.commit();
    }

    // endregion

    // region Nested classes

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class BtleScanCallback extends ScanCallback {

        private Map<String, BluetoothDevice> mScanResults;

        BtleScanCallback(Map<String, BluetoothDevice> scanResults) {
            mScanResults = scanResults;
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
            mScanResults.put(deviceAddress, device);
        }
    }

    // endregion
}
