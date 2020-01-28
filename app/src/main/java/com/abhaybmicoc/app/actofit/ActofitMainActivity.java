package com.abhaybmicoc.app.actofit;

import android.util.Log;
import android.os.Bundle;
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
import com.abhaybmicoc.app.oxygen.data.Const;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.screen.DisplayRecordScreen;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.utils.Constant;

import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ActofitMainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // region Variables

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

    private Button btnSkip;
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
    private String txtSpeak = "";
    private String KEY_ERROR = "error";
    private String KEY_MESSAGE = "message";
    public static final String TAG = "MainActivity";

    boolean isAthlete;
    private String storedHeight;

    /*
    RESULT_CANCELED = 101;
    RESULT_CANCELED = 102;
    RESULT_CANCELED = 100;
    */

    // endregion

    // region Events

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
        float subFat = intent.getFloatExtra("subfat", 0f);
        float protein = intent.getFloatExtra("protine", 0f);    // TODO: Check variable name
        float bodyFat = intent.getFloatExtra("bodyfat", 0f);
        float metaAge = intent.getFloatExtra("metaage", 0f);
        float boneMass = intent.getFloatExtra("bonemass", 0f);
        float physique = intent.getFloatExtra("physique", 0f);
        float bodyWater = intent.getFloatExtra("bodywater", 0f);
        float muscleMass = intent.getFloatExtra("musmass", 0f);
        float visceralFat = intent.getFloatExtra("visfat", 0f);
        float skeletalMuscle = intent.getFloatExtra("skemus", 0f);
        float healthScore = intent.getFloatExtra("helthscore", 0f);
        float fatFreeWeight = intent.getFloatExtra("fatfreeweight", 0f);

        int height = Integer.parseInt(sharedPreferencesActofit.getString(Constant.Fields.HEIGHT,""));

        try {

            SharedPreferences.Editor editor = sharedPreferencesActofit.edit();

            editor.putString(Constant.Fields.BMR, String.valueOf(bmr));
            editor.putString(Constant.Fields.BMI, String.valueOf(bmi));
            editor.putString(Constant.Fields.HEIGHT, String.valueOf(height));
            editor.putString(Constant.Fields.WEIGHT, String.valueOf(weight));
            editor.putString(Constant.Fields.SUBCUTANEOUS_FAT, String.valueOf(subFat));
            editor.putString(Constant.Fields.BODY_FAT, String.valueOf(bodyFat));
            editor.putString(Constant.Fields.PROTEIN, String.valueOf(protein));
            editor.putString(Constant.Fields.PHYSIQUE, String.valueOf(physique));
            editor.putString(Constant.Fields.BONE_MASS, String.valueOf(boneMass));
            editor.putString(Constant.Fields.BODY_WATER, String.valueOf(bodyWater));
            editor.putString(Constant.Fields.MUSCLE_MASS, String.valueOf(muscleMass));
            editor.putString(Constant.Fields.VISCERAL_FAT, String.valueOf(visceralFat));
            editor.putString(Constant.Fields.HEALTH_SCORE, String.valueOf(healthScore));
            editor.putString(Constant.Fields.FAT_FREE_WEIGHT, String.valueOf(fatFreeWeight));
            editor.putString(Constant.Fields.SKELETAL_MUSCLE, String.valueOf(skeletalMuscle));

            editor.commit();
        } catch (Exception e) {
            // TODO: Handle exception here
        }

        Intent intent1 = new Intent(ActofitMainActivity.this, DisplayRecordScreen.class);

        intent1.putExtra(Constant.Fields.BMR, bmr);
        intent1.putExtra(Constant.Fields.BMI, bmi);
        intent1.putExtra(Constant.Fields.WEIGHT, weight);
        intent1.putExtra(Constant.Fields.BODY_FAT, bodyFat);
        intent1.putExtra(Constant.Fields.PROTEIN, protein);
        intent1.putExtra(Constant.Fields.META_AGE, metaAge);
        intent1.putExtra(Constant.Fields.PHYSIQUE, physique);
        intent1.putExtra(Constant.Fields.BONE_MASS, boneMass);
        intent1.putExtra(Constant.Fields.BODY_WATER, bodyWater);
        intent1.putExtra(Constant.Fields.MUSCLE_MASS, muscleMass);
        intent1.putExtra(Constant.Fields.SUBCUTANEOUS_FAT, subFat);
        intent1.putExtra(Constant.Fields.HEALTH_SCORE, healthScore);
        intent1.putExtra(Constant.Fields.VISCERAL_FAT, visceralFat);
        intent1.putExtra(Constant.Fields.FAT_FREE_WEIGHT, fatFreeWeight);
        intent1.putExtra(Constant.Fields.SKELETAL_MUSCLE, skeletalMuscle);

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
        startTextToSpeech(status);
    }

    // endregion

    // region Initialization methods

    /**
     *
     */
    private void setupUI() {
        setContentView(R.layout.actofit_main_activity);

        textToSpeech = new TextToSpeech(getApplicationContext(), this);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Weight Measurement");

        etUserId = findViewById(R.id.txtuid);
        etHeight = findViewById(R.id.txtuheight);
        etUserName = findViewById(R.id.txtuname);
        etUserDateOfBirth = findViewById(R.id.txt_sdob);

        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        radioGroupGender = findViewById(R.id.rdogrp);

        switchAthlete = findViewById(R.id.switchbtn);

        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvGender = findViewById(R.id.tv_gender);
        tvHeight = findViewById(R.id.tv_header_height);
        tvMobile = findViewById(R.id.tv_mobile_number);

        btnSkip = findViewById(R.id.btn_skip);
        btnRepeat = findViewById(R.id.btn_repeat);
        btnSmartScale = findViewById(R.id.btn_smart_scale);
    }

    /**
     *
     */
    private void setupEvents() {
        btnSkip.setOnClickListener(view -> goNext());
        btnSmartScale.setOnClickListener(view -> startSmartScale());

        etUserDateOfBirth.setOnClickListener(view -> editUserDateOfBirth());
        switchAthlete.setOnCheckedChangeListener((compoundButton, isChecked) -> updateIsAthlete(isChecked));

        tvHeight.setOnClickListener(view -> {
            context.startActivity(new Intent(this, HeightActivity.class));
        });
    }

    /**
     *
     */
    private void initializeData() {
        txtSpeak = "Please Click on GoTo SmartScale, and stand on weight Scale";
        speakOut(txtSpeak);

        storedHeight = getIntent().getStringExtra(Constant.Fields.HEIGHT);

        try {
            sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

            tvName.setText("Name : " + sharedPreferencesPersonal.getString(Constant.Fields.NAME, ""));
            tvGender.setText("Gender : " + sharedPreferencesPersonal.getString(Constant.Fields.GENDER, ""));
            tvAge.setText("DOB : " + sharedPreferencesPersonal.getString(Constant.Fields.DATE_OF_BIRTH, ""));
            tvMobile.setText("Phone : " + sharedPreferencesPersonal.getString(Constant.Fields.MOBILE_NUMBER, ""));
        } catch (Exception e) {
            // TODO: Handle exception here
        }

        sharedPreferencesActofit = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
    }

    // endregion

    /**
     *
     */
    private void startSmartScale() {
        boolean appInstalled = isAppInstalled("com.actofitSmartScale");

        if (appInstalled) {
            Intent intent = new Intent();

            String customAction = "com.actofit.share.smartscale";
            intent.setAction(customAction);

            @SuppressLint("SimpleDateFormat")
            Date initDate = null;
            try {
                initDate = new SimpleDateFormat("yyyy-MM-dd").parse(sharedPreferencesPersonal.getString(Constant.Fields.DATE_OF_BIRTH,""));
                Log.e("initDate",""+initDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String parsedDate = formatter.format(initDate);

            intent.putExtra(Constant.Fields.HEIGHT, Integer.parseInt(sharedPreferencesActofit.getString(Constant.Fields.HEIGHT,"")));
            intent.putExtra(Constant.Fields.IS_ATHLETE, isAthlete);
            intent.putExtra(Constant.Fields.DATE_OF_BIRTH, parsedDate);
            intent.putExtra(Constant.Fields.ID, sharedPreferencesPersonal.getString(Constant.Fields.ID, ""));
            intent.putExtra(Constant.Fields.NAME, sharedPreferencesPersonal.getString(Constant.Fields.NAME, ""));
            intent.putExtra(Constant.Fields.GENDER, sharedPreferencesPersonal.getString(Constant.Fields.GENDER, ""));

            intent.setType("text/plain");
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivityForResult(intent, REQUSET_CODE);

            tvName.setText("Name : " + sharedPreferencesPersonal.getString(Constant.Fields.NAME, ""));
            tvGender.setText("Gender : " + sharedPreferencesPersonal.getString(Constant.Fields.GENDER, ""));
            tvAge.setText("DOB : " + sharedPreferencesPersonal.getString(Constant.Fields.DATE_OF_BIRTH, ""));
            tvMobile.setText("Phone : " + sharedPreferencesPersonal.getString(Constant.Fields.MOBILE_NUMBER, ""));
        }
    }

    /**
     *
     */
    private void goNext() {
        Intent objIntent = new Intent(getApplicationContext(), ThermometerScreen.class);
        startActivity(objIntent);
        finish();
    }

    /**
     *
     */
    private void editUserDateOfBirth() {
        final Calendar c = Calendar.getInstance();

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = (datePicker, year, month, day) -> {
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
     * @param isAthlete
     */
    private void updateIsAthlete(boolean isAthlete) {
        this.isAthlete = isAthlete;
    }

    /**
     * @param text
     */
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * @param status
     */
    private void startTextToSpeech(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
                // TODO: Handle this instead of logging
            } else {
                speakOut(txtSpeak);
            }

        } else {
            Log.e("TTS", "Initialization Failed!");
            // TODO: Handle this instead of logging
        }
    }

    /**
     *
     */
    private void stopTextToSpeech() {
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        } catch (Exception e) {
            Log.e("TTS", "Stopping text to speech Failed!");
        }
    }

    /**
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

    /**
     *
     */
    @Override
    public void onBackPressed() {
    }
}
