package com.abhaybmi.app.oximeter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmi.app.BpLoginScreen;
import com.abhaybmi.app.DashboardActivity;
import com.abhaybmi.app.OtpLoginScreen;
import com.abhaybmi.app.R;
import com.abhaybmi.app.actofitheight.ActofitMainActivity;
import com.abhaybmi.app.glucose.Activity_ScanList;
import com.abhaybmi.app.heightweight.Principal;
import com.abhaybmi.app.thermometer.ThermometerScreen;
import com.abhaybmi.app.utils.ApiUtils;
import com.abhaybmi.app.utils.Tools;
import com.choicemmed.c208blelibrary.Device.C208Device;
import com.choicemmed.c208blelibrary.cmd.invoker.C208Invoker;
import com.choicemmed.c208blelibrary.cmd.listener.C208BindDeviceListener;
import com.choicemmed.c208blelibrary.cmd.listener.C208ConnectDeviceListener;
import com.choicemmed.c208blelibrary.cmd.listener.C208DisconnectCommandListener;
import com.choicemmed.c208blelibrary.utils.LogUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener,TextToSpeech.OnInitListener {
    private static final String TAG = "MainActivity";
    @ViewInject(R.id.spo)
    private TextView tv_spo;
    @ViewInject(R.id.pr)
    private TextView tv_pr;
    @ViewInject(R.id.bindDevice)
    private Button bindDevice;
    @ViewInject(R.id.connectDevice)
    private Button connectDevice;
    @ViewInject(R.id.disconnect)
    private Button disconnect;
    private C208Invoker c208Invoker;
    private static final int RECEIVE_SPO_PR = 1;
    private String macAddress = "";
    public static final String MAC_ADDRESS_KEY = "MAC_ADDRESS_KEY";
    private Button btnrepeat;
    private ProgressDialog pd;
    private SharedPreferences savedata, shared;
    private TextView txtName, txtAge, txtGender, txtMobile;
    private TextView txtHeight,txtWeight,txtTemprature;

    Context context;

    boolean flag = true;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RECEIVE_SPO_PR:
                    tv_pr.setText("Pulse rate: " + msg.arg2);
                    tv_spo.setText("Body Oxygen：" + msg.arg1);
                    savedata = getSharedPreferences(ApiUtils.PREFERENCE_PULSE, MODE_PRIVATE);
                    // Writing data to SharedPreferences
                    SharedPreferences.Editor editor = savedata.edit();
                    editor.putString("pulse_rate", String.valueOf(msg.arg2));
                    editor.putString("body_oxygen", String.valueOf(msg.arg1));
                    editor.commit();
                    break;
            }
        }
    };

    private BluetoothAdapter mBluetoothAdapter;
    private TextToSpeech tts;
    private String txt = "";

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //Disable back button
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //reinitialize the tts engine
        tts = new TextToSpeech(this,this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pulse_oximeter_main);
        ViewUtils.inject(this);
        macAddress = SharePreferenceUtil.get(this, MAC_ADDRESS_KEY, "").toString();
        bindDevice.setOnClickListener(this);
        connectDevice.setOnClickListener(this);
        disconnect.setOnClickListener(this);
        c208Invoker = new C208Invoker(this);

        context = MainActivity.this;

        tts = new TextToSpeech(this,this);

        txt = "Put Finger inside the Device and Click Start Test Button";
        speakOut(txt);

        this.setFinishOnTouchOutside(false);

        shared = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

//        enableBlutooth();

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);

        //Initialization of the top boxes

        txtHeight = findViewById(R.id.txtmainheight);
        txtWeight = findViewById(R.id.txtmainweight);
