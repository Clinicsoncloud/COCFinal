package com.abhaybmicoc.app.screen;

import android.util.Log;
import android.os.Bundle;
import android.widget.Button;
import android.text.Editable;
import android.content.Intent;
import android.widget.EditText;
import android.content.Context;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.app.ProgressDialog;
import android.speech.tts.TextToSpeech;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.utils.Utils;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;

import com.android.volley.Request;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.Map;
import java.util.Locale;
import java.util.HashMap;

import static com.abhaybmicoc.app.utils.ApiUtils.PREFERENCE_THERMOMETERDATA;

public class OtpLoginScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // region Variables

    private Button btnLogin;
    private EditText etMobileNumber;
    private TextToSpeech textTopSpeech;
    private ProgressDialog progressDialog;

    private String kiosk_id;
    private String WELCOME_LOGIN_MESSAGE = "Welcome to Clinics on Cloud Please Enter Mobile Number";

    final int MOBILE_NUMBER_MAX_LENGTH = 10; //max length of your text

    SharedPreferences userData, activator;

    // endregion

    // region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopTextToSpeech();
    }

    @Override
    protected void onResume() {
        super.onResume();

        speakOut(WELCOME_LOGIN_MESSAGE);
    }

    @Override
    public void onInit(int status) {
        checkIfTextToSpeechIsActivated(status);
    }

    // endregion

    // region Initialization methods

    /**
     *
     */
    private void setupUI(){
        setContentView(R.layout.activity_otp_login_screen);

        btnLogin = findViewById(R.id.btn_submit);
        etMobileNumber = findViewById(R.id.et_mobile_number);
    }

    /**
     *
     */
    private void setupEvents(){
        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence txtWatcherStr, int start, int before, int count) {
                if (count == MOBILE_NUMBER_MAX_LENGTH) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etMobileNumber.getWindowToken(), 0);
                }
            }
        });

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void initializeData(){
        textTopSpeech = new TextToSpeech(getApplicationContext(), this);

        speakOut(WELCOME_LOGIN_MESSAGE);

        try {
            activator = getSharedPreferences(ApiUtils.PREFERENCE_ACTIVATOR, MODE_PRIVATE);
            kiosk_id = activator.getString("pinLock", "");
        } catch (Exception e) {
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etMobileNumber, InputMethodManager.SHOW_IMPLICIT);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(MOBILE_NUMBER_MAX_LENGTH);
        etMobileNumber.setFilters(filterArray);

        clearDatabase();
    }

    // endregion

    // region Logical methods

    /**
     *
     * @param text
     */
    private void speakOut(String text) {
        textTopSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     *
     */
    private void checkIfTextToSpeechIsActivated(int status){
        if (status == TextToSpeech.SUCCESS) {
            int result = textTopSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("textTopSpeech", "This Language is not supported");
            } else {
                speakOut(WELCOME_LOGIN_MESSAGE);
            }

        } else {
            Log.e("textTopSpeech", "Initilization Failed!");
        }
    }

    /**
     *
     */
    private void stopTextToSpeech(){
        try {
            if (textTopSpeech != null) {
                textTopSpeech.stop();
                textTopSpeech.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }
    }

    /**
     *
     */
    private void doLogin(){
        if (Utils.getInstance().giveLocationPermission(this)) {
            if (etMobileNumber.getText().toString().equals("")) {
                etMobileNumber.setError("Please Enter Mobile Number");
            } else if (etMobileNumber.getText().toString().length() < 10) {
                etMobileNumber.setError("Please Enter Valid Mobile Number");
            } else {
                GenerateOTP();
            }
        }
    }

    /**
     *
     */
    private void clearDatabase() {
        clearSharedPreference(ApiUtils.PREFERENCE_URL);
        clearSharedPreference(ApiUtils.PREFERENCE_PULSE);
        clearSharedPreference(PREFERENCE_THERMOMETERDATA);
        clearSharedPreference(ApiUtils.PREFERENCE_ACTOFIT);
        clearSharedPreference(ApiUtils.PREFERENCE_BIOSENSE);
        clearSharedPreference(ApiUtils.PREFERENCE_NEWRECORD);
        clearSharedPreference(ApiUtils.PREFERENCE_HEMOGLOBIN);
        clearSharedPreference(ApiUtils.PREFERENCE_BLOODPRESSURE);
    }

    private void clearSharedPreference(String preferenceName){
        getSharedPreferences(preferenceName, MODE_PRIVATE).edit().clear().commit();
    }

    // endregion

    // region API methods

    /**
     *
     */
    private void GenerateOTP() {
        progressDialog = Tools.progressDialog(OtpLoginScreen.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUtils.LOGIN_URL, response -> {
            try {
                progressDialog.dismiss();

                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getJSONObject("data").getJSONArray("patient").length() == 0) {
                    Intent objIntent = new Intent(getApplicationContext(), PostVerifiedOtpScreen.class);

                    objIntent.putExtra("mobile", etMobileNumber.getText().toString());
                    objIntent.putExtra("kioskid", kiosk_id);
                    startActivity(objIntent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                    finish();
                } else {
                    writePersonalSharedPreferences(jsonResponse);

                    Intent objIntent = new Intent(getApplicationContext(), PostVerifiedOtpScreen.class);
                    objIntent.putExtra("mobile", etMobileNumber.getText().toString());
                    objIntent.putExtra("kioskid", kiosk_id);
                    startActivity(objIntent);
                    finish();
                }

            } catch (JSONException e) {

            }
        }, volleyError -> {
            progressDialog.dismiss();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params;
                params = new HashMap<>();
                params.put("kiosk_id", kiosk_id);
                params.put("mobile", etMobileNumber.getText().toString());
                return params;
            }
        };

        AndMedical_App_Global.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(90000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     *
     * @param jsonResponse
     * @throws JSONException
     */
    private void writePersonalSharedPreferences(JSONObject jsonResponse) throws JSONException{
        SharedPreferences sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesPersonal.edit();

        editor.putString("name", jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("name"));
        editor.putString("mobile_number", jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("mobile"));
        editor.putString("email", jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("email"));
        editor.putString("dob", jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("dob"));
        editor.putString("token", jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("token"));
        editor.putString("gender", jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("gender"));
        editor.putString("id", jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("id"));

        editor.commit();
    }

    // endregion
}