package com.abhaybmicoc.app.screen;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.activity.SettingsActivity;
import com.abhaybmicoc.app.database.DataBaseHelper;
import com.abhaybmicoc.app.services.DateService;
import com.abhaybmicoc.app.services.HttpService;
import com.abhaybmicoc.app.services.SharedPreferenceManager;
import com.abhaybmicoc.app.services.TextToSpeechService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.Utils;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OtpLoginScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    //implements TextToSpeech.OnInitListener

    // region Variables

    private Context context = OtpLoginScreen.this;
    private Button btnLogin;
    private EditText etMobileNumber;

    private DataBaseHelper dataBaseHelper;

    private int selectedId;

    private ProgressDialog progressDialog;

    SharedPreferenceManager sharedPreferenceManager;
    private SharedPreferences sharedPreferencesActivator;

    /*SharedPreferences sharedPreferencesPersonal;
    SharedPreferences sharedPreferencesOffline;
    SharedPreferences sharedPreferenceLanguage;*/

    TextToSpeechService textToSpeechService;

    private String kiosk_id;
    private String WELCOME_LOGIN_MESSAGE = "";

    final int MOBILE_NUMBER_MAX_LENGTH = 10; //max length of your text

    private FloatingActionButton fabMenuOptions;
    private CardView cvOfflineDataStatus;
    private Button btnSynch;
    private TextView tvNoOfRecords, tvNoOfUploadedRecords;

    private Animation slideUpAnimation;

    private boolean menuOptionsClicked = false;

    private BluetoothAdapter bluetoothAdapter;

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private NavigationView navigationView;

    final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    android.app.Dialog changeLanguageDilog;

    ArrayList<String> languagesList;

    // endregion

    // region Initialization methods

    /**
     *
     */
    private void setupUI() {
        setContentView(R.layout.activity_otp_login_screen);

        btnLogin = findViewById(R.id.btn_login);
        etMobileNumber = findViewById(R.id.et_mobile_number);

        fabMenuOptions = findViewById(R.id.fab_menuOptions);
        cvOfflineDataStatus = findViewById(R.id.cv_OfflineDataStatus);
        btnSynch = findViewById(R.id.btn_Synch);
        tvNoOfRecords = findViewById(R.id.tv_no_of_records);
        tvNoOfUploadedRecords = findViewById(R.id.tv_no_of_uploaded_records);

        WELCOME_LOGIN_MESSAGE = getResources().getString(R.string.mobile_no_msg);

        changeLanguageDilog = new android.app.Dialog(context);
        changeLanguageDilog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        fabMenuOptions.setOnClickListener((v -> showOfflineDataStatus()));
        btnSynch.setOnClickListener(v -> getOfflineRecords());
    }

    /**
     * @param position
     */
    private void setLanguageSelection(int position) {
        if (position == 0) {
            setLocale("en");
            Toast.makeText(context, "English selected", Toast.LENGTH_SHORT).show();
        } else if (position == 1) {
            setLocale("hi");
            Toast.makeText(context, "Hindi Selected", Toast.LENGTH_SHORT).show();
        } else if (position == 2) {
            setLocale("mar");
            Toast.makeText(context, "Marathi Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOfflineDataStatus() {
        cvOfflineDataStatus.setVisibility(View.VISIBLE);
        cvOfflineDataStatus.startAnimation(slideUpAnimation);
        menuOptionsClicked = true;

        setOfflineDataStatus();
    }

    private void setOfflineDataStatus() {

        JSONArray dataArray = dataBaseHelper.getOfflineData();

        if (dataArray != null && dataArray.length() > 0) {
            tvNoOfRecords.setText(String.valueOf(dataArray.length()));
            btnSynch.setVisibility(View.VISIBLE);
        } else {
            tvNoOfRecords.setText("0");
            btnSynch.setVisibility(View.GONE);
        }

        String uploaded_Count = sharedPreferenceManager.getLastSyncDateTime();

        if (uploaded_Count != null && !uploaded_Count.equals(""))
            tvNoOfUploadedRecords.setText(uploaded_Count);
        else
            tvNoOfUploadedRecords.setText("0");
    }

    private void getOfflineRecords() {
        try {
            JSONArray dataArray = dataBaseHelper.getOfflineData();

            if (dataArray != null && dataArray.length() > 0) {
                uploadOfflineRecords(dataArray);
            } else {
                setOfflineDataStatus();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadOfflineRecords(JSONArray dataArray) {
        try {
            if (Utils.isOnline(context)) {

                JSONObject dataObject = new JSONObject();
                dataObject.put("data", dataArray);

                HttpService.accessWebServicesJSON(
                        context, ApiUtils.SYNC_OFFLINE_DATA_URL, dataObject,
                        (response, error, status) -> handleOfflineAPIResponse(response, error, status));

            } else {
                Toast.makeText(context, "No internet connection, Try again...", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }

    private void handleOfflineAPIResponse(String response, VolleyError error, String status) {
        try {
            updateLocalStatus(response);
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    private void updateLocalStatus(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            JSONArray patientIdArray = jsonObject.getJSONArray("patient_ids");
            JSONArray parameterIdArray = jsonObject.getJSONArray("parameter_ids");

            String patientId = "";
            String parameterId = "";

            for (int i = 0; i < patientIdArray.length(); i++) {
                if (patientId.equals("")) {
                    patientId = patientIdArray.getString(i);
                } else {
                    patientId = patientId + "," + patientIdArray.getString(i);
                }
            }

            for (int j = 0; j < parameterIdArray.length(); j++) {
                if (parameterId.equals("")) {
                    parameterId = parameterIdArray.getString(j);
                } else {
                    parameterId = parameterId + "," + parameterIdArray.getString(j);
                }
            }


            sharedPreferenceManager.setLastSyncDateTime(DateService.getCurrentDateTime(DateService.DATE_FORMAT));

            dataBaseHelper.deleteTable_data(Constant.TableNames.TBL_PATIENTS, Constant.Fields.PATIENT_ID, patientId);

            dataBaseHelper.deleteTable_data(Constant.TableNames.TBL_PARAMETERS, Constant.Fields.PARAMETER_ID, parameterId);

            setOfflineDataStatus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        //saving data to shared preference
        sharedPreferenceManager.setSelectedLanguage(lang);

    }


    private void initializeData() {
        dataBaseHelper = new DataBaseHelper(context);

        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.out_to_right);

        textToSpeechService = new TextToSpeechService(getApplicationContext(), WELCOME_LOGIN_MESSAGE);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();

        try {
            sharedPreferenceManager = SharedPreferenceManager.getInstance(context);

            sharedPreferencesActivator = getSharedPreferences(ApiUtils.PREFERENCE_ACTIVATOR, MODE_PRIVATE);
//            sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
//            sharedPreferenceLanguage = getSharedPreferences(ApiUtils.PREFERENCE_LANGUAGE, MODE_PRIVATE);
//            sharedPreferencesOffline = getSharedPreferences(ApiUtils.PREFERENCE_OFFLINE, MODE_PRIVATE);
//            sharedPreferencesPersonal.edit().clear().apply();

            kiosk_id = sharedPreferencesActivator.getString("pinLock", "");
        } catch (Exception e) {
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etMobileNumber, InputMethodManager.SHOW_IMPLICIT);

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(MOBILE_NUMBER_MAX_LENGTH);
        etMobileNumber.setFilters(filterArray);

        languagesList = new ArrayList<>();
        languagesList.add("English");
        languagesList.add("Hindi");
        languagesList.add("Marathi");

        clearDatabase();
    }

    // endregion

    // region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupNavigationDrawer();
        setupEvents();
        initializeData();
    }

    private void setupNavigationDrawer() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        textToSpeechService.stopTextToSpeech();
    }

    @Override
    protected void onResume() {
        super.onResume();

        textToSpeechService.speakOut(WELCOME_LOGIN_MESSAGE);
    }

    // endregion

    // region Logical methods

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
                if (Utils.isOnline(context)) {
                    GenerateOTP();
                } else {
                    savePatient();
                }
            }
        }
    }

    private void savePatient() {
        try {
            ContentValues patientContentValues = new ContentValues();

            patientContentValues.put(Constant.Fields.KIOSK_ID, kiosk_id);
            patientContentValues.put(Constant.Fields.MOBILE_NUMBER, etMobileNumber.getText().toString());

            dataBaseHelper.saveToLocalTable(Constant.TableNames.TBL_PATIENTS, patientContentValues, etMobileNumber.getText().toString());

            Intent objIntent = new Intent(getApplicationContext(), OtpVerifyScreen.class);
            objIntent.putExtra(Constant.Fields.MOBILE_NUMBER, etMobileNumber.getText().toString());
            objIntent.putExtra("connectivity", "offline");
            startActivity(objIntent);

        } catch (Exception e) {
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

    /**
     * @param jsonResponse
     * @throws JSONException
     */
    private void writePersonalSharedPreferences(JSONObject jsonResponse) throws JSONException {

        sharedPreferenceManager.setPatientId(jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString(Constant.Fields.ID));
        sharedPreferenceManager.setPatientName(jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString(Constant.Fields.NAME));
        sharedPreferenceManager.setPatientEmail(jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString(Constant.Fields.EMAIL));
        sharedPreferenceManager.setPatientGender(jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString(Constant.Fields.GENDER));
        sharedPreferenceManager.setDateOfBirth(jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString(Constant.Fields.DATE_OF_BIRTH));
        sharedPreferenceManager.setMobileNumber(jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString(Constant.Fields.MOBILE_NUMBER));
        sharedPreferenceManager.setToken(jsonResponse.getJSONObject("data").getJSONArray("patient").getJSONObject(0).getString(Constant.Fields.TOKEN));
    }

    // endregion

    // region API methods

    /**
     *
     */
    private void GenerateOTP() {

        Map<String, String> headerParams = new HashMap<>();
        Map<String, String> requestBodyParams = new HashMap<>();

        requestBodyParams.put("kiosk_id", kiosk_id);
        requestBodyParams.put("mobile", etMobileNumber.getText().toString());

        HttpService.accessWebServices(
                context,
                ApiUtils.LOGIN_URL,
                requestBodyParams,
                headerParams,
                (response, error, status) -> handleAPIResponse(response, error, status));
    }

    private void handleAPIResponse(String response, VolleyError error, String status) {
        if (status.equals("response")) {
            try {
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.getJSONObject("data").getJSONArray("patient").length() == 0) {

                    sharedPreferenceManager.setToken(jsonResponse.getJSONObject("data").getString(Constant.Fields.TOKEN));

                    Intent objIntent = new Intent(getApplicationContext(), OtpVerifyScreen.class);
                    objIntent.putExtra(Constant.Fields.MOBILE_NUMBER, etMobileNumber.getText().toString());
                    objIntent.putExtra(Constant.Fields.KIOSK_ID, kiosk_id);
                    objIntent.putExtra("connectivity", "online");
                    startActivity(objIntent);

                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);

                    finish();
                } else {
                    writePersonalSharedPreferences(jsonResponse);

                    Intent objIntent = new Intent(getApplicationContext(), OtpVerifyScreen.class);
                    objIntent.putExtra(Constant.Fields.MOBILE_NUMBER, etMobileNumber.getText().toString());
                    objIntent.putExtra(Constant.Fields.KIOSK_ID, kiosk_id);
                    objIntent.putExtra("connectivity", "online");
                    startActivity(objIntent);

                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);

                    finish();
                }
            } catch (Exception e) {
                // TODO: Handle exception
            }
        } else if (status.equals("error")) {
            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            // TODO: Handle error
        }
    }

    @Override

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.nav_offline_sync:
                showOfflineDataStatus();
                break;


            case R.id.nav_setting:
                startActivity(new Intent(context, SettingsActivity.class));
                finish();
                break;

            case R.id.nav_language:

                showLanguageDilog();

                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLanguageDilog() {
        changeLanguageDilog.setContentView(R.layout.select_language_dilog);
        layoutParams.copyFrom(changeLanguageDilog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        changeLanguageDilog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        changeLanguageDilog.setCanceledOnTouchOutside(false);

        final Spinner spnSelectLanguage = changeLanguageDilog.findViewById(R.id.spn_SelectLanguage);
        final ImageView icCloseDilog = changeLanguageDilog.findViewById(R.id.ic_CloseDilog);
        final Button btnChangeLanguage = changeLanguageDilog.findViewById(R.id.btn_ChangeLanguage);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, languagesList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSelectLanguage.setAdapter(dataAdapter);

        if (sharedPreferenceManager.getSelectedLanguage().equals("en")) {
            spnSelectLanguage.setSelection(0);
        } else if (sharedPreferenceManager.getSelectedLanguage().equals("hi")) {
            spnSelectLanguage.setSelection(1);
        } else if (sharedPreferenceManager.getSelectedLanguage().equals("mar")) {
            spnSelectLanguage.setSelection(2);
        }

        spnSelectLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setLanguageSelection(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguageDilog.dismiss();
            }
        });

        icCloseDilog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguageDilog.dismiss();
            }
        });

        changeLanguageDilog.show();
        changeLanguageDilog.getWindow().setAttributes(layoutParams);

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (event.getAction() == KeyEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }

            cvOfflineDataStatus.setVisibility(View.GONE);

            /*if (v instanceof AutoCompleteTextView) {
                InputM   ethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }*/

        }
        return super.dispatchTouchEvent(event);
    }

    // endregion
}