//        txtTemprature = findViewById(R.id.txtmaintempreture);

        //bind click events of the top boxes for previous test only
        bindEvents();

        txtName.setText("Name : " + shared.getString("name", ""));
        txtGender.setText("Gender : " + shared.getString("gender", ""));
        txtMobile.setText("Phone : " + shared.getString("mobile_number", ""));
        txtAge.setText("DOB : " + shared.getString("dob", ""));

        btnrepeat = findViewById(R.id.btnrepeat);
        btnrepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_spo.setText("spo");
                tv_pr.setText(R.string.pr);
            }
        });

        Button txtnext = findViewById(R.id.txtnext);
        Button btnskip = findViewById(R.id.btnskip);
        txtnext.setOnClickListener(v -> {
            Intent objIntent = new Intent(getApplicationContext(), ThermometerScreen.class);
            startActivity(objIntent);
            finish();
        });

        btnskip.setOnClickListener(v -> {
            Intent objIntent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(objIntent);
            finish();
        });
    }

    private void bindEvents() {
        //click evet
        txtHeight.setOnClickListener(this);
        txtWeight.setOnClickListener(this);

    }


    private void enableBlutooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bindDevice:/*绑定设备*/
                    pd = Tools.progressDialog(MainActivity.this);
                    c208Invoker.bindDevice(new C208BindDeviceListener() {
                        @Override
                        public void onDataResponse(int spo, int pr) {
                            LogUtils.d(TAG, "bindDevice---->" + "spo:" + spo + "pr:" + pr);
                            pd.dismiss();
                            flag = false;
                            Message message = new Message();
                            message.arg1 = spo;
                            message.arg2 = pr;
                            message.what = RECEIVE_SPO_PR;
                            handler.sendMessage(message);
                        }

                        @Override
                        public void onError(String message) {
                            LogUtils.d(TAG, "Bind device error message--->" + message);
                        }

                        @Override
                        public void onStateChanged(int oldState, int newState) {
                            LogUtils.d(TAG, "oldState:" + oldState + "---->newState:" + newState);
                        }

                        @Override
                        public void onBindDeviceSuccess(C208Device c208Device) {
                            LogUtils.d(TAG, "deviceInfo-->" + c208Device.toString());
                            macAddress = c208Device.getDeviceMacAddress();
                            SharePreferenceUtil.put(MainActivity.this, MAC_ADDRESS_KEY, macAddress);
                        }

                        @Override
                        public void onBindDeviceFail(String failMessage) {
                            LogUtils.d(TAG, "Bind device failure information--->" + failMessage);
                        }
                    });

//                    funHandler();
                break;
            case R.id.connectDevice:/*连接设备*/
                if ("".equals(macAddress)) {
                    LogUtils.d(TAG, "macAddress是空");
                    Toast.makeText(this, "Please bind the device first！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                C208Device device = new C208Device();
                device.setDeviceMacAddress(macAddress);
                c208Invoker.connectDevice(device, new C208ConnectDeviceListener() {
                    @Override
                    public void onDataResponse(int spo, int pr) {
                        LogUtils.d(TAG, "connectDevice---->" + "spo:" + spo + "pr:" + pr);
                        Message message = new Message();
                        message.arg1 = spo;
                        message.arg2 = pr;
                        message.what = RECEIVE_SPO_PR;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(String message) {
                        LogUtils.d(TAG, "Connection device error message--->" + message);
                    }

                    @Override
                    public void onStateChanged(int oldState, int newState) {
                        LogUtils.d(TAG, "oldState:" + oldState + "---->newState:" + newState);
                    }

                    @Override
                    public void onConnectedDeviceSuccess() {
                        LogUtils.d(TAG, "onConnectedDeviceSuccess");
                    }

                    @Override
                    public void onConnectedDeviceFail(String failMessage) {
                        LogUtils.d(TAG, "Connection device failure message--->" + failMessage);
                    }
                });
                break;
            case R.id.disconnect:/*断开设备连接*/
                c208Invoker.disconnectDevice(new C208DisconnectCommandListener() {
                    @Override
                    public void onDisconnected() {
                        LogUtils.d(TAG, "Disconnect device！！！");
                    }
                });
                break;

            case R.id.txtmainheight:
                // back to the height screen
                context.startActivity(new Intent(this, Principal.class));
                break;

            case R.id.txtmainweight:
                //back to the actofit screen
                context.startActivity(new Intent(this, ActofitMainActivity.class));
                break;

        }
    }

    private void funHandler() {

        /*  final Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            pd = Tools.progressDialog(MainActivity.this);
                            c208Invoker.bindDevice(new C208BindDeviceListener() {
                                @Override
                                public void onDataResponse(int spo, int pr) {
                                    LogUtils.d(TAG, "bindDevice---->" + "spo:" + spo + "pr:" + pr);
                                    pd.dismiss();
                                    flag = false;
                                    Message message = new Message();
                                    message.arg1 = spo;
                                    message.arg2 = pr;
                                    message.what = RECEIVE_SPO_PR;
                                    handler.sendMessage(message);
                                }
                                @Override
                                public void onError(String message) {
                                    LogUtils.d(TAG, "Bind device error message--->" + message);
                                }

                                @Override
                                public void onStateChanged(int oldState, int newState) {
                                    LogUtils.d(TAG, "oldState:" + oldState + "---->newState:" + newState);
                                }

                                @Override
                                public void onBindDeviceSuccess(C208Device c208Device) {
                                    LogUtils.d(TAG, "deviceInfo-->" + c208Device.toString());
                                    macAddress = c208Device.getDeviceMacAddress();
                                    SharePreferenceUtil.put(MainActivity.this, MAC_ADDRESS_KEY, macAddress);
                                }

                                @Override
                                public void onBindDeviceFail(String failMessage) {
                                    LogUtils.d(TAG, "Bind device failure information--->" + failMessage);
                                }
                            });

                        }
                    }, 100);*/
    }


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
}

