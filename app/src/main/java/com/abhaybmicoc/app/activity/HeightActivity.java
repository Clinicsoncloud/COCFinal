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

    private ArrayAdapter<String> adp;
    private ArrayList mMacDispositivos;
    private ArrayList mDispositivosVinculados;

    private EditText multitxt, etManualHeight;

    private InputStream ins;
    private OutputStream ons;
    private BluetoothSocket socket;
    private BluetoothAdapter mBluetoothAdapter;

    private Spinner spn;
    private ProgressDialog progressDialog;

    private SharedPreferences shared;
    private SharedPreferences sharedPreferenceActofit;
    private SharedPreferences objBluetoothAddress;

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
        HeightActivity.this.connectTryCount = 0;

        HeightActivity.this.estadoBoton = "Connect";
        HeightActivity.this.estadoBoton2 = HeightActivity.this.conec;
        HeightActivity.this.enable = "false";
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

        sharedPreferenceActofit = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);

        if (this.estadoBoton.equals("Connect")) {
            this.btnConnect.setText(this.conec);
        }

        this.btnNext = findViewById(R.id.btnnext);

        textToSpeech = new TextToSpeech(getApplicationContext(),this);

    }


    private void connectToDevice() {
        if (HeightActivity.this.estadoBoton.equals("Connect")) {
            HeightActivity.this.encenderBluetooth();
            new Connect(HeightActivity.this, null).execute(new String[]{objBluetoothAddress.getString("hcbluetooth", "")});
            HeightActivity.this.enable = "false";
            return;
        }
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
            this.adp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, this.mDispositivosVinculados);
            this.spn.setAdapter(this.adp);
            Toast.makeText(this, this.BluetoothEncendido, Toast.LENGTH_LONG).show();
            return;
        }
        finish();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();

        if(!sharedPreferenceActofit.getString("height","").equals("")) {
            Log.e("onResume : ", "Height :" + sharedPreferenceActofit.getString("height", ""));
        }
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

    public void encenderBluetooth() {
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

        this.multitxt = findViewById(R.id.editText2);
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

        this.btnConnect = findViewById(R.id.button1);
        this.btnEnviar = findViewById(R.id.button3);
        this.btnLimpiar = findViewById(R.id.button4);

        shared = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        objBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);

        txtAge = findViewById(R.id.txtAge);
        txtName = findViewById(R.id.txtName);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);
        etManualHeight = findViewById(R.id.etManualHeight);

        this.spn = findViewById(R.id.spinner1);
    }

    private void setupEvents(){
        this.btnNext.setOnClickListener(view -> gotoNext());
        this.btnConnect.setOnClickListener(view -> connect());

        encenderBluetooth();
        Set<BluetoothDevice> dispositivos = this.mBluetoothAdapter.getBondedDevices();
        if (dispositivos.size() > 0) {
            for (BluetoothDevice device : dispositivos) {
                if (device.getName().equals("HC-05")) {

                    this.mDispositivosVinculados.add(device.getName() + "\n" + device.getAddress());
                    this.mMacDispositivos.add(device.getAddress());

                    objBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);
                    SharedPreferences.Editor editor = objBluetoothAddress.edit();
                    if(objBluetoothAddress.getString("hcbluetooth","").equalsIgnoreCase("")){
                        editor.putString("hcbluetooth", device.getAddress());
                        editor.commit();
                    }
                    Log.e("device_name", "" + device.getName());
                    Log.e("device_address", "" + device.getAddress());
                }

            }
        }

        this.adp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, this.mDispositivosVinculados);
        this.spn.setAdapter(this.adp);
        this.spn.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View arg1, int position, long arg3) {
                HeightActivity.this.dispositivoSeleccionado = (String) ((CharSequence) HeightActivity.this.mMacDispositivos.get(position));

                Log.e("address", "" + objBluetoothAddress.getString("hcbluetooth", ""));

//                Toast.makeText(HeightActivity.this, new StringBuilder(String.valueOf(HeightActivity.this.Seleccionado)).append((String) ((CharSequence) HeightActivity.this.mDispositivosVinculados.get(position))).toString(), Toast.LENGTH_SHORT).show();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        this.btnEnviar.setOnClickListener(view -> send());
        this.btnLimpiar.setOnClickListener(view -> clean());
    }

    private void initializeData(){
        this.btnLimpiar.setText(this.Limpiar);

        this.multitxt.setFocusable(false);
        this.multitxt.setText(">:Bluetooth Terminal\n");
        this.multitxt.setTextColor(getResources().getColor(R.color.white));
        this.multitxt.setBackgroundColor(getResources().getColor(R.color.black));

        txtAge.setText("DOB : " + shared.getString("dob", ""));
        txtName.setText("Name : " + shared.getString("name", ""));
        txtGender.setText("Gender : " + shared.getString("gender", ""));
        txtMobile.setText("Phone : " + shared.getString("mobile_number", ""));
    }

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
     */
    private void connect(){
        if (HeightActivity.this.estadoBoton.equals("Connect")) {
            HeightActivity.this.encenderBluetooth();
            new Connect(HeightActivity.this, null).execute(new String[]{HeightActivity.this.dispositivoSeleccionado});
            HeightActivity.this.enable = "false";
            return;
        }
        try {
            HeightActivity.this.socket.close();
            HeightActivity.this.estadoBoton = "Connect";
            HeightActivity.this.btnConnect.setText(HeightActivity.this.conec);
        } catch (Exception e) {
            ErrorUtils.logErrors(e,"HeightActivity.java","btnonClick","failded to close socket");
        }
    }

    /**
     *
     */
    private void send(){
        Log.e("getHeight","clicked");
        if (HeightActivity.this.estadoBoton.equals("Connect")) {
            Toast.makeText(HeightActivity.this, "Connecting to device...", Toast.LENGTH_SHORT).show();
            connectToDevice();
            return;
        }

        HeightActivity.this.multitxt.append(">:" + "1" + "\n");
        String env = "1";
        strHeight = "";

        try {
            if (etManualHeight.getText().length() > 0) {
                etManualHeight.setText("");
                HeightActivity.this.ons.write(env.getBytes(Charset.forName("UTF-8")));
            } else {
                HeightActivity.this.ons.write(env.getBytes(Charset.forName("UTF-8")));
            }
        } catch (IOException e) {
            ErrorUtils.logErrors(e,"HeightActivity.java","onClick","click event failed");
            Toast.makeText(HeightActivity.this, HeightActivity.this.Nosepuede, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void clean(){
        HeightActivity.this.etManualHeight.setText("");
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

            objIntent.putExtra("id", shared.getString("id", ""));
            objIntent.putExtra("name", shared.getString("name", ""));
            objIntent.putExtra("gender", shared.getString("gender", ""));
            objIntent.putExtra("dob", shared.getString("dob", ""));
            objIntent.putExtra("height", etManualHeight.getText().toString());

            writeToActofitSharedPreference("height", etManualHeight.getText().toString());

            startActivity(objIntent);
            finish();
        }
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
        if (ins != null) {
            try {ins.close();}
            catch (Exception e) {
                Log.e("",""+e.getMessage());
            }
            ins = null;
        }

        if (ons != null) {
            try {ons.close();} catch (Exception e) {
                Log.e("",""+e.getMessage());
            }
            ons = null;
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


    public class Recibir extends AsyncTask<String, String, String> {

        public Recibir() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("onPreExecute : Executed");
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            String str = params[0];
            byte[] buffer = new byte[128];
            HeightActivity.this.message = "";
            while (HeightActivity.this.enable.equals("true")) {
                try {
                    int bytes = HeightActivity.this.ins.read(buffer);
                    HeightActivity.this.message = new String(buffer, 0, bytes);
                } catch (IOException e) {
                    HeightActivity.this.enable = "false";
                    HeightActivity.this.message = "";
                    ErrorUtils.logErrors(e,"HeightActivity.java","doInBackground","failed to read bytes data");
                }
                publishProgress(new String[]{HeightActivity.this.message, HeightActivity.this.enable});
            }
            return HeightActivity.this.message;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(String... recib) {

            str = recib[0];
            str = str.replace("�", "");

            strHeight += str;
            System.out.println("=======Str2 :==="+ HeightActivity.this.etManualHeight.getText());
            System.out.println("=======strHeight :==="+strHeight);

            try {
                if (!strHeight.equalsIgnoreCase("")) {
                    adjustedHeight = Integer.parseInt(strHeight) - 1;
                    HeightActivity.this.etManualHeight.setText(String.valueOf(adjustedHeight));
                }
            }catch (Exception e){
                ErrorUtils.logErrors(e,"HeightActivity.java","onProgressUpdate","failed to adjust height");
            }

            if (recib[1].equals("false")) {
                HeightActivity.this.estadoBoton = "Connect";
                HeightActivity.this.estadoBoton2 = HeightActivity.this.conec;
                HeightActivity.this.enable = "false";
                HeightActivity.this.multitxt.append(">:" + HeightActivity.this.ConexionPerdida + "\n");
            } else {
                HeightActivity.this.estadoBoton = "Desconectar";
                HeightActivity.this.estadoBoton2 = HeightActivity.this.disconec;
                HeightActivity.this.enable = "true";
            }
            HeightActivity.this.btnConnect.setText(HeightActivity.this.estadoBoton2);
        }

    }

    private class Connect extends AsyncTask<String, String, String> {
        private Connect() {

        }

        /* synthetic */ Connect(HeightActivity principal, Connect Connect) {
            this();
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
        public String doInBackground(String... disp) {
            String d = disp[0];
            Log.e("d", "" + d);
            if (d.equalsIgnoreCase("")) {
                return "";
            }

            HeightActivity.this.dispositivo = HeightActivity.this.mBluetoothAdapter.getRemoteDevice(d);
            try {
                HeightActivity.this.socket = HeightActivity.this.dispositivo.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                if(!HeightActivity.this.socket.isConnected()){
                    HeightActivity.this.socket.connect();
                }
                HeightActivity.this.ins = HeightActivity.this.socket.getInputStream();
                HeightActivity.this.ons = HeightActivity.this.socket.getOutputStream();
                return "Conectado";

            } catch (Exception e) {
                ErrorUtils.logErrors(e,"HeightActivity.java","doInBackground","failed to connect with socket");
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
                mEstado = HeightActivity.this.ConectadO;
            }
            Toast.makeText(HeightActivity.this, mEstado, Toast.LENGTH_SHORT).show();
            if (eg.equals("Conectado")) {
                HeightActivity.this.estadoBoton = "Desconectar";
                HeightActivity.this.estadoBoton2 = HeightActivity.this.disconec;
                HeightActivity.this.enable = "true";
                txt = "Please stand below the height sensor and click get Height Button";
                speakOut(txt);
                new Recibir().execute(new String[]{HeightActivity.this.enable});
            } else {
                if(HeightActivity.this.connectTryCount > HeightActivity.this.ALLOWED_BLUETOOTH_CONNECT_TRY_COUNT) {
                    HeightActivity.this.showCannotConnectToDevice();
                }else{
                    try{
                        Thread.sleep(HeightActivity.this.CONNECT_TRY_PAUSE_MILLISECONDS);

                        // Increase connection try count and try to connect
                        HeightActivity.this.connectTryCount++;
                        HeightActivity.this.connectToDevice();

                    }catch(Exception ex){

                        ErrorUtils.logErrors(ex,"HeightActivity.java","onPostExecute","error while reconnecting");
                        HeightActivity.this.showCannotConnectToDevice();

                    }
                }
            }
            HeightActivity.this.btnConnect.setText(HeightActivity.this.estadoBoton2);
        }
    }

    // endregion
}
