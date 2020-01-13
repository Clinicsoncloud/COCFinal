package com.abhaybmicoc.app.hemoglobin;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
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


import com.abhaybmicoc.app.DashboardActivity;
import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.actofitheight.ActofitMainActivity;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;
import com.abhaybmicoc.app.glucose.Activity_ScanList;
import com.abhaybmicoc.app.heightweight.Principal;
import com.abhaybmicoc.app.hemoglobin.util.BluetoothUtils;
import com.abhaybmicoc.app.hemoglobin.util.StringUtils;
import com.abhaybmicoc.app.printer.esys.pridedemoapp.Act_Main;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.utils.ApiUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.abhaybmicoc.app.hemoglobin.Constants.SCAN_PERIOD;
import static com.abhaybmicoc.app.hemoglobin.Constants.SERVICE_UUID;


public class MainActivity extends AppCompatActivity implements GattClientActionListener,TextToSpeech.OnInitListener, View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_FINE_LOCATION = 2;

    private BluetoothAdapter mBluetoothAdapter;
    private ScanCallback mScanCallback;
    private BluetoothLeScanner mBluetoothLeScanner;
    private BluetoothGatt mGatt;

    private Map<String, BluetoothDevice> mScanResults;
    private Handler mHandler;

    private boolean mScanning;
    private boolean mConnected;
    private boolean mTimeInitialized;
    private boolean mEchoInitialized;

    private TextView textViewdevice;
    private TextView textViewdisplay;
    private TextView textName,textDob,textGender,textMobile;

    private String deviceName;

    SharedPreferences sharedPreferences, hemoglobinObject,personalObject;

    Button buttonconnect;
    Spinner spinnerDevice;
    ArrayList<String> deviceArrayList;
    ProgressDialog progressDialog, scanprogressDialog,connectionProgressDialog;
    private TextToSpeech tts;
    private String txt;
    private TextView txtmainHeight,txtmainWeight,txtmainTemprature,txtmainOximeter,txtmainBpMonitor,txtmainSugar;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_try_hemoglobin);

        init();

        setupUI();
        bindEvents();

        progressDialogs();

        requestPermission();

    }

    private void bindEvents() {
        txtmainHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(MainActivity.this, Principal.class));
            }
        });

        txtmainWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(MainActivity.this, ActofitMainActivity.class));
            }
        });

        txtmainTemprature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(MainActivity.this, ThermometerScreen.class));
            }
        });
        txtmainOximeter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(MainActivity.this, com.abhaybmicoc.app.oximeter.MainActivity.class));
            }
        });

        txtmainBpMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            }
        });

        txtmainSugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(MainActivity.this, Activity_ScanList.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        context.startActivity(new Intent(this,Activity_ScanList.class));
    }

    private void progressDialogs() {

        //Test progress Dialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Abort Test", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                sendMessage("U371");
            }
        });

        //Scanning progress dialog
        scanprogressDialog = new ProgressDialog(MainActivity.this);
        scanprogressDialog.setMessage("Scanning...");
        scanprogressDialog.setCancelable(false);
        scanprogressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Stop Scan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        connectionProgressDialog = new ProgressDialog(MainActivity.this);
        connectionProgressDialog.setMessage("Connecting...");
        connectionProgressDialog.setCancelable(true);
    }

    private void setupUI() {

        buttonconnect = findViewById(R.id.btnconnect);
        buttonconnect.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect1));

        textViewdevice = findViewById(R.id.textdevice);

        textViewdisplay = findViewById(R.id.display);

        spinnerDevice = findViewById(R.id.spindevice);

        textName = findViewById(R.id.txtName);
        textDob = findViewById(R.id.txtAge);
        textGender = findViewById(R.id.txtGender);
        textMobile = findViewById(R.id.txtMobile);

        txtmainHeight = findViewById(R.id.txtmainheight);
        txtmainWeight = findViewById(R.id.txtmainweight);
        txtmainTemprature = findViewById(R.id.txtmaintempreture);
        txtmainOximeter = findViewById(R.id.txtmainpulseoximeter);
        txtmainBpMonitor = findViewById(R.id.txtmainbloodpressure);
        txtmainSugar = findViewById(R.id.txtmainbloodsugar);

        deviceArrayList = new ArrayList();

        setUserInfo();
    }

    private void setUserInfo() {

        textName.setText("Name : " + personalObject.getString("name", ""));
        textGender.setText("Gender : " + personalObject.getString("gender", ""));
        textMobile.setText("Phone : " + personalObject.getString("mobile_number", ""));
        textDob.setText("DOB : " + personalObject.getString("dob", ""));

    }

    private void init() {

        context = MainActivity.this;

        tts = new TextToSpeech(getApplicationContext(),this);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        sharedPreferences = getSharedPreferences("device_data", MODE_PRIVATE);
        hemoglobinObject = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
        personalObject = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        //voice command for the device
        txt = "Please press the power button of device and click on scan button";
        speakOut(txt);
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

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
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_FINE_LOCATION);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasLocationPermissions() {
        return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnectGattServer();
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
        if(textViewdisplay.getText().toString().contains("Test")){
            sendMessage("U370");
            disconnectGattServer();
        }

        if(tts != null){
            tts.shutdown();
        }
    }

    @Override
    public void showToast(final String msg) {

       //for progress bar
        new Thread() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        //   Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                        if (msg.contains("Test")) {
                            progressDialog.show();
                        } else if (msg.contains("Hb = ")) {
                            progressDialog.dismiss();
                            try {
                                String hbvalue = msg;

                                hbvalue = hbvalue.replaceAll("[^0-9.]", "");

                                String device_name = sharedPreferences.getString("devicename", "NA");
                                String device_address = sharedPreferences.getString("device", "NA");
                                SharedPreferences.Editor editor = hemoglobinObject.edit();
                                editor.putString("hemoglobin", hbvalue);
                                editor.commit();
                                Log.e("hbvalue", " = " + hbvalue);
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
                        textViewdisplay.setText("");
                        textViewdisplay.append(msg + "\n");
                    }
                });
            }
        }.start();

    }

    @Override
    public void log(String message) {
        Log.d("msg: ", message);
    }

    @Override
    public void logError(String msg) {
        Log.d("Error: ", msg);
    }

    @Override
    public void setConnected(boolean connected) {
        connectionProgressDialog.dismiss();
        mConnected = connected;
        buttonconnect.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect2));
        buttonconnect.setText("Connected");
        textViewdevice.setText(deviceName);
    }

    @Override
    public void initializeTime() {
        mTimeInitialized = true;
    }

    @Override
    public void initializeEcho() {
        mEchoInitialized = true;
    }


    @Override
    public void disconnectGattServer() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        mConnected = false;
        mEchoInitialized = false;
        mTimeInitialized = false;
        buttonconnect.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buttonshapeconnect1));
        textViewdevice.setText("NA");
        buttonconnect.setText("Connect");
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void scan(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions() || mScanning) {
                return;
            }
        }
        disconnectGattServer();
        scanprogressDialog.show();
        if (!sharedPreferences.getString("device", "NA").equals("NA")) {
            //BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(sharedPreferences.getString("device","NA"));
            //connectDevice(device);
            //deviceName=sharedPreferences.getString("devicename","NA");
        }


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

        mScanCallback = null;
        mScanning = false;
        mHandler = null;

    }

    private void scanComplete() {
        if (mScanResults.isEmpty()) {
            return;
        }
        scanprogressDialog.dismiss();
        list_show();
    }

    public void list_show() {

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

    private void requestBluetoothEnable() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

    }

    public void start(View view) {
        sendMessage("U371");
    }

    public void test(View view) {
        //sendMessage("ON");
        sendMessage("U401");
        sendMessage("U401");
    }

    public void readLastTest(View view) {
        sendMessage("U502");
    }

    public void connect(View view) {
        disconnectGattServer();
        if (deviceArrayList.size() != 0) {
            String s = spinnerDevice.getSelectedItem().toString();
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(s.substring(s.length() - 17));
            connectDevice(device);
            connectionProgressDialog.show();
            showToast(device.getName() + "");
            deviceName = device.getName();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("device", s.substring(s.length() - 17));
            editor.putString("devicename", device.getName());
            editor.commit();

            deviceArrayList.clear();
            ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.text1, R.id.text1, deviceArrayList);
            adapter.notifyDataSetChanged();
            spinnerDevice.setAdapter(adapter);

        }
    }

    public void readBatchCode(View view) {
        sendMessage("U402");
    }

    public void writeBatchCode(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Set Batch Code");

        final EditText input = new EditText(MainActivity.this);
        input.setHint("Batch Code");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
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

    public void device_off(View view) {

        sendMessage("U370");
        disconnectGattServer();

        try {
            AndMedical_App_Global.mBTcomm = null;
        } catch(NullPointerException e) { }
        //when device is off we can move to next screen
        startActivity(new Intent(MainActivity.this, Act_Main.class));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txtmainheight:
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(this, Principal.class));
                break;

            case R.id.txtmainweight:
                context.startActivity(new Intent(this, ActofitMainActivity.class));
                break;


            case R.id.txtmaintempreture:
                context.startActivity(new Intent(this, ThermometerScreen.class));
                break;


            case R.id.txtmainpulseoximeter:
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(this, com.abhaybmicoc.app.oximeter.MainActivity.class));
                break;


            case R.id.txtmainbloodpressure:
                context.startActivity(new Intent(this, DashboardActivity.class));
                break;


            case R.id.txtmainbloodsugar:
                context.startActivity(new Intent(this, Activity_ScanList.class));
                break;

        }
    }


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

    // Messaging

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
        if (success) {

            showToast("MSG SENT");

        } else {
            logError("Failed to write data");
        }
    }
}
