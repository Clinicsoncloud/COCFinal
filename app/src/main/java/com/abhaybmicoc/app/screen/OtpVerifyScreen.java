package com.abhaybmicoc.app.screen;

import android.content.ContentValues;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.os.Bundle;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.EditText;
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
import com.abhaybmicoc.app.activity.SelectTestOptionsActivity;
import com.abhaybmicoc.app.activity.SplashActivity;
import com.abhaybmicoc.app.database.DataBaseHelper;
import com.abhaybmicoc.app.model.Common_Update_Response;
import com.abhaybmicoc.app.model.Patient_Data;
import com.abhaybmicoc.app.model.Patient_Response;
import com.abhaybmicoc.app.services.DateService;
import com.abhaybmicoc.app.services.HttpService;
import com.abhaybmicoc.app.services.TextToSpeechService;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.DTU;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.activity.HeightActivity;

import com.abhaybmicoc.app.utils.Utils;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;

import java.util.Map;
import java.util.Locale;
import java.util.HashMap;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class OtpVerifyScreen extends AppCompatActivity {
    // region Variables

    private Context context = OtpVerifyScreen.this;

    private int day;
    private int year;
    private int month;
    private int selectedGenderId;
    private String patientID;

    private String FILL_REGISTRATION_MESSAGE = "";

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

    TextToSpeechService textToSpeechService;

    private String strConnectivity = "", strMobileNo = "", token = "";
    private DataBaseHelper dataBaseHelper;

    private BluetoothAdapter bluetoothAdapter;
    private String patient_id = "";

    // endregion

    // region Event methods

    @Override
    public void onBackPressed() {
        goToOtpLoginScreen();

        clearPersonalInformation();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify_screen);

        setupUI();
        getIntentData();
        setupEvents();
        initializeData();
        enableBluetooth();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (textToSpeechService != null)
            textToSpeechService.stopTextToSpeech();
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

        FILL_REGISTRATION_MESSAGE = getString(R.string.registration_msg);
    }

    private void getIntentData() {
        strConnectivity = getIntent().getStringExtra("connectivity");
        strMobileNo = getIntent().getStringExtra(Constant.Fields.MOBILE_NUMBER);
        patient_id = getIntent().getStringExtra(Constant.Fields.PATIENT_ID);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
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
                updatePatientInfo();

                if (Utils.isOnline(context)) {
                    if (strConnectivity.equals("online"))
                        postData();
                    else
                        Toast.makeText(context, "No Internet connection, Please Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rdGenderGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            selectedGenderId = rdGenderGroup.getCheckedRadioButtonId();
            rdGender = findViewById(selectedGenderId);

            writeToPersonalSharedPreferenceKey(Constant.Fields.GENDER, rdGender.getText().toString());
        });
    }

    private void initializeData() {

        dataBaseHelper = new DataBaseHelper(context);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etMobileNumber, InputMethodManager.SHOW_IMPLICIT);

        setupTextToSpeech();

        try {
            sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

            token = sharedPreferencesPersonal.getString(Constant.Fields.TOKEN, "");
            etName.setText(sharedPreferencesPersonal.getString(Constant.Fields.NAME, ""));
            etMobileNumber.setText(getIntent().getStringExtra(Constant.Fields.MOBILE_NUMBER));

            if (sharedPreferencesPersonal.getString(Constant.Fields.EMAIL, "").equalsIgnoreCase("null"))
                etEmail.setText("");
            else
                etEmail.setText(sharedPreferencesPersonal.getString(Constant.Fields.EMAIL, ""));

            if (sharedPreferencesPersonal.getString(Constant.Fields.DATE_OF_BIRTH, "") != null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    etDateOfBirth.setText(sharedPreferencesPersonal.getString(Constant.Fields.DATE_OF_BIRTH, ""));
                }

            initializeGender();
        } catch (Exception e) {
            // TODO: Handle exception
        }
    }

    private void setupTextToSpeech() {
        if (Utils.isOnline(context)) {
            textToSpeechService = new TextToSpeechService(getApplicationContext(), FILL_REGISTRATION_MESSAGE);
        }
    }

    // endregion

    private void showDateOfBirthPicker() {
//        etDateOfBirth.setEnabled(false);

        DTU.showDatePickerDialog(context, DTU.FLAG_OLD_AND_NEW, etDateOfBirth);

        /*final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        showDatePickerDialog();*/
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void postData() {

        Map<String, String> requestBodyParams = new HashMap<>();
        requestBodyParams.put(Constant.Fields.NAME, etName.getText().toString());
        requestBodyParams.put(Constant.Fields.EMAIL, etEmail.getText().toString());
        requestBodyParams.put(Constant.Fields.DATE_OF_BIRTH, DTU.get_yyyy_mm_dd_HMS(etDateOfBirth.getText().toString()));
        requestBodyParams.put(Constant.Fields.GENDER, getSelectedGender());
        requestBodyParams.put(Constant.Fields.CREATED_AT, DateService.getCurrentDateTime(DateService.YYYY_MM_DD_HMS));

        HashMap headersParams = new HashMap();

        String bearer = "Bearer ".concat(token);
        headersParams.put("Authorization", bearer);
        headersParams.put("app_version", SplashActivity.currentVersion);

        String updatePatientURL = ApiUtils.PATIENT_URL + "/" + sharedPreferencesPersonal.getString(Constant.Fields.PATIENT_ID, "");

        Log.e("reqBodyParams_Verify", ":" + requestBodyParams);
        Log.e("headersParams_Verify", ":" + headersParams);
        Log.e("updatePatientURL_Verify", ":" + updatePatientURL);

        HttpService.accessWebServices(
                context,
                updatePatientURL,
                Request.Method.PUT,
                requestBodyParams,
                headersParams,
                (response, error, status) -> handleAPIResponse(response, error, status));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleAPIResponse(String response, VolleyError error, String status) {

        Log.e("response_Verify", ":" + response);

        if (status.equals("response")) {
            try {

                Common_Update_Response common_update_response = (Common_Update_Response) Utils.parseResponse(response, Common_Update_Response.class);

                if (common_update_response.getSuccess()) {
                    getPatientData();
                }




                /*Intent objIntent = new Intent(getApplicationContext(), HeightActivity.class);
                startActivity(objIntent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                finish();*/

            } catch (Exception e) {
                // TODO: Handle exception
            }
        } else if (status.equals("error")) {
            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getPatientData() {

        Map<String, String> requestBodyParams = new HashMap<>();

        HashMap headersParams = new HashMap();

        String getPatientURL = ApiUtils.PATIENT_URL + "/" + sharedPreferencesPersonal.getString(Constant.Fields.PATIENT_ID, "");

        Log.e("getPatientURL_Log", ":" + getPatientURL);

        HttpService.accessWebServices(
                context,
                getPatientURL,
                Request.Method.GET,
                requestBodyParams,
                headersParams,
                (response, error, status) -> handleGetPatientAPIResponse(response, error, status));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleGetPatientAPIResponse(String response, VolleyError error, String status) {

        Log.e("getPatientRes_Log", ":" + response);

        if (status.equals("response")) {
            try {
                Patient_Response patient_response = (Patient_Response) Utils.parseResponse(response, Patient_Response.class);

                if (patient_response.getFound()) {
                    writeToPersonalSharedPreference(patient_response.getData(), "online");


                    if (!sharedPreferencesPersonal.getString(Constant.Fields.NAME, "").equals("")) {

                        startActivity(new Intent(context, SelectTestOptionsActivity.class));
//                    showReportOptionsPopUp();
                    } else {
                        Intent objIntent = new Intent(getApplicationContext(), HeightActivity.class);
                        startActivity(objIntent);
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                        finish();
                    }
                }

            } catch (Exception e) {
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updatePatientInfo() {

        try {
            ContentValues patientContentValues = new ContentValues();

            patientContentValues.put(Constant.Fields.NAME, etName.getText().toString());
            patientContentValues.put(Constant.Fields.EMAIL, etEmail.getText().toString());
            patientContentValues.put(Constant.Fields.DATE_OF_BIRTH, DTU.get_yyyy_mm_dd_HMS(etDateOfBirth.getText().toString()));
            patientContentValues.put(Constant.Fields.GENDER, getSelectedGender());

            dataBaseHelper.updatePatientInfo(Constant.TableNames.PATIENTS, patientContentValues, patient_id);

//            String patient_id = dataBaseHelper.lastInsertID(Constant.Fields.PATIENT_ID, Constant.TableNames.PATIENTS);
//            JSONObject resObject = new JSONObject();
//            JSONObject dataObject = new JSONObject();
//            JSONObject patientObject = new JSONObject();

            Patient_Data patient_data = new Patient_Data();

            patient_data.setId(patient_id);
            patient_data.setName(etName.getText().toString());
            patient_data.setEmail(etEmail.getText().toString());
            patient_data.setToken("");
            patient_data.setDob(etDateOfBirth.getText().toString());
            patient_data.setMobile(etMobileNumber.getText().toString());
            patient_data.setGender(getSelectedGender());

//            dataObject.put("patient", patientObject);
//            resObject.put("data", dataObject);

            writeToPersonalSharedPreference(patient_data, "offline");

        } catch (Exception e) {
        }
    }

    // region Logical methods

    /**
     *
     */
    private void goToOtpLoginScreen() {
        context.startActivity(new Intent(OtpVerifyScreen.this, OtpLoginScreen.class));
    }

    /**
     * @param
     * @param
     * @param
     * @throws JSONException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void writeToPersonalSharedPreference(Patient_Data patient_data, String networkStatus) {
        try {
            SharedPreferences sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesPersonal.edit();

//        if (jsonObject.has(Constant.Fields.PATIENT_ID))
            writeToPersonalSharedPreferenceKey(Constant.Fields.ID, patient_id);
//            writeToPersonalSharedPreferenceKey(Constant.Fields.PATIENT_ID, patient_data.getId());

            writeToPersonalSharedPreferenceKey(Constant.Fields.NAME, patient_data.getName());
            writeToPersonalSharedPreferenceKey(Constant.Fields.EMAIL, patient_data.getEmail());
            writeToPersonalSharedPreferenceKey(Constant.Fields.TOKEN, patient_data.getToken());

            if (networkStatus.equals("online"))
                writeToPersonalSharedPreferenceKey(Constant.Fields.DATE_OF_BIRTH, DTU.get_DateOnlyFromTimeZoneDate(patient_data.getDob()));
            else
                writeToPersonalSharedPreferenceKey(Constant.Fields.DATE_OF_BIRTH, patient_data.getDob());

            writeToPersonalSharedPreferenceKey(Constant.Fields.MOBILE_NUMBER, patient_data.getMobile());

            editor.commit();

            if (!Utils.isOnline(context)) {
                Intent objIntent = new Intent(getApplicationContext(), HeightActivity.class);
                startActivity(objIntent);
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void enableBluetooth() {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBluetoothAdapter.enable();
        /*if (!this.mBluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 3);
        }*/
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

