package com.abhaybmi.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.abhaybmi.app.entities.AndMedical_App_Global;
import com.abhaybmi.app.heightweight.HeightScreen;
import com.abhaybmi.app.utils.ApiUtils;
import com.abhaybmi.app.utils.Tools;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PostVerifiedOtpScreen extends AppCompatActivity {

    String Id, MobileNo;
    Button btnVerify;
    EditText etOTP;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_verified_otp_screen);
        init();
    }

    public void init() {
        Id = getIntent().getStringExtra("kioskid");
        MobileNo = getIntent().getStringExtra("mobile");
        etOTP = findViewById(R.id.etOTP);
        btnVerify = findViewById(R.id.btnLogin);

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

}
