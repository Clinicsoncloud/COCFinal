package com.abhaybmicoc.app.heightweight;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.OtpLoginScreen;
import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.actofitheight.ActofitMainActivity;
import com.abhaybmicoc.app.utils.ApiUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class Principal extends Activity implements TextToSpeech.OnInitListener, OnClickListener {
    private static final int REQUEST_ENABLE_BT = 3;
    String BluetoothEncendido;
    String ConectadO;
    String Conectese;
    String ConexionPerdida;
    String Connectad;
    String Desconectad;
    String Enviar;
    String Limpiar;
    String Nosepuede;
    String Seleccionado;
    ArrayAdapter<String> adp;
    Button btn;
//    byte[] byteArray;
    String conec;
    EditText datos;
//    DataInputStream dins;
    String disconec;
    BluetoothDevice dispositivo;
    String dispositivoSeleccionado;
    String enable;
    Button enviar;
    String estadoBoton = "Connect";
    String estadoBoton2;
    InputStream ins;
    Button limpiar;
    BluetoothAdapter mBluetoothAdapter;
    ArrayList mDispositivosVinculados;
    ArrayList mMacDispositivos;
    String message = "";
    EditText multitxt, etManualHeight;
    OutputStream ons;
    String recibido;
    BluetoothSocket socket;
    String btDeviceAddres;
    Spinner spn;
    Button next;
    SharedPreferences shared;
    SharedPreferences objdoctor;
    ProgressDialog progressDialog;
    SharedPreferences objBluetoothAddress;
    private TextView txtName, txtAge, txtGender, txtMobile;
    private TextView txtHeight, txtWeight, txtTemprature, txtBpMonitor,txtOximeter,txtSugar,txtHemoglobin;
    String str = "";
    private StringBuffer sb;
    private int adjustedHeight;
    private String strHeight = "";
    private TextToSpeech tts;
    String txt ="";
    private Context context;

    private boolean isConnected = false;
    
    private int connectTryCount = 0;
    private int ALLOWED_BLUETOOTH_CONNECT_TRY_COUNT = 1;

    //milliseconds declaration
    //It will take break of 5ms while trying to reconect
    private int CONNECT_TRY_PAUSE_MILLISECONDS = 500;

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
//        String text = "StartActivity me aapka swagat hain kripaya next button click kre aur aage badhe";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onClick(View view) {

        //Click event on top headers on click perform perticular action

    }

    private void openScreen(Intent intent) {
        startActivity(intent);
    }


    private class Connect extends AsyncTask<String, String, String> {
        private Connect() {
        }

        /* synthetic */ Connect(Principal principal, Connect Connect) {
            this();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Principal.this);
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

            Principal.this.dispositivo = Principal.this.mBluetoothAdapter.getRemoteDevice(d);
            try {
                Principal.this.socket = Principal.this.dispositivo.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                if(!Principal.this.socket.isConnected()){
                    Principal.this.socket.connect();
                }
                Principal.this.ins = Principal.this.socket.getInputStream();
                Principal.this.ons = Principal.this.socket.getOutputStream();
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
                mEstado = Principal.this.ConectadO;
            }
            Toast.makeText(Principal.this, mEstado, Toast.LENGTH_SHORT).show();
            if (eg.equals("Conectado")) {
                Principal.this.estadoBoton = "Desconectar";
                Principal.this.estadoBoton2 = Principal.this.disconec;
                Principal.this.enable = "true";
                txt = "Please stand below the height sensor and click get Height Button";
                speakOut(txt);
                new Recibir().execute(new String[]{Principal.this.enable});
            } else {
                if(Principal.this.connectTryCount > Principal.this.ALLOWED_BLUETOOTH_CONNECT_TRY_COUNT) {
                    Principal.this.showCannotConnectToDevice();
                }else{
                    try{
                        Thread.sleep(Principal.this.CONNECT_TRY_PAUSE_MILLISECONDS);
                        
                        // Increase connection try count and try to connect
                        Principal.this.connectTryCount++;
                        Principal.this.connectToDevice();

                    }catch(Exception ex){

                        Principal.this.showCannotConnectToDevice();

                    }
                }
            }
            Principal.this.btn.setText(Principal.this.estadoBoton2);
        }
    }

    private void showCannotConnectToDevice(){
        // Reset connect try count to 0
        Principal.this.connectTryCount = 0;

        Principal.this.estadoBoton = "Connect";
        Principal.this.estadoBoton2 = Principal.this.conec;
        Principal.this.enable = "false";
        txt = "No Bluetooth Device Found Please Connect it Manually";
        speakOut(txt);
    }
    
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
            Principal.this.message = "";
            while (Principal.this.enable.equals("true")) {
                try {
                    int bytes = Principal.this.ins.read(buffer);
                    Principal.this.message = new String(buffer, 0, bytes);
                } catch (IOException e) {
                    Principal.this.enable = "false";
                    Principal.this.message = "";
                }
                publishProgress(new String[]{Principal.this.message, Principal.this.enable});
            }
            return Principal.this.message;
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(String... recib) {

            str = recib[0];

            str = str.replace("�", "");

            System.out.println("=======Str :==="+str);

            Principal.this.etManualHeight.append("" +str);

           /* strHeight += str;
            System.out.println("=======Str2 :==="+Principal.this.etManualHeight.getText());
            System.out.println("=======strHeight :==="+strHeight);

            try {
                if (!strHeight.equalsIgnoreCase("")) {
                    adjustedHeight = Integer.parseInt(strHeight) - 11;
                    Principal.this.etManualHeight.setText(String.valueOf(adjustedHeight));
                }
            }catch (Exception e){

            }*/

            if (recib[1].equals("false")) {
                Principal.this.estadoBoton = "Connect";
                Principal.this.estadoBoton2 = Principal.this.conec;
                Principal.this.enable = "false";
                Principal.this.multitxt.append(">:" + Principal.this.ConexionPerdida + "\n");
            } else {
                Principal.this.estadoBoton = "Desconectar";
                Principal.this.estadoBoton2 = Principal.this.disconec;
                Principal.this.enable = "true";
            }
            Principal.this.btn.setText(Principal.this.estadoBoton2);
        }

    }

    @Override
    public void onBackPressed() {
        context.startActivity(new Intent(Principal.this, OtpLoginScreen.class));
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height_screen);

        this.multitxt = (EditText) findViewById(R.id.editText2);
        this.multitxt.setBackgroundColor(getResources().getColor(R.color.black));
        this.multitxt.setTextColor(getResources().getColor(R.color.white));
        this.multitxt.setFocusable(false);
        this.multitxt.setText(">:Bluetooth Terminal\n");
        this.mDispositivosVinculados = new ArrayList();
        this.mMacDispositivos = new ArrayList();
        this.conec = (String) getText(R.string.Connectad);
        this.disconec = (String) getText(R.string.Desconectad);
        this.Connectad = (String) getText(R.string.Connectad);
        this.Desconectad = (String) getText(R.string.Desconectad);
        this.ConexionPerdida = (String) getText(R.string.ConexionPerdida);
        this.ConectadO = (String) getText(R.string.ConectadO);
        this.Conectese = (String) getText(R.string.Conectese);
        this.Nosepuede = (String) getText(R.string.Nosepuede);
        this.Seleccionado = (String) getText(R.string.Seleccionado);
        this.Enviar = (String) getText(R.string.Enviar);
        this.Limpiar = (String) getText(R.string.Limpiar);
        this.BluetoothEncendido = (String) getText(R.string.BluetoothEncendido);
        this.btn = (Button) findViewById(R.id.button1);
        this.enviar = (Button) findViewById(R.id.button3);
