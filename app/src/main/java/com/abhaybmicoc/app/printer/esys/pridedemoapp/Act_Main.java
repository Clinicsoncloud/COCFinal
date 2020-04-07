package com.abhaybmicoc.app.printer.esys.pridedemoapp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.activity.PrintPreviewActivity;
import com.abhaybmicoc.app.entities.AndMedical_App_Global;
import com.abhaybmicoc.app.hemoglobin.MainActivity;
import com.abhaybmicoc.app.printer.evolute.bluetooth.BluetoothComm;
import com.abhaybmicoc.app.printer.evolute.bluetooth.BluetoothPair;
import com.abhaybmicoc.app.services.TextToSpeechService;
import com.abhaybmicoc.app.utils.ApiUtils;

import com.prowesspride.api.Setup;
import com.prowesspride.api.Printer_ESC;
import com.prowesspride.api.Printer_GEN;

/**
 * The main interface <br />
 * Â  * Maintain a connection with the Bluetooth communication operations,
 * check Bluetooth status after the first entry, did not start then turn on Bluetooth,
 * then immediately into the search interface. <br/>
 * Â  * The need to connect the device to get built on the main interface paired with a connection,
 * Bluetooth object is stored in globalPool so that other functional modules of different
 * communication modes calls.
 */

public class Act_Main extends Activity {
    /**
     * CONST: scan device menu id
     */
    public static AndMedical_App_Global mGP = null;
    public static BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
    public static BluetoothDevice mBDevice = null;
    private TextView mtvDeviceInfo = null;
    private TextView tvScanbt;
    private LinearLayout mllDeviceCtrl = null;
    private LinearLayout llSelectedDevicesLayout = null;
    private Button btnPair = null;
    private Button btnComm = null;
    private Button btnBack, btnScanbt, btnContinue;
    public static final byte REQUEST_DISCOVERY = 0x01;
    public static final byte REQUEST_ABOUT = 0x05;
    public static final int EXIT_ON_RETURN = 21;
    private Hashtable<String, String> mhtDeviceInfo = new Hashtable<String, String>();
    private boolean mblBonded = false;
    public static Printer_GEN prnGen;
    public static Printer_ESC prnEsc;
    public static boolean blResetBtnEnable = false;
    public final static String EXTRA_DEVICE_TYPE = "android.bluetooth.device.extra.DEVICE_TYPE";
    private boolean blBleStatusBefore = false;
    final Context context = this;
    public Dialog dlgRadioBtn, dlgSupport;
    public static ProgressDialog prgDialog;
    private String sTo, sSubject, sMessage, sDevicetype;
    private EditText edtTo, edtSubject, edtMessage;
    private ScrollView svScroll, svRadio;
    private RadioGroup rgProtocol;
    private RadioButton rbtnProtocol;
    ScaleAnimation scale;
    public static String TAG = "Act_Main";
    Context contextGlb;
    public Dialog dlgCustomdialog;
    public ProgressBar pbProgress;
    private LinearLayout llprog;
    private Button btnOk, btIcon;
    int iRetVal;
    public SharedPreferences preferences;
    private LinearLayout scanLL;

    InputStream input;// = BluetoothComm.misIn;
    OutputStream outstream;


