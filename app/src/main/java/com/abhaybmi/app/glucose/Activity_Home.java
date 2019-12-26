package com.abhaybmi.app.glucose;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmi.app.DashboardActivity;
import com.abhaybmi.app.PrintPreviewActivity;
import com.abhaybmi.app.R;
import com.abhaybmi.app.actofitheight.ActofitMainActivity;
import com.abhaybmi.app.glucose.adapters.ReadingAdapter;
import com.abhaybmi.app.heightweight.Principal;
import com.abhaybmi.app.hemoglobin.MainActivity;
import com.abhaybmi.app.printer.esys.pridedemoapp.Act_Main;
import com.abhaybmi.app.printer.esys.pridedemoapp.PrintPriviewScreen;
import com.abhaybmi.app.thermometer.ThermometerScreen;
import com.abhaybmi.app.utils.ApiUtils;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.maniteja.com.synclib.helper.Communicator;
import org.maniteja.com.synclib.helper.HelperC;
import org.maniteja.com.synclib.helper.SerializeUUID;
import org.maniteja.com.synclib.helper.SyncLib;
import org.maniteja.com.synclib.helper.Util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Activity_Home extends AppCompatActivity implements Communicator, TextToSpeech.OnInitListener, View.OnClickListener {

    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    Util util;
    private String mDeviceAddress;
    Animation animation;

    ActionBar mActionBar;

    //Action bar
    ImageView batteryIcon;
    ImageView bluetoothIcon;
    TextView connectionLabel;

    ImageView ivSteps;
    LinearLayout llGlucose;
    TextView resultText;

    View mView;

    SharedPreferences glucoseData;

    RadioGroup rgGlucose;
    RadioButton radioButtonId, rbFasting, rbPostMeal, rbRandom;
    int selectedId;


    public static boolean mConnected = false;
    public static boolean devTestStarted;

    Button startTest, restartTest, writeData, getData;
    TextView logDisplay, offlineTitle;
    RecyclerView readingrecycler;

    Activity_Home activity_home;
    InputStream ins;
    SerializeUUID serializeUUID;
    private TextView txtName, txtAge, txtGender, txtMobile;
    SharedPreferences shared;
    SyncLib syncLib;

    String resultOfGlucose;

    ImageView ivGlucose;

    Communicator communicator = this;
    private TextToSpeech tts;
    private String txt = "";

    private TextView txtHeight,txtWeight,txtTemprature,txtOximeter,txtBpMonitor;

    Context context;
    private TextView resultTextNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = Activity_Home.this;

        util = new Util(this, this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
        toolbar.setContentInsetsAbsolute(0, 0);
        setSupportActionBar(toolbar);

        toolbar.setOnTouchListener((v, event) -> {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception crap) {
                crap.printStackTrace();

            }
            return false;
        });

        ins = getResources().openRawResource(R.raw.synclibserialize);

        serializeUUID = new SerializeUUID();
        serializeUUID.readFile(ins);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater mInflater = LayoutInflater.from(this);

        ivSteps = (ImageView) findViewById(R.id.iv_steps);

        ivGlucose = (ImageView) findViewById(R.id.iv_glucose);

        llGlucose = (LinearLayout) findViewById(R.id.ll_glucose);

        resultText = llGlucose.findViewById(R.id.tv_resultText);

        resultTextNew = llGlucose.findViewById(R.id.tv_resultNew);

        rgGlucose = llGlucose.findViewById(R.id.rg_glucose);
        rbFasting = llGlucose.findViewById(R.id.rb_fasting);
        rbPostMeal = llGlucose.findViewById(R.id.rb_post);
        rbRandom = llGlucose.findViewById(R.id.rb_random);

        //Initialization of all top box elements
        txtHeight = findViewById(R.id.txtmainheight);
        txtWeight = findViewById(R.id.txtmainweight);
        txtTemprature = findViewById(R.id.txtmaintempreture);
        txtOximeter = findViewById(R.id.txtmainpulseoximeter);
        txtBpMonitor = findViewById(R.id.txtmainbloodpressure);

        bindEvents();

        View mCustomView = mInflater.inflate(R.layout.custom_action_bar, null);

        ImageView menuIcon = (ImageView) mCustomView.findViewById(R.id.menuIcon);
        menuIcon.setVisibility(View.GONE);

        connectionLabel = (TextView) mCustomView.findViewById(R.id.connectionLabel);

        batteryIcon = (ImageView) mCustomView.findViewById(R.id.batteryIcon);
        bluetoothIcon = (ImageView) mCustomView.findViewById(R.id.bluetoothIcon);
        shared = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        //String channel = (shared.getString(keyChannel, ""));

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);
        mView = findViewById(R.id.custView);
        tts = new TextToSpeech(this,this);

        txt = "Please click on start Test";
        speakOut(txt);

        txtName.setText("Name : " + shared.getString("name", ""));
        txtGender.setText("Gender : " + shared.getString("gender", ""));
        txtMobile.setText("Phone : " + shared.getString("mobile_number", ""));
        txtAge.setText("DOB : " + shared.getString("dob", ""));

        bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncLib.setmDeviceAddress(mDeviceAddress);
                syncLib.connectOrDisconnect();
            }
        });

        mActionBar.setCustomView(mCustomView);

        activity_home = Activity_Home.this;

        //communicator = (Communicator) activity_home;

        startTest = (Button) findViewById(R.id.startTest);
        startTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnected) {
                    syncLib.startTest();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Connect to Device!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        restartTest = (Button) findViewById(R.id.restartTest);
        restartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnected) {
                    if (devTestStarted) {
                        try {
                            syncLib.stopTest();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Test Not Started!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Connect to Device!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        writeData = (Button) findViewById(R.id.writeData);
        writeData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (mConnected)
                {
                    IntentIntegrator integrator = new IntentIntegrator(activity_home);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                    integrator.setPrompt("scan");
                    integrator.setCameraId(0);

                    integrator.setBeepEnabled(true);
                    integrator.setBarcodeImageEnabled(true);
                    integrator.initiateScan();
                    integrator.setOrientationLocked(false);
                } else
                {
                    Toast.makeText(getApplicationContext(), "Please Connect to Device!", Toast.LENGTH_SHORT).show();
                }
            */
                if (mConnected) {
                    syncLib.notifyGetData();
                }
                if (rgGlucose.getCheckedRadioButtonId() == -1) {
                    // no radio buttons are checked
                    Toast.makeText(context, "Please select any one type", Toast.LENGTH_SHORT).show();
                }
                else {
                    // one of the radio buttons is checked
                    Intent objprint = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(objprint);
                    finish();
                }
            }
        });

        getData = (Button) findViewById(R.id.getData);
        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConnected) {
                    syncLib.notifyGetData();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Connect to Device!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logDisplay = (TextView) findViewById(R.id.logDisplay);
        offlineTitle = (TextView) findViewById(R.id.offlineTitle);

        readingrecycler = (RecyclerView) findViewById(R.id.readingrecycler);

        final Intent intent = getIntent();
        if (intent.getStringExtra(EXTRAS_DEVICE_ADDRESS) != null) {
            mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
            util.putString(HelperC.key_mybluetoothaddress, mDeviceAddress);
        } else {
            mDeviceAddress = util.readString(HelperC.key_autoconnectaddress, "");
        }

        syncLib = new SyncLib(communicator, this, Activity_Home.this, serializeUUID, mDeviceAddress);

        util.print("Scan List Address :Main " + mDeviceAddress + "::" + util.readString(HelperC.key_mybluetoothaddress, "") + " - " + mDeviceAddress.length());
    }

    private void bindEvents() {

        //bind click events to top boxes

        txtHeight.setOnClickListener(this);
        txtWeight.setOnClickListener(this);
        txtTemprature.setOnClickListener(this);
        txtOximeter.setOnClickListener(this);
        txtBpMonitor.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("device id");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result == null) {
                Toast.makeText(this, getResources().getString(R.string.canceled), Toast.LENGTH_LONG).show();
            } else {
                if (result.getContents() != null) {
                    syncLib.writeCalibData(result.getContents());
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tts = new TextToSpeech(this,this);
        if (!mConnected) {
            syncLib.startReceiver();
        }else if(mConnected){
            syncLib.startReceiver();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        syncLib.stopReceiver();
    }


    @Override
    public void onBackPressed() {
        setSwitchActivity();
    }

    @Override
    public boolean go(String text) {

        //set the log of the go text
        logDisplay.setText(text);

        //check the conditio of the go text if there is go then send voice command to user to click on the start test button
        if(text.equals("go")){
            txt = "Click on start Test";
            speakOut(txt);
        }

        //already existed the return statement of the boolean method
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        //close the text to speach object to avoid run time exception
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
    public void setLog(String text) {
        Log.e("text_title", " : " + text);
        logDisplay.setText(text);

        if (text.equals("Insert Strip!")) {

            readingrecycler.setVisibility(View.GONE);
            ivSteps.setVisibility(View.VISIBLE);
            ivGlucose.setVisibility(View.VISIBLE);
            txt = "Please Insert Strip";
            speakOut(txt);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivSteps.setImageDrawable(getDrawable(R.drawable.insertstrip));
            }

        } else if (text.equals("Add Blood")) {

            readingrecycler.setVisibility(View.GONE);
            ivSteps.setVisibility(View.VISIBLE);
            ivGlucose.setVisibility(View.VISIBLE);
            txt = "Please Add Blood";
            speakOut(txt);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivSteps.setImageDrawable(getDrawable(R.drawable.addblood));
            }

        } else if (text.equals("Used Strip\n" + "Please remove the used strip and insert a new strip")) {

            readingrecycler.setVisibility(View.GONE);
            ivSteps.setVisibility(View.VISIBLE);
            ivGlucose.setVisibility(View.VISIBLE);
            txt = "Please insert the new strip";
            speakOut(txt);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivSteps.setImageDrawable(getDrawable(R.drawable.insertstrip));
            }


        } else {


            if (text.contains("Result")) {

                logDisplay.setVisibility(View.VISIBLE);
                resultTextNew.setVisibility(View.GONE);
                resultText.setVisibility(View.VISIBLE);

                logDisplay.setText("Blood Glucose Result is");

                text = text.replace("Result is", "");
                text = text.replace(" ", "");


                resultOfGlucose = text;

                Log.e("text_result",""+text);

                Log.e("result_sugar",""+resultOfGlucose);

                resultText.setText(text);

                resultText.setVisibility(View.GONE);
                resultTextNew.setVisibility(View.VISIBLE);

                resultTextNew.setText(resultOfGlucose);


                Log.e("result_after_setText",""+resultOfGlucose);

                readingrecycler.setVisibility(View.GONE);
                ivSteps.setVisibility(View.GONE);
                ivGlucose.setVisibility(View.GONE);
                llGlucose.setVisibility(View.VISIBLE);
                mView.setVisibility(View.GONE);
                llGlucose.setGravity(Gravity.CENTER);

                rgGlucose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {

                        selectedId = rgGlucose.getCheckedRadioButtonId();
                        radioButtonId = findViewById(selectedId);

                        glucoseData = getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
                        SharedPreferences.Editor editor = glucoseData.edit();
                        editor.putString("glucosetype", radioButtonId.getText().toString());
                        editor.putString("last", resultTextNew.getText().toString());

                        Log.e("glucosetype", "" + radioButtonId.getText().toString());
                        Log.e("result", "" + resultTextNew.getText().toString());

                        editor.commit();

                    }
                });

            }
            logDisplay.setVisibility(View.GONE);
            resultText.setText(text);
            readingrecycler.setVisibility(View.GONE);
            ivSteps.setVisibility(View.GONE);
            ivGlucose.setVisibility(View.GONE);
            llGlucose.setVisibility(View.VISIBLE);
            llGlucose.setGravity(Gravity.CENTER);
        }
    }

    @Override
    public void testStarted(boolean testStarted) {
        devTestStarted = testStarted;
    }

    @Override
    public void stopNotiFication() {

    }

    @Override
    public void setConnectionStatus(String s, boolean connectionStatus) {
        mConnected = connectionStatus;
        if (mConnected) {
            bluetoothIcon.setBackgroundResource(R.drawable.connect);
            batteryIcon.setVisibility(View.VISIBLE);
        } else {
            batteryIcon.setVisibility(View.INVISIBLE);
            bluetoothIcon.setBackgroundResource(R.drawable.disconnect);
        }
    }

    @Override
    public void setSwitchActivity() {
        Intent intent = new Intent(getApplicationContext(), Activity_ScanList.class);
        intent.putExtra("flag", 2);
        startActivity(intent);
        finish();
    }


    @Override
    public void setBatteryLevel(int value) {
        if (value > 25 && value < 33) {
            if (animation != null)
                animation.cancel();
            batteryIcon.setBackgroundResource(R.drawable.battery1);
        } else if (value >= 33 && value < 50) {
            if (animation != null)
                animation.cancel();
            batteryIcon.setBackgroundResource(R.drawable.battery2);
        } else if (value >= 50) {
            if (animation != null)
                animation.cancel();
            batteryIcon.setBackgroundResource(R.drawable.battery3);
        } else if (value > 0 && value <= 25) {
            batteryIcon.setBackgroundResource(R.drawable.battery0);
            animation = new AlphaAnimation(1, 0);
            animation.setDuration(400);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(Animation.INFINITE);
            animation.setRepeatMode(Animation.REVERSE);
            batteryIcon.startAnimation(animation);
        }
    }

    @Override
    public void setManufacturerName(String s) {
        //Manufaturer Name
    }

    @Override
    public void setSerialNumber(String s) {
        //Serial Number
    }

    @Override
    public void setModelNumber(String s) {
        //Manufacture Date
    }

    @Override
    public void getOfflineResults(ArrayList<String> arrayList) {
        if (arrayList.size() > 0) {
            Collections.reverse(arrayList);
            offlineTitle.setText("Offline Results");
            ReadingAdapter readingAdapter = new ReadingAdapter(arrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            readingrecycler.setLayoutManager(mLayoutManager);
            readingrecycler.setItemAnimator(new DefaultItemAnimator());
            readingrecycler.setAdapter(readingAdapter);
        }
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
//        String text = "StartActivity me aapka swagat hain kripaya next button click kre aur aage badhe";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onClick(View view) {
        //click event listener for top boxes
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
        }

    }
}
