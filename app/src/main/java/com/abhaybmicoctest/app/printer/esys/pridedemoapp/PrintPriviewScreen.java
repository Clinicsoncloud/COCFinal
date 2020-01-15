package com.abhaybmicoctest.app.printer.esys.pridedemoapp;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.abhaybmicoctest.app.OtpLoginScreen;
import com.abhaybmicoctest.app.R;
import com.abhaybmicoctest.app.entities.AndMedical_App_Global;
import com.abhaybmicoctest.app.printer.evolute.bluetooth.BluetoothComm;
import com.abhaybmicoctest.app.utils.ApiUtils;
import com.abhaybmicoctest.app.utils.Tools;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.evolute.qrimage.QRCodeGenerator;
import com.evolute.textimage.TextGenerator;
import com.evolute.textimage.TextGenerator.ImageWidth;
import com.evolute.textimage.TextGenerator.Justify;
import com.prowesspride.api.HexString;
import com.prowesspride.api.Printer_GEN;

import static com.abhaybmicoctest.app.utils.ApiUtils.PREFERENCE_THERMOMETERDATA;

public class PrintPriviewScreen extends Activity {

    private final static int MESSAGE_BOX = 1;
    private String sBarcodeStr, sAddDataStr;
    private ImageView imgGalery;
    public static int RESULT_LOAD_IMAGE = 10;
    private String sPicturePath = null;
    private static int iBacodeSpinnerPostion = 1;
    private static byte iBarcodeStyle;
    private Uri uriSelectedImage;
    public Dialog dlgCustomdialog;
    private LinearLayout llprog;
    public static ProgressBar pbProgress;
    private Button btnUnicode11, btnConfirm;
    private Dialog dlgBarcode;
    private Button btnOk;
    private Bitmap bSelectImg;
    boolean bGrayScale = false;
    Button cust_btn, homebtn;
    int counter = 0;
    String str, id;
    int xx = 0;
    int xx1 = 0;
    public static final int INVALID_IMAGE = -101;
    // public static Printer_ESC ptrEsc;
    Boolean bEditPrint = false;
    public static Justify justfyPostion;
    public Boolean bConfirm = false;
    LinearLayout linrlayout;
    EditText cust_edt;
    TextView tv_selectLang;
    LinearLayout linrtxtvw;
    ProgressDialog pd;
    SharedPreferences ActofitObject, OximeterObject, PersonalObject, ThermometerObject, BPObject;
    SharedPreferences BiosenseObject, HemoglobinObject, spToken;
    public static String TAG = "Act_GeneralPrinterActivity";
    public static final String[] titles = new String[]{"[1]Test Print",
            "[2]Evolute Logo", "[3]Custom Text", "[4]Bitmap Print", "[5]GreyScale Print",
            "[6]Barcode Print", "[7]Paperfeed", "[8]Diagnostics",
            "[9]Unicode Printing", "[10]QR-code"};

    private static final String[] descriptions = new String[]{
            "To print test print from Pride",
            "To print stored logo packets of evolute BMP",
            "To print text in different fonts",
            "To print selected bitmap image",
            "To print Selected bitmap image in grayscale",
            "To print Barcode data with specified barcode type",
            "To feed paper", "To check device status",
            "To print Unicode data as image", "To print QR code"};

    private String[] entertext_font = {"Font Large Normal", "FONT LARGE BOLD",
            "FONT SMALL NORMAL", "FONT SMALL BOLD", "FONT ULLARGE NORMAL",
            "FONT ULLARGE BOLD", "FONT ULSMALL NORMAL", "FONT ULSMALL BOLD",
            "FONT 180LARGE NORMAL", "FONT 180LARGE BOLD",
            "FONT 180SMALL NORMAL", "FONT 180 SMALLBOLD",
            "FONT 180ULLARGE NORMAL", "FONT 180ULLARGE BOLD",
            "FONT 180ULSMALL NORMAL", "FONT 180ULSMALL BOLD"};

    private String[] barcode_style = {"2OF5 COMPRESSED", "2OF5UNCOMPRESSED",
            "3OF9 COMPRESSED", "3OF9UNCOMPRESSED", "EAN13/UPC-A"};

    ListView lvGenerol;
    List<Act_GenRowItem> lRowItems;
    Context context = this;
    int iRetVal;
    private String printString = "";
    EditText edtText, edtBarcode, edtAddLine;
    public static Printer_GEN ptrGen;
    private static byte bFontStyle;
    public static int iWidth;
    public static final int DEVICE_NOTCONNECTED = -100;
    public static boolean General = false;

    public static Display display;

