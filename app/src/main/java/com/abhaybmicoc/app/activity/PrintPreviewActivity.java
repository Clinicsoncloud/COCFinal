package com.abhaybmicoc.app.activity;

import android.net.Uri;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.os.Handler;
import android.os.Message;
import android.app.Dialog;
import android.view.Window;
import android.os.AsyncTask;
import android.widget.Toast;
import android.app.Activity;
import android.widget.Button;
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

import org.json.JSONObject;
import org.json.JSONException;

import java.io.InputStream;
import java.io.OutputStream;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Hashtable;

import butterknife.OnClick;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.abhaybmicoc.app.utils.ApiUtils.PREFERENCE_THERMOMETERDATA;

public class PrintPreviewActivity extends Activity implements TextToSpeech.OnInitListener {
    // region Variables

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
    Button homebtn;
    @BindView(R.id.printbtn)
    Button printbtn;

    private LinearLayout llprog;
    @BindView(R.id.buttonLL)
    LinearLayout buttonLL;
    @BindView(R.id.headerLL)
    LinearLayout headerLL;

    SharedPreferences ActofitObject, OximeterObject, PersonalObject, ThermometerObject, BPObject, BiosenseObject, HemoglobinObject, spToken;

    private ImageView ivDownload;

    private int age;
    private int height;
    private int iRetVal;
    public static int iWidth;
    public static final int DEVICE_NOTCONNECTED = -100;
    private static final int PERMISSION_STORAGE_CODE = 1000;

    private Dialog dlgBarcode;
    public Dialog dlgCustomdialog;
    public static ProgressBar pbProgress;
    private ProgressDialog progressDialog;

    private double weight;
    double standardWeighRangeTo;
    double standardWeighRangeFrom;
    private double standardMetabolism;

    boolean isMale = false;
    private boolean blBleStatusBefore = false;

    private String subcutaneousFat;
    private String printString = "";
    private String printStringNew = "";
    private String fileName = "";
    private String standardWeightTo;
    private String standardWeightFrom;
    private String standarHemoglobin;
    private String standardBodyFat;
    private String standardBodyWater;
    private String standardSkeltonMuscle;
    private String standardBoneMass;
    private String standardMuscleMass;
    private String standardBMR;
    private String standardGlucose;
    private String currentDate;
    private String currentTime;
    private String txt = "";
    private String parsedate1 = null;
    private String bmiResult;
    private String bmrResult;
    private String sugarResult;
    private String pulseResult;
    private String oxygenResult;
    private String weightResult;
    private String proteinResult;
    private String metaageResult;
    private String bodyfatResult;
    private String bonemassResult;
    private String bodywaterResult;
    private String diastolicResult;
    private String TEMPERATUREResult;
    private String musclemassResult;
    private String downloadUrl = "";
    private String hemoglobinResult;
    private String visceralfatResult;
    private String subcutaneousResult;
    private String bloodpressureResult;
    private String standardVisceralFat;
    private String standardWeightRange;
    private String skeletonmuscleResult;
    private String fatfreeweightResult = "";

    public static Printer_GEN ptrGen;
    private TextToSpeech textToSpeech;
    private AndMedical_App_Global mGP = null;
    public static BluetoothDevice mBDevice = null;
    public static BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();

