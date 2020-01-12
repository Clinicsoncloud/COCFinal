package com.abhaybmicoc.app.actofit;

import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Switch;
import android.content.Intent;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.app.DatePickerDialog;
import android.annotation.SuppressLint;
import android.speech.tts.TextToSpeech;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.screen.DisplayRecordScreen;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;

import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ActofitMainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {
    private Context context = ActofitMainActivity.this;

    public SimpleDateFormat EEEddMMMyyyyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    private EditText edtHeight;
    private EditText edtUserId;
    private EditText edtUserName;

    private Switch switchAthlete;
    private RadioGroup radioGroupGender;
    private RadioButton radioMale;
    private RadioButton radioFemale;

    android.support.v7.app.ActionBar actionBar;

    private int day, month, year;

    private TextToSpeech tts;

    private SharedPreferences sharedPreferencesPersonal;
    private SharedPreferences sharedPreferencesActofit;

    private Button btnNext;
    private Button btnRepeat;
    private Button btnSmartScale;

    private TextView txtAge;
    private TextView txtName;
    private TextView txtGender;
    private TextView txtMobile;
    private TextView txtHeight;
    private TextView edtUserDOB;

    public static final int REQUSET_CODE = 1001;

    private String gen;
    private String txt = "";
    String KEY_ERROR = "error";
    String KEY_MESSAGE = "message";
    public static final String TAG = "MainActivity";

    boolean isAthlete;

    /*
    RESULT_CANCELED = 101;
    RESULT_CANCELED = 102;
    RESULT_CANCELED = 100;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
    }

    private void getdata(Intent intent) {
        float weight = intent.getFloatExtra("weight", 0f);
        float bmi = intent.getFloatExtra("bmi", 0f);
        float bodyfat = intent.getFloatExtra("bodyfat", 0f);
        float fatfreeweight = intent.getFloatExtra("fatfreeweight", 0f);
        float physique = intent.getFloatExtra("physique", 0f);
        float subfat = intent.getFloatExtra("subfat", 0f);
        float visfat = intent.getFloatExtra("visfat", 0f);
        float bodywater = intent.getFloatExtra("bodywater", 0f);
        float skemus = intent.getFloatExtra("skemus", 0f);
        float musmass = intent.getFloatExtra("musmass", 0f);
        float bonemass = intent.getFloatExtra("bonemass", 0f);
        float protine = intent.getFloatExtra("protine", 0f);
        float bmr = intent.getFloatExtra("bmr", 0f);
        float metaage = intent.getFloatExtra("metaage", 0f);
        float helthscore = intent.getFloatExtra("helthscore", 0f);

        try {
            sharedPreferencesActofit = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferencesActofit.edit();

            editor.putString("weight", String.valueOf(weight));
            editor.putString("height", getIntent().getStringExtra("height"));
            editor.putString("bmi", String.valueOf(bmi));
            editor.putString("bodyfat", String.valueOf(bodyfat));
            editor.putString("fatfreeweight", String.valueOf(fatfreeweight));
            editor.putString("physique", String.valueOf(physique));
            editor.putString("visfat", String.valueOf(visfat));
            editor.putString("bodywater", String.valueOf(bodywater));
            editor.putString("musmass", String.valueOf(musmass));
            editor.putString("bonemass", String.valueOf(bonemass));
            editor.putString("protine", String.valueOf(protine));
            editor.putString("bmr", String.valueOf(bmr));
            editor.putString("subfat", String.valueOf(subfat));
            editor.putString("skemus", String.valueOf(skemus));
            editor.putString("helthscore", String.valueOf(helthscore));
            editor.commit();

        } catch (Exception e) {

        }

        Log.d(TAG, "getdata: bonemass: " + bonemass + " bmr: " + bmr);
        Intent intent1 = new Intent(ActofitMainActivity.this, DisplayRecordScreen.class);

        intent1.putExtra("weight", weight);
        intent1.putExtra("bmi", bmi);
        intent1.putExtra("bodyfat", bodyfat);
        intent1.putExtra("fatfreeweight", fatfreeweight);
        intent1.putExtra("physique", physique);
        intent1.putExtra("subfat", subfat);
        intent1.putExtra("visfat", visfat);
        intent1.putExtra("bodywater", bodywater);
        intent1.putExtra("skemus", skemus);
        intent1.putExtra("musmass", musmass);
        intent1.putExtra("bonemass", bonemass);
        intent1.putExtra("protine", protine);
        intent1.putExtra("bmr", bmr);
        intent1.putExtra("metaage", metaage);
        intent1.putExtra("helthscore", helthscore);
        startActivity(intent1);
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopTextToSpeech();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode + "  " + resultCode);
        if (requestCode == REQUSET_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d(TAG, "onActivityResult IF : ohkkk");
                getdata(data);
            }
        } else if (requestCode == REQUSET_CODE && resultCode == RESULT_CANCELED) {
            Toast.makeText(ActofitMainActivity.this, "Cancelled!!!", Toast.LENGTH_SHORT).show();
        } else if (requestCode == RESULT_CANCELED) {
            Toast.makeText(ActofitMainActivity.this, "Your Subscription has Expired!!!", Toast.LENGTH_SHORT).show();
        }
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
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onClick(View view) {
        //click Listener
        switch (view.getId()) {
            case R.id.txtmainheight:
                context.startActivity(new Intent(this, HeightActivity.class));
                break;
        }
    }

    /**
     *
     */
    private void setupUI(){
        setContentView(R.layout.actofit_main_activity);

        tts = new TextToSpeech(getApplicationContext(), this);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Weight Measurement");

        edtUserId = findViewById(R.id.txtuid);
        edtUserDOB = findViewById(R.id.txt_sdob);
        edtUserName = findViewById(R.id.txtuname);
        edtHeight = findViewById(R.id.txtuheight);

        radioMale = findViewById(R.id.radioMale);
        radioGroupGender = findViewById(R.id.rdogrp);
        radioFemale = findViewById(R.id.radioFemale);

        switchAthlete = findViewById(R.id.switchbtn);

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);

        txtHeight = findViewById(R.id.txtmainheight);

        btnNext = findViewById(R.id.btnnext);
        btnSmartScale = findViewById(R.id.btn_smart_scale);
        btnRepeat = findViewById(R.id.btnrepeat);
    }

    /**
     *
     */
    private void setupEvents(){
        btnNext.setOnClickListener(view -> goNext());

        edtUserDOB.setOnClickListener(view -> editUserDateOfBirth());

        switchAthlete.setOnCheckedChangeListener((compoundButton, isChecked) -> updateIsAthlete(isChecked));

        btnSmartScale.setOnClickListener(view -> startSmartScale());
    }

    private void startSmartScale(){
        boolean appInstalled = isAppInstalled("com.actofitSmartScale");

        if (appInstalled) {
            Intent intent = new Intent();

            String customAction = "com.actofit.share.smartscale";
            intent.setAction(customAction);

            @SuppressLint("SimpleDateFormat") Date initDate = null;
            try {
                initDate = new SimpleDateFormat("yyyy-MM-dd").parse(getIntent().getStringExtra("dob"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String parsedDate = formatter.format(initDate);

            String height = getIntent().getStringExtra("height");

            intent.putExtra("id", sharedPreferencesPersonal.getString("id", ""));
            intent.putExtra("name", sharedPreferencesPersonal.getString("name", ""));
            intent.putExtra("gender", sharedPreferencesPersonal.getString("gender", ""));
            intent.putExtra("dob", parsedDate);
            intent.putExtra("height", Integer.parseInt(height));
            intent.putExtra("isAthlete", isAthlete);

            intent.setType("text/plain");
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivityForResult(intent, REQUSET_CODE);

            txtName.setText("Name : " + sharedPreferencesPersonal.getString("name", ""));
            txtGender.setText("Gender : " + sharedPreferencesPersonal.getString("gender", ""));
            txtMobile.setText("Phone : " + sharedPreferencesPersonal.getString("mobile_number", ""));
            txtAge.setText("DOB : " + sharedPreferencesPersonal.getString("dob", ""));
        }
    }

    /**
     *
     */
    private void initializeData(){
        txt = "Please Click on GoTo SmartScale, and stand on weight Scale";
        speakOut(txt);

        try {
            sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

            txtName.setText("Name : " + sharedPreferencesPersonal.getString("name", ""));
            txtGender.setText("Gender : " + sharedPreferencesPersonal.getString("gender", ""));
            txtMobile.setText("Phone : " + sharedPreferencesPersonal.getString("mobile_number", ""));
            txtAge.setText("DOB : " + sharedPreferencesPersonal.getString("dob", ""));
        } catch (Exception e) {

        }
    }

    /**
     *
     */
    private void goNext(){
        Intent objIntent = new Intent(getApplicationContext(), ThermometerScreen.class);
        startActivity(objIntent);
        finish();
    }

    /**
     *
     */
    private void editUserDateOfBirth(){
        final Calendar c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = (datePicker, year, month, day) ->{
                Calendar calander = Calendar.getInstance();
                calander.setTimeInMillis(0);
                calander.set(year, month, day, 0, 0, 0);

                Date selectedDate = calander.getTime();
                String dateFormatUS = EEEddMMMyyyyFormat.format(selectedDate);
                edtUserDOB.setText(dateFormatUS);
        };

        DatePickerDialog dpDialog = new DatePickerDialog(ActofitMainActivity.this, listener, year, month + 1, day);
        dpDialog.getDatePicker().setMaxDate(Long.parseLong("1141038198000"));
        dpDialog.show();
    }

    /**
     *
     * @param isAthlete
     */
    private void updateIsAthlete(boolean isAthlete){
        this.isAthlete = isAthlete;
    }

    /**
     *
     */
    private void stopTextToSpeech(){
        /* close the tts engine to avoide the runtime exception from it */
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }
    }

    /**
     *
     * @param uri
     * @return
     */
    private boolean isAppInstalled(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
