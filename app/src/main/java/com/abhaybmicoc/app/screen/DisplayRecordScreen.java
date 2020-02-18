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
import com.abhaybmicoc.app.oximeter.MainActivity;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.actofit.ActofitMainActivity;

public class DisplayRecordScreen extends AppCompatActivity implements View.OnClickListener {
    // region Variables

    Context context = DisplayRecordScreen.this;

    private Button btnBack;
    private Button btnRetest;

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
        initializeData();
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
        actionBar.hide();

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

        btnBack = findViewById(R.id.btn_skip);
        btnRetest = findViewById(R.id.btn_retest);
    }

    /**
     *
     */
    private void setupEvents(){
        tvHeight.setOnClickListener(view -> {
            context.startActivity(new Intent(this, HeightActivity.class));
        });

        tvWeight.setOnClickListener(view -> {
            context.startActivity(new Intent(this, ActofitMainActivity.class));
        });

        btnBack.setOnClickListener(v -> goNext());

        btnRetest.setOnClickListener(v -> goBack());
    }

    private void goNext() {
        clearData();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));

        finish();
    }

    private void initializeData(){
        Intent intent = getIntent();

        showData(intent);
        updateActofitSharedPreferences(intent);
    }

    // endregion

    // region Logical methods

    /**
     *
     */
    private void goBack(){
        clearData();

        startActivity(new Intent(getApplicationContext(), ActofitMainActivity.class));

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

        physique = getPhysique((intent.getFloatExtra(Constant.Fields.PHYSIQUE, 0f)));

        tvPhysique.setText("" + physique);
        tvBmi.setText("" + intent.getFloatExtra(Constant.Fields.BMI, 0f));
        tvWeight.setText(intent.getFloatExtra(Constant.Fields.WEIGHT, 0f) + "Kg");
        tvMetaAge.setText("" + intent.getFloatExtra(Constant.Fields.META_AGE, 0f));
        tvBmr.setText("" + intent.getFloatExtra(Constant.Fields.BMR, 0f) + "kcal");
        tvProtein.setText("" + intent.getFloatExtra(Constant.Fields.PROTEIN, 0f) + "%");
        tvBodyFat.setText("" + intent.getFloatExtra(Constant.Fields.BODY_FAT, 0f) + "%");
        tvName.setText("Name : " + sharedPreferencesPersonal.getString(Constant.Fields.NAME, ""));
        tvVisceralFat.setText("" + intent.getFloatExtra(Constant.Fields.VISCERAL_FAT, 0f));
        tvHealthScore.setText("" + intent.getFloatExtra(Constant.Fields.HEALTH_SCORE, 0f));
        tvBoneMass.setText("" + intent.getFloatExtra(Constant.Fields.BONE_MASS, 0f) + "kg");
        tvMusMass.setText("" + intent.getFloatExtra(Constant.Fields.MUSCLE_MASS, 0f) + "kg");
        tvBodyWater.setText("" + intent.getFloatExtra(Constant.Fields.BODY_WATER, 0f) + "%");
        tvGender.setText("Gender : " + sharedPreferencesPersonal.getString(Constant.Fields.GENDER, ""));
        tvFatFreeWeight.setText("" + intent.getFloatExtra(Constant.Fields.FAT_FREE_WEIGHT, 0f));
        tvSubFat.setText("" + intent.getFloatExtra(Constant.Fields.SUBCUTANEOUS_FAT, 0f) + "%");
        tvAge.setText("DOB : " + sharedPreferencesPersonal.getString(Constant.Fields.DATE_OF_BIRTH, ""));
        tvMobile.setText("Phone : " + sharedPreferencesPersonal.getString(Constant.Fields.MOBILE_NUMBER, ""));
        tvSkeletalMuscle.setText("" + intent.getFloatExtra(Constant.Fields.SKELETAL_MUSCLE, 0f) + "%");
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

        editor.putString(Constant.Fields.PHYSIQUE, physique);
        editor.putString(Constant.Fields.BMI, String.valueOf(intent.getFloatExtra(Constant.Fields.BMI,0f)));
        editor.putString(Constant.Fields.BMR, String.valueOf(intent.getFloatExtra(Constant.Fields.BMR,0f)));
        editor.putString(Constant.Fields.WEIGHT, String.valueOf(intent.getFloatExtra(Constant.Fields.WEIGHT,0f)));
        editor.putString(Constant.Fields.PROTEIN, String.valueOf(intent.getFloatExtra(Constant.Fields.PROTEIN,0f)));
        editor.putString(Constant.Fields.META_AGE, String.valueOf(intent.getFloatExtra(Constant.Fields.META_AGE,0f)));
        editor.putString(Constant.Fields.BODY_FAT, String.valueOf(intent.getFloatExtra(Constant.Fields.BODY_FAT,0f)));
        editor.putString(Constant.Fields.BONE_MASS, String.valueOf(intent.getFloatExtra(Constant.Fields.BONE_MASS,0f)));
        editor.putString(Constant.Fields.BODY_WATER, String.valueOf(intent.getFloatExtra(Constant.Fields.BODY_WATER,0f)));
        editor.putString(Constant.Fields.MUSCLE_MASS, String.valueOf(intent.getFloatExtra(Constant.Fields.MUSCLE_MASS,0f)));
        editor.putString(Constant.Fields.VISCERAL_FAT, String.valueOf(intent.getFloatExtra(Constant.Fields.VISCERAL_FAT,0f)));
        editor.putString(Constant.Fields.HEALTH_SCORE, String.valueOf(intent.getFloatExtra(Constant.Fields.HEALTH_SCORE,0f)));
        editor.putString(Constant.Fields.SKELETAL_MUSCLE, String.valueOf(intent.getFloatExtra(Constant.Fields.SKELETAL_MUSCLE,0f)));
        editor.putString(Constant.Fields.FAT_FREE_WEIGHT, String.valueOf(intent.getFloatExtra(Constant.Fields.FAT_FREE_WEIGHT,0f)));
        editor.putString(Constant.Fields.SUBCUTANEOUS_FAT, String.valueOf(intent.getFloatExtra(Constant.Fields.SUBCUTANEOUS_FAT,0f)));

        editor.commit();
    }

    // endregion
}
