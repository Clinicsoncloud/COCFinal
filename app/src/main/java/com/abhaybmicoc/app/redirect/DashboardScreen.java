package com.abhaybmicoc.app.redirect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import main.java.com.abhaybmicoc.app.screen.BpLoginScreen;
import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.glucose.GlucoseScanListActivity;
import com.abhaybmicoc.app.heightweight.HeightScreen;
import com.abhaybmicoc.app.hemoglobin.MainActivity;
import com.abhaybmicoc.app.oxygen.OxygenActivity;
import com.abhaybmicoc.app.printer.esys.pridedemoapp.Act_Main;
import com.abhaybmicoc.app.printer.esys.pridedemoapp.PrintPriviewScreen;
import com.abhaybmicoc.app.thermometer.ThermometerScreen;
import com.abhaybmicoc.app.weight.WeightScreen;

public class DashboardScreen extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);
        init();
    }

    public void init() {
        TextView txtmainweight = findViewById(R.id.tv_header_weight);
        TextView txtmainheight = findViewById(R.id.tv_header_height);
        TextView txtmainbloodpresue = findViewById(R.id.tv_header_bloodpressure);
        TextView txtmainbloodsugar = findViewById(R.id.tv_header_bloodsugar);
        TextView txtmainhemoglobin = findViewById(R.id.tv_header_hemoglobin);
        TextView txtmainpulseoxi = findViewById(R.id.tv_header_pulseoximeter);
        TextView txtmaintempresue = findViewById(R.id.tv_header_tempreture);
        TextView txtmainprinter = findViewById(R.id.txtmainprinter);
        TextView txtprintersetup = findViewById(R.id.txtprintersetup);

        txtmainbloodpresue.setOnClickListener(this);
        txtmainweight.setOnClickListener(this);
        txtmainheight.setOnClickListener(this);
        txtmainhemoglobin.setOnClickListener(this);
        txtmainbloodsugar.setOnClickListener(this);
        txtmainpulseoxi.setOnClickListener(this);
        txtmaintempresue.setOnClickListener(this);
        txtmainprinter.setOnClickListener(this);
        txtprintersetup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_header_height:
                Intent objheight = new Intent(getApplicationContext(), HeightScreen.class);
                startActivity(objheight);
                break;
            case R.id.tv_header_weight:
                Intent objweight = new Intent(getApplicationContext(), WeightScreen.class);
                startActivity(objweight);
                break;
            case R.id.tv_header_tempreture:
                Intent objtemp = new Intent(getApplicationContext(), ThermometerScreen.class);
                startActivity(objtemp);
                break;
            case R.id.tv_header_pulseoximeter:
                Intent objpulse = new Intent(getApplicationContext(), OxygenActivity.class);
                startActivity(objpulse);
                break;
            case R.id.tv_header_bloodpressure:
                Intent objbp = new Intent(getApplicationContext(), BpLoginScreen.class);
                startActivity(objbp);
                break;
            case R.id.tv_header_hemoglobin:
                Intent objhemo = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(objhemo);
                break;
            case R.id.tv_header_bloodsugar:
                Intent objsugar = new Intent(getApplicationContext(), GlucoseScanListActivity.class);
                startActivity(objsugar);
                break;
            case R.id.txtmainprinter:
                Intent objprint = new Intent(getApplicationContext(), PrintPriviewScreen.class);
                startActivity(objprint);
                break;
            case R.id.txtprintersetup:
                Intent objprintersetup = new Intent(getApplicationContext(), Act_Main.class);
                startActivity(objprintersetup);
                break;
        }
    }
}
