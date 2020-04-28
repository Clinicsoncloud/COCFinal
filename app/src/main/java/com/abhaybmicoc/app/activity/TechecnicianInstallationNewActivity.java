package com.abhaybmicoc.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
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
import com.abhaybmicoc.app.model.Common_Update_Response;
import com.abhaybmicoc.app.model.Location_Response;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.screen.OtpVerifyScreen;
import com.abhaybmicoc.app.services.DateService;
import com.abhaybmicoc.app.services.HttpService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.DTU;
import com.abhaybmicoc.app.utils.GPSTracker;
import com.abhaybmicoc.app.utils.Utils;
import com.abhaybmicoc.app.utils.VU;
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
    private GPSTracker gpsTracker;

    private ArrayList<String> locationList;
    private ArrayList<String> locationIDList;

    private SharedPreferences sharedPreferencesActivator;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();

        requestGPSPermission();

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

        edtInstallationDate.setText(DTU.getd_m_Y(DTU.getCurrentDate()));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

            gpsTracker = new GPSTracker(context);

            Log.e("Latitude_Log", ":" + gpsTracker.getLatitude());
            Log.e("Longitude_Log", ":" + gpsTracker.getLongitude());

        } catch (Exception e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestGPSPermission() {
        try {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            String provider = Settings.Secure.getString(getContentResolver(), LocationManager.GPS_PROVIDER);

            if (!statusOfGPS) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("GPS Disabled");
                alertDialogBuilder.setMessage("Kindly make sure device location is on.")
                        .setCancelable(false)
                        .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 111);
                            }
                        });

                /* create alert dialog */
                AlertDialog alertDialog = alertDialogBuilder.create();
                /* show alert dialog */
                if (!((Activity) context).isFinishing())
                    alertDialog.show();
                alertDialogBuilder.setCancelable(false);
                // Notify users and show settings if they want to enable GPS
            }
        } catch (RuntimeException ex) {
            // TODO: Show message that we did not get permission to access bluetooth
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
            if (status.equals("response")) {
                Location_Response locationResponse = (Location_Response) Utils.parseResponse(response, Location_Response.class);

                if (locationResponse.getFound()) {

                    locationList = new ArrayList<>();
                    locationIDList = new ArrayList<>();

                    locationList.add("Select Location");
                    locationIDList.add("0");

                    for (int i = 0; i < locationResponse.getData().size(); i++) {
                        locationList.add(locationResponse.getData().get(i).getName());
                        locationIDList.add(locationResponse.getData().get(i).getId());
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
                }
            }
        } catch (Exception e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveInstallationData() {

        if (Validate()) {

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
                requestBodyParams.put(Constant.Fields.INSTALLATION_DATE, DTU.get_yyyy_mm_dd_HMS(edtInstallationDate.getText().toString()));
                requestBodyParams.put(Constant.Fields.LICENSE_EXPIRY_DATE, DTU.get_yyyy_mm_dd_HMS(DateService.getExpiryDate(edtInstallationDate.getText().toString())));
                requestBodyParams.put(Constant.Fields.LATITUDE, String.valueOf(gpsTracker.getLatitude()));
                requestBodyParams.put(Constant.Fields.LONGITUDE, String.valueOf(gpsTracker.getLongitude()));
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

    }

    private boolean Validate() {

        if (VU.isEmpty(edtInstalledByName)) {
            edtInstalledByName.requestFocus();
            edtInstalledByName.setError("Please Enter Installed By Name");
            return false;
        } else if (VU.isEmpty(edtMachineOperatorName)) {
            edtMachineOperatorName.requestFocus();
            edtMachineOperatorName.setError("Please enter machine operator name");
            return false;
        } else if (VU.isEmpty(edtMachineOperatorMobile)) {
            edtMachineOperatorMobile.requestFocus();
            edtMachineOperatorMobile.setError("Please enter machine operator mobile number.");
            return false;
        } else if (VU.isEmpty(edtInstallationDate)) {
            edtInstallationDate.requestFocus();
            edtInstallationDate.setError("Please select installation date");
            return false;
        } else if (spinnerLocation.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please select location", Toast.LENGTH_SHORT).show();
            return false;
        } else if (VU.isEmpty(edtAddress)) {
            edtAddress.requestFocus();
            edtAddress.setError("Please enter address.");
            return false;
        }
        return true;
    }

    private void handleAPIResponse(String response, VolleyError error, String status) {

        Log.e("response_Log_Inst", ":" + response);

        if (status.equals("response")) {
            try {
                Common_Update_Response commonUpdateResponse = (Common_Update_Response) Utils.parseResponse(response, Common_Update_Response.class);
                if (commonUpdateResponse.getSuccess()) {
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
