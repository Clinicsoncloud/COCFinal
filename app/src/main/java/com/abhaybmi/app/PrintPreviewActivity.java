package com.abhaybmi.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmi.app.adapter.PrintPreviewActivityNew;
import com.abhaybmi.app.adapter.PrintpriviewAdapter;
import com.abhaybmi.app.entities.AndMedical_App_Global;
import com.abhaybmi.app.model.PrintData;
import com.abhaybmi.app.model.PrintDataNew;
import com.abhaybmi.app.printer.esys.pridedemoapp.Act_GlobalPool;
import com.abhaybmi.app.printer.esys.pridedemoapp.Act_Main;
import com.abhaybmi.app.printer.evolute.bluetooth.BluetoothComm;
import com.abhaybmi.app.utils.ApiUtils;
import com.abhaybmi.app.utils.Tools;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.prowesspride.api.Printer_GEN;
import com.prowesspride.api.Setup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Parser;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.abhaybmi.app.printer.esys.pridedemoapp.PrintPriviewScreen.TAG;
import static com.abhaybmi.app.utils.ApiUtils.PREFERENCE_THERMOMETERDATA;

public class PrintPreviewActivity extends Activity implements TextToSpeech.OnInitListener {
    List<PrintData> printDataList = new ArrayList<>();
    List<PrintDataNew> printDataListNew = new ArrayList<>();
    @BindView(R.id.parameterTV)
    TextView parameterTV;
    @BindView(R.id.resultTV)
    TextView resultTV;
    @BindView(R.id.valueTV)
    TextView valueTV;
    @BindView(R.id.rangeTV)
    TextView rangeTV;
    @BindView(R.id.topLL)
    LinearLayout topLL;
    @BindView(R.id.lV)
    ListView lV;
    @BindView(R.id.txtWish)
    TextView txtWish;
    @BindView(R.id.homebtn)
    Button homebtn;
    @BindView(R.id.printbtn)
    Button printbtn;
    @BindView(R.id.buttonLL)
    LinearLayout buttonLL;
    public static Printer_GEN ptrGen;
    int iRetVal;
    public static final int DEVICE_NOTCONNECTED = -100;
    SharedPreferences ActofitObject, OximeterObject, PersonalObject, ThermometerObject, BPObject, BiosenseObject, HemoglobinObject, spToken;
    @BindView(R.id.nameTV)
    TextView nameTV;
    @BindView(R.id.dobTV)
    TextView dobTV;
    @BindView(R.id.heightTV)
    TextView heightTV;
    @BindView(R.id.genderTV)
    TextView genderTV;
    @BindView(R.id.headerLL)
    LinearLayout headerLL;
    private String printString = "";
    private String printStringNew = "";
    private String fileName = "";

    public Dialog dlgCustomdialog;
    private LinearLayout llprog;
    public static ProgressBar pbProgress;
    private Button btnUnicode11, btnConfirm;
    private Dialog dlgBarcode;
    private Button btnOk;
    private ImageView ivDownload;
    public static int iWidth;

    int age;

    String parsedate1 = null;

    String currentDate;

    String currentTime;

    ProgressDialog pd;

    boolean isMale = false;


    double standardWeighRangeFrom;
    double standardWeighRangeTo;
    private int height;
    private double weight;

    private String standardWeightFrom, standardWeightTo;
    private String standarHemoglobin;
    private String standardBodyFat;
    private String standardBodyWater;
    private String standardSkeltonMuscle;
    private String standardBoneMass;
    private String standardMuscleMass;
    private String standardBMR;
    private String standardGlucose;

    private Hashtable<String, String> mhtDeviceInfo = new Hashtable<String, String>();
    public static BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
    public static BluetoothDevice mBDevice = null;
    private AndMedical_App_Global mGP = null;
    private boolean blBleStatusBefore = false;
    private String subcutaneousFat;
    private double standardMetabolism;
    private String standardWeightRange;
    private String standardVisceralFat;