//        this.enviar.setText(this.Enviar);
        this.limpiar = (Button) findViewById(R.id.button4);
        this.limpiar.setText(this.Limpiar);
        this.datos = (EditText) findViewById(R.id.editText1);
        shared = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        objBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);

        Log.e("onCreate_address",""+objBluetoothAddress.getString("hcbluetooth",""));

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);
        etManualHeight = findViewById(R.id.etManualHeight);

        //All headers to select and go on perticylar screen
        // when we click o the perticular screen open the screeen and do perticular test

        txtHeight = findViewById(R.id.txtmainheight);
        txtWeight = findViewById(R.id.txtmainweight);
        txtTemprature = findViewById(R.id.txtmaintempreture);
        txtBpMonitor = findViewById(R.id.txtmainbloodpressure);
        txtOximeter = findViewById(R.id.txtmainpulseoximeter);
        txtSugar = findViewById(R.id.txtmainbloodsugar);
        txtHemoglobin = findViewById(R.id.txtmainhemoglobin);


        context = Principal.this;

        txtName.setText("Name : " + shared.getString("name", ""));
        txtGender.setText("Gender : " + shared.getString("gender", ""));
        txtMobile.setText("Phone : " + shared.getString("mobile_number", ""));
        txtAge.setText("DOB : " + shared.getString("dob", ""));

        objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);

        if (this.estadoBoton.equals("Connect")) {
            this.btn.setText(this.conec);
        }

        this.next = findViewById(R.id.btnnext);

        tts = new TextToSpeech(getApplicationContext(),this);

        this.next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etManualHeight.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter Manual Height", Toast.LENGTH_SHORT).show();
                    txt = "Please Enter Manual Height";
                    speakOut(txt);
                } else {
                    System.out.println(" Height : = "+Principal.this.etManualHeight.getText().toString());
                    Intent objIntent = new Intent(getApplicationContext(), ActofitMainActivity.class);
                    objIntent.putExtra("id", shared.getString("id", ""));
                    objIntent.putExtra("name", shared.getString("name", ""));
                    objIntent.putExtra("gender", shared.getString("gender", ""));
                    objIntent.putExtra("dob", shared.getString("dob", ""));
                    objIntent.putExtra("height", etManualHeight.getText().toString());
                    objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
                    SharedPreferences.Editor editor = objdoctor.edit();
                    editor.putString("height", etManualHeight.getText().toString());
                    editor.commit();
                    startActivity(objIntent);
                    finish();
                }

            }
        });

        this.spn = (Spinner) findViewById(R.id.spinner1);
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

                Principal.this.dispositivoSeleccionado = (String) ((CharSequence) Principal.this.mMacDispositivos.get(position));

                Log.e("address", "" + objBluetoothAddress.getString("hcbluetooth", ""));

//                Toast.makeText(Principal.this, new StringBuilder(String.valueOf(Principal.this.Seleccionado)).append((String) ((CharSequence) Principal.this.mDispositivosVinculados.get(position))).toString(), Toast.LENGTH_SHORT).show();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        this.btn.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                if (Principal.this.estadoBoton.equals("Connect")) {
                    Principal.this.encenderBluetooth();
                    new Connect(Principal.this, null).execute(new String[]{Principal.this.dispositivoSeleccionado});
                    Principal.this.enable = "false";
                    return;
                }
                try {
                    Principal.this.socket.close();
                    Principal.this.estadoBoton = "Connect";
                    Principal.this.btn.setText(Principal.this.conec);
                } catch (Exception e) {
                }
            }
        });
        this.enviar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.e("getHeight","clicked");
                if (Principal.this.estadoBoton.equals("Connect")) {
                    Toast.makeText(Principal.this, "Connecting to device...", Toast.LENGTH_SHORT).show();
                    connectToDevice();
                    return;
                }
                Principal.this.multitxt.append(">:" + "1" + "\n");
                String env = "1";
                strHeight = "";
                try {
                    if (etManualHeight.getText().length() > 0) {
                        etManualHeight.setText("");
                        Principal.this.ons.write(env.getBytes(Charset.forName("UTF-8")));
                    } else {
                        Principal.this.ons.write(env.getBytes(Charset.forName("UTF-8")));
                    }
                } catch (IOException e) {
                    Toast.makeText(Principal.this, Principal.this.Nosepuede, Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.limpiar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Principal.this.etManualHeight.setText("");
            }
        });
    }


    private void connectToDevice() {
        Log.e("auto_connect", "method working");
        Log.e("connected_state"," = "+estadoBoton);
        if (Principal.this.estadoBoton.equals("Connect")) {
            Principal.this.encenderBluetooth();
            new Connect(Principal.this, null).execute(new String[]{objBluetoothAddress.getString("hcbluetooth", "")});
            Principal.this.enable = "false";
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

        if(!objdoctor.getString("height","").equals("")) {
            Log.e("onResume : ", "Height :" + objdoctor.getString("height", ""));
        }
        connectToDevice();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();

        /*
        close the soket connection of bluetooth and sensor
        free up the bluetooth socket
        close the input and output stream
        */
//        closeConnection();

        //close the tts engine to avoide the runtime exception from it
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }

    }

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

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (this.estadoBoton.equals("Desconectar")) {
            this.estadoBoton = "Connect";
            this.btn.setText(this.conec);
            this.enable = "false";
            try {
                this.socket.close();
            } catch (IOException e) {
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
}
