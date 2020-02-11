package com.abhaybmicoc.app.actofit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
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
import android.bluetooth.BluetoothAdapter;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.oximeter.MainActivity;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.screen.DisplayRecordScreen;
import com.abhaybmicoc.app.services.TextToSpeechService;

import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ActofitMainActivity extends AppCompatActivity {
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
    private Button btnSmartScale;

    private TextView tvAge;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvMobile;
    private TextView tvHeight;
    private TextView etUserDateOfBirth;

    public static final int REQUSET_CODE = 1001;

//    private String SMARTSCALE_MSG = "Please Click on GoTo SmartScale, and stand on weight Scale";
    private String SMARTSCALE_MSG = "Go To  smartScale बटण वर क्लिक करा आणि smartscale वर उभे राहा";
    public static final String TAG = "MainActivity";

    TextToSpeechService textToSpeechService;

    boolean isAthlete;
    private BluetoothAdapter mBluetoothAdapter;

    /*
    RESULT_CANCELED = 101;
    RESULT_CANCELED = 102;
    RESULT_CANCELED = 100;
    */

    // endregion

    // region Events

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();

        setupEvents();

        initializeData();

        requestGPSPermission();

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

        int height = Integer.parseInt(sharedPreferencesActofit.getString(Constant.Fields.HEIGHT, ""));

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

        textToSpeechService.stopTextToSpeech();
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


    // endregion

    // region Initialization methods

    /**
     *
     */
    private void setupUI() {
        setContentView(R.layout.actofit_main_activity);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Weight Measurement");
        actionBar.hide();

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
        textToSpeechService = new TextToSpeechService(getApplicationContext(), SMARTSCALE_MSG);

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

        enableBluetooth();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestGPSPermission() {
        try {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            String provider = Settings.Secure.getString(getContentResolver(), LocationManager.GPS_PROVIDER);

            if (!statusOfGPS) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("GPS Disabled");
                alertDialogBuilder.setMessage("Kindly make sure device location is on.")
                        .setCancelable(false)
                        .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 111);
                            }
                        });

                /* create alert dialog */
                AlertDialog alertDialog = alertDialogBuilder.create();
                /* show alert dialog */
                if (!((Activity) context).isFinishing())
                    alertDialog.show();
                alertDialogBuilder.setCancelable(false);
                // Notify users and show settings if they want to enable GPS
            }
        } catch (RuntimeException ex) {
            // TODO: Show message that we did not get permission to access bluetooth
        }
    }

    private void enableBluetooth() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
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
                initDate = new SimpleDateFormat("yyyy-MM-dd").parse(sharedPreferencesPersonal.getString(Constant.Fields.DATE_OF_BIRTH, ""));
                Log.e("initDate", "" + initDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String parsedDate = formatter.format(initDate);

            intent.putExtra(Constant.Fields.HEIGHT, Integer.parseInt(sharedPreferencesActofit.getString(Constant.Fields.HEIGHT, "")));
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
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
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
