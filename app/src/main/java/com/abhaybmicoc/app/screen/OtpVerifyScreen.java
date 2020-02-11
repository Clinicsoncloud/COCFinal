package com.abhaybmicoc.app.screen;

import android.annotation.TargetApi;
import android.os.Build;
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
import android.content.SharedPreferences;
import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.hemoglobin.util.AppUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.Tools;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.activity.HeightActivity;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;

import com.android.volley.Request;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
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
    private final String MARATHI_FILL_REGISTRATION_MESSAGE = "कृपया आपली माहिती भरा";

    private Button btnLogin;

    private EditText etName;
    private EditText etEmail;
    private EditText etDateOfBirth;
    private EditText etMobileNumber;

    private RadioButton rdMale;
    private RadioButton rdFemale;
    private RadioButton rdGender;
    private RadioGroup rdGenderGroup;

    private ProgressDialog progressDialog;

    private TextToSpeech textToSpeech;
    private BluetoothAdapter mBluetoothAdapter;
    private SharedPreferences sharedPreferencesPersonal;

    // endregion

    // region Event methods

    @Override
    public void onBackPressed() {
        goToOtpLoginScreen();

        clearPersonalInformation();
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

//        speakOut(FILL_REGISTRATION_MESSAGE);
        speakOut(MARATHI_FILL_REGISTRATION_MESSAGE);
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
    }

    private void setupEvents() {
        etDateOfBirth.setOnClickListener(v -> showDateOfBirthPicker());

        btnLogin.setOnClickListener(v -> {
            if (etMobileNumber.getText().toString().equals("")) {
                etMobileNumber.setError("Please Enter Mobile Number");
                etMobileNumber.requestFocus();
            } else if (etMobileNumber.getText().toString().length() < 10) {
                etMobileNumber.setError("Please Enter Valid Mobile Number");
                etMobileNumber.requestFocus();
            } else if (etName.getText().toString().equals("")) {
                etName.setError("Please Enter Name");
                etName.requestFocus();
            } else if (etDateOfBirth.getText().toString().equals("")) {
                etDateOfBirth.setError("Please Select Date Of Birth");
                etDateOfBirth.requestFocus();
                Toast.makeText(context, "Please select Date Of Birth", Toast.LENGTH_SHORT).show();
            } else if (rdGenderGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(context, "Please select the gender", Toast.LENGTH_SHORT).show();
            } else {
                postData();
            }
        });

        rdGenderGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            selectedGenderId = rdGenderGroup.getCheckedRadioButtonId();
            rdGender = findViewById(selectedGenderId);

            writeToPersonalSharedPreferenceKey(Constant.Fields.GENDER, rdGender.getText().toString());
        });
    }

    private void initializeData() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etMobileNumber, InputMethodManager.SHOW_IMPLICIT);

        textToSpeech = new TextToSpeech(getApplicationContext(), this);

        try {
            sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

            etName.setText(sharedPreferencesPersonal.getString(Constant.Fields.NAME, ""));
            etMobileNumber.setText(getIntent().getStringExtra(Constant.Fields.MOBILE_NUMBER));
            etDateOfBirth.setText(sharedPreferencesPersonal.getString(Constant.Fields.DATE_OF_BIRTH, ""));

            if (sharedPreferencesPersonal.getString(Constant.Fields.EMAIL, "").equalsIgnoreCase("null"))
                etEmail.setText("");
            else
                etEmail.setText(sharedPreferencesPersonal.getString(Constant.Fields.EMAIL, ""));

            initializeGender();
        } catch (Exception e) {
            // TODO: Handle exception
        }

