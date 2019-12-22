package com.abhaybmi.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abhaybmi.app.entities.AndMedical_App_Global;
import com.abhaybmi.app.utils.ApiUtils;
import com.abhaybmi.app.utils.Tools;
import com.abhaybmi.app.utils.Utils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.abhaybmi.app.utils.ApiUtils.PREFERENCE_THERMOMETERDATA;

public class OtpLoginScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private Button btnLogin;
    private EditText etMobile;
    private ProgressDialog pd;
    private String kiosk_id;
    SharedPreferences userData, activator;
    private TextToSpeech tts;
    private long  startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_login_screen);
        tts = new TextToSpeech(this,this);
        init();
        clearDatabase();

    }

    @Override
    protected void onResume() {
        super.onResume();
        speakOut();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        //Disable the back button
    }

    public void init() {

        speakOut();

        btnLogin = findViewById(R.id.btnLogin);
        etMobile = findViewById(R.id.etMobile);
        try {
            activator = getSharedPreferences(ApiUtils.PREFERENCE_ACTIVATOR, MODE_PRIVATE);
            kiosk_id = activator.getString("pinLock", "");
            clearDatabase();
            SharedPreferences objAshok = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, Context.MODE_PRIVATE);
            objAshok.edit().clear().commit();
        } catch (Exception e) {

        }
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etMobile, InputMethodManager.SHOW_IMPLICIT);

        final int maxTextLength = 10;//max length of your text

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(maxTextLength);
        etMobile.setFilters(filterArray);

        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence txtWatcherStr, int start, int before, int count) {
                if (count == maxTextLength) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etMobile.getWindowToken(), 0);
                }
            }
        });

        btnLogin.setOnClickListener(v -> {
            System.out.println("start time = "+System.currentTimeMillis());
            startTime = System.currentTimeMillis();
            if (Utils.getInstance().giveLocationPermission(this)) {
                if (etMobile.getText().toString().equals("")) {
                    etMobile.setError("Please Enter Mobile Number");
                } else if (etMobile.getText().toString().length() < 10) {
                    etMobile.setError("Please Enter Valid Mobile Number");
                } else {
                    GenerateOTP();
                }
            }
        });
    }

    private void clearDatabase() {

        Log.e("clearing","SharedPrefernceData");

        SharedPreferences objBiosense = getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, Context.MODE_PRIVATE);
        SharedPreferences objBp = getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
        SharedPreferences objPulse = getSharedPreferences(ApiUtils.PREFERENCE_PULSE, MODE_PRIVATE);
        SharedPreferences objActofit = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
        SharedPreferences objHemoglobin = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
        SharedPreferences objNewRecord = getSharedPreferences(ApiUtils.PREFERENCE_NEWRECORD, MODE_PRIVATE);
        SharedPreferences objUrl = getSharedPreferences(ApiUtils.PREFERENCE_URL, MODE_PRIVATE);
        SharedPreferences objAshok = getSharedPreferences(PREFERENCE_THERMOMETERDATA, MODE_PRIVATE);
        objBiosense.edit().clear().commit();
        objBp.edit().clear().commit();
        objPulse.edit().clear().commit();
        objActofit.edit().clear().commit();
        objHemoglobin.edit().clear().commit();
        objNewRecord.edit().clear().commit();
        objUrl.edit().clear().commit();
        objAshok.edit().clear().commit();
    }

    private void GenerateOTP() {
        pd = Tools.progressDialog(OtpLoginScreen.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUtils.LOGIN_URL,
                response -> {
                    //Disimissing the progress dialog
                    System.out.println("Login Response" + response);
                    try {
                        pd.dismiss();
                        long elapsedTime = System.currentTimeMillis() - startTime;
                        System.out.println("total time = "+elapsedTime);
                        JSONObject jobj = new JSONObject(response);
                        if (jobj.getJSONObject("data").getJSONArray("patient").length() == 0) {
                            Intent objIntent = new Intent(getApplicationContext(), PostVerifiedOtpScreen.class);
                            objIntent.putExtra("mobile", etMobile.getText().toString());
                            objIntent.putExtra("kioskid", kiosk_id);
                            startActivity(objIntent);
                            finish();
                        } else {
                            SharedPreferences objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
                            SharedPreferences.Editor editor = objdoctor.edit();
                            editor.putString("name", jobj.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("name"));
                            editor.putString("mobile_number", jobj.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("mobile"));
                            editor.putString("email", jobj.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("email"));
                            editor.putString("dob", jobj.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("dob"));
                            editor.putString("token", jobj.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("token"));
                            editor.putString("gender", jobj.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("gender"));
                            editor.putString("id", jobj.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString("id"));
                            editor.commit();
                            Intent objIntent = new Intent(getApplicationContext(), PostVerifiedOtpScreen.class);
                            objIntent.putExtra("mobile", etMobile.getText().toString());
                            objIntent.putExtra("kioskid", kiosk_id);
                            startActivity(objIntent);
                            finish();
                        }

                    } catch (JSONException e) {

                    }
                },
                volleyError -> {
                    pd.dismiss();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params;
                params = new HashMap<>();
                params.put("kiosk_id", kiosk_id);
                params.put("mobile", etMobile.getText().toString());
                return params;
            }
        };
        AndMedical_App_Global.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    private void GenerateOtp() {
        pd = Tools.progressDialog(OtpLoginScreen.this);
        StringRequest jsonStringRequestt = new StringRequest(Request.Method.GET, ApiUtils.REGISTER_URL + etMobile.getText().toString(),
                jsonObject1 -> {
                    System.out.println("Response is" + jsonObject1);

                    pd.dismiss();

                    try {
                        JSONArray array = new JSONArray(jsonObject1);
                        if (array.length() > 0) {
                            String mobile_number = array.getJSONObject(array.length() - 1).getString("mobile_number");
                            String name = array.getJSONObject(array.length() - 1).getString("name");
                            String id = array.getJSONObject(array.length() - 1).getString("id");
                            String dob = array.getJSONObject(array.length() - 1).getString("dob");
                            String email = array.getJSONObject(array.length() - 1).getString("email");
                            String gender = array.getJSONObject(array.length() - 1).getString("gender");
                            Intent intent = new Intent(getApplicationContext(), OtpVerifyScreen.class);
                            intent.putExtra("mobile_number", mobile_number);
                            intent.putExtra("name", name);
                            intent.putExtra("id", id);
                            intent.putExtra("dob", dob);
                            intent.putExtra("email", email);
                            intent.putExtra("gender", gender);
                            userData = getSharedPreferences(ApiUtils.PREFERENCE_URL, MODE_PRIVATE);
                            // Writing data to SharedPreferences
                            SharedPreferences.Editor editor = userData.edit();
                            editor.putString("mobile_number", mobile_number);
                            editor.putString("name", name);
                            editor.putString("id", id);
                            editor.putString("dob", dob);
                            editor.putString("email", email);
                            editor.putString("gender", gender);
                            editor.commit();
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(getApplicationContext(), OtpVerifyScreen.class);
                            startActivity(intent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> {
                    Log.d("", "onErrorResponse: " + volleyError.toString());
                    pd.dismiss();
                }
        );
        AndMedical_App_Global.getInstance().addToRequestQueue(jsonStringRequestt);
        jsonStringRequestt.setRetryPolicy(new DefaultRetryPolicy(500000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            tts.setSpeechRate(1);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut() {
        String text = "Welcome to Clinics on Cloud, Please Enter Your Mobile Number";
//        String text = "StartActivity me aapka swagat hain kripaya next button click kre aur aage badhe";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
