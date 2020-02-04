package com.abhaybmicoc.app.activity;

import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Dialog;
import android.view.Window;
import android.os.AsyncTask;
import android.widget.Toast;
import android.app.Activity;
import android.widget.Button;
import android.os.SystemClock;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.content.Intent;
import android.app.AlertDialog;
import android.widget.TextView;
import android.content.Context;
import android.widget.ListView;
import android.widget.ImageView;
import android.app.ProgressDialog;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.app.DownloadManager;
import android.speech.tts.TextToSpeech;
import android.content.DialogInterface;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.bluetooth.BluetoothAdapter;
import android.graphics.drawable.ColorDrawable;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.services.DateService;
import com.abhaybmicoc.app.services.SharedPreferenceService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.model.PrintDataOld;
import com.abhaybmicoc.app.model.PrintData;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;
import com.abhaybmicoc.app.adapter.PrintPreviewAdapter;
import com.abhaybmicoc.app.printer.evolute.bluetooth.BluetoothComm;
import com.abhaybmicoc.app.printer.esys.pridedemoapp.Act_GlobalPool;

import com.abhaybmicoc.app.utils.Constant;
import com.android.volley.Request;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.StringRequest;

import com.prowesspride.api.Printer_GEN;
import com.prowesspride.api.Setup;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.InputStream;
import java.io.OutputStream;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.abhaybmicoc.app.utils.ApiUtils.PREFERENCE_THERMOMETERDATA;

public class PrintPreviewActivity extends Activity implements TextToSpeech.OnInitListener {
    // region Variables

    private Context context = PrintPreviewActivity.this;

    @BindView(R.id.dobTV)
    TextView dobTV;
    @BindView(R.id.nameTV)
    TextView nameTV;
    @BindView(R.id.valueTV)
    TextView valueTV;
    @BindView(R.id.rangeTV)
    TextView rangeTV;
    @BindView(R.id.txtWish)
    TextView txtWish;
    @BindView(R.id.heightTV)
    TextView heightTV;
    @BindView(R.id.resultTV)
    TextView resultTV;
    @BindView(R.id.genderTV)
    TextView genderTV;
    @BindView(R.id.parameterTV)
    TextView parameterTV;

    @BindView(R.id.lV)
    ListView lV;
    @BindView(R.id.topLL)
    LinearLayout topLL;

    private Button btnOk;
    private Button btnConfirm;
    private Button btnUnicode11;
    @BindView(R.id.homebtn)
    Button btnHome;
    @BindView(R.id.printbtn)
    Button btnPrint;

    @BindView(R.id.btn_Reconnect)
    Button btnReconnect;

    private LinearLayout llprog;
    @BindView(R.id.buttonLL)
    LinearLayout buttonLL;
    @BindView(R.id.headerLL)
    LinearLayout headerLL;

    private SharedPreferences sharedPreferencesToken;
    private SharedPreferences sharedPreferencesSugar;
    private SharedPreferences sharedPreferencesActofit;
    private SharedPreferences sharedPreferencesOximeter;
    private SharedPreferences sharedPreferencesHemoglobin;
    private SharedPreferences sharedPreferencesThermometer;
    private SharedPreferences sharedPreferencesPersonalData;
    private SharedPreferences sharedPreferencesBloodPressure;

    private ImageView ivDownload;

    private int age;
    private int height;
    private int iRetVal;
    public static int iWidth;
    public static final int DEVICE_NOTCONNECTED = -100;
    private static final int PERMISSION_STORAGE_CODE = 1000;

    public Dialog dlgCustomdialog;
    public static ProgressBar pbProgress;

    private double weight;
    private double standardWeighRangeTo;
    private double standardWeighRangeFrom;
    private double standardMetabolism;

    private String txtSpeak;
    private String fileName;
    private String bmrResult;
    private String bmiResult;
    private String printerText;
    private String downloadUrl;
    private String currentDate;
    private String currentTime;
    private String sugarResult;
    private String pulseResult;
    private String standardBMR;
    private String oxygenResult;
    private String weightResult;
    private String proteinResult;
    private String metaageResult;
    private String bodyfatResult;
    private String bonemassResult;
    private String bodywaterResult;
    private String diastolicResult;
    private String standardGlucose;
    private String subcutaneousFat;
    private String standardBodyFat;
    private String tempratureResult;
    private String musclemassResult;
    private String standardWeightTo;
    private String standardBoneMass;
    private String hemoglobinResult;
    private String standardHemoglobin;
    private String standardBodyWater;
    private String visceralfatResult;
    private String standardWeightFrom;
    private String standardMuscleMass;
    private String subcutaneousResult;
    private String bloodpressureResult;
    private String standardVisceralFat;
    private String standardWeightRange;
    private String skeletonmuscleResult;
    private String standardSkeltonMuscle;
    private String fatfreeweightResult = "";

    public static Printer_GEN ptrGen;
    private TextToSpeech textToSpeech;
    private AndMedical_App_Global mGP = null;
    public static BluetoothDevice mBDevice = null;
    public static BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();

    List<PrintDataOld> printDataList = new ArrayList<>();
    List<PrintData> printDataListNew = new ArrayList<>();
    private Hashtable<String, String> mhtDeviceInfo = new Hashtable<String, String>();
    private String TAG = "PrinPriviewActivity";

    // endregion

