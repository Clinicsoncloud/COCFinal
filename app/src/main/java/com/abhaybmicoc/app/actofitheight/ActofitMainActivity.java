package com.abhaybmicoc.app.actofitheight;

import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.app.DatePickerDialog;
import android.annotation.SuppressLint;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.Switch;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.CompoundButton;

import android.util.Log;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.heightweight.Principal;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;

import java.util.Date;
import java.util.Locale;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ActofitMainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    Context context = ActofitMainActivity.this;

    public static final int REQUSET_CODE = 1001;
    public static final String TAG = "MainActivity";

    private String txt = "";

    private boolean isAthlete;

    private int day, month, year;

    private SharedPreferences objData;
    private SharedPreferences actofitData;

    private SimpleDateFormat EEEddMMMyyyyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    private Button btn;
    private Button btnNext;
    private Button btnrepeat;

    private EditText edtHeight;
    private EditText edtUserId;
    private EditText edtUserName;

    private Switch aSwitch;

    private RadioGroup RdioGrp;
    private RadioButton radioMale;
    private RadioButton radioFemale;

    private TextView txtAge;
    private TextView txtName;
    private TextView txtHeight;
    private TextView txtGender;
    private TextView txtMobile;
    private TextView edtUserDOB;
    private ActionBar actionBar;

    private TextToSpeech tts;

  /*  RESULT_CANCELED = 101;
    RESULT_CANCELED = 102;
    RESULT_CANCELED = 100;*/

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();

        loadData();

        btnNext = findViewById(R.id.btnnext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(getApplicationContext(), ThermometerScreen.class);
                startActivity(objIntent);
                finish();
            }
        });

        edtUserDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calander2 = Calendar.getInstance();
                        calander2.setTimeInMillis(0);
                        calander2.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        Date SelectedDate = calander2.getTime();
                        DateFormat dateformat_US = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                        String StringDateformat_US = EEEddMMMyyyyFormat.format(SelectedDate);
                        edtUserDOB.setText(StringDateformat_US);
                    }
                };
                DatePickerDialog dpDialog = new DatePickerDialog(ActofitMainActivity.this, listener, year, month + 1, day);
                dpDialog.getDatePicker().setMaxDate(Long.parseLong("1141038198000"));
                dpDialog.show();
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    isAthlete = true;

                } else {
                    isAthlete = false;
                }
            }
        });


        btn = (Button) findViewById(R.id.btnsave);
        btnrepeat = (Button) findViewById(R.id.btnrepeat);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                String packagenames = "com.actofitSmartScale"; //edtPackagename.getText().toString();

                if (count == 0) {
                    String packageName = "com.actofitSmartScale";
                    boolean isAppInstalled = appInstalledOrNot("com.actofitSmartScale");

                    if (isAppInstalled) {
//                        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                        Intent intent = new Intent();
                        if (intent != null) {
                            String customAction = "com.actofit.share.smartscale";
                            intent.setAction(customAction);
//                            intent.setAction(Intent.ACTION_SEND);
                            @SuppressLint("SimpleDateFormat") Date initDate = null;
                            try {
                                initDate = new SimpleDateFormat("yyyy-MM-dd").parse(getIntent().getStringExtra("dob"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                            String parsedDate = formatter.format(initDate);

                            String height = getIntent().getStringExtra("height");

                            System.out.println("Date---------" + parsedDate);

                            intent.putExtra("id", objData.getString("id", ""));
                            intent.putExtra("name", objData.getString("name", ""));
                            intent.putExtra("gender", objData.getString("gender", ""));
                            intent.putExtra("dob", parsedDate);
                            intent.putExtra("height", Integer.parseInt(height));
                            intent.putExtra("isAthlete", isAthlete);
//                            intent.putExtra("packagename", packagenames);
                            intent.setType("text/plain");
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            //startActivity(Intent.createChooser(intent, "Send"));
                            startActivityForResult(intent, REQUSET_CODE);

                            txtName.setText("Name : " + objData.getString("name", ""));
                            txtGender.setText("Gender : " + objData.getString("gender", ""));
                            txtMobile.setText("Phone : " + objData.getString("mobile_number", ""));
                            txtAge.setText("DOB : " + objData.getString("dob", ""));
                            //startActivity(intent);
                            System.out.println("Data-----id=====" + objData.getString("id", ""));
                            System.out.println("Data-----name=====" + objData.getString("name", ""));
                            System.out.println("Data-----gender=====" + getIntent().getStringExtra("gender"));
                            System.out.println("Data-----dob=====" + objData.getString("dob", ""));
                            System.out.println("Data-----height=====" + getIntent().getStringExtra("height"));
                        }
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));

                    }
                }
            }
        });
    }

    private void getdata(Intent intent) {

        String action = intent.getAction();
        String type = intent.getType();

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
            actofitData = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
            // Writing data to SharedPreferences
            SharedPreferences.Editor editor = actofitData.edit();
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
        Intent intent1 = new Intent(ActofitMainActivity.this, DisplayRecord.class);
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
    protected void onResume() {
        super.onResume();

        //reinitialization of the tts engine for voice commands

//        tts = new TextToSpeech(this,this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        //close the tts engine to avoide runtime exception
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode + "  " + resultCode);
        if (requestCode == REQUSET_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d(TAG, "onActivityResult IF : ohkkk");
                getdata(data);
            }
        } else if (requestCode == REQUSET_CODE && resultCode == RESULT_CANCELED) {
            /*
            String message = getResources().getString(R.string.subscription_over);
            new AlertDialog.Builder(ActofitMainActivity.this)
                    .setTitle("Subsciption Over!!!")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
                */
        } else if (requestCode == RESULT_CANCELED) {
            Toast.makeText(ActofitMainActivity.this, "Your Subscription has Expired!!!", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
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

    private void setupUI(){
        setContentView(R.layout.actofit_main_activity);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Weight Measurement");

        edtUserId = findViewById(R.id.txtuid);
        edtUserDOB = findViewById(R.id.txt_sdob);
        edtUserName = findViewById(R.id.txtuname);
        edtHeight = findViewById(R.id.txtuheight);

        RdioGrp = findViewById(R.id.rdogrp);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);

        aSwitch = findViewById(R.id.switchbtn);

        txtAge = findViewById(R.id.txtAge);
        txtName = findViewById(R.id.txtName);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);

        txtHeight = findViewById(R.id.txtmainheight);

        txt = "Please Click on GoTo SmartScale, and stand on weight Scale";
        tts = new TextToSpeech(getApplicationContext(), this);
        speakOut(txt);
    }

    private void setupEvents(){
        txtHeight.setOnClickListener(view -> {
            context.startActivity(new Intent(this, Principal.class));
        });
    }

    private void loadData(){
        try {
            objData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

            txtName.setText("Name : " + objData.getString("name", ""));
            txtGender.setText("Gender : " + objData.getString("gender", ""));
            txtMobile.setText("Phone : " + objData.getString("mobile_number", ""));
            txtAge.setText("DOB : " + objData.getString("dob", ""));

        } catch (Exception e) {

        }
    }
}
