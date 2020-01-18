package com.abhaybmicoc.app.thermometer;

import android.os.Bundle;
import android.os.AsyncTask;
import android.content.Intent;
import android.content.Context;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.bluetooth.BluetoothAdapter;

import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.oximeter.MainActivity;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.ErrorUtils;

import java.util.Set;
import java.util.UUID;
import java.util.Locale;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.nio.charset.Charset;

import static com.abhaybmicoc.app.utils.ApiUtils.PREFERENCE_THERMOMETERDATA;

public class ThermometerScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // region Variables

    Context context = ThermometerScreen.this;

    private String conec;
    private String strEnabled;
    private String disconec;
    private String txt = "";
    private String ConectadO;
    private String str  = "";
    private String Connectad;
    private String strCannotSend;
    private String strNew = "";
    private String strDisconnect;
    private String message = "";
    private String estadoBoton2;
    private String strTemp = "";
    private String strBluetoothTurnedOn;
    private String strConnect = "Connect";
    public static String TAG = "ThermometerActivity";

    private ArrayList listLinkedDevices;
    private ArrayList listDeviceAddresses;

    private ArrayAdapter<String> adapterDevices;

    private BluetoothSocket socket;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter mBluetoothAdapter;

    private Button btnBaud;
    private Button btnConnect;
    private Button btnGetTemperature;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvHeight;
    private TextView tvMobile;
    private TextView tvWeight;

    private EditText etTemperature;

    private Spinner spinner;

    private InputStream inputStreamHeightReceiver;
    private OutputStream outputStreamHeightReceiver;

    private ProgressDialog progressDialog;

    private SharedPreferences sharePreferenceThermometer;
    private SharedPreferences sharedPreferencePersonalData;
    private SharedPreferences sharedPreferenceBluetoothAddress;

    private TextToSpeech textToSpeech;

    private int connectTryCount = 0;
    private long CONNECT_TRY_PAUSE_MILLISECONDS = 500;
    private int ALLOWED_BLUETOOTH_CONNECT_TRY_COUNT = 1;

    // endregion

    // region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
        turnOnBluetooth();
        storeBluetoothDevices();
    }

    @Override
    public void onInit(int status) {
        startTextToSpeech(status);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        handleTemperatureResult(requestCode, resultCode, data);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();

        connectToDevice();
    }

    @Override
    public void onPause() {
        super.onPause();

        freeConnections();
    }

    @Override
    public void onStop() {
        super.onStop();

        stopTextToSpeech();
        closeBluetooth();
    }

    // endregion

    // region Initialization data

    private void setupUI(){
        setContentView(R.layout.activity_main_temperature);

        textToSpeech = new TextToSpeech(getApplicationContext(),this);

        sharedPreferencePersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        etTemperature = findViewById(R.id.et_temprature);

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvMobile = findViewById(R.id.tv_mobile_number);

        btnConnect = findViewById(R.id.btn_connect_temperature);
        btnGetTemperature = findViewById(R.id.btn_get_temperature);

        tvName.setText("Name : " + sharedPreferencePersonalData.getString(Constant.Fields.NAME, ""));
        tvGender.setText("Gender : " + sharedPreferencePersonalData.getString(Constant.Fields.GENDER, ""));
        tvAge.setText("DOB : " + sharedPreferencePersonalData.getString(Constant.Fields.DATE_OF_BIRTH, ""));
        tvMobile.setText("Phone : " + sharedPreferencePersonalData.getString(Constant.Fields.MOBILE_NUMBER, ""));

        tvHeight = findViewById(R.id.tv_header_height);
        tvWeight = findViewById(R.id.tv_header_weight);

        btnBaud = findViewById(R.id.btn_next);

        if (strConnect.equals("Connect")) {
            btnConnect.setText(conec);
        }

        etTemperature.setVisibility(View.VISIBLE);

        this.listDeviceAddresses = new ArrayList();
        this.listLinkedDevices = new ArrayList();

        spinner = findViewById(R.id.sp_temprature);
        adapterDevices = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listLinkedDevices);
        spinner.setAdapter(adapterDevices);
    }

    private void setupEvents(){
        /* Add event for top weight and height */
        tvHeight.setOnClickListener(view -> {
            context.startActivity(new Intent(this, HeightActivity.class));
        });

        tvWeight.setOnClickListener(view -> {
            context.startActivity(new Intent(this, ActofitMainActivity.class));
        });

        btnBaud.setOnClickListener(view -> handleBaud());
        btnConnect.setOnClickListener(view -> connectToDevice());
        btnGetTemperature.setOnClickListener(view -> getTemperature());

        // TODO: What this code is doing?
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View arg1, int position, long arg3) {
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initializeData(){
        sharePreferenceThermometer = getSharedPreferences(PREFERENCE_THERMOMETERDATA, MODE_PRIVATE);

        this.conec = (String) getText(R.string.connect);
        this.Connectad = (String) getText(R.string.connect);
        this.ConectadO = (String) getText(R.string.connected);
        this.disconec = (String) getText(R.string.disconnect);
        this.strDisconnect = (String) getText(R.string.disconnect);
        this.strCannotSend = (String) getText(R.string.cannotSend);
        this.strBluetoothTurnedOn = (String) getText(R.string.bluetoothTurnedOn);

        sharedPreferenceBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);
    }

    // endregion

    // region Logical methods

    private void turnBluetoothOff() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    private void handleBaud(){
        if(etTemperature.getText().length() > 0) {
            if(etTemperature.getText().toString().indexOf(".") == etTemperature.getText().length() - 2 || etTemperature.getText().toString().contains(".")) {
                Intent objpulse = new Intent(getApplicationContext(), MainActivity.class);

                SharedPreferences.Editor editor = sharePreferenceThermometer.edit();
                editor.putString(Constant.Fields.TEMPERATURE, etTemperature.getText().toString().trim());
                editor.commit();

                try {
                    // turnBluetoothOff();
                } catch (Exception e) {

                    Toast.makeText(ThermometerScreen.this, "Exception", Toast.LENGTH_SHORT).show();
                }

                startActivity(objpulse);
                finish();
            }else{
                Toast.makeText(context, "Please Enter temprature in valid format", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(ThermometerScreen.this, "Enter Manual Temprature", Toast.LENGTH_SHORT).show();
            txt = "Please Enter Body Temprature Manually";
            speakOut(txt);
        }
    }

    /**
     *
     */
    private void connectToDevice() {
        if (strConnect.equals("Connect")) {
            turnOnBluetooth();

            new ThermometerScreen.Connect(ThermometerScreen.this, null).execute(new String[]{sharedPreferenceBluetoothAddress.getString("hcthermometer", "")});
            ThermometerScreen.this.strEnabled = "false";
            return;
        }
    }

    /**
     *
     */
    private void showCannotConnectToDevice() {
        //reset the connectTryCount to 0
        connectTryCount = 0;

        estadoBoton2 = conec;
        strEnabled = "false";
        strConnect = "Connect";

        String message = "No Bluetooth Device Found Please Connect it Manually";
        speakOut(message);
    }

    /**
     *
     */
    private void sendCommand() {
        if (ThermometerScreen.this.strConnect.equals("Connect")) {
            Toast.makeText(ThermometerScreen.this, "Connecting to device...", Toast.LENGTH_SHORT).show();

            connectToDevice();

            return;
        }

        Toast.makeText(ThermometerScreen.this, "Device Ready", Toast.LENGTH_SHORT).show();

        String env = "T";

        try {
            outputStreamHeightReceiver.write(env.getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            Toast.makeText(ThermometerScreen.this, ThermometerScreen.this.strCannotSend, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void getTemperature(){
        if (ThermometerScreen.this.strConnect == "Connect") {
            Toast.makeText(ThermometerScreen.this, "Connecting to device...", Toast.LENGTH_SHORT).show();

            connectToDevice();

            return;
        }

        Toast.makeText(ThermometerScreen.this, "Device Ready", Toast.LENGTH_SHORT).show();

        String env = "T";
        try {
            outputStreamHeightReceiver.write(env.getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            Toast.makeText(ThermometerScreen.this, strCannotSend, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void turnOnBluetooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }

    /**
     *
     */
    private void storeBluetoothDevices(){
        Set<BluetoothDevice> devices = this.mBluetoothAdapter.getBondedDevices();

        if (devices != null && devices.size() > 0) {
            for (BluetoothDevice device : devices) {
                if (device.getName().contains("THERMOMETER")) {
                    this.listLinkedDevices.add(device.getName() + "\n" + device.getAddress());
                    this.listDeviceAddresses.add(device.getAddress());

                    sharedPreferenceBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferenceBluetoothAddress.edit();

                    if(sharedPreferenceBluetoothAddress.getString("hcthermometer","").equalsIgnoreCase("")){
                        editor.putString("hcthermometer", device.getAddress());
                        editor.commit();
                    }
                }
            }
        }
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private void handleTemperatureResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Set<BluetoothDevice> devices = this.mBluetoothAdapter.getBondedDevices();

            if(devices != null && devices.size() > 0) {
                listLinkedDevices = new ArrayList();
                listDeviceAddresses = new ArrayList();

                if (devices.size() > 0) {
                    for (BluetoothDevice device : devices) {
                        listDeviceAddresses.add(device.getAddress());
                        listLinkedDevices.add(device.getName() + "\n" + device.getAddress());
                    }
                }

                this.adapterDevices = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, this.listLinkedDevices);
                this.spinner.setAdapter(this.adapterDevices);

                Toast.makeText(this, strBluetoothTurnedOn, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     *
     */
    private void freeConnections(){
        closeBluetooth();
        stopTextToSpeech();
    }

    /**
     *
     */
    private void closeBluetooth(){
        if (this.strConnect.equals("Disconnect")) {
            this.strEnabled = "false";
            this.strConnect = "Connect";
            this.btnConnect.setText(this.conec);

            try {
                this.socket.close();
            } catch (IOException e) {
            }
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

    // endregion

    // region Nested classes

    private class Connect extends AsyncTask<String, String, String> {
        private Connect() {
        }

        /* synthetic */
        Connect(ThermometerScreen ThermometerScreen, ThermometerScreen.Connect Connect) {
            this();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ThermometerScreen.this);
            progressDialog.setCancelable(true);
            progressDialog.setMessage("Connecting..");
            progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            if (params[0].equalsIgnoreCase("")) {
                return "";
            }

            bluetoothDevice = mBluetoothAdapter.getRemoteDevice(d);
            try {
                ThermometerScreen.this.socket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                if(!ThermometerScreen.this.socket.isConnected()){
                    ThermometerScreen.this.socket.connect();
                }

                inputStreamHeightReceiver = ThermometerScreen.this.socket.getInputStream();
                outputStreamHeightReceiver = ThermometerScreen.this.socket.getOutputStream();

                return "Connected";
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String res) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();

            String eg = res;
            String mEstado = res;
            if (res.equals("Connected"))
                mEstado = ThermometerScreen.this.ConectadO;

            Toast.makeText(ThermometerScreen.this, mEstado, Toast.LENGTH_SHORT).show();

            if (eg.equals("Connected")) {
                strConnect = "Disconnect";
                estadoBoton2 = strDisconnect;
                strEnabled = "true";
//                sendCommand();

                String message = "Device Ready to use Point Device To forehead and Press Button";
                speakOut(message);
                new ThermometerScreen.Receiver().execute(new String[]{ThermometerScreen.this.strEnabled});
            } else {
                if(ThermometerScreen.this.connectTryCount > ThermometerScreen.this.ALLOWED_BLUETOOTH_CONNECT_TRY_COUNT) {
                    ThermometerScreen.this.showCannotConnectToDevice();
                }else{
                    try{
                        Thread.sleep(ThermometerScreen.this.CONNECT_TRY_PAUSE_MILLISECONDS);

                        /* Increase connection try count and try to connect */
                        connectTryCount++;

                        connectToDevice();
                    }catch(Exception ex){

                        ThermometerScreen.this.showCannotConnectToDevice();
                    }
                }
            }

            ThermometerScreen.this.btnConnect.setText(estadoBoton2);
        }
    }

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
                    ErrorUtils.logErrors(e,"HeightActivity.java","doInBackground()","Failed to read bytes data");
                }

                publishProgress(new String[]{message, strEnabled});
            }

            return message;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(String... params) {
            str = params[0];

            str = str.replace("�", "");

            strTemp += str;

            ThermometerScreen.this.etTemperature.setText(""+strTemp);

            strTemp = "";

            if (params[1].equals("false")) {
                strEnabled = "false";
                strConnect = "Connect";
                estadoBoton2 = ThermometerScreen.this.conec;
            } else {
                strEnabled = "true";
                strConnect = "Disconnect";
                estadoBoton2 = strDisconnect;
            }

            btnConnect.setText(ThermometerScreen.this.estadoBoton2);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    // endregion
}
