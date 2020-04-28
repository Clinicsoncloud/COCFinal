package com.abhaybmicoc.app.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.adapter.RreportsPrintPreviewAdapter;
import com.abhaybmicoc.app.adapter.VisitDatesRVAdapter;
import com.abhaybmicoc.app.database.DataBaseHelper;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;
import com.abhaybmicoc.app.interfaces.RvClickListener;
import com.abhaybmicoc.app.model.Common_Update_Response;
import com.abhaybmicoc.app.model.Patient_Visit_Response;
import com.abhaybmicoc.app.model.PrintData;
import com.abhaybmicoc.app.model.PrintDataOld;
import com.abhaybmicoc.app.model.ReportsPrintData;
import com.abhaybmicoc.app.model.Visit_Upload_Response;
import com.abhaybmicoc.app.printer.esys.pridedemoapp.Act_GlobalPool;
import com.abhaybmicoc.app.printer.evolute.bluetooth.BluetoothComm;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.services.DateService;
import com.abhaybmicoc.app.services.HttpService;
import com.abhaybmicoc.app.services.SharedPreferenceService;
import com.abhaybmicoc.app.services.TextToSpeechService;
import com.abhaybmicoc.app.utils.ApiUtils;
import com.abhaybmicoc.app.utils.Constant;
import com.abhaybmicoc.app.utils.DTU;
import com.abhaybmicoc.app.utils.Utils;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.hsalf.smilerating.SmileRating;
import com.prowesspride.api.Printer_GEN;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportsPrintPreviewActivity extends Activity implements RvClickListener {
    // region Variables

    private Context context = ReportsPrintPreviewActivity.this;

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

    private Button saveRating;
    private SmileRating smileRating;
    private int feedbackRating = 0;

    private ImageView ivDownload;
    private RecyclerView rvVisitDates;

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
    private double standarWeightHighFrom;
    private double standarWeightHighTo;
    private double standarWeightLowFrom;
    private double standarWeightLowTo;

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
    private String standardEyeRange = "";
    private String updatedParameterID = "";
    private String lastInsertedpatient_id = "";
    private String lastInsertedparameter_id = "";
    public static Printer_GEN ptrGen;
    private AndMedical_App_Global mGP = null;
    public static BluetoothDevice mBDevice = null;
    public static BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();

    List<PrintDataOld> printDataList = new ArrayList<>();
    List<ReportsPrintData> printDataListNew;
    private Hashtable<String, String> mhtDeviceInfo = new Hashtable<String, String>();
    private String TAG = "PrinPriviewActivity";

    DataBaseHelper dataBaseHelper;

    private String PRINT_MSG = "";
    private String RECONNECT_MSG = "";
    private String RECEIPT_MSG = "";

    TextToSpeechService textToSpeechService;

    private SharedPreferences sharedPreferencePersonalData;
    Patient_Visit_Response patientVisitResponse;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports_printpreview);
        ButterKnife.bind(this);

        init("");
        setupUI();
        setupEvents();

        initializeData();
        requestGPSPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBT.isEnabled())
            mBT.disable();

        if (textToSpeechService != null)
            textToSpeechService.stopTextToSpeech();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    // endregion

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestGPSPermission() {
        try {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            String provider = Settings.Secure.getString(getContentResolver(), LocationManager.GPS_PROVIDER);

            if (!statusOfGPS) {
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

    private void init(String msg) {
        if (Utils.isOnline(context))
            textToSpeechService = new TextToSpeechService(getApplicationContext(), msg);
    }

    private void setupUI() {
        ivDownload = findViewById(R.id.iv_download);
        rvVisitDates = findViewById(R.id.rv_VisitDates);

        PRINT_MSG = getResources().getString(R.string.print_msg);
        RECONNECT_MSG = getResources().getString(R.string.print_reconnect_msg);
        RECEIPT_MSG = getResources().getString(R.string.print_result_msg);
    }

    private void setupEvents() {

        btnHome.setOnClickListener(view -> goToHome());

        btnPrint.setOnClickListener(view -> {
            Toast.makeText(this, "Getting Printout", Toast.LENGTH_SHORT).show();
            init(RECEIPT_MSG);

            EnterTextAsyc asynctask = new EnterTextAsyc();
            asynctask.execute(0);

        });

        ivDownload.setOnClickListener(view -> downloadFile(fileName));

        btnReconnect.setOnClickListener(view -> autoConnectPrinter());
    }

    private void initializeData() {
        sharedPreferencePersonalData = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        mGP = ((AndMedical_App_Global) getApplicationContext());
        connectToSavedPrinter();

        getVisitsData();
        setStaticData();
        age = getAge();
    }

    private void setStaticData() {
        nameTV.setText("Name :" + sharedPreferencePersonalData.getString(Constant.Fields.NAME, ""));
        genderTV.setText("Gender :" + sharedPreferencePersonalData.getString(Constant.Fields.GENDER, ""));
        dobTV.setText("DOB :" + sharedPreferencePersonalData.getString(Constant.Fields.DATE_OF_BIRTH, ""));
    }

    private void getVisitsData() {
        Map<String, String> requestBodyParams = new HashMap<>();
        requestBodyParams.put("patient_id", sharedPreferencePersonalData.getString(Constant.Fields.PATIENT_ID, ""));

        HashMap headersParams = new HashMap();
        Log.e("getPatientVisitURL_Log", ":" + ApiUtils.PARAMETER_PATIENT_URL);
        Log.e("getPatientViPrm_Log", ":" + requestBodyParams);

        HttpService.accessWebServices(
                context,
                ApiUtils.PARAMETER_PATIENT_URL,
                Request.Method.POST,
                requestBodyParams,
                headersParams,
                (response, error, status) -> handleGetAPIResponse(response, error, status));
    }

    private void handleGetAPIResponse(String response, VolleyError error, String status) {

        Log.e("response_Logdasd", ":" + response);
        if (status.equals("response")) {

            patientVisitResponse = (Patient_Visit_Response) Utils.parseResponse(response, Patient_Visit_Response.class);

            if (patientVisitResponse.getFound()) {
                patientVisitResponse.getData().get(0).setIsSelectedDate(true);
                setNewList(patientVisitResponse.getData().get(0));
                setVisitDateAdapter(patientVisitResponse.getData());
            }
        }
    }

    private void setVisitDateAdapter(List<Patient_Visit_Response.Patient_Visit_Data> dataList) {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false);
        rvVisitDates.setLayoutManager(gridLayoutManager);
        VisitDatesRVAdapter adapter = new VisitDatesRVAdapter(context, dataList);
        rvVisitDates.setAdapter(adapter);
        adapter.setRvClickListener(this);

    }

    @Override
    public void rv_click(int position, int value, String key) {
        Log.e("Selected_VisitDate_key", ":" + key);

        if (key.equals("selected_date")) {
            updateIsSelected(position);
            setNewList(patientVisitResponse.getData().get(position));
        }
    }

    private void updateIsSelected(int position) {

        for (int i = 0; i < patientVisitResponse.getData().size(); i++) {
            Log.e("sdaghdcaghvasgh", ":position:" + position + "  :i:  " + i);

            if (i == position) {
                patientVisitResponse.getData().get(i).setIsSelectedDate(true);
            } else {
                patientVisitResponse.getData().get(i).setIsSelectedDate(false);
            }
        }

        setVisitDateAdapter(patientVisitResponse.getData());
    }

    private void setupTextToSpeech() {
        init(PRINT_MSG);
    }

    private void connectToSavedPrinter() {
        SharedPreferences data = getSharedPreferences("printer", MODE_PRIVATE);

        if (data.getString("NAME", "").length() > 0) {

            mBDevice = mBT.getRemoteDevice(data.getString("MAC", ""));

            if (getIntent().getStringExtra("is_PrinterConnected").equals("false")) {

                btnPrint.setBackground(getResources().getDrawable(R.drawable.grayback));
                btnPrint.setEnabled(false);

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
            iWidth = getWindowManager().getDefaultDisplay().getWidth();
            InputStream input = BluetoothComm.misIn;
            OutputStream outstream = BluetoothComm.mosOut;
            ptrGen = new Printer_GEN(Act_GlobalPool.setup, outstream, input);

            btnReconnect.setBackground(getResources().getDrawable(R.drawable.greenback));
            btnReconnect.setEnabled(true);

            btnPrint.setBackground(getResources().getDrawable(R.drawable.greenback));
            btnPrint.setEnabled(true);

            init(PRINT_MSG);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // endregion

    private void setNewList(Patient_Visit_Response.Patient_Visit_Data patientVisitData) {
        try {

            try {
                fileName = patientVisitData.getId();
                updatedParameterID = patientVisitData.getId();
            } catch (Exception e) {
                // TODO: Handle exception here
            }

            getPrintData(patientVisitData);
            heightTV.setText("Height :" + patientVisitData.getHeight() + " cm");
            printDataListNew = new ArrayList<>();

            printDataListNew.add(new ReportsPrintData("Weight", patientVisitData.getWeightresult(), patientVisitData.getWeight(), patientVisitData.getWeightrange()));
            printDataListNew.add(new ReportsPrintData("BMI", patientVisitData.getBmiresult(), patientVisitData.getBmi(), patientVisitData.getBmirange()));
            printDataListNew.add(new ReportsPrintData("Body fat", patientVisitData.getBodyfatresult(), patientVisitData.getBodyFat(), patientVisitData.getBodyfatrange()));
            printDataListNew.add(new ReportsPrintData("Fat Free weight", patientVisitData.getFatfreeweightresult(), patientVisitData.getFatFreeWeight(), patientVisitData.getFatfreerange()));
            printDataListNew.add(new ReportsPrintData("Subcutaneous Fat", patientVisitData.getSubcutaneousresult(), patientVisitData.getSubcutaneous(), patientVisitData.getSubfatrange()));
            printDataListNew.add(new ReportsPrintData("Visceral Fat", patientVisitData.getVisceralfatresult(), patientVisitData.getVisceralFat(), patientVisitData.getVisceralfatrange()));
            printDataListNew.add(new ReportsPrintData("Body water", patientVisitData.getBodywaterresult(), patientVisitData.getBodyWater(), patientVisitData.getBodywaterrange()));
            printDataListNew.add(new ReportsPrintData("Skeleton muscle", patientVisitData.getSkeletonmuscleresult(), patientVisitData.getSkeletonMuscle(), patientVisitData.getSkeletanmusclerange()));
            printDataListNew.add(new ReportsPrintData("Protein", patientVisitData.getProteinresult(), patientVisitData.getProtein(), patientVisitData.getProteinrange()));
            printDataListNew.add(new ReportsPrintData("Metabolic Age", patientVisitData.getMetaageresult(), patientVisitData.getMetaAge(), patientVisitData.getMetaagerange()));
            printDataListNew.add(new ReportsPrintData("Health Score", "", patientVisitData.getHealthScore(), ""));
            printDataListNew.add(new ReportsPrintData("BMR", patientVisitData.getBmrresult(), patientVisitData.getBmr(), patientVisitData.getBmrrange()));
            printDataListNew.add(new ReportsPrintData("Physique", "", patientVisitData.getPhysique(), patientVisitData.getPhysiquerange()));
            printDataListNew.add(new ReportsPrintData("Muscle Mass", patientVisitData.getMusclemassresult(), patientVisitData.getMuscleMass(), patientVisitData.getMusclemassrange()));
            printDataListNew.add(new ReportsPrintData("Bone Mass", patientVisitData.getBonemassresult(), patientVisitData.getBoneMass(), patientVisitData.getBonemassrange()));
            printDataListNew.add(new ReportsPrintData("Body Temp", patientVisitData.getTemperatureresult(), patientVisitData.getTemperature(), patientVisitData.getBodytemprange()));
            printDataListNew.add(new ReportsPrintData("Systolic", patientVisitData.getSystolicresult(), patientVisitData.getBloodPressure(), patientVisitData.getSystolicrange()));
            printDataListNew.add(new ReportsPrintData("Diastolic", patientVisitData.getBloodpressureresult(), patientVisitData.getDialostic(), patientVisitData.getDialosticrange()));
            printDataListNew.add(new ReportsPrintData("Pulse Oximeter", patientVisitData.getOxygenresult(), patientVisitData.getOxygen(), patientVisitData.getPulseoximeterrange()));
            printDataListNew.add(new ReportsPrintData("Pulse", patientVisitData.getPulseresult(), patientVisitData.getPulse(), patientVisitData.getPulserange()));
            printDataListNew.add(new ReportsPrintData("Blood Glucose", patientVisitData.getSugarresult(), patientVisitData.getSugar(), patientVisitData.getBloodsugarrange()));
            printDataListNew.add(new ReportsPrintData("Hemoglobin", patientVisitData.getHemoglobinresult(), patientVisitData.getHemoglobin(), patientVisitData.getHemoglobinrange()));
            printDataListNew.add(new ReportsPrintData("Left Eye Vision", patientVisitData.getEyeleftresult(), patientVisitData.getEyeLeftVision(), patientVisitData.getEyerange()));
            printDataListNew.add(new ReportsPrintData("Right Eye Vision", patientVisitData.getEyerightresult(), patientVisitData.getEyeRightVision(), patientVisitData.getEyerange()));

            lV.setAdapter(new RreportsPrintPreviewAdapter(this, R.layout.printlist_item, printDataListNew));

            Log.e("printDataListNew_Log", ":" + printDataListNew);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPrintData(Patient_Visit_Response.Patient_Visit_Data patientVisitData) {
        printerText = getPrintText(patientVisitData);

        Log.e("printerText_Log", ":" + printerText);
    }

    /**
     * @param
     * @param
     */
    private void readFileName(Visit_Upload_Response.Visit_Upload_Response_Data visitUploadResponseData) {
        try {
            fileName = visitUploadResponseData.getParameter().getId();
            updatedParameterID = visitUploadResponseData.getParameter().getId();
        } catch (Exception e) {
            // TODO: Handle exception here
        }
    }


    private void downloadFile(String fileName) {
        downloadUrl = ApiUtils.DOWNLOAD_PDF_URL + fileName;

        if (fileName != null) {
            Intent intent = new Intent();
            intent.setDataAndType(Uri.parse(downloadUrl), "application/pdf");
            startActivity(intent);
        } else {
            Toast.makeText(ReportsPrintPreviewActivity.this, "No Pdf file available ", Toast.LENGTH_SHORT).show();
        }
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

            if (result == -1) {
                Toast.makeText(context, "Please Reconnect the device...", Toast.LENGTH_LONG).show();

                btnPrint.setBackground(getResources().getDrawable(R.drawable.grayback));
                btnPrint.setEnabled(false);

                btnReconnect.setBackground(getResources().getDrawable(R.drawable.greenback));
                btnReconnect.setEnabled(true);
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
                    Toast.makeText(ReportsPrintPreviewActivity.this, (String) msg.obj, Toast.LENGTH_LONG)
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

            return DateService.getAgeFromStringDate(DTU.getYYYYMD(dateOfBirth));
        } else
            return 0;
    }

    private void goToHome() {

        Intent newIntent = new Intent(getApplicationContext(), OtpLoginScreen.class);

        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(newIntent);
        finish();
    }

    private String getPrintText(Patient_Visit_Response.Patient_Visit_Data patientVisitData) {
        String str = "" + "    " + "Clinics On Cloud" + "" + "\n\n" +
                "Name: {name}\n" +
                "Age : {age}  Gender: {gender}\n" +
                "{currentDate}  {currentTime} \n" +
                "-----------------------\n" +
                "Height: {height} cm\n" +
                "Weight: {weight} kg\n" +
                "[Normal Range]:\n" + "{standardWeightRange} kg\n" +
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
                "Left Eye Vision : {left_eye_vision}" + "\n" +
                "[Normal Range]: {standardEyeRange}\n\n" +
                "Right Eye Vision: {right_eye_vision}\n" +
                "[Normal Range]:" + "{standardEyeRange}\n" +
                "-----------------------\n" +
                "       Thank You\n" +
                "   Above results are\n" +
                "       indicative\n" +
                "  figure,don't follow it\n" +
                "   without consulting a\n" +
                "        doctor\n\n\n\n\n\n\n";

        try {
            str = str.replace("{name}", sharedPreferencePersonalData.getString(Constant.Fields.NAME, ""));
            str = str.replace("{age}", String.valueOf(age));
            str = str.replace("{gender}", sharedPreferencePersonalData.getString(Constant.Fields.GENDER, ""));
            str = str.replace("{currentDate}", DTU.getCurrentDate());
            str = str.replace("{currentTime}", DTU.getTime());
            str = str.replace("{height}", patientVisitData.getHeight());
            str = str.replace("{weight}", String.valueOf(TextUtils.isEmpty(patientVisitData.getHeight()) ? 0 : Double.parseDouble(patientVisitData.getHeight())));
            str = str.replace("{standardWeightRange}", String.valueOf(TextUtils.isEmpty(patientVisitData.getWeightrange()) ? "NA" : Double.parseDouble(patientVisitData.getWeightrange())));
            str = str.replace("{bmi}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBmi()) ? 0 : Double.parseDouble(patientVisitData.getBmi())));
            str = str.replace("{bodyFat}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBodyFat()) ? 0 : Double.parseDouble(patientVisitData.getBodyFat())));
            str = str.replace("{standardBodyFat}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBodyfatrange()) ? "NA" : Double.parseDouble(patientVisitData.getBodyfatrange())));
            str = str.replace("{fatFreeWeight}", String.valueOf(TextUtils.isEmpty(patientVisitData.getFatFreeWeight()) ? 0 : Double.parseDouble(patientVisitData.getFatFreeWeight())));
            str = str.replace("{subcutaneousFat}", String.valueOf(TextUtils.isEmpty(patientVisitData.getSubcutaneous()) ? 0 : Double.parseDouble(patientVisitData.getSubcutaneous())));
            str = str.replace("{subcutaneousFatRange}", String.valueOf(TextUtils.isEmpty(patientVisitData.getSubfatrange()) ? "NA" : Double.parseDouble(patientVisitData.getSubfatrange())));
            str = str.replace("{visceralFat}", String.valueOf(TextUtils.isEmpty(patientVisitData.getVisceralFat()) ? 0 : Double.parseDouble(patientVisitData.getVisceralFat())));
            str = str.replace("{bodyWater}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBodyWater()) ? 0 : Double.parseDouble(patientVisitData.getBodyWater())));
            str = str.replace("{standardBodyWater}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBodywaterrange()) ? "NA" : Double.parseDouble(patientVisitData.getBodyWater())));
            str = str.replace("{skeletalMuscle}", String.valueOf(TextUtils.isEmpty(patientVisitData.getSkeletonMuscle()) ? 0 : Double.parseDouble(patientVisitData.getSkeletonMuscle())));
            str = str.replace("{standardSkeletalMuscle}", String.valueOf(TextUtils.isEmpty(patientVisitData.getSkeletanmusclerange()) ? "NA" : Double.parseDouble(patientVisitData.getSkeletanmusclerange())));
            str = str.replace("{muscleMass}", String.valueOf(TextUtils.isEmpty(patientVisitData.getMuscleMass()) ? 0 : Double.parseDouble(patientVisitData.getMuscleMass())));
            str = str.replace("{standardMuscleMass}", String.valueOf(TextUtils.isEmpty(patientVisitData.getMusclemassrange()) ? "NA" : Double.parseDouble(patientVisitData.getMusclemassrange())));
            str = str.replace("{boneMass}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBoneMass()) ? 0 : Double.parseDouble(patientVisitData.getBoneMass())));
            str = str.replace("{standardBoneMass}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBonemassrange()) ? "NA" : Double.parseDouble(patientVisitData.getBonemassrange())));
            str = str.replace("{protein}", String.valueOf(TextUtils.isEmpty(patientVisitData.getProtein()) ? 0 : Double.parseDouble(patientVisitData.getProtein())));
            str = str.replace("{bmr}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBmr()) ? 0 : Double.parseDouble(patientVisitData.getBmr())));
            str = str.replace("{standardMetabolism}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBmrrange()) ? "NA" : Double.parseDouble(patientVisitData.getBmrrange())));
            str = str.replace("{physique}", String.valueOf(TextUtils.isEmpty(patientVisitData.getPhysique()) ? 0 : Double.parseDouble(patientVisitData.getPhysique())));
            str = str.replace("{metaAge}", String.valueOf(TextUtils.isEmpty(patientVisitData.getMetaAge()) ? 0 : Double.parseDouble(patientVisitData.getMetaAge())));
            str = str.replace("{healthScore}", String.valueOf(TextUtils.isEmpty(patientVisitData.getHealthScore()) ? 0 : Double.parseDouble(patientVisitData.getHealthScore())));
            str = str.replace("{sugar}", String.valueOf(TextUtils.isEmpty(patientVisitData.getSugar()) ? 0 : Double.parseDouble(patientVisitData.getSugar())));
            str = str.replace("{standardSugar}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBloodsugarrange()) ? "NA" : Double.parseDouble(patientVisitData.getBloodsugarrange())));
            str = str.replace("{hemoglobin}", String.valueOf(TextUtils.isEmpty(patientVisitData.getHemoglobin()) ? 0 : Double.parseDouble(patientVisitData.getHemoglobin())));
            str = str.replace("{standardHemoglobin}", String.valueOf(TextUtils.isEmpty(patientVisitData.getHemoglobinrange()) ? "NA" : Double.parseDouble(patientVisitData.getHemoglobinrange())));
            str = str.replace("{bloodPressureSystolic}", String.valueOf(TextUtils.isEmpty(patientVisitData.getBloodPressure()) ? 0 : Double.parseDouble(patientVisitData.getBloodPressure())));
            str = str.replace("{bloodPressureDiastolic}", String.valueOf(TextUtils.isEmpty(patientVisitData.getDialostic()) ? 0 : Double.parseDouble(patientVisitData.getDialostic())));
            str = str.replace("{bloodOxygen}", String.valueOf(TextUtils.isEmpty(patientVisitData.getOxygen()) ? 0 : Double.parseDouble(patientVisitData.getOxygen())));
            str = str.replace("{pulseRate}", String.valueOf(TextUtils.isEmpty(patientVisitData.getPulse()) ? 0 : Double.parseDouble(patientVisitData.getPulse())));
            str = str.replace("{temperature}", String.valueOf(TextUtils.isEmpty(patientVisitData.getTemperature()) ? 0 : Double.parseDouble(patientVisitData.getTemperature())));

            str = str.replace("{left_eye_vision}", String.valueOf(TextUtils.isEmpty(patientVisitData.getEyeLeftVision()) ? 0 : Double.parseDouble(patientVisitData.getEyeLeftVision())));
            str = str.replace("{right_eye_vision}", String.valueOf(TextUtils.isEmpty(patientVisitData.getEyeRightVision()) ? 0 : Double.parseDouble(patientVisitData.getEyeRightVision())));
            str = str.replace("{standardEyeRange}", String.valueOf(TextUtils.isEmpty(patientVisitData.getEyerange()) ? "NA" : Double.parseDouble(patientVisitData.getEyerange())));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
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
            mpd = new ProgressDialog(ReportsPrintPreviewActivity.this);
            mpd.setMessage(getString(R.string.actMain_msg_device_connecting));
            mpd.setCancelable(false);
            mpd.setCanceledOnTouchOutside(false);
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

            if (mpd != null && mpd.isShowing())
                mpd.dismiss();

            if (CONN_SUCCESS == result) {

                btnPrint.setBackground(getResources().getDrawable(R.drawable.greenback));
                btnPrint.setEnabled(false);

                printerActivation();
            } else {
                showReconnectPopup();
            }
        }
    }

    private void showReconnectPopup() {
        init(RECONNECT_MSG);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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
