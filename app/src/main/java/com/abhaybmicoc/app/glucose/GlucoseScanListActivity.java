package com.abhaybmicoc.app.glucose;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import android.app.Activity;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.view.GestureDetector;
import android.content.DialogInterface;
import android.speech.tts.TextToSpeech;
import android.content.pm.PackageManager;
import android.support.v7.widget.Toolbar;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.hemoglobin.MainActivity;
import com.abhaybmicoc.app.glucose.adapters.ScanList;
import com.abhaybmicoc.app.glucose.models.ResultsModel;

import org.maniteja.com.synclib.helper.Util;
import org.maniteja.com.synclib.helper.HelperC;

import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class GlucoseScanListActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    // region Variables

    private int flag;

    private boolean mScanning;
    private boolean autoConnectFlag = false;

    String deviceAddress;
    private String txt = "";

    private static final long SCAN_PERIOD = 20000;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private Util util;
    private Handler mHandler;
    private ScanList adapter;
    private ImageView ivScanImage;

    private TextView tvScanningText;

    private Button btnGo;
    private Button btnScanList;

    private Toolbar toolBar;
    private RecyclerView recyclerView;

    private TextToSpeech textToSpeech;
    private BluetoothAdapter mBluetoothAdapter;

    private List<ResultsModel> resultsListforAdapter = new ArrayList<>();

    // endregion

    // Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        setupEvents();
        initializeData();
    }

    @Override
    public void onInit(int status) {
        startTextToSpeech(status);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        refreshScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED)
        {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        clearScan();
        stopTextToSpeech();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_COARSE_LOCATION:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //Log.d(TAG, "coarse location permission granted");
                } else {
                    showNoLocationAccessDialog();
                }

                return;
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = (bluetoothDevice, rssi, scanRecord) -> handleScanCallback(bluetoothDevice);

    /**
     *
     * @param device
     */
    private void handleScanCallback(BluetoothDevice device) {
        if (device != null)
        {
            if ("SyncPlus".equals(device.getName()) || util.readString(HelperC.key_devname, "SyncPlus").equals(device.getName()))
            {
                adapter.addDevice(device);
                adapter.notifyDataSetChanged();
                ivScanImage.setVisibility(View.GONE);

                if (autoConnectFlag)
                {
                    if (device == null)
                    {
                        System.out.println("Scan List :inside  ");
                        return;
                    }

                    if (deviceAddress.equals(device.getAddress()))
                    {
                        if (flag != 2)
                        {
                            final Intent intent = new Intent(getApplicationContext(), ActivityGlucose.class);
                            intent.putExtra(HelperC.EXTRAS_DEVICE_NAME, device.getName());
                            intent.putExtra(HelperC.EXTRAS_DEVICE_ADDRESS, device.getAddress());

                            if (mScanning)
                            {
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                mScanning = false;
                            }

                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }
        }
    }

    // endregion

    // region Initialization methods

    /**
     *
     */
    private void setupUI(){
        setContentView(R.layout.activity_scan_list);

        /* Setup toolbar */
        toolBar = findViewById(R.id.toolbar);
        TextView mTitle = toolBar.findViewById(R.id.title);
        mTitle.setText(getResources().getString(R.string.scandevices));

        setSupportActionBar(toolBar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mHandler = new Handler();
        recyclerView = findViewById(R.id.resultsrecycler);

        btnGo = findViewById(R.id.btnGo);
        btnScanList = findViewById(R.id.btnScan);

        ivScanImage = findViewById(R.id.imageview);

        tvScanningText = findViewById(R.id.scaningtext);
    }

    /**
     *
     */
    private void setupEvents(){
        btnGo.setOnClickListener(view -> handleGo());
        btnScanList.setOnClickListener(view -> handleScanList());

        recyclerView.addOnItemTouchListener(new RecyclerTouchListner(getApplicationContext(), recyclerView, new ClickListner()
        {
            @Override
            public void onClick(View view, int position)
            {
                handleRecyclerClick(view, position);
            }

            @Override
            public void onLongClick(View view, int position)
            {
            }
        }));
    }

    /**
     *
     */
    private void initializeData(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            /*if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(dialog -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }*/
        }
        //}


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textToSpeech = new TextToSpeech(getApplicationContext(),this);

        flag = getIntent().getIntExtra("flag", -1);

        ivScanImage.setVisibility(View.GONE);
        tvScanningText.setText("Scaning Started");

        util = new Util(this, this);
        deviceAddress = util.readString(HelperC.key_autoconnectaddress, "");
        autoConnectFlag = util.readboolean(HelperC.key_autoconnectflag, false);

        txt = "Please long press the device bluetooth button and click on Sync Plus";
        speakOut(txt);
    }

    /**
     *
     */
    private void handleGo(){
        Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(objIntent);
    }

    /**
     *
     */
    private void handleScanList(){
        if (btnScanList.getText().toString().equals("Scan"))
        {
            btnScanList.setText("Stop Scan");
            tvScanningText.setText("Scaning Started");
            scanLeDevice(true);
        } else
        {
            btnScanList.setText("Scan");
            tvScanningText.setText("Scaning Stopped");
            scanLeDevice(false);
        }
    }

    /**
     *
     * @param view
     * @param position
     */
    private void handleRecyclerClick(View view, int position){
        TextView name = view.findViewById(R.id.btname);
        try
        {

            final BluetoothDevice device = adapter.getDevice(position);
            if (device == null)
            {
                return;
            }

            util.putString(HelperC.key_autoconnecbtname, name.getText().toString());
            util.putString(HelperC.key_autoconnectaddress, device.getAddress());
            util.putBoolean(HelperC.key_autoconnectflag, true);

            final Intent intent = new Intent(getApplicationContext(), ActivityGlucose.class);
            intent.putExtra(HelperC.EXTRAS_DEVICE_NAME, name.getText().toString());
            intent.putExtra(HelperC.EXTRAS_DEVICE_ADDRESS, device.getAddress());

            if (mScanning)
            {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                mScanning = false;
            }

            startActivity(intent);
            finish();
        } catch (Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please scan once again.", Toast.LENGTH_SHORT).show();
        }
    }

    // endregion

    // region Logical methods

    /**
     *
     * @param text
     */
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    /**
     *
     * @param status
     */
    private void startTextToSpeech(int status){
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                speakOut(txt);
            }

        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }

    /**
     *
     */
    private void stopTextToSpeech(){
        /* close the textToSpeech engine to avoid the runtime exception from it */
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }
    }

    /**
     *
     */
    private void clearScan(){
        scanLeDevice(false);

        try
        {
            adapter.clear();
            //   unregisterReceiver(mReceiver);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please scan once again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     */
    private void refreshScan(){
        try{
            // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
            // fire an intent to display a dialog asking the user to grant permission to enable it.
            if (!mBluetoothAdapter.isEnabled())
            {
                if (!mBluetoothAdapter.isEnabled())
                {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
            }

            /* Initializes list view adapter */
            adapter = new ScanList(getApplicationContext(), resultsListforAdapter, true, 1, GlucoseScanListActivity.this);
            recyclerView.setAdapter(adapter);

            LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getApplicationContext()); // (Context context, int spanCount)
            mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);

            recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            scanLeDevice(true);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please scan once again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * @param enable
     */
    private void scanLeDevice(final boolean enable)
    {
        if (enable)
        {
            // Stops scanning after a pre-defined scan period.

            ivScanImage.setVisibility(View.VISIBLE);
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mScanning = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                    {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        tvScanningText.setText("Scaning Stopped");
                        btnScanList.setText("Scan");
                    }
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                btnScanList.setText("Stop Scan");
            }
        } else {
            mScanning = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                tvScanningText.setText("Scaning Stopped");
                btnScanList.setText("Scan");
            }

        }
        invalidateOptionsMenu();
    }

    /**
     *
     */
    private void showNoLocationAccessDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Functionality limited");
        builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
        builder.setPositiveButton(android.R.string.ok, null);

        builder.setOnDismissListener(dialogInterface -> {});

        builder.show();
    }

    // endregion

    // region Nested classes

    public static interface ClickListner
    {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListner implements RecyclerView.OnItemTouchListener
    {
        private GestureDetector gestureDetector;
        private ClickListner clickListner;

        public RecyclerTouchListner(Context context, final RecyclerView recyclerView, final ClickListner clickListner)
        {
            this.clickListner = clickListner;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener()
            {
                @Override
                public boolean onSingleTapUp(MotionEvent e)
                {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e)
                {
                    super.onLongPress(e);
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListner != null)
                    {
                        clickListner.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
        {
            View child = rv.findChildViewUnder(e.getX(), e.getY());

            if (child != null && clickListner != null && gestureDetector.onTouchEvent(e))
            {
                clickListner.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e)
        {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
        {

        }
    }

    // endregion
}