//        speakOut(FILL_REGISTRATION_MESSAGE);
        speakOut(MARATHI_FILL_REGISTRATION_MESSAGE);
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
    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener listener = (datePicker, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(0);
            calendar.set(year, monthOfYear, dayOfMonth, 0, 0, 0);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String dateTimeFormatUS = simpleDateFormat.format(calendar.getTime());

            etDateOfBirth.setEnabled(true);
            etDateOfBirth.setText(dateTimeFormatUS);
        };

        DatePickerDialog dpDialog = new DatePickerDialog(OtpVerifyScreen.this, listener, year, month + 1, day);

        dpDialog.setCancelable(false);
        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dpDialog.setOnDismissListener(dialogInterface -> etDateOfBirth.setEnabled(true));

        dpDialog.show();
    }

    private void postData() {
        progressDialog = Tools.progressDialog(OtpVerifyScreen.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUtils.PROFILE_URL,
                response -> {
                    try {
                        progressDialog.dismiss();

                        JSONObject jsonObject = new JSONObject(response);

                        writeToPersonalSharedPreference(jsonObject);

                        Intent objIntent = new Intent(getApplicationContext(), HeightActivity.class);
                        startActivity(objIntent);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                        finish();


                    } catch (Exception e) {
                        // TODO: Handle exception here
                    }
                },
                volleyError -> {
                    progressDialog.dismiss();
                }) {
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();

                String bearer = "Bearer ".concat(sharedPreferencesPersonal.getString(Constant.Fields.TOKEN, ""));
                headers.put("Authorization", bearer);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put(Constant.Fields.NAME, etName.getText().toString());
                params.put(Constant.Fields.EMAIL, etEmail.getText().toString());
                params.put(Constant.Fields.DATE_OF_BIRTH, etDateOfBirth.getText().toString());
                params.put(Constant.Fields.GENDER, getSelectedGender());
                return params;
            }
        };

        AndMedical_App_Global.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));
    }

    // region Logical methods

    /**
     *
     */
    private void goToOtpLoginScreen() {
        context.startActivity(new Intent(OtpVerifyScreen.this, OtpLoginScreen.class));
    }

    /**
     * @param jsonObject
     * @throws JSONException
     */
    private void writeToPersonalSharedPreference(JSONObject jsonObject) throws JSONException {
        SharedPreferences sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesPersonal.edit();

        writeToPersonalSharedPreferenceKey(Constant.Fields.ID, jsonObject.getJSONObject("data").getJSONObject("patient").getString(Constant.Fields.ID));
        writeToPersonalSharedPreferenceKey(Constant.Fields.NAME, jsonObject.getJSONObject("data").getJSONObject("patient").getString(Constant.Fields.NAME));
        writeToPersonalSharedPreferenceKey(Constant.Fields.EMAIL, jsonObject.getJSONObject("data").getJSONObject("patient").getString(Constant.Fields.EMAIL));
        writeToPersonalSharedPreferenceKey(Constant.Fields.TOKEN, jsonObject.getJSONObject("data").getJSONObject("patient").getString(Constant.Fields.TOKEN));
        writeToPersonalSharedPreferenceKey(Constant.Fields.DATE_OF_BIRTH, jsonObject.getJSONObject("data").getJSONObject("patient").getString(Constant.Fields.DATE_OF_BIRTH));
        writeToPersonalSharedPreferenceKey(Constant.Fields.MOBILE_NUMBER, jsonObject.getJSONObject("data").getJSONObject("patient").getString(Constant.Fields.MOBILE_NUMBER));

        editor.commit();
    }

    /**
     *
     */
    private void writeToPersonalSharedPreferenceKey(String key, String value) {
        SharedPreferences sharedPreference = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     *
     */
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     *
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startTextToSpeech(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.forLanguageTag("mar"));

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
//                speakOut(FILL_REGISTRATION_MESSAGE);
                speakOut(MARATHI_FILL_REGISTRATION_MESSAGE);
            }

        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    /**
     *
     */
    private void stopTextToSpeech() {
        /* close the textToSpeech engine to avoid the runtime exception from it */
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        } catch (Exception e) {
            System.out.println("onPauseException" + e.getMessage());
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
    private void initializeGender() {
        if (sharedPreferencesPersonal.getString(Constant.Fields.GENDER, "").equalsIgnoreCase("male")) {
            rdMale.setChecked(true);

            writeToPersonalSharedPreferenceKey(Constant.Fields.GENDER, getSelectedGender());
        } else if (sharedPreferencesPersonal.getString(Constant.Fields.GENDER, "").equalsIgnoreCase("female")) {
            rdFemale.setChecked(true);

            writeToPersonalSharedPreferenceKey(Constant.Fields.GENDER, getSelectedGender());
        }
    }

    private String getSelectedGender() {
        int radioButtonID = rdGenderGroup.getCheckedRadioButtonId();

        RadioButton radioButton = rdGenderGroup.findViewById(radioButtonID);
        return radioButton.getText().toString();
    }

    private void clearPersonalInformation() {

        SharedPreferences.Editor sharedPreferencePersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE).edit().clear();
        sharedPreferencePersonalData.clear().apply();
    }

    // endregion
}

