package com.abhaybmicoc.app.oxygen;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import main.java.com.abhaybmicoc.app.screen.BpLoginScreen;
import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.hemoglobin.MainActivity;
import com.abhaybmicoc.app.oxygen.ble.BleController;
import com.abhaybmicoc.app.oxygen.data.Const;
import com.abhaybmicoc.app.oxygen.data.DataParser;
import com.abhaybmicoc.app.oxygen.dialog.DeviceListAdapter;
import com.abhaybmicoc.app.oxygen.dialog.SearchDevicesDialog;
import com.abhaybmicoc.app.oxygen.views.WaveformView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OxygenActivity extends AppCompatActivity implements BleController.StateListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.btnSearch) Button btnSearch;
    @BindView(R.id.tvStatus)  TextView tvStatus;
    @BindView(R.id.tvParams)  TextView tvResult;
    @BindView(R.id.wfvPleth)
    WaveformView wfvPleth;
    @BindView(R.id.etNewBtName) EditText etNewBtName;
    @BindView(R.id.llChangeName) LinearLayout llChangeName;

    private DataParser mDataParser;
    private BleController mBleControl;

    private SearchDevicesDialog mSearchDialog;
    private DeviceListAdapter mBtDevicesAdapter;
    private ArrayList<BluetoothDevice> mBtDevices = new ArrayList<>();
    private Button btnnext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oxygen_main);
        ButterKnife.bind(this);

        mDataParser = new DataParser(new DataParser.onPackageReceivedListener() {
            @Override
            public void onOxiParamsChanged(final DataParser.OxiParams params) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvResult.setText("SpO2: "+ params.getSpo2() + "   Pulse Rate:"+params.getPulseRate());
                        tvStatus.setText("98");
                    }
                });
            }
            @Override
            public void onPlethWaveReceived(final int amp) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wfvPleth.addAmp(amp);
                    }
                });
            }
        });
        mDataParser.start();

        mBleControl = BleController.getDefaultBleController(this);
        mBleControl.enableBtAdapter();
        mBleControl.bindService(this);

        mBtDevicesAdapter = new DeviceListAdapter(this,mBtDevices);
        mSearchDialog = new SearchDevicesDialog(this,mBtDevicesAdapter) {
            @Override
            public void onSearchButtonClicked() {
                mBtDevices.clear();
                mBtDevicesAdapter.notifyDataSetChanged();
                mBleControl.scanLeDevice(true);
            }

            @Override
            public void onClickDeviceItem(int pos) {
                BluetoothDevice device = mBtDevices.get(pos);
                tvStatus.setText("Name:"+device.getName()+"     "+"Mac:"+device.getAddress());
                mBleControl.connect(device);
                dismiss();
            }
        };

        btnnext = (Button) findViewById(R.id.btnnext);
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent objbp = new Intent(getApplicationContext(), BpLoginScreen.class);
                startActivity(objbp);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBleControl.registerBtReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBleControl.unregisterBtReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataParser.stop();
        mBleControl.unbindService(this);
        System.exit(0);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearch:
                /*if(!mBleControl.isConnected()){
                    mBleControl.scanLeDevice(true);
                    mSearchDialog.show();
                    mBtDevices.clear();
                    mBtDevicesAdapter.notifyDataSetChanged();
                }
                else {
                    mBleControl.disconnect();
                }*/
                Intent objbp = new Intent(getApplicationContext(), BpLoginScreen.class);
                startActivity(objbp);
                break;
            case R.id.tvGetSource:
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(Const.GITHUB_SITE)));
                break;
            case R.id.btnChangeName:
                mBleControl.changeBtName(etNewBtName.getText().toString());
                break;
        }
    }

    @Override
    public void onFoundDevice(final BluetoothDevice device) {
        if(!mBtDevices.contains(device)){
            mBtDevices.add(device);
            mBtDevicesAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onConnected() {
        btnSearch.setText("Disconnect");
        Toast.makeText(OxygenActivity.this, "Connected",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(OxygenActivity.this, "Disconnected",Toast.LENGTH_SHORT).show();
        btnSearch.setText("Search");
        llChangeName.setVisibility(View.GONE);
    }

    @Override
    public void onReceiveData(byte[] dat) {
        mDataParser.add(dat);
    }

    @Override
    public void onServicesDiscovered() {
        llChangeName.setVisibility(mBleControl.isChangeNameAvailable() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onScanStop() {
        mSearchDialog.stopSearch();
    }
}
