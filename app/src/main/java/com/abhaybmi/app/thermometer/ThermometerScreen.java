package com.abhaybmi.app.thermometer;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmi.app.BpLoginScreen;
import com.abhaybmi.app.DashboardActivity;
import com.abhaybmi.app.OtpVerifyScreen;
import com.abhaybmi.app.R;
import com.abhaybmi.app.oximeter.MainActivity;
import com.abhaybmi.app.oxygen.OxygenActivity;
import com.abhaybmi.app.utils.ApiUtils;
import com.abhaybmi.app.utils.Tools;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static com.abhaybmi.app.utils.ApiUtils.PREFERENCE_THERMOMETERDATA;

public class ThermometerScreen extends AppCompatActivity {

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
    ProgressDialog progressDialog;
    private TextView display;
    private EditText editText1;
    SharedPreferences userData, shared;
    private TextView txtName, txtAge, txtGender, txtMobile;
    private SharedPreferences objBluetoothAddress;
    private Button getTemp;
    public static String TAG = "ThermometerActivity";


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

            progressDialog.dismiss();
            String mEstado = res;
            String eg = res;
            if (res == "Conectado") {
                mEstado = ThermometerScreen.this.ConectadO;
            }
            Toast.makeText(ThermometerScreen.this, mEstado, Toast.LENGTH_SHORT).show();
            if (eg == "Conectado") {
                ThermometerScreen.this.estadoBoton = "Desconectar";
                ThermometerScreen.this.estadoBoton2 = ThermometerScreen.this.disconec;
                ThermometerScreen.this.enable = "true";
                sendCommand();
                new ThermometerScreen.Recibir().execute(new String[]{ThermometerScreen.this.enable});
            } else {
                ThermometerScreen.this.estadoBoton = "Connect";
                ThermometerScreen.this.estadoBoton2 = ThermometerScreen.this.conec;
                ThermometerScreen.this.enable = "false";
            }
            ThermometerScreen.this.btn.setText(ThermometerScreen.this.estadoBoton2);
        }
    }

    public class Recibir extends AsyncTask<String, String, String> {
        public Recibir() {
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            String str = params[0];
            byte[] buffer = new byte[128];
            ThermometerScreen.this.message = "";
            while (ThermometerScreen.this.enable == "true") {
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

            String str = recib[0];

                str = str.replace("Ready", "");

            ThermometerScreen.this.editText1.append("" + str);


            if (recib[1] == "false") {
                ThermometerScreen.this.estadoBoton = "Connect";
                ThermometerScreen.this.estadoBoton2 = ThermometerScreen.this.conec;
                ThermometerScreen.this.enable = "false";
            } else {
                ThermometerScreen.this.estadoBoton = "Desconectar";
                ThermometerScreen.this.estadoBoton2 = ThermometerScreen.this.disconec;
                ThermometerScreen.this.enable = "true";
            }
            ThermometerScreen.this.btn.setText(ThermometerScreen.this.estadoBoton2);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp);


        setStrings();

        userData = getSharedPreferences(PREFERENCE_THERMOMETERDATA, MODE_PRIVATE);

        objBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);
        display = (TextView) findViewById(R.id.textView1);
        editText1 = findViewById(R.id.editText1);
        editText1.setVisibility(View.VISIBLE);

       /* InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText1, InputMethodManager.SHOW_IMPLICIT);*/

        shared = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        //String channel = (shared.getString(keyChannel, ""));


        Log.e(TAG,"pref_address : "+objBluetoothAddress.getString("hcthermometer",""));

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);
        btn = findViewById(R.id.button1);
        getTemp = findViewById(R.id.button3);


        txtName.setText("Name : " + shared.getString("name", ""));
        txtGender.setText("Gender : " + shared.getString("gender", ""));
        txtMobile.setText("Phone : " + shared.getString("mobile_number", ""));
        txtAge.setText("DOB : " + shared.getString("dob", ""));
        Button baudrateButton = (Button) findViewById(R.id.buttonBaudrate);

        if (this.estadoBoton == "Connect") {
            this.btn.setText(this.conec);
        }

        this.spn = (Spinner) findViewById(R.id.spinner1);
        encenderBluetooth();
        Set<BluetoothDevice> dispositivos = this.mBluetoothAdapter.getBondedDevices();
        if (dispositivos.size() > 0) {
            for (BluetoothDevice device : dispositivos) {

                if (device.getName().equals("TEMPERATURE")) {

                    this.mDispositivosVinculados.add(device.getName() + "\n" + device.getAddress());
                    this.mMacDispositivos.add(device.getAddress());

                    objBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);
                    SharedPreferences.Editor editor = objBluetoothAddress.edit();
                    if(objBluetoothAddress.getString("hcthermometer","").equalsIgnoreCase("")){
                        editor.putString("hcthermometer", device.getAddress());
                        editor.commit();
                    }
                    Log.e("device_name", "" + device.getName());
                    Log.e("device_address", "" + device.getAddress());
                }

            }
        }
        this.adp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, this.mDispositivosVinculados);
        this.spn.setAdapter(this.adp);
        this.spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View arg1, int position, long arg3) {


                ThermometerScreen.this.dispositivoSeleccionado = (String) ((CharSequence) ThermometerScreen.this.mMacDispositivos.get(position));

                Log.e(TAG, "address_thermo : " + objBluetoothAddress.getString("hcthermometer", ""));

//           Toast.makeText(ThermometerScreen.this, new StringBuilder(String.valueOf(ThermometerScreen.this.Seleccionado)).append((String) ((CharSequence) ThermometerScreen.this.mDispositivosVinculados.get(position))).toString(), Toast.LENGTH_SHORT).show();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        this.btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (ThermometerScreen.this.estadoBoton == "Connect") {
                    ThermometerScreen.this.encenderBluetooth();
                    new ThermometerScreen.Connect(ThermometerScreen.this, null).execute(new String[]{ThermometerScreen.this.dispositivoSeleccionado});
                    ThermometerScreen.this.enable = "false";
                    return;
                }
                try {
                    ThermometerScreen.this.socket.close();
                    ThermometerScreen.this.estadoBoton = "Connect";
                    ThermometerScreen.this.btn.setText(ThermometerScreen.this.conec);
                } catch (Exception e) {
                }
            }
        });
        baudrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getApplicationContext().stopService(new Intent(getApplicationContext(), ThermometerUsbService.class));
                if(editText1.getText().length() > 0) {
                    Intent objpulse = new Intent(getApplicationContext(), MainActivity.class);
                    SharedPreferences.Editor editor = userData.edit();
                    editor.putString("data", editText1.getText().toString());
                    editor.commit();
                    try{
                        offBluetooth();
                    }catch (Exception e){}
                    startActivity(objpulse);
                    finish();
                }else{
                    Toast.makeText(ThermometerScreen.this, "Enter Manual Temprature", Toast.LENGTH_SHORT).show();
                }
            }
        });

        this.getTemp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
        });
    }

    private void offBluetooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
    }

    private void connectToDevice() {
        Log.e("auto_connect", "method working");
        if (ThermometerScreen.this.estadoBoton == "Connect") {
            ThermometerScreen.this.encenderBluetooth();
            Log.e(TAG,"creating_connection");
            new ThermometerScreen.Connect(ThermometerScreen.this, null).execute(new String[]{objBluetoothAddress.getString("hcthermometer","")});
            ThermometerScreen.this.enable = "false";
            return;
        }
    }

    private void sendCommand() {
        if (ThermometerScreen.this.estadoBoton == "Connect") {
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

    private void encenderBluetooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }

    private void setStrings() {
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
        this.BluetoothEncendido = (String) getText(R.string.BluetoothEncendido);
//        this.enviar.setText(this.Enviar);
        this.datos = (EditText) findViewById(R.id.editText1);
        this.btn = findViewById(R.id.button1);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

    @Override
    public void onResume() {
        super.onResume();
        connectToDevice();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
