package com.abhaybmicoc.app.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.screen.OtpVerifyScreen;
import com.abhaybmicoc.app.services.HttpService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.DTU;
import com.abhaybmicoc.app.utils.Utils;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TechecnicianInstallationNewActivity extends Activity {

    private Context context = TechecnicianInstallationNewActivity.this;

    private int day;
    private int year;
    private int month;

    private ImageView ivBack;
    private EditText edtClinicName;
    private EditText edtInstalledByName;
    private EditText edtMachineOperatorName;
    private EditText edtMachineOperatorMobile;
    private EditText edtInstallationDate;
    private EditText edtClientName;
    private Spinner spinnerClientName;
    private Spinner spinnerLocation;
    private EditText edtAddress;
    private Button btnSave;

    private ArrayList<String> locationList;
    private ArrayList<String> locationIDList;

    private SharedPreferences sharedPreferencesActivator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();

        if (Utils.isOnline(context)) {
            getLocationData();
        } else {
            Toast.makeText(context, "No Internet connection, Please Try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void setupUI() {
        setContentView(R.layout.activity_techecnician_installation_new);

        ivBack = findViewById(R.id.iv_Back);
        edtClinicName = findViewById(R.id.edt_ClinicName);
        edtInstalledByName = findViewById(R.id.edt_InstalledByName);
        edtMachineOperatorName = findViewById(R.id.edt_MachineOperatorName);
        edtMachineOperatorMobile = findViewById(R.id.edt_MachineOperatorMobile);
        edtInstallationDate = findViewById(R.id.edt_InstallationDate);
        edtClientName = findViewById(R.id.edt_ClientName);
        spinnerClientName = findViewById(R.id.spn_ClientName);
        spinnerLocation = findViewById(R.id.spn_Location);
        edtAddress = findViewById(R.id.edt_address);
        btnSave = findViewById(R.id.btn_Save);
    }

    private void setupEvents() {
        edtInstallationDate.setOnClickListener(view -> showDatePicler());
        ivBack.setOnClickListener(view -> goToBackScreen());
        btnSave.setOnClickListener(view -> saveInstallationData());
    }

    private void initializeData() {

        try {
            sharedPreferencesActivator = getSharedPreferences(ApiUtils.PREFERENCE_ACTIVATOR, MODE_PRIVATE);

            edtClinicName.setText(sharedPreferencesActivator.getString("clinic_name", ""));
            edtClientName.setText(sharedPreferencesActivator.getString("client_name", ""));

        } catch (Exception e) {
        }
    }

    private void getLocationData() {

        Map<String, String> headerParams = new HashMap<>();
        Map<String, String> requestBodyParams = new HashMap<>();

        HttpService.accessWebServices(
                context,
                ApiUtils.LOCATION_URL,
                Request.Method.GET,
                requestBodyParams,
                headerParams,
                (response, error, status) -> handleLocationAPIResponse(response, error, status));
    }

    private void handleLocationAPIResponse(String response, VolleyError error, String status) {

        Log.e("Location_response", ":" + response);

        try {

            JSONArray responseArray = new JSONArray(response);

            locationList = new ArrayList<>();
            locationIDList = new ArrayList<>();

            locationList.add("Select Location");
            locationIDList.add("0");

            for (int i = 0; i < responseArray.length(); i++) {
                locationList.add(responseArray.getJSONObject(i).getString("name"));
                locationIDList.add(responseArray.getJSONObject(i).getString("id"));
            }


            ArrayAdapter<String> dataAdapter;
            dataAdapter = new ArrayAdapter<String>(context.getApplicationContext(),
                    R.layout.simple_item_selected, locationList);
            dataAdapter.setDropDownViewResource(R.layout.simple_item);
            spinnerLocation.setAdapter(dataAdapter);

            if (sharedPreferencesActivator.getString("location_id", "") != null &&
                    !sharedPreferencesActivator.getString("location_id", "").equals("")) {
                spinnerLocation.setSelection(locationIDList.indexOf(sharedPreferencesActivator.getString("location_id", "")));
            }
        } catch (Exception e) {
        }
    }

    private void saveInstallationData() {

        if (Utils.isOnline(context)) {
            Map<String, String> requestBodyParams = new HashMap<>();
//            requestBodyParams.put(Constant.Fields.NAME, sharedPreferencesActivator.getString("clinic_name", ""));
            requestBodyParams.put(Constant.Fields.APP_VERSION, SplashActivity.currentVersion);
            requestBodyParams.put(Constant.Fields.STATUS, "1");
//            requestBodyParams.put(Constant.Fields.TOKEN, sharedPreferencesActivator.getString("pinLock", ""));
            requestBodyParams.put(Constant.Fields.IS_TRIAL_MODE, "0");
            requestBodyParams.put(Constant.Fields.CLIENT_NAME, "XYZ");
            requestBodyParams.put(Constant.Fields.MACHINE_OPERATOR_NAME, edtMachineOperatorName.getText().toString());
            requestBodyParams.put(Constant.Fields.INSTALLED_BY, edtInstalledByName.getText().toString());
            requestBodyParams.put(Constant.Fields.MACHINE_OPERATOR_MOBILE_NUMBER, edtMachineOperatorMobile.getText().toString());
            requestBodyParams.put(Constant.Fields.INSTALLATION_DATE, edtInstallationDate.getText().toString());
            requestBodyParams.put(Constant.Fields.ADDRESS, edtAddress.getText().toString());

            HashMap headersParams = new HashMap();

            String url = ApiUtils.UPDATE_CLINIC_URL + sharedPreferencesActivator.getString("id", "");
            Log.e("reqBdyParms_UpdtClinic", ":" + requestBodyParams);
            Log.e("url_UpdateClinic", ":" + url);

            HttpService.accessWebServices(
                    context,
                    url,
                    Request.Method.PUT,
                    requestBodyParams,
                    headersParams,
                    (response, error, status) -> handleAPIResponse(response, error, status));

        } else {
            Toast.makeText(context, "No Internet connection, Please Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleAPIResponse(String response, VolleyError error, String status) {

        if (status.equals("response")) {
            try {
                Log.e("Update_response", ":" + response);
                JSONObject responseObject = new JSONObject(response);

                if (responseObject != null) {
                    startActivity(new Intent(context, OtpLoginScreen.class));
                    finish();
                } else {
                    Toast.makeText(context, "Server Error, Please Try again", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
            }
        } else {
            Toast.makeText(context, "Server Error, Please Try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToBackScreen() {
        startActivity(new Intent(context, OtpLoginScreen.class));
        finish();
    }

    private void showDatePicler() {

        DTU.showDatePickerDialog(context, DTU.FLAG_OLD_AND_NEW, edtInstallationDate);

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

            edtInstallationDate.setEnabled(true);
            edtInstallationDate.setText(dateTimeFormatUS);
        };

        DatePickerDialog dpDialog = new DatePickerDialog(context, listener, year, month + 1, day);

        dpDialog.setCancelable(false);
        dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        dpDialog.setOnDismissListener(dialogInterface -> edtInstallationDate.setEnabled(true));

        dpDialog.show();
    }

    @Override
    public void onBackPressed() {
        goToBackScreen();
    }
}