    private TextView txtprotine, txtphysique, txtweight, txtfat_free_weight, txtvisceral_fat, txtbody_water;
    private TextView txtskeletal_muscle, txtmuscle_mass, txtbone_mass, txtBMR, txtmeta_age, txthelthscore;
    private TextView txtskemus, txtsubfat, txtBmi, txtbodyfat, txtheight, txtpulse, txtoxygen;
    private TextView txtname, txtmobile, txttempreture, txtbp, txtglucose, txthemoglobin;

    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generolprinter);

        txtpulse = findViewById(R.id.txtpulse);
        txtoxygen = findViewById(R.id.txtoxygen);
        txtphysique = findViewById(R.id.txtphysique);
        txtweight = findViewById(R.id.txtweight);
        txtprotine = findViewById(R.id.txtprotine);
        txtfat_free_weight = findViewById(R.id.txtfat_free_weight);
        txtvisceral_fat = findViewById(R.id.txtvisceral_fat);
        txtbody_water = findViewById(R.id.txtbody_water);

        txtbodyfat = findViewById(R.id.txtbodyfat);
        txtskeletal_muscle = findViewById(R.id.txtskeletal_muscle);
        txtmuscle_mass = findViewById(R.id.txtmuscle_mass);
        txtbone_mass = findViewById(R.id.txtbone_mass);
        txtBMR = findViewById(R.id.txtBMR);
        txtmeta_age = findViewById(R.id.txtmeta_age);
        txthelthscore = findViewById(R.id.txthelthscore);
        txtskemus = findViewById(R.id.txtskemus);
        txtsubfat = findViewById(R.id.txtsubfat);
        txtBmi = findViewById(R.id.txtBmi);
        txtheight = findViewById(R.id.txtheight);
        txtname = findViewById(R.id.txtname);
        txtmobile = findViewById(R.id.txtmobile);
        txttempreture = findViewById(R.id.txttempreture);
        txtbp = findViewById(R.id.txtbp);
        txthemoglobin = findViewById(R.id.txthemoglobin);
        txtglucose = findViewById(R.id.txtglucose);
        homebtn = findViewById(R.id.homebtn);

        homebtn.setOnClickListener(v -> {
            /**
             * clear Database here */
            SharedPreferences objBiosense = context.getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, Context.MODE_PRIVATE);
            SharedPreferences objBp = context.getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, Context.MODE_PRIVATE);
            SharedPreferences objPulse = context.getSharedPreferences(ApiUtils.PREFERENCE_PULSE, Context.MODE_PRIVATE);
            SharedPreferences objActofit = context.getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, Context.MODE_PRIVATE);
            SharedPreferences objNewRecord = context.getSharedPreferences(ApiUtils.PREFERENCE_NEWRECORD, Context.MODE_PRIVATE);
            SharedPreferences objUrl = context.getSharedPreferences(ApiUtils.PREFERENCE_URL, Context.MODE_PRIVATE);
            SharedPreferences objAshok = context.getSharedPreferences("ashok", Context.MODE_PRIVATE);
            objBiosense.edit().clear().commit();
            objBp.edit().clear().commit();
            objPulse.edit().clear().commit();
            objActofit.edit().clear().commit();
            objNewRecord.edit().clear().commit();
            objUrl.edit().clear().commit();
            objAshok.edit().clear().commit();
            Intent newIntent = new Intent(getApplicationContext(), OtpLoginScreen.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(newIntent);
            finish();
        });

        try {
            InputStream input = BluetoothComm.misIn;
            OutputStream outstream = BluetoothComm.mosOut;
            ptrGen = new Printer_GEN(Act_GlobalPool.setup, outstream, input);
            Log.e("", "pirnter gen is activated");
        } catch (Exception e) {
            Log.e("", "pirnter gen is not activated" + e);
        }

        try {
            SharedPreferences shared = getSharedPreferences(ApiUtils.PREFERENCE_URL, MODE_PRIVATE);
            // id = (shared.getString("id", ""));
            ActofitObject = getSharedPreferences(ApiUtils.PREFERENCE_ACTOFIT, MODE_PRIVATE);
            OximeterObject = getSharedPreferences(ApiUtils.PREFERENCE_PULSE, MODE_PRIVATE);
            PersonalObject = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
            ThermometerObject = getSharedPreferences(PREFERENCE_THERMOMETERDATA, MODE_PRIVATE);
            BPObject = getSharedPreferences(ApiUtils.PREFERENCE_BLOODPRESSURE, MODE_PRIVATE);
            BiosenseObject = getSharedPreferences(ApiUtils.PREFERENCE_BIOSENSE, MODE_PRIVATE);
            HemoglobinObject = getSharedPreferences(ApiUtils.PREFERENCE_HEMOGLOBIN, MODE_PRIVATE);
            spToken = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);
            System.out.println("-AAAAA----------" + BPObject.getString("systolic", ""));
            txtweight.setText("Weight : " + ActofitObject.getString("weight", "") + " Kg");
            txtheight.setText("Height : " + ActofitObject.getString("height", "") + " CM");
            txtBmi.setText("BMI : " + ActofitObject.getString("bmi", ""));
            txtbodyfat.setText("Body Fat : " + ActofitObject.getString("bodyfat", "") + " %");
            txtfat_free_weight.setText("Fat Free Weight : " + ActofitObject.getString("fatfreeweight", "") + " Kg");
            txtphysique.setText("Physiue : " + ActofitObject.getString("physique", ""));
            txtvisceral_fat.setText("Visceral Fat : " + ActofitObject.getString("visfat", ""));
            txtbody_water.setText("Body Water : " + ActofitObject.getString("bodywater", ""));
            txtmuscle_mass.setText("Muscle Mass : " + ActofitObject.getString("musmass", ""));
            txtbone_mass.setText("Bone Mass : " + ActofitObject.getString("bonemass", ""));
            txtprotine.setText("Protine : " + ActofitObject.getString("protine", ""));
            txtBMR.setText("BMR : " + ActofitObject.getString("bmr", ""));
            txtsubfat.setText("Subcutaneous Fat : " + ActofitObject.getString("subfat", "") + " %");
            txtskeletal_muscle.setText("Skeletal Muscle : " + ActofitObject.getString("skemus", ""));
            txtmeta_age.setText("Meta Age : " + ActofitObject.getString("metaage", ""));
            txthelthscore.setText("Health Score : " + ActofitObject.getString("helthscore", ""));

            txtpulse.setText("Pulse : " + OximeterObject.getString("pulse_rate", ""));
            txtoxygen.setText("Oxygen : " + OximeterObject.getString("body_oxygen", ""));

            txtname.setText("Name : " + PersonalObject.getString("name", ""));
            txtmobile.setText("Mobile Number : " + PersonalObject.getString("mobile_number", ""));

            txttempreture.setText("Temperature : " + ThermometerObject.getString("data", ""));

            txtbp.setText("Blood Pressure : systolic - " + BPObject.getString("systolic", "") + " diastolic - " + BPObject.getString("diastolic", ""));
            txtglucose.setText("Blood Sugar : " + BiosenseObject.getString("last", ""));

            txthemoglobin.setText("Hemoglobin : " + HemoglobinObject.getString("hemoglobin", ""));

        } catch (Exception e) {

        }

        display = getWindowManager().getDefaultDisplay();
        iWidth = display.getWidth();

        lRowItems = new ArrayList<Act_GenRowItem>();
        for (int i = 0; i < titles.length; i++) {
            Act_GenRowItem item = new Act_GenRowItem(titles[i], descriptions[i]);
            lRowItems.add(item);
        }

        Button procedurebtn = findViewById(R.id.procedurebtn);
        procedurebtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgEnterText();