    List<PrintDataOld> printDataList = new ArrayList<>();
    List<PrintData> printDataListNew = new ArrayList<>();
    private Hashtable<String, String> mhtDeviceInfo = new Hashtable<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.printpreview);
        ButterKnife.bind(this);

        textToSpeech = new TextToSpeech(this, this);

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

        //reinitialization of the textToSpeech engine for voice command
        textToSpeech = new TextToSpeech(this, this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        //close the textToSpeech engine to avoide runtime exception
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

        if (PersonalObject.getString("gender", "").equalsIgnoreCase("male")) {
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
            getTEMPERATUREResult();

        } else {
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
            getTEMPERATUREResult();
        }

    }

    private void getDiastolicResult() {
        if (!BPObject.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(BPObject.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, "")) > 89) {
                diastolicResult = "High";
            } else if (Double.parseDouble(BPObject.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, "")) <= 89 && Double.parseDouble(BPObject.getString("diastolic", "")) >= 60) {
                diastolicResult = "standard";
            } else {
                diastolicResult = "Low";
            }
        } else {
            diastolicResult = "NA";
        }
    }

    private void getMetaAgeResult() {
        if (!ActofitObject.getString(Constant.Fields.META_AGE, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.META_AGE, "")) <= age) {
                metaageResult = "Standard";
            } else {
                metaageResult = "Not upto standard";
            }
        } else {
            metaageResult = "NA";
        }
    }

    private void getSubcutaneousResult() {
        if (!ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "")) > 26.7) {
                subcutaneousResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "")) <= 26.7 && Double.parseDouble(ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "")) >= 18.5) {
                subcutaneousResult = "standard";
            } else {
                subcutaneousResult = "Low";
            }
        } else {
            subcutaneousResult = "NA";
        }
    }

    private void getHemoglobinResult() {
        if (!HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "")) > 15.1) {
                hemoglobinResult = "High";
            } else if (Double.parseDouble(HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "")) <= 15.1 && Double.parseDouble(HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "")) >= 12.1) {
                hemoglobinResult = "standard";
            } else {
                hemoglobinResult = "Low";
            }
        } else {
            hemoglobinResult = "NA";
        }
    }

    private void getBoneMassResult() {

        if (!ActofitObject.getString(Constant.Fields.BONE_MASS, "").equalsIgnoreCase("")) {
            if (weight > 60) {
                standardBoneMass = "2.3 - 2.7kg";
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) > 2.7) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) <= 2.7 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) >= 2.3) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            } else if (weight <= 60 && weight >= 45) {
                standardBoneMass = "2.0-2.4kg";
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) > 2.4) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) <= 2.4 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) >= 2.0) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            } else if (weight < 45) {
                standardBoneMass = "1.6 - 2.0kg";
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) > 2.0) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) <= 2.0 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) >= 1.6) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            }
        } else {
            bonemassResult = "NA";
        }

    }

    private void getMuscleMassResult() {
        if (!ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "").equalsIgnoreCase("")) {

            if (height > 170) {
                standardMuscleMass = "49.4-59.5kg";
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) > 59.5) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) <= 59.5 && Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) >= 49.4) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            } else if (height <= 170 && height >= 160) {
                standardMuscleMass = "44-52.4kg";
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) > 52.4) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) <= 52.4 && Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) >= 44) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            } else if (height < 160) {
                standardMuscleMass = "38.5-46.5kg";
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) > 46.5) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) <= 46.5 && Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) >= 38.5) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            }
        } else {
            musclemassResult = "NA";
        }
    }

    private void getSkeletalMuscle() {
        if (!ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "")) > 50) {
                skeletonmuscleResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "")) <= 50 && Double.parseDouble(ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "")) >= 40) {
                skeletonmuscleResult = "standard";
            } else {
                skeletonmuscleResult = "Low";
            }
        } else {
            skeletonmuscleResult = "NA";
        }
    }

    private void getBodWaterResult() {
        if (!ActofitObject.getString(Constant.Fields.BODY_WATER, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_WATER, "")) > 60) {
                bodywaterResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_WATER, "")) <= 60 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_WATER, "")) >= 45) {
                bodywaterResult = "standard";
            } else {
                bodywaterResult = "Low";
            }
        } else {
            bodywaterResult = "NA";
        }
    }

    private void getTEMPERATUREResult() {
        if (!ThermometerObject.getString(Constant.Fields.TEMPERATURE, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ThermometerObject.getString(Constant.Fields.TEMPERATURE, "")) > 99) {
                TEMPERATUREResult = "High";
            } else if (Double.parseDouble(ThermometerObject.getString(Constant.Fields.TEMPERATURE, "")) <= 99 && Double.parseDouble(ThermometerObject.getString(Constant.Fields.TEMPERATURE, "")) >= 97) {
                TEMPERATUREResult = "standard";
            } else {
                TEMPERATUREResult = "Low";
            }
        } else {
            TEMPERATUREResult = "NA";
        }
    }

    private void getPulseResult() {
        if (!OximeterObject.getString(Constant.Fields.PULSE_RATE, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(OximeterObject.getString(Constant.Fields.PULSE_RATE, "")) > 100) {
                pulseResult = "High";
            } else if (Double.parseDouble(OximeterObject.getString(Constant.Fields.PULSE_RATE, "")) <= 100 && Double.parseDouble(OximeterObject.getString(Constant.Fields.PULSE_RATE, "")) >= 60) {
                pulseResult = "standard";
            } else {
                pulseResult = "Low";
            }
        } else {
            pulseResult = "NA";
        }
    }

    private void getOximeterResult() {
        if (!OximeterObject.getString(Constant.Fields.BLOOD_OXYGEN, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(OximeterObject.getString("body_oxygen", "")) >= 94) {
                oxygenResult = "Standard";
            } else if (Double.parseDouble(OximeterObject.getString("body_oxygen", "")) < 94) {
                oxygenResult = "Low";
            }
        } else {
            oxygenResult = "NA";
        }
    }

    private void getBloodPressureResult() {
        if (!BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "")) > 139) {
                bloodpressureResult = "High";
            } else if (Double.parseDouble(BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "")) <= 139 && Double.parseDouble(BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "")) >= 90) {
                bloodpressureResult = "standard";
            } else {
                bloodpressureResult = "Low";
            }
        } else {
            bloodpressureResult = "NA";
        }
    }


    private void getMaleHemoglobinResult() {
        if (!HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "")) > 17.2) {
                hemoglobinResult = "High";
            } else if (Double.parseDouble(HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "")) <= 17.2 && Double.parseDouble(HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "")) >= 13.8) {
                hemoglobinResult = "standard";
            } else {
                hemoglobinResult = "Low";
            }
        } else {
            hemoglobinResult = "NA";
        }
    }

    private void getMaleGlucoseResult() {

        if (!BiosenseObject.getString(Constant.Fields.SUGAR, "").equalsIgnoreCase("")) {

            if (BiosenseObject.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Fasting (Before Meal)")) {
                standardGlucose = "70-100mg/dl(Fasting)";
                if (Double.parseDouble(BiosenseObject.getString(Constant.Fields.SUGAR, "")) > 100) {
                    sugarResult = "High";
                } else if (Double.parseDouble(BiosenseObject.getString(Constant.Fields.SUGAR, "")) <= 100 && Double.parseDouble(BiosenseObject.getString(Constant.Fields.SUGAR, "")) >= 70) {
                    sugarResult = "standard";
                } else {
                    sugarResult = "Low";
                }
            } else if (BiosenseObject.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Post Prandial (After Meal)")) {
                standardGlucose = "70-140 mg/dl(Post Meal)";
                if (Double.parseDouble(BiosenseObject.getString(Constant.Fields.SUGAR, "")) > 140) {
                    sugarResult = "High";
                } else if (Double.parseDouble(BiosenseObject.getString(Constant.Fields.SUGAR, "")) <= 140 && Double.parseDouble(BiosenseObject.getString(Constant.Fields.SUGAR, "")) >= 70) {
                    sugarResult = "standard";
                } else {
                    sugarResult = "Low";
                }
            } else if (BiosenseObject.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Random (Not Sure)")) {
                standardGlucose = "79-160 mg/dl(Random)";
                if (Double.parseDouble(BiosenseObject.getString(Constant.Fields.SUGAR, "")) > 160) {
                    sugarResult = "High";
                } else if (Double.parseDouble(BiosenseObject.getString(Constant.Fields.SUGAR, "")) <= 160 && Double.parseDouble(BiosenseObject.getString(Constant.Fields.SUGAR, "")) >= 79) {
                    sugarResult = "standard";
                } else {
                    sugarResult = "Low";
                }
            }
        } else {
            sugarResult = "NA";
        }

    }

    private void getMaleBMRResult() {
        if (!ActofitObject.getString(Constant.Fields.BMR, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BMR, "")) >= standardMetabolism) {
                bmrResult = "High";
            } else {
                bmrResult = "Not upto Standard";
            }
        } else {
            bmrResult = "NA";
        }

    }

    private void getMaleProteinResult() {
        if (!ActofitObject.getString(Constant.Fields.PROTEIN, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.PROTEIN, "")) > 18) {
                proteinResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.PROTEIN, "")) <= 18 && Double.parseDouble(ActofitObject.getString(Constant.Fields.PROTEIN, "")) >= 16) {
                proteinResult = "standard";
            } else {
                proteinResult = "Low";
            }
        } else {
            proteinResult = "NA";
        }

    }

    private void getMaleBoneMassResult() {
        if (!ActofitObject.getString(Constant.Fields.BONE_MASS, "").equalsIgnoreCase("")) {
            if (weight > 75) {
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) > 3.4) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) <= 3.4 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) >= 3.0) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            } else if (weight <= 75 && weight >= 60) {
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) > 3.1) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) <= 3.1 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) >= 2.7) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            } else if (weight < 60) {
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) > 2.7) {
                    bonemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) <= 2.7 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) >= 2.3) {
                    bonemassResult = "standard";
                } else {
                    bonemassResult = "Low";
                }
            }
        } else {
            bonemassResult = "NA";
        }

    }


    private void getMaleMuscleMassResult() {
        if (!ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "").equalsIgnoreCase("")) {

            if (height > 170) {
                standardMuscleMass = "49.4-59.5kg";
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) > 59.5) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) <= 59.5 && Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) >= 49.4) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            } else if (height <= 170 && height >= 160) {
                standardMuscleMass = "44-52.4kg";
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) > 52.4) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) <= 52.4 && Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) >= 44) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            } else if (height < 160) {
                standardMuscleMass = "38.5-46.5kg";
                if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) > 46.5) {
                    musclemassResult = "High";
                } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) <= 46.5 && Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) >= 38.5) {
                    musclemassResult = "standard";
                } else {
                    musclemassResult = "Low";
                }
            }
        } else {
            musclemassResult = "NA";
        }
    }

    private void getMaleSkeletalMuscle() {
        if (!ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "")) > 59) {
                skeletonmuscleResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "")) <= 59 && Double.parseDouble(ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "")) >= 49) {
                skeletonmuscleResult = "standard";
            } else {
                skeletonmuscleResult = "Low";
            }
        } else {
            skeletonmuscleResult = "NA";
        }
    }

    private void getMaleBodyWaterResult() {
        if (!ActofitObject.getString(Constant.Fields.BODY_WATER, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_WATER, "")) > 65) {
                bodywaterResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_WATER, "")) <= 65 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_WATER, "")) >= 55) {
                bodywaterResult = "standard";
            } else {
                bodywaterResult = "Low";
            }
        } else {
            bodywaterResult = "NA";
        }
    }

    private void getMaleVisceralFatResult() {
        if (!ActofitObject.getString(Constant.Fields.VISCERAL_FAT, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.VISCERAL_FAT, "")) > 14) {
                visceralfatResult = "Seriously High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.VISCERAL_FAT, "")) > 9) {
                visceralfatResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.VISCERAL_FAT, "")) <= 9) {
                visceralfatResult = "standard";
            }
        } else {
            visceralfatResult = "NA";
        }
    }

    private void getMaleSubcutaneousResult() {
        if (!ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "")) > 16.7) {
                subcutaneousResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "")) <= 16.7 && Double.parseDouble(ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "")) >= 8.6) {
                subcutaneousResult = "standard";
            } else {
                subcutaneousResult = "Low";
            }
        } else {
            subcutaneousResult = "NA";
        }
    }

    private void getMaleBodyFatResult() {
        if (!ActofitObject.getString(Constant.Fields.BODY_FAT, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) > 26) {
                bodyfatResult = "Seriously High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) <= 26 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) >= 22) {
                bodyfatResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) <= 21 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) >= 11) {
                bodyfatResult = "Standard";
            } else {
                bodyfatResult = "Low";
            }
        } else {
            bodyfatResult = "NA";
        }
    }

    private void getMaleBMIResult() {
        if (!ActofitObject.getString(Constant.Fields.BMI, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BMI, "")) > 25) {
                bmiResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BMI, "")) <= 25 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BMI, "")) >= 18.5) {
                bmiResult = "standard";
            } else {
                bmiResult = "Low";
            }
        } else {
            bmiResult = "NA";
        }
    }

    private void getMaleWeightResult() {
        if (!ActofitObject.getString(Constant.Fields.WEIGHT, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.WEIGHT, "")) > standardWeighRangeTo) {
                weightResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.WEIGHT, "")) <= standardWeighRangeTo && Double.parseDouble(ActofitObject.getString(Constant.Fields.WEIGHT, "")) >= standardWeighRangeFrom) {
                weightResult = "standard";
            } else {
                weightResult = "Low";
            }
        } else {
            weightResult = "NA";
        }
    }

    private void getBodyFatResult() {
        if (!ActofitObject.getString(Constant.Fields.BODY_FAT, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) > 36) {
                bodyfatResult = "Seriously High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) <= 36 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) >= 31) {
                bodyfatResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) <= 30 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) >= 21) {
                bodyfatResult = "Standard";
            } else {
                bodyfatResult = "Low";
            }
        } else {
            bodyfatResult = "NA";
        }
    }

    private void getBMIResult() {
        if (!ActofitObject.getString(Constant.Fields.BMI, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BMI, "")) > 25) {
                bmiResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.BMI, "")) <= 25 && Double.parseDouble(ActofitObject.getString(Constant.Fields.BMI, "")) >= 18.5) {
                bmiResult = "standard";
            } else {
                bmiResult = "Low";
            }
        } else {
            bmiResult = "NA";
        }
    }

    private void getWeightResult() {
        if (!ActofitObject.getString(Constant.Fields.WEIGHT, "").equalsIgnoreCase("")) {
            if (Double.parseDouble(ActofitObject.getString(Constant.Fields.WEIGHT, "")) > standardWeighRangeTo) {
                weightResult = "High";
            } else if (Double.parseDouble(ActofitObject.getString(Constant.Fields.WEIGHT, "")) <= standardWeighRangeTo && Double.parseDouble(ActofitObject.getString(Constant.Fields.WEIGHT, "")) >= standardWeighRangeFrom) {
                weightResult = "standard";
            } else {
                weightResult = "Low";
            }
        } else {
            weightResult = "NA";
        }

    }

    private void setupUI() {
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


        if (ActofitObject.getString(Constant.Fields.WEIGHT, "").equalsIgnoreCase("")) {
            weight = 0.0;
        } else {
            weight = Double.parseDouble(ActofitObject.getString(Constant.Fields.WEIGHT, ""));
        }


        if (!ActofitObject.getString(Constant.Fields.HEIGHT, "").equalsIgnoreCase("")) {
            height = Integer.parseInt(ActofitObject.getString(Constant.Fields.HEIGHT, ""));
        } else {
            height = 0;
        }


        double standardWeightMen = ((height - 80) * 0.7);
        standardWeightMen = Double.parseDouble(new DecimalFormat("#.##").format(standardWeightMen));
        double standardWeightFemale = (((height * 1.37) - 110) * 0.45);
        standardWeightFemale = Double.parseDouble(new DecimalFormat("#.##").format(standardWeightFemale));


        glucoseRange();

        if (PersonalObject.getString(Constant.Fields.GENDER, "").equals("male")) {
            maleRange(standardWeightMen);
        } else {
            femaleRange(standardWeightFemale);
        }

    }

    //creating region on glucoseRange
    private void glucoseRange() {

        if (BiosenseObject.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Fasting (Before Meal)")) {
            standardGlucose = "70-100mg/dl(Fasting)";
        } else if (BiosenseObject.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Post Prandial (After Meal)")) {
            standardGlucose = "70-140 mg/dl(Post Meal)";
        } else if (BiosenseObject.getString(Constant.Fields.GLUCOSE_TYPE, "").equals("Random (Not Sure)")) {
            standardGlucose = "79-160 mg/dl(Random)";
        } else {
            standardGlucose = "";
        }

    }

    private void femaleRange(double standardWeightFemale) {
        standardWeighRangeFrom = (0.90 * standardWeightFemale);
        standardWeighRangeFrom = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeFrom));
        standardWeighRangeTo = (1.09 * standardWeightFemale);
        standardWeighRangeTo = Double.parseDouble(new DecimalFormat("#.##").format(standardWeighRangeTo));

        standardWeightRange = String.valueOf(standardWeighRangeFrom) + "-" + String.valueOf(standardWeighRangeTo);
        Log.e("standardWeightRange", "" + standardWeightRange);

        if (height > 160) {
            standardMuscleMass = "36.4-42.5kg";
        } else if (height <= 160 && height >= 150) {
            standardMuscleMass = "32.9-37.5kg";
        } else if (height < 150) {
            standardMuscleMass = "29.1-34.7kg";
        } else {
            standardMuscleMass = "";
        }

        if (weight > 60) {
            standardBoneMass = "2.3 - 2.7kg";
        } else if (weight <= 60 && weight >= 45) {
            standardBoneMass = "2.0-2.4kg";
        } else if (weight < 45) {
            standardBoneMass = "1.6 - 2.0kg";
        } else {
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
        standardBMR = " > =" + String.valueOf(standardMetabolism) + "kcal";
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

        standardWeightRange = String.valueOf(standardWeighRangeFrom) + "-" + String.valueOf(standardWeighRangeTo);
        ;
        Log.e("standardWeightRange", "" + standardWeightRange);

        if (height > 170) {
            standardMuscleMass = "49.4-59.5kg";
        } else if (height <= 170 && height >= 160) {
            standardMuscleMass = "44-52.4kg";
        } else if (height < 160) {
            standardMuscleMass = "38.5-46.5kg";
        } else {
            standardMuscleMass = "";
        }

        if (weight > 75) {
            standardBoneMass = "3.0-3.4kg";
        } else if (weight <= 75 && weight >= 60) {
            standardBoneMass = "2.7-3.1kg";
        } else if (weight < 60) {
            standardBoneMass = "2.3-2.7kg";
        } else {
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
        standardBMR = " > =" + String.valueOf(standardMetabolism) + "kcal";
        standardVisceralFat = "< = 9";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setStaticData() {

        nameTV.setText("Name :" + PersonalObject.getString("name", ""));
        dobTV.setText("DOB :" + PersonalObject.getString("dob", ""));
        heightTV.setText("Height :" + ActofitObject.getString("height", ""));
        genderTV.setText("Gender :" + PersonalObject.getString("gender", ""));

    }

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

        if (!BiosenseObject.getString(Constant.Fields.SUGAR, "").equalsIgnoreCase("") && !HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "").equalsIgnoreCase("")) {

            printStringNew = "" + "  " + "Clinics On Cloud" + "" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Name :" + PersonalObject.getString(Constant.Fields.NAME, "") + "\n" +
                    "Age :" + age + "   " + "Gender :" + PersonalObject.getString(Constant.Fields.GENDER, "") + "\n" +
                    "" + currentDate + "  " + "" + currentTime + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Height :" + ActofitObject.getString(Constant.Fields.HEIGHT, "") + "CM" + "\n" +
                    "Weight :" + ActofitObject.getString(Constant.Fields.WEIGHT, "") + "Kg" + "\n" +
                    "[Normal Range]:" + "\n"
                    + standardWeighRangeFrom + "-" + standardWeighRangeTo + "kg" + "\n" +
                    "BMI :" + "" + ActofitObject.getString(Constant.Fields.BMI, "") + "\n" +
                    "[Normal Range]:" + "18.5 - 25" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Body Fat :" + ActofitObject.getString(Constant.Fields.BODY_FAT, "") + "%" + "\n" +
                    "[Normal Range] :" + standardBodyFat + "\n\n" +
                    "Fat Free Weight :" + ActofitObject.getString(Constant.Fields.FAT_FREE_WEIGHT, "") + "Kg" + "\n\n" +
                    "Subcutaneous Fat :" + ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "") + "%" + "\n" +
                    "[Normal Range]:" + subcutaneousFat + "\n\n" +
                    "Visceral Fat :" + ActofitObject.getString(Constant.Fields.VISCERAL_FAT, "") + "\n" +
                    "[Normal Range]:" + "<=9" + "\n\n" +
                    "Body Water : " + ActofitObject.getString(Constant.Fields.BODY_WATER, "") + "\n" +
                    "[Normal Range]:" +
                    standardBodyWater + "\n\n" +
                    "Skeletal Muscle :" + ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "") + "\n" +
                    "[Normal Range]:" +
                    standardSkeltonMuscle + "\n\n" +
                    "Muscle Mass :" + ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "") + "\n" +
                    "[Normal Range]:" + standardMuscleMass + "\n\n" +
                    "Bone Mass :" + ActofitObject.getString(Constant.Fields.BONE_MASS, "") + "\n" +
                    "[Normal Range]:" + standardBoneMass + "\n\n" +
                    "Protein :" + ActofitObject.getString(Constant.Fields.PROTEIN, "") + "\n" +
                    "[Normal Range]:" + "16-18(%)" + "\n\n" +
                    "BMR :" + ActofitObject.getString(Constant.Fields.BMR, "") + "\n" +
                    "[Normal Range]:" + "\n"
                    + "> = " + standardMetabolism + "Kcal" + "\n\n" +
                    "Physique:" + ActofitObject.getString(Constant.Fields.PHYSIQUE, "") + "\n\n" +
                    "Meta Age :" + ActofitObject.getString(Constant.Fields.META_AGE, "") + "yrs" + "\n\n" +
                    "Health Score :" + ActofitObject.getString(Constant.Fields.HEALTH_SCORE, "") + "\n\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Glucose :" + BiosenseObject.getString(Constant.Fields.SUGAR, "") + "mg/dl" + "\n" +
                    "[Normal Range]:" + "\n" +
                    standardGlucose + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Hemoglobin :" + HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "") + " g/dl" + "\n" +
                    "[Normal Range]:" + "\n" +
                    standarHemoglobin + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Pressure :" + "\n" +
                    "Systolic :" + BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "") + "mmHg" + "\n" +
                    "Diastolic :" + BPObject.getString("diastolic", "") + "mmHg" + "\n" +
                    "[Normal Range]:" + "\n" +
                    "systolic :" + "90-139mmHg" + "\n" +
                    "Diastolic :" + "60-89mmHg" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Oxygen :" + OximeterObject.getString("body_oxygen", "") + " %" + "\n" +
                    "[Normal Range]:" + ">94%" + "\n" +
                    "Pulse Rate: " + OximeterObject.getString(Constant.Fields.PULSE_RATE, "") + " bpm" + "\n" +
                    "[Normal Range]:" + "60-100bpm" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Temperature :" + ThermometerObject.getString(Constant.Fields.TEMPERATURE, "") + "F" + "\n" +
                    "[Normal Range]:" + "97-99F " + "\n" +
                    "" + "-----------------------" + "\n" +
                    "       " + "Thank You" + "\n" +
                    "   " + "Above results are" + "\n" +
                    "      " + " indicative" + "\n" +
                    "  " + "figure,don't follow it" + "\n" +
                    "   " + "without consulting a" + "\n" +
                    "        " + "doctor" + "\n\n\n\n\n\n\n";

        } else if (!BiosenseObject.getString(Constant.Fields.SUGAR, "").equalsIgnoreCase("") && HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "").equalsIgnoreCase("")) {
            printStringNew = "" + "  " + "Clinics On Cloud" + "" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Name :" + PersonalObject.getString("name", "") + "\n" +
                    "Age :" + age + "   " + "Gender :" + PersonalObject.getString(Constant.Fields.GENDER, "") + "\n" +
                    "" + currentDate + "  " + "" + currentTime + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Height :" + ActofitObject.getString(Constant.Fields.HEIGHT, "") + "CM" + "\n" +
                    "Weight :" + ActofitObject.getString(Constant.Fields.WEIGHT, "") + "Kg" + "\n" +
                    "[Normal Range]:" + "\n"
                    + standardWeighRangeFrom + "-" + standardWeighRangeTo + "kg" + "\n" +
                    "BMI :" + "" + ActofitObject.getString(Constant.Fields.BMI, "") + "\n" +
                    "[Normal Range]:" + "18.5 - 25" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Body Fat :" + ActofitObject.getString(Constant.Fields.BODY_FAT, "") + "%" + "\n" +
                    "[Normal Range] :" + standardBodyFat + "\n\n" +
                    "Fat Free Weight :" + ActofitObject.getString(Constant.Fields.FAT_FREE_WEIGHT, "") + "Kg" + "\n\n" +
                    "Subcutaneous Fat :" + ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "") + "%" + "\n" +
                    "[Normal Range]:" + subcutaneousFat + "\n\n" +
                    "Visceral Fat :" + ActofitObject.getString(Constant.Fields.VISCERAL_FAT, "") + "\n" +
                    "[Normal Range]:" + "<=9" + "\n\n" +
                    "Body Water : " + ActofitObject.getString(Constant.Fields.BODY_WATER, "") + "\n" +
                    "[Normal Range]:" +
                    standardBodyWater + "\n\n" +
                    "Skeletal Muscle :" + ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "") + "\n" +
                    "[Normal Range]:" +
                    standardSkeltonMuscle + "\n\n" +
                    "Muscle Mass :" + ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "") + "\n" +
                    "[Normal Range]:" + standardMuscleMass + "\n\n" +
                    "Bone Mass :" + ActofitObject.getString(Constant.Fields.BONE_MASS, "") + "\n" +
                    "[Normal Range]:" + standardBoneMass + "\n\n" +
                    "Protein :" + ActofitObject.getString(Constant.Fields.PROTEIN, "") + "\n" +
                    "[Normal Range]:" + "16-18(%)" + "\n\n" +
                    "BMR :" + ActofitObject.getString(Constant.Fields.BMR, "") + "\n" +
                    "[Normal Range]:" + "\n"
                    + "> = " + standardMetabolism + "Kcal" + "\n\n" +
                    "Physique:" + ActofitObject.getString(Constant.Fields.PHYSIQUE, "") + "\n\n" +
                    "Meta Age :" + ActofitObject.getString(Constant.Fields.META_AGE, "") + "yrs" + "\n\n" +
                    "Health Score :" + ActofitObject.getString(Constant.Fields.HEALTH_SCORE, "") + "\n\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Glucose :" + BiosenseObject.getString(Constant.Fields.SUGAR, "") + "mg/dl" + "\n" +
                    "[Normal Range]:" + "\n" +
                    standardGlucose + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Hemoglobin :" + "NA" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Pressure :" + "\n" +
                    "Systolic :" + BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "") + "mmHg" + "\n" +
                    "Diastolic :" + BPObject.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, "") + "mmHg" + "\n" +
                    "[Normal Range]:" + "\n" +
                    "systolic :" + "90-139mmHg" + "\n" +
                    "Diastolic :" + "60-89mmHg" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Oxygen :" + OximeterObject.getString(Constant.Fields.BLOOD_OXYGEN, "") + " %" + "\n" +
                    "[Normal Range]:" + ">94%" + "\n" +
                    "Pulse Rate: " + OximeterObject.getString(Constant.Fields.PULSE_RATE, "") + " bpm" + "\n" +
                    "[Normal Range]:" + "60-100bpm" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Temperature :" + ThermometerObject.getString(Constant.Fields.TEMPERATURE, "") + "F" + "\n" +
                    "[Normal Range]:" + "97-99F " + "\n" +
                    "" + "-----------------------" + "\n" +
                    "       " + "Thank You" + "\n" +
                    "   " + "Above results are" + "\n" +
                    "      " + " indicative" + "\n" +
                    "  " + "figure,don't follow it" + "\n" +
                    "   " + "without consulting a" + "\n" +
                    "        " + "doctor" + "\n\n\n\n\n\n\n";
        } else if (BiosenseObject.getString(Constant.Fields.SUGAR, "").equalsIgnoreCase("") && !HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "").equalsIgnoreCase("")) {
            printStringNew = "" + "  " + "Clinics On Cloud" + "" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Name :" + PersonalObject.getString("name", "") + "\n" +
                    "Age :" + age + "   " + "Gender :" + PersonalObject.getString("gender", "") + "\n" +
                    "" + currentDate + "  " + "" + currentTime + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Height :" + ActofitObject.getString("height", "") + "CM" + "\n" +
                    "Weight :" + ActofitObject.getString(Constant.Fields.WEIGHT, "") + "Kg" + "\n" +
                    "[Normal Range]:" + "\n"
                    + standardWeighRangeFrom + "-" + standardWeighRangeTo + "kg" + "\n" +
                    "BMI :" + "" + ActofitObject.getString(Constant.Fields.BMI, "") + "\n" +
                    "[Normal Range]:" + "18.5 - 25" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Body Fat :" + ActofitObject.getString(Constant.Fields.BODY_FAT, "") + "%" + "\n" +
                    "[Normal Range] :" + standardBodyFat + "\n\n" +
                    "Fat Free Weight :" + ActofitObject.getString(Constant.Fields.FAT_FREE_WEIGHT, "") + "Kg" + "\n\n" +
                    "Subcutaneous Fat :" + ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "") + "%" + "\n" +
                    "[Normal Range]:" + subcutaneousFat + "\n\n" +
                    "Visceral Fat :" + ActofitObject.getString(Constant.Fields.VISCERAL_FAT, "") + "\n" +
                    "[Normal Range]:" + "<=9" + "\n\n" +
                    "Body Water : " + ActofitObject.getString(Constant.Fields.BODY_WATER, "") + "\n" +
                    "[Normal Range]:" +
                    standardBodyWater + "\n\n" +
                    "Skeletal Muscle :" + ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "") + "\n" +
                    "[Normal Range]:" +
                    standardSkeltonMuscle + "\n\n" +
                    "Muscle Mass :" + ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "") + "\n" +
                    "[Normal Range]:" + standardMuscleMass + "\n\n" +
                    "Bone Mass :" + ActofitObject.getString(Constant.Fields.BONE_MASS, "") + "\n" +
                    "[Normal Range]:" + standardBoneMass + "\n\n" +
                    "Protein :" + ActofitObject.getString(Constant.Fields.PROTEIN, "") + "\n" +
                    "[Normal Range]:" + "16-18(%)" + "\n\n" +
                    "BMR :" + ActofitObject.getString(Constant.Fields.BMR, "") + "\n" +
                    "[Normal Range]:" + "\n"
                    + "> = " + standardMetabolism + "Kcal" + "\n\n" +
                    "Physique:" + ActofitObject.getString(Constant.Fields.PHYSIQUE, "") + "\n\n" +
                    "Meta Age :" + ActofitObject.getString(Constant.Fields.META_AGE, "") + "yrs" + "\n\n" +
                    "Health Score :" + ActofitObject.getString(Constant.Fields.HEALTH_SCORE, "") + "\n\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Glucose :" + "NA" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Hemoglobin :" + HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "") + " g/dl" + "\n" +
                    "[Normal Range]:" + "\n" +
                    standarHemoglobin + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Pressure :" + "\n" +
                    "Systolic :" + BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "") + "mmHg" + "\n" +
                    "Diastolic :" + BPObject.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, "") + "mmHg" + "\n" +
                    "[Normal Range]:" + "\n" +
                    "systolic :" + "90-139mmHg" + "\n" +
                    "Diastolic :" + "60-89mmHg" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Oxygen :" + OximeterObject.getString(Constant.Fields.BLOOD_OXYGEN, "") + " %" + "\n" +
                    "[Normal Range]:" + ">94%" + "\n" +
                    "Pulse Rate: " + OximeterObject.getString(Constant.Fields.PULSE_RATE, "") + " bpm" + "\n" +
                    "[Normal Range]:" + "60-100bpm" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Temperature :" + ThermometerObject.getString(Constant.Fields.TEMPERATURE, "") + "F" + "\n" +
                    "[Normal Range]:" + "97-99F " + "\n" +
                    "" + "-----------------------" + "\n" +
                    "       " + "Thank You" + "\n" +
                    "   " + "Above results are" + "\n" +
                    "      " + " indicative" + "\n" +
                    "  " + "figure,don't follow it" + "\n" +
                    "   " + "without consulting a" + "\n" +
                    "        " + "doctor" + "\n\n\n\n\n\n\n";
        } else {

            printStringNew = "" + "  " + "Clinics On Cloud" + "" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Name :" + PersonalObject.getString(Constant.Fields.NAME, "") + "\n" +
                    "Age :" + age + "   " + "Gender :" + PersonalObject.getString(Constant.Fields.GENDER, "") + "\n" +
                    "" + currentDate + "  " + "" + currentTime + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Height :" + ActofitObject.getString(Constant.Fields.HEIGHT, "") + "CM" + "\n" +
                    "Weight :" + ActofitObject.getString(Constant.Fields.WEIGHT, "") + "Kg" + "\n" +
                    "[Normal Range]:" + "\n"
                    + standardWeighRangeFrom + "-" + standardWeighRangeTo + "kg" + "\n" +
                    "BMI :" + "" + ActofitObject.getString(Constant.Fields.BMI, "") + "\n" +
                    "[Normal Range]:" + "18.5 - 25" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Body Fat :" + ActofitObject.getString(Constant.Fields.BODY_FAT, "") + "%" + "\n" +
                    "[Normal Range] :" + standardBodyFat + "\n\n" +
                    "Fat Free Weight :" + ActofitObject.getString(Constant.Fields.FAT_FREE_WEIGHT, "") + "Kg" + "\n\n" +
                    "Subcutaneous Fat :" + ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "") + "%" + "\n" +
                    "[Normal Range]:" + subcutaneousFat + "\n\n" +
                    "Visceral Fat :" + ActofitObject.getString(Constant.Fields.VISCERAL_FAT, "") + "\n" +
                    "[Normal Range]:" + "<=9" + "\n\n" +
                    "Body Water : " + ActofitObject.getString(Constant.Fields.BODY_WATER, "") + "\n" +
                    "[Normal Range]:" +
                    standardBodyWater + "\n\n" +
                    "Skeletal Muscle :" + ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "") + "\n" +
                    "[Normal Range]:" +
                    standardSkeltonMuscle + "\n\n" +
                    "Muscle Mass :" + ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "") + "\n" +
                    "[Normal Range]:" + standardMuscleMass + "\n\n" +
                    "Bone Mass :" + ActofitObject.getString(Constant.Fields.BONE_MASS, "") + "\n" +
                    "[Normal Range]:" + standardBoneMass + "\n\n" +
                    "Protein :" + ActofitObject.getString(Constant.Fields.PROTEIN, "") + "\n" +
                    "[Normal Range]:" + "16-18(%)" + "\n\n" +
                    "BMR :" + ActofitObject.getString(Constant.Fields.BMR, "") + "\n" +
                    "[Normal Range]:" + "\n"
                    + "> = " + standardMetabolism + "Kcal" + "\n\n" +
                    "Physique:" + ActofitObject.getString(Constant.Fields.PHYSIQUE, "") + "\n\n" +
                    "Meta Age :" + ActofitObject.getString(Constant.Fields.META_AGE, "") + "yrs" + "\n\n" +
                    "Health Score :" + ActofitObject.getString(Constant.Fields.HEALTH_SCORE, "") + "\n\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Glucose :" + "NA" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Hemoglobin :" + "NA" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Pressure :" + "\n" +
                    "Systolic :" + BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "") + "mmHg" + "\n" +
                    "Diastolic :" + BPObject.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, "") + "mmHg" + "\n" +
                    "[Normal Range]:" + "\n" +
                    "systolic :" + "90-139mmHg" + "\n" +
                    "Diastolic :" + "60-89mmHg" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Blood Oxygen :" + OximeterObject.getString(Constant.Fields.BLOOD_OXYGEN, "") + " %" + "\n" +
                    "[Normal Range]:" + ">94%" + "\n" +
                    "Pulse Rate: " + OximeterObject.getString(Constant.Fields.PULSE_RATE, "") + " bpm" + "\n" +
                    "[Normal Range]:" + "60-100bpm" + "\n" +
                    "" + "-----------------------" + "\n" +
                    "Temperature :" + ThermometerObject.getString(Constant.Fields.TEMPERATURE, "") + "F" + "\n" +
                    "[Normal Range]:" + "97-99F " + "\n" +
                    "" + "-----------------------" + "\n" +
                    "       " + "Thank You" + "\n" +
                    "   " + "Above results are" + "\n" +
                    "      " + " indicative" + "\n" +
                    "  " + "figure,don't follow it" + "\n" +
                    "   " + "without consulting a" + "\n" +
                    "        " + "doctor" + "\n\n\n\n\n\n\n";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setNewList() {

        printDataListNew.add(new PrintData("Height", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.WEIGHT, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.WEIGHT, ""))));
        printDataListNew.add(new PrintData("BMI", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.BMI, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.BMI, ""))));
        printDataListNew.add(new PrintData("Body fat", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.BODY_FAT, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_FAT, ""))));
        printDataListNew.add(new PrintData("Fat Free weight", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.FAT_FREE_WEIGHT, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.FAT_FREE_WEIGHT, ""))));
        printDataListNew.add(new PrintData("Subcutaneous Fat", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, ""))));
        printDataListNew.add(new PrintData("Visceral Fat", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.VISCERAL_FAT, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.VISCERAL_FAT, ""))));
        printDataListNew.add(new PrintData("Body water", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.BODY_WATER, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.BODY_WATER, ""))));
        printDataListNew.add(new PrintData("Skeleton muscle", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, ""))));
        printDataListNew.add(new PrintData("Protein", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.PROTEIN, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.PROTEIN, ""))));
        printDataListNew.add(new PrintData("Metabolic Age", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.META_AGE, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.META_AGE, ""))));
        printDataListNew.add(new PrintData("Health Score", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.HEALTH_SCORE, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.HEALTH_SCORE, ""))));
        printDataListNew.add(new PrintData("BMR", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.BMR, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.BMR, ""))));
        printDataListNew.add(new PrintData("Physique", 0.0));
        printDataListNew.add(new PrintData("Muscle Mass", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.MUSCLE_MASS, ""))));
        printDataListNew.add(new PrintData("Bone Mass", TextUtils.isEmpty(ActofitObject.getString(Constant.Fields.BONE_MASS, "")) ? 0 : Double.parseDouble(ActofitObject.getString(Constant.Fields.BONE_MASS, ""))));
        printDataListNew.add(new PrintData("Body Temp", TextUtils.isEmpty(ThermometerObject.getString(Constant.Fields.TEMPERATURE, "")) ? 0 : Double.parseDouble(ThermometerObject.getString(Constant.Fields.TEMPERATURE, ""))));
        printDataListNew.add(new PrintData("Systolic", TextUtils.isEmpty(BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "")) ? 0 : Double.parseDouble(BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, ""))));
        printDataListNew.add(new PrintData("Diastolic", TextUtils.isEmpty(BPObject.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, "")) ? 0 : Double.parseDouble(BPObject.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, ""))));
        printDataListNew.add(new PrintData("Pulse Oximeter", TextUtils.isEmpty(OximeterObject.getString(Constant.Fields.BLOOD_OXYGEN, "")) ? 0 : Double.parseDouble(OximeterObject.getString(Constant.Fields.BLOOD_OXYGEN, ""))));
        printDataListNew.add(new PrintData("Pulse ", TextUtils.isEmpty(OximeterObject.getString(Constant.Fields.PULSE_RATE, "")) ? 0 : Double.parseDouble(OximeterObject.getString(Constant.Fields.PULSE_RATE, ""))));
        printDataListNew.add(new PrintData("Blood Glucose", TextUtils.isEmpty(BiosenseObject.getString(Constant.Fields.SUGAR, "")) ? 0 : Double.parseDouble(BiosenseObject.getString(Constant.Fields.SUGAR, ""))));
        printDataListNew.add(new PrintData("Hemoglobin", TextUtils.isEmpty(HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "")) ? 0 : Double.parseDouble(HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, ""))));

        lV.setAdapter(new PrintPreviewAdapter(this, R.layout.printlist_item, printDataListNew));

    }

    private void postData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUtils.PRINT_POST_URL,
                response -> {
                    readFileName(response);

                    try {
                        Toast.makeText(getApplicationContext(), "Data Uploaded on Server", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {

                    }
                },
                volleyError -> {
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
                params.put(Constant.Fields.BMI, ActofitObject.getString(Constant.Fields.BMI, ""));
                params.put(Constant.Fields.BMR, ActofitObject.getString(Constant.Fields.BMR, ""));
                params.put(Constant.Fields.SUGAR, BiosenseObject.getString(Constant.Fields.SUGAR, ""));
                params.put(Constant.Fields.HEIGHT, ActofitObject.getString(Constant.Fields.HEIGHT, ""));
                params.put(Constant.Fields.WEIGHT, ActofitObject.getString(Constant.Fields.WEIGHT, ""));
                params.put(Constant.Fields.GENDER, PersonalObject.getString(Constant.Fields.GENDER, ""));
                params.put(Constant.Fields.PROTEIN, ActofitObject.getString(Constant.Fields.PROTEIN, ""));
                params.put(Constant.Fields.META_AGE, ActofitObject.getString(Constant.Fields.META_AGE, ""));
                params.put(Constant.Fields.PULSE_RATE, OximeterObject.getString(Constant.Fields.PULSE_RATE, ""));
                params.put(Constant.Fields.BODY_FAT, ActofitObject.getString(Constant.Fields.BODY_FAT, ""));
                params.put(Constant.Fields.PHYSIQUE, ActofitObject.getString(Constant.Fields.PHYSIQUE, ""));
                params.put(Constant.Fields.BONE_MASS, ActofitObject.getString(Constant.Fields.BONE_MASS, ""));
                params.put(Constant.Fields.BLOOD_OXYGEN, OximeterObject.getString(Constant.Fields.BLOOD_OXYGEN, ""));
                params.put(Constant.Fields.BODY_WATER, ActofitObject.getString(Constant.Fields.BODY_WATER, ""));
                params.put(Constant.Fields.MUSCLE_MASS, ActofitObject.getString(Constant.Fields.MUSCLE_MASS, ""));
                params.put(Constant.Fields.HEMOGLOBIN, HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, ""));
                params.put(Constant.Fields.HEALTH_SCORE, ActofitObject.getString(Constant.Fields.HEALTH_SCORE, ""));
                params.put(Constant.Fields.VISCERAL_FAT, ActofitObject.getString(Constant.Fields.VISCERAL_FAT, ""));
                params.put(Constant.Fields.TEMPERATURE, ThermometerObject.getString(Constant.Fields.TEMPERATURE, ""));
                params.put(Constant.Fields.SUBCUTANEOUS_FAT, ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, ""));
                params.put(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, BPObject.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, ""));
                params.put(Constant.Fields.SKELETAL_MUSCLE, ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, ""));
                params.put(Constant.Fields.FAT_FREE_WEIGHT, ActofitObject.getString(Constant.Fields.FAT_FREE_WEIGHT, ""));
                params.put(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, ""));

                if (!ActofitObject.getString(Constant.Fields.WEIGHT, "").equalsIgnoreCase("")) {
                    params.put("weightrange", "" + standardWeightRange + "kg");
                } else {
                    params.put("weightrange", "NA");
                }

                if (!ActofitObject.getString(Constant.Fields.BMI, "").equalsIgnoreCase("")) {
                    params.put("bmirange", "18.5-25");
                } else {
                    params.put("bmirange", "NA");
                }

                if (!ActofitObject.getString(Constant.Fields.BODY_FAT, "").equalsIgnoreCase("")) {
                    params.put("bodyfatrange", standardBodyFat);
                } else {
                    params.put("bodyfatrange", "NA");
                }

                if (!ActofitObject.getString(Constant.Fields.SUBCUTANEOUS_FAT, "").equalsIgnoreCase("")) {
                    params.put("subfatrange", subcutaneousFat);
                } else {
                    params.put("subfatrange", "NA");
                }
                if (!ActofitObject.getString(Constant.Fields.VISCERAL_FAT, "").equalsIgnoreCase("")) {
                    params.put("visceralfatrange", standardVisceralFat);
                    Log.e("viscerialFatRange", "" + standardVisceralFat);
                } else {
                    params.put("visceralfatrange", "NA");
                }
                if (!ActofitObject.getString(Constant.Fields.BODY_WATER, "").equalsIgnoreCase("")) {
                    params.put("bodywaterrange", standardBodyWater);
                } else {
                    params.put("bodywaterrange", "NA");
                }
                if (!ActofitObject.getString(Constant.Fields.SKELETAL_MUSCLE, "").equalsIgnoreCase("")) {
                    params.put("skeletanmusclerange", standardSkeltonMuscle);
                } else {
                    params.put("skeletanmusclerange", "NA");
                }
                if (!ActofitObject.getString(Constant.Fields.PROTEIN, "").equalsIgnoreCase("")) {
                    params.put("proteinrange", "16-18 %");
                } else {
                    params.put("proteinrange", "NA");
                }
                if (!ActofitObject.getString(Constant.Fields.META_AGE, "").equalsIgnoreCase("")) {
                    params.put("metaagerange", "<=" + age);
                    Log.e("metaage_range", "<=" + age);
                } else {
                    params.put("metaagerange", "NA");
                }
                params.put("healthscorerange", "");
                if (!ActofitObject.getString(Constant.Fields.BMR, "").equalsIgnoreCase("")) {
                    params.put("bmrrange", standardBMR);
                } else {
                    params.put("bmrrange", "NA");
                }
                params.put("physiquerange", "");
                if (!ActofitObject.getString(Constant.Fields.MUSCLE_MASS, "").equalsIgnoreCase("")) {
                    params.put("musclemassrange", standardMuscleMass);
                } else {
                    params.put("musclemassrange", "NA");
                }
                if (!ActofitObject.getString(Constant.Fields.BONE_MASS, "").equalsIgnoreCase("")) {
                    params.put("bonemassrange", standardBoneMass);
                } else {
                    params.put("bonemassrange", "NA");
                }
                if (!ThermometerObject.getString(Constant.Fields.TEMPERATURE, "").equalsIgnoreCase("")) {
                    params.put("bodytemprange", "97 - 99 F");
                } else {
                    params.put("bodytemprange", "NA");
                }

                if (!BPObject.getString(Constant.Fields.BLOOD_PRESSURE_SYSTOLIC, "").equalsIgnoreCase(""))
                    params.put("systolicrange", "90-139 mmHg");
                else
                    params.put("systolicrange", "NA");
                if (!BPObject.getString(Constant.Fields.BLOOD_PRESSURE_DIASTOLIC, "").equalsIgnoreCase(""))
                    params.put("dialosticrange", "60-89 mmHg");
                else
                    params.put("dialosticrange", "NA");
                if (!OximeterObject.getString(Constant.Fields.BLOOD_OXYGEN, "").equalsIgnoreCase(""))
                    params.put("pulseoximeterrange", ">94%");
                else
                    params.put("pulseoximeterrange", "NA");
                if (!OximeterObject.getString(Constant.Fields.PULSE_RATE, "").equalsIgnoreCase(""))
                    params.put("pulserange", "60-100 bpm");
                else
                    params.put("pulserange", "NA");
                if (!BiosenseObject.getString(Constant.Fields.SUGAR, "").equalsIgnoreCase(""))
                    params.put("bloodsugarrange", standardGlucose);
                else
                    params.put("bloodsugarrange", "NA");
                if (!HemoglobinObject.getString(Constant.Fields.HEMOGLOBIN, "").equalsIgnoreCase(""))
                    params.put("hemoglobinrange", standarHemoglobin);
                else
                    params.put("hemoglobinrange", "NA");

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
                params.put("temperatureresult", TEMPERATUREResult);
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

    /**
     * @param response
     */
    private void readFileName(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            fileName = dataObject.getString("file");
            System.out.println("fileName = " + fileName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        return new SimpleDateFormat("HH:mm aa").format(Calendar.getInstance().getTime());
    }

    @OnClick({R.id.homebtn, R.id.printbtn, R.id.iv_download})
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
                downloadFile(fileName);
                break;
        }
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

            int result = textToSpeech.setLanguage(Locale.US);

            textToSpeech.setSpeechRate(1);

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

    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
            if (iRetVal == DEVICE_NOTCONNECTED)
                ptrHandler.obtainMessage(1, "Device not connected").sendToTarget();
            else if (iRetVal == Printer_GEN.SUCCESS)
                ptrHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
            else if (iRetVal == Printer_GEN.PLATEN_OPEN)
                ptrHandler.obtainMessage(1, "Platen open").sendToTarget();
            else if (iRetVal == Printer_GEN.PAPER_OUT)
                ptrHandler.obtainMessage(1, "Paper out").sendToTarget();
            else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE)
                ptrHandler.obtainMessage(1, "Printer at improper voltage").sendToTarget();
            else if (iRetVal == Printer_GEN.FAILURE) {
                ptrHandler.obtainMessage(1, "Print failed").sendToTarget();
                PrintPreviewActivity.this.recreate();
            } else if (iRetVal == Printer_GEN.PARAM_ERROR)
                ptrHandler.obtainMessage(1, "Parameter error").sendToTarget();
            else if (iRetVal == Printer_GEN.NO_RESPONSE)
                ptrHandler.obtainMessage(1, "No response from Pride device").sendToTarget();
            else if (iRetVal == Printer_GEN.DEMO_VERSION)
                ptrHandler.obtainMessage(1, "Library in demo version").sendToTarget();
            else if (iRetVal == Printer_GEN.INVALID_DEVICE_ID)
                ptrHandler.obtainMessage(1, "Connected  device is not authenticated.").sendToTarget();
            else if (iRetVal == Printer_GEN.NOT_ACTIVATED)
                ptrHandler.obtainMessage(1, "Library not activated").sendToTarget();
            else if (iRetVal == Printer_GEN.NOT_SUPPORTED)
                ptrHandler.obtainMessage(1, "Not Supported").sendToTarget();
            else
                ptrHandler.obtainMessage(1, "Unknown Response from Device").sendToTarget();

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
