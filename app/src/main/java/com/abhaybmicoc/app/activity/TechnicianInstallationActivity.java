package com.abhaybmicoc.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.abhaybmicoc.app.R;


public class TechnicianInstallationActivity extends AppCompatActivity {

    private Context context = TechnicianInstallationActivity.this;
    private ImageView iv_Back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        initializeData();
        setupEvents();
    }

    private void setupUI() {
        setContentView(R.layout.activity_technician_installation);

//        iv_Back = findViewById(R.id.iv_Back);

    }

    private void initializeData() {

    }

    private void setupEvents() {

//        iv_Back.setOnClickListener(view -> goToBackScreen());
    }

    @Override
    public void onBackPressed() {
//        startActivity(new Intent(context, OtpLoginScreen.class));
//        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void goToBackScreen() {


        /*startActivity(new Intent(context, OtpLoginScreen.class));
        finish();*/
    }


}
