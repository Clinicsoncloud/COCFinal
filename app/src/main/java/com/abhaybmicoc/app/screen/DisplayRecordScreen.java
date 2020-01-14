package com.abhaybmicoc.app.screen;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.support.v7.app.ActionBar;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;

public class DisplayRecordScreen extends AppCompatActivity implements View.OnClickListener {
    // region Variables

    Context context = DisplayRecordScreen.this;

    private Button btnBack;
    private ActionBar actionBar;

    private SharedPreferences sharedPreferencesActofit;
    private SharedPreferences sharedPreferencesPersonal;

    private TextView tvAge;
    private TextView tvBmi;
    private TextView tvBmr;
    private TextView tvName;
    private TextView tvGender;
    private TextView tvSubFat;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvMobile;
    private TextView tvMetaAge;
    private TextView tvBodyFat;
    private TextView tvMusMass;
    private TextView tvProtein;
    private TextView tvPhysique;
    private TextView tvBoneMass;
    private TextView tvBodyWater;
    private TextView tvVisceralFat;
    private TextView tvHealthScore;
    private TextView tvFatFreeWeight;
    private TextView tvSkeletalMuscle;

    private String physique = "--";

    // endregion

    // region Events

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_header_height:
                context.startActivity(new Intent(this, HeightActivity.class));
                break;

