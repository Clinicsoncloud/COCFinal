package com.abhaybmicoc.app.screen;

import android.util.Log;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.EditText;
import android.app.ProgressDialog;
import android.speech.tts.TextToSpeech;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.Constant;
import com.android.volley.Request;

import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;

import java.util.Map;
import java.util.Locale;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONException;

public class PostVerifiedOtpScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // region Variables

    private Context context = PostVerifiedOtpScreen.this;

    private final String OTP_MESSAGE = "Please Enter Otp";

    private String kioskId;
    private String mobileNumber;

    private EditText etOTP;
    private Button btnVerify;
    private TextToSpeech textToSpeech; //CHANGE IT
    private ProgressDialog progressDialog;

    // endregion

    // region Event methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeLogic();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopTextToSpeech();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        speakOut(OTP_MESSAGE);
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
    private void setupUI(){
        setContentView(R.layout.activity_post_verified_otp_screen);

        etOTP = findViewById(R.id.et_otp);
        btnVerify = findViewById(R.id.btn_verify);
        kioskId = getIntent().getStringExtra(Constant.Fields.KIOSK_ID);
        mobileNumber = getIntent().getStringExtra(Constant.Fields.MOBILE_NUMBER);
    }

    /**
     *
     */
    private void setupEvents(){
        btnVerify.setOnClickListener(v -> {
            verifyOtp();
        });
    }

    private void initializeLogic(){
        textToSpeech = new TextToSpeech(getApplicationContext(),this);
        speakOut(OTP_MESSAGE);
    }

    // endregion

    // region Logical methods

    /**
     *
     */
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     *
     */
    private void goBack(){
        context.startActivity(new Intent(this,OtpLoginScreen.class));
    }

    /**
     *
     */
    private void stopTextToSpeech(){
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException" + e.getMessage());
        }
    }

    private void startTextToSpeech(int status){
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(OTP_MESSAGE);
            }

        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    /**
     *
     */
    private void verifyOtp(){
        if (etOTP.getText().toString().equals("")) {
            etOTP.setError("Please Enter OTP");
        } else {
            verifyOtpFromAPI();
        }
    }

    /**
     *
     */
    private void writeToPersonalSharedPreference(String key, String value){
        SharedPreferences sharedPreference = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // endregion

    // region API methods

    /**
     *
     */
    private void verifyOtpFromAPI() {
        progressDialog = Tools.progressDialog(PostVerifiedOtpScreen.this);

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, ApiUtils.VERIFY_OTP_URL,
                jsonObject -> {
                    progressDialog.dismiss();

                    try {
                        JSONObject jobj = new JSONObject(jsonObject);

                        writeToPersonalSharedPreference(Constant.Fields.TOKEN, jobj.getJSONObject("data").getString(Constant.Fields.TOKEN));

                        Intent objIntent = new Intent(getApplicationContext(), OtpVerifyScreen.class);
                        objIntent.putExtra(Constant.Fields.MOBILE_NUMBER, mobileNumber);
                        startActivity(objIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> {
                    Log.d("", "onErrorResponse: " + volleyError.toString());
                    progressDialog.dismiss();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params;

                params = new HashMap<>();
                params.put(Constant.Fields.KIOSK_ID, kioskId);
                params.put(Constant.Fields.MOBILE_NUMBER, mobileNumber);
                params.put(Constant.Fields.OTP, etOTP.getText().toString());

                return params;
            }
        };

        AndMedical_App_Global.getInstance().addToRequestQueue(jsonObjectRequest);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    // endregion
}