//                EnterTextAsyc asynctask = new EnterTextAsyc();
//                asynctask.execute(0);
                PostData();
            }
        });
        lvGenerol = (ListView) findViewById(R.id.lvGenerol);
        TextView txtTitle = findViewById(R.id.txtTitle);
        TextView txtname = findViewById(R.id.txtname);
        TextView txtmobile = findViewById(R.id.txtmobile);
        TextView txtheight = findViewById(R.id.txtheight);
        TextView txtweight = findViewById(R.id.txtweight);
        TextView txtbp = findViewById(R.id.txtbp);
        TextView txtBmi = findViewById(R.id.txtBmi);
        TextView txtoxygen = findViewById(R.id.txtoxygen);
        TextView txtpulse = findViewById(R.id.txtpulse);
        TextView txthemoglobin = findViewById(R.id.txthemoglobin);
        TextView txtglucose = findViewById(R.id.txtglucose);
        TextView txttempreture = findViewById(R.id.txttempreture);
        TextView txtphysique = findViewById(R.id.txtphysique);
        TextView txtfat_free_weight = findViewById(R.id.txtfat_free_weight);
        TextView txtvisceral_fat = findViewById(R.id.txtvisceral_fat);
        TextView txtbody_water = findViewById(R.id.txtbody_water);
        TextView txtskeletal_muscle = findViewById(R.id.txtskeletal_muscle);
        TextView txtbodyfat = findViewById(R.id.txtbodyfat);
        TextView txtmuscle_mass = findViewById(R.id.txtmuscle_mass);
        TextView txtbone_mass = findViewById(R.id.txtbone_mass);
        TextView txtprotine = findViewById(R.id.txtprotine);
        TextView txtBMR = findViewById(R.id.txtBMR);
        TextView txtmeta_age = findViewById(R.id.txtmeta_age);
        TextView txthelthscore = findViewById(R.id.txthelthscore);
        TextView txtskemus = findViewById(R.id.txtskemus);
        TextView txtsubfat = findViewById(R.id.txtsubfat);
        TextView txtWish = findViewById(R.id.txtWish);

        TextView txtstandardrange = findViewById(R.id.txtstandardrange);
        TextView txtbmirange = findViewById(R.id.txtbmirange);
        TextView txtbodyfatrange = findViewById(R.id.txtbodyfatrange);
        TextView txtSubcutaneousfatrange = findViewById(R.id.txtSubcutaneousfatrange);
        TextView txtSkeletalMusclerange = findViewById(R.id.txtSkeletalMusclerange);
        TextView txtMuscleMassrange = findViewById(R.id.txtMuscleMassrange);
        TextView txtBoneMassRange = findViewById(R.id.txtBoneMassRange);
        TextView txtProteinMassRange = findViewById(R.id.txtProteinMassRange);

        printString = "\n\n\n" + txtTitle.getText().toString() + "\n" +
                txtname.getText().toString() + "\n" +
                txtmobile.getText().toString() + "\n" +
                txtheight.getText().toString() + "\n" +
                txtweight.getText().toString() + "\n" +
                txtbp.getText().toString() + "\n" +
                txtoxygen.getText().toString() + "\n" +
                txthemoglobin.getText().toString() + "\n" +
                txtglucose.getText().toString() + "\n" +
                txtphysique.getText().toString() + "\n" +
                txtfat_free_weight.getText().toString() + "\n" +
                txtvisceral_fat.getText().toString() + "\n" +
                txtbody_water.getText().toString() + "\n" +
                txtskeletal_muscle.getText().toString() + "\n" +
                txtmuscle_mass.getText().toString() + "\n" +
                txtbone_mass.getText().toString() + "\n" +
                txtprotine.getText().toString() + "\n" +
                txtBMR.getText().toString() + "\n" +
                txtmeta_age.getText().toString() + "\n" +
                txthelthscore.getText().toString() + "\n" +
                txtskemus.getText().toString() + "\n" +
                txtsubfat.getText().toString() + "\n" +
                txtpulse.getText().toString() + "\n" +
                txtBmi.getText().toString() + "\n" +
                txtbodyfat.getText().toString() + "\n" +
                txttempreture.getText().toString() + "\n\n\n\n" +
                txtstandardrange.getText().toString() + "\n" +
                txtbmirange.getText().toString() + "\n" +
                txtbodyfatrange.getText().toString() + "\n" +
                txtSubcutaneousfatrange.getText().toString() + "\n" +
                txtSkeletalMusclerange.getText().toString() + "\n" +
                txtMuscleMassrange.getText().toString() + "\n" +
                txtBoneMassRange.getText().toString() + "\n" +
                txtProteinMassRange.getText().toString() + "\n\n\n\n\n";

        Act_GeneralBaseAdapter adapter = new Act_GeneralBaseAdapter(this, lRowItems);
        lvGenerol.setAdapter(adapter);
        lvGenerol.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        /* ITestPrint undergoes AsynTask operation */
                        TestPrint itest = new TestPrint();
                        itest.execute(0);
                        break;
                    case 1:
                        /* ILogPrint undergoes AsynTask operation */
                        LogPrint ilog = new LogPrint();
                        ilog.execute(0);
                        break;
                    case 2:
                        /* CustomText undergoes AsynTask operation */
                        dlgEnterText();
                        break;
                    case 3:
                        /* PrintBitmap undergoes AsynTask operation */
                        dlgShowImage();
                        break;
                    case 4:
                        bGrayScale = true;
                        dlgShowImage();
                        //Toast.makeText(getApplicationContext(), "Gray scale Print", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        /* PrintBarcode undergoes AsynTask operation */
                        dlgBarcode();
                        break;
                    case 6:
                        /* PaperFeed undergoes AsynTask operation */
                        PaperFeed paperfeed = new PaperFeed();
                        paperfeed.execute(0);
                        break;
                    case 7:
                        /* DiagnosePrint undergoes AsynTask operation */
                        DiagnosePrint diagonous = new DiagnosePrint();
                        diagonous.execute(0);
                        break;
                    case 8:
                        Log.e("tag", "in unicode");
                        /* DiagnosePrint undergoes AsynTask operation */
                        //   Act_UnicodePrinting unicodeprinting=new Act_UnicodePrinting(context, ptrGen);
                        //  unicodeprinting.unicode();
                        break;
                    case 9:
                        Log.e(">>QR", "in case 8");
                        // Act_QRCode qrcodeasyc=new Act_QRCode(context, ptrGen);
                        //  qrcodeasyc.QrCode();
                        break;
                    default:
                        break;
                }
            }

            private void QrCode() {

                // TODO Auto-generated method stub
                Log.e(">>QR", "in method...>>");
                Bitmap bmpDrawQRCode = null;
                try {
                    bmpDrawQRCode = QRCodeGenerator.bmpDrawQRCode(ImageWidth.Inch_3, "hello");
                    Log.e(">>QR", "before api");
                    byte[] bBmpFileData = TextGenerator.bGetBmpFileData(bmpDrawQRCode);
                    Log.d("leg", "byte data...." + HexString.bufferToHex(bBmpFileData));//bBmpFileData);
                    ByteArrayInputStream bis = new ByteArrayInputStream(bBmpFileData);
                    int xx1 = ptrGen.iBmpPrint(bis);
                    Log.e("QR", "result" + xx1);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }
        });

    }

    @SuppressLint("HandlerLeak")
    Handler hander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MESSAGE_BOX:
                    String str = (String) msg.obj;
                    dlgShow(str);

                    break;
                default:
                    break;
            }
        }

        ;
    };

    /* Custom Dialogbox for barcode */
    public void dlgBarcode() {
        dlgBarcode = new Dialog(context);
        dlgBarcode.setTitle("Barcode Print");
        dlgBarcode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgBarcode.setContentView(R.layout.dlggenbarcode);
        TextView textView11 = (TextView) dlgBarcode
                .findViewById(R.id.textView11);
        textView11.setWidth(iWidth);
        edtBarcode = (EditText) dlgBarcode.findViewById(R.id.barcode_edt);
        Spinner barcode_sp = (Spinner) dlgBarcode.findViewById(R.id.spBarcode);
        ArrayAdapter<String> barcode_ad = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, barcode_style);
        barcode_ad
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barcode_sp.setAdapter(barcode_ad);
        barcode_sp.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View v,
                                       int position, long arg3) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        iBacodeSpinnerPostion = 1;
                        iBarcodeStyle = Printer_GEN.BARCODE_2OF5_COMPRESSED;// (byte)
                        break;
                    case 1:
                        InputMethodManager mgr1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr1.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        iBacodeSpinnerPostion = 2;
                        iBarcodeStyle = Printer_GEN.BARCODE_2OF5_UNCOMPRESSED;// (byte)
                        break;
                    case 2:
                        InputMethodManager mgr2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr2.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        iBacodeSpinnerPostion = 3;
                        iBarcodeStyle = Printer_GEN.BARCODE_3OF9_COMPRESSED;// (byte)
                        break;
                    case 3:
                        InputMethodManager mgr3 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr3.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        iBacodeSpinnerPostion = 4;
                        iBarcodeStyle = Printer_GEN.BARCODE_3OF9_UNCOMPRESSED;// (byte)
                        break;
                    case 4:
                        InputMethodManager mgr4 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr4.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        iBacodeSpinnerPostion = 5;
                        iBarcodeStyle = Printer_GEN.BARCODE_EAN_13_STANDARD;// (byte)
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        edtBarcode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (iBacodeSpinnerPostion) {
                    case 1:
                        edtBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                        edtBarcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
                        break;
                    case 2:
                        edtBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                        edtBarcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                        break;
                    case 3:
                        edtBarcode.setInputType(InputType.TYPE_CLASS_TEXT);
                        edtBarcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
                        break;
                    case 4:
                        edtBarcode.setInputType(InputType.TYPE_CLASS_TEXT);
                        edtBarcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                        break;
                    case 5:
                        edtBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                        edtBarcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                        break;
                    default:
                        break;
                }
            }
        });

        Button btnBarcodeprint = (Button) dlgBarcode.findViewById(R.id.btnBarcodeprint);
        btnBarcodeprint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlgBarcode.dismiss();
                sBarcodeStr = edtBarcode.getText().toString();
                edtBarcode.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean focused) {
                        switch (iBacodeSpinnerPostion) {
                            case 1:
                                edtBarcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                                break;
                            case 2:
                                edtBarcode.setInputType(InputType.TYPE_CLASS_TEXT);
                                break;
                            default:
                                break;
                        }
                    }
                });

                char[] ccc = sBarcodeStr.toCharArray();
                if (sBarcodeStr.length() == 0) {
                    ptrHandler.obtainMessage(2, "Enter text").sendToTarget();
                } else if (sBarcodeStr.length() > 0) {
                    switch (iBacodeSpinnerPostion) {
                        case 3:
                            BarCodePrint barcode = new BarCodePrint();
                            barcode.execute(0);
                            break;
                        case 1:
                            int i;
                            for (i = 0; i < sBarcodeStr.length(); i++) {
                                if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
                                    break;
                                }
                            }
                            if (i != sBarcodeStr.length()) {
                                ptrHandler.obtainMessage(2,
                                        "Please enter numeric characters")
                                        .sendToTarget();
                            } else {
                                BarCodePrint barcode1 = new BarCodePrint();
                                barcode1.execute(0);
                            }
                            break;
                        case 2:
                            sBarcodeStr = edtBarcode.getText().toString();
                            if (sBarcodeStr.length() > 12) {
                                ptrHandler.obtainMessage(2,
                                        "Enter data less than 13 characters")
                                        .sendToTarget();
                            } else {
                                for (i = 0; i < sBarcodeStr.length(); i++) {
                                    if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
                                        break;
                                    }
                                }
                                if (i != sBarcodeStr.length()) {
                                    ptrHandler.obtainMessage(2,
                                            "Please enter numeric characters")
                                            .sendToTarget();
                                } else {
                                    BarCodePrint barcode1 = new BarCodePrint();
                                    barcode1.execute(0);
                                }
                            }
                            break;
                        case 4:
                            sBarcodeStr = edtBarcode.getText().toString();
                            if (sBarcodeStr.length() > 12) {
                                ptrHandler.obtainMessage(2,
                                        "Enter data less than 13 characters")
                                        .sendToTarget();
                            } else {
                                BarCodePrint barcode1 = new BarCodePrint();
                                barcode1.execute(0);
                            }
                            break;
                        case 5:
                            sBarcodeStr = edtBarcode.getText().toString();
                            if (sBarcodeStr.length() > 13) {
                                ptrHandler.obtainMessage(2,
                                        "Enter data less than 13").sendToTarget();
                            } else {
                                for (i = 0; i < sBarcodeStr.length(); i++) {
                                    if (!(ccc[i] >= '0' && ccc[i] <= '9')) {
                                        break;
                                    }
                                }
                                if (i != sBarcodeStr.length()) {
                                    ptrHandler.obtainMessage(2,
                                            "Please enter numerics only")
                                            .sendToTarget();
                                } else {
                                    BarCodePrint barcode1 = new BarCodePrint();
                                    barcode1.execute(0);
                                }
                            }
                            break;
                    }

                }
            }
        });
        dlgBarcode.show();
    }

    Button print;
    Dialog dialog1;
    LinearLayout ll;

    /* Custom Diaglog box for bmp selection */
    public void dlgShowImage() {
        dialog1 = new Dialog(context);
        dialog1.setTitle("BMP File");
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.dlggenbitmap);
        TextView tvTitel = (TextView) dialog1.findViewById(R.id.tvTitel);
        tvTitel.setWidth(iWidth);
        print = (Button) dialog1.findViewById(R.id.Print_but);
        imgGalery = (ImageView) dialog1.findViewById(R.id.galery_img_g);
        //imgGalery.setImageResource(R.drawable.evoluteogo);
        if (!bGrayScale) {
            imgGalery.setImageResource(R.drawable.evoluteogo);
        } else {
            imgGalery.setImageResource(R.drawable.sampy1111);
        }
        ll = (LinearLayout) dialog1.findViewById(R.id.imaglay);
        final Button selectimage_but = (Button) dialog1
                .findViewById(R.id.selectimage_but);
        selectimage_but.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                selectimage_but.setEnabled(true);
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                print.setEnabled(true);
                TextView selectpath_tv = (TextView) dialog1
                        .findViewById(R.id.selectpath_tv);
                selectpath_tv.setText(sPicturePath);
            }
        });

        print.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                PrintBitmap bmp = new PrintBitmap();
                bmp.execute(0);

            }
        });
        dialog1.show();
    }

    class Qrcode extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            dlgShowCustom(context, "Please Wait....");
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... params) {


            Log.e(TAG, "in method...>>");
            Bitmap bmpDrawQRCode = null;
            try {
                bmpDrawQRCode = QRCodeGenerator.bmpDrawQRCode(ImageWidth.Inch_2, "hello");
                Log.e(TAG, "before api");
                byte[] bBmpFileData = TextGenerator.bGetBmpFileData(bmpDrawQRCode);
                Log.d(TAG, "byte data...." + HexString.bufferToHex(bBmpFileData));//bBmpFileData);
                ByteArrayInputStream bis = new ByteArrayInputStream(bBmpFileData);
                iRetVal = ptrGen.iBmpPrint(bis);
                Log.e(TAG, "result" + iRetVal);
            } catch (IllegalArgumentException e) {

                e.printStackTrace();
            } catch (Exception e) {
                iRetVal = DEVICE_NOTCONNECTED;
                e.printStackTrace();
            }
            return iRetVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            llprog.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
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
                ptrHandler.obtainMessage(1, "Printing failed").sendToTarget();
            } else if (iRetVal == Printer_GEN.PARAM_ERROR) {
                ptrHandler.obtainMessage(1, "Parameter error").sendToTarget();
            } else if (iRetVal == Printer_GEN.NO_RESPONSE) {
                ptrHandler.obtainMessage(1, "No response from Legend device")
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

    /* This method shows the ITestPrint AsynTask operation */
    public class TestPrint extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog until background task is completed */
        @Override
        protected void onPreExecute() {
            dlgShowCustom(context, "Please Wait....");
            super.onPreExecute();
        }

        /* Task of ITestPrint performing in the background */
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                Log.e(TAG, "iTestPrint.......<<<<<<<>>>>>>>");
                iRetVal = ptrGen.iTestPrint();
                Log.e(TAG, "Test print " + iRetVal);
            } catch (NullPointerException e) {
                Log.e(TAG, "Test print expection " + iRetVal);
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This displays the status messages of ITestPrint in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            llprog.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
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

    /* To show response messages */
    public void dlgShow(String str) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
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

    /* Handler to display UI response messages */
    Handler ptrHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
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
                    Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG)
                            .show();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /* This method shows the ILogPrint AsynTask operation */
    public class LogPrint extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog untill background task is completed */
        @Override
        protected void onPreExecute() {
            dlgShowCustom(context, "Please Wait....");
            super.onPreExecute();
        }

        /* Task of ILogPrint performing in the background */
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                ptrGen.iFlushBuf();
                //iRetVal = ptrGen.iBmpPrint(Act_GenLogos.EVOLUTE_LOGO);
                iRetVal = ptrGen.iBmpPrint(context, R.drawable.logo1);
                if (iRetVal == Printer_GEN.SUCCESS) {
                    String empty = "\n";
                    ptrGen.iAddData((byte) 0x01, empty);
                    ptrGen.iStartPrinting(1);
                }
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This displays the status messages of ILogPrint in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            llprog.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
            if (iRetVal == DEVICE_NOTCONNECTED) {
                ptrHandler.obtainMessage(1, "Device not connected")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.SUCCESS) {
                ptrHandler.obtainMessage(1, "Print Success")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
                ptrHandler.obtainMessage(1, "Platen open").sendToTarget();
            } else if (iRetVal == Printer_GEN.PAPER_OUT) {
                ptrHandler.obtainMessage(1, "Paper out").sendToTarget();
            } else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
                ptrHandler.obtainMessage(1, "Printer at improper voltage")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.FAILURE) {
                ptrHandler.obtainMessage(1, "Print Failed").sendToTarget();
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

    /* Custom dialog box for Font */
    public void dlgEnterText() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.act_genentertext);
        dialog.setTitle("Text Print");
        dialog.setCancelable(true);
        TextView txtpulse = dialog.findViewById(R.id.txtpulse);
        TextView txtoxygen = dialog.findViewById(R.id.txtoxygen);
        TextView txtphysique = dialog.findViewById(R.id.txtphysique);
        TextView txtweight = dialog.findViewById(R.id.txtweight);
        TextView txtprotine = dialog.findViewById(R.id.txtprotine);
        TextView txtfat_free_weight = dialog.findViewById(R.id.txtfat_free_weight);
        TextView txtvisceral_fat = dialog.findViewById(R.id.txtvisceral_fat);
        TextView txtbody_water = dialog.findViewById(R.id.txtbody_water);

        TextView txtbodyfat = dialog.findViewById(R.id.txtbodyfat);
        TextView txtskeletal_muscle = dialog.findViewById(R.id.txtskeletal_muscle);
        TextView txtmuscle_mass = dialog.findViewById(R.id.txtmuscle_mass);
        TextView txtbone_mass = dialog.findViewById(R.id.txtbone_mass);
        TextView txtBMR = dialog.findViewById(R.id.txtBMR);
        TextView txtmeta_age = dialog.findViewById(R.id.txtmeta_age);
        TextView txthelthscore = dialog.findViewById(R.id.txthelthscore);
        TextView txtskemus = dialog.findViewById(R.id.txtskemus);
        TextView txtsubfat = dialog.findViewById(R.id.txtsubfat);
        TextView txtBmi = dialog.findViewById(R.id.txtBmi);
        TextView txtheight = dialog.findViewById(R.id.txtheight);
        TextView txtname = dialog.findViewById(R.id.txtname);
        TextView txtmobile = dialog.findViewById(R.id.txtmobile);
        TextView txttempreture = dialog.findViewById(R.id.txttempreture);
        TextView txtbp = dialog.findViewById(R.id.txtbp);


        txtweight.setText("Weight : " + ActofitObject.getString("weight", "") + " Kg");
        txtheight.setText("Height : " + ActofitObject.getString("height", "") + " CM");
        txtBmi.setText("BMI : " + ActofitObject.getString("bmi", ""));
        txtbodyfat.setText("Body Fat : " + ActofitObject.getString("bodyfat", "") + " %");
        txtfat_free_weight.setText("Fat Free Weight : " + ActofitObject.getString("fatfreeweight", "") + " Kg");
        txtphysique.setText("Physiue : " + ActofitObject.getString("physique", ""));
        txtvisceral_fat.setText("Visceral Fat : " + ActofitObject.getString("visfat", ""));
        txtbody_water.setText("Body Water : " + ActofitObject.getString("bodywater", ""));
        txtmuscle_mass.setText("Muscle Mass : " + ActofitObject.getString("musmass", ""));
        txtbone_mass.setText("Bone Mass : " + ActofitObject.getString("bonemass", ""));
        txtprotine.setText("Protine : " + ActofitObject.getString("protine", ""));
        txtBMR.setText("BMR : " + ActofitObject.getString("bmr", ""));
        txtsubfat.setText("Subcutaneous Fat : " + ActofitObject.getString("subfat", "") + " %");
        txtskeletal_muscle.setText("Skeletal Muscle : " + ActofitObject.getString("skemus", ""));
        txtmeta_age.setText("Meta Age : " + ActofitObject.getString("metaage", ""));
        txthelthscore.setText("Health Score : " + ActofitObject.getString("helthscore", ""));

        txtpulse.setText("Pulse : " + OximeterObject.getString("pulse_rate", ""));
        txtoxygen.setText("Oxygen : " + OximeterObject.getString("body_oxygen", ""));

        txtname.setText("Name : " + PersonalObject.getString("name", ""));
        txtmobile.setText("Mobile Number : " + PersonalObject.getString("mobile_number", ""));

        txttempreture.setText(ThermometerObject.getString("data", ""));
        txtbp.setText("Blood Pressure : " + BPObject.getString("systolic", "") + " diastolic - " + BPObject.getString("diastolic", ""));


        TextView tvTitel = (TextView) dialog.findViewById(R.id.tvTitel);
        tvTitel.setWidth(iWidth);
        edtText = (EditText) dialog.findViewById(R.id.edtText);
        edtText.setText("Evolute Systems");
        Spinner spGFontsty = (Spinner) dialog.findViewById(R.id.spGFontsty);
        ArrayAdapter<String> arradGFontsty = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, entertext_font);
        arradGFontsty
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGFontsty.setAdapter(arradGFontsty);
        spGFontsty.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3) {
                // TODO Auto-generated method stub
                switch (position) {
                    case 0:
                        bFontStyle = Printer_GEN.FONT_LARGE_NORMAL;// (byte) 0x01;
                        break;
                    case 1:
                        bFontStyle = Printer_GEN.FONT_LARGE_BOLD;// (byte) 0x02;
                        break;
                    case 2:
                        bFontStyle = Printer_GEN.FONT_SMALL_NORMAL;// (byte) 0x03;
                        break;
                    case 3:
                        bFontStyle = Printer_GEN.FONT_SMALL_BOLD;// (byte) 0x04;
                        break;
                    case 4:
                        bFontStyle = Printer_GEN.FONT_UL_LARGE_NORMAL;// (byte)
                        // 0x05;
                        break;
                    case 5:
                        bFontStyle = Printer_GEN.FONT_UL_LARGE_BOLD;// (byte) 0x06;
                        break;
                    case 6:
                        bFontStyle = Printer_GEN.FONT_UL_SMALL_NORMAL;// (byte)
                        // 0x07;
                        break;
                    case 7:
                        bFontStyle = Printer_GEN.FONT_UL_SMALL_BOLD;// (byte) 0x08;
                        break;
                    case 8:
                        bFontStyle = Printer_GEN.FONT_180_LARGE_NORMAL;// (byte)
                        // 0x09;
                        break;
                    case 9:
                        bFontStyle = Printer_GEN.FONT_180_LARGE_BOLD;// (byte) 0x0A;
                        break;
                    case 10:
                        bFontStyle = Printer_GEN.FONT_180_SMALL_NORMAL;// (byte)
                        // 0x0B;
                        break;
                    case 11:
                        bFontStyle = Printer_GEN.FONT_180_SMALL_BOLD;// (byte) 0x0C;
                        break;
                    case 12:
                        bFontStyle = Printer_GEN.FONT_180_UL_LARGE_NORMAL;// (byte)
                        // 0x0D;
                        break;
                    case 13:
                        bFontStyle = Printer_GEN.FONT_180_UL_LARGE_BOLD;// (byte)
                        // 0x0E;
                        break;
                    case 14:
                        bFontStyle = Printer_GEN.FONT_180_UL_SMALL_NORMAL;// (byte)
                        // 0x0F;
                        break;
                    case 15:
                        bFontStyle = Printer_GEN.FONT_180_UL_SMALL_BOLD;// (byte)
                        // 0x10;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        Button btnOk = (Button) dialog.findViewById(R.id.btnGOk);
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sAddDataStr = edtText.getText().toString();
                if (sAddDataStr.length() == 0) {
                    ptrHandler.obtainMessage(2, "Clinics On Cloud").sendToTarget();
                } else if (sAddDataStr.length() > 0) {
                    dialog.dismiss();
                    EnterTextAsyc asynctask = new EnterTextAsyc();
                    asynctask.execute(0);
                }
            }
        });
        dialog.show();
    }

    /* This method shows the EnterTextAsyc AsynTask operation */
    public class EnterTextAsyc extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog untill background task is completed */
        @Override
        protected void onPreExecute() {
            dlgShowCustom(context, "Please Wait....");
            super.onPreExecute();
        }

        /* Task of EnterTextAsyc performing in the background */
        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                sAddDataStr = printString;
                ptrGen.iFlushBuf();
                String empty = sAddDataStr;
                ptrGen.iAddData(bFontStyle, empty);
                iRetVal = ptrGen.iStartPrinting(1);
                // if (D) Log.d(TAG,"<<<<<<<Enter Text value.....>>>>>>>>>" +
                // k);
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This displays the status messages of EnterTextAsyc in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            llprog.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
            edtText.setText("");
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

    /* This method shows the PrintBitmap AsynTask operation */
    public class PrintBitmap extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog untill background task is completed */
        @Override
        protected void onPreExecute() {
            dlgShowCustom(context, "Please Wait....");
            super.onPreExecute();
        }

        /* Task of PrintBitmap performing in the background */
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                if ((sPicturePath == "") || (sPicturePath == null)) {
                    Log.e("", "spicturepath is null");

                    if (!bGrayScale) {
                        iRetVal = ptrGen.iBmpPrint(context, R.drawable.logo1);
                    } else {
                        iRetVal = ptrGen.iGrayscalePrint(context, R.drawable.sampy1111);
                        Log.e("tag", "grayscale print...");
                        bGrayScale = false;
                    }
                } else if (sPicturePath.equals("invalid")) {
                    //dlgShow("invalid device");
                    iRetVal = INVALID_IMAGE;
                    sPicturePath = "";
                } else {
                    if (!bGrayScale) {
                        iRetVal = ptrGen.iBmpPrint(new File(sPicturePath));
                    } else {
                        iRetVal = ptrGen.iGrayscalePrint(new File(sPicturePath));
                        Log.e("tag", "grayscale print...");
                        bGrayScale = false;
                    }

                }
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This displays the status messages of PrintBitmap in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            llprog.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
            if (iRetVal == DEVICE_NOTCONNECTED) {
                ptrHandler.obtainMessage(1, "Device not connected")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.SUCCESS) {
                sPicturePath = "";
                ptrHandler.obtainMessage(1, "Printing Successfull")
                        .sendToTarget();
            } else if (iRetVal == INVALID_IMAGE) {
                ptrHandler.obtainMessage(1, "Invalid Image ").sendToTarget();
            } else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
                ptrHandler.obtainMessage(1, "Platen open").sendToTarget();
            } else if (iRetVal == Printer_GEN.PAPER_OUT) {
                ptrHandler.obtainMessage(1, "Paper out").sendToTarget();
            } else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
                ptrHandler.obtainMessage(1, "Printer at improper voltage")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.FAILURE) {
                ptrHandler.obtainMessage(1, "Print failed").sendToTarget();
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

    /* This method shows the BarCodePrint AsynTask operation */
    public class BarCodePrint extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog until background task is completed */
        @Override
        protected void onPreExecute() {
            dlgShowCustom(context, "Please Wait....");
            super.onPreExecute();
        }

        /* Task of BarCodePrint performing in the background */
        @SuppressLint("WrongThread")
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                ptrGen.iFlushBuf();
                sBarcodeStr = edtBarcode.getText().toString();
                iRetVal = ptrGen.iBarcodePrint(iBarcodeStyle, sBarcodeStr);
                Log.e(TAG, "Barcode result " + iRetVal);
                String empty = " \n" + " \n" + " \n" + " \n";
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This displays the status messages of BarCodePrint in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            llprog.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
            edtBarcode.setText("");
            if (iRetVal == DEVICE_NOTCONNECTED) {
                ptrHandler.obtainMessage(1, "Device not connected").sendToTarget();
            } else if (iRetVal == Printer_GEN.SUCCESS) {
                ptrHandler.obtainMessage(1, "Printing Successfull").sendToTarget();
                ptrGen.iPaperFeed();
            } else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
                ptrHandler.obtainMessage(1, "Platen open").sendToTarget();
            } else if (iRetVal == Printer_GEN.PAPER_OUT) {
                ptrHandler.obtainMessage(1, "Paper out").sendToTarget();
            } else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
                ptrHandler.obtainMessage(1, "Printer at improper voltage").sendToTarget();
            } else if (iRetVal == Printer_GEN.FAILURE) {
                ptrHandler.obtainMessage(1, "Print Failed").sendToTarget();
            } else if (iRetVal == Printer_GEN.PARAM_ERROR) {
                ptrHandler.obtainMessage(1, "Parameter error").sendToTarget();
            } else if (iRetVal == Printer_GEN.NO_RESPONSE) {
                ptrHandler.obtainMessage(1, "No response from Pride device").sendToTarget();
            } else if (iRetVal == Printer_GEN.DEMO_VERSION) {
                ptrHandler.obtainMessage(1, "Library in demo version").sendToTarget();
            } else if (iRetVal == Printer_GEN.INVALID_DEVICE_ID) {
                ptrHandler.obtainMessage(1, "Connected  device is not authenticated.").sendToTarget();
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

    /* This method shows the PaperFeed AsynTask operation */
    public class PaperFeed extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog untill background task is completed */
        @Override
        protected void onPreExecute() {
            dlgShowCustom(context, "Please Wait....");
            super.onPreExecute();
        }

        /* Task of PaperFeed performing in the background */
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                iRetVal = ptrGen.iPaperFeed();
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /* This displays the status messages of PaperFeed in the dialog box */
        @Override
        protected void onPostExecute(Integer result) {
            llprog.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
            if (iRetVal == DEVICE_NOTCONNECTED) {
                ptrHandler.obtainMessage(1, "Device not connected")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.SUCCESS) {
                ptrHandler.obtainMessage(1, "Paper feed Successfull")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
                ptrHandler.obtainMessage(1, "Platen open").sendToTarget();
            } else if (iRetVal == Printer_GEN.PAPER_OUT) {
                ptrHandler.obtainMessage(1, "Paper out").sendToTarget();
            } else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
                ptrHandler.obtainMessage(1, "Printer at improper voltage")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.FAILURE) {
                ptrHandler.obtainMessage(1, "Print Failed").sendToTarget();
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

    /* This method shows the Diagnose AsynTask operation */
    public class DiagnosePrint extends AsyncTask<Integer, Integer, Integer> {
        /* displays the progress dialog untill background task is completed */
        @Override
        protected void onPreExecute() {
            dlgShowCustom(context, "Please Wait....");
            super.onPreExecute();
        }

        /* Task of Diagnose performing in the background */
        @Override
        protected Integer doInBackground(Integer... params) {
            try {
                iRetVal = ptrGen.iDiagnose();
            } catch (NullPointerException e) {
                iRetVal = DEVICE_NOTCONNECTED;
                return iRetVal;
            }
            return iRetVal;
        }

        /*
         * This sends message to handler to display the status messages of
         * Diagnose in the dialog box
         */
        @Override
        protected void onPostExecute(Integer result) {
            llprog.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
            if (iRetVal == DEVICE_NOTCONNECTED) {
                ptrHandler.obtainMessage(1, "Device not connected")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.SUCCESS) {
                ptrHandler.obtainMessage(1, "Printer is in good condition")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.PLATEN_OPEN) {
                ptrHandler.obtainMessage(1, "Platen open").sendToTarget();
            } else if (iRetVal == Printer_GEN.PAPER_OUT) {
                ptrHandler.obtainMessage(1, "Paper out").sendToTarget();
            } else if (iRetVal == Printer_GEN.IMPROPER_VOLTAGE) {
                ptrHandler.obtainMessage(1, "Printer at improper voltage")
                        .sendToTarget();
            } else if (iRetVal == Printer_GEN.FAILURE) {
                ptrHandler.obtainMessage(1, "Print Failed").sendToTarget();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE) {// && resultCode ==
            // Activity.RESULT_OK && null !=
            // data) {
            try {
                uriSelectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uriSelectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                sPicturePath = cursor.getString(columnIndex);
                cursor.close();
                try {
                    bSelectImg = BitmapFactory.decodeFile(sPicturePath);
                    int ii = bSelectImg.getWidth();
                    int i = bSelectImg.getHeight();
                    Log.e(TAG, "Height " + i);
                    Log.e(TAG, "width " + ii);
                    if (bSelectImg == null || bSelectImg.getWidth() != 384 || bSelectImg.getHeight() > 1500) {
                        Log.e(TAG, "inside invalid");
                        sPicturePath = "invalid";
                    }
                    imgGalery.setImageBitmap(bSelectImg);
                } catch (OutOfMemoryError e) {
                    String str = "Image Size is not supported";
                    dlgShow(str);
                } catch (Exception e) {
                    dlgShow("Image Size is Large");
                }
            } catch (Exception e) {
                ptrHandler.obtainMessage(3,
                        "No Image Selected\nSelecting Default Image")
                        .sendToTarget();
                e.printStackTrace();
            }
        }
    }

    // Exit confirmation dialog box
    public void dlgExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        // set title
        alertDialogBuilder.setTitle("Pride Demo Application");
        // alertDialogBuilder.setIcon(R.drawable.icon);
        alertDialogBuilder
                .setMessage("Are you sure you want to exit Pride Demo application");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            BluetoothComm.mosOut = null;
                            BluetoothComm.misIn = null;
                        } catch (NullPointerException e) {
                        }
                        System.gc();
                        PrintPriviewScreen.this.finish();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dlgExit();
        }
        return super.onKeyDown(keyCode, event);
    }

    /* This performs Progress dialog box to show the progress of operation */
    protected void dlgShowCustom(Context con, String Message) {
        dlgCustomdialog = new Dialog(con);
        dlgCustomdialog.setTitle("Pride Demo");
        dlgCustomdialog.setCancelable(false);
        dlgCustomdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgCustomdialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlgCustomdialog.dismiss();
            }
        });
        dlgCustomdialog.show();
    }

    private void PostData() {
        pd = Tools.progressDialog(PrintPriviewScreen.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiUtils.PRINTPOST_URL,
                response -> {
                    //Disimissing the progress dialog
                    System.out.println("Login Response" + response);
                    try {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Data Uploaded on Server", Toast.LENGTH_SHORT).show();
                        dlgEnterText();

                    } catch (Exception e) {

                    }
                },
                volleyError -> {
                    pd.dismiss();
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
                params.put("oxygen", OximeterObject.getString("body_oxygen", ""));
                params.put("pulse", OximeterObject.getString("pulse_rate", ""));
                params.put("temperature", ThermometerObject.getString("data", ""));
                params.put("hemoglobin", HemoglobinObject.getString("hemoglobin", ""));
                params.put("sugar", BiosenseObject.getString("last", ""));
                return params;
            }

        };
        AndMedical_App_Global.getInstance().addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                90000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
