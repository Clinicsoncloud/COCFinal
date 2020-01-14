package com.abhaybmicoc.app.screen;

import android.util.Log;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.app.ProgressDialog;
import android.app.DatePickerDialog;
import android.speech.tts.TextToSpeech;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;

import com.android.volley.Request;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.Map;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class OtpVerifyScreen extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // region Variables

    private Context context = OtpVerifyScreen.this;

    private int day;
    private int year;
    private int month;
    private int selectedGenderId;

    private final String FILL_REGISTRATION_MESSAGE = "Please Enter Registration detail";

    private Button btnLogin;

    private EditText etName;
    private EditText etEmail;
    private EditText etDateOfBirth;
    private EditText etMobileNumber;

    private ProgressDialog pd;

    private SharedPreferences sharedPreferencesToken;

    private RadioButton rdMale;
    private RadioButton rdFemale;
    private RadioButton rdGender;
    private RadioGroup rdGenderGroup;

    private  SimpleDateFormat EEEddMMMyyyyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private TextToSpeech tts;
    private BluetoothAdapter mBluetoothAdapter;

    // endregion

    // region Event methods

    @Override
    public void onBackPressed() {
        goToOtpLoginScreen();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify_screen);

        setupUI();
        setupEvents();
        initializeData();
        enableBluetooth();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tts = new TextToSpeech(this,this);

        speakOut(FILL_REGISTRATION_MESSAGE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopTextToSpeech();
    }

    @Override
    public void onInit(int status) {
        startTextToSpeech(status);
    }

    // endregion

    // region Initialization methods

    public void setupUI() {
        btnLogin = findViewById(R.id.btn_login);

        rdMale = findViewById(R.id.rd_male);
        rdFemale = findViewById(R.id.rd_female);
        rdGender = findViewById(selectedGenderId);
        rdGenderGroup = findViewById(R.id.rd_gender);

        selectedGenderId = rdGenderGroup.getCheckedRadioButtonId();

        context = OtpVerifyScreen.this;

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email_id);
        etDateOfBirth = findViewById(R.id.et_date_of_birth);
        etMobileNumber = findViewById(R.id.et_mobile_number);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etMobileNumber, InputMethodManager.SHOW_IMPLICIT);

        tts = new TextToSpeech(getApplicationContext(), this);

        try {
            sharedPreferencesToken = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

            etName.setText(sharedPreferencesToken.getString("name", ""));
            etDateOfBirth.setText(sharedPreferencesToken.getString("dob", ""));
            etMobileNumber.setText(getIntent().getStringExtra("mobile"));

            if (sharedPreferencesToken.getString("email", "").equalsIgnoreCase("null"))
                etEmail.setText("");
            else
                etEmail.setText(sharedPreferencesToken.getString("email", ""));

            initializeGender();
        } catch (Exception e) {

        }
    }

    private void setupEvents(){
        etDateOfBirth.setOnClickListener(v -> {
            showDateOfBirthPicker();
        });

        btnLogin.setOnClickListener(v -> {
            if (etMobileNumber.getText().toString().equals("")) {
                etMobileNumber.setError("Please Enter Mobile Number");
            } else if (etMobileNumber.getText().toString().length() < 10) {
                etMobileNumber.setError("Please Enter Valid Mobile Number");
            } else if (etName.getText().toString().equals("")) {
                etName.setError("Please Enter Name");
            } else if (etDateOfBirth.getText().toString().equals("")) {
                etDateOfBirth.setError("Please Select Date Of Birth");
            } else {
                postData();
            }
        });

        rdGenderGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            selectedGenderId = rdGenderGroup.getCheckedRadioButtonId();
            rdGender = findViewById(selectedGenderId);

            writeToPersonalSharedPreference("gender", rdGender.getText().toString());
        });
    }

    private void initializeData(){
        speakOut(FILL_REGISTRATION_MESSAGE);
    }

    // endregion

    private void showDateOfBirthPicker() {
        etDateOfBirth.setEnabled(false);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        showDatePickerDialog();
    }

    /**
     *
     */
    private void showDatePickerDialog(){
        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calander = Calendar.getInstance();
                calander.setTimeInMillis(0);
                calander.set(year, monthOfYear, dayOfMonth, 0, 0, 0);

                Date selectedDate = calander.getTime();
                String dateTimeFormatUS = EEEddMMMyyyyFormat.format(selectedDate);
                etDateOfBirth.setText(dateTimeFormatUS);
                etDateOfBirth.setEnabled(true);
            }
        };

        DatePickerDialog dpDialog = new DatePickerDialog(OtpVerifyScreen.this, listener, year, month + 1, day);

        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpDialog.setCancelable(false);

        dpDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                etDateOfBirth.setEnabled(true);
            }
        });

        dpDialog.show();
    }

    private void postData() {
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

                        Intent objIntent = new Intent(getApplicationContext(), HeightActivity.class);
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
                String bearer = "Bearer ".concat(sharedPreferencesToken.getString("token", ""));
                headers.put("Authorization", bearer);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params;
                params = new HashMap<>();
                params.put("name", etName.getText().toString());
                params.put("email", etEmail.getText().toString());
                params.put("dob", etDateOfBirth.getText().toString());
                params.put("gender", getSelectedGender());
                return params;
            }

        };
        AndMedical_App_Global.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    // region Logical methods

    /**
     *
     */
    private void goToOtpLoginScreen() {
        context.startActivity(new Intent(OtpVerifyScreen.this, OtpLoginScreen.class));
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

    /**
     *
     */
    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     *
     */
    private void startTextToSpeech(int status){
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(FILL_REGISTRATION_MESSAGE);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    /**
     *
     */
    private void stopTextToSpeech(){
        /* close the tts engine to avoid the runtime exception from it */
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }
    }

    /**
     *
     */
    public void enableBluetooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }
    }

    /**
     *
     */
    private void initializeGender(){
        if (sharedPreferencesToken.getString("gender", "").equalsIgnoreCase("male")) {
            rdMale.setChecked(true);

            writeToPersonalSharedPreference("gender", getSelectedGender());
        } else if (sharedPreferencesToken.getString("gender", "").equalsIgnoreCase("female")) {
            rdFemale.setChecked(true);

            writeToPersonalSharedPreference("gender", getSelectedGender());
        }
    }

    private String getSelectedGender(){
        int radioButtonID = rdGenderGroup.getCheckedRadioButtonId();

        RadioButton radioButton = rdGenderGroup.findViewById(radioButtonID);
        return radioButton.getText().toString();
    }

    // endregion
}

