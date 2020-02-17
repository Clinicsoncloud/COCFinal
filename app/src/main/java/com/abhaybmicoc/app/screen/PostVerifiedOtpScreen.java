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
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.services.HttpService;
import com.android.volley.Request;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.services.TextToSpeechService;

import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;

import java.util.Map;
import java.util.Locale;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONException;

public class PostVerifiedOtpScreen extends AppCompatActivity {

    // region Variables

    private Context context = PostVerifiedOtpScreen.this;

    private String OTP_MESSAGE = "" ;

    private String kioskId;
    private String mobileNumber;

    private EditText etOTP;
    private Button btnVerify;
    private TextToSpeech textToSpeech;
    private ProgressDialog progressDialog;

    TextToSpeechService textToSpeechService;

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

        textToSpeechService.stopTextToSpeech();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        textToSpeechService.speakOut(OTP_MESSAGE);
    }


    // endregion

    // region Initialization methods

    /**
     *
     */
    private void setupUI() {
        setContentView(R.layout.activity_post_verified_otp_screen);

        etOTP = findViewById(R.id.et_otp);
        btnVerify = findViewById(R.id.btn_verify);
        kioskId = getIntent().getStringExtra(Constant.Fields.KIOSK_ID);
        mobileNumber = getIntent().getStringExtra(Constant.Fields.MOBILE_NUMBER);
        OTP_MESSAGE =  getResources().getString(R.string.otp_msg);
    }

    /**
     *
     */
    private void setupEvents() {
        btnVerify.setOnClickListener(v -> verifyOtp());
    }

    private void initializeLogic() {
        textToSpeechService = new TextToSpeechService(getApplicationContext(), OTP_MESSAGE);
    }

    // endregion

    // region Logical methods

    /**
     *
     */
    private void goBack() {
        context.startActivity(new Intent(this, OtpLoginScreen.class));
    }

    /**
     *
     */
    private void verifyOtp() {
        if (etOTP.getText().toString().equals("")) {
            etOTP.setError("Please Enter OTP");
        } else {
            verifyOtpFromAPI();
        }
    }

    /**
     *
     */
    private void writeToPersonalSharedPreference(String key, String value) {
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


        Map<String, String> params;

        params = new HashMap<>();

        params.put(Constant.Fields.KIOSK_ID, kioskId);
        params.put("mobile", mobileNumber);
        params.put("otp", etOTP.getText().toString());

        Map<String, String> headerParams;
        headerParams = new HashMap<>();

        HttpService.accessWebServices(
                context,
                ApiUtils.VERIFY_OTP_URL,
                params, headerParams,
                (response, error, status) -> handleAPIResponse(response, error, status));
    }

    private void handleAPIResponse(String response, VolleyError error, String status) {
        if (status.equals("response")) {
            try {
                JSONObject responseObject = new JSONObject(response);

                writeToPersonalSharedPreference(Constant.Fields.TOKEN, responseObject.getJSONObject("data").getString(Constant.Fields.TOKEN));

                Intent objIntent = new Intent(getApplicationContext(), OtpVerifyScreen.class);
                objIntent.putExtra(Constant.Fields.MOBILE_NUMBER, mobileNumber);
                startActivity(objIntent);
            } catch (Exception e) {
                // TODO: Handle exception
            }
        } else if (status.equals("error")) {
            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            // TODO: Handle error
        }
    }

    // endregion
}
