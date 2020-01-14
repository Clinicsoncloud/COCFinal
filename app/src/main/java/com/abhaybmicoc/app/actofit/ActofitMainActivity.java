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
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.activity.HeightActivity;
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

    private EditText etHeight;
    private EditText etUserId;
    private EditText etUserName;

    private Switch switchAthlete;

    private RadioButton radioMale;
    private RadioButton radioFemale;
    private RadioGroup radioGroupGender;

    android.support.v7.app.ActionBar actionBar;

    private int day, month, year;

    private TextToSpeech textToSpeech;

    private SharedPreferences sharedPreferencesActofit;
    private SharedPreferences sharedPreferencesPersonal;

    private Button btnNext;
    private Button btnRepeat;
    private Button btnSmartScale;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvMobile;
    private TextView tvHeight;
    private TextView etUserDateOfBirth;

    public static final int REQUSET_CODE = 1001;

    private String gen;
    private String txt = "";
    private String KEY_ERROR = "error";
    private String KEY_MESSAGE = "message";
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

    private void readAndStoreData(Intent intent) {
        // TODO: Check what data is returned here and read variables accordingly

        float bmr = intent.getFloatExtra("bmr", 0f);
        float bmi = intent.getFloatExtra("bmi", 0f);
        float weight = intent.getFloatExtra("weight", 0f);
        float subFat = intent.getFloatExtra("subFat", 0f);
        float protein = intent.getFloatExtra("protein", 0f);
        float bodyFat = intent.getFloatExtra("bodyFat", 0f);
        float metaAge = intent.getFloatExtra("metaAge", 0f);
        float boneMass = intent.getFloatExtra("boneMass", 0f);
        float physique = intent.getFloatExtra("physique", 0f);
        float bodyWater = intent.getFloatExtra("bodyWater", 0f);
        float muscleMass = intent.getFloatExtra("muscleMass", 0f);
        float visceralFat = intent.getFloatExtra("visceralFat", 0f);
        float healthScore = intent.getFloatExtra("healthScore", 0f);
        float fatFreeWeight = intent.getFloatExtra("fatFreeWeight", 0f);
        float skeletalMuscle = intent.getFloatExtra("skeletalMuscle", 0f);

        float height = Float.parseFloat(getIntent().getStringExtra("height"));

        try {
            sharedPreferencesActofit = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedPreferencesActofit.edit();

            editor.putString("bmr", String.valueOf(bmr));
            editor.putString("bmi", String.valueOf(bmi));
            editor.putString("height", String.valueOf(height));
            editor.putString("weight", String.valueOf(weight));
            editor.putString("subFat", String.valueOf(subFat));
            editor.putString("bodyFat", String.valueOf(bodyFat));
            editor.putString("protein", String.valueOf(protein));
            editor.putString("physique", String.valueOf(physique));
            editor.putString("boneMass", String.valueOf(boneMass));
            editor.putString("bodyWater", String.valueOf(bodyWater));
            editor.putString("muscleMass", String.valueOf(muscleMass));
            editor.putString("visceralFat", String.valueOf(visceralFat));
            editor.putString("healthScore", String.valueOf(healthScore));
            editor.putString("fatFreeWeight", String.valueOf(fatFreeWeight));
            editor.putString("skeletalMuscle", String.valueOf(skeletalMuscle));

            editor.commit();
        } catch (Exception e) {

        }

        Intent intent1 = new Intent(ActofitMainActivity.this, DisplayRecordScreen.class);

        intent1.putExtra("bmr", bmr);
        intent1.putExtra("bmi", bmi);
        intent1.putExtra("subFat", subFat);
        intent1.putExtra("weight", weight);
        intent1.putExtra("bodyFat", bodyFat);
        intent1.putExtra("protein", protein);
        intent1.putExtra("metaAge", metaAge);
        intent1.putExtra("physique", physique);
        intent1.putExtra("boneMass", boneMass);
        intent1.putExtra("bodyWater", bodyWater);
        intent1.putExtra("muscleMass", muscleMass);
        intent1.putExtra("healthScore", healthScore);
        intent1.putExtra("visceralFat", visceralFat);
        intent1.putExtra("fatFreeWeight", fatFreeWeight);
        intent1.putExtra("skeletalMuscle", skeletalMuscle);

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

        if (requestCode == REQUSET_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                readAndStoreData(data);
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
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(txt);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    public void onClick(View view) {
        //click Listener
        switch (view.getId()) {
            case R.id.tv_header_height:
                context.startActivity(new Intent(this, HeightActivity.class));
                break;
        }
    }

    /**
     *
     */
    private void setupUI(){
        setContentView(R.layout.actofit_main_activity);

        textToSpeech = new TextToSpeech(getApplicationContext(), this);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Weight Measurement");

        etUserId = findViewById(R.id.txtuid);
        etUserName = findViewById(R.id.txtuname);
        etHeight = findViewById(R.id.txtuheight);
        etUserDateOfBirth = findViewById(R.id.txt_sdob);

        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        radioGroupGender = findViewById(R.id.rdogrp);

        switchAthlete = findViewById(R.id.switchbtn);

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvMobile = findViewById(R.id.tv_mobile_number);

        tvHeight = findViewById(R.id.tv_header_height);

        btnNext = findViewById(R.id.btn_next);
        btnRepeat = findViewById(R.id.btn_repeat);
        btnSmartScale = findViewById(R.id.btn_smart_scale);
    }

    /**
     *
     */
    private void setupEvents(){
        btnNext.setOnClickListener(view -> goNext());

        etUserDateOfBirth.setOnClickListener(view -> editUserDateOfBirth());

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

            tvName.setText("Name : " + sharedPreferencesPersonal.getString("name", ""));
            tvGender.setText("Gender : " + sharedPreferencesPersonal.getString("gender", ""));
            tvMobile.setText("Phone : " + sharedPreferencesPersonal.getString("mobile_number", ""));
            tvAge.setText("DOB : " + sharedPreferencesPersonal.getString("dob", ""));
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

            tvAge.setText("DOB : " + sharedPreferencesPersonal.getString("dob", ""));
            tvName.setText("Name : " + sharedPreferencesPersonal.getString("name", ""));
            tvGender.setText("Gender : " + sharedPreferencesPersonal.getString("gender", ""));
            tvMobile.setText("Phone : " + sharedPreferencesPersonal.getString("mobile_number", ""));
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
                Calendar calender = Calendar.getInstance();
                calender.setTimeInMillis(0);
                calender.set(year, month, day, 0, 0, 0);

                Date selectedDate = calender.getTime();
                String dateFormatUS = EEEddMMMyyyyFormat.format(selectedDate);
                etUserDateOfBirth.setText(dateFormatUS);
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
     * @param text
     */
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     *
     */
    private void stopTextToSpeech(){
        /* close the textToSpeech engine to avoide the runtime exception from it */
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
