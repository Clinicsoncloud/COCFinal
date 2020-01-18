package com.abhaybmicoc.app.screen;

import android.app.Service;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.interfaces.VolleyResponse;
import com.abhaybmicoc.app.services.AccessWebServices;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.utils.Utils;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;

import com.android.volley.Request;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.Map;
import java.util.Locale;
import java.util.HashMap;

public class OtpLoginScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // region Variables

    Context mContext;
    private Button btnLogin;
    private EditText etMobileNumber;
    private TextToSpeech textTopSpeech;
    private ProgressDialog progressDialog;

    private String kiosk_id;
    private String WELCOME_LOGIN_MESSAGE = "Welcome to Clinics on Cloud Please Enter Mobile Number";

    final int MOBILE_NUMBER_MAX_LENGTH = 10; //max length of your text

    private SharedPreferences activator;

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
        startTextToSpeech(status);
    }

    // endregion

    // region Initialization methods

    /**
     *
     */
    private void setupUI() {
        setContentView(R.layout.activity_otp_login_screen);
        mContext = OtpLoginScreen.this;

        btnLogin = findViewById(R.id.btn_login);
        etMobileNumber = findViewById(R.id.et_mobile_number);
    }

    /**
     *
     */
    private void setupEvents() {
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

    private void initializeData() {
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
     * @param text
     */
    private void speakOut(String text) {
        textTopSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     *
     */
    private void startTextToSpeech(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textTopSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("textTopSpeech", "This Language is not supported");
            } else {
                speakOut(WELCOME_LOGIN_MESSAGE);
            }

        } else {
            Log.e("textTopSpeech", "Initialization Failed!");
        }
    }

    /**
     *
     */
    private void stopTextToSpeech() {
        try {
            if (textTopSpeech != null) {
                textTopSpeech.stop();
                textTopSpeech.shutdown();
            }
        } catch (Exception e) {
            System.out.println("onPauseException" + e.getMessage());
        }
    }

    /**
     *
     */
    private void doLogin() {
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
        clearSharedPreference(ApiUtils.PREFERENCE_ACTOFIT);
        clearSharedPreference(ApiUtils.PREFERENCE_BIOSENSE);
        clearSharedPreference(ApiUtils.PREFERENCE_NEWRECORD);
        clearSharedPreference(ApiUtils.PREFERENCE_HEMOGLOBIN);
        clearSharedPreference(ApiUtils.PREFERENCE_BLOODPRESSURE);
        clearSharedPreference(ApiUtils.PREFERENCE_THERMOMETERDATA);
    }

    private void clearSharedPreference(String preferenceName) {
        getSharedPreferences(preferenceName, MODE_PRIVATE).edit().clear().commit();
    }

    // endregion

    // region API methods
    private void GenerateOTP() {

        Map<String, String> params;

        params = new HashMap<>();
        params.put("kiosk_id", kiosk_id);
        params.put("mobile", etMobileNumber.getText().toString());


        Map<String, String> headerParams;
        headerParams = new HashMap<>();


        AccessWebServices.accessWebServices(mContext, ApiUtils.LOGIN_URL, params, headerParams, new VolleyResponse() {

            @Override
            public void onProcessFinish(String response, VolleyError error, String status) {
                if (status.equals("response")) {
                    try {

                        JSONObject jsonResponse = new JSONObject(response);

                        if (jsonResponse.getJSONObject("data").getJSONArray("patient").length() == 0) {
                            Intent objIntent = new Intent(getApplicationContext(), PostVerifiedOtpScreen.class);

                            objIntent.putExtra(Constant.Fields.MOBILE_LOGIN, etMobileNumber.getText().toString());
                            objIntent.putExtra(Constant.Fields.KIOSK_ID, kiosk_id);

                            startActivity(objIntent);

                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);

                            finish();
                        } else {
                            writePersonalSharedPreferences(jsonResponse);

                            Intent objIntent = new Intent(getApplicationContext(), PostVerifiedOtpScreen.class);

                            objIntent.putExtra(Constant.Fields.MOBILE_LOGIN, etMobileNumber.getText().toString());
                            objIntent.putExtra(Constant.Fields.KIOSK_ID, kiosk_id);

                            startActivity(objIntent);

                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (status.equals("error")) {
                    Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * @param jsonResponse
     * @throws JSONException
     */
    private void writePersonalSharedPreferences(JSONObject jsonResponse) throws JSONException {
        SharedPreferences sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesPersonal.edit();

        editor.putString(Constant.Fields.ID, jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("id"));
        editor.putString(Constant.Fields.NAME, jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("name"));
        editor.putString(Constant.Fields.EMAIL, jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("email"));
        editor.putString(Constant.Fields.TOKEN, jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("token"));
        editor.putString(Constant.Fields.GENDER, jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("gender"));
        editor.putString(Constant.Fields.DATE_OF_BIRTH, jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("dob"));
        editor.putString(Constant.Fields.MOBILE_NUMBER, jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("mobile"));

        editor.commit();
    }

    // endregion
}