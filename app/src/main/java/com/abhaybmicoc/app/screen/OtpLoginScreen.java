package com.abhaybmicoc.app.screen;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.activity.ChangeLanguageActivity;
import com.abhaybmicoc.app.activity.SettingsActivity;
import com.abhaybmicoc.app.activity.SplashActivity;
import com.abhaybmicoc.app.activity.TechecnicianInstallationNewActivity;
import com.abhaybmicoc.app.database.DataBaseHelper;
import com.abhaybmicoc.app.model.Clinic_Data;
import com.abhaybmicoc.app.model.Clinic_Data_Info;
import com.abhaybmicoc.app.model.Offline_Sync_Response;
import com.abhaybmicoc.app.model.Patient_Data;
import com.abhaybmicoc.app.model.Patient_Response;
import com.abhaybmicoc.app.services.DateService;
import com.abhaybmicoc.app.services.HttpService;
import com.abhaybmicoc.app.services.TextToSpeechService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.DTU;
import com.abhaybmicoc.app.utils.Utils;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class OtpLoginScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //implements TextToSpeech.OnInitListener

    // region Variables

    private Context context = OtpLoginScreen.this;
    private Button btnLogin;
    private EditText etMobileNumber;

    private int selectedId;

    private ProgressDialog progressDialog;

    SharedPreferences sharedPreferencesPersonal;
    SharedPreferences sharedPreferencesOffline;

    private DataBaseHelper dataBaseHelper;

    TextToSpeechService textToSpeechService;

    private String kiosk_id = "", clinic_name = "";
    private String WELCOME_LOGIN_MESSAGE = "";

    final int MOBILE_NUMBER_MAX_LENGTH = 10; //max length of your text

    private SharedPreferences sharedPreferencesActivator;

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
    private String patient_id = "";

    final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
    android.app.Dialog changeLanguageDilog;

    ArrayList<String> languagesList;

    private TextView tvClinicName;
    private TextView tvKioskID;

    Dialog installationKioskDialog;

