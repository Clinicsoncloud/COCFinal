package com.abhaybmicoc.app.screen;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.activity.SplashActivity;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.skyfishjy.library.RippleBackground;

import java.util.List;

public class ActivateScreen extends AppCompatActivity {

    private EditText etPin;
    private Button btnSubmit;
    private SharedPreferences sp;
    private String activatorKey;
    List<Integer> imageList;
    RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_screen);
        init();
    }

    public void init() {
        try {
            sp = getSharedPreferences(ApiUtils.PREFERENCE_ACTIVATOR, MODE_PRIVATE);
            activatorKey = sp.getString("pinLock", "");
            if (activatorKey.equals("")) {
                Toast.makeText(getApplicationContext(), "Activate The Machine", Toast.LENGTH_SHORT).show();
            } else {
                Intent objIntent = new Intent(getApplicationContext(), SplashActivity.class);
                startActivity(objIntent);
                finish();
            }

        } catch (Exception e) {

        }

        etPin = findViewById(R.id.etPin);
        btnSubmit = findViewById(R.id.procedurebtn);

        rippleBackground = findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

        btnSubmit.setOnClickListener(view -> {
            sp = getSharedPreferences(ApiUtils.PREFERENCE_ACTIVATOR, MODE_PRIVATE);
            // Writing data to SharedPreferences
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("pinLock", etPin.getText().toString());
            editor.commit();
            Intent objIntent = new Intent(getApplicationContext(), SplashActivity.class);
            startActivity(objIntent);
            finish();
        });
    }
}
