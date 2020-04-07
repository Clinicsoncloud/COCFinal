package com.abhaybmicoc.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.utils.ApiUtils;

import java.util.ArrayList;
import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {

    Context context = ChangeLanguageActivity.this;

    Spinner spnSelectLanguage;
    Button btnChangeLanguage;
    ImageView ivBack;

    SharedPreferences sharedPreferenceLanguage;
    ArrayList<String> languagesList;
    String selected_language = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setupUI();
        initializeData();
        setupEvents();

    }

    private void setupUI() {
        setContentView(R.layout.activity_change_language);
    }

    private void initializeData() {

        spnSelectLanguage = findViewById(R.id.spn_SelectLanguage);
        btnChangeLanguage = findViewById(R.id.btn_ChangeLanguage);
        ivBack = findViewById(R.id.iv_Back);

        try {
            sharedPreferenceLanguage = getSharedPreferences(ApiUtils.PREFERENCE_LANGUAGE, MODE_PRIVATE);
        } catch (Exception e) {
        }

        languagesList = new ArrayList<>();
        languagesList.add("Select Language");
        languagesList.add("English");
        languagesList.add("Hindi");
        languagesList.add("Marathi");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<String>(context.getApplicationContext(),
                R.layout.simple_item_selected, languagesList);
        dataAdapter.setDropDownViewResource(R.layout.simple_item);
        spnSelectLanguage.setAdapter(dataAdapter);


        if (sharedPreferenceLanguage.getString("language", "").equals("en")) {
            spnSelectLanguage.setSelection(1);
        } else if (sharedPreferenceLanguage.getString("language", "").equals("hi")) {
            spnSelectLanguage.setSelection(2);
        } else if (sharedPreferenceLanguage.getString("language", "").equals("mar")) {
            spnSelectLanguage.setSelection(3);
        }
    }

    private void setupEvents() {

        ivBack.setOnClickListener(view -> goToBack());

        spnSelectLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position != 0)
                    selected_language = spnSelectLanguage.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(context, "No preffered Language selected", Toast.LENGTH_SHORT).show();
            }
        });

        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!selected_language.equals("")) {
                    setLanguageSelection();
                }
            }
        });
    }

    private void goToBack() {
        startActivity(new Intent(context, OtpLoginScreen.class));
        finish();
    }

    private void setLanguageSelection() {
        if (selected_language.equals("English")) {
            setLocale("en");
            Toast.makeText(context, "English selected", Toast.LENGTH_SHORT).show();
        } else if (selected_language.equals("Hindi")) {
            setLocale("hi");
            Toast.makeText(context, "Hindi Selected", Toast.LENGTH_SHORT).show();
        } else if (selected_language.equals("Marathi")) {
            setLocale("mar");
            Toast.makeText(context, "Marathi Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //saving data to shared preference
        SharedPreferences.Editor editor = sharedPreferenceLanguage.edit();
        editor.putString("language", lang);
        editor.apply();

        goToBack();
    }

    @Override
    public void onBackPressed() {
        goToBack();
    }
}