    // region Events

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printpreview);
        ButterKnife.bind(this);

        setupUI();
        setupEvents();
        initializeData();
        requestGPSPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //reinitialization of the textToSpeech engine for voice command
        textToSpeech = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        startTextToSpeech(status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopTextToSpeech();
    }


    // endregion

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestGPSPermission() {
        try {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            String provider = Settings.Secure.getString(getContentResolver(), LocationManager.GPS_PROVIDER);
            Log.e("provider_GPS", ":" + provider + "  :  " + statusOfGPS);

            if (!statusOfGPS) {
                Log.e("GPS_Permission", " Location providers: " + provider);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
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

    // region Initialization methods

    private void setupUI() {
        ivDownload = findViewById(R.id.iv_download);
    }

    private void setupEvents() {
        btnHome.setOnClickListener(view -> goToHome());

        btnPrint.setOnClickListener(view -> {
            Toast.makeText(this, "Getting Printout", Toast.LENGTH_SHORT).show();

            EnterTextAsyc asynctask = new EnterTextAsyc();
            asynctask.execute(0);

            txtSpeak = "Please collect your result receipt";
            speakOut(txtSpeak);
        });

        ivDownload.setOnClickListener(view -> downloadFile(fileName));

        btnReconnect.setOnClickListener(view -> autoConnectPrinter());
    }

    private void initializeData() {
        textToSpeech = new TextToSpeech(this, this);

        mGP = ((AndMedical_App_Global) getApplicationContext());

        txtSpeak = "Please click on the print button to get your printout";


        connectToSavedPrinter();


        gettingDataObjects();
        calculations();
        setNewList();
        getStandardRange();
        setStaticData();
        getPrintData();
        getResults();
        postData();

    }


    private void connectToSavedPrinter() {
        SharedPreferences data = getSharedPreferences("printer", MODE_PRIVATE);
        Log.e("Connect_Printer", " :0: ");

        if (data.getString("NAME", "").length() > 0) {
            Log.e("Connect_Printer", " :Name: " + data.getString("NAME", ""));
            Log.e("Connect_Printer_BOND", " :BOND: " + data.getString("BOND", ""));

            Log.e("Connect_Printer", " :BOND: " + data.getString("BOND", ""));

            mBDevice = mBT.getRemoteDevice(data.getString("MAC", ""));

            Log.e("Connect_Printer", " :Address:  " + mBDevice.getAddress());

            Log.e("PrinterConnected_Print", ":" + getIntent().getStringExtra("is_PrinterConnected"));
            if (getIntent().getStringExtra("is_PrinterConnected").equals("false")) {
                printerBond();
                autoConnectPrinter();
            } else {
                printerActivation();
            }
        }
    }


    private void autoConnectPrinter() {
        mGP.closeConn();
        new ConnSocketTask().execute(mBDevice.getAddress());
    }

    // endregion

    // region Logical methods

    /**
     * @param text
     */
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     * @param status
     */
    private void startTextToSpeech(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(txtSpeak);
            }

        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    /**
     *
     */
    private void stopTextToSpeech() {
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        } catch (Exception e) {
            System.out.println("onPauseException" + e.getMessage());
        }
    }

    private void getResults() {
        if (SharedPreferenceService.isMalePatient(context)) {
            /* Calculate result as per male gender */

            getMaleWeightResult();
            getMaleBMIResult();
            getMaleBodyFatResult();
            getMaleSubcutaneousResult();
            getMaleVisceralFatResult();
            getMaleBodyWaterResult();
            getMaleSkeletalMuscle();
            getMaleMuscleMassResult();
            getMaleBoneMassResult();
            getMaleProteinResult();
            getMaleBMRResult();
            getMaleGlucoseResult();
            getMaleHemoglobinResult();

            getMetaAgeResult();
            getSystolicBloodPressureResult();
            getDiastolicBloodPressureResult();
            getOximeterResult();
            getPulseResult();
            getTemperatureResult();

        } else {
            /* Calculate result as per female gender */

            getFemaleWeightResult();
            getFemaleBMIResult();
            getFemaleBodyFatResult();
            getFemaleSubcutaneousResult();
            getMaleVisceralFatResult();
            getFemaleBodyWaterResult();
            getFemaleSkeletalMuscle();
            getFemaleMuscleMassResult();
            getFemaleBoneMassResult();
            getMaleProteinResult();
            getMaleBMRResult();
            getMaleGlucoseResult();
            getFemaleHemoglobinResult();

            getMetaAgeResult();
            getSystolicBloodPressureResult();
            getDiastolicBloodPressureResult();
            getOximeterResult();
            getPulseResult();
            getTemperatureResult();
        }
    }

    // endregion

    // region Gender common calculation methods

    private void getMetaAgeResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.META_AGE)) {
            double metaAge = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.META_AGE);

            if (metaAge <= age)
                metaageResult = "Standard";
            else
                metaageResult = "Not up to standard";
        } else
            metaageResult = "NA";
    }

    private void getTemperatureResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_THERMOMETERDATA, Constant.Fields.TEMPERATURE)) {
            double temperature = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_THERMOMETERDATA, Constant.Fields.TEMPERATURE);

            if (temperature > 99)
                tempratureResult = "High";
            else if (temperature >= 97 && temperature <= 99)
                tempratureResult = "Standard";
            else
                tempratureResult = "Low";
        } else
            tempratureResult = "NA";
    }

    private void getPulseResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_BLOODPRESSURE, Constant.Fields.PULSE_RATE)) {
            double pulseRate = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_BLOODPRESSURE, Constant.Fields.PULSE_RATE);

            if (pulseRate > 100)
                pulseResult = "High";
            else if (pulseRate >= 60 && pulseRate <= 100)
                pulseResult = "Standard";
            else
                pulseResult = "Low";
        } else
            pulseResult = "NA";
    }

    private void getOximeterResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_PULSE, Constant.Fields.BLOOD_OXYGEN)) {
            double bloodOxygen = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_PULSE, Constant.Fields.BLOOD_OXYGEN);

            if (bloodOxygen >= 94)
                oxygenResult = "Standard";
            else if (bloodOxygen < 94)
                oxygenResult = "Low";
        } else
            oxygenResult = "NA";
    }

    private void getSystolicBloodPressureResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_BLOODPRESSURE, Constant.Fields.BLOOD_PRESSURE_SYSTOLIC)) {
            double bloodPressureSystolic = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_BLOODPRESSURE, Constant.Fields.BLOOD_PRESSURE_SYSTOLIC);

            if (bloodPressureSystolic > 139)
                bloodpressureResult = "High";
            else if (bloodPressureSystolic >= 90 && bloodPressureSystolic <= 139)
                bloodpressureResult = "Standard";
            else
                bloodpressureResult = "Low";
        } else
            bloodpressureResult = "NA";
    }

    private void getDiastolicBloodPressureResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_BLOODPRESSURE, Constant.Fields.BLOOD_PRESSURE_DIASTOLIC)) {
            double bloodPressureDiastolic = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_BLOODPRESSURE, Constant.Fields.BLOOD_PRESSURE_DIASTOLIC);

            if (bloodPressureDiastolic > 89)
                diastolicResult = "High";
            else if (bloodPressureDiastolic >= 60 && bloodPressureDiastolic <= 89)
                diastolicResult = "Standard";
            else
                diastolicResult = "Low";
        } else
            diastolicResult = "NA";
    }

    // endregion

    // region Female calculation methods

    private void getFemaleSubcutaneousResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.SUBCUTANEOUS_FAT)) {
            double subcutaneousFat = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.SUBCUTANEOUS_FAT);

            if (subcutaneousFat > 26.7)
                subcutaneousResult = "High";
            else if (subcutaneousFat >= 18.5 && subcutaneousFat <= 26.7)
                subcutaneousResult = "Standard";
            else
                subcutaneousResult = "Low";
        } else
            subcutaneousResult = "NA";
    }

    private void getFemaleHemoglobinResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_HEMOGLOBIN, Constant.Fields.SUBCUTANEOUS_FAT)) {
            double hemoglobin = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_HEMOGLOBIN, Constant.Fields.SUBCUTANEOUS_FAT);

            if (hemoglobin > 15.1)
                hemoglobinResult = "High";
            else if (hemoglobin >= 12.1 && hemoglobin <= 15.1)
                hemoglobinResult = "Standard";
            else
                hemoglobinResult = "Low";
        } else
            hemoglobinResult = "NA";
    }

    private void getFemaleBoneMassResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BONE_MASS)) {
            double boneMass = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BONE_MASS);

            if (weight > 60) {
                standardBoneMass = "2.3 - 2.7kg";

                if (boneMass > 2.7)
                    bonemassResult = "High";
                else if (boneMass >= 2.3 && boneMass <= 2.7)
                    bonemassResult = "Standard";
                else
                    bonemassResult = "Low";
            } else if (weight >= 45 && weight <= 60) {
                standardBoneMass = "2.0-2.4kg";

                if (boneMass > 2.4)
                    bonemassResult = "High";
                else if (boneMass >= 2.0 && boneMass <= 2.4)
                    bonemassResult = "Standard";
                else
                    bonemassResult = "Low";
            } else if (weight < 45) {
                standardBoneMass = "1.6 - 2.0kg";

                if (boneMass > 2.0)
                    bonemassResult = "High";
                else if (boneMass >= 1.6 && boneMass <= 2.0)
                    bonemassResult = "Standard";
                else
                    bonemassResult = "Low";
            }
        } else
            bonemassResult = "NA";
    }

    private void getFemaleMuscleMassResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.MUSCLE_MASS)) {
            double muscleMass = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.MUSCLE_MASS);

            if (height > 170) {
                standardMuscleMass = "49.4-59.5kg";

                if (muscleMass > 59.5)
                    musclemassResult = "High";
                else if (muscleMass >= 49.4 && muscleMass <= 59.5)
                    musclemassResult = "Standard";
                else if (muscleMass < 49.4)
                    musclemassResult = "Low";
            } else if (height <= 170 && height >= 160) {
                standardMuscleMass = "44-52.4kg";

                if (muscleMass > 52.4)
                    musclemassResult = "High";
                else if (muscleMass >= 44 && muscleMass <= 52.4)
                    musclemassResult = "Standard";
                else if (muscleMass < 44)
                    musclemassResult = "Low";
            } else if (height < 160) {
                musclemassResult = "38.5-46.5kg";

                if (muscleMass > 46.5)
                    musclemassResult = "High";
                else if (muscleMass >= 38.5 && muscleMass <= 46.5)
                    musclemassResult = "Standard";
                else if (muscleMass < 38.5)
                    musclemassResult = "Low";
            }
        } else
            musclemassResult = "NA";
    }

    private void getFemaleSkeletalMuscle() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.SKELETAL_MUSCLE)) {
            double skeletalMuscle = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.SKELETAL_MUSCLE);

            if (skeletalMuscle > 50)
                skeletonmuscleResult = "High";
            else if (skeletalMuscle >= 40 && skeletalMuscle <= 50)
                skeletonmuscleResult = "Standard";
            else if (skeletalMuscle < 40)
                skeletonmuscleResult = "Low";
        } else
            skeletonmuscleResult = "NA";
    }

    private void getFemaleBodyWaterResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BODY_WATER)) {
            double bodyWater = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BODY_WATER);

            if (bodyWater > 60)
                bodywaterResult = "High";
            else if (bodyWater >= 45 && bodyWater <= 60)
                bodywaterResult = "Standard";
            else if (bodyWater < 45)
                bodywaterResult = "Low";
        } else
            bodywaterResult = "NA";
    }

    private void getFemaleBodyFatResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BODY_FAT)) {
            double bodyFat = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BODY_FAT);

            if (bodyFat > 36)
                bodyfatResult = "Seriously High";
            else if (bodyFat > 30 && weight <= 36)
                bodyfatResult = "High";
            else if (bodyFat > 21 && weight <= 30)
                bodyfatResult = "Standard";
            else
                bodyfatResult = "Low";
        } else
            bodyfatResult = "NA";
    }

    private void getFemaleBMIResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BMI)) {
            double bmi = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BMI);

            if (bmi > 25)
                bmiResult = "Seriously High";
            else if (bmi > 18.5 && weight <= 25)
                bmiResult = "High";
            else
                bmiResult = "Low";
        } else
            bmiResult = "NA";
    }

    private void getFemaleWeightResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.WEIGHT)) {
            double weight = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.WEIGHT);

            if (weight > standardWeighRangeTo)
                weightResult = "High";
            else if (weight > standardWeighRangeTo && weight <= standardWeighRangeTo)
                weightResult = "Standard";
            else
                weightResult = "Low";
        } else
            weightResult = "NA";
    }

    // endregion

    // region Male calculation methods

    private void getMaleHemoglobinResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_HEMOGLOBIN, Constant.Fields.HEMOGLOBIN)) {
            double hemoglobin = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_HEMOGLOBIN, Constant.Fields.HEMOGLOBIN);

            if (hemoglobin > 17.2) {
                hemoglobinResult = "High";
            } else if (hemoglobin >= 13.8 && hemoglobin <= 17.2) {
                hemoglobinResult = "Standard";
            } else {
                hemoglobinResult = "Low";
            }
        } else
            hemoglobinResult = "NA";

    }

    private void getMaleGlucoseResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_BIOSENSE, Constant.Fields.GLUCOSE_TYPE)) {

            double sugar = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_BIOSENSE, Constant.Fields.SUGAR);
            String glucoseType = SharedPreferenceService.getString(context, ApiUtils.PREFERENCE_BIOSENSE, Constant.Fields.GLUCOSE_TYPE);

            if (glucoseType.equals("Fasting (Before Meal)")) {
                standardGlucose = "70-100mg/dl(Fasting)";

                if (sugar > 100) {
                    sugarResult = "High";
                } else if (sugar >= 70 && sugar <= 100) {
                    sugarResult = "Standard";
                } else {
                    sugarResult = "Low";
                }
            } else if (glucoseType.equals("Post Prandial (After Meal)")) {
                standardGlucose = "70-140 mg/dl(Post Meal)";

                if (sugar > 140) {
                    sugarResult = "High";
                } else if (sugar >= 70 && sugar <= 140) {
                    sugarResult = "Standard";
                } else {
                    sugarResult = "Low";
                }
            } else if (glucoseType.equals("Random (Not Sure)")) {
                standardGlucose = "79-160 mg/dl(Random)";

                if (sugar > 160)
                    sugarResult = "High";
                else if (sugar >= 79 && sugar <= 160)
                    sugarResult = "Standard";
                else
                    sugarResult = "Low";
            }
        } else
            sugarResult = "NA";
    }

    private void getMaleBMRResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BMR)) {
            double bmr = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BMR);

            if (bmr >= standardMetabolism)
                bmrResult = "High";
            else
                bmrResult = "Not up to mark";
        } else
            bmrResult = "NA";
    }

    private void getMaleProteinResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.PROTEIN)) {
            double protein = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.PROTEIN);

            if (protein > 18)
                proteinResult = "High";
            else if (protein >= 16 && protein <= 18)
                proteinResult = "Standard";
            else if (protein < 16)
                proteinResult = "Low";
        } else
            proteinResult = "NA";
    }

    private void getMaleBoneMassResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BONE_MASS)) {
            double boneMass = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BONE_MASS);

            if (weight > 75) {
                if (boneMass > 3.4) {
                    bonemassResult = "High";
                } else if (boneMass <= 3.4 && boneMass >= 3.0) {
                    bonemassResult = "Standard";
                } else {
                    bonemassResult = "Low";
                }
            } else if (weight <= 75 && weight >= 60) {
                if (boneMass > 3.1) {
                    bonemassResult = "High";
                } else if (boneMass <= 3.1 && boneMass >= 2.7) {
                    bonemassResult = "Standard";
                } else {
                    bonemassResult = "Low";
                }
            } else if (weight < 60) {
                if (boneMass > 2.7) {
                    bonemassResult = "High";
                } else if (boneMass <= 2.7 && boneMass >= 2.3) {
                    bonemassResult = "Standard";
                } else {
                    bonemassResult = "Low";
                }
            }
        } else
            bonemassResult = "NA";
    }

    private void getMaleMuscleMassResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.MUSCLE_MASS)) {
            double muscleMass = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.MUSCLE_MASS);

            if (height > 170) {
                standardMuscleMass = "49.4-59.5kg";

                if (muscleMass > 59.5)
                    musclemassResult = "High";
                else if (muscleMass >= 49.4 && muscleMass <= 59.5)
                    musclemassResult = "Standard";
                else
                    musclemassResult = "Low";
            } else if (height <= 170 && height >= 160) {
                standardMuscleMass = "44-52.4kg";

                if (muscleMass > 52.4)
                    musclemassResult = "High";
                else if (muscleMass >= 44 && muscleMass <= 52.4)
                    musclemassResult = "Standard";
                else
                    musclemassResult = "Low";
            } else if (height < 160) {
                musclemassResult = "38.5-46.5kg";

                if (muscleMass > 46.5)
                    musclemassResult = "High";
                else if (muscleMass >= 38.5 && muscleMass <= 46.5)
                    musclemassResult = "Standard";
                else
                    musclemassResult = "Low";
            }
        } else
            musclemassResult = "NA";
    }

    private void getMaleSkeletalMuscle() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.SKELETAL_MUSCLE)) {
            double skeletalMuscle = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.SKELETAL_MUSCLE);

            if (skeletalMuscle > 59)
                skeletonmuscleResult = "High";
            else if (skeletalMuscle >= 49 && skeletalMuscle <= 59)
                skeletonmuscleResult = "Standard";
            else if (skeletalMuscle < 49)
                skeletonmuscleResult = "Low";
        } else
            skeletonmuscleResult = "NA";
    }

    private void getMaleBodyWaterResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BODY_WATER)) {
            double bodyWater = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BODY_WATER);

            if (bodyWater > 65)
                bodywaterResult = "High";
            else if (bodyWater >= 55 && bodyWater <= 65)
                bodywaterResult = "Standard";
            else if (bodyWater < 55)
                bodywaterResult = "Low";
        } else
            bodywaterResult = "NA";
    }

    private void getMaleVisceralFatResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.VISCERAL_FAT)) {
            double visceralFat = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.VISCERAL_FAT);

            if (visceralFat > 14)
                visceralfatResult = "Seriously High";
            else if (visceralFat > 9)
                visceralfatResult = "High";
            else
                visceralfatResult = "Standard";
        } else
            visceralfatResult = "NA";
    }

    private void getMaleSubcutaneousResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.SUBCUTANEOUS_FAT)) {
            double subcutaneousFat = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.SUBCUTANEOUS_FAT);

            // TODO: Check these values
            if (subcutaneousFat > 16.7)
                subcutaneousResult = "High";
            else if (subcutaneousFat > 8.6 && subcutaneousFat <= 16.7)
                subcutaneousResult = "Standard";
            else
                subcutaneousResult = "Low";
        } else
            subcutaneousResult = "NA";
    }

    private void getMaleBodyFatResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BODY_FAT)) {
            double bodyFat = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BODY_FAT);

            if (bodyFat > 26)
                bodyfatResult = "Seriously High";
            else if (bodyFat >= 22 && bodyFat <= 26)
                bodyfatResult = "High";
            else if (bodyFat > 11 && bodyFat <= 21)
                bodyfatResult = "Standard";
            else
                bodyfatResult = "Low";
        } else
            bodyfatResult = "NA";
    }

    private void getMaleBMIResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BMI)) {
            double bmi = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.BMI);

            if (bmi > 25)
                bmiResult = "High";
            else if (bmi > 18.5 && bmi <= 25)
                bmiResult = "Standard";
            else
                bmiResult = "Low";
        } else
            bmiResult = "NA";
    }

    private void getMaleWeightResult() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.WEIGHT)) {
            double weight = SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.WEIGHT);

            if (weight > standardWeighRangeTo)
                weightResult = "High";
            else if (weight > standardWeighRangeFrom && weight <= standardWeighRangeTo)
                weightResult = "Standard";
            else
                weightResult = "Low";
        } else
            weightResult = "NA";
    }

    // endregion

    // region Printer methods

    private void printerBond() {
        try {
            SharedPreferences data = getSharedPreferences("printer", MODE_PRIVATE);

            if (data.getString("NAME", "").length() > 0) {
                this.mhtDeviceInfo.put("NAME", data.getString("NAME", ""));
                this.mhtDeviceInfo.put("MAC", data.getString("MAC", ""));
                this.mhtDeviceInfo.put("COD", data.getString("COD", ""));
                this.mhtDeviceInfo.put("RSSI", data.getString("RSSI", ""));
                this.mhtDeviceInfo.put("DEVICE_TYPE", data.getString("DEVICE_TYPE", ""));
                this.mhtDeviceInfo.put("BOND", data.getString("BOND", ""));

                if (!this.mhtDeviceInfo.get("BOND").equals(getString(R.string.actDiscovery_bond_nothing))) {
                    mBDevice = mBT.getRemoteDevice(this.mhtDeviceInfo.get("MAC"));
                }
            }

        } catch (Exception e) {
            // TODO: Handle exception here
        }
    }

    private void printerActivation() {
        try {
            Log.e("PrinterActivation", ":0:");

            iWidth = getWindowManager().getDefaultDisplay().getWidth();
            InputStream input = BluetoothComm.misIn;
            OutputStream outstream = BluetoothComm.mosOut;
            ptrGen = new Printer_GEN(Act_GlobalPool.setup, outstream, input);

            Log.e("PrinterActivation", ":ptrgen:" + ptrGen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // endregion

    // region Range methods

    private void getStandardRange() {
        height = getHeight();
        weight = getWeight();

        double standardWeightMen = ((height - 80) * 0.7);
        standardWeightMen = Double.parseDouble(new DecimalFormat("#.##").format(standardWeightMen));

        double standardWeightFemale = (((height * 1.37) - 110) * 0.45);
        standardWeightFemale = Double.parseDouble(new DecimalFormat("#.##").format(standardWeightFemale));

        glucoseRange();

        if (SharedPreferenceService.isMalePatient(context))
            maleRange(standardWeightMen);
        else
            femaleRange(standardWeightFemale);
    }

    private void glucoseRange() {
        if (sharedPreferencesSugar.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Fasting (Before Meal)"))
            standardGlucose = "70-100 mg/dl(Fasting)";
        else if (sharedPreferencesSugar.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Post Prandial (After Meal)"))
            standardGlucose = "70-140 mg/dl(Post Meal)";
        else if (sharedPreferencesSugar.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Random (Not Sure)"))
            standardGlucose = "79-160 mg/dl(Random)";
        else
            standardGlucose = "";
    }

    private void femaleRange(double standardWeightFemale) {
        standardWeighRangeFrom = (0.90 * standardWeightFemale);
        standardWeighRangeFrom = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeFrom));

        standardWeighRangeTo = (1.09 * standardWeightFemale);
        standardWeighRangeTo = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeTo));

        standardWeightRange = standardWeighRangeFrom + "-" + standardWeighRangeTo;

        if (height > 160)
            standardMuscleMass = "36.4-42.5kg";
        else if (height <= 160 && height >= 150)
            standardMuscleMass = "32.9-37.5kg";
        else if (height < 150)
            standardMuscleMass = "29.1-34.7kg";
        else
            standardMuscleMass = "";

        if (weight > 60)
            standardBoneMass = "2.3 - 2.7kg";
        else if (weight <= 60 && weight >= 45)
            standardBoneMass = "2.0-2.4kg";
        else if (weight < 45)
            standardBoneMass = "1.6 - 2.0kg";
        else
            standardBoneMass = "";

        if (age >= 70) {
            standardMetabolism = 20.7 * weight;
            standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));
        } else if (age >= 50 && age <= 69) {
            standardMetabolism = 20.7 * weight;
            standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));
        } else if (age >= 30 && age <= 49) {
            standardMetabolism = 21.7 * weight;
            standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));
        } else if (age >= 18 && age <= 29) {
            standardMetabolism = 23.6 * weight;
            standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));
        }

        standardVisceralFat = "<=9";
        standardBodyFat = "21-30(%)";
        standardBodyFat = "21-30(%)";
        standardBodyWater = "45-60(%)";
        subcutaneousFat = "18.5-26.7(%)";
        standardSkeltonMuscle = "40-50(%)";
        standardHemoglobin = "12-15gm/dl";
        standardBMR = " > = " + standardMetabolism + " kcal";
    }

    private void maleRange(double standardWeightMen) {
        standardBodyFat = "11-21(%)";
        standardBodyWater = "55-65(%)";
        standardSkeltonMuscle = "49-59(%)";
        standardHemoglobin = "13-17gm/dl";

        standardWeighRangeFrom = (0.90 * standardWeightMen);
        standardWeighRangeFrom = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeFrom));

        standardWeighRangeTo = (1.09 * standardWeightMen);
        standardWeighRangeTo = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeTo));

        standardWeightRange = standardWeighRangeFrom + "-" + standardWeighRangeTo;

        if (height > 170)
            standardMuscleMass = "49.4-59.5kg";
        else if (height <= 170 && height >= 160)
            standardMuscleMass = "44-52.4kg";
        else if (height < 160)
            standardMuscleMass = "38.5-46.5kg";
        else
            standardMuscleMass = "";

        if (weight > 75)
            standardBoneMass = "3.0-3.4kg";
        else if (weight <= 75 && weight >= 60)
            standardBoneMass = "2.7-3.1kg";
        else if (weight < 60)
            standardBoneMass = "2.3-2.7kg";
        else
            standardBoneMass = "";

        if (age >= 70) {
            standardMetabolism = 21.5 * weight;
            standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));
        } else if (age >= 50 && age <= 69) {
            standardMetabolism = 21.5 * weight;
            standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));
        } else if (age >= 30 && age <= 49) {
            standardMetabolism = 22.3 * weight;
            standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));
        } else if (age >= 18 && age <= 29) {
            standardMetabolism = 24 * weight;
            standardMetabolism = Double.parseDouble(new DecimalFormat("#.##").format(standardMetabolism));
        }

        standardBodyFat = "11-21(%)";
        standardVisceralFat = "< = 9";
        subcutaneousFat = "8.6-16.7(%)";
        standardBMR = " > =" + standardMetabolism + "kcal";
    }

    // endregion

    private void setStaticData() {
        nameTV.setText("Name :" + sharedPreferencesPersonalData.getString(Constant.Fields.NAME, ""));
        heightTV.setText("Height :" + sharedPreferencesActofit.getString(Constant.Fields.HEIGHT, ""));
        genderTV.setText("Gender :" + sharedPreferencesPersonalData.getString(Constant.Fields.GENDER, ""));
        dobTV.setText("DOB :" + sharedPreferencesPersonalData.getString(Constant.Fields.DATE_OF_BIRTH, ""));
    }

    @SuppressLint("SimpleDateFormat")
    private void calculations() {
        age = getAge();

        SimpleDateFormat formatterCurrent = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();

        currentDate = formatterCurrent.format(date);
        currentTime = getCurrentTime();
    }

    private int getHeight() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.HEIGHT))
            return SharedPreferenceService.getInteger(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.HEIGHT);
        else
            return 0;
    }

    private double getWeight() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.WEIGHT))
            return SharedPreferenceService.getDouble(context, ApiUtils.PREFERENCE_ACTOFIT, Constant.Fields.WEIGHT);
        else
            return 0;
    }

    private void getPrintData() {
        printerText = getPrintText();
    }

    private void setNewList() {
        try {
            printDataListNew.add(new PrintData("Weight", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.WEIGHT, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.WEIGHT, ""))));
            printDataListNew.add(new PrintData("BMI", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.BMI, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.BMI, ""))));
            printDataListNew.add(new PrintData("Body fat", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.BODY_FAT, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.BODY_FAT, ""))));
            printDataListNew.add(new PrintData("Fat Free weight", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.FAT_FREE_WEIGHT, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.FAT_FREE_WEIGHT, ""))));
            printDataListNew.add(new PrintData("Subcutaneous Fat", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.SUBCUTANEOUS_FAT, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.SUBCUTANEOUS_FAT, ""))));
            printDataListNew.add(new PrintData("Visceral Fat", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.VISCERAL_FAT, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.VISCERAL_FAT, ""))));
            printDataListNew.add(new PrintData("Body water", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.BODY_WATER, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.BODY_WATER, ""))));
            printDataListNew.add(new PrintData("Skeleton muscle", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.SKELETAL_MUSCLE, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.SKELETAL_MUSCLE, ""))));
            printDataListNew.add(new PrintData("Protein", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.PROTEIN, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.PROTEIN, ""))));
            printDataListNew.add(new PrintData("Metabolic Age", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.META_AGE, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.META_AGE, ""))));
            printDataListNew.add(new PrintData("Health Score", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.HEALTH_SCORE, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.HEALTH_SCORE, ""))));
            printDataListNew.add(new PrintData("BMR", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.BMR, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.BMR, ""))));
            printDataListNew.add(new PrintData("Physique", 0.0));
            printDataListNew.add(new PrintData("Muscle Mass", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.MUSCLE_MASS, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.MUSCLE_MASS, ""))));
            printDataListNew.add(new PrintData("Bone Mass", TextUtils.isEmpty(sharedPreferencesActofit.getString(Constant.Fields.BONE_MASS, "")) ? 0 : Double.parseDouble(sharedPreferencesActofit.getString(Constant.Fields.BONE_MASS, ""))));
            printDataListNew.add(new PrintData("Body Temp", TextUtils.isEmpty(sharedPreferencesThermometer.getString(Constant.Fields.TEMPERATURE, "")) ? 0 : Double.parseDouble(sharedPreferencesThermometer.getString(Constant.Fields.TEMPERATURE, ""))));
            printDataListNew.add(new PrintData("Systolic", TextUtils.isEmpty(sharedPreferencesBloodPressure.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "")) ? 0 : Double.parseDouble(sharedPreferencesBloodPressure.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, ""))));
            printDataListNew.add(new PrintData("Diastolic", TextUtils.isEmpty(sharedPreferencesBloodPressure.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, "")) ? 0 : Double.parseDouble(sharedPreferencesBloodPressure.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, ""))));
            printDataListNew.add(new PrintData("Pulse Oximeter", TextUtils.isEmpty(sharedPreferencesOximeter.getString(Constant.Fields.BLOOD_OXYGEN, "")) ? 0 : Double.parseDouble(sharedPreferencesOximeter.getString(Constant.Fields.BLOOD_OXYGEN, ""))));
            printDataListNew.add(new PrintData("Pulse ", TextUtils.isEmpty(sharedPreferencesBloodPressure.getString(Constant.Fields.PULSE_RATE, "")) ? 0 : Double.parseDouble(sharedPreferencesBloodPressure.getString(Constant.Fields.PULSE_RATE, ""))));
            printDataListNew.add(new PrintData("Blood Glucose", TextUtils.isEmpty(sharedPreferencesSugar.getString(Constant.Fields.SUGAR, "")) ? 0 : Double.parseDouble(sharedPreferencesSugar.getString(Constant.Fields.SUGAR, ""))));
            printDataListNew.add(new PrintData("Hemoglobin", TextUtils.isEmpty(sharedPreferencesHemoglobin.getString(Constant.Fields.HEMOGLOBIN, "")) ? 0 : Double.parseDouble(sharedPreferencesHemoglobin.getString(Constant.Fields.HEMOGLOBIN, ""))));

            lV.setAdapter(new PrintPreviewAdapter(this, R.layout.printlist_item, printDataListNew));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUtils.PRINT_POST_URL,
                response -> {
                    readFileName(response);

                    Toast.makeText(getApplicationContext(), "Data Uploaded on Server", Toast.LENGTH_SHORT).show();
                },
                volleyError -> {
                    // TODO: Handle error
                }) {
            @Override
            public Map getHeaders() {
                HashMap headers = new HashMap();

                String bearer = "Bearer ".concat(sharedPreferencesToken.getString(Constant.Fields.TOKEN, ""));
                headers.put("Authorization", bearer);

                return headers;
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                try {
                    params.put(Constant.Fields.BMI, sharedPreferencesActofit.getString(Constant.Fields.BMI, ""));
                    params.put(Constant.Fields.BMR, sharedPreferencesActofit.getString(Constant.Fields.BMR, ""));
                    params.put(Constant.Fields.SUGAR, sharedPreferencesSugar.getString(Constant.Fields.SUGAR, ""));
                    params.put(Constant.Fields.HEIGHT, sharedPreferencesActofit.getString(Constant.Fields.HEIGHT, ""));
                    params.put(Constant.Fields.WEIGHT, sharedPreferencesActofit.getString(Constant.Fields.WEIGHT, ""));
                    params.put(Constant.Fields.PROTEIN, sharedPreferencesActofit.getString(Constant.Fields.PROTEIN, ""));
                    params.put(Constant.Fields.META_AGE, sharedPreferencesActofit.getString(Constant.Fields.META_AGE, ""));
                    params.put(Constant.Fields.BODY_FAT, sharedPreferencesActofit.getString(Constant.Fields.BODY_FAT, ""));
                    params.put(Constant.Fields.PHYSIQUE, sharedPreferencesActofit.getString(Constant.Fields.PHYSIQUE, ""));
                    params.put(Constant.Fields.GENDER, sharedPreferencesPersonalData.getString(Constant.Fields.GENDER, ""));
                    params.put(Constant.Fields.BONE_MASS, sharedPreferencesActofit.getString(Constant.Fields.BONE_MASS, ""));
                    params.put(Constant.Fields.BODY_WATER, sharedPreferencesActofit.getString(Constant.Fields.BODY_WATER, ""));
                    params.put(Constant.Fields.PULSE_RATE, sharedPreferencesBloodPressure.getString(Constant.Fields.PULSE_RATE, ""));
                    params.put(Constant.Fields.MUSCLE_MASS, sharedPreferencesActofit.getString(Constant.Fields.MUSCLE_MASS, ""));
                    params.put(Constant.Fields.HEMOGLOBIN, sharedPreferencesHemoglobin.getString(Constant.Fields.HEMOGLOBIN, ""));
                    params.put(Constant.Fields.HEALTH_SCORE, sharedPreferencesActofit.getString(Constant.Fields.HEALTH_SCORE, ""));
                    params.put(Constant.Fields.VISCERAL_FAT, sharedPreferencesActofit.getString(Constant.Fields.VISCERAL_FAT, ""));
                    params.put(Constant.Fields.BLOOD_OXYGEN, sharedPreferencesOximeter.getString(Constant.Fields.BLOOD_OXYGEN, ""));
                    params.put(Constant.Fields.TEMPERATURE, sharedPreferencesThermometer.getString(Constant.Fields.TEMPERATURE, ""));
                    params.put(Constant.Fields.SKELETAL_MUSCLE, sharedPreferencesActofit.getString(Constant.Fields.SKELETAL_MUSCLE, ""));
                    params.put(Constant.Fields.FAT_FREE_WEIGHT, sharedPreferencesActofit.getString(Constant.Fields.FAT_FREE_WEIGHT, ""));
                    params.put(Constant.Fields.SUBCUTANEOUS_FAT, sharedPreferencesActofit.getString(Constant.Fields.SUBCUTANEOUS_FAT, ""));
                    params.put(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, sharedPreferencesBloodPressure.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, ""));
                    params.put(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, sharedPreferencesBloodPressure.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, ""));

                    if (!sharedPreferencesActofit.getString(Constant.Fields.WEIGHT, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.WEIGHT_RANGE, "" + standardWeightRange + " kg");
                    else
                        params.put(Constant.Fields.WEIGHT_RANGE, "NA");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.BMI, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.BMI_RANGE, "18.5-25");
                    else
                        params.put(Constant.Fields.BMI_RANGE, "NA");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.BODY_FAT, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.BODY_FAT_RANGE, standardBodyFat);
                    else
                        params.put(Constant.Fields.BODY_FAT_RANGE, "NA");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.SUBCUTANEOUS_FAT, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.SUBCUTANEOUS_FAT_RANGE, subcutaneousFat);
                    else
                        params.put(Constant.Fields.SUBCUTANEOUS_FAT_RANGE, "NA");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.VISCERAL_FAT, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.VISCERAL_FAT_RANGE, standardVisceralFat);
                    else
                        params.put(Constant.Fields.VISCERAL_FAT_RANGE, "NA");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.BODY_WATER, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.BODY_WATER_RANGE, standardBodyWater);
                    else
                        params.put(Constant.Fields.BODY_WATER_RANGE, "NA");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.SKELETAL_MUSCLE, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.SKELETAL_MUSCLE_RANGE, standardSkeltonMuscle);
                    else
                        params.put(Constant.Fields.SKELETAL_MUSCLE_RANGE, "NA");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.PROTEIN, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.PROTEIN_RANGE, "16-18 %");
                    else
                        params.put(Constant.Fields.PROTEIN_RANGE, "NA");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.META_AGE, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.META_AGE_RANGE, "<=" + age);
                    else
                        params.put(Constant.Fields.META_AGE_RANGE, "NA");

                    params.put(Constant.Fields.HEALTH_SCORE_RANGE, "");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.BMR, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.BMR_RANGE, standardBMR);
                    else
                        params.put(Constant.Fields.BMR_RANGE, "NA");

                    params.put(Constant.Fields.PHYSIQUE_RANGE, "");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.MUSCLE_MASS, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.MUSCLE_MASS_RANGE, standardMuscleMass);
                    else
                        params.put(Constant.Fields.MUSCLE_MASS_RANGE, "NA");

                    if (!sharedPreferencesActofit.getString(Constant.Fields.BONE_MASS, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.BONE_MASS_RANGE, standardBoneMass);
                    else
                        params.put(Constant.Fields.BONE_MASS_RANGE, "NA");

                    if (!sharedPreferencesThermometer.getString(Constant.Fields.TEMPERATURE, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.TEMPERATURE_RANGE, "97 - 99 F");
                    else
                        params.put(Constant.Fields.TEMPERATURE_RANGE, "NA");

                    if (!sharedPreferencesBloodPressure.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC_RANGE, "90-139 mmHg");
                    else
                        params.put(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC_RANGE, "NA");

                    if (!sharedPreferencesBloodPressure.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC_RANGE, "60-89 mmHg");
                    else
                        params.put(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC_RANGE, "NA");

                    if (!sharedPreferencesOximeter.getString(Constant.Fields.BLOOD_OXYGEN, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.BLOOD_OXYGEN_RANGE, ">94%");
                    else
                        params.put(Constant.Fields.BLOOD_OXYGEN_RANGE, "NA");

                    if (!sharedPreferencesBloodPressure.getString(Constant.Fields.PULSE_RATE, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.PULSE_RATE_RANGE, "60-100 bpm");
                    else
                        params.put(Constant.Fields.PULSE_RATE_RANGE, "NA");

                    if (!sharedPreferencesSugar.getString(Constant.Fields.SUGAR, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.SUGAR_RANGE, standardGlucose);
                    else
                        params.put(Constant.Fields.SUGAR_RANGE, "NA");

                    if (!sharedPreferencesHemoglobin.getString(Constant.Fields.HEMOGLOBIN, "").equalsIgnoreCase(""))
                        params.put(Constant.Fields.HEMOGLOBIN_RANGE, standardHemoglobin);
                    else
                        params.put(Constant.Fields.HEMOGLOBIN_RANGE, "NA");

                    params.put(Constant.Fields.HEIGHT_RESULT, "");
                    params.put(Constant.Fields.BMI_RESULT, bmiResult);
                    params.put(Constant.Fields.BMR_RESULT, bmrResult);
                    params.put(Constant.Fields.SUGAR_RESULT, sugarResult);
                    params.put(Constant.Fields.WEIGHT_REUSLT, weightResult);
                    params.put(Constant.Fields.PROTEIN_RESULT, proteinResult);
                    params.put(Constant.Fields.META_AGE_RESULT, metaageResult);
                    params.put(Constant.Fields.BODY_FAT_RESULT, bodyfatResult);
                    params.put(Constant.Fields.PULSE_RATE_RESULT, pulseResult);
                    params.put(Constant.Fields.BONE_MASS_RESULT, bonemassResult);
                    params.put(Constant.Fields.BLOOD_OXYGEN_RESULT, oxygenResult);
                    params.put(Constant.Fields.BODY_WATER_RESULT, bodywaterResult);
                    params.put(Constant.Fields.HEMOGLOBIN_RESULT, hemoglobinResult);
                    params.put(Constant.Fields.MUSCLE_MASS_RESULT, musclemassResult);
                    params.put(Constant.Fields.TEMPERATURE_RESULT, tempratureResult);
                    params.put(Constant.Fields.VISCERAL_FAT_RESULT, visceralfatResult);
                    params.put(Constant.Fields.FAT_FREE_WEIGHT_RESULT, fatfreeweightResult);
                    params.put(Constant.Fields.SUBCUTANEOUS_FAT_RESULT, subcutaneousResult);
                    params.put(Constant.Fields.SKELETAL_MUSCLE_RESULT, skeletonmuscleResult);
                    params.put(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC_RESULT, diastolicResult);
                    params.put(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC_RESULT, bloodpressureResult);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        AndMedical_App_Global.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * @param response
     */
    private void readFileName(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            fileName = dataObject.getString("file");
        } catch (JSONException e) {
            // TODO: Handle exception here
        }
    }

    private void gettingDataObjects() {
        sharedPreferencesOximeter = getSharedPreferences(ApiUtils.PREFERENCE_PULSE, MODE_PRIVATE);
        sharedPreferencesSugar = getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
        sharedPreferencesActofit = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
        sharedPreferencesThermometer = getSharedPreferences(PREFERENCE_THERMOMETERDATA, MODE_PRIVATE);
        sharedPreferencesToken = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        sharedPreferencesHemoglobin = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
        sharedPreferencesPersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        sharedPreferencesBloodPressure = getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
    }


    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm aa").format(Calendar.getInstance().getTime());
    }

    private void downloadFile(String fileName) {
        downloadUrl = ApiUtils.DOWNLOAD_PDF_URL + fileName;

        if (fileName != null)
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)));
        else
            Toast.makeText(PrintPreviewActivity.this, "No Pdf file available ", Toast.LENGTH_SHORT).show();
    }

    private void startDownload() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        request.setTitle("Download");
        request.setDescription("Downloading File...");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "" + System.currentTimeMillis());

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                } else {
                    Toast.makeText(this, "Permission Denied..!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class EnterTextAsyc extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog untill background task is completed */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /* Task of EnterTextAsyc performing in the background */
        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                ptrGen.iFlushBuf();

                String empty = printerText;

//                ptrGen.iAddData(Printer_GEN.FONT_SMALL_NORMAL, empty);
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, empty);
                iRetVal = ptrGen.iStartPrinting(1);
            } catch (Exception e) {
                e.printStackTrace();
                iRetVal = DEVICE_NOTCONNECTED;
                // TODO: Handle exception
                return iRetVal;
            }
            return iRetVal;
        }

        /* This displays the status messages of EnterTextAsyc in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {

            Log.e("result_PosstLog", ":" + result);

            if (result == -1) {
                Toast.makeText(context, "Please Reconnect the device...", Toast.LENGTH_LONG).show();
            }

            super.onPostExecute(result);
        }
    }

    /* Handler to display UI response messages */
    @SuppressLint("HandlerLeak")
    Handler ptrHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        TextView tvMessage = (TextView) dlgCustomdialog.findViewById(R.id.tvMessage);
                        tvMessage.setText("" + msg.obj);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                case 2:
                    String str1 = (String) msg.obj;
                    dlgShow(str1);
                    break;
                case 3:
                    Toast.makeText(PrintPreviewActivity.this, (String) msg.obj, Toast.LENGTH_LONG)
                            .show();
                    break;
                default:
                    break;
            }
        }

        ;
    };


    /* To show response messages */
    public void dlgShow(String str) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Pride Demo Application");
        alertDialogBuilder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        /* create alert dialog */
        AlertDialog alertDialog = alertDialogBuilder.create();
        /* show alert dialog */
        alertDialog.show();
    }

    /* This performs Progress dialog box to show the progress of operation */
    protected void dlgShowCustom(Context con, String Message) {
        dlgCustomdialog = new Dialog(con);
        dlgCustomdialog.setCancelable(false);
        dlgCustomdialog.setTitle("Pride Demo");
        dlgCustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgCustomdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dlgCustomdialog.setContentView(R.layout.progressdialog);

        TextView tvTitle = dlgCustomdialog.findViewById(R.id.tvTitle);
        TextView tvMessage = dlgCustomdialog.findViewById(R.id.tvMessage);

        tvTitle.setWidth(iWidth);
        tvMessage.setText(Message);

        llprog = dlgCustomdialog.findViewById(R.id.llProg);
        pbProgress = dlgCustomdialog.findViewById(R.id.pbDialog);

        btnOk = dlgCustomdialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(view -> dlgCustomdialog.dismiss());

        dlgCustomdialog.show();
    }

    private int getAge() {
        if (SharedPreferenceService.isAvailable(context, ApiUtils.PREFERENCE_PERSONALDATA, Constant.Fields.DATE_OF_BIRTH)) {
            String dateOfBirth = SharedPreferenceService.getString(context, ApiUtils.PREFERENCE_PERSONALDATA, Constant.Fields.DATE_OF_BIRTH);
            return DateService.getAgeFromStringDate(dateOfBirth);
        } else
            return 0;
    }

    private void goToHome() {
        clearDatabase();

        Intent newIntent = new Intent(getApplicationContext(), OtpLoginScreen.class);

        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(newIntent);
    }

    private void clearDatabase() {
        SharedPreferences objSugar = getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
        SharedPreferences objNewRecord = getSharedPreferences(ApiUtils.PREFERENCE_NEWRECORD, MODE_PRIVATE);
        SharedPreferences sharedPreferencesURL = getSharedPreferences(ApiUtils.PREFERENCE_URL, MODE_PRIVATE);
        SharedPreferences sharedPreferencesPulse = getSharedPreferences(ApiUtils.PREFERENCE_PULSE, MODE_PRIVATE);
        SharedPreferences sharedPreferencesActofit = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
        SharedPreferences sharedPreferencesBiosense = getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
        SharedPreferences sharedPreferencesHemoglobin = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
        SharedPreferences sharedPreferencesBloodPressure = getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
        SharedPreferences sharedPreferencesPersonalDate = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        objSugar.edit().clear().apply();
        objNewRecord.edit().clear().apply();
        sharedPreferencesURL.edit().clear().apply();
        sharedPreferencesPulse.edit().clear().apply();
        sharedPreferencesSugar.edit().clear().apply();
        sharedPreferencesActofit.edit().clear().apply();
        sharedPreferencesActofit.edit().clear().apply();
        sharedPreferencesBiosense.edit().clear().apply();
        sharedPreferencesOximeter.edit().clear().apply();
        sharedPreferencesHemoglobin.edit().clear().apply();
        sharedPreferencesHemoglobin.edit().clear().apply();
        sharedPreferencesThermometer.edit().clear().apply();
        sharedPreferencesPersonalData.edit().clear().apply();
        sharedPreferencesBloodPressure.edit().clear().apply();
        sharedPreferencesBloodPressure.edit().clear().apply();
        sharedPreferencesPersonalDate.edit().clear().apply();
    }

    private String getPrintText() {
        String str = "" + "    " + "Clinics On Cloud" + "" + "\n\n" +
                "Name: {name}\n" +
                "Age : {age}  Gender: {gender}\n" +
                "{currentDate}  {currentTime} \n" +
                "-----------------------\n" +
                "Height: {height} cm\n" +
                "Weight: {weight} kg\n" +
                "[Normal Range]:\n" + "{standardWeightRangeFrom} - {standardWeightRangeTo} kg\n" +
                "BMI: {bmi} \n" +
                "[Normal Range]:" + "18.5-25\n" +
                "-----------------------\n" +
                "Body Fat:{bodyFat}\n" +
                "[Normal Range]:" + "{standardBodyFat}\n\n" +
                "Fat Free Weight:{fatFreeWeight} Kg" + "\n\n" +
                "Subcutaneous Fat: {subcutaneousFat}%" + "\n" +
                "[Normal Range]:\n" + "{subcutaneousFatRange}\n\n" +
                "Visceral Fat : {visceralFat}\n" +
                "[Normal Range]: <= 9\n\n" +
                "Body Water : {bodyWater}\n" +
                "[Normal Range]:" + "{standardBodyWater}\n\n" +
                "Skeletal Muscle : {skeletalMuscle}\n" +
                "[Normal Range]:" + "{standardSkeletalMuscle}\n\n" +
                "Muscle Mass : {muscleMass}\n" +
                "[Normal Range]:\n" + "{standardMuscleMass}\n\n" +
                "Bone Mass : {boneMass}\n" +
                "[Normal Range]:" + "{standardBoneMass}\n\n" +
                "Protein : {protein}\n" +
                "[Normal Range]:" + "16-18(%) \n\n" +
                "BMR : {bmr}\n" +
                "[Normal Range]:\n" + ">={standardMetabolism}Kcal\n\n" +
                "Physique: {physique}\n\n" +
                "Meta Age : {metaAge} yrs\n\n" +
                "Health Score : {healthScore}\n\n" +
                "-----------------------\n" +
                "Blood Glucose : {sugar} mg/dl\n" +
                "[Normal Range]:\n" + "{standardSugar}mg/dl\n\n" +
                "-----------------------\n" +
                "Hemoglobin : {hemoglobin} g/dl\n" +
                "[Normal Range]:\n" + "{standardHemoglobin}\n\n" +
                "-----------------------\n" +
                "Blood Pressure : \n" +
                "Systolic : {bloodPressureSystolic} mmHg" + "\n" +
                "Diastolic : {bloodPressureDiastolic} mmHg \n" +
                "[Normal Range]: \n" +
                "Systolic : 90-139mmHg\n" +
                "Diastolic : 60-89mmHg\n" +
                "-----------------------\n" +
                "Blood Oxygen : {bloodOxygen} %" + "\n" +
                "[Normal Range]: >94%\n\n" +
                "Pulse Rate: {pulseRate} bpm\n" +
                "[Normal Range]:" + "60-100bpm\n" +
                "-----------------------\n" +
                "Temperature : {temperature} F\n" +
                "[Normal Range]: 97-99 F\n" +
                "-----------------------\n" +
                "       Thank You\n" +
                "   Above results are\n" +
                "       indicative\n" +
                "  figure,don't follow it\n" +
                "   without consulting a\n" +
                "        doctor\n\n\n\n\n\n\n";

        str = str.replace("{name}", getPersonalPreferenceData(Constant.Fields.NAME));
        str = str.replace("{age}", String.valueOf(age));
        str = str.replace("{gender}", getPersonalPreferenceData(Constant.Fields.GENDER));
        str = str.replace("{currentDate}", currentDate);
        str = str.replace("{currentTime}", currentTime);
        str = str.replace("{height}", getActofitPreferenceData(Constant.Fields.HEIGHT));
        str = str.replace("{weight}", getActofitPreferenceData(Constant.Fields.WEIGHT));
        str = str.replace("{standardWeightRangeFrom}", String.valueOf(standardWeighRangeFrom));
        str = str.replace("{standardWeightRangeTo}", String.valueOf(standardWeighRangeTo));
        str = str.replace("{bmi}", getActofitPreferenceData(Constant.Fields.BMI));
        str = str.replace("{bodyFat}", getActofitPreferenceData(Constant.Fields.BODY_FAT));
        str = str.replace("{standardBodyFat}", standardBodyFat);
        str = str.replace("{fatFreeWeight}", getActofitPreferenceData(Constant.Fields.FAT_FREE_WEIGHT));
        str = str.replace("{subcutaneousFat}", getActofitPreferenceData(Constant.Fields.SUBCUTANEOUS_FAT));
        str = str.replace("{subcutaneousFatRange}", subcutaneousFat);
        str = str.replace("{visceralFat}", getActofitPreferenceData(Constant.Fields.VISCERAL_FAT));
        str = str.replace("{bodyWater}", getActofitPreferenceData(Constant.Fields.BODY_WATER));
        str = str.replace("{standardBodyWater}", standardBodyWater);
        str = str.replace("{skeletalMuscle}", getActofitPreferenceData(Constant.Fields.SKELETAL_MUSCLE));
        str = str.replace("{standardSkeletalMuscle}", standardSkeltonMuscle);
        str = str.replace("{muscleMass}", getActofitPreferenceData(Constant.Fields.MUSCLE_MASS));
        str = str.replace("{standardMuscleMass}", standardMuscleMass);
        str = str.replace("{boneMass}", getActofitPreferenceData(Constant.Fields.BONE_MASS));
        str = str.replace("{standardBoneMass}", standardBoneMass);
        str = str.replace("{protein}", getActofitPreferenceData(Constant.Fields.PROTEIN));
        str = str.replace("{bmr}", getActofitPreferenceData(Constant.Fields.BMR));
        str = str.replace("{standardMetabolism}", String.valueOf(standardMetabolism));
        str = str.replace("{physique}", getActofitPreferenceData(Constant.Fields.PHYSIQUE));
        str = str.replace("{metaAge}", getActofitPreferenceData(Constant.Fields.META_AGE));
        str = str.replace("{healthScore}", getActofitPreferenceData(Constant.Fields.HEALTH_SCORE));
        str = str.replace("{sugar}", getSugarPreferenceData(Constant.Fields.SUGAR));
        str = str.replace("{standardSugar}", standardGlucose);
        str = str.replace("{hemoglobin}", getHemoglobinPreferenceData(Constant.Fields.HEMOGLOBIN));
        str = str.replace("{standardHemoglobin}", standardHemoglobin);
        str = str.replace("{bloodPressureSystolic}", geBloodPressurePreferenceData(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC));
        str = str.replace("{bloodPressureDiastolic}", geBloodPressurePreferenceData(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC));
        str = str.replace("{bloodOxygen}", getOximeterPreferenceData(Constant.Fields.BLOOD_OXYGEN));
        str = str.replace("{pulseRate}", geBloodPressurePreferenceData(Constant.Fields.PULSE_RATE));
        str = str.replace("{temperature}", getThermometerPreferenceData(Constant.Fields.TEMPERATURE));

        return str;
    }

    private String getPersonalPreferenceData(String key) {
        return sharedPreferencesPersonalData.getString(key, "");
    }

    private String getActofitPreferenceData(String key) {
        return sharedPreferencesActofit.getString(key, "");
    }

    private String getSugarPreferenceData(String key) {
        return sharedPreferencesSugar.getString(key, "");
    }

    private String getHemoglobinPreferenceData(String key) {
        return sharedPreferencesHemoglobin.getString(key, "");
    }

    private String getThermometerPreferenceData(String key) {
        return sharedPreferencesThermometer.getString(key, "");
    }

    private String getOximeterPreferenceData(String key) {
        return sharedPreferencesOximeter.getString(key, "");
    }

    private String geBloodPressurePreferenceData(String key) {
        return sharedPreferencesBloodPressure.getString(key, "");
    }

    private class ConnSocketTask extends AsyncTask<String, String, Integer> {
        /**
         * Process waits prompt box
         */
        private ProgressDialog mpd = null;
        /**
         * Constants: connection fails
         */
        private static final int CONN_FAIL = 0x01;
        /**
         * Constant: the connection is established
         */
        private static final int CONN_SUCCESS = 0x02;

        /**
         * Thread start initialization
         */
        @Override
        public void onPreExecute() {
            mpd = new ProgressDialog(PrintPreviewActivity.this);
            mpd.setMessage(getString(R.string.actMain_msg_device_connecting));
            mpd.setCancelable(false);
            mpd.setCanceledOnTouchOutside(false);
            btnHome.setEnabled(false);
            btnPrint.setEnabled(false);
            if (!((Activity) context).isFinishing())
                mpd.show();
        }

        /* Task of  performing in the background*/

        @Override
        protected Integer doInBackground(String... arg0) {
            if (mGP.createConn(arg0[0])) {
                SystemClock.sleep(2000);
                return CONN_SUCCESS;
            } else {
                return CONN_FAIL;
            }
        }

        /* This displays the status messages of in the dialog box */
        @Override
        public void onPostExecute(Integer result) {

            Log.e("result_Connection", ":" + result);

            if (mpd != null && mpd.isShowing())
                mpd.dismiss();

            if (CONN_SUCCESS == result) {

                btnHome.setEnabled(true);
                btnPrint.setEnabled(true);

                printerActivation();
            } else {
                showReconnectPopup();
            }
        }
    }

    private void showReconnectPopup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle("Communication Lost!");
        alertDialogBuilder.setMessage("Device is not active, try again").setCancelable(false)
                .setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        autoConnectPrinter();
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        /* create alert dialog */
        AlertDialog alertDialog = alertDialogBuilder.create();
        /* show alert dialog */
        if (!((Activity) context).isFinishing())
            alertDialog.show();
        alertDialogBuilder.setCancelable(false);
    }
}

