package com.abhaybmicoc.app.glucose;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.glucose.adapters.ScanList;
import com.abhaybmicoc.app.glucose.models.ResultsModel;
import com.abhaybmicoc.app.hemoglobin.MainActivity;

import org.maniteja.com.synclib.helper.HelperC;
import org.maniteja.com.synclib.helper.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Activity_ScanList extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 20000;
    List<ResultsModel> resultsListforAdapter = new ArrayList<>();
    private RecyclerView recyclerView;
    private ScanList adapter;
    Util util;
    Context c;
    Button btnScanList, btnGo;
    String defname, defAddress;
    ImageView scanimage;
    boolean autocoFlag = false;
    int flag;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    TextView scaningtext;

    boolean isCampSelected = false;
    private TextToSpeech tts;
    private String txt = "";

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    //disable the back button
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_list);

        tts = new TextToSpeech(this,this);

        flag = getIntent().getIntExtra("flag", -1);
        util = new Util(this, this);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        TextView mTitle = (TextView) toolBar.findViewById(R.id.title);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText(getResources().getString(R.string.scandevices));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        defname = util.readString(HelperC.key_autoconnecbtname, "");
        defAddress = util.readString(HelperC.key_autoconnectaddress, "");
        autocoFlag = util.readboolean(HelperC.key_autoconnectflag, false);
        mHandler = new Handler();
        recyclerView = (RecyclerView) findViewById(R.id.resultsrecycler);
        btnScanList = (Button) findViewById(R.id.btnScan);
        btnGo = (Button) findViewById(R.id.btnGo);
        scanimage = (ImageView) findViewById(R.id.imageview);
        scanimage.setVisibility(View.GONE);

        txt = "Please long press the device bluetooth button and click on Sync Plus";
        speakOut(txt);

        scaningtext = (TextView) findViewById(R.id.scaningtext);
        scaningtext.setText("Scaning Started");

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(objIntent);
            }
        });



        btnScanList.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btnScanList.getText().toString().equals("Scan"))
                {
                    btnScanList.setText("Stop Scan");
                    scaningtext.setText("Scaning Started");
                    scanLeDevice(true);
                } else
                {
                    btnScanList.setText("Scan");
                    scaningtext.setText("Scaning Stopped");
                    scanLeDevice(false);
                }
            }
        });

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
        recyclerView.addOnItemTouchListener(new RecyclerTouchListner(getApplicationContext(), recyclerView, new ClickListner()
        {
            @Override
            public void onClick(View view, int position)
            {
                TextView name = (TextView) view.findViewById(R.id.btname);
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
                    final Intent intent = new Intent(getApplicationContext(), Activity_Home.class);
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

            @Override
            public void onLongClick(View view, int position)
            {

            }
        }));
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        tts = new TextToSpeech(this,this);
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

            // Initializes list view adapter.
            adapter = new ScanList(getApplicationContext(), resultsListforAdapter, true, 1, Activity_ScanList.this);
            recyclerView.setAdapter(adapter);
            LinearLayoutManager mLinearLayoutManagerVertical = new LinearLayoutManager(getApplicationContext()); // (Context context, int spanCount)
            mLinearLayoutManagerVertical.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(mLinearLayoutManagerVertical);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            scanLeDevice(true);
        } catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Please scan once again.", Toast.LENGTH_SHORT).show();
        }
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
        scanLeDevice(false);

        try
        {
            adapter.clear();
            //   unregisterReceiver(mReceiver);
        } catch (Exception e)
        {
            Toast.makeText(getApplicationContext(), "Please scan once again.", Toast.LENGTH_SHORT).show();
        }

        //closing the text to speach object to avoid the runtime exception
        try {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }catch (Exception e){
            System.out.println("onPauseException"+e.getMessage());
        }
    }

    private void scanLeDevice(final boolean enable)
    {
        if (enable)
        {
            // Stops scanning after a pre-defined scan period.

            scanimage.setVisibility(View.VISIBLE);
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mScanning = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                    {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        scaningtext.setText("Scaning Stopped");
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
        } else
        {
            mScanning = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                scaningtext.setText("Scaning Stopped");
                btnScanList.setText("Scan");
            }
        }
        invalidateOptionsMenu();
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback()
            {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord)
                {
                    runOnUiThread(new Runnable()
                                  {
                                      @Override
                                      public void run()
                                      {
                                          if (device != null)
                                          {
                                              Log.i("SCANNING ", util.readString(HelperC.key_devname, "Sync"));
                                              System.out.println("SCANNING " + util.readString(HelperC.key_devname, "Sync"));
                                              if ("SyncPlus".equals(device.getName()) || util.readString(HelperC.key_devname, "SyncPlus").equals(device.getName()))
                                              {
                                                  System.out.println("Scan List : " + device.getAddress());
                                                  adapter.addDevice(device);
                                                  adapter.notifyDataSetChanged();
                                                  scanimage.setVisibility(View.GONE);
                                                  if (autocoFlag)
                                                  {
                                                      if (device == null)
                                                      {
                                                          System.out.println("Scan List :inside  ");
                                                          return;
                                                      }
                                                      if (defAddress.equals(device.getAddress()))
                                                      {
                                                          if (flag != 2)
                                                          {
                                                              final Intent intent = new Intent(getApplicationContext(), Activity_Home.class);
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
                                  }

                    );
                }
            };

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
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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

    public static interface ClickListner
    {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_COARSE_LOCATION:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //Log.d(TAG, "coarse location permission granted");
                } else
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {

                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {

                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
}