    private String heightResult;
    private String weightResult;
    private String bmiResult;
    private String bmrResult;
    private String metaageResult;
    private String subcutaneousResult;
    private String visceralfatResult;
    private String skeletonmuscleResult;
    private String bodywaterResult;
    private String musclemassResult;
    private String fatfreeweightResult = "";
    private String proteinResult;
    private String bodyfatResult;
    private String bonemassResult;
    private String bloodpressureResult;
    private String oxygenResult;
    private String pulseResult;
    private String tempratureResult;
    private String hemoglobinResult;
    private String sugarResult;
    private String diastolicResult;
    private TextToSpeech tts;
    private String txt = "";
    private static final int  PERMISSION_STORAGE_CODE = 1000;
    private String downloadUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printpreview);
        ButterKnife.bind(this);

        tts = new TextToSpeech(this,this);

        ivDownload = findViewById(R.id.iv_download);

        txt = "Please click on the print button to get your printout";
        speakOut(txt);

        printerBond();

        gettingDataObjects();

        calculations();

        setNewList();

        getStandardRange();

        setStaticData();

        getPrintData();

        getResults();

        setupUI();

        postData();

        printerActivation();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //reinitialization of the tts engine for voice command
        tts = new TextToSpeech(this,this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        //close the tts engine to avoide runtime exception
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }

    }

    private void getResults() {

        if(PersonalObject.getString("gender","").equalsIgnoreCase("male")){
            //Calculate result as per male gender
            getMaleWeightResult();
            getMaleBMIResult();
            getMaleBodyFatResult();
            getMetaAgeResult();
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
            getBloodPressureResult();
            getDiastolicResult();
            getOximeterResult();
            getPulseResult();
            getTempratureResult();

        }else {
            //calculate result as per female gender
            getWeightResult();
            getBMIResult();
            getBodyFatResult();
            getMetaAgeResult();
            getSubcutaneousResult();
            getMaleVisceralFatResult();
            getBodWaterResult();
            getSkeletalMuscle();
            getMuscleMassResult();
            getBoneMassResult();
            getMaleProteinResult();
            getMaleBMRResult();
            getMaleGlucoseResult();
            getHemoglobinResult();
            getBloodPressureResult();
            getDiastolicResult();
            getOximeterResult();
            getPulseResult();
            getTempratureResult();
        }

    }

    private void getDiastolicResult() {
        if(!BPObject.getString("diastolic","").equalsIgnoreCase("")){
            if (Double.parseDouble(BPObject.getString("diastolic", "")) > 89) {
                diastolicResult = "High";
            } else if (Double.parseDouble(BPObject.getString("diastolic", "")) <= 89 && Double.parseDouble(BPObject.getString("diastolic", "")) >= 60) {
                diastolicResult = "standard";
            } else {
                diastolicResult = "Low";
            }
        }else{
            diastolicResult = "NA";
        }
    }

    private void getMetaAgeResult() {
        if(!ActofitObject.getString("metaage", "").equalsIgnoreCase("")){
            if(Double.parseDouble(ActofitObject.getString("metaage","")) <= age){
                metaageResult = "Standard";
            }else{
                metaageResult = "Not upto standard";
            }
        }else{
            metaageResult = "NA";
        }
    }

    private void getSubcutaneousResult() {
        if(!ActofitObject.getString("subfat","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("subfat", "")) > 26.7) {
                subcutaneousResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("subfat", "")) <= 26.7 && Double.parseDouble(ActofitObject.getString("subfat", "")) >= 18.5) {
                subcutaneousResult = "standard";
            } else {
                subcutaneousResult = "Low";
            }
        }else {
            subcutaneousResult = "NA";
        }
    }

    private void getHemoglobinResult() {
        if(!HemoglobinObject.getString("hemoglobin","").equalsIgnoreCase("")){
            if (Double.parseDouble(HemoglobinObject.getString("hemoglobin", "")) > 15.1) {
                hemoglobinResult = "High";
            } else if (Double.parseDouble(HemoglobinObject.getString("hemoglobin", "")) <= 15.1 && Double.parseDouble(HemoglobinObject.getString("hemoglobin", "")) >= 12.1) {
                hemoglobinResult = "standard";
            } else {
                hemoglobinResult = "Low";
            }
        }else{
            hemoglobinResult = "NA";
        }
    }

    private void getBoneMassResult() {

        if(!ActofitObject.getString("bonemass","").equalsIgnoreCase("")) {
            if (weight > 60) {
                standardBoneMass = "2.3 - 2.7kg";
                if (Double.parseDouble(ActofitObject.getString("bonemass", "")) > 2.7) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("bonemass", "")) <= 2.7 && Double.parseDouble(ActofitObject.getString("bonemass", "")) >= 2.3) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            } else if (weight <= 60 && weight >= 45) {
                standardBoneMass = "2.0-2.4kg";
                if (Double.parseDouble(ActofitObject.getString("bonemass", "")) > 2.4) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("bonemass", "")) <= 2.4 && Double.parseDouble(ActofitObject.getString("bonemass", "")) >= 2.0) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            } else if (weight < 45) {
                standardBoneMass = "1.6 - 2.0kg";
                if (Double.parseDouble(ActofitObject.getString("bonemass", "")) > 2.0) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("bonemass", "")) <= 2.0 && Double.parseDouble(ActofitObject.getString("bonemass", "")) >= 1.6) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            }
        }else{
            bonemassResult = "NA";
        }

    }

    private void getMuscleMassResult() {
        if(!ActofitObject.getString("musmass","").equalsIgnoreCase("")) {

            if (height > 170) {
                standardMuscleMass = "49.4-59.5kg";
                if (Double.parseDouble(ActofitObject.getString("musmass", "")) > 59.5) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("musmass", "")) <= 59.5 && Double.parseDouble(ActofitObject.getString("musmass", "")) >= 49.4) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            } else if (height <= 170 && height >= 160) {
                standardMuscleMass = "44-52.4kg";
                if (Double.parseDouble(ActofitObject.getString("musmass", "")) > 52.4) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("musmass", "")) <= 52.4 && Double.parseDouble(ActofitObject.getString("musmass", "")) >= 44) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            } else if (height < 160) {
                standardMuscleMass = "38.5-46.5kg";
                if (Double.parseDouble(ActofitObject.getString("musmass", "")) > 46.5) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("musmass", "")) <= 46.5 && Double.parseDouble(ActofitObject.getString("musmass", "")) >= 38.5) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            }
        }else{
            musclemassResult = "NA";
        }
    }

    private void getSkeletalMuscle() {
        if(!ActofitObject.getString("skemus","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("skemus", "")) > 50) {
                skeletonmuscleResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("skemus", "")) <= 50 && Double.parseDouble(ActofitObject.getString("skemus", "")) >= 40) {
                skeletonmuscleResult = "standard";
            } else {
                skeletonmuscleResult = "Low";
            }
        }else{
            skeletonmuscleResult = "NA";
        }
    }

    private void getBodWaterResult() {
        if(!ActofitObject.getString("bodywater","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("bodywater", "")) > 60) {
                bodywaterResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("bodywater", "")) <= 60 && Double.parseDouble(ActofitObject.getString("bodywater", "")) >= 45) {
                bodywaterResult = "standard";
            } else {
                bodywaterResult = "Low";
            }
        }else {
            bodywaterResult = "NA";
        }
    }

    private void getTempratureResult() {
        if(!ThermometerObject.getString("data","").equalsIgnoreCase("")){
            if (Double.parseDouble(ThermometerObject.getString("data", "")) > 99) {
                tempratureResult = "High";
            } else if (Double.parseDouble(ThermometerObject.getString("data", "")) <= 99 && Double.parseDouble(ThermometerObject.getString("data", "")) >= 97) {
                tempratureResult = "standard";
            } else {
                tempratureResult = "Low";
            }
        }else{
            tempratureResult = "NA";
        }
    }

    private void getPulseResult() {
        if(!OximeterObject.getString("pulse_rate","").equalsIgnoreCase("")){
            if (Double.parseDouble(OximeterObject.getString("pulse_rate", "")) > 100) {
                pulseResult = "High";
            } else if (Double.parseDouble(OximeterObject.getString("pulse_rate", "")) <= 100 && Double.parseDouble(OximeterObject.getString("pulse_rate", "")) >= 60) {
                pulseResult = "standard";
            } else {
                pulseResult = "Low";
            }
        }else{
            pulseResult = "NA";
        }
    }

    private void getOximeterResult() {
        if(!OximeterObject.getString("body_oxygen","").equalsIgnoreCase("")){
            if (Double.parseDouble(OximeterObject.getString("body_oxygen", "")) >= 94) {
                oxygenResult = "Standard";
            } else if (Double.parseDouble(OximeterObject.getString("body_oxygen", "")) < 94 ) {
                oxygenResult = "Low";
            }
        }else{
            oxygenResult = "NA";
        }
    }

    private void getBloodPressureResult() {
        if(!BPObject.getString("systolic","").equalsIgnoreCase("")){
            if (Double.parseDouble(BPObject.getString("systolic", "")) > 139) {
                bloodpressureResult = "High";
            } else if (Double.parseDouble(BPObject.getString("systolic", "")) <= 139 && Double.parseDouble(BPObject.getString("systolic", "")) >= 90) {
                bloodpressureResult = "standard";
            } else {
                bloodpressureResult = "Low";
            }
        }else{
            bloodpressureResult = "NA";
        }
    }


    private void getMaleHemoglobinResult() {
        if(!HemoglobinObject.getString("hemoglobin","").equalsIgnoreCase("")){
            if (Double.parseDouble(HemoglobinObject.getString("hemoglobin", "")) > 17.2) {
                hemoglobinResult = "High";
            } else if (Double.parseDouble(HemoglobinObject.getString("hemoglobin", "")) <= 17.2 && Double.parseDouble(HemoglobinObject.getString("hemoglobin", "")) >= 13.8) {
                hemoglobinResult = "standard";
            } else {
                hemoglobinResult = "Low";
            }
        }else{
            hemoglobinResult = "NA";
        }
    }

    private void getMaleGlucoseResult() {

        if(!BiosenseObject.getString("last","").equalsIgnoreCase("")) {

            if (BiosenseObject.getString("glucosetype", "").equals("Fasting (Before Meal)")) {
                standardGlucose = "70-100mg/dl(Fasting)";
                if (Double.parseDouble(BiosenseObject.getString("last", "")) > 100) {
                    sugarResult = "High";
                } else if (Double.parseDouble(BiosenseObject.getString("last", "")) <= 100 && Double.parseDouble(BiosenseObject.getString("last", "")) >= 70) {
                    sugarResult = "standard";
                } else {
                    sugarResult = "Low";
                }
            } else if (BiosenseObject.getString("glucosetype", "").equals("Post Prandial (After Meal)")) {
                standardGlucose = "70-140 mg/dl(Post Meal)";
                if (Double.parseDouble(BiosenseObject.getString("last", "")) > 140) {
                    sugarResult = "High";
                } else if (Double.parseDouble(BiosenseObject.getString("last", "")) <= 140 && Double.parseDouble(BiosenseObject.getString("last", "")) >= 70) {
                    sugarResult = "standard";
                } else {
                    sugarResult = "Low";
                }
            } else if (BiosenseObject.getString("glucosetype", "").equals("Random (Not Sure)")) {
                standardGlucose = "79-160 mg/dl(Random)";
                if (Double.parseDouble(BiosenseObject.getString("last", "")) > 160) {
                    sugarResult = "High";
                } else if (Double.parseDouble(BiosenseObject.getString("last", "")) <= 160 && Double.parseDouble(BiosenseObject.getString("last", "")) >= 79) {
                    sugarResult = "standard";
                } else {
                    sugarResult = "Low";
                }
            }
        }else{
            sugarResult = "NA";
        }

    }

    private void getMaleBMRResult() {
        if(!ActofitObject.getString("bmr","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("bmr", "")) >= standardMetabolism) {
                bmrResult = "High";
            } else {
                bmrResult = "Not upto Standard";
            }
        }else{
            bmrResult = "NA";
        }

    }

    private void getMaleProteinResult() {
        if(!ActofitObject.getString("protine","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("protine", "")) > 18) {
                proteinResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("protine", "")) <= 18 && Double.parseDouble(ActofitObject.getString("protine", "")) >= 16) {
                proteinResult = "standard";
            } else {
                proteinResult = "Low";
            }
        }else{
            proteinResult = "NA";
        }

    }

    private void getMaleBoneMassResult() {
        if(!ActofitObject.getString("bonemass","").equalsIgnoreCase("")) {
            if (weight > 75) {
                if (Double.parseDouble(ActofitObject.getString("bonemass", "")) > 3.4) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("bonemass", "")) <= 3.4 && Double.parseDouble(ActofitObject.getString("bonemass", "")) >= 3.0) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            } else if (weight <= 75 && weight >= 60) {
                if (Double.parseDouble(ActofitObject.getString("bonemass", "")) > 3.1) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("bonemass", "")) <= 3.1 && Double.parseDouble(ActofitObject.getString("bonemass", "")) >= 2.7) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            } else if (weight < 60) {
                if (Double.parseDouble(ActofitObject.getString("bonemass", "")) > 2.7) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("bonemass", "")) <= 2.7 && Double.parseDouble(ActofitObject.getString("bonemass", "")) >= 2.3) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            }
        }else{
            bonemassResult = "NA";
        }

    }


    private void getMaleMuscleMassResult() {
        if(!ActofitObject.getString("musmass","").equalsIgnoreCase("")) {

            if (height > 170) {
                standardMuscleMass = "49.4-59.5kg";
                if (Double.parseDouble(ActofitObject.getString("musmass", "")) > 59.5) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("musmass", "")) <= 59.5 && Double.parseDouble(ActofitObject.getString("musmass", "")) >= 49.4) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            } else if (height <= 170 && height >= 160) {
                standardMuscleMass = "44-52.4kg";
                if (Double.parseDouble(ActofitObject.getString("musmass", "")) > 52.4) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("musmass", "")) <= 52.4 && Double.parseDouble(ActofitObject.getString("musmass", "")) >= 44) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            } else if (height < 160) {
                standardMuscleMass = "38.5-46.5kg";
                if (Double.parseDouble(ActofitObject.getString("musmass", "")) > 46.5) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString("musmass", "")) <= 46.5 && Double.parseDouble(ActofitObject.getString("musmass", "")) >= 38.5) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            }
        }else{
            musclemassResult = "NA";
        }
    }

    private void getMaleSkeletalMuscle() {
        if(!ActofitObject.getString("skemus","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("skemus", "")) > 59) {
                skeletonmuscleResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("skemus", "")) <= 59 && Double.parseDouble(ActofitObject.getString("skemus", "")) >= 49) {
                skeletonmuscleResult = "standard";
            } else {
                skeletonmuscleResult = "Low";
            }
        }else{
            skeletonmuscleResult = "NA";
        }
    }

    private void getMaleBodyWaterResult() {
        if(!ActofitObject.getString("bodywater","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("bodywater", "")) > 65) {
                bodywaterResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("bodywater", "")) <= 65 && Double.parseDouble(ActofitObject.getString("bodywater", "")) >= 55) {
                bodywaterResult = "standard";
            } else {
                bodywaterResult = "Low";
            }
        }else {
            bodywaterResult = "NA";
        }
    }

    private void getMaleVisceralFatResult() {
        if(!ActofitObject.getString("visfat","").equalsIgnoreCase("")) {
            if(Double.parseDouble(ActofitObject.getString("visfat", "")) > 14){
                visceralfatResult = "Seriously High";
            }else if (Double.parseDouble(ActofitObject.getString("visfat", "")) > 9) {
                visceralfatResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("visfat", "")) <= 9) {
                visceralfatResult = "standard";
            }
        }else {
            visceralfatResult = "NA";
        }
    }

    private void getMaleSubcutaneousResult() {
        if(!ActofitObject.getString("subfat","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("subfat", "")) > 16.7) {
                subcutaneousResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("subfat", "")) <= 16.7 && Double.parseDouble(ActofitObject.getString("subfat", "")) >= 8.6) {
                subcutaneousResult = "standard";
            } else {
                subcutaneousResult = "Low";
            }
        }else {
            subcutaneousResult = "NA";
        }
    }

    private void getMaleBodyFatResult() {
        if(!ActofitObject.getString("bodyfat","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("bodyfat", "")) > 26) {
                bodyfatResult = "Seriously High";
            } else if (Double.parseDouble(ActofitObject.getString("bodyfat", "")) <= 26 && Double.parseDouble(ActofitObject.getString("bodyfat", "")) >= 22) {
                bodyfatResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("bodyfat", "")) <= 21 && Double.parseDouble(ActofitObject.getString("bodyfat", "")) >= 11) {
                bodyfatResult = "Standard";
            }else{
                bodyfatResult = "Low";
            }
        }else {
            bodyfatResult = "NA";
        }
    }

    private void getMaleBMIResult() {
        if(!ActofitObject.getString("bmi","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("bmi", "")) > 25) {
                bmiResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("bmi", "")) <= 25 && Double.parseDouble(ActofitObject.getString("bmi", "")) >= 18.5) {
                bmiResult = "standard";
            } else {
                bmiResult = "Low";
            }
        }else {
            bmiResult = "NA";
        }
    }

    private void getMaleWeightResult() {
        if(!ActofitObject.getString("weight","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("weight", "")) > standardWeighRangeTo) {
                weightResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("weight", "")) <= standardWeighRangeTo && Double.parseDouble(ActofitObject.getString("weight", "")) >= standardWeighRangeFrom) {
                weightResult = "standard";
            } else {
                weightResult = "Low";
            }
        }else{
            weightResult = "NA";
        }
    }

    private void getBodyFatResult() {
        if(!ActofitObject.getString("bodyfat","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("bodyfat", "")) > 36) {
                bodyfatResult = "Seriously High";
            } else if (Double.parseDouble(ActofitObject.getString("bodyfat", "")) <= 36 && Double.parseDouble(ActofitObject.getString("bodyfat", "")) >= 31) {
                bodyfatResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("bodyfat", "")) <= 30 && Double.parseDouble(ActofitObject.getString("bodyfat", "")) >= 21) {
                bodyfatResult = "Standard";
            }else{
                bodyfatResult = "Low";
            }
        }else{
            bodyfatResult = "NA";
        }
    }

    private void getBMIResult() {
        if(!ActofitObject.getString("bmi","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("bmi", "")) > 25) {
                bmiResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("bmi", "")) <= 25 && Double.parseDouble(ActofitObject.getString("bmi", "")) >= 18.5) {
                bmiResult = "standard";
            } else {
                bmiResult = "Low";
            }
        }else{
            bmiResult = "NA";
        }
    }

    private void getWeightResult() {
        if(!ActofitObject.getString("weight","").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString("weight", "")) > standardWeighRangeTo) {
                weightResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString("weight", "")) <= standardWeighRangeTo && Double.parseDouble(ActofitObject.getString("weight", "")) >= standardWeighRangeFrom) {
                weightResult = "standard";
            } else {
                weightResult = "Low";
            }
        }else{
            weightResult = "NA";
        }

    }

    private void setupUI() {
        Log.e("weightresult", ""+weightResult);
        Log.e("bmiresult", ""+bmiResult);
        Log.e("bmrresult", ""+bmrResult);
        Log.e("metaageresult", ""+metaageResult);
        Log.e("subcutaneousresult", ""+subcutaneousResult);
        Log.e("visceralfatresult", ""+visceralfatResult);
        Log.e("skeletonmuscleresult", ""+skeletonmuscleResult);
        Log.e("bodywaterresult", ""+bodywaterResult);
        Log.e("musclemassresult", ""+musclemassResult);
        Log.e("fatfreeweightresult", ""+fatfreeweightResult);
        Log.e("proteinresult", ""+proteinResult);
        Log.e("bodyfatresult", ""+bodyfatResult);
        Log.e("bonemassresult", ""+bonemassResult);
        Log.e("bloodpressureresult", ""+bloodpressureResult);
        Log.e("oxygenresult", ""+oxygenResult);
        Log.e("pulseresult", ""+pulseResult);
        Log.e("temperatureresult", ""+tempratureResult);
        Log.e("hemoglobinresult", ""+hemoglobinResult);
        Log.e("sugarresult", ""+sugarResult);
    }

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

        }
    }

    private void printerActivation() {

        iWidth = getWindowManager().getDefaultDisplay().getWidth();
        try {
            InputStream input = BluetoothComm.misIn;
            OutputStream outstream = BluetoothComm.mosOut;
            ptrGen = new Printer_GEN(Act_GlobalPool.setup, outstream, input);
            Log.e("activate", "pirnter gen is activated");
        } catch (Exception e) {
            Log.e("de_activate", "pirnter gen is not activated" + e);
        }
    }

    private void getStandardRange() {


        if (ActofitObject.getString("weight", "").equalsIgnoreCase("")) {
            weight = 0.0;
        } else {
            weight = Double.parseDouble(ActofitObject.getString("weight", ""));
        }


        if(!ActofitObject.getString("height","").equalsIgnoreCase("")){
            height = Integer.parseInt(ActofitObject.getString("height", ""));
        }else {
            height = 0;
        }


        double standardWeightMen = ((height - 80) * 0.7);
        standardWeightMen = Double.parseDouble(new DecimalFormat("#.##").format(standardWeightMen));
        double standardWeightFemale = (((height * 1.37) - 110) * 0.45);
        standardWeightFemale = Double.parseDouble(new DecimalFormat("#.##").format(standardWeightFemale));



        glucoseRange();

        if (PersonalObject.getString("gender", "").equals("male")) {
            maleRange(standardWeightMen);
        } else {
            femaleRange(standardWeightFemale);
        }

    }

    //creating region on glucoseRange
    private void glucoseRange() {

        if (BiosenseObject.getString("glucosetype", "").equals("Fasting (Before Meal)")) {
            standardGlucose = "70-100mg/dl(Fasting)";
        } else if (BiosenseObject.getString("glucosetype", "").equals("Post Prandial (After Meal)")) {
            standardGlucose = "70-140 mg/dl(Post Meal)";
        } else if (BiosenseObject.getString("glucosetype", "").equals("Random (Not Sure)")) {
            standardGlucose = "79-160 mg/dl(Random)";
        }else{
            standardGlucose = "";
        }

    }

    private void femaleRange(double standardWeightFemale) {
        standardWeighRangeFrom = (0.90 * standardWeightFemale);
        standardWeighRangeFrom = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeFrom));
        standardWeighRangeTo = (1.09 * standardWeightFemale);
        standardWeighRangeTo = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeTo));

        standardWeightRange = String.valueOf(standardWeighRangeFrom)+"-"+String.valueOf(standardWeighRangeTo);
        Log.e("standardWeightRange",""+standardWeightRange);

        if (height > 160) {
            standardMuscleMass = "36.4-42.5kg";
        } else if (height <= 160 && height >= 150) {
            standardMuscleMass = "32.9-37.5kg";
        } else if (height < 150) {
            standardMuscleMass = "29.1-34.7kg";
        }else{
            standardMuscleMass = "";
        }

        if (weight > 60) {
            standardBoneMass = "2.3 - 2.7kg";
        } else if (weight <= 60 && weight >= 45) {
            standardBoneMass = "2.0-2.4kg";
        } else if (weight < 45) {
            standardBoneMass = "1.6 - 2.0kg";
        }else{
            standardBoneMass = "";
        }

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

        standardBodyFat = "21-30(%)";
        standarHemoglobin = "12.1-15.1gm/dl";
        standardBodyFat = "21-30(%)";
        standardBodyWater = "45-60(%)";
        standardSkeltonMuscle = "40-50(%)";
        subcutaneousFat = "18.5-26.7(%)";
        standardVisceralFat = "<=9";
        standardBMR = " > ="+String.valueOf(standardMetabolism)+"kcal";
        Log.e("standardWeightFrom", "" + standardWeightFrom);
        Log.e("standardWeightTo", "" + standardWeightTo);

    }

    private void maleRange(double standardWeightMen) {

        standarHemoglobin = "13.8-17.2gm/dl";
        standardBodyFat = "11-21(%)";
        standardBodyWater = "55-65(%)";
        standardSkeltonMuscle = "49-59(%)";
        standardWeighRangeFrom = (0.90 * standardWeightMen);
        standardWeighRangeFrom = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeFrom));
        standardWeighRangeTo = (1.09 * standardWeightMen);
        standardWeighRangeTo = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeTo));

        standardWeightRange = String.valueOf(standardWeighRangeFrom)+"-"+String.valueOf(standardWeighRangeTo);;
        Log.e("standardWeightRange",""+standardWeightRange);

        if (height > 170) {
            standardMuscleMass = "49.4-59.5kg";
        } else if (height <= 170 && height >= 160) {
            standardMuscleMass = "44-52.4kg";
        } else if (height < 160) {
            standardMuscleMass = "38.5-46.5kg";
        }else{
            standardMuscleMass = "";
        }

        if (weight > 75) {
            standardBoneMass = "3.0-3.4kg";
        } else if (weight <= 75 && weight >= 60) {
            standardBoneMass = "2.7-3.1kg";
        } else if (weight < 60) {
            standardBoneMass = "2.3-2.7kg";
        }else{
            standardBoneMass = "";
        }

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
        subcutaneousFat = "8.6-16.7(%)";
        standardBMR = " > ="+String.valueOf(standardMetabolism)+"kcal";
        standardVisceralFat = "< = 9";
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //Disable the back button
    }

    private void setStaticData() {

        nameTV.setText("Name :" + PersonalObject.getString("name", ""));
        dobTV.setText("DOB :" + PersonalObject.getString("dob", ""));
        heightTV.setText("Height :" + ActofitObject.getString("height", ""));
        genderTV.setText("Gender :" + PersonalObject.getString("gender", ""));

    }
