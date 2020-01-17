package com.abhaybmicoc.app.activity;

import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.Activity;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.widget.Spinner;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.app.ProgressDialog;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.speech.tts.TextToSpeech;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.bluetooth.BluetoothAdapter;
import android.widget.AdapterView.OnItemSelectedListener;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.CoutputStreamHeightReceivertant;
import com.abhaybmicoc.app.utils.ErrorUtils;
import com.abhaybmicoc.app.screen.OtpLoginScreen;

import java.util.Set;
import java.util.UUID;
import java.util.Locale;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class HeightActivity extends Activity implements TextToSpeech.OnInitListener{
    // region Variables

    private Context context;

    private static final int REQUEST_ENABLE_BT = 3;

    private String conec;
    private String enable;
    private String Enviar;
    private String Limpiar;
    private String txt = "";
    private String recibido;
    private String disconec;
    private String str = "";
    private String ConectadO;
    private String Connectad;
    private String Conectese;
    private String Nosepuede;
    private String Desconectad;
    private String Seleccionado;
    private String estadoBoton2;
    private String message = "";
    private String btDeviceAddres;
    private String strHeight = "";
    private String ConexionPerdida;
    private String BluetoothEncendido;
    private String estadoBoton = "Connect";
    private String dispositivoSeleccionado;

    private StringBuffer sb;

    private int adjustedHeight;
    private boolean isConnected = false;

    private Button btnNext;
    private Button btnEnviar;       // enviar (Spanish) => send (English)
    private Button btnLimpiar;         // limpiar (Spanish) => clean (English)
    private Button btnConnect;

    private BluetoothDevice dispositivo;

    private ArrayAdapter<String> adapterDevices;
    private ArrayList mMacDispositivos;
    private ArrayList mDispositivosVinculados;

    private EditText multitxt, etManualHeight;

    private InputStream inputStreamHeightReceiver;
    private OutputStream outputStreamHeightReceiver;
    private BluetoothSocket socket;
    private BluetoothAdapter mBluetoothAdapter;

    private Spinner spDevices;
    private ProgressDialog progressDialog;

    private SharedPreferences sharedPreferencePersonalData;
    private SharedPreferences sharedPreferenceActofit;
    private SharedPreferences sharedPreferenceBluetoothAddress;

    private TextView txtAge;
    private TextView txtName;
    private TextView txtGender;
    private TextView txtMobile;

    private TextToSpeech textToSpeech;

    private int connectTryCount = 0;
    private int ALLOWED_BLUETOOTH_CONNECT_TRY_COUNT = 1;

    //milliseconds declaration
    //It will take break of 5ms while trying to reconect
    private int CONNECT_TRY_PAUSE_MILLISECONDS = 500;

    // endregion

    @Override
    public void onInit(int status) {
        startTextToSpeech(status);
    }

    private void showCannotConnectToDevice(){
        // Reset connect try count to 0
        connectTryCount = 0;

        estadoBoton = "Connect";
        estadoBoton2 = conec;
        enable = "false";
        txt = "No Bluetooth Device Found Please Connect it Manually";
        speakOut(txt);
    }

    @Override
    public void onBackPressed() {
        context.startActivity(new Intent(HeightActivity.this, OtpLoginScreen.class));
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
    }

    private void connectToDevice() {
        if (estadoBoton.equals("Connect")) {
            enable = "false";

            encenderBluetooth();

            new Connect().execute(new String[]{getDeviceAddress()});

            return;
        }
    }

    private String getDeviceAddress(){
        return sharedPreferenceBluetoothAddress.getString("hcbluetooth", "");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            Set<BluetoothDevice> dispositivos = this.mBluetoothAdapter.getBondedDevices();
            int a = dispositivos.size();
            this.mDispositivosVinculados = new ArrayList();
            this.mMacDispositivos = new ArrayList();

            if (a > 0) {
                for (BluetoothDevice device : dispositivos) {
                    this.mDispositivosVinculados.add(device.getName() + "\n" + device.getAddress());
                    this.mMacDispositivos.add(device.getAddress());
                }
            }

            this.adapterDevices = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, this.mDispositivosVinculados);
            this.spDevices.setAdapter(this.adapterDevices);

            Toast.makeText(this, this.BluetoothEncendido, Toast.LENGTH_LONG).show();

            return;
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();

        connectToDevice();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();

        /* close the textToSpeech engine to avoide the runtime exception from it */
        stopTextToSpeech();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (this.estadoBoton.equals("Desconectar")) {
            this.estadoBoton = "Connect";
            this.btnConnect.setText(this.conec);
            this.enable = "false";
            try {
                this.socket.close();
            } catch (IOException e) {
                ErrorUtils.logErrors(e,"HeightActivity.java","onStop","not connected");
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        encenderBluetooth();
        super.onRestart();
    }
    
    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
    }

    /* Request enabling bluetooth if not enabled */
    private void encenderBluetooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }

    // region Initialization methods

    /**
     *
     */
    private void setupUI(){
        setContentView(R.layout.activity_height_screen);

        this.mMacDispositivos = new ArrayList();
        this.mDispositivosVinculados = new ArrayList();

        this.multitxt = findViewById(R.id.et_multiline);
        this.Enviar = (String) getText(R.string.Enviar);
        this.conec = (String) getText(R.string.Connectad);
        this.Limpiar = (String) getText(R.string.Limpiar);
        this.Connectad = (String) getText(R.string.Connectad);
        this.ConectadO = (String) getText(R.string.ConectadO);
        this.Conectese = (String) getText(R.string.Conectese);
        this.Nosepuede = (String) getText(R.string.Nosepuede);
        this.disconec = (String) getText(R.string.Desconectad);
        this.Desconectad = (String) getText(R.string.Desconectad);
        this.Seleccionado = (String) getText(R.string.Seleccionado);
        this.ConexionPerdida = (String) getText(R.string.ConexionPerdida);
        this.BluetoothEncendido = (String) getText(R.string.BluetoothEncendido);

        this.btnNext = findViewById(R.id.btn_next);
        this.btnLimpiar = findViewById(R.id.btn_clean);
        this.btnConnect = findViewById(R.id.btn_connect);
        this.btnEnviar = findViewById(R.id.btn_get_height);

        sharedPreferencePersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        sharedPreferenceBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);

        txtAge = findViewById(R.id.tv_age);
        txtName = findViewById(R.id.tv_name);
        txtGender = findViewById(R.id.tv_gender);
        txtMobile = findViewById(R.id.tv_mobile_number);
        etManualHeight = findViewById(R.id.et_manual_height);

        this.spDevices = findViewById(R.id.sp_height);
    }

    private void setupEvents(){
        this.btnEnviar.setOnClickListener(view -> send());
        this.btnNext.setOnClickListener(view -> gotoNext());
        this.btnLimpiar.setOnClickListener(view -> clean());
        this.btnConnect.setOnClickListener(view -> connect());
    }

    private void initializeData(){
        this.btnLimpiar.setText(this.Limpiar);
        this.btnNext = findViewById(R.id.btn_next);

        this.multitxt.setFocusable(false);
        this.multitxt.setText(">:Bluetooth Terminal\n");
        this.multitxt.setTextColor(getResources().getColor(R.color.white));
        this.multitxt.setBackgroundColor(getResources().getColor(R.color.black));

        txtAge.setText("DOB : " + sharedPreferencePersonalData.getString("dob", ""));
        txtName.setText("Name : " + sharedPreferencePersonalData.getString("name", ""));
        txtGender.setText("Gender : " + sharedPreferencePersonalData.getString("gender", ""));
        txtMobile.setText("Phone : " + sharedPreferencePersonalData.getString("mobile_number", ""));
        
        sharedPreferenceActofit = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);

        if (this.estadoBoton.equals("Connect")) {
            this.btnConnect.setText(this.conec);
        }

        textToSpeech = new TextToSpeech(getApplicationContext(),this);
        
        initializeBluetooth();
    }
    
    private void initializeBluetooth(){
        encenderBluetooth();

        Set<BluetoothDevice> dispositivos = this.mBluetoothAdapter.getBondedDevices();
        if (dispositivos.size() > 0) {
            for (BluetoothDevice device : dispositivos) {
                if (device.getName().equals("HC-05")) {

                    this.mDispositivosVinculados.add(device.getName() + "\n" + device.getAddress());
                    this.mMacDispositivos.add(device.getAddress());

                    storeBluetoothInformation(device);
                }
            }
        }

        this.adapterDevices = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, this.mDispositivosVinculados);
        this.spDevices.setAdapter(this.adapterDevices);
        
        this.spDevices.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View arg1, int position, long arg3) {
                dispositivoSeleccionado = (String) ((CharSequence) mMacDispositivos.get(position));
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void storeBluetoothInformation(BluetoothDevice device) {
        sharedPreferenceBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferenceBluetoothAddress.edit();
        if(sharedPreferenceBluetoothAddress.getString("hcbluetooth","").equalsIgnoreCase("")){
            editor.putString("hcbluetooth", device.getAddress());
            editor.commit();
        }
    }

    // region Logical methods

    /**
     *
     */
    private void connect(){
        if (estadoBoton.equals("Connect")) {
            encenderBluetooth();
            new Connect(HeightActivity.this, null).execute(new String[]{dispositivoSeleccionado});
            enable = "false";
            return;
        }
        try {
            socket.close();
            estadoBoton = "Connect";
            btnConnect.setText(conec);
        } catch (Exception e) {
            ErrorUtils.logErrors(e,"HeightActivity.java","btnonClick","failded to close socket");
        }
    }

    /**
     *
     */
    private void send(){
        if (estadoBoton.equals("Connect")) {
            Toast.makeText(HeightActivity.this, "Connecting to device...", Toast.LENGTH_SHORT).show();
            connectToDevice();
            return;
        }

        multitxt.append(">:" + "1" + "\n");
        String env = "1";
        strHeight = "";

        try {
            if (etManualHeight.getText().length() > 0) {
                etManualHeight.setText("");
                outputStreamHeightReceiver.write(env.getBytes(Charset.forName("UTF-8")));
            } else {
                outputStreamHeightReceiver.write(env.getBytes(Charset.forName("UTF-8")));
            }
        } catch (IOException e) {
            ErrorUtils.logErrors(e,"HeightActivity.java","onClick","click event failed");
            Toast.makeText(HeightActivity.this, Nosepuede, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void clean(){
        etManualHeight.setText("");
    }

    /**
     *
     */
    private void gotoNext(){
        if (etManualHeight.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter Manual Height", Toast.LENGTH_SHORT).show();
            txt = "Please Enter Manual Height";
            speakOut(txt);
        } else {
            Intent objIntent = new Intent(getApplicationContext(), ActofitMainActivity.class);

            objIntent.putExtra(CoutputStreamHeightReceivertant.Fields.HEIGHT, etManualHeight.getText().toString());
            objIntent.putExtra(CoutputStreamHeightReceivertant.Fields.ID, sharedPreferencePersonalData.getString(CoutputStreamHeightReceivertant.Fields.ID, ""));
            objIntent.putExtra(CoutputStreamHeightReceivertant.Fields.NAME, sharedPreferencePersonalData.getString(CoutputStreamHeightReceivertant.Fields.NAME, ""));
            objIntent.putExtra(CoutputStreamHeightReceivertant.Fields.GENDER, sharedPreferencePersonalData.getString(CoutputStreamHeightReceivertant.Fields.GENDER, ""));
            objIntent.putExtra(CoutputStreamHeightReceivertant.Fields.DATE_OF_BIRTH, sharedPreferencePersonalData.getString(CoutputStreamHeightReceivertant.Fields.DATE_OF_BIRTH, ""));

            writeToActofitSharedPreference(CoutputStreamHeightReceivertant.Fields.HEIGHT, etManualHeight.getText().toString());

            startActivity(objIntent);
            finish();
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

    /**
     *
     */
    private void closeConnection() {
        if (inputStreamHeightReceiver != null) {
            try {inputStreamHeightReceiver.close();}
            catch (Exception e) {
                Log.e("",""+e.getMessage());
            }
            inputStreamHeightReceiver = null;
        }

        if (outputStreamHeightReceiver != null) {
            try {outputStreamHeightReceiver.close();} catch (Exception e) {
                Log.e("",""+e.getMessage());
            }
            outputStreamHeightReceiver = null;
        }

        if (socket != null) {
            try {socket.close();} catch (Exception e) {
                Log.e("",""+e.getMessage());
            }
            socket = null;
        }
    }
    
    /**
     *
     */
    private void writeToActofitSharedPreference(String key, String value){
        SharedPreferences sharedPreference = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // endregion

    // region Nested classes

    /** Recibir => Receiver **/
    public class Recibir extends AsyncTask<String, String, String> {
        public Recibir() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            message = "";
            byte[] buffer = new byte[128];

            while (enable.equals("true")) {
                try {
                    int bytes = inputStreamHeightReceiver.read(buffer);
                    message = new String(buffer, 0, bytes);
                } catch (IOException e) {
                    message = "";
                    enable = "false";
                    ErrorUtils.logErrors(e,"HeightActivity.java","doInBackground","failed to read bytes data");
                }

                publishProgress(new String[]{message, enable});
            }

            return message;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(String... recib) {
            str = recib[0];
            str = str.replace("�", "");

            strHeight += str;

            try {
                if (!strHeight.equalsIgnoreCase("")) {
                    adjustedHeight = Integer.parseInt(strHeight) - 1;
                    etManualHeight.setText(String.valueOf(adjustedHeight));
                }
            }catch (Exception e){
                ErrorUtils.logErrors(e,"HeightActivity.java","onProgressUpdate","failed to adjust height");
            }

            if (recib[1].equals("false")) {
                enable = "false";
                estadoBoton2 = conec;
                estadoBoton = "Connect";

                multitxt.append(">:" + ConexionPerdida + "\n");
            } else {
                enable = "true";
                estadoBoton2 = disconec;
                estadoBoton = "Desconectar";
            }

            btnConnect.setText(estadoBoton2);
        }
    }

    private class Connect extends AsyncTask<String, String, String> {
        public Connect() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(HeightActivity.this);
            progressDialog.setMessage("Connecting..");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... deviceAddresses) {
            String deviceAddress = deviceAddresses[0];

            if (deviceAddress.trim().length() == 0) {
                return "";
            }

            dispositivo = mBluetoothAdapter.getRemoteDevice(d);

            try {
                socket = dispositivo.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                
                if(!socket.isConnected()){
                    socket.connect();
                }
                
                inputStreamHeightReceiver = socket.getInputStream();
                outputStreamHeightReceiver = socket.getOutputStream();

                return "Conectado";
            } catch (Exception e) {
                ErrorUtils.logErrors(e,"HeightActivity.java","doInBackground","failed to connect with socket");
                return "";
            }
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            String mEstado = result;

            if (result.equals("Conectado")) {
                mEstado = ConectadO;
            }

            Toast.makeText(HeightActivity.this, mEstado, Toast.LENGTH_SHORT).show();

            if (result.equals("Conectado")) {
                enable = "true";
                estadoBoton2 = disconec;
                estadoBoton = "Desconectar";

                speakOut("Please stand below the height sensor and click get Height Button");

                new Recibir().execute(new String[]{enable});
            } else {
                if(connectTryCount > ALLOWED_BLUETOOTH_CONNECT_TRY_COUNT) {
                    showCannotConnectToDevice();
                }else{
                    try{
                        Thread.sleep(CONNECT_TRY_PAUSE_MILLISECONDS);

                        // Increase connection try count and try to connect
                        connectTryCount++;

                        connectToDevice();
                    }catch(Exception ex){
                        ErrorUtils.logErrors(ex,"HeightActivity.java","onPostExecute","error while reconnecting");
                        showCannotConnectToDevice();
                    }
                }
            }

            btnConnect.setText(estadoBoton2);
        }
    }

    // endregion
}