    private BroadcastReceiver _mPairingRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothDevice device = null;
            if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED)
                    mblBonded = true;
                else
                    mblBonded = false;
            }
        }
    };

    private String WAITING_MSG = "Please wait while Calculating Result";

    private String SCAN_DEVICE_MSG = "Scan Printer for Connection";

    TextToSpeechService textToSpeechService;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        textToSpeechService.stopTextToSpeech();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        requestGPSPermission();

        svScroll = (ScrollView) findViewById(R.id.scroll);
        svRadio = (ScrollView) findViewById(R.id.redioscroll);
        btIcon = findViewById(R.id.btIcon);
        scanLL = findViewById(R.id.scanBTLL);

        mtvDeviceInfo = (TextView) findViewById(R.id.actMain_tv_device_info);
        mllDeviceCtrl = (LinearLayout) findViewById(R.id.actMain_ll_device_ctrl);
        llSelectedDevicesLayout = (LinearLayout) findViewById(R.id.actMain_ll_SelectedDevicesLayout);
        btnPair = (Button) findViewById(R.id.actMain_btn_pair);
        btnComm = (Button) findViewById(R.id.actMain_btn_conn);


        /*
         * This intent added for temp period only for testing to avoid connection printer
         */
        /*Intent tempintent = new Intent(Act_Main.this, PrintPreviewActivity.class);
        tempintent.putExtra("is_PrinterConnected", getIntent().getStringExtra("is_PrinterConnected"));
        startActivity(tempintent);*/

        llSelectedDevicesLayout.setVisibility(View.GONE);

        textToSpeechService = new TextToSpeechService(getApplicationContext(), SCAN_DEVICE_MSG);

        preferences = getSharedPreferences(ApiUtils.PREFERENCE_PERSONALDATA, MODE_PRIVATE);

        try {
            Act_GlobalPool.setup = new Setup();
            boolean activate = Act_GlobalPool.setup.blActivateLibrary(context, R.raw.licence_nodlg_prdgen);

            if (activate == true) {
                btnComm.setVisibility(View.VISIBLE);
            } else if (activate == false) {
                btnComm.setVisibility(View.GONE);
            }
        } catch (Exception e) {
        }
        if (null == mBT) {
            Toast.makeText(this, "Bluetooth module not found", Toast.LENGTH_LONG).show();
            finish();
        }


        btnBack = (Button) findViewById(R.id.back_but);
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlgExit();
            }
        });

        /*information*/
        Button btnInfo = (Button) findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlgInformationBox();
            }
        });

        /*help*/
        Button btnHelp = (Button) findViewById(R.id.btnHelp);
        btnHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dlgHelp();
            }
        });


        btIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showScanOption();
            }
        });


        /*scan bluetooth devices*/
        btnScanbt = (Button) findViewById(R.id.btnScanbt);
        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        btnScanbt.startAnimation(animScale);
        mGP = ((AndMedical_App_Global) getApplicationContext());
        btnScanbt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mGP != null) {
                    svRadio.setVisibility(View.INVISIBLE);
                    mGP.closeConn();
                    new StartBluetoothDeviceTask().execute("");
                } else {
                    new StartBluetoothDeviceTask().execute("");
                }
            }
        });

        /*scan bluetooth devices*/
        tvScanbt = (TextView) findViewById(R.id.tvScanbt);

        tvScanbt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGP != null) {
                    svRadio.setVisibility(View.INVISIBLE);
                    mGP.closeConn();
                    new StartBluetoothDeviceTask().execute("");

                } else {
                    new StartBluetoothDeviceTask().execute("");
                }
            }
        });

        try {

            SharedPreferences data = getSharedPreferences("printer", MODE_PRIVATE);

            if (data.getString("NAME", "").length() > 0) {

                svScroll.setVisibility(View.VISIBLE);
                scanLL.setVisibility(View.GONE);
                btIcon.setVisibility(View.VISIBLE);

                mllDeviceCtrl.setVisibility(View.VISIBLE);
                mhtDeviceInfo.put("NAME", data.getString("NAME", ""));
                mhtDeviceInfo.put("MAC", data.getString("MAC", ""));
                mhtDeviceInfo.put("COD", data.getString("COD", ""));
                mhtDeviceInfo.put("RSSI", data.getString("RSSI", ""));
                mhtDeviceInfo.put("DEVICE_TYPE", data.getString("DEVICE_TYPE", ""));
                mhtDeviceInfo.put("BOND", data.getString("BOND", ""));

                dlgShowDeviceInfo();

                if (mhtDeviceInfo.get("BOND").equals(getString(R.string.actDiscovery_bond_nothing))) {

                    llSelectedDevicesLayout.setVisibility(View.VISIBLE);
                    btnPair.setVisibility(View.VISIBLE);
                    btnComm.setVisibility(View.GONE);

                    onClickBtnPair(btnPair);
                } else {

                    mBDevice = mBT.getRemoteDevice(mhtDeviceInfo.get("MAC"));

                    llSelectedDevicesLayout.setVisibility(View.VISIBLE);
                    btnPair.setVisibility(View.GONE);
                    btnComm.setVisibility(View.VISIBLE);

                    Intent intent = new Intent(Act_Main.this, PrintPreviewActivity.class);
                    intent.putExtra("is_PrinterConnected", getIntent().getStringExtra("is_PrinterConnected"));
                    startActivity(intent);
                }
            }

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

    private void showScanOption() {
        if (scanLL.getVisibility() == View.GONE) {
            scanLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mGP.closeConn();
        if (null != mBT && !blBleStatusBefore) {
        }
    }

    @SuppressLint("StringFormatMatches")
    private void dlgShowDeviceInfo() {
        mtvDeviceInfo.setText(String.format(getString(R.string.actMain_device_info),
                mhtDeviceInfo.get("NAME"),
                mhtDeviceInfo.get("MAC"),
                mhtDeviceInfo.get("COD"),
                mhtDeviceInfo.get("RSSI"),
                mhtDeviceInfo.get("DEVICE_TYPE"),
                mhtDeviceInfo.get("BOND"))
        );
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        svScroll.setVisibility(View.VISIBLE);
        if (requestCode == REQUEST_DISCOVERY) {
            if (Activity.RESULT_OK == resultCode) {
                // Device is selected
                mllDeviceCtrl.setVisibility(View.VISIBLE);
                mhtDeviceInfo.put("NAME", data.getStringExtra("NAME"));
                mhtDeviceInfo.put("MAC", data.getStringExtra("MAC"));
                mhtDeviceInfo.put("COD", data.getStringExtra("COD"));
                mhtDeviceInfo.put("RSSI", data.getStringExtra("RSSI"));
                mhtDeviceInfo.put("DEVICE_TYPE", data.getStringExtra("DEVICE_TYPE"));
                mhtDeviceInfo.put("BOND", data.getStringExtra("BOND"));

                dlgShowDeviceInfo();

                SharedPreferences objdoctor = getSharedPreferences("printer", MODE_PRIVATE);
                SharedPreferences.Editor editor = objdoctor.edit();
                editor.putString("NAME", data.getStringExtra("NAME"));
                editor.putString("MAC", data.getStringExtra("MAC"));
                editor.putString("COD", data.getStringExtra("COD"));
                editor.putString("RSSI", data.getStringExtra("RSSI"));
                editor.putString("DEVICE_TYPE", data.getStringExtra("DEVICE_TYPE"));
                editor.putString("BOND", data.getStringExtra("BOND"));
                editor.commit();

                if (mhtDeviceInfo.get("BOND").equals(getString(R.string.actDiscovery_bond_nothing))) {

                    llSelectedDevicesLayout.setVisibility(View.VISIBLE);
                    btnPair.setVisibility(View.VISIBLE);
                    btnComm.setVisibility(View.GONE);
                } else {

                    llSelectedDevicesLayout.setVisibility(View.VISIBLE);
                    mBDevice = mBT.getRemoteDevice(mhtDeviceInfo.get("MAC"));
                    btnPair.setVisibility(View.GONE);
                    btnComm.setVisibility(View.VISIBLE);
                }
            } else if (Activity.RESULT_CANCELED == resultCode) {
                // None of device is selected
                finish();
            }
        } else if (requestCode == EXIT_ON_RETURN) {
            finish();
        }
    }

    /**
     * Pairing button click event
     *
     * @return void
     */
    public void onClickBtnPair(View v) {
        new PairTask().execute(mhtDeviceInfo.get("MAC"));
        btnPair.setEnabled(false);
    }

    /**
     * Connect button click event
     *
     * @return void
     */
    public void onClickBtnConn(View v) {

        if (scanLL.getVisibility() == View.VISIBLE) {
            scanLL.setVisibility(View.GONE);
            btIcon.setVisibility(View.VISIBLE);
        }

        // waiting msg
        textToSpeechService = new TextToSpeechService(getApplicationContext(), WAITING_MSG);

        new ConnSocketTask().execute(mBDevice.getAddress());
    }

    // Turn on Bluetooth of the device
    private class StartBluetoothDeviceTask extends AsyncTask<String, String, Integer> {
        private static final int RET_BULETOOTH_IS_START = 0x0001;
        private static final int RET_BLUETOOTH_START_FAIL = 0x04;
        private static final int miWATI_TIME = 15;
        private static final int miSLEEP_TIME = 150;
        private ProgressDialog mpd;

        @Override
        public void onPreExecute() {
            mpd = new ProgressDialog(Act_Main.this);
            mpd.setMessage(getString(R.string.actDiscovery_msg_starting_device));
            mpd.setCancelable(false);
            mpd.setCanceledOnTouchOutside(false);
            mpd.show();
            blBleStatusBefore = mBT.isEnabled();
        }

        @Override
        protected Integer doInBackground(String... arg0) {
            int iWait = miWATI_TIME * 1000;
            /* BT isEnable */
            if (!mBT.isEnabled()) {
                mBT.enable();
                //Wait miSLEEP_TIME seconds, start the Bluetooth device before you start scanning
                while (iWait > 0) {
                    if (!mBT.isEnabled())
                        iWait -= miSLEEP_TIME;
                    else
                        break;
                    SystemClock.sleep(miSLEEP_TIME);
                }
                if (iWait < 0)
                    return RET_BLUETOOTH_START_FAIL;
            }
            return RET_BULETOOTH_IS_START;
        }

        /**
         * After blocking cleanup task execution
         */
        @Override
        public void onPostExecute(Integer result) {
            if (mpd.isShowing())
                mpd.dismiss();
            if (RET_BLUETOOTH_START_FAIL == result) {
                // Turning ON Bluetooth failed
                AlertDialog.Builder builder = new AlertDialog.Builder(Act_Main.this);
                builder.setTitle(getString(R.string.dialog_title_sys_err));
                builder.setMessage(getString(R.string.actDiscovery_msg_start_bluetooth_fail));
                builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBT.disable();
                        finish();
                    }
                });
                builder.create().show();
            } else if (RET_BULETOOTH_IS_START == result) {
                Intent intent = new Intent(Act_Main.this, Act_BTDiscovery.class);
                startActivityForResult(intent, REQUEST_DISCOVERY);
            }
        }
    }

    /*   This method shows the PairTask  PairTask operation */
    private class PairTask extends AsyncTask<String, String, Integer> {
        /**
         * Constants: the pairing is successful
         */
        static private final int RET_BOND_OK = 0x00;
        /**
         * Constants: Pairing failed
         */
        static private final int RET_BOND_FAIL = 0x01;
        /**
         * Constants: Pairing waiting time (15 seconds)
         */
        static private final int iTIMEOUT = 1000 * 15;

        /**
         * Thread start initialization
         */
        @Override
        public void onPreExecute() {
            Toast.makeText(Act_Main.this, getString(R.string.actMain_msg_bluetooth_Bonding), Toast.LENGTH_SHORT).show();
            registerReceiver(_mPairingRequest, new IntentFilter(BluetoothPair.PAIRING_REQUEST));
            registerReceiver(_mPairingRequest, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        }

        /* Task of PairTask performing in the background*/
        @Override
        protected Integer doInBackground(String... arg0) {
            final int iStepTime = 150;
            int iWait = iTIMEOUT;
            try {
                mBDevice = mBT.getRemoteDevice(arg0[0]);//arg0[0] is MAC address
                BluetoothPair.createBond(mBDevice);
                mblBonded = false;
            } catch (Exception e1) {
                Log.d(getString(R.string.app_name), "create Bond failed!");
                e1.printStackTrace();
                return RET_BOND_FAIL;
            }
            while (!mblBonded && iWait > 0) {
                SystemClock.sleep(iStepTime);
                iWait -= iStepTime;
            }
            if (iWait > 0) {
                //RET_BOND_OK
            } else {
                //RET_BOND_FAIL
            }
            return (int) ((iWait > 0) ? RET_BOND_OK : RET_BOND_FAIL);
        }

        /* This displays the status messages of PairTask in the dialog box */
        @Override
        public void onPostExecute(Integer result) {
            unregisterReceiver(_mPairingRequest);
            if (RET_BOND_OK == result) {
                Toast.makeText(Act_Main.this, getString(R.string.actMain_msg_bluetooth_Bond_Success), Toast.LENGTH_SHORT).show();
                btnPair.setVisibility(View.GONE);
                btnComm.setVisibility(View.VISIBLE);
                mhtDeviceInfo.put("BOND", getString(R.string.actDiscovery_bond_bonded));
                dlgShowDeviceInfo();
            } else {
                Toast.makeText(Act_Main.this, getString(R.string.actMain_msg_bluetooth_Bond_fail), Toast.LENGTH_LONG).show();
                try {
                    BluetoothPair.removeBond(mBDevice);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                btnPair.setEnabled(true);
                new ConnSocketTask().execute(mBDevice.getAddress());
            }
        }
    }

    /*   This method shows the   PairTask operation */
    public class ConnSocketTask extends AsyncTask<String, String, Integer> {
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
            mpd = new ProgressDialog(Act_Main.this);
            mpd.setMessage(getString(R.string.actMain_msg_device_connecting));
            mpd.setCancelable(false);
            mpd.setCanceledOnTouchOutside(false);
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
            mpd.dismiss();

            if (CONN_SUCCESS == result) {
                btnComm.setVisibility(View.GONE);
                funContinue();
            } else {
                Toast.makeText(Act_Main.this, getString(R.string.actMain_msg_device_connect_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void funContinue() {

        try {
            try {
                SharedPreferences data = getSharedPreferences("printer", MODE_PRIVATE);
                if (data.getString("SerialNo", "").length() > 0) {
                    sDevicetype = data.getString("SerialNo", "");

                    Toast.makeText(Act_Main.this, "Serial No. is " + sDevicetype, Toast.LENGTH_LONG).show();
                    Intent printIntent = new Intent(getApplicationContext(), PrintPreviewActivity.class);
                    printIntent.putExtra("is_PrinterConnected", "true");
                    startActivityForResult(printIntent, EXIT_ON_RETURN);
                } else {

                    genGetSerialNo genSerial = new genGetSerialNo();
                    genSerial.execute(0);

                }
            } catch (Exception e) {

            }
            // get selected radio button from radioGroup
            int selectedId = rgProtocol.getCheckedRadioButtonId();
            rbtnProtocol = (RadioButton) findViewById(selectedId);
            if (rbtnProtocol.getText().equals("General Protocol")) {
                if (mGP.connection == true) {
                    try {

                        SharedPreferences data = getSharedPreferences("printer", MODE_PRIVATE);
                        if (data.getString("SerialNo", "").length() > 0) {
                            sDevicetype = data.getString("SerialNo", "");
                            Toast.makeText(Act_Main.this, "Serial No. is " + sDevicetype, Toast.LENGTH_LONG).show();
                            Intent printIntent = new Intent(getApplicationContext(), PrintPreviewActivity.class);
                            printIntent.putExtra("is_PrinterConnected", "true");

                            startActivityForResult(printIntent, EXIT_ON_RETURN);
                        } else {
                            genGetSerialNo genSerial = new genGetSerialNo();
                            genSerial.execute(0);
                        }
                    } catch (Exception e) {
                    }
                }
            } else if (rbtnProtocol.getText().equals("Esc Sequence Protocol")) {
                if (mGP.connection == true) {
                    escGetSerialNo escSerial = new escGetSerialNo();
                    escSerial.execute(0);
                }
            }
        } catch (Exception e) {
            String str = "Please Select a choice";
//			dlgShow(str);
        }

    }

    //Exit confirmation dialog box
    public void dlgExit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title
        alertDialogBuilder.setTitle("Pride Demo Application");
        //alertDialogBuilder.setIcon(R.drawable.icon);
        alertDialogBuilder.setMessage(
                "Are you sure you want to exit Clinics on Cloud application");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                try {
                    BluetoothComm.mosOut = null;
                    BluetoothComm.misIn = null;
                } catch (NullPointerException e) {
                }
                System.gc();
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    // display information dialog box
    public void dlgInformationBox() { //TODO
        Dialog alert = new Dialog(context);
        alert.getWindow();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // custom layout for information display
        alert.setContentView(R.layout.informationbox);
        TextView site_tv = (TextView) alert.findViewById(R.id.site_tv);
        String str_links = "<a href='http://www.evolute-sys.com'>www.evolute-sys.com</a><br />";
        site_tv.setLinksClickable(true);
        site_tv.setMovementMethod(LinkMovementMethod.getInstance());
        site_tv.setText(Html.fromHtml(str_links));
        alert.show();
    }

    // displays a dialog box for composing a mail
    public void dlgSupportEmail(String stEmailId) { //TODO
        Button buttonSend;
        Display display = getWindowManager().getDefaultDisplay();
        @SuppressWarnings("deprecation")
        int width = display.getWidth();
        dlgSupport = new Dialog(context);
        dlgSupport.getWindow();
        dlgSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgSupport.setContentView(R.layout.bdteamsupport);
        edtTo = (EditText) dlgSupport.findViewById(R.id.editTextTo);
        edtTo.setText(stEmailId);
        edtTo.setWidth(width);
        edtSubject = (EditText) dlgSupport.findViewById(R.id.editTextSubject);
        edtMessage = (EditText) dlgSupport.findViewById(R.id.editTextMessage);
        buttonSend = (Button) dlgSupport.findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sTo = edtTo.getText().toString();
                sSubject = edtSubject.getText().toString();
                sMessage = edtMessage.getText().toString();
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{sTo});
                email.putExtra(Intent.EXTRA_SUBJECT, sSubject);
                email.putExtra(Intent.EXTRA_TEXT, sMessage);
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                dlgSupport.cancel();
            }
        });
        dlgSupport.show();
    }

    Dialog helpdialog;

    //displays a dialog box for composing a mail
    public void dlgHelp() {
        helpdialog = new Dialog(context);
        helpdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        helpdialog.setContentView(R.layout.dlghelp);
        TextView supportteam_tv = (TextView) helpdialog.findViewById(R.id.supportteam_tv);
        String supportteam_links = "<a href='http://supportteam'>Support team</a>";

        supportteam_tv.setText(Html.fromHtml(supportteam_links));
        supportteam_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgSupportEmail("support@evolute-sys.com");
                helpdialog.cancel();
            }
        });

        TextView feedbck_tv = (TextView) helpdialog.findViewById(R.id.feedbck_tv);
        String feedback_links = "<a href='http://feedback'>Feedback</a>";

        feedbck_tv.setText(Html.fromHtml(feedback_links));
        feedbck_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgSupportEmail("sales@evolute-sys.com");

            }
        });
        helpdialog.show();
    }

    class escGetSerialNo extends AsyncTask<Integer, Integer, String> {

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
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            mpd = new ProgressDialog(Act_Main.this);
            mpd.setMessage("Please wait...");
            mpd.setCancelable(false);
            mpd.setCanceledOnTouchOutside(false);
            mpd.show();
            //super.onPreExecute();
        }

        @Override
        protected String doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            try {
                try {
                    input = BluetoothComm.misIn;
                    outstream = BluetoothComm.mosOut;

                    prnEsc = new Printer_ESC(Act_GlobalPool.setup, outstream, input);
                } catch (Exception e) {
                }

                sDevicetype = prnEsc.sGetDeviceInfo(Printer_ESC.DEVICE_SERIAL_NUMBER);

                SharedPreferences objdoctor = getSharedPreferences("printer", MODE_PRIVATE);
                SharedPreferences.Editor editor = objdoctor.edit();
                editor.putString("SerialNo", sDevicetype);
                editor.commit();

                Toast.makeText(Act_Main.this, "Serial No. is " + sDevicetype, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }

            return sDevicetype;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            {
                //SystemClock.sleep(2000);
                Toast.makeText(Act_Main.this, "Serial No. is " + sDevicetype, Toast.LENGTH_LONG).show();
            }
            mpd.dismiss();
        }
    }

    class genGetSerialNo extends AsyncTask<Integer, Integer, String> {

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
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            mpd = new ProgressDialog(Act_Main.this);
            mpd.setMessage("Please wait...");
            mpd.setCancelable(false);
            mpd.setCanceledOnTouchOutside(false);
            mpd.show();

        }

        @Override
        protected String doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            try {
                try {
                    input = BluetoothComm.misIn;
                    outstream = BluetoothComm.mosOut;

                    prnGen = new Printer_GEN(Act_GlobalPool.setup, outstream, input);
                } catch (Exception e) {
                }
                sDevicetype = prnGen.sGetSerialNumber();

                SharedPreferences objdoctor = getSharedPreferences("printer", MODE_PRIVATE);
                SharedPreferences.Editor editor = objdoctor.edit();
                editor.putString("SerialNo", sDevicetype);
                editor.commit();
            } catch (Exception e) {
                // TODO Auto-generated catch block
            }

            return sDevicetype;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub

            Toast.makeText(Act_Main.this, "Serial No. is " + sDevicetype, Toast.LENGTH_LONG).show();
            Intent protocol8a = new Intent(Act_Main.this, PrintPreviewActivity.class);
            protocol8a.putExtra("is_PrinterConnected", "true");

            startActivityForResult(protocol8a, EXIT_ON_RETURN);

            mpd.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//                Disable the exit dialog in print screen
//            dlgExit();
        }
        return super.onKeyDown(keyCode, event);
    }

    /*  To show response messages  */
    public void dlgShow(String str) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Pride Demo Application");
        alertDialogBuilder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        /* create alert dialog*/
        AlertDialog alertDialog = alertDialogBuilder.create();
        /* show alert dialog*/
        alertDialog.show();

    }

}
