package com.abhaybmicoc_test.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.abhaybmicoc_test.app.entities.AndMedical_App_Global;
import com.abhaybmicoc_test.app.utils.ApiUtils;
import com.abhaybmicoc_test.app.utils.Tools;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostVerifiedOtpScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {

    String Id, MobileNo;
    Button btnVerify;
    EditText etOTP;
    ProgressDialog pd;

    TextToSpeech tts;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_verified_otp_screen);
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close the tts engine to avoide the runtime exception from it
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
//            Toast.makeText(getApplicationContext(), "TTS Stoped", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        context.startActivity(new Intent(this,OtpLoginScreen.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        speakOut();
    }

    public void init() {
        Id = getIntent().getStringExtra("kioskid");
        MobileNo = getIntent().getStringExtra("mobile");
        etOTP = findViewById(R.id.etOTP);
        btnVerify = findViewById(R.id.btnLogin);

        context = PostVerifiedOtpScreen.this;

        tts = new TextToSpeech(getApplicationContext(),this);

        //voice cmd for user to enter otp for verify
        speakOut();

        btnVerify.setOnClickListener(v -> {
            if (etOTP.getText().toString().equals("")) {
                etOTP.setError("Please Enter OTP");
            } else {
                verifyOtp();
            }
        });
    }

    private void verifyOtp() {
        pd = Tools.progressDialog(PostVerifiedOtpScreen.this);
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, ApiUtils.VERIFYOTP_URL,
                jsonObject1 -> {
                    System.out.println("Response is" + jsonObject1);
                    pd.dismiss();

                    try {
                        JSONObject jobj = new JSONObject(jsonObject1);
                        SharedPreferences objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
                        SharedPreferences.Editor editor = objdoctor.edit();
                        editor.putString("token", jobj.getJSONObject("data").getString("token"));
                        editor.commit();
                        Intent objIntent = new Intent(getApplicationContext(), OtpVerifyScreen.class);
                        objIntent.putExtra("mobile", MobileNo);
                        startActivity(objIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> {
                    Log.d("", "onErrorResponse: " + volleyError.toString());
                    pd.dismiss();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params;
                params = new HashMap<>();
                params.put("kiosk_id", Id);
                params.put("mobile", MobileNo);
                params.put("otp", etOTP.getText().toString());
                return params;
            }
        };
        AndMedical_App_Global.getInstance().addToRequestQueue(jsonObjectRequest);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

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
        String text = "Please Enter Otp";
//        String text = "StartActivity me aapka swagat hain kripaya next button click kre aur aage badhe";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
