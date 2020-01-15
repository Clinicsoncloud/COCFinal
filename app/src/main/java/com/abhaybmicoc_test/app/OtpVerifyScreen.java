package com.abhaybmicoc_test.app;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc_test.app.entities.AndMedical_App_Global;
import com.abhaybmicoc_test.app.heightweight.Principal;
import com.abhaybmicoc_test.app.utils.ApiUtils;
import com.abhaybmicoc_test.app.utils.Tools;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtpVerifyScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {
    Button btnLogin;
    TextView txtName, txtmobile, txtemail;
    EditText etMobile, etName, etDOB, etEmail;
    ProgressDialog pd;
    SharedPreferences sp, spToken;
    private RadioGroup genderradio;
    private RadioButton genderbutton, malebutton, femalebutton;
    BluetoothAdapter mBluetoothAdapter;
    int selectedId;
    private int day, month, year;
    public SimpleDateFormat EEEddMMMyyyyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    String gender = "";
    private TextToSpeech tts;
    private Context context;


    @Override
    public void onBackPressed() {
        context.startActivity(new Intent(OtpVerifyScreen.this, OtpLoginScreen.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify_screen);

        init();
        enableBluetooth();

        speakOut();

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etMobile, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        tts = new TextToSpeech(this,this);

        speakOut();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close the tts engine to avoide the runtime exception
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }
    }

    public void init() {

        btnLogin = findViewById(R.id.btnLogin);
        txtName = findViewById(R.id.txtName);
        txtmobile = findViewById(R.id.txtmobile);
        txtemail = findViewById(R.id.txtemail);
        genderradio = findViewById(R.id.genderradio);
        malebutton = findViewById(R.id.malebutton);
        femalebutton = findViewById(R.id.femalebutton);
        selectedId = genderradio.getCheckedRadioButtonId();
        genderbutton = findViewById(selectedId);

        context = OtpVerifyScreen.this;

        etMobile = findViewById(R.id.etMobile);
        etName = findViewById(R.id.etName);
        etDOB = findViewById(R.id.etDOB);
        etEmail = findViewById(R.id.etEmail);
//        System.out.println("====Data====aaaaaaaaaaaaaaaa" + genderbutton.getText().toString());

        tts = new TextToSpeech(getApplicationContext(), this);

        try {
            sp = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
            spToken = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);


            etMobile.setText(getIntent().getStringExtra("mobile"));
            etName.setText(spToken.getString("name", ""));
            etDOB.setText(spToken.getString("dob", ""));
            if (spToken.getString("email", "").equalsIgnoreCase("null"))
                etEmail.setText("");
            else
                etEmail.setText(spToken.getString("email", ""));


            if (spToken.getString("gender", "").equalsIgnoreCase("male")) {
                Log.e("gender_log", "" + spToken.getString("gender", ""));
                malebutton.setChecked(true);
                selectedId = malebutton.getId();
                genderbutton = findViewById(selectedId);
                SharedPreferences objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
                SharedPreferences.Editor editor = objdoctor.edit();
                editor.putString("gender", genderbutton.getText().toString());
                editor.commit();
            } else if (spToken.getString("gender", "").equalsIgnoreCase("female")) {
                Log.e("gender_log_in_female", "" + spToken.getString("gender", ""));
                femalebutton.setChecked(true);
                selectedId = femalebutton.getId();
                genderbutton = findViewById(selectedId);
                SharedPreferences objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
                SharedPreferences.Editor editor = objdoctor.edit();
                editor.putString("gender", genderbutton.getText().toString());
                editor.commit();
            }

            genderradio.setOnCheckedChangeListener((radioGroup, i) -> {
                selectedId = genderradio.getCheckedRadioButtonId();
                genderbutton = findViewById(selectedId);
                SharedPreferences objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
                SharedPreferences.Editor editor = objdoctor.edit();
                editor.putString("gender", genderbutton.getText().toString());
                editor.commit();
            });


        } catch (Exception e) {

        }


        etDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etDOB.setEnabled(false);
                showDialog();
            }
        });

        btnLogin.setOnClickListener(v -> {
            if (etMobile.getText().toString().equals("")) {
                etMobile.setError("Please Enter Mobile Number");
            } else if (etMobile.getText().toString().length() < 10) {
                etMobile.setError("Please Enter Valid Mobile Number");
            } else if (etName.getText().toString().equals("")) {
                etName.setError("Please Enter Name");
            } else if (etDOB.getText().toString().equals("")) {
                etDOB.setError("Please Select Date Of Birth");
            } else {
                PostData();
            }

        });
    }

    private void showDialog() {

        etDOB.setEnabled(false);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calander2 = Calendar.getInstance();
                calander2.setTimeInMillis(0);
                calander2.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                Date SelectedDate = calander2.getTime();
                DateFormat dateformat_US = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US);
                String StringDateformat_US = EEEddMMMyyyyFormat.format(SelectedDate);
                etDOB.setText(StringDateformat_US);
                etDOB.setEnabled(true);
            }
        };
        DatePickerDialog dpDialog = new DatePickerDialog(OtpVerifyScreen.this, listener, year, month + 1, day);
        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpDialog.setCancelable(false);
        dpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                etDOB.setEnabled(true);
            }
        });
        dpDialog.show();
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    private void registerUser() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile_number", etMobile.getText().toString());
            jsonObject.put("name", etName.getText().toString());
            jsonObject.put("dob", etDOB.getText().toString());
            jsonObject.put("email", etEmail.getText().toString());
            jsonObject.put("gender", genderbutton.getText().toString());
            jsonObject.put("author", 2);
            jsonObject.put("device", 2);
        } catch (Exception e) {
            e.printStackTrace();

        }
        pd = Tools.progressDialog(OtpVerifyScreen.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ApiUtils.POSTDATA_URL, jsonObject,
                jsonObject1 -> {

                    System.out.println("Response is" + jsonObject1.toString());

                    pd.dismiss();
                    try {
                        String id = jsonObject1.getString("id");
                        String mobile_number = jsonObject1.getString("mobile_number");
                        Toast.makeText(getApplicationContext(), "You Will receive Sms Shortly", Toast.LENGTH_SHORT).show();
                        Intent objIntent = new Intent(getApplicationContext(), PostVerifiedOtpScreen.class);
                        objIntent.putExtra("id", id);
                        objIntent.putExtra("mobile_number", mobile_number);
                        SharedPreferences objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
                        SharedPreferences.Editor editor = objdoctor.edit();
                        editor.putString("gender", genderbutton.getText().toString());
                        editor.commit();
                        startActivity(objIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> {
                    Log.d("", "onErrorResponse: " + volleyError.toString());
                    pd.dismiss();
                }
        );
        AndMedical_App_Global.getInstance().addToRequestQueue(jsonObjectRequest);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(90000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void PostData() {
        pd = Tools.progressDialog(OtpVerifyScreen.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUtils.PROFILE_URL,
                response -> {
                    //Disimissing the progress dialog
                    System.out.println("Login Response" + response);
                    try {
                        pd.dismiss();
                        JSONObject jobj = new JSONObject(response);
                        SharedPreferences objdoctor = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
                        SharedPreferences.Editor editor = objdoctor.edit();
                        editor.putString("name", jobj.getJSONObject("data").getJSONObject("patient").getString("name"));
                        editor.putString("mobile_number", jobj.getJSONObject("data").getJSONObject("patient").getString("mobile"));
                        editor.putString("email", jobj.getJSONObject("data").getJSONObject("patient").getString("email"));
                        editor.putString("dob", jobj.getJSONObject("data").getJSONObject("patient").getString("dob"));
                        editor.putString("token", jobj.getJSONObject("data").getJSONObject("patient").getString("token"));
                        editor.putString("id", jobj.getJSONObject("data").getJSONObject("patient").getString("id"));
                        editor.commit();
                        finish();

                        Intent objIntent = new Intent(getApplicationContext(), Principal.class);
                        startActivity(objIntent);
                        finish();

                    } catch (Exception e) {

                    }
                },
                volleyError -> {
                    pd.dismiss();
                }) {
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();
                String bearer = "Bearer ".concat(spToken.getString("token", ""));
                headers.put("Authorization", bearer);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params;
                params = new HashMap<>();
                params.put("name", etName.getText().toString());
                params.put("email", etEmail.getText().toString());
                params.put("dob", etDOB.getText().toString());
                params.put("gender", genderbutton.getText().toString());
                return params;
            }

        };
        AndMedical_App_Global.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void enableBluetooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
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
        String text = "Please Enter Registration detail";
//        String text = "StartActivity me aapka swagat hain kripaya next button click kre aur aage badhe";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}

