package com.abhaybmi.app.actofitheight;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.abhaybmi.app.R;
import com.abhaybmi.app.thermometer.ThermometerScreen;
import com.abhaybmi.app.utils.ApiUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ActofitMainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int REQUSET_CODE = 1001;
    public SimpleDateFormat EEEddMMMyyyyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
    Button btn, btnnext, btnrepeat;
    EditText edtUserId, edtUserName, edtUserEmail, edtHeight;
    Switch aSwitch;
    RadioGroup RdioGrp;
    RadioButton radioMale, radioFemale;
    TextView edtUserDOB;
    String gen;
    int Athlate_val = 0;
    TextView distext;
    android.support.v7.app.ActionBar actionBar;
    private int day, month, year;
    SharedPreferences actofitData;
    SharedPreferences objData;
    private String globalName, globlaid, globaldob, globalgender;
    private TextView txtName, txtAge, txtGender, txtMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actofit_main_activity);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Weight Measurement");
        actionBar.hide();
        edtUserId = (EditText) findViewById(R.id.txtuid);
        edtUserName = (EditText) findViewById(R.id.txtuname);
        edtUserDOB = (TextView) findViewById(R.id.txt_sdob);
        edtHeight = (EditText) findViewById(R.id.txtuheight);
        RdioGrp = (RadioGroup) findViewById(R.id.rdogrp);
        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);
        aSwitch = (Switch) findViewById(R.id.switchbtn);

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);

        try {
            objData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
            // Reading from SharedPreferences

            txtName.setText("Name : " + objData.getString("name", ""));
            txtGender.setText("Gender : " + objData.getString("gender", ""));
            txtMobile.setText("Phone : " + objData.getString("mobile_number", ""));
            txtAge.setText("DOB : " + objData.getString("dob", ""));

        } catch (Exception e) {

        }

        btnnext = findViewById(R.id.btnnext);
        btnnext.setOnClickListener(new View.OnClickListener() {
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

                        // String date1=dayOfMonth + "-" + monthOfYear + "-" + year;
                        //edtFrom.setText(date1);

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
                    Athlate_val = 1;

                } else {
                    Athlate_val = 0;
                }
            }
        });


        btn = (Button) findViewById(R.id.btnsave);
        btnrepeat = (Button) findViewById(R.id.btnrepeat);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;

                /*int id_radio = RdioGrp.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(id_radio);
                String gender = radioButton.getText().toString();
                if (gender.equals("Male")) {
                    gen = "male";
                } else {
                    gen = "female";

                }
                String dob = edtUserDOB.getText().toString();
                if (dob.equals("")) {
                    count++;
                }

                String height = edtHeight.getText().toString();
                if (height.equals("")) {
                    edtHeight.setError("Height between 90 - 275");
                    count++;
                } else {
                    Double height_cal = Double.valueOf(height);
                    if (height_cal < 90 || height_cal > 275) {
                        edtHeight.setError("Height between 90 - 275");
                        count++;

                    }
                }
*/
                String packagenames = "com.actofit.actofitengage"; //edtPackagename.getText().toString();

                if (count == 0) {
                    String packageName = "com.actofit.actofitengage";
                    boolean isAppInstalled = appInstalledOrNot("com.actofit.actofitengage");

                    if (isAppInstalled) {

                        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                        if (intent != null) {
                            intent.setAction(Intent.ACTION_SEND);
                            /*@SuppressLint("SimpleDateFormat") SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy MM dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat targetFormat = new SimpleDateFormat("dd MM yyyy" );
                            Date date;
                            try {
                                date = originalFormat.parse(getIntent().getStringExtra("dob"));
                                System.out.println("Old Format :   " + originalFormat.format(date));
                                System.out.println("New Format :   " + targetFormat.format(date));

                            } catch (ParseException ex) {
                                // Handle Exception.
                            }*/
                            @SuppressLint("SimpleDateFormat") Date initDate = null;
                            try {
                                initDate = new SimpleDateFormat("yyyy-MM-dd").parse(getIntent().getStringExtra("dob"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                            String parsedDate = formatter.format(initDate);
                            System.out.println("Date---------" + parsedDate);

                            intent.putExtra("id", objData.getString("id", ""));
                            intent.putExtra("name", objData.getString("name", ""));
                            intent.putExtra("gender", objData.getString("gender", ""));
                            intent.putExtra("dob", parsedDate);
                            intent.putExtra("height", getIntent().getStringExtra("height"));
                            intent.putExtra("amode", String.valueOf(Athlate_val));
                            intent.putExtra("packagename", packagenames);
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


        btnrepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;

                /*int id_radio = RdioGrp.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(id_radio);
                String gender = radioButton.getText().toString();
                if (gender.equals("Male")) {
                    gen = "male";
                } else {
                    gen = "female";

                }
                String dob = edtUserDOB.getText().toString();
                if (dob.equals("")) {
                    count++;
                }

                String height = edtHeight.getText().toString();
                if (height.equals("")) {
                    edtHeight.setError("Height between 90 - 275");
                    count++;
                } else {
                    Double height_cal = Double.valueOf(height);
                    if (height_cal < 90 || height_cal > 275) {
                        edtHeight.setError("Height between 90 - 275");
                        count++;

                    }
                }
*/
                String packagenames = "com.actofit.actofitengage"; //edtPackagename.getText().toString();

                if (count == 0) {
                    String packageName = "com.actofit.actofitengage";
                    boolean isAppInstalled = appInstalledOrNot("com.actofit.actofitengage");

                    if (isAppInstalled) {

                        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                        if (intent != null) {
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra("id", getIntent().getStringExtra("id"));
                            intent.putExtra("name", getIntent().getStringExtra("name"));
                            intent.putExtra("gender", getIntent().getStringExtra("gender"));
                            intent.putExtra("dob", getIntent().getStringExtra("dob"));
                            intent.putExtra("height", getIntent().getStringExtra("height"));
                            intent.putExtra("amode", String.valueOf(Athlate_val));
                            intent.putExtra("packagename", packagenames);
                            intent.setType("text/plain");
                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            //startActivity(Intent.createChooser(intent, "Send"));
                            startActivityForResult(intent, REQUSET_CODE);
                            //startActivity(intent);
                            System.out.println("Data-----id=====" + getIntent().getStringExtra("id"));
                            System.out.println("Data-----name=====" + getIntent().getStringExtra("name"));
                            System.out.println("Data-----gender=====" + getIntent().getStringExtra("gender"));
                            System.out.println("Data-----dob=====" + getIntent().getStringExtra("dob"));
                            System.out.println("Data-----height=====" + getIntent().getStringExtra("height"));

                        }
                    } else {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));

                    }
                }
            }
        });

        //getdata();

    }

    private void getdata(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        String weight = intent.getStringExtra("weight");
        String bmi = intent.getStringExtra("bmi");
        String bodyfat = intent.getStringExtra("bodyfat");
        String fatfreeweight = intent.getStringExtra("fatfreeweight");
        String physique = intent.getStringExtra("physique");
        String subfat = intent.getStringExtra("subfat");
        String visfat = intent.getStringExtra("visfat");
        String bodywater = intent.getStringExtra("bodywater");
        String skemus = intent.getStringExtra("skemus");
        String musmass = intent.getStringExtra("musmass");

        String bonemass = intent.getStringExtra("bonemass");
        String protine = intent.getStringExtra("protine");
        String bmr = intent.getStringExtra("bmr");
        String metaage = intent.getStringExtra("metaage");
        String helthscore = intent.getStringExtra("helthscore");

        try {
            actofitData = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
            // Writing data to SharedPreferences
            SharedPreferences.Editor editor = actofitData.edit();
            editor.putString("weight", weight);
            editor.putString("height", getIntent().getStringExtra("height"));
            editor.putString("bmi", bmi);
            editor.putString("bodyfat", bodyfat);
            editor.putString("fatfreeweight", fatfreeweight);
            editor.putString("physique", physique);
            editor.putString("visfat", visfat);
            editor.putString("bodywater", bodywater);
            editor.putString("musmass", musmass);
            editor.putString("bonemass", bonemass);
            editor.putString("protine", protine);
            editor.putString("bmr", bmr);
            editor.putString("subfat", subfat);
            editor.putString("skemus", skemus);
            editor.putString("helthscore", helthscore);
            editor.commit();

        } catch (Exception e) {

        }

        Log.d(TAG, "getdata: bonemass: " + bonemass + " bmr: " + bmr);

        Intent intent1 = new Intent(getApplicationContext(), DisplayRecord.class);
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
        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: " + requestCode + resultCode);
        if (requestCode == REQUSET_CODE) {

            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: ohkkk");
                getdata(data);
            } else {
                Log.d(TAG, "onActivityResult: result else");
            }

        } else {

            Log.d(TAG, "onActivityResult:  " + requestCode);
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

}


/*

 intent.putExtra("weight", weight);
                intent.putExtra("bmi",bmi);
                intent.putExtra("bodyfat",bodyfat);
                intent.putExtra("fatfreeweight",fatfreeweight);
                intent.putExtra("physique",physique);
                intent.putExtra("subfat",subfat);
                intent.putExtra("visfat",visfat);
                intent.putExtra("bodywater",bodywater);
                intent.putExtra("skemus",skemus);
                intent.putExtra("musmass",musmass);
                intent.putExtra("bonemass",bonemass);
                intent.putExtra("protine",protine);
                intent.putExtra("bmr",bmr);
                intent.putExtra("metaage",metaage);
                intent.putExtra("helthscore",helthscore);

 */