            case R.id.tv_header_weight:
                context.startActivity(new Intent(this, ActofitMainActivity.class));
                break;
        }
    }

    // endregion

    // region Initialization methods

    /**
     *
     */
    private void setupUI(){
        setContentView(R.layout.layout_display_record_screen);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Composition Measure Data");

        tvBmi = findViewById(R.id.tv_bmi);
        tvBmr = findViewById(R.id.tv_bmr);
        tvAge = findViewById(R.id.tv_age);
        tvName = findViewById(R.id.tv_name);
        tvWeight = findViewById(R.id.tv_weight);
        tvGender = findViewById(R.id.tv_gender);
        tvWeight = findViewById(R.id.tv_weight);
        tvProtein = findViewById(R.id.tv_protine);
        tvBodyFat = findViewById(R.id.tv_body_fat);
        tvMetaAge = findViewById(R.id.tv_meta_age);
        tvPhysique = findViewById(R.id.tv_physique);
        tvBoneMass = findViewById(R.id.tv_bone_mass);
        tvMusMass = findViewById(R.id.tv_muscle_mass);
        tvMobile = findViewById(R.id.tv_mobile_number);
        tvHeight = findViewById(R.id.tv_header_height);
        tvBodyWater = findViewById(R.id.tv_body_water);
        tvHealthScore = findViewById(R.id.tv_helth_score);
        tvSubFat = findViewById(R.id.tv_subcutaneous_fat);
        tvVisceralFat = findViewById(R.id.tv_visceral_fat);
        tvFatFreeWeight = findViewById(R.id.tv_fat_free_weight);
        tvSkeletalMuscle = findViewById(R.id.tv_skeletel_muscle);

        btnBack = findViewById(R.id.btn_next);

        Intent intent = getIntent();

        showData(intent);
        updateActofitSharedPreferences(intent);
    }

    /**
     *
     */
    private void setupEvents(){
        tvHeight.setOnClickListener(this);
        tvWeight.setOnClickListener(this);

        btnBack.setOnClickListener(v -> goBack());
    }

    // endregion

    // region Logical methods

    /**
     *
     */
    private void goBack(){
        clearData();

        startActivity(new Intent(getApplicationContext(), ThermometerScreen.class));

        finish();
    }

    /**
     *
     */
    private void clearData() {
        tvBmi.setText("");
        tvBmr.setText("");
        tvWeight.setText("");
        tvSubFat.setText("");
        tvBodyFat.setText("");
        tvMusMass.setText("");
        tvProtein.setText("");
        tvMetaAge.setText("");
        tvPhysique.setText("");
        tvBoneMass.setText("");
        tvBodyWater.setText("");
        tvHealthScore.setText("");
        tvVisceralFat.setText("");
        tvFatFreeWeight.setText("");
        tvSkeletalMuscle.setText("");
    }

    /**
     *
     */
    private void showData(Intent intent){
        sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        physique = getPhysique((intent.getFloatExtra("physique", 0f)));

        tvPhysique.setText("" + physique);
        tvBmi.setText("" + intent.getFloatExtra("bmi", 0f));
        tvWeight.setText(intent.getFloatExtra("weight", 0f) + "Kg");
        tvMetaAge.setText("" + intent.getFloatExtra("metaage", 0f));
        tvBmr.setText("" + intent.getFloatExtra("bmr", 0f) + "kcal");
        tvAge.setText("DOB : " + sharedPreferencesPersonal.getString("dob", ""));
        tvVisceralFat.setText("" + intent.getFloatExtra("visfat", 0f));
        tvSubFat.setText("" + intent.getFloatExtra("subfat", 0f) + "%");
        tvName.setText("Name : " + sharedPreferencesPersonal.getString("name", ""));
        tvBodyFat.setText("" + intent.getFloatExtra("bodyFat", 0f) + "%");
        tvProtein.setText("" + intent.getFloatExtra("protein", 0f) + "%");
        tvHealthScore.setText("" + intent.getFloatExtra("healthScore", 0f));
        tvBoneMass.setText("" + intent.getFloatExtra("boneMass", 0f) + "kg");
        tvMusMass.setText("" + intent.getFloatExtra("muscleMass", 0f) + "kg");
        tvBodyWater.setText("" + intent.getFloatExtra("bodyWater", 0f) + "%");
        tvGender.setText("Gender : " + sharedPreferencesPersonal.getString("gender", ""));
        tvSkeletalMuscle.setText("" + intent.getFloatExtra("skemus", 0f) + "%");
        tvFatFreeWeight.setText("" + intent.getFloatExtra("fatFreeWeight", 0f));
        tvMobile.setText("Phone : " + sharedPreferencesPersonal.getString("mobileNumber", ""));
    }

    /**
     *
     * @param val
     * @return
     */
    public String getPhysique(float val) {
        String value;

        if (val == 1) {
            value = "Potential Overweight";
        } else if (val == 2) {
            value = "Under Exercised";
        } else if (val == 3) {
            value = "Thin";
        } else if (val == 4) {
            value = "Standard";
        } else if (val == 5) {
            value = "Thin Muscular";
        } else if (val == 6) {
            value = "Obese";
        } else if (val == 7) {
            value = "Overweight";
        } else if (val == 8) {
            value = "Standard Muscular";
        } else if (val == 9) {
            value = "Strong Muscular";
        } else {
            value = "--";
        }

        return value;
    }
    
    private void updateActofitSharedPreferences(Intent intent){
        sharedPreferencesActofit = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
        
        SharedPreferences.Editor editor = sharedPreferencesActofit.edit();

        editor.putString("physique", physique);
        editor.putString("bmi", String.valueOf(intent.getFloatExtra("bmi",0f)));
        editor.putString("bmr", String.valueOf(intent.getFloatExtra("bmr",0f)));
        editor.putString("subfat", String.valueOf(intent.getFloatExtra("subfat",0f)));
        editor.putString("visfat", String.valueOf(intent.getFloatExtra("visfat",0f)));
        editor.putString("weight", String.valueOf(intent.getFloatExtra("weight",0f)));
        editor.putString("skemus", String.valueOf(intent.getFloatExtra("skemus",0f)));
        editor.putString("metaage", String.valueOf(intent.getFloatExtra("metaage",0f)));
        editor.putString("bodyfat", String.valueOf(intent.getFloatExtra("bodyfat",0f)));
        editor.putString("musmass", String.valueOf(intent.getFloatExtra("musmass",0f)));
        editor.putString("protine", String.valueOf(intent.getFloatExtra("protine",0f)));
        editor.putString("bonemass", String.valueOf(intent.getFloatExtra("bonemass",0f)));
        editor.putString("bodywater", String.valueOf(intent.getFloatExtra("bodywater",0f)));
        editor.putString("helthscore", String.valueOf(intent.getFloatExtra("helthscore",0f)));
        editor.putString("fatfreeweight", String.valueOf(intent.getFloatExtra("fatfreeweight",0f)));

        editor.commit();
    }

    // endregion
}
