package com.abhaybmicoctest.app.actofitheight;

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

import com.abhaybmicoctest.app.R;

import com.abhaybmicoctest.app.heightweight.Principal;
import com.abhaybmicoctest.app.thermometer.ThermometerScreen;
import com.abhaybmicoctest.app.utils.ApiUtils;

public class DisplayRecord extends AppCompatActivity implements View.OnClickListener {

    TextView txtView;
    TextView txtweight, txtbmi, txtbodyfat, txtphysics, txtfatfreeweight, txtsubfat, txtvisfat, txtbodywater, txtskemus, txtmusmass,
            txtbonemass, txtprotine, txtbmr, txtmetagae, txthealthscore;
    Button btnBack;
    ActionBar actionBar;
    SharedPreferences actofitData;
    private TextView txtName, txtAge, txtGender, txtMobile;
    SharedPreferences objData;
    private TextView txtHeight, txtWeight, txtTemprature, txtBpMonitor, txtOximeter, txtSugar, txtHemoglobin;

    Context context;
    private String phy = "--";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_secound);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Composition Measure Data");
//        actionBar.hide();
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

        //Initialization of the all top boxes or pointers
        txtHeight = findViewById(R.id.txtmainheight);
        txtweight = findViewById(R.id.txtweight);
        txtTemprature = findViewById(R.id.txtmaintempreture);
        txtBpMonitor = findViewById(R.id.txtmainbloodpressure);
        txtOximeter = findViewById(R.id.txtmainpulseoximeter);
        txtSugar = findViewById(R.id.txtmainbloodsugar);
        txtHemoglobin = findViewById(R.id.txtmainhemoglobin);

        context = DisplayRecord.this;

        //bind events related to the top boxes
        bindEvents();

        objData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        // Reading from SharedPreferences

        txtName.setText("Name : " + objData.getString("name", ""));
        txtGender.setText("Gender : " + objData.getString("gender", ""));
        txtMobile.setText("Phone : " + objData.getString("mobile_number", ""));
        txtAge.setText("DOB : " + objData.getString("dob", ""));

        Intent intent = getIntent();
        /*txtweight.setText("Weight : " + intent.getFloatExtra("weight", 0f) + "Kg");
        txtbmi.setText("BMI : " + intent.getFloatExtra("bmi", 0f));
        txtbodyfat.setText("Body Fat : " + intent.getFloatExtra("bodyfat", 0f) + "%");
        txtfatfreeweight.setText("Fat Free Weight : " + intent.getFloatExtra("fatfreeweight", 0f));
        phy = getPhy((intent.getFloatExtra("physique", 0f)));
        txtphysics.setText("Physique : " + phy);
        txtsubfat.setText("Subcutaneous : " + intent.getFloatExtra("subfat", 0f) + "%");
        txtvisfat.setText("Visceral Fat : " + intent.getFloatExtra("visfat", 0f));
        txtbodywater.setText("Body Water : " + intent.getFloatExtra("bodywater", 0f) + "%");
        txtskemus.setText("Skeletal Muscle : " + intent.getFloatExtra("skemus", 0f) + "%");
        txtmusmass.setText("Muscle Mass : " + intent.getFloatExtra("musmass", 0f) + "kg");
        txtbonemass.setText("Bone Mass : " + intent.getFloatExtra("bonemass", 0f) + "kg");
        txtprotine.setText("Protine : " + intent.getFloatExtra("protine", 0f) + "%");
        txtbmr.setText("BMR : " + intent.getFloatExtra("bmr", 0f) + "kcal");
        txtmetagae.setText("Meta Age : " + intent.getFloatExtra("metaage", 0f));
        txthealthscore.setText("Health Score : " + intent.getFloatExtra("helthscore", 0f));
*/

        txtweight.setText(intent.getFloatExtra("weight", 0f) + "Kg");
        txtbmi.setText(""+intent.getFloatExtra("bmi", 0f));
        txtbodyfat.setText(""+intent.getFloatExtra("bodyfat", 0f) + "%");
        txtfatfreeweight.setText(""+intent.getFloatExtra("fatfreeweight", 0f));
        phy = getPhy((intent.getFloatExtra("physique", 0f)));
        txtphysics.setText(""+ phy);
        txtsubfat.setText(""+intent.getFloatExtra("subfat", 0f) + "%");
        txtvisfat.setText(""+intent.getFloatExtra("visfat", 0f));
        txtbodywater.setText(""+intent.getFloatExtra("bodywater", 0f) + "%");
        txtskemus.setText(""+intent.getFloatExtra("skemus", 0f) + "%");
        txtmusmass.setText(""+intent.getFloatExtra("musmass", 0f) + "kg");
        txtbonemass.setText(""+intent.getFloatExtra("bonemass", 0f) + "kg");
        txtprotine.setText(""+intent.getFloatExtra("protine", 0f) + "%");
        txtbmr.setText(""+intent.getFloatExtra("bmr", 0f) + "kcal");
        txtmetagae.setText(""+intent.getFloatExtra("metaage", 0f));
        txthealthscore.setText(""+intent.getFloatExtra("helthscore", 0f));


        actofitData = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
        // Writing data to SharedPreferences
        SharedPreferences.Editor editor = actofitData.edit();
        editor.putString("weight", String.valueOf(intent.getFloatExtra("weight",0f)));
        editor.putString("bmi", String.valueOf(intent.getFloatExtra("bmi",0f)));
        editor.putString("bodyfat", String.valueOf(intent.getFloatExtra("bodyfat",0f)));

        editor.putString("fatfreeweight", String.valueOf(intent.getFloatExtra("fatfreeweight",0f)));
        editor.putString("physique", phy);
        editor.putString("visfat", String.valueOf(intent.getFloatExtra("visfat",0f)));

        editor.putString("bodywater", String.valueOf(intent.getFloatExtra("bodywater",0f)));
        editor.putString("musmass", String.valueOf(intent.getFloatExtra("musmass",0f)));
        editor.putString("bonemass", String.valueOf(intent.getFloatExtra("bonemass",0f)));

        editor.putString("protine", String.valueOf(intent.getFloatExtra("protine",0f)));
        editor.putString("bmr", String.valueOf(intent.getFloatExtra("bmr",0f)));
        editor.putString("subfat", String.valueOf(intent.getFloatExtra("subfat",0f)));

        editor.putString("skemus", String.valueOf(intent.getFloatExtra("skemus",0f)));
        editor.putString("metaage", String.valueOf(intent.getFloatExtra("metaage",0f)));
        editor.putString("helthscore", String.valueOf(intent.getFloatExtra("helthscore",0f)));

        editor.commit();

        btnBack.setOnClickListener(v -> {
            cleardata();
            startActivity(new Intent(getApplicationContext(), ThermometerScreen.class));
            finish();
        });
    }

    private void bindEvents() {

        //click event

        txtHeight.setOnClickListener(this);
        txtweight.setOnClickListener(this);
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


    public String getPhy(float val) {
        String value = "Phy";
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

    @Override
    public void onClick(View view) {

        //click events listeners

        switch (view.getId()){
            case R.id.txtmainheight:
                context.startActivity(new Intent(this, Principal.class));
                break;

            case R.id.txtmainweight:
                context.startActivity(new Intent(this, ActofitMainActivity.class));
                break;
        }

    }
}
