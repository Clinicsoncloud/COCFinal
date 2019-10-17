package com.abhaybmi.app.hemoglobin;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmi.app.OtpVerifyScreen;
import com.abhaybmi.app.R;
import com.abhaybmi.app.entities.AndMedical_App_Global;
import com.abhaybmi.app.glucose.Activity_ScanList;
import com.abhaybmi.app.glucose.adapters.ScanList;
import com.abhaybmi.app.printer.esys.pridedemoapp.Act_Main;
import com.abhaybmi.app.utils.ApiUtils;
import com.abhaybmi.app.utils.Tools;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    KProgressHUD pd;
    private UsbService usbService;
    private TextView txthemoglobin;
    private ImageView stripIV;
    private TextView stripTV;
    private Button startSensorbtn, offSensorbtn, startTest, startNext,btnRepeat;
    private MyHandler mHandler;
    private String onDevice = "U371";
    private String offDevice = "U370";
    private String StartTest = "U401";
    private TextView txtName, txtAge, txtGender, txtMobile;
    SharedPreferences shared;

    SharedPreferences hemoglobinData;

    AVLoadingIndicatorView avi;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hemoglobin);

        txthemoglobin = findViewById(R.id.txthemoglobin);
        startSensorbtn = findViewById(R.id.startSensorbtn);
        offSensorbtn = findViewById(R.id.offSensorbtn);
        startTest = findViewById(R.id.startTest);
        startNext = findViewById(R.id.startNext);
        btnRepeat = findViewById(R.id.btn_repeat);
        shared = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        hemoglobinData = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
        //String channel = (shared.getString(keyChannel, ""));

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);
        stripIV = findViewById(R.id.iv_imageStrip);
        stripTV = findViewById(R.id.tv_insertStrip);

        txtName.setText("Name : " + shared.getString("name", ""));
        txtGender.setText("Gender : " + shared.getString("gender", ""));
        txtMobile.setText("Phone : " + shared.getString("mobile_number", ""));
        txtAge.setText("DOB : " + shared.getString("dob", ""));


        txthemoglobin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("TextVIEW", charSequence.toString());


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        startSensorbtn.setOnClickListener((View v) -> {
            byte[] data = onDevice.getBytes();
            usbService.write(data, 1);
        });
        offSensorbtn.setOnClickListener((View v) -> {

            if(txthemoglobin.length() > 0 && (stripIV.getVisibility() == View.VISIBLE))
              pd = Tools.kHudDialog(MainActivity.this);
            else
                Toast.makeText(MainActivity.this, "please follow the procedure", Toast.LENGTH_SHORT).show();

        });
        startTest.setOnClickListener((View v) -> {
            byte[] data = StartTest.getBytes();
            usbService.write(data, 1);
            if (txthemoglobin.getText().toString().length() > 0) {
                setImage();
            }
        });

        startNext.setOnClickListener(v -> {
            Intent objIntent = new Intent(getApplicationContext(), Act_Main.class);
            byte[] data = offDevice.getBytes();
            usbService.write(data, 1);
            try {
                AndMedical_App_Global.mBTcomm = null;
            } catch(NullPointerException e) { }
            startActivity(objIntent);
        });

        btnRepeat.setOnClickListener(v -> {

            if (txthemoglobin.getText().toString().length() > 0 && stripIV.getVisibility() == View.VISIBLE) {
                stripIV.setVisibility(View.GONE);
                stripTV.setVisibility(View.GONE);
                byte[] data = offDevice.getBytes();
                usbService.write(data, 1);
                Toast.makeText(MainActivity.this, "Restart the test", Toast.LENGTH_SHORT).show();
            }

        });

        mHandler = new MyHandler(this, this);
    }

    private void setImage() {
        stripIV.setImageResource(R.drawable.glocometer);
        stripIV.setVisibility(View.VISIBLE);
        stripTV.setVisibility(View.VISIBLE);
    }

    private void startTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                byte[] data = StartTest.getBytes();
                usbService.write(data, 1);
            }
        }, 2000);
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }


    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mUsbReceiver);
            unbindService(usbConnection);
        }catch (Exception e){}
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);

    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        Context context;

        public MyHandler(MainActivity activity, Context context) {
            mActivity = new WeakReference<>(activity);
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UsbService.SYNC_READ:

                    if (mActivity.get().pd != null && mActivity.get().pd.isShowing()) {
                        mActivity.get().pd.dismiss();
                    }

                    String buffer = (String) msg.obj;

                    if (msg.arg1 == 1) {
                        try {
                            String decodedString = URLDecoder.decode(buffer, "UTF-8");
                            String s = mActivity.get().txthemoglobin.getText().toString();
                            s = s + decodedString;
                            mActivity.get().txthemoglobin.setText(s);
                            if (s.length() > 0) {
//                                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                                if (s.toLowerCase().endsWith("g/dl")) {
                                    s = s.toLowerCase();
                                    s = s.substring(s.indexOf("hb:"),s.lastIndexOf("g"));
                                    s = s.replace(" ", "");
                                    s = s.replace("hb:", "");
                                    SharedPreferences.Editor editor = mActivity.get().hemoglobinData.edit();
                                    editor.putString("hemoglobin", s);
                                    editor.commit();
                                    mActivity.get().txthemoglobin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35f);
                                    mActivity.get().txthemoglobin.setText(s);

                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                    }
                    break;
            }
        }
    }


//    ������o������n������
//            ������
//            ������
//            ������h������i������
//            ������
//            ������r������e������a������d������y������ ������f������o������r������ ������b������l������e������
//            ������b������:������ ������2������3������3������ ������h������b������:������ ������5������.������8������ ������g������/������d������l������
//            ������

//    ������o������n������
//            ������
//            ������
//            ������h������i������
//            ������
//            ������r������e������a������d������y������ ������f������o������r������ ������b������l������e������
//            ������b������:������ ������2������3������3������ ������h������b������:������ ������7������.������2������ ������g������/������d������l������
//            ������

}
