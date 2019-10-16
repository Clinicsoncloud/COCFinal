package com.abhaybmi.app.weight;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.abhaybmi.app.R;
import com.abhaybmi.app.actofitheight.ActofitMainActivity;
import com.abhaybmi.app.heightweight.HeightScreen;
import com.abhaybmi.app.oxygen.OxygenActivity;
import com.abhaybmi.app.thermometer.ThermometerScreen;

public class WeightScreen extends AppCompatActivity {

    Button btnstart, btnnext;
    Context ctx = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnstart = findViewById(R.id.btnstart);
        btnnext = findViewById(R.id.btnnext);

        btnstart.setOnClickListener(v -> {
            try {
                Intent objIntent = new Intent(getApplicationContext(), ActofitMainActivity.class);
                startActivity(objIntent);

            } catch (Exception e) {

            }

        });


        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent objIntent = new Intent(getApplicationContext(), ThermometerScreen.class);
                    startActivity(objIntent);
                    finish();

                } catch (Exception e) {

                }

            }
        });

    }

}