// region Events

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupNavigationDrawer();
        setupEvents();
        initializeData();


        try {

            long DateTime = DTU.getTimeStampFromDateTime(DateService.getCurrentDateTime(DateService.YYYY_MM_DD_HMS), DateService.YYYY_MM_DD_HMS);

            Log.e("ReceivedTodayDate", ":" + DTU.getd_m_Y(DTU.getCurrentDate()));
            Log.e("ReceivedTimeZone", ":" + DateService.getCurrentDateTime(DateService.YYYY_MM_DD_T_HMS_Z));
            Log.e("TimeZone", ":" + Calendar.getInstance().getTimeZone());
            Log.e("LongTimeStampFrmDate", ":" + DateTime);
            Log.e("TimeStampFrmDate", ":" + DTU.getDateTimeFromTimeStamp(DateTime, DateService.YYYY_MM_DD_T_HMS_Z));

        } catch (Exception e) {
            e.printStackTrace();
        }


        /*SharedPreferences sharedPreferenceBluetoothAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferenceBluetoothAddress.edit();
        if (sharedPreferenceBluetoothAddress.getString("hcbluetooth", "").equalsIgnoreCase("")) {
            editor.putString("hcbluetooth", "FC:A8:9B:00:58:D4");
            editor.commit();
        }*/

        if (Utils.isOnline(context)) {
            checkKioskStatus();
        } else {
            if (sharedPreferencesActivator.getString("is_trial_mode", "").equals("1"))
                showInstallationOrTrialPopUp();
        }
    }

    private void checkKioskStatus() {
        try {
            Map<String, String> headerParams = new HashMap<>();
            Map<String, String> requestBodyParams = new HashMap<>();

            requestBodyParams.put("token", kiosk_id);

            HttpService.accessWebServices(
                    context,
                    ApiUtils.FIND_BY_TOKEN,
                    Request.Method.POST,
                    requestBodyParams,
                    headerParams,
                    (response, error, status) -> handleCheckKioskAPIResponse(response, error, status));
        } catch (Exception e) {
        }
    }

    private void handleCheckKioskAPIResponse(String response, VolleyError error, String status) {
        try {
            if (status.equals("response")) {

                Clinic_Data user_data = (Clinic_Data) Utils.parseResponse(response, Clinic_Data.class);
                Log.e("Found_Status_Log", ":" + user_data.getFound());

                if (user_data.getFound()) {
                    updateKioskInfo(user_data.getData());

                    Log.e("isTrialMode_OTPLog", ":" + user_data.getData().getIsTrialMode());


                    if (user_data.getData().getIsTrialMode() == 1) {
                        Log.e("fvdjhfvddvddvjvj", "     :aadsdsd");
                        showInstallationOrTrialPopUp();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void updateKioskInfo(Clinic_Data_Info clinic_data_info) {
        try {
            // Writing data to SharedPreferences
            SharedPreferences.Editor editor = sharedPreferencesActivator.edit();

            // Writing data to SharedPreferences

            editor.putString("pinLock", clinic_data_info.getToken());
            editor.putString("clinic_name", clinic_data_info.getName());
            editor.putString("address", clinic_data_info.getAddress());
            editor.putString("app_version", clinic_data_info.getAppVersion());
            editor.putString("location_id", clinic_data_info.getLocationId());
            editor.putString("total_tests_done", String.valueOf(clinic_data_info.getTotalTestsDone()));
            editor.putString("allowed_trial_tests", String.valueOf(clinic_data_info.getAllowedTrialTests()));
            editor.putString("installed_by", clinic_data_info.getInstalledBy());
            editor.putString("assigned_user_id", String.valueOf(clinic_data_info.getAssignedUserId()));
            editor.putString("installation_date", clinic_data_info.getInstallationDate());
            editor.putString("machine_operator_name", clinic_data_info.getMachineOperatorName());
            editor.putString("machine_operator_mobile_number", clinic_data_info.getMachineOperatorMobileNumber());
//                            editor.putString("client_name", clinicObject.getString("client_name"));
            editor.putString("is_trial_mode", String.valueOf(clinic_data_info.getIsTrialMode()));
            editor.putString("id", clinic_data_info.getId());
            editor.commit();

        } catch (Exception e) {
        }
    }


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


        tvClinicName = hView.findViewById(R.id.tv_ClinicName);
        tvKioskID = hView.findViewById(R.id.tv_KioskID);
    }


    /**
     *
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
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

    private void showOfflineDataStatus() {
        cvOfflineDataStatus.setVisibility(View.VISIBLE);
        cvOfflineDataStatus.startAnimation(slideUpAnimation);

        menuOptionsClicked = true;
        setOfflineDataStatus();
    }

    private void setOfflineDataStatus() {
        JSONArray dataArray = dataBaseHelper.getAllOfflineData();
        if (dataArray != null && dataArray.length() > 0) {
            tvNoOfRecords.setText(String.valueOf(dataArray.length()));
            btnSynch.setVisibility(View.VISIBLE);
        } else {
            tvNoOfRecords.setText("0");
            btnSynch.setVisibility(View.GONE);
        }

        String uploaded_Count = sharedPreferencesOffline.getString(Constant.Fields.UPLOADED_RECORDS_COUNT, "");

        if (uploaded_Count != null && !uploaded_Count.equals(""))
            tvNoOfUploadedRecords.setText(sharedPreferencesOffline.getString(Constant.Fields.UPLOADED_RECORDS_COUNT, ""));
        else
            tvNoOfUploadedRecords.setText("NA");

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
                        context,
                        ApiUtils.SYNC_OFFLINE_DATA_URL,
                        dataObject,
                        (response, error, status) -> handleOfflineAPIResponse(response, error, status));
            } else {
                Toast.makeText(context, "No internet connection, Try again...", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }

    private void handleOfflineAPIResponse(String response, VolleyError error, String status) {

        Log.e("response_Log", ":" + response);

        try {
            if (status.equals("response")) {

                Offline_Sync_Response offlineSyncResponse = (Offline_Sync_Response) Utils.parseResponse(response, Offline_Sync_Response.class);

                if (offlineSyncResponse.getSuccess()) {
                    SharedPreferences.Editor editor = sharedPreferencesOffline.edit();

                    editor.putString(Constant.Fields.UPLOADED_RECORDS_COUNT, DateService.getCurrentDateTime(DateService.DATE_FORMAT));

                    editor.commit();

                    updateLocalStatus(offlineSyncResponse.getData());
                } else {
                    Toast.makeText(context, "Data uploading failed, try again", Toast.LENGTH_SHORT).show();

                }


            } else {
                Toast.makeText(context, "Data uploading failed, try again", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // TODO: Handle exception
            e.printStackTrace();
        }
    }

    private void updateLocalStatus(Offline_Sync_Response.Offline_Sync_Response_Data offlineSyncResponseData) {
        try {

//            JSONArray resultArray = jsonObject.getJSONArray("result");
//            JSONArray patient_id_Array = jsonObject.getJSONArray("patient_ids");
//            JSONArray parameter_id_Array = jsonObject.getJSONArray("parameter_ids");

            String patientId = "";
            String parameterId = "";

            for (int i = 0; i < offlineSyncResponseData.getPatientIds().size(); i++) {
                if (patientId.equals("")) {
                    patientId = offlineSyncResponseData.getPatientIds().get(i);
                } else {
                    patientId = patientId + "," + offlineSyncResponseData.getPatientIds().get(i);
                }
            }

            for (int j = 0; j < offlineSyncResponseData.getParameterIds().size(); j++) {
                if (parameterId.equals("")) {
                    parameterId = offlineSyncResponseData.getParameterIds().get(j);
                } else {
                    parameterId = parameterId + "," + offlineSyncResponseData.getParameterIds().get(j);
                }
            }

            Log.e("patientId_Updated", ":" + patientId);
            Log.e("parameterId_Updated", ":" + parameterId);

            dataBaseHelper.deleteTable_data(Constant.TableNames.PATIENTS, Constant.Fields.PATIENT_ID, patientId);

            dataBaseHelper.deleteTable_data(Constant.TableNames.PARAMETERS, Constant.Fields.PARAMETER_ID, parameterId);

            Toast.makeText(context, "Data Sync successfully", Toast.LENGTH_SHORT).show();

            getOfflineRecords();
            setOfflineDataStatus();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeData() {

        Log.e("getTimeXone_Date", ":" + DateService.getCurrentDateTime(DateService.YYYY_MM_DD_T_HMS_Z));

        dataBaseHelper = new DataBaseHelper(context);

        installationKioskDialog = new android.app.Dialog(context);
        installationKioskDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        slideUpAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.out_to_right);

        setupTextToSpeech();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();

        try {

            sharedPreferencesActivator = getSharedPreferences(ApiUtils.PREFERENCE_ACTIVATOR, MODE_PRIVATE);
            sharedPreferencesPersonal = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
            sharedPreferencesOffline = getSharedPreferences(ApiUtils.PREFERENCE_OFFLINE, MODE_PRIVATE);

            sharedPreferencesPersonal.edit().clear().apply();

            kiosk_id = sharedPreferencesActivator.getString("pinLock", "");
            clinic_name = "Welcome to " + sharedPreferencesActivator.getString("clinic_name", "");

            tvClinicName.setText(clinic_name);
            tvKioskID.setText(kiosk_id);

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


    private void setupTextToSpeech() {
        if (Utils.isOnline(context)) {
            textToSpeechService = new TextToSpeechService(getApplicationContext(), WELCOME_LOGIN_MESSAGE);
        }
    }

    // endregion

    @SuppressLint("SetTextI18n")

    private void showInstallationOrTrialPopUp() {
        installationKioskDialog.setContentView(R.layout.installation_or_trial_dilog);
        layoutParams.copyFrom(installationKioskDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        installationKioskDialog.getWindow().setLayout(layoutParams.width, layoutParams.height);
        installationKioskDialog.setCanceledOnTouchOutside(false);

        try {
            final TextView tv_msg = installationKioskDialog.findViewById(R.id.tv_msg);
            final Button btnProceed = installationKioskDialog.findViewById(R.id.btn_Proceed);
            final ImageView ic_CloseDilog = installationKioskDialog.findViewById(R.id.ic_CloseDilog);

            final RadioGroup rgSelectMode = installationKioskDialog.findViewById(R.id.rg_SelectMode);
            final RadioButton rbTrialMode = installationKioskDialog.findViewById(R.id.rb_TrialMode);
            final RadioButton rbInstallationMode = installationKioskDialog.findViewById(R.id.rb_InstallationMode);

            tv_msg.setTextColor(context.getResources().getColor(R.color.white));
            int total_tests_done = 0;
            int allowed_trial_tests = 10;

            Log.e("testFodasghd_Log", ":" + sharedPreferencesActivator.getString("total_tests_done", ""));

            if (!sharedPreferencesActivator.getString("total_tests_done", "").equals("null")) {
                total_tests_done = Integer.parseInt(sharedPreferencesActivator.getString("total_tests_done", ""));
            }

            if (!sharedPreferencesActivator.getString("allowed_trial_tests", "").equals("null")) {
                allowed_trial_tests = Integer.parseInt(sharedPreferencesActivator.getString("allowed_trial_tests", ""));
            } else {
                allowed_trial_tests = 10;
            }

            Log.e("total_tests_done_Log", ":" + total_tests_done);

            if (total_tests_done <= allowed_trial_tests) {
                int remaining_test = allowed_trial_tests - total_tests_done;
                tv_msg.setText("In Trial mode you have remaining only " + remaining_test + " attempts!");
            } else {
                rbTrialMode.setChecked(false);
                rbTrialMode.setVisibility(View.GONE);
            }


            try {

                rgSelectMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                        if (checkedId == rbTrialMode.getId()) {
                            tv_msg.setVisibility(View.VISIBLE);
                        } else if (checkedId == rbInstallationMode.getId()) {
                            tv_msg.setVisibility(View.GONE);
                        }
                    }
                });

                ic_CloseDilog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        installationKioskDialog.dismiss();
                    }
                });

                btnProceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!rbTrialMode.isChecked()) {
                            startActivity(new Intent(context, TechecnicianInstallationNewActivity.class));
                            finish();
                            installationKioskDialog.dismiss();
                        } else {
                            if (Utils.isOnline(context)) {
                                installationKioskDialog.dismiss();
                            } else {
                                Toast.makeText(context, "Trial mode doesn't support offline testing", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            } catch (
                    Exception e) {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        installationKioskDialog.show();
        installationKioskDialog.getWindow().setAttributes(layoutParams);
    }


    // endregion


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (textToSpeechService != null)
            textToSpeechService.stopTextToSpeech();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // endregion

    // region Logical methods

    /**
     * 1.check the location permission
     * 2.check the mobile number is empty or not
     * 3.check mobile number is valid or not
     * 4.a.check for network connection
     * 4.b.if network available generate otp
     * 4.c.if network not available save patient locally
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void doLogin() {
        if (Utils.getInstance().giveLocationPermission(this)) {
            if (etMobileNumber.getText().toString().equals("")) {
                etMobileNumber.setError("Please Enter Mobile Number");
            } else if (etMobileNumber.getText().toString().length() < 10) {
                etMobileNumber.setError("Please Enter Valid Mobile Number");
            } else {
                savePatient();

                if (Utils.isOnline(context)) {
                    GenerateOTP();
                } else {
                    Toast.makeText(context, "No Internet connection, Please Try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * save patient data locally
     */
    private void savePatient() {
        try {
            ContentValues patientContentValues = new ContentValues();

            patientContentValues.put(Constant.Fields.KIOSK_ID, kiosk_id);
            patientContentValues.put(Constant.Fields.MOBILE_NUMBER, etMobileNumber.getText().toString());

            Long saveResponse = dataBaseHelper.saveToLocalTable(Constant.TableNames.PATIENTS, patientContentValues, etMobileNumber.getText().toString());

            if (saveResponse != -1) {
                patient_id = dataBaseHelper.getLastInsertPatientID();

                Log.e("patient_id_OTP", ":" + patient_id);

                Log.e("akgaygygahfa", ":" + sharedPreferencesActivator.getString("is_trial_mode", ""));
                if (!Utils.isOnline(context) && sharedPreferencesActivator.getString("is_trial_mode", "").equals("0")) {
                    Intent objIntent = new Intent(getApplicationContext(), OtpVerifyScreen.class);
                    objIntent.putExtra(Constant.Fields.MOBILE_NUMBER, etMobileNumber.getText().toString());
                    objIntent.putExtra(Constant.Fields.PATIENT_ID, patient_id);
                    objIntent.putExtra("connectivity", "offline");
                    objIntent.putExtra("isNewPatient", "false");
                    startActivity(objIntent);
                }
            } else {
                //TODO:  Handle Error Message
            }

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
        clearSharedPreference(ApiUtils.PREFERENCE_VISION_RESULT);
    }

    private void clearSharedPreference(String preferenceName) {
        getSharedPreferences(preferenceName, MODE_PRIVATE).edit().clear().commit();
    }

    /**
     * @param
     * @param
     * @throws JSONException
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void writePersonalSharedPreferences(Patient_Data patient_data_info) throws
            JSONException {
        try {
            SharedPreferences.Editor editor = sharedPreferencesPersonal.edit();

            Log.e("patient_ID_Log", ":" + patient_data_info.getId());
            editor.putString(Constant.Fields.PATIENT_ID, patient_data_info.getId());

            editor.putString(Constant.Fields.ID, patient_id);

            if (patient_data_info.getName() != null)
                editor.putString(Constant.Fields.NAME, patient_data_info.getName());
            else
                editor.putString(Constant.Fields.NAME, "");

            if (patient_data_info.getEmail() != null)
                editor.putString(Constant.Fields.EMAIL, patient_data_info.getEmail());
            else
                editor.putString(Constant.Fields.EMAIL, "");

            editor.putString(Constant.Fields.TOKEN, patient_data_info.getToken());
            editor.putString(Constant.Fields.GENDER, patient_data_info.getGender());

            if (patient_data_info.getDob() != null)
                editor.putString(Constant.Fields.DATE_OF_BIRTH, DTU.get_DateOnlyFromTimeZoneDate(patient_data_info.getDob()));

            editor.putString(Constant.Fields.MOBILE_NUMBER, patient_data_info.getMobile());

            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void GenerateOTP() {

        Map<String, String> headerParams = new HashMap<>();
        headerParams.put("app_version", SplashActivity.currentVersion);

        Map<String, String> requestBodyParams = new HashMap<>();
        requestBodyParams.put("kiosk_id", kiosk_id);
        requestBodyParams.put("mobile", etMobileNumber.getText().toString());
        requestBodyParams.put(Constant.Fields.CREATED_AT, DateService.getCurrentDateTime(DateService.YYYY_MM_DD_HMS));

        Log.e("requestBodyParams_Login", ":" + requestBodyParams);
        Log.e("headerParams_Login", ":" + headerParams);

        HttpService.accessWebServices(
                context,
                ApiUtils.LOGIN_URL,
                Request.Method.POST,
                requestBodyParams,
                headerParams,
                (response, error, status) -> handleAPIResponse(response, error, status));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleAPIResponse(String response, VolleyError error, String status) {

        Log.e("response_Login", ":" + response);
        if (status.equals("response")) {
            try {

                Patient_Response patient_response = (Patient_Response) Utils.parseResponse(response, Patient_Response.class);
                if (patient_response.getFound()) {

                    writePersonalSharedPreferences(patient_response.getData());
                    Log.e("Nameadd_Log", ":" + patient_response.getData().getName());

//                    if (patient_response.getData().getName().equals("null"))
//                        Log.e("Nameadd_Log_Strng", "  :Null: " + patient_response.getData().getName());


                    Intent objIntent = new Intent(getApplicationContext(), OtpVerifyScreen.class);
                    objIntent.putExtra(Constant.Fields.MOBILE_NUMBER, etMobileNumber.getText().toString());
                    objIntent.putExtra(Constant.Fields.PATIENT_ID, patient_id);
                    objIntent.putExtra(Constant.Fields.KIOSK_ID, kiosk_id);
                    objIntent.putExtra("connectivity", "online");
                    if (patient_response.getData().getName() == null) {
                        objIntent.putExtra("isNewPatient", "true");
                        Log.e("Nameadd_Log_null", "   :Null:  " + patient_response.getData().getName());
                    } else {
                        objIntent.putExtra("isNewPatient", "false");
                        Log.e("Nameadd_Log_false", "   :Null:  " + patient_response.getData().getName());
                    }


                    startActivity(objIntent);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);

                    finish();
                }
            } catch (Exception e) {
                // TODO: Handle exception
                e.printStackTrace();
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
                startActivity(new Intent(context, ChangeLanguageActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        }
        return super.dispatchTouchEvent(event);
    }
    // endregion

    @Override
    public void onBackPressed() {

    }
}