package com.abhaybmi.app.heightweight;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.abhaybmi.app.R;
import com.abhaybmi.app.actofitheight.ActofitMainActivity;
import com.abhaybmi.app.utils.ApiUtils;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class Principal extends Activity {
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
    byte[] byteArray;
    String conec;
    EditText datos;
    DataInputStream dins;
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
    ProgressDialog progressDialog;
    SharedPreferences objBluetoothAddress;
    private TextView txtName, txtAge, txtGender, txtMobile;
    String str = "";
    private StringBuffer sb;
    private int adjustedHeight;
    private String strHeight = "";



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

            progressDialog.dismiss();
            String mEstado = res;
            String eg = res;
            if (res == "Conectado") {
                mEstado = Principal.this.ConectadO;
            }
            Toast.makeText(Principal.this, mEstado, Toast.LENGTH_SHORT).show();
            if (eg == "Conectado") {
                Principal.this.estadoBoton = "Desconectar";
                Principal.this.estadoBoton2 = Principal.this.disconec;
                Principal.this.enable = "true";
                new Recibir().execute(new String[]{Principal.this.enable});
            } else {
                Principal.this.estadoBoton = "Connect";
                Principal.this.estadoBoton2 = Principal.this.conec;
                Principal.this.enable = "false";
            }
            Principal.this.btn.setText(Principal.this.estadoBoton2);
        }
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
            while (Principal.this.enable == "true") {
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

//            Principal.this.etManualHeight.append("" + str);

            strHeight += str;
            System.out.println("=======Str2 :==="+Principal.this.etManualHeight.getText());
            System.out.println("=======strHeight :==="+strHeight);

            if(!strHeight.equalsIgnoreCase("")) {
                adjustedHeight = Integer.parseInt(strHeight) + 12;
                Principal.this.etManualHeight.setText(String.valueOf(adjustedHeight));
            }

            if (recib[1] == "false") {
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

        txtName.setText("Name : " + shared.getString("name", ""));
        txtGender.setText("Gender : " + shared.getString("gender", ""));
        txtMobile.setText("Phone : " + shared.getString("mobile_number", ""));
        txtAge.setText("DOB : " + shared.getString("dob", ""));

        if (this.estadoBoton == "Connect") {
            this.btn.setText(this.conec);
        }


        this.next = findViewById(R.id.btnnext);


     /*   if(!objBluetoothAddress.getString("hcbluetooth","").equalsIgnoreCase("")) {
            connectToDevice();
        }*/


        this.next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etManualHeight.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter Manual Height", Toast.LENGTH_SHORT).show();
                } else {

                    System.out.println(" Height : = "+Principal.this.etManualHeight.getText().toString());
                    Intent objIntent = new Intent(getApplicationContext(), ActofitMainActivity.class);
                    objIntent.putExtra("id", shared.getString("id", ""));
                    objIntent.putExtra("name", shared.getString("name", ""));
                    objIntent.putExtra("gender", shared.getString("gender", ""));
                    objIntent.putExtra("dob", shared.getString("dob", ""));
                    objIntent.putExtra("height", etManualHeight.getText().toString());
                    SharedPreferences objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
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
                if (Principal.this.estadoBoton == "Connect") {
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
                if (Principal.this.estadoBoton == "Connect") {
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
        if (Principal.this.estadoBoton == "Connect") {
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
        connectToDevice();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        if (this.estadoBoton == "Desconectar") {
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