/*
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PrintPreviewActivity.this,Act_Main.class);
        try {
            AndMedical_App_Global.mBTcomm = null;
        } catch(NullPointerException e) { }
        startActivity(intent);
//        super.onBackPressed();
    }*/

    private void calculations() {
        String parsedDate = null;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String inputText = PersonalObject.getString("dob", "");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yy");
        try {
            Date date = inputDateFormat.parse(inputText);
            parsedDate = formatter.format(date);
            parsedate1 = formatter1.format(date);
            System.out.println("Date---------" + parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        age = getAge(parsedDate);

        Log.e("age", "" + age);

        SimpleDateFormat formatterCurrent = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        currentDate = formatterCurrent.format(date);
        currentTime = getCurrentTime();

    }

    private void getPrintData() {

        if(!BiosenseObject.getString("last","").equalsIgnoreCase("") && !HemoglobinObject.getString("hemoglobin","").equalsIgnoreCase("")) {

            printStringNew = "" + "  " + "Clinics On Cloud" + "" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Name :" + PersonalObject.getString("name", "") + "\n" +
                    "Age :" + age + "   " + "Gender :" + PersonalObject.getString("gender", "") + "\n" +
                    "" + currentDate + "  " + "" + currentTime + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Height :" + ActofitObject.getString("height", "") + "CM" + "\n" +
                    "Weight :" + ActofitObject.getString("weight", "") + "Kg" + "\n" +
                    "[Normal Range]:" + "\n"
                    + standardWeighRangeFrom + "-" + standardWeighRangeTo + "kg" + "\n" +
                    "BMI :" + "" + ActofitObject.getString("bmi", "") + "\n" +
                    "[Normal Range]:" + "18.5 - 25" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Body Fat :" + ActofitObject.getString("bodyfat", "") + "%" + "\n" +
                    "[Normal Range] :" + standardBodyFat + "\n\n" +
                    "Fat Free Weight :" + ActofitObject.getString("fatfreeweight", "") + "Kg" + "\n\n" +
                    "Subcutaneous Fat :" + ActofitObject.getString("subfat", "") + "%" + "\n" +
                    "[Normal Range]:" + subcutaneousFat + "\n\n" +
                    "Visceral Fat :" + ActofitObject.getString("visfat", "") + "\n" +
                    "[Normal Range]:" + "<=9" + "\n\n" +
                    "Body Water : " + ActofitObject.getString("bodywater", "") + "\n" +
                    "[Normal Range]:" +
                    standardBodyWater + "\n\n" +
                    "Skeletal Muscle :" + ActofitObject.getString("skemus", "") + "\n" +
                    "[Normal Range]:" +
                    standardSkeltonMuscle + "\n\n" +
                    "Muscle Mass :" + ActofitObject.getString("musmass", "") + "\n" +
                    "[Normal Range]:" + standardMuscleMass + "\n\n" +
                    "Bone Mass :" + ActofitObject.getString("bonemass", "") + "\n" +
                    "[Normal Range]:" + standardBoneMass + "\n\n" +
                    "Protein :" + ActofitObject.getString("protine", "") + "\n" +
                    "[Normal Range]:" + "16-18(%)" + "\n\n" +
                    "BMR :" + ActofitObject.getString("bmr", "") + "\n" +
                    "[Normal Range]:" + "\n"
                    + "> = " + standardMetabolism + "Kcal" + "\n\n" +
                    "Physique:" + ActofitObject.getString("physique", "") + "\n\n" +
                    "Meta Age :" + ActofitObject.getString("metaage", "") + "yrs" + "\n\n" +
                    "Health Score :" + ActofitObject.getString("helthscore", "") + "\n\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Glucose :" + BiosenseObject.getString("last", "") + "mg/dl" + "\n" +
                    "[Normal Range]:" + "\n" +
                    standardGlucose + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Hemoglobin :" + HemoglobinObject.getString("hemoglobin", "") + " g/dl" + "\n" +
                    "[Normal Range]:" + "\n" +
                    standarHemoglobin + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Pressure :" + "\n" +
                    "Systolic :" + BPObject.getString("systolic", "") + "mmHg" + "\n" +
                    "Diastolic :" + BPObject.getString("diastolic", "") + "mmHg" + "\n" +
                    "[Normal Range]:" + "\n" +
                    "systolic :" + "90-139mmHg" + "\n" +
                    "Diastolic :" + "60-89mmHg" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Oxygen :" + OximeterObject.getString("body_oxygen", "") + " %" + "\n" +
                    "[Normal Range]:" + ">94%" + "\n" +
                    "Pulse Rate: " + OximeterObject.getString("pulse_rate", "") + " bpm" + "\n" +
                    "[Normal Range]:" + "60-100bpm" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Temperature :" + ThermometerObject.getString("data", "") +"F"+ "\n" +
                    "[Normal Range]:" + "97-99F " + "\n" +
                    "" + "-----------------------" + "\n" +
                    "       " + "Thank You" + "\n" +
                    "" + "Above are the indicative" + "\n" +
                    "   " + "figure consult your" + "\n" +
                    "        " + "doctor" + "\n\n\n\n\n\n";

        }else if(!BiosenseObject.getString("last","").equalsIgnoreCase("") && HemoglobinObject.getString("hemoglobin","").equalsIgnoreCase("")){
            printStringNew = "" + "  " + "Clinics On Cloud" + "" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Name :" + PersonalObject.getString("name", "") + "\n" +
                    "Age :" + age + "   " + "Gender :" + PersonalObject.getString("gender", "") + "\n" +
                    "" + currentDate + "  " + "" + currentTime + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Height :" + ActofitObject.getString("height", "") + "CM" + "\n" +
                    "Weight :" + ActofitObject.getString("weight", "") + "Kg" + "\n" +
                    "[Normal Range]:" + "\n"
                    + standardWeighRangeFrom + "-" + standardWeighRangeTo + "kg" + "\n" +
                    "BMI :" + "" + ActofitObject.getString("bmi", "") + "\n" +
                    "[Normal Range]:" + "18.5 - 25" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Body Fat :" + ActofitObject.getString("bodyfat", "") + "%" + "\n" +
                    "[Normal Range] :" + standardBodyFat + "\n\n" +
                    "Fat Free Weight :" + ActofitObject.getString("fatfreeweight", "") + "Kg" + "\n\n" +
                    "Subcutaneous Fat :" + ActofitObject.getString("subfat", "") + "%" + "\n" +
                    "[Normal Range]:" + subcutaneousFat + "\n\n" +
                    "Visceral Fat :" + ActofitObject.getString("visfat", "") + "\n" +
                    "[Normal Range]:" + "<=9" + "\n\n" +
                    "Body Water : " + ActofitObject.getString("bodywater", "") + "\n" +
                    "[Normal Range]:" +
                    standardBodyWater + "\n\n" +
                    "Skeletal Muscle :" + ActofitObject.getString("skemus", "") + "\n" +
                    "[Normal Range]:" +
                    standardSkeltonMuscle + "\n\n" +
                    "Muscle Mass :" + ActofitObject.getString("musmass", "") + "\n" +
                    "[Normal Range]:" + standardMuscleMass + "\n\n" +
                    "Bone Mass :" + ActofitObject.getString("bonemass", "") + "\n" +
                    "[Normal Range]:" + standardBoneMass + "\n\n" +
                    "Protein :" + ActofitObject.getString("protine", "") + "\n" +
                    "[Normal Range]:" + "16-18(%)" + "\n\n" +
                    "BMR :" + ActofitObject.getString("bmr", "") + "\n" +
                    "[Normal Range]:" + "\n"
                    + "> = " + standardMetabolism + "Kcal" + "\n\n" +
                    "Physique:" + ActofitObject.getString("physique", "") + "\n\n" +
                    "Meta Age :" + ActofitObject.getString("metaage", "") + "yrs" + "\n\n" +
                    "Health Score :" + ActofitObject.getString("helthscore", "") + "\n\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Glucose :" + BiosenseObject.getString("last", "") + "mg/dl" + "\n" +
                    "[Normal Range]:" + "\n" +
                    standardGlucose + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Hemoglobin :" +"NA" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Pressure :" + "\n" +
                    "Systolic :" + BPObject.getString("systolic", "") + "mmHg" + "\n" +
                    "Diastolic :" + BPObject.getString("diastolic", "") + "mmHg" + "\n" +
                    "[Normal Range]:" + "\n" +
                    "systolic :" + "90-139mmHg" + "\n" +
                    "Diastolic :" + "60-89mmHg" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Oxygen :" + OximeterObject.getString("body_oxygen", "") + " %" + "\n" +
                    "[Normal Range]:" + ">94%" + "\n" +
                    "Pulse Rate: " + OximeterObject.getString("pulse_rate", "") + " bpm" + "\n" +
                    "[Normal Range]:" + "60-100bpm" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Temperature :" + ThermometerObject.getString("data", "") +"F"+ "\n" +
                    "[Normal Range]:" + "97-99F " + "\n" +
                    "" + "-----------------------" + "\n" +
                    "       " + "Thank You" + "\n" +
                    "" + "Above are the indicative" + "\n" +
                    "   " + "figure consult your" + "\n" +
                    "        " + "doctor" + "\n\n\n\n\n\n";
        }else if(BiosenseObject.getString("last","").equalsIgnoreCase("") && !HemoglobinObject.getString("hemoglobin","").equalsIgnoreCase("")){
            printStringNew = "" + "  " + "Clinics On Cloud" + "" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Name :" + PersonalObject.getString("name", "") + "\n" +
                    "Age :" + age + "   " + "Gender :" + PersonalObject.getString("gender", "") + "\n" +
                    "" + currentDate + "  " + "" + currentTime + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Height :" + ActofitObject.getString("height", "") + "CM" + "\n" +
                    "Weight :" + ActofitObject.getString("weight", "") + "Kg" + "\n" +
                    "[Normal Range]:" + "\n"
                    + standardWeighRangeFrom + "-" + standardWeighRangeTo + "kg" + "\n" +
                    "BMI :" + "" + ActofitObject.getString("bmi", "") + "\n" +
                    "[Normal Range]:" + "18.5 - 25" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Body Fat :" + ActofitObject.getString("bodyfat", "") + "%" + "\n" +
                    "[Normal Range] :" + standardBodyFat + "\n\n" +
                    "Fat Free Weight :" + ActofitObject.getString("fatfreeweight", "") + "Kg" + "\n\n" +
                    "Subcutaneous Fat :" + ActofitObject.getString("subfat", "") + "%" + "\n" +
                    "[Normal Range]:" + subcutaneousFat + "\n\n" +
                    "Visceral Fat :" + ActofitObject.getString("visfat", "") + "\n" +
                    "[Normal Range]:" + "<=9" + "\n\n" +
                    "Body Water : " + ActofitObject.getString("bodywater", "") + "\n" +
                    "[Normal Range]:" +
                    standardBodyWater + "\n\n" +
                    "Skeletal Muscle :" + ActofitObject.getString("skemus", "") + "\n" +
                    "[Normal Range]:" +
                    standardSkeltonMuscle + "\n\n" +
                    "Muscle Mass :" + ActofitObject.getString("musmass", "") + "\n" +
                    "[Normal Range]:" + standardMuscleMass + "\n\n" +
                    "Bone Mass :" + ActofitObject.getString("bonemass", "") + "\n" +
                    "[Normal Range]:" + standardBoneMass + "\n\n" +
                    "Protein :" + ActofitObject.getString("protine", "") + "\n" +
                    "[Normal Range]:" + "16-18(%)" + "\n\n" +
                    "BMR :" + ActofitObject.getString("bmr", "") + "\n" +
                    "[Normal Range]:" + "\n"
                    + "> = " + standardMetabolism + "Kcal" + "\n\n" +
                    "Physique:" + ActofitObject.getString("physique", "") + "\n\n" +
                    "Meta Age :" + ActofitObject.getString("metaage", "") + "yrs" + "\n\n" +
                    "Health Score :" + ActofitObject.getString("helthscore", "") + "\n\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Glucose :" + "NA"+ "\n" +
                    "" + "-----------------------" + "\n" +
                    "Hemoglobin :" + HemoglobinObject.getString("hemoglobin", "") + " g/dl" + "\n" +
                    "[Normal Range]:" + "\n" +
                    standarHemoglobin + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Pressure :" + "\n" +
                    "Systolic :" + BPObject.getString("systolic", "") + "mmHg" + "\n" +
                    "Diastolic :" + BPObject.getString("diastolic", "") + "mmHg" + "\n" +
                    "[Normal Range]:" + "\n" +
                    "systolic :" + "90-139mmHg" + "\n" +
                    "Diastolic :" + "60-89mmHg" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Oxygen :" + OximeterObject.getString("body_oxygen", "") + " %" + "\n" +
                    "[Normal Range]:" + ">94%" + "\n" +
                    "Pulse Rate: " + OximeterObject.getString("pulse_rate", "") + " bpm" + "\n" +
                    "[Normal Range]:" + "60-100bpm" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Temperature :" + ThermometerObject.getString("data", "") +"F"+ "\n" +
                    "[Normal Range]:" + "97-99F " + "\n" +
                    "" + "-----------------------" + "\n" +
                    "       " + "Thank You" + "\n" +
                    "" + "Above are the indicative" + "\n" +
                    "   " + "figure consult your" + "\n" +
                    "        " + "doctor" + "\n\n\n\n\n\n";
        }else{

            printStringNew = "" + "  " + "Clinics On Cloud" + "" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Name :" + PersonalObject.getString("name", "") + "\n" +
                    "Age :" + age + "   " + "Gender :" + PersonalObject.getString("gender", "") + "\n" +
                    "" + currentDate + "  " + "" + currentTime + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Height :" + ActofitObject.getString("height", "") + "CM" + "\n" +
                    "Weight :" + ActofitObject.getString("weight", "") + "Kg" + "\n" +
                    "[Normal Range]:" + "\n"
                    + standardWeighRangeFrom + "-" + standardWeighRangeTo + "kg" + "\n" +
                    "BMI :" + "" + ActofitObject.getString("bmi", "") + "\n" +
                    "[Normal Range]:" + "18.5 - 25" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Body Fat :" + ActofitObject.getString("bodyfat", "") + "%" + "\n" +
                    "[Normal Range] :" + standardBodyFat + "\n\n" +
                    "Fat Free Weight :" + ActofitObject.getString("fatfreeweight", "") + "Kg" + "\n\n" +
                    "Subcutaneous Fat :" + ActofitObject.getString("subfat", "") + "%" + "\n" +
                    "[Normal Range]:" + subcutaneousFat + "\n\n" +
                    "Visceral Fat :" + ActofitObject.getString("visfat", "") + "\n" +
                    "[Normal Range]:" + "<=9" + "\n\n" +
                    "Body Water : " + ActofitObject.getString("bodywater", "") + "\n" +
                    "[Normal Range]:" +
                    standardBodyWater + "\n\n" +
                    "Skeletal Muscle :" + ActofitObject.getString("skemus", "") + "\n" +
                    "[Normal Range]:" +
                    standardSkeltonMuscle + "\n\n" +
                    "Muscle Mass :" + ActofitObject.getString("musmass", "") + "\n" +
                    "[Normal Range]:" + standardMuscleMass + "\n\n" +
                    "Bone Mass :" + ActofitObject.getString("bonemass", "") + "\n" +
                    "[Normal Range]:" + standardBoneMass + "\n\n" +
                    "Protein :" + ActofitObject.getString("protine", "") + "\n" +
                    "[Normal Range]:" + "16-18(%)" + "\n\n" +
                    "BMR :" + ActofitObject.getString("bmr", "") + "\n" +
                    "[Normal Range]:" + "\n"
                    + "> = " + standardMetabolism + "Kcal" + "\n\n" +
                    "Physique:" + ActofitObject.getString("physique", "") + "\n\n" +
                    "Meta Age :" + ActofitObject.getString("metaage", "") + "yrs" + "\n\n" +
                    "Health Score :" + ActofitObject.getString("helthscore", "") + "\n\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Glucose :" + "NA"+ "\n" +
                    "" + "-----------------------" + "\n" +
                    "Hemoglobin :" +"NA" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Pressure :" + "\n" +
                    "Systolic :" + BPObject.getString("systolic", "") + "mmHg" + "\n" +
                    "Diastolic :" + BPObject.getString("diastolic", "") + "mmHg" + "\n" +
                    "[Normal Range]:" + "\n" +
                    "systolic :" + "90-139mmHg" + "\n" +
                    "Diastolic :" + "60-89mmHg" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Oxygen :" + OximeterObject.getString("body_oxygen", "") + " %" + "\n" +
                    "[Normal Range]:" + ">94%" + "\n" +
                    "Pulse Rate: " + OximeterObject.getString("pulse_rate", "") + " bpm" + "\n" +
                    "[Normal Range]:" + "60-100bpm" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Temperature :" + ThermometerObject.getString("data", "") +"F"+ "\n" +
                    "[Normal Range]:" + "97-99F " + "\n" +
                    "" + "-----------------------" + "\n" +
                    "       " + "Thank You" + "\n" +
                    "" + "Above are the indicative" + "\n" +
                    "   " + "figure consult your" + "\n" +
                    "        " + "doctor" + "\n\n\n\n\n\n";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setNewList() {

        printDataListNew.add(new PrintDataNew("Weight", TextUtils.isEmpty(ActofitObject.getString("weight", "")) ? 0 : Double.parseDouble(ActofitObject.getString("weight", ""))));
        printDataListNew.add(new PrintDataNew("BMI", TextUtils.isEmpty(ActofitObject.getString("bmi", "")) ? 0 : Double.parseDouble(ActofitObject.getString("bmi", ""))));
        printDataListNew.add(new PrintDataNew("Body fat", TextUtils.isEmpty(ActofitObject.getString("bodyfat", "")) ? 0 : Double.parseDouble(ActofitObject.getString("bodyfat", ""))));
        printDataListNew.add(new PrintDataNew("Fat Free weight", TextUtils.isEmpty(ActofitObject.getString("fatfreeweight", "")) ? 0 : Double.parseDouble(ActofitObject.getString("fatfreeweight", ""))));
        printDataListNew.add(new PrintDataNew("Subcutaneous Fat", TextUtils.isEmpty(ActofitObject.getString("subfat", "")) ? 0 : Double.parseDouble(ActofitObject.getString("subfat", ""))));
        printDataListNew.add(new PrintDataNew("Visceral Fat", TextUtils.isEmpty(ActofitObject.getString("visfat", "")) ? 0 : Double.parseDouble(ActofitObject.getString("visfat", ""))));
        printDataListNew.add(new PrintDataNew("Body water", TextUtils.isEmpty(ActofitObject.getString("bodywater", "")) ? 0 : Double.parseDouble(ActofitObject.getString("bodywater", ""))));
        printDataListNew.add(new PrintDataNew("Skeleton muscle", TextUtils.isEmpty(ActofitObject.getString("skemus", "")) ? 0 : Double.parseDouble(ActofitObject.getString("skemus", ""))));
        printDataListNew.add(new PrintDataNew("Protein", TextUtils.isEmpty(ActofitObject.getString("protine", "")) ? 0 : Double.parseDouble(ActofitObject.getString("protine", ""))));
        printDataListNew.add(new PrintDataNew("Metabolic Age", TextUtils.isEmpty(ActofitObject.getString("metaage", "")) ? 0 : Double.parseDouble(ActofitObject.getString("metaage", ""))));
        printDataListNew.add(new PrintDataNew("Health Score", TextUtils.isEmpty(ActofitObject.getString("helthscore", "")) ? 0 : Double.parseDouble(ActofitObject.getString("helthscore", ""))));
        printDataListNew.add(new PrintDataNew("BMR", TextUtils.isEmpty(ActofitObject.getString("bmr", "")) ? 0 : Double.parseDouble(ActofitObject.getString("bmr", ""))));
        printDataListNew.add(new PrintDataNew("Physique", 0.0));
        printDataListNew.add(new PrintDataNew("Muscle Mass", TextUtils.isEmpty(ActofitObject.getString("musmass", "")) ? 0 : Double.parseDouble(ActofitObject.getString("musmass", ""))));
        printDataListNew.add(new PrintDataNew("Bone Mass", TextUtils.isEmpty(ActofitObject.getString("bonemass", "")) ? 0 : Double.parseDouble(ActofitObject.getString("bonemass", ""))));
        printDataListNew.add(new PrintDataNew("Body Temp", TextUtils.isEmpty(ThermometerObject.getString("data", "")) ? 0 : Double.parseDouble(ThermometerObject.getString("data", ""))));
        printDataListNew.add(new PrintDataNew("Systolic", TextUtils.isEmpty(BPObject.getString("systolic", "")) ? 0 : Double.parseDouble(BPObject.getString("systolic", ""))));
        printDataListNew.add(new PrintDataNew("Diastolic", TextUtils.isEmpty(BPObject.getString("diastolic", "")) ? 0 : Double.parseDouble(BPObject.getString("diastolic", ""))));
        printDataListNew.add(new PrintDataNew("Pulse Oximeter", TextUtils.isEmpty(OximeterObject.getString("body_oxygen", "")) ? 0 : Double.parseDouble(OximeterObject.getString("body_oxygen", ""))));
        printDataListNew.add(new PrintDataNew("Pulse ", TextUtils.isEmpty(OximeterObject.getString("pulse_rate", "")) ? 0 : Double.parseDouble(OximeterObject.getString("pulse_rate", ""))));
        printDataListNew.add(new PrintDataNew("Blood Glucose", TextUtils.isEmpty(BiosenseObject.getString("last", "")) ? 0 : Double.parseDouble(BiosenseObject.getString("last", ""))));
        printDataListNew.add(new PrintDataNew("Hemoglobin", TextUtils.isEmpty(HemoglobinObject.getString("hemoglobin", "")) ? 0 : Double.parseDouble(HemoglobinObject.getString("hemoglobin", ""))));

        lV.setAdapter(new PrintPreviewActivityNew(this, R.layout.printlist_item, printDataListNew));

    }

    private void postData() {
//        pd = Tools.progressDialog(PrintPreviewActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUtils.PRINTPOST_URL,
                response -> {
                    //Disimissing the progress dialog
                    System.out.println("Login_Response" + response);

                    readFileName(response);

                    try {
//                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Data Uploaded on Server", Toast.LENGTH_SHORT).show();
                        //  dlgEnterText();
                    } catch (Exception e) {

                    }
                },
                volleyError -> {
//                    pd.dismiss();
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
                params.put("height", ActofitObject.getString("height", ""));
                params.put("weight", ActofitObject.getString("weight", ""));
                params.put("gender", PersonalObject.getString("gender", ""));
                params.put("bmi", ActofitObject.getString("bmi", ""));
                params.put("bmr", ActofitObject.getString("bmr", ""));
                params.put("meta_age", ActofitObject.getString("metaage", ""));
                params.put("health_score", ActofitObject.getString("helthscore", ""));
                params.put("physique", ActofitObject.getString("physique", ""));
                params.put("subcutaneous", ActofitObject.getString("subfat", ""));
                params.put("visceral_fat", ActofitObject.getString("visfat", ""));
                params.put("skeleton_muscle", ActofitObject.getString("skemus", ""));
                params.put("body_water", ActofitObject.getString("bodywater", ""));
                params.put("muscle_mass", ActofitObject.getString("musmass", ""));
                params.put("fat_free_weight", ActofitObject.getString("fatfreeweight", ""));
                params.put("protein", ActofitObject.getString("protine", ""));
                params.put("body_fat", ActofitObject.getString("bodyfat", ""));
                params.put("bone_mass", ActofitObject.getString("bonemass", ""));
                params.put("blood_pressure", BPObject.getString("systolic", ""));
                params.put("dialostic", BPObject.getString("diastolic", ""));
                params.put("oxygen", OximeterObject.getString("body_oxygen", ""));
                params.put("pulse", OximeterObject.getString("pulse_rate", ""));
                params.put("temperature", ThermometerObject.getString("data", ""));
                params.put("hemoglobin", HemoglobinObject.getString("hemoglobin", ""));
                params.put("sugar", BiosenseObject.getString("last", ""));
                if(!ActofitObject.getString("weight","").equalsIgnoreCase("")) {
                    params.put("weightrange", "" + standardWeightRange+"kg");
                }else{
                    params.put("weightrange", "NA");
                }
                if(!ActofitObject.getString("bmi","").equalsIgnoreCase("")) {
                    params.put("bmirange", "18.5-25");
                }else{
                    params.put("bmirange", "NA");
                }
                if(!ActofitObject.getString("bodyfat","").equalsIgnoreCase("")) {
                    params.put("bodyfatrange", standardBodyFat);
                }else{
                    params.put("bodyfatrange", "NA");
                }
                if(!ActofitObject.getString("subfat","").equalsIgnoreCase("")) {
                    params.put("subfatrange", subcutaneousFat);
                }else{
                    params.put("subfatrange", "NA");
                }
                if(!ActofitObject.getString("visfat","").equalsIgnoreCase("")) {
                    params.put("visceralfatrange",standardVisceralFat);
                    Log.e("viscerialFatRange",""+standardVisceralFat);
                }else{
                    params.put("visceralfatrange","NA");
                }
                if(!ActofitObject.getString("bodywater","").equalsIgnoreCase("")) {
                    params.put("bodywaterrange", standardBodyWater);
                }else{
                    params.put("bodywaterrange", "NA");
                }
                if(!ActofitObject.getString("skemus","").equalsIgnoreCase("")) {
                    params.put("skeletanmusclerange", standardSkeltonMuscle);
                }else{
                    params.put("skeletanmusclerange", "NA");
                }
                if(!ActofitObject.getString("protine","").equalsIgnoreCase("")) {
                    params.put("proteinrange","16-18 %");
                }else{
                    params.put("proteinrange","NA");
                }
                if(!ActofitObject.getString("metaage","").equalsIgnoreCase("")) {
                    params.put("metaagerange", "<="+age);
                    Log.e("metaage_range","<="+age);
                }else{
                    params.put("metaagerange","NA");
                }
                params.put("healthscorerange","");
                if(!ActofitObject.getString("bmr","").equalsIgnoreCase("")) {
                    params.put("bmrrange", standardBMR);
                }else{
                    params.put("bmrrange", "NA");
                }
                params.put("physiquerange", "");
                if(!ActofitObject.getString("musmass","").equalsIgnoreCase("")) {
                    params.put("musclemassrange",standardMuscleMass);
                }else{
                    params.put("musclemassrange","NA");
                }
                if(!ActofitObject.getString("bonemass","").equalsIgnoreCase("")) {
                    params.put("bonemassrange", standardBoneMass);
                }else{
                    params.put("bonemassrange", "NA");
                }
                if(!ThermometerObject.getString("data", "").equalsIgnoreCase("")) {
                    params.put("bodytemprange", "97 - 99 F");
                }else{
                    params.put("bodytemprange", "NA");
                }

                if(!BPObject.getString("systolic","").equalsIgnoreCase(""))
                    params.put("systolicrange","90-139 mmHg");
                else
                    params.put("systolicrange","NA");
                if(!BPObject.getString("diastolic","").equalsIgnoreCase(""))
                    params.put("dialosticrange", "60-89 mmHg");
                else
                    params.put("dialosticrange", "NA");
                if(!OximeterObject.getString("body_oxygen", "").equalsIgnoreCase(""))
                    params.put("pulseoximeterrange",">94%");
                else
                    params.put("pulseoximeterrange","NA");
                if(!OximeterObject.getString("pulse_rate","").equalsIgnoreCase(""))
                    params.put("pulserange", "60-100 bpm");
                else
                    params.put("pulserange", "NA");
                if(!BiosenseObject.getString("last", "").equalsIgnoreCase(""))
                    params.put("bloodsugarrange", standardGlucose);
                else
                    params.put("bloodsugarrange", "NA");
                if(!HemoglobinObject.getString("hemoglobin", "").equalsIgnoreCase(""))
                    params.put("hemoglobinrange",standarHemoglobin);
                else
                    params.put("hemoglobinrange","NA");
                params.put("heightresult", "");
                params.put("weightresult", weightResult);
                params.put("bmiresult", bmiResult);
                params.put("bmrresult", bmrResult);
                params.put("metaageresult", metaageResult);
                params.put("subcutaneousresult", subcutaneousResult);
                params.put("visceralfatresult", visceralfatResult);
                params.put("skeletonmuscleresult", skeletonmuscleResult);
                params.put("bodywaterresult", bodywaterResult);
                params.put("musclemassresult", musclemassResult);
                params.put("fatfreeweightresult", fatfreeweightResult);
                params.put("proteinresult", proteinResult);
                params.put("bodyfatresult", bodyfatResult);
                params.put("bonemassresult", bonemassResult);
                params.put("systolicresult", bloodpressureResult);
                params.put("bloodpressureresult", diastolicResult);
                params.put("oxygenresult", oxygenResult);
                params.put("pulseresult", pulseResult);
                params.put("temperatureresult", tempratureResult);
                params.put("hemoglobinresult", hemoglobinResult);
                params.put("sugarresult", sugarResult);
                return params;
            }
        };

        AndMedical_App_Global.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void readFileName(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject dataObject  = jsonObject.getJSONObject("data");
            fileName = dataObject.getString("file");
            System.out.println("fileName = " +fileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setList() {

        printDataList.add(new PrintData("Weight", TextUtils.isEmpty(ActofitObject.getString("weight", "")) ? 0 : Double.parseDouble(ActofitObject.getString("weight", "")), 8, 15, "Kg"));
        printDataList.add(new PrintData("BMI", TextUtils.isEmpty(ActofitObject.getString("bmi", "")) ? 0 : Double.parseDouble(ActofitObject.getString("bmi", "")), 18.0, 25.0, "Kg"));
        printDataList.add(new PrintData("Body fat", TextUtils.isEmpty(ActofitObject.getString("bodyfat", "")) ? 0 : Double.parseDouble(ActofitObject.getString("bodyfat", "")), 11.0, 21.0, "(%)"));
        printDataList.add(new PrintData("fat free weight", TextUtils.isEmpty(ActofitObject.getString("fatfreeweight", "")) ? 0 : Double.parseDouble(ActofitObject.getString("fatfreeweight", "")), 0, 0, ""));
        printDataList.add(new PrintData("subcutaneous fat", TextUtils.isEmpty(ActofitObject.getString("subfat", "")) ? 0 : Double.parseDouble(ActofitObject.getString("subfat", "")), 8.6, 16.7, "(%)"));
        printDataList.add(new PrintData("visceral fat", TextUtils.isEmpty(ActofitObject.getString("visfat", "")) ? 0 : Double.parseDouble(ActofitObject.getString("visfat", "")), 0, 0, " < 9"));
        printDataList.add(new PrintData("Body water", TextUtils.isEmpty(ActofitObject.getString("bodywater", "")) ? 0 : Double.parseDouble(ActofitObject.getString("bodywater", "")), 55.0, 65.0, "(%)"));
        printDataList.add(new PrintData("skeletal muscle", TextUtils.isEmpty(ActofitObject.getString("skemus", "")) ? 0 : Double.parseDouble(ActofitObject.getString("skemus", "")), 49.0, 59.0, "(%)"));
        printDataList.add(new PrintData("muscle mass", TextUtils.isEmpty(ActofitObject.getString("musmass", "")) ? 0 : Double.parseDouble(ActofitObject.getString("musmass", "")), 44, 52.4, "Kg"));
        printDataList.add(new PrintData("Bone mass", TextUtils.isEmpty(ActofitObject.getString("bonemass", "")) ? 0 : Double.parseDouble(ActofitObject.getString("bonemass", "")), 2.7, 3.1, "Kg"));
        printDataList.add(new PrintData("protein", TextUtils.isEmpty(ActofitObject.getString("protine", "")) ? 0 : Double.parseDouble(ActofitObject.getString("protine", "")), 16.0, 18.0, "(%)"));
        printDataList.add(new PrintData("BMR", TextUtils.isEmpty(ActofitObject.getString("bmr", "")) ? 0 : Double.parseDouble(ActofitObject.getString("bmr", "")), 0, 0, " > 1592.40(Kcal)"));
        printDataList.add(new PrintData("Metabolic age", TextUtils.isEmpty(ActofitObject.getString("metaage", "")) ? 0 : Double.parseDouble(ActofitObject.getString("metaage", "")), 0, 0, "< Actual Age(Yrs)"));
        printDataList.add(new PrintData("Health Score", TextUtils.isEmpty(ActofitObject.getString("helthscore", "")) ? 0 : Double.parseDouble(ActofitObject.getString("helthscore", "")), 0, 0, ""));
        lV.setAdapter(new PrintpriviewAdapter(this, R.layout.printlist_item, printDataList));

    }

    private void gettingDataObjects() {

        ActofitObject = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
        OximeterObject = getSharedPreferences(ApiUtils.PREFERENCE_PULSE, MODE_PRIVATE);
        PersonalObject = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
        ThermometerObject = getSharedPreferences(PREFERENCE_THERMOMETERDATA, MODE_PRIVATE);
        BPObject = getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
        BiosenseObject = getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
        HemoglobinObject = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
        spToken = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

    }


    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa");

        String currentTime = sdf.format(cal.getTime());
        System.out.println(sdf.format(cal.getTime()));
        return currentTime;
    }


    @OnClick({R.id.homebtn, R.id.printbtn,R.id.iv_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.homebtn:
                SharedPreferences objBiosense = getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
                SharedPreferences objBp = getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
                SharedPreferences objPulse = getSharedPreferences(ApiUtils.PREFERENCE_PULSE, MODE_PRIVATE);
                SharedPreferences objActofit = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
                SharedPreferences objNewRecord = getSharedPreferences(ApiUtils.PREFERENCE_NEWRECORD, MODE_PRIVATE);
                SharedPreferences objUrl = getSharedPreferences(ApiUtils.PREFERENCE_URL, MODE_PRIVATE);
                SharedPreferences objHemoglobin = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
                SharedPreferences objSugar = getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
                SharedPreferences objAshok = getSharedPreferences("ashok", MODE_PRIVATE);
                objBiosense.edit().clear().commit();
                objBp.edit().clear().commit();
                objPulse.edit().clear().commit();
                objActofit.edit().clear().commit();
                objNewRecord.edit().clear().commit();
                objUrl.edit().clear().commit();
                objAshok.edit().clear().commit();
                objHemoglobin.edit().clear().commit();
                objSugar.edit().clear().commit();
                ActofitObject.edit().clear().commit();
                OximeterObject.edit().clear();
                PersonalObject.edit().clear();
                ThermometerObject.edit().clear();
                BPObject.edit().clear();
                BiosenseObject.edit().clear();
                HemoglobinObject.edit().clear();
                Intent newIntent = new Intent(getApplicationContext(), OtpLoginScreen.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(newIntent);
                break;
            case R.id.printbtn:
                Toast.makeText(this, "Getting Printout", Toast.LENGTH_SHORT).show();
                EnterTextAsyc asynctask = new EnterTextAsyc();
                asynctask.execute(0);
                txt = "Please collect your result receipt";
                speakOut(txt);
                break;

            case R.id.iv_download:
                downloadFile();
                break;
        }
    }

    private void downloadFile() {

        downloadUrl = "http://45.252.190.29/api/v1/pdf/" + fileName;

        if(fileName != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)));
        }else{
            Toast.makeText(PrintPreviewActivity.this, "No Pdf file Available ", Toast.LENGTH_SHORT).show();
        }

    }

    private void openPDFFile() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){

                //permission denied
                String [] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                //show popup for permission
                requestPermissions(permissions,PERMISSION_STORAGE_CODE);


            }else{
                //permission granted

                startDownload();

            }
        }else{
            startDownload();
        }
    }

    private void startDownload() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                DownloadManager.Request.NETWORK_MOBILE );

        request.setTitle("Download");
        request.setDescription("Downloading File...");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,""+System.currentTimeMillis());

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startDownload();
                }else{
                    Toast.makeText(this, "Permission Denied..!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private int getAge(String dobString) {

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = sdf.parse(dobString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date == null)
            return 0;

        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(date);

        int year = dob.get(Calendar.YEAR);
        int month = dob.get(Calendar.MONTH);
        int day = dob.get(Calendar.DAY_OF_MONTH);

        dob.set(year, month + 1, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            tts.setSpeechRate(1);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(txt);
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut(String textToSpeech) {
        String text = textToSpeech;
//        String text = "StartActivity me aapka swagat hain kripaya next button click kre aur aage badhe";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public class EnterTextAsyc extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog untill background task is completed */
        @Override
        protected void onPreExecute() {
//            dlgShowCustom(PrintPreviewActivity.this, "Please Wait....");
            super.onPreExecute();
        }

        /* Task of EnterTextAsyc performing in the background */
        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                ptrGen.iFlushBuf();
//                String empty = printString;
                String empty = printStringNew;
                ptrGen.iAddData(Printer_GEN.FONT_LARGE_NORMAL, empty);
                iRetVal = ptrGen.iStartPrinting(1);
                Log.e("iRetVal", "" + iRetVal);
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                e.printStackTrace();
                return iRetVal;
            }
            return iRetVal;
        }

        /* This displays the status messages of EnterTextAsyc in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
//            llprog.setVisibility(View.GONE);
//            btnOk.setVisibility(View.VISIBLE);
//            edtText.setText("");
            if (iRetVal == DEVICE_NOTCONNECTED) {
                ptrHandler.obtainMessage(1, "Device not connected")
                        .sendToTarget();

            } else if (iRetVal == Printer_GEN.SUCCESS) {
                ptrHandler.obtainMessage(1, "Printing Successfull")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
                ptrHandler.obtainMessage(1, "Platen open").sendToTarget();
            } else if (iRetVal == Printer_GEN.PAPER_OUT) {
                ptrHandler.obtainMessage(1, "Paper out").sendToTarget();
            } else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
                ptrHandler.obtainMessage(1, "Printer at improper voltage")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.FAILURE) {
                ptrHandler.obtainMessage(1, "Print failed").sendToTarget();
                PrintPreviewActivity.this.recreate();
            } else if (iRetVal == Printer_GEN.PARAM_ERROR) {
                ptrHandler.obtainMessage(1, "Parameter error").sendToTarget();
            } else if (iRetVal == Printer_GEN.NO_RESPONSE) {
                ptrHandler.obtainMessage(1, "No response from Pride device")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.DEMO_VERSION) {
                ptrHandler.obtainMessage(1, "Library in demo version")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.INVALID_DEVICE_ID) {
                ptrHandler.obtainMessage(1,
                        "Connected  device is not authenticated.")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.NOT_ACTIVATED) {
                ptrHandler.obtainMessage(1, "Library not activated")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.NOT_SUPPORTED) {
                ptrHandler.obtainMessage(1, "Not Supported").sendToTarget();
            } else {
                ptrHandler.obtainMessage(1, "Unknown Response from Device")
                        .sendToTarget();
            }
            super.onPostExecute(result);
        }
    }

    /* Handler to display UI response messages */
    Handler ptrHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        TextView tvMessage = (TextView) dlgCustomdialog
                                .findViewById(R.id.tvMessage);
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
        dlgCustomdialog.setTitle("Pride Demo");
        dlgCustomdialog.setCancelable(false);
        dlgCustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgCustomdialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dlgCustomdialog.setContentView(R.layout.progressdialog);
        TextView title_tv = (TextView) dlgCustomdialog
                .findViewById(R.id.tvTitle);
        title_tv.setWidth(iWidth);
        TextView message_tv = (TextView) dlgCustomdialog
                .findViewById(R.id.tvMessage);
        message_tv.setText(Message);
        llprog = (LinearLayout) dlgCustomdialog.findViewById(R.id.llProg);
        pbProgress = (ProgressBar) dlgCustomdialog.findViewById(R.id.pbDialog);
        btnOk = (Button) dlgCustomdialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlgCustomdialog.dismiss();
            }
        });
        dlgCustomdialog.show();
    }



}
