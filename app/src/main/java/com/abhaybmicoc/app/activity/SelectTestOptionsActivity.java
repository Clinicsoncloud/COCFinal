package com.abhaybmicoc.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.printer.esys.pridedemoapp.Act_Main;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.screen.OtpVerifyScreen;
import com.abhaybmicoc.app.utils.ApiUtils;

public class SelectTestOptionsActivity extends AppCompatActivity {

    private Context context = SelectTestOptionsActivity.this;

    private LinearLayout llFreeCheckupLayout;
    private LinearLayout llPatientReportsLayout;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        initializeData();
        setupEvents();
    }

    private void setupUI() {
        setContentView(R.layout.activity_select_test_options);

        llFreeCheckupLayout = findViewById(R.id.ll_FreeCheckupLayout);
        llPatientReportsLayout = findViewById(R.id.ll_PatientReportsLayout);
        ivBack = findViewById(R.id.iv_Back);

    }

    private void initializeData() {
    }

    private void setupEvents() {

        llFreeCheckupLayout.setOnClickListener(view -> goToHealthCheckup());
        llPatientReportsLayout.setOnClickListener(view -> goToPatientsReports());
        ivBack.setOnClickListener(view -> goToBack());

    }

    private void goToHealthCheckup() {
        Intent objIntent = new Intent(getApplicationContext(), HeightActivity.class);
        startActivity(objIntent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
        finish();
//               Node JS 12.
//                lts
    }

    private void goToPatientsReports() {

        Intent intent = new Intent(context, Act_Main.class);
        intent.putExtra("is_PrinterConnected", "");
        intent.putExtra("report_type", "view_only_report");
        startActivity(intent);
    }

    private void goToBack() {

        Intent intent = new Intent(context, OtpVerifyScreen.class);
        intent.putExtra("connectivity", "online");
        startActivity(intent);
        finish();
    }

    private void clearPersonalInformation() {

        SharedPreferences.Editor sharedPreferencePersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE).edit().clear();
        sharedPreferencePersonalData.clear().apply();
    }

    @Override
    public void onBackPressed() {
        goToBack();
    }
}
