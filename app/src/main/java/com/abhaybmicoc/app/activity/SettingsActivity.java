package com.abhaybmicoc.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhaybmicoc.app.R;
import com.abhaybmicoc.app.adapter.ConnectedDevicesListAdapter;
import com.abhaybmicoc.app.interfaces.RvClickListener;
import com.abhaybmicoc.app.screen.OtpLoginScreen;
import com.abhaybmicoc.app.utils.ApiUtils;

import org.json.JSONArray;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity implements RvClickListener {

    private Context context = SettingsActivity.this;
    private RecyclerView rv_ConnectedDevicesList;

    //    private ArrayList<String> devicesList;
    private JSONArray devicesArray;
    protected JSONObject deviceObj;
    private TextView tvSelectedDevice, tvDeviceName, tvDeviceAddress, tvDeviceNotConnected;
    private Button btnRemoveDevice;
    private ImageView iv_Back;

    private SharedPreferences spDeviceAddress;
    private LinearLayout llDeviceDetailsLayout;
    private String selectedDevice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        initializeData();
        setupEvents();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(context, OtpLoginScreen.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupUI() {
        setContentView(R.layout.activity_settings);

        rv_ConnectedDevicesList = findViewById(R.id.rv_ConnectedDevicesList);
        tvSelectedDevice = findViewById(R.id.tv_SelectedDevice);
        tvDeviceName = findViewById(R.id.tv_DeviceName);
        tvDeviceAddress = findViewById(R.id.tv_DeviceAddress);
        btnRemoveDevice = findViewById(R.id.btn_RemoveDevice);
        tvDeviceNotConnected = findViewById(R.id.tv_DeviceNotConnected);
        llDeviceDetailsLayout = findViewById(R.id.ll_DeviceDetailsLayout);
        iv_Back = findViewById(R.id.iv_Back);

    }

    private void initializeData() {
        try {
            devicesArray = new JSONArray();

            deviceObj = new JSONObject();
            deviceObj.put("device_name", "Height Sensor");
            deviceObj.put("is_selected", "1");
            devicesArray.put(deviceObj);

            deviceObj = new JSONObject();
            deviceObj.put("device_name", "Thermometer");
            deviceObj.put("is_selected", "0");
            devicesArray.put(deviceObj);

            deviceObj = new JSONObject();
            deviceObj.put("device_name", "Sugar");
            deviceObj.put("is_selected", "0");
            devicesArray.put(deviceObj);

            deviceObj = new JSONObject();
            deviceObj.put("device_name", "Hemoglobin");
            deviceObj.put("is_selected", "0");
            devicesArray.put(deviceObj);

            deviceObj = new JSONObject();
            deviceObj.put("device_name", "Printer");
            deviceObj.put("is_selected", "0");
            devicesArray.put(deviceObj);

            setConnectedDevicesAdapter();

            showHeightSensorDetails();

        } catch (Exception e) {
        }
    }

    private void setupEvents() {

        btnRemoveDevice.setOnClickListener(view -> removeDevice());
        iv_Back.setOnClickListener(view -> goToBackScreen());

    }

    private void goToBackScreen() {
        startActivity(new Intent(context, OtpLoginScreen.class));
        finish();
    }


    private void setConnectedDevicesAdapter() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);
        rv_ConnectedDevicesList.setLayoutManager(gridLayoutManager);
        ConnectedDevicesListAdapter adapter = new ConnectedDevicesListAdapter(context, devicesArray);
        rv_ConnectedDevicesList.setAdapter(adapter);
        adapter.setRvClickListener(this);
    }

    @Override
    public void rv_click(int position, int value, String key) {

        selectedDevice = key;
        if (key.equals("Height Sensor")) {
            showHeightSensorDetails();
            updateArray(key);
        } else if (key.equals("Thermometer")) {
            showThermometerDetails();
            updateArray(key);
        } else if (key.equals("Sugar")) {
            showSugarDetails();
            updateArray(key);
        } else if (key.equals("Hemoglobin")) {
            showHemoglobinDetails();
            updateArray(key);
        } else if (key.equals("Printer")) {
            showPrinterDetails();
            updateArray(key);
        }
    }

    private void updateArray(String key) {

        for (int i = 0; i < devicesArray.length(); i++) {
            try {

                if (devicesArray.getJSONObject(i).getString("device_name").equals(key))
                    devicesArray.getJSONObject(i).put("is_selected", "1");
                else
                    devicesArray.getJSONObject(i).put("is_selected", "0");

            } catch (Exception e) {
            }
        }
        setConnectedDevicesAdapter();
    }

    private void showHeightSensorDetails() {
        tvSelectedDevice.setText("Height Sensor");
        spDeviceAddress = getSharedPreferences(ApiUtils.AUTO_CONNECT, MODE_PRIVATE);

        if (!spDeviceAddress.getString("hcbluetooth", "").equals("")) {

            llDeviceDetailsLayout.setVisibility(View.VISIBLE);
            tvDeviceNotConnected.setVisibility(View.GONE);
            tvDeviceAddress.setText(spDeviceAddress.getString("hcbluetooth", ""));
            tvDeviceName.setText("HC-05");
        } else {
            llDeviceDetailsLayout.setVisibility(View.GONE);
            tvDeviceNotConnected.setVisibility(View.VISIBLE);
        }
    }

    private void showThermometerDetails() {
        tvSelectedDevice.setText("Thermometer");

        spDeviceAddress = getSharedPreferences(ApiUtils.THERMOMETER_AUTO_CONNECT, MODE_PRIVATE);
        if (!spDeviceAddress.getString("hcthermometer", "").equals("")) {

            llDeviceDetailsLayout.setVisibility(View.VISIBLE);
            tvDeviceNotConnected.setVisibility(View.GONE);
            tvDeviceAddress.setText(spDeviceAddress.getString("hcthermometer", ""));
            tvDeviceName.setText(spDeviceAddress.getString("hcthermometerName", ""));
        } else {
            llDeviceDetailsLayout.setVisibility(View.GONE);
            tvDeviceNotConnected.setVisibility(View.VISIBLE);
        }
    }

    private void showSugarDetails() {
        spDeviceAddress = getSharedPreferences("glucose_device_data", MODE_PRIVATE);

        if (!spDeviceAddress.getString("glucoseDeviceAddress", "").equals("")) {
            Log.e("SugarDevice", ":" + spDeviceAddress.getString("glucoseDeviceAddress", "") + " :spDeviceAddress: " + spDeviceAddress);
            tvDeviceNotConnected.setVisibility(View.GONE);
            llDeviceDetailsLayout.setVisibility(View.VISIBLE);

            tvDeviceAddress.setText(spDeviceAddress.getString("glucoseDeviceAddress", ""));
            tvDeviceName.setText(spDeviceAddress.getString("glucoseDeviceName", ""));
        } else {
            llDeviceDetailsLayout.setVisibility(View.GONE);
            tvDeviceNotConnected.setVisibility(View.VISIBLE);
        }
    }


    private void showHemoglobinDetails() {
        spDeviceAddress = getSharedPreferences("device_data", MODE_PRIVATE);

        if (!spDeviceAddress.getString("deviceName", "").equals("")) {
            Log.e("HemoglobinDevice", ":" + spDeviceAddress.getString("deviceName", "") + " :spDeviceAddress: " + spDeviceAddress);
            llDeviceDetailsLayout.setVisibility(View.VISIBLE);
            tvDeviceNotConnected.setVisibility(View.GONE);

            tvDeviceAddress.setText(spDeviceAddress.getString("deviceName", ""));
            tvDeviceName.setText(spDeviceAddress.getString("deviceAddress", ""));
        } else {
            llDeviceDetailsLayout.setVisibility(View.GONE);
            tvDeviceNotConnected.setVisibility(View.VISIBLE);
        }
    }

    private void showPrinterDetails() {
        spDeviceAddress = getSharedPreferences("printer", MODE_PRIVATE);

        if (!spDeviceAddress.getString("NAME", "").equals("")) {
            llDeviceDetailsLayout.setVisibility(View.VISIBLE);
            tvDeviceNotConnected.setVisibility(View.GONE);

            tvDeviceName.setText(spDeviceAddress.getString("NAME", ""));
            tvDeviceAddress.setText(spDeviceAddress.getString("MAC", ""));
        } else {
            llDeviceDetailsLayout.setVisibility(View.GONE);
            tvDeviceNotConnected.setVisibility(View.VISIBLE);
        }
    }

    private void removeDevice() {
        spDeviceAddress.edit().clear().apply();

        if (selectedDevice.equals("Height Sensor")) {
            showHeightSensorDetails();
        } else if (selectedDevice.equals("Thermometer")) {
            showThermometerDetails();
        } else if (selectedDevice.equals("Sugar")) {
            showSugarDetails();
        } else if (selectedDevice.equals("Hemoglobin")) {
            showHemoglobinDetails();
        } else if (selectedDevice.equals("Printer")) {
            showPrinterDetails();
        }

    }

}

