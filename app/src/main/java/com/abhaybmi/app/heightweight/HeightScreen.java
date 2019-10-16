package com.abhaybmi.app.heightweight;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmi.app.ActivateScreen;
import com.abhaybmi.app.R;
import com.abhaybmi.app.actofitheight.ActofitMainActivity;
import com.abhaybmi.app.utils.ApiUtils;

import java.lang.ref.WeakReference;
import java.util.Set;

import io.fabric.sdk.android.services.concurrency.Task;

public class HeightScreen extends AppCompatActivity implements View.OnClickListener {

    private Button btnnext;
    private EditText etManualheight;
    private TextView txtheight;

    SharedPreferences shared;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case com.abhaybmi.app.heightweight.HeightUsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case com.abhaybmi.app.heightweight.HeightUsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case com.abhaybmi.app.heightweight.HeightUsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case com.abhaybmi.app.heightweight.HeightUsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case com.abhaybmi.app.heightweight.HeightUsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
                default: {
                    //com.abhaybmi.app.heightweight.HeightUsbService.ACTION_USB_DEVICE_NO_RESULT_SEND: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB Device has Result not sent", Toast.LENGTH_SHORT).show();
                    //break;
                }
            }
        }
    };
    private HeightUsbService HeightUsbService;
    private TextView display;
    private CheckBox box9600, box38400;
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            mHandler = new MyHandler(HeightScreen.this);
            HeightUsbService = ((HeightUsbService.UsbBinder) arg1).getService();
            HeightUsbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d("==Disconnected==", arg0.getClassName());
            Log.d("==Disconnected==", "Closed srvice");
            HeightUsbService = null;
            HeightUsbService.setHandler(mHandler);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_height_screen);
        init();
    }

    public void init() {
        btnnext = findViewById(R.id.btnnext);
        //  etManualheight = findViewById(R.id.etManualheight);
        txtheight = findViewById(R.id.txtheight);
        btnnext.setOnClickListener(this);
        shared = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        //String channel = (shared.getString(keyChannel, ""));

        /*txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);

        txtName.setText("Name : " + shared.getString("name", ""));
        txtGender.setText("Gender : " + shared.getString("gender", ""));
        txtMobile.setText("Phone : " + shared.getString("mobile_number", ""));
        txtAge.setText("DOB : " + shared.getString("dob", ""));*/


        display = findViewById(R.id.txtheight);
        Button sendButton = findViewById(R.id.btnstart);
        sendButton.setOnClickListener(v -> {
            etManualheight.setText("");
            String data = "1";
            if (HeightUsbService != null) { // if UsbSerwevice was correctly binded, Send data
                HeightUsbService.write(data.getBytes());
            }
        });

        box9600 = findViewById(R.id.checkBox);
        box9600.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (box9600.isChecked())
                box38400.setChecked(false);
            else
                box38400.setChecked(true);
        });

        box38400 = findViewById(R.id.checkBox2);
        box38400.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (box38400.isChecked())
                box9600.setChecked(false);
            else
                box9600.setChecked(true);
        });

        Button baudrateButton = findViewById(R.id.buttonBaudrate);
        baudrateButton.setOnClickListener(v -> {
            if (box9600.isChecked())
                HeightUsbService.changeBaudRate(9600);
            else
                HeightUsbService.changeBaudRate(38400);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.removeCallbacks(null);
            Toast.makeText(HeightScreen.this, "Service Un-Binded", Toast.LENGTH_LONG).show();
            getApplicationContext().unbindService(usbConnection);
            stopService(new Intent(this, HeightUsbService.class));
        } catch (Exception e) {
        }
    }

    public void stop(Context context) {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacks(null);
        context.unbindService(usbConnection);
        stopService(new Intent(this, HeightUsbService.class));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnnext) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        stopService(new Intent(HeightScreen.this, HeightUsbService.class));
                        unregisterReceiver(mUsbReceiver);
                        unbindService(usbConnection);
//                        mUsbReceiver.abortBroadcast();

                    } catch (Exception e) {
                        Log.d("===Data Log===", "=====" + e);
                    }

                    if (etManualheight.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Please enter Manual Height", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Go to Next screen", Toast.LENGTH_SHORT).show();
                        try {
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.removeCallbacks(null);
                            getApplicationContext().unbindService(usbConnection);
                        } catch (Exception e) {

                        }
                        Intent objIntent = new Intent(getApplicationContext(), ActofitMainActivity.class);
                        objIntent.putExtra("id", shared.getString("id", ""));
                        objIntent.putExtra("name", shared.getString("name", ""));
                        objIntent.putExtra("gender", shared.getString("gender", ""));
                        objIntent.putExtra("dob", shared.getString("dob", ""));
                        objIntent.putExtra("height", etManualheight.getText().toString());
                        SharedPreferences objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
                        SharedPreferences.Editor editor = objdoctor.edit();
                        editor.putString("height", etManualheight.getText().toString());
                        editor.commit();

                        startActivity(objIntent);
                        finish();
                    }
                }
            });

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(HeightUsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!com.abhaybmi.app.heightweight.HeightUsbService.SERVICE_CONNECTED) {
            initService(service, extras);
        }
        Intent bindingIntent = new Intent(this, service);
        try {
            bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {

        }
    }

    private void initService(Class<?> service, Bundle extras) {
        Intent startService = new Intent(this, service);
        if (extras != null && !extras.isEmpty()) {
            Set<String> keys = extras.keySet();
            for (String key : keys) {
                String extra = extras.getString(key);
                startService.putExtra(key, extra);
            }
        }
        // startService(startService);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(com.abhaybmi.app.heightweight.HeightUsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(com.abhaybmi.app.heightweight.HeightUsbService.ACTION_NO_USB);
        filter.addAction(com.abhaybmi.app.heightweight.HeightUsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(com.abhaybmi.app.heightweight.HeightUsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(com.abhaybmi.app.heightweight.HeightUsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private class MyHandler extends Handler {
        //private final WeakReference<HeightScreen> mActivity;
        HeightScreen mActivity;

        public MyHandler(HeightScreen activity) {
            //mActivity = new WeakReference<>(activity);
            mActivity = activity;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case com.abhaybmi.app.heightweight.HeightUsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    mActivity.etManualheight.append(data);
                    Toast.makeText(mActivity, data, Toast.LENGTH_SHORT).show();
                    break;
                case com.abhaybmi.app.heightweight.HeightUsbService.CTS_CHANGE:
                    Toast.makeText(mActivity, "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case com.abhaybmi.app.heightweight.HeightUsbService.DSR_CHANGE:
                    Toast.makeText(mActivity, "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case com.abhaybmi.app.heightweight.HeightUsbService.SYNC_READ:
                    String buffer = (String) msg.obj;
                    // mActivity.etManualheight.append(buffer);
                    break;
            }
        }
    }
}
