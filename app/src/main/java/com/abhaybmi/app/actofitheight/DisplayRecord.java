package com.abhaybmi.app.actofitheight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.abhaybmi.app.R;
import com.abhaybmi.app.thermometer.ThermometerScreen;
import com.abhaybmi.app.utils.ApiUtils;

public class DisplayRecord extends AppCompatActivity {

    TextView txtView;
    TextView txtweight, txtbmi, txtbodyfat, txtphysics, txtfatfreeweight, txtsubfat, txtvisfat, txtbodywater, txtskemus, txtmusmass,
            txtbonemass, txtprotine, txtbmr, txtmetagae, txthealthscore;
    Button btnBack;
    ActionBar actionBar;
    SharedPreferences actofitData;
    private TextView txtName, txtAge, txtGender, txtMobile;
    SharedPreferences objData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_secound);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Composition Measure Data");
        actionBar.hide();
        txtweight = findViewById(R.id.txtweight);
        txtbmi = findViewById(R.id.txtbmi);
        txtbodyfat = findViewById(R.id.txtbodyfat);
        txtphysics = findViewById(R.id.txtphysics);
        txtfatfreeweight = findViewById(R.id.txtfatfreeweight);
        txtsubfat = findViewById(R.id.txtsubfat);
        txtvisfat = findViewById(R.id.txtvisfat);
        txtbodywater = findViewById(R.id.txtbodywater);
        txtskemus = findViewById(R.id.txtskemus);
        txtmusmass = findViewById(R.id.txtmusmass);
        txtbonemass = findViewById(R.id.txtbonemass);
        txtprotine = findViewById(R.id.txtprotine);
        txtbmr = findViewById(R.id.txtbmr);
        txtmetagae = findViewById(R.id.txtmetaage);
        txthealthscore = findViewById(R.id.txthelthscore);
        btnBack = findViewById(R.id.btnback);

        txtName = findViewById(R.id.txtName);
        txtAge = findViewById(R.id.txtAge);
        txtGender = findViewById(R.id.txtGender);
        txtMobile = findViewById(R.id.txtMobile);


        objData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        // Reading from SharedPreferences

        txtName.setText("Name : " + objData.getString("name", ""));
        txtGender.setText("Gender : " + objData.getString("gender", ""));
        txtMobile.setText("Phone : " + objData.getString("mobile_number", ""));
        txtAge.setText("DOB : " + objData.getString("dob", ""));

        Intent intent = getIntent();
        txtweight.setText(intent.getStringExtra("weight") + "Kg");
        txtbmi.setText(intent.getStringExtra("bmi"));
        txtbodyfat.setText(intent.getStringExtra("bodyfat") + "%");
        txtfatfreeweight.setText(intent.getStringExtra("fatfreeweight"));
        String phy = getPhy(Integer.parseInt(intent.getStringExtra("physique")));
        txtphysics.setText(phy);
        txtsubfat.setText(intent.getStringExtra("subfat") + "%");
        txtvisfat.setText(intent.getStringExtra("visfat"));
        txtbodywater.setText(intent.getStringExtra("bodywater") + "%");
        txtskemus.setText(intent.getStringExtra("skemus") + "%");
        txtmusmass.setText(intent.getStringExtra("musmass") + "kg");
        txtbonemass.setText(intent.getStringExtra("bonemass") + "kg");
        txtprotine.setText(intent.getStringExtra("protine") + "%");
        txtbmr.setText(intent.getStringExtra("bmr") + "kcal");
        txtmetagae.setText(intent.getStringExtra("metaage"));
        txthealthscore.setText(intent.getStringExtra("helthscore"));

        actofitData = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = actofitData.edit();
        editor.putString("weight", intent.getStringExtra("weight"));
        editor.putString("bmi", intent.getStringExtra("bmi"));
        editor.putString("bodyfat", intent.getStringExtra("bodyfat"));

        editor.putString("fatfreeweight", intent.getStringExtra("fatfreeweight"));
        editor.putString("physique", phy);
        editor.putString("visfat", intent.getStringExtra("visfat"));

        editor.putString("bodywater", intent.getStringExtra("bodywater"));
        editor.putString("musmass", intent.getStringExtra("musmass"));
        editor.putString("bonemass", intent.getStringExtra("bonemass"));

        editor.putString("protine", intent.getStringExtra("protine"));
        editor.putString("bmr", intent.getStringExtra("bmr"));
        editor.putString("subfat", intent.getStringExtra("subfat"));

        editor.putString("skemus", intent.getStringExtra("skemus"));
        editor.putString("metaage", intent.getStringExtra("metaage"));
        editor.putString("helthscore", intent.getStringExtra("helthscore"));

        editor.commit();

        btnBack.setOnClickListener(v -> {
            cleardata();
            startActivity(new Intent(getApplicationContext(), ThermometerScreen.class));
            finish();
        });
    }

    public void cleardata() {
        txtweight.setText("");
        txtbmi.setText("");
        txtbodyfat.setText("");
        txtfatfreeweight.setText("");
        txtphysics.setText("");
        txtsubfat.setText("");
        txtvisfat.setText("");
        txtbodywater.setText("");
        txtskemus.setText("");
        txtmusmass.setText("");
        txtbonemass.setText("");
        txtprotine.setText("");
        txtbmr.setText("");
        txtmetagae.setText("");
        txthealthscore.setText("");

    }


    public String getPhy(int val) {
        String value = "";
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
}
