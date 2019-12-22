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
import android.speech.tts.TextToSpeech;
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

import com.abhaybmi.app.DashboardActivity;
import com.abhaybmi.app.OtpVerifyScreen;
import com.abhaybmi.app.R;
import com.abhaybmi.app.actofitheight.ActofitMainActivity;
import com.abhaybmi.app.entities.AndMedical_App_Global;
import com.abhaybmi.app.glucose.Activity_ScanList;
import com.abhaybmi.app.glucose.adapters.ScanList;
import com.abhaybmi.app.heightweight.Principal;
import com.abhaybmi.app.printer.esys.pridedemoapp.Act_Main;
import com.abhaybmi.app.thermometer.ThermometerScreen;
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
import java.util.Locale;
import java.util.Set;

import ticker.views.com.ticker.widgets.circular.timer.callbacks.CircularViewCallback;
import ticker.views.com.ticker.widgets.circular.timer.view.CircularView;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

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
    private String readBatchCode = "U402"; //Reading batch code from the device
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
    private TextToSpeech tts;
    private String txt = "";
    private CircularView circularViewWithTimer;

    private TextView txtHeight,txtWeight,txtTemprature,txtOximeter,txtBpMonitor,txtSugar;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_hemoglobin);

        context = MainActivity.this;

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

        // Initialization of the top boxes

        txtHeight = findViewById(R.id.txtmainheight);
        txtWeight = findViewById(R.id.txtmainweight);
        txtTemprature = findViewById(R.id.txtmaintempreture);
        txtOximeter = findViewById(R.id.txtmainpulseoximeter);
        txtBpMonitor = findViewById(R.id.txtmainbloodpressure);
        txtSugar = findViewById(R.id.txtmainbloodsugar);

        tts = new TextToSpeech(this,this);

        setCounter();

        bindEvents();

        txt = "Please Click on On Sensor Button";
        speakOut(txt);

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
            startTest.setEnabled(false);
            txt = "Please click on start test Button";
            speakOut(txt);
            byte[] batchData = readBatchCode.getBytes();
            usbService.write(batchData,1);
            Toast.makeText(getApplicationContext(), "getting the batch code.", Toast.LENGTH_SHORT).show();
            startTest.setEnabled(true);
        });
        offSensorbtn.setOnClickListener((View v) -> {

            if(txthemoglobin.length() > 0 && (stripIV.getVisibility() == View.VISIBLE)) {
//                pd = Tools.kHudDialog(MainActivity.this);
                txt = "Please wait we are calculating your result";
                speakOut(txt);

                //show the new progress dialog with timer in hemoglobin while calculating your resuult
                circularViewWithTimer.setVisibility(View.VISIBLE);
                setCounter();
                startSensorbtn.setEnabled(false);
                startTest.setEnabled(false);
                offSensorbtn.setEnabled(false);
                startNext.setEnabled(false);
                btnRepeat.setEnabled(false);
                circularViewWithTimer.startTimer();
            } else{
                Toast.makeText(MainActivity.this, "please follow the procedure", Toast.LENGTH_SHORT).show();
            }


        });
        startTest.setOnClickListener((View v) -> {
            byte[] data = StartTest.getBytes();
            usbService.write(data, 1);
            if (txthemoglobin.getText().toString().length() > 0) {
                setImage();
                txt = "Please Insert strip and Add Blood and click on Get Result Button";
                speakOut(txt);
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
                txthemoglobin.setText("");
                Toast.makeText(MainActivity.this, "Restart the test", Toast.LENGTH_SHORT).show();
            }
        });

        mHandler = new MyHandler(this, this);
    }

    private void bindEvents() {

        //bind click events for the top box
        txtHeight.setOnClickListener(this);
        txtWeight.setOnClickListener(this);
        txtTemprature.setOnClickListener(this);
        txtOximeter.setOnClickListener(this);
        txtBpMonitor.setOnClickListener(this);
        txtSugar.setOnClickListener(this);

    }

    private void setCounter() {
        circularViewWithTimer = findViewById(R.id.circular_view);
        CircularView.OptionsBuilder builderWithTimer = new
                CircularView.OptionsBuilder()
                .shouldDisplayText(true)
                .setCounterInSeconds(60)
                .setCircularViewCallback(new CircularViewCallback() {
                    @Override
                    public void onTimerFinish() {
                        circularViewWithTimer.stopTimer();
                        startSensorbtn.setEnabled(true);
                        startTest.setEnabled(true);
                        offSensorbtn.setEnabled(true);
                        startNext.setEnabled(true);
                        btnRepeat.setEnabled(true);
                        circularViewWithTimer.setVisibility(View.GONE);
//                        Toast.makeText(MainActivity.this, "CircularCallback: Timer Finished ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onTimerCancelled() {
                        Toast.makeText(MainActivity.this, "CircularCallback: Timer Cancelled ", Toast.LENGTH_SHORT).show();
                    }
                });
        circularViewWithTimer.setOptions(builderWithTimer);
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
        Log.e("onResume","service restarted");
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it

        //recreation of tts object
        tts = new TextToSpeech(this,this);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "service unbinded");
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);

        //close the connection of the tts object
        closeTtsConnection();
    }

    private void closeTtsConnection() {
       tts.shutdown();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Log.e("onDestroy", "service unbinded");
            unregisterReceiver(mUsbReceiver);
            unbindService(usbConnection);
        }catch (Exception e){
            e.printStackTrace();
        }
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

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            tts.setSpeechRate(1);

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

    @Override
    public void onBackPressed() {

//        super.onBackPressed();
        //Disable back button
    }

    @Override
    public void onClick(View view) {

        //click event listener
        switch (view.getId()){

            case R.id.txtmainheight:
                context.startActivity(new Intent(this, Principal.class));
                break;

            case R.id.txtmainweight:
                context.startActivity(new Intent(this, ActofitMainActivity.class));
                break;

            case R.id.txtmaintempreture:
                context.startActivity(new Intent(this, ThermometerScreen.class));
                break;

            case R.id.txtmainpulseoximeter:
                context.startActivity(new Intent(this, com.abhaybmi.app.oximeter.MainActivity.class));
                break;

            case R.id.txtmainbloodpressure:
                context.startActivity(new Intent(this, DashboardActivity.class));
                break;

            case R.id.txtmainbloodsugar:
                context.startActivity(new Intent(this,Activity_ScanList.class));
                break;
        }

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
                    Log.e("msg_wht"," = "+msg.what);
                    if (mActivity.get().circularViewWithTimer != null ) {
                        mActivity.get().circularViewWithTimer.stopTimer();
                        mActivity.get().circularViewWithTimer.setVisibility(View.GONE);
                        enableButtons();
                    }

                    String buffer = (String) msg.obj;

                    Log.e("msg_arg1"," = "+msg.arg1);

                    if (msg.arg1 == 1) {
                        try {

                            String decodedString = URLDecoder.decode(buffer, "UTF-8");
                            String s = mActivity.get().txthemoglobin.getText().toString();
                            s = s + decodedString;
                            mActivity.get().txthemoglobin.setText(s);
//                            Toast.makeText(context, "data is "+s, Toast.LENGTH_SHORT).show();
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
                            Log.e("Exception ","check");
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }

        private void enableButtons() {

            mActivity.get().startSensorbtn.setEnabled(true);
            mActivity.get().startTest.setEnabled(true);
            mActivity.get().offSensorbtn.setEnabled(true);
            mActivity.get().startNext.setEnabled(true);
            mActivity.get().btnRepeat.setEnabled(true);

        }
    }

}
