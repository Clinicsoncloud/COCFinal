package com.abhaybmicoc.app.screen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.activity.ActofitMainActivity;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.utils.ApiUtils;

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
    private TextView tvVisFat;
    private TextView tvSkemus;
    private TextView tvWeight;
    private TextView tvMobile;
    private TextView tvMetaAge;
    private TextView tvBodyFat;
    private TextView tvMusMass;
    private TextView tvProtein;
    private TextView tvPhysique;
    private TextView tvBoneMass;
    private TextView tvBodyWater;
    private TextView tvHealthScore;
    private TextView tvFatFreeWeight;

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
            case R.id.txtmainheight:
                context.startActivity(new Intent(this, HeightActivity.class));
                break;

            case R.id.txtmainweight:
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
        setContentView(R.layout.layout_secound);        // ISSUE: Bad name for layout

        actionBar = getSupportActionBar();
        actionBar.setTitle("Composition Measure Data");

        tvBmi = findViewById(R.id.txtbmi);
        tvBmr = findViewById(R.id.txtbmr);
        tvAge = findViewById(R.id.txtAge);
        tvName = findViewById(R.id.txtName);
        tvWeight = findViewById(R.id.txtweight);
        tvSubFat = findViewById(R.id.txtsubfat);
        tvVisFat = findViewById(R.id.txtvisfat);
        tvGender = findViewById(R.id.txtGender);
        tvSkemus = findViewById(R.id.txtskemus);
        tvMobile = findViewById(R.id.txtMobile);
        tvWeight = findViewById(R.id.txtweight);
        tvBodyFat = findViewById(R.id.txtbodyfat);
        tvMusMass = findViewById(R.id.txtmusmass);
        tvProtein = findViewById(R.id.txtprotine);
        tvMetaAge = findViewById(R.id.txtmetaage);
        tvPhysique = findViewById(R.id.txtphysique);
        tvBoneMass = findViewById(R.id.txtbonemass);
        tvHeight = findViewById(R.id.txtmainheight);
        tvBodyWater = findViewById(R.id.txtbodywater);
        tvHealthScore = findViewById(R.id.txthelthscore);
        tvFatFreeWeight = findViewById(R.id.txtfatfreeweight);

        btnBack = findViewById(R.id.btnback);

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

        btnBack.setOnClickListener(v -> {
            goBack();
        });
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
        tvVisFat.setText("");
        tvSkemus.setText("");
        tvBodyFat.setText("");
        tvPhysique.setText("");
        tvMusMass.setText("");
        tvProtein.setText("");
        tvMetaAge.setText("");
        tvBoneMass.setText("");
        tvBodyWater.setText("");
        tvHealthScore.setText("");
        tvFatFreeWeight.setText("");
    }

    /**
     *
     */
    private void showData(Intent intent){
        sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        physique = getphysiquesique((intent.getFloatExtra("physique", 0f)));

        tvPhysique.setText("" + physique);
        tvAge.setText("DOB : " + sharedPreferencesPersonal.getString("dob", ""));
        tvName.setText("Name : " + sharedPreferencesPersonal.getString("name", ""));
        tvBmi.setText("" + intent.getFloatExtra("bmi", 0f));
        tvGender.setText("Gender : " + sharedPreferencesPersonal.getString("gender", ""));
        tvVisFat.setText("" + intent.getFloatExtra("visfat", 0f));
        tvMobile.setText("Phone : " + sharedPreferencesPersonal.getString("mobile_number", ""));
        tvWeight.setText(intent.getFloatExtra("weight", 0f) + "Kg");
        tvMetaAge.setText("" + intent.getFloatExtra("metaage", 0f));
        tvBmr.setText("" + intent.getFloatExtra("bmr", 0f) + "kcal");
        tvSkemus.setText("" + intent.getFloatExtra("skemus", 0f) + "%");
        tvSubFat.setText("" + intent.getFloatExtra("subfat", 0f) + "%");
        tvBodyFat.setText("" + intent.getFloatExtra("bodyfat", 0f) + "%");
        tvProtein.setText("" + intent.getFloatExtra("protine", 0f) + "%");
        tvMusMass.setText("" + intent.getFloatExtra("musmass", 0f) + "kg");
        tvHealthScore.setText("" + intent.getFloatExtra("helthscore", 0f));
        tvBoneMass.setText("" + intent.getFloatExtra("bonemass", 0f) + "kg");
        tvBodyWater.setText("" + intent.getFloatExtra("bodywater", 0f) + "%");
        tvFatFreeWeight.setText("" + intent.getFloatExtra("fatfreeweight", 0f));
    }

    /**
     *
     * @param val
     * @return
     */
    public String getphysiquesique(float val) {
        String value = null;

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
