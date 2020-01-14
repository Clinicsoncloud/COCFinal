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

    private static final int REQUEST_ENABLE_BT = 3;

    private String conec;
    private String Enviar;
    private String enable;
    private Button enviar;
    private String Limpiar;
    private String disconec;
    private String recibido;
    private String txt = "";
    private String ConectadO;
    private String Conectese;
    private String str  = "";
    private String Connectad;
    private String Nosepuede;
    private String strNew = "";
    private String Desconectad;
    private String message = "";
    private String estadoBoton2;
    private String Seleccionado;
    private String strTemp = "";
    private String btDeviceAddres;
    private String ConexionPerdida;
    private String BluetoothEncendido;
    private String estadoBoton = "Connect";
    private String dispositivoSeleccionado;
    public static String TAG = "ThermometerActivity";

    private ArrayList mMacDispositivos;
    private ArrayList mDispositivosVinculados;
    private ArrayAdapter<String> adapterSpinner;

    private BluetoothSocket socket;
    private BluetoothDevice dispositivo;
    private BluetoothAdapter mBluetoothAdapter;

    private Button btnNext;
    private Button btnBaud;
    private Button btnConnect;
    private Button btnLimpiar;         // limpiar (Spanish), clean (English)
    private Button btnGetTemperature;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvMobile;
    private TextView tvHeight;
    private TextView tvWeight;

    private EditText etTemperature;
    private EditText etManualHeight;

    private Spinner spinner;

    private InputStream ins;
    private OutputStream ons;
    private DataInputStream dins;
    private ProgressDialog progressDialog;

    private SharedPreferences objBluetoothAddress;
    private SharedPreferences sharedPreferencesUser;
    private SharedPreferences sharedPreferencesPersonal;

    private TextToSpeech textToSpeech;

    private int connectTryCount = 0;
    private long CONNECT_TRY_PAUSE_MILLISECONDS = 500;
    private int ALLOWED_BLUETOOTH_CONNECT_TRY_COUNT = 1;

    // endregion

    // Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
        encenderBluetooth();
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

        sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        etTemperature = findViewById(R.id.et_temprature);

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvMobile = findViewById(R.id.tv_mobile_number);

        btnConnect = findViewById(R.id.btn_connect_temperature);
        btnGetTemperature = findViewById(R.id.btn_get_temperature);

        tvAge.setText("DOB : " + sharedPreferencesPersonal.getString("dob", ""));
        tvName.setText("Name : " + sharedPreferencesPersonal.getString("name", ""));
        tvGender.setText("Gender : " + sharedPreferencesPersonal.getString("gender", ""));
        tvMobile.setText("Phone : " + sharedPreferencesPersonal.getString("mobile_number", ""));

        tvHeight = findViewById(R.id.tv_header_height);
        tvWeight = findViewById(R.id.tv_header_weight);

        btnBaud = findViewById(R.id.btn_next);

        if (estadoBoton.equals("Connect")) {
            btnConnect.setText(conec);
        }

        etTemperature.setVisibility(View.VISIBLE);

        this.mMacDispositivos = new ArrayList();
        this.mDispositivosVinculados = new ArrayList();
        
        spinner = findViewById(R.id.sp_temprature);
        adapterSpinner = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mDispositivosVinculados);
        spinner.setAdapter(adapterSpinner);
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View arg1, int position, long arg3) {
                ThermometerScreen.this.dispositivoSeleccionado = (String) ((CharSequence) ThermometerScreen.this.mMacDispositivos.get(position));
                Log.e(TAG, "address_thermo : " + objBluetoothAddress.getString("hcthermometer", ""));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initializeData(){
        sharedPreferencesUser = getSharedPreferences(PREFERENCE_THERMOMETERDATA, MODE_PRIVATE);

        this.conec = (String) getText(R.string.Connectad);
        this.Connectad = (String) getText(R.string.Connectad);
        this.ConectadO = (String) getText(R.string.ConectadO);
        this.Nosepuede = (String) getText(R.string.Nosepuede);
        this.Conectese = (String) getText(R.string.Conectese);
        this.disconec = (String) getText(R.string.Desconectad);
        this.Desconectad = (String) getText(R.string.Desconectad);
        this.Seleccionado = (String) getText(R.string.Seleccionado);
        this.ConexionPerdida = (String) getText(R.string.ConexionPerdida);
        this.BluetoothEncendido = (String) getText(R.string.BluetoothEncendido);

        objBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);
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

                SharedPreferences.Editor editor = sharedPreferencesUser.edit();
                editor.putString("data", etTemperature.getText().toString().trim());
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
        if (ThermometerScreen.this.estadoBoton.equals("Connect")) {
            ThermometerScreen.this.encenderBluetooth();

            new ThermometerScreen.Connect(ThermometerScreen.this, null).execute(new String[]{objBluetoothAddress.getString("hcthermometer", "")});
            ThermometerScreen.this.enable = "false";
            return;
        }
    }

    /**
     *
     */
    private void showCannotConnectToDevice() {
        //reset the connectTryCount to 0
        ThermometerScreen.this.connectTryCount = 0;

        ThermometerScreen.this.enable = "false";
        ThermometerScreen.this.estadoBoton = "Connect";
        ThermometerScreen.this.estadoBoton2 = ThermometerScreen.this.conec;

        String message = "No Bluetooth Device Found Please Connect it Manually";
        speakOut(message);
    }

    /**
     *
     */
    private void sendCommand() {
        if (ThermometerScreen.this.estadoBoton.equals("Connect")) {
            Toast.makeText(ThermometerScreen.this, "Connecting to device...", Toast.LENGTH_SHORT).show();
            connectToDevice();
            return;
        }
        Toast.makeText(ThermometerScreen.this, "Device Ready", Toast.LENGTH_SHORT).show();
        String env = "T";
        try {
            Log.e(TAG,"sending_data : "+env);
            ThermometerScreen.this.ons.write(env.getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            Toast.makeText(ThermometerScreen.this, ThermometerScreen.this.Nosepuede, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void getTemperature(){
        if (ThermometerScreen.this.estadoBoton == "Connect") {
            Toast.makeText(ThermometerScreen.this, "Connecting to device...", Toast.LENGTH_SHORT).show();
            connectToDevice();
            return;
        }
        Toast.makeText(ThermometerScreen.this, "Device Ready", Toast.LENGTH_SHORT).show();
        String env = "T";
        try {
            ThermometerScreen.this.ons.write(env.getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            Toast.makeText(ThermometerScreen.this, ThermometerScreen.this.Nosepuede, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void encenderBluetooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }

    /**
     *
     */
    private void storeBluetoothDevices(){
        Set<BluetoothDevice> dispositivos = this.mBluetoothAdapter.getBondedDevices();
        if (dispositivos != null && dispositivos.size() > 0) {
            for (BluetoothDevice device : dispositivos) {
                if (device.getName().contains("THERMOMETER")) {
                    this.mDispositivosVinculados.add(device.getName() + "\n" + device.getAddress());
                    this.mMacDispositivos.add(device.getAddress());

                    objBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);
                    SharedPreferences.Editor editor = objBluetoothAddress.edit();
                    if(objBluetoothAddress.getString("hcthermometer","").equalsIgnoreCase("")){
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
            Set<BluetoothDevice> dispositivos = this.mBluetoothAdapter.getBondedDevices();

            if(dispositivos != null && dispositivos.size() > 0) {
                int a = dispositivos.size();

                this.mMacDispositivos = new ArrayList();
                this.mDispositivosVinculados = new ArrayList();

                if (a > 0) {
                    for (BluetoothDevice device : dispositivos) {
                        this.mDispositivosVinculados.add(device.getName() + "\n" + device.getAddress());
                        this.mMacDispositivos.add(device.getAddress());
                    }
                }

                this.adapterSpinner = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, this.mDispositivosVinculados);
                this.spinner.setAdapter(this.adapterSpinner);

                Toast.makeText(this, this.BluetoothEncendido, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     *
     */
    private void freeConnections(){
        //close the bluetooth socket of thermometer
        closeBluetooth();

        //close the textToSpeech engine
        stopTextToSpeech();
    }

    /**
     *
     */
    private void closeBluetooth(){
        if (this.estadoBoton.equals("Desconectar")) {
            this.estadoBoton = "Connect";
            this.btnConnect.setText(this.conec);
            this.enable = "false";
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

        /* synthetic */ Connect(ThermometerScreen ThermometerScreen, ThermometerScreen.Connect Connect) {
            this();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ThermometerScreen.this);
            progressDialog.setMessage("Connecting..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... disp) {
            String d = disp[0];

            Log.e("d", "" + d);
            if (d.equalsIgnoreCase("")) {
                return "";
            }
            ThermometerScreen.this.dispositivo = ThermometerScreen.this.mBluetoothAdapter.getRemoteDevice(d);
            try {
                ThermometerScreen.this.socket = ThermometerScreen.this.dispositivo.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                if(!ThermometerScreen.this.socket.isConnected()){
                    ThermometerScreen.this.socket.connect();
                }
                ThermometerScreen.this.ins = ThermometerScreen.this.socket.getInputStream();
                ThermometerScreen.this.ons = ThermometerScreen.this.socket.getOutputStream();
                return "Conectado";

            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }

        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String res) {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            String mEstado = res;
            String eg = res;
            if (res.equals("Conectado")) {
                mEstado = ThermometerScreen.this.ConectadO;
            }
            Toast.makeText(ThermometerScreen.this, mEstado, Toast.LENGTH_SHORT).show();
            if (eg.equals("Conectado")) {
                ThermometerScreen.this.estadoBoton = "Desconectar";
                ThermometerScreen.this.estadoBoton2 = ThermometerScreen.this.disconec;
                ThermometerScreen.this.enable = "true";
//                sendCommand();
                txt = "Device Ready to use Point Device To forehead and Press Button";
                speakOut(txt);
                new ThermometerScreen.Recibir().execute(new String[]{ThermometerScreen.this.enable});
            } else {
                if(ThermometerScreen.this.connectTryCount > ThermometerScreen.this.ALLOWED_BLUETOOTH_CONNECT_TRY_COUNT) {
                    ThermometerScreen.this.showCannotConnectToDevice();
                }else{
                    try{
                        Thread.sleep(ThermometerScreen.this.CONNECT_TRY_PAUSE_MILLISECONDS);

                        // Increase connection try count and try to connect
                        ThermometerScreen.this.connectTryCount++;
                        ThermometerScreen.this.connectToDevice();

                    }catch(Exception ex){

                        ThermometerScreen.this.showCannotConnectToDevice();

                    }
                }
            }
            ThermometerScreen.this.btnConnect.setText(ThermometerScreen.this.estadoBoton2);
        }
    }

    public class Recibir extends AsyncTask<String, String, String> {

        public Recibir() {
            Log.e("onRecibirConstructor","in");
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            Log.e("doInBackground","in");
            String str = params[0];
            byte[] buffer = new byte[128];
            ThermometerScreen.this.message = "";
            while (ThermometerScreen.this.enable.equals("true")) {
                try {
                    int bytes = ThermometerScreen.this.ins.read(buffer);
                    ThermometerScreen.this.message = new String(buffer, 0, bytes);
                } catch (IOException e) {
                    ThermometerScreen.this.enable = "false";
                    ThermometerScreen.this.message = "";
                }
                publishProgress(new String[]{ThermometerScreen.this.message, ThermometerScreen.this.enable});
            }
            return ThermometerScreen.this.message;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(String... recib) {

            Log.e("onProgressUpdate","in");


            str = recib[0];

            Log.e("recib[0]", "" + recib[0]);

            Log.e("str", "" + str);

            str = str.replace("�", "");

            strTemp += str;

            //Akshay Thermometer code
            ThermometerScreen.this.etTemperature.setText(""+strTemp);

            strTemp = "";


            Log.e("strTemp", "" + strTemp);

            String length = strTemp.trim();

            Log.e("length", "" +length.length());

            //  Ajit Thermometer code
                 /*   if(strTemp.indexOf(".") == strTemp.length() - 2) {
                        if(strTemp.indexOf("0") == strTemp.length() - 5){
                            strTemp = strTemp.replaceFirst("0","");
                        }
                        ThermometerScreen.this.etTemperature.setText("" + strTemp);
                        strTemp = "";
                    }*/

            if (recib[1].equals("false")) {
                ThermometerScreen.this.estadoBoton = "Connect";
                ThermometerScreen.this.estadoBoton2 = ThermometerScreen.this.conec;
                ThermometerScreen.this.enable = "false";
            } else {
                ThermometerScreen.this.estadoBoton = "Desconectar";
                ThermometerScreen.this.estadoBoton2 = ThermometerScreen.this.disconec;
                ThermometerScreen.this.enable = "true";
            }
            ThermometerScreen.this.btnConnect.setText(ThermometerScreen.this.estadoBoton2);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("onPostExecute","in");
        }
    }

    // endregion
}
