package com.abhaybmicoc.app;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.abhaybmicoc.app.base.ADInstructionActivity;
import com.abhaybmicoc.app.gatt.BleConnectService;


public class InstructionActivity extends ADInstructionActivity {

	// One-Time Flag
	private boolean isShowPairing = false;
	private boolean isShowDialog = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		if (mDeviceScanMode == DEVICE_SCAN_MODE_WS) {
			setDialogImageWithResourceID(R.drawable.ws_pairing);
			setDialogBackGroundColorWithResorceID(R.color.dashboard_theme_color_weightscale);
			setDialogMessageWithResorceID(R.string.paring_message_weight_scale);

		} else if (mDeviceScanMode == DEVICE_SCAN_MODE_BP) {
            setDialogBackGroundColorWithResorceID(R.color.white);
			setDialogMessageWithResorceID(R.string.paring_message_bloodpressure_monitor);

		} else if (mDeviceScanMode == DEVICE_SCAN_MODE_AM) {
			setDialogBackGroundColorWithResorceID(R.color.dashboard_theme_color_activitymonitor);
			setDialogMessageWithResorceID(R.string.paring_message_activity_monitor);


        } else if (mDeviceScanMode == DEVICE_SCAN_MODE_TM) {
			setDialogImageWithResourceID(R.drawable.th_pairing);
			setDialogBackGroundColorWithResorceID(R.color.thermometer_theme_color);
			setDialogMessageWithResorceID(R.string.paring_message_thermometer);
		}else if (mDeviceScanMode == DEVICE_SCAN_MODE_AM_UW) {
		}
	}
	
	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		if(mBleService != null) {
			mBleService.startScanDevices();
		}
	}
	
	@Override
	protected void onFindConnectDevice(String address) {
		super.onFindConnectDevice(address);

		if(mBleService != null) {
			mBleService.stopScanDevice();
		}

		if(!isShowPairing) {
			isShowPairing = true;

			BluetoothDevice device = null;
			device = BleConnectService.getBluetoothDevice(InstructionActivity.this,address);

			if (device != null) {
				if (mDeviceScanMode == DEVICE_SCAN_MODE_WS) {
					SharedPreferences.Editor editor = getSharedPreferences(
							"ANDMEDICAL", MODE_PRIVATE).edit();
					editor.putString("weightdeviceid",
							"" + device.getUuids());
					editor.commit();
				}
				else if (mDeviceScanMode == DEVICE_SCAN_MODE_BP) {
					SharedPreferences.Editor editor = getSharedPreferences(
							"ANDMEDICAL", MODE_PRIVATE).edit();
					editor.putString("bpdeviceid", "" + device.getUuids());
					editor.commit();
				}
				
				if(mBleService != null) {
					mBleService.connectDevice(device.getAddress());
				}
			}
		}
	}
	
	@Override
	protected void onDevicePairingResult(boolean result) {
		super.onDevicePairingResult(result);
		if(result && !isShowDialog) {
			isShowDialog = true;
			ViewGroup dialogLayout = (ViewGroup)findViewById(R.id.dialog_layout);
			dialogLayout.setVisibility(View.INVISIBLE);

			Intent intent = new Intent(InstructionActivity.this,
					DialogActivity.class);
			intent.putExtra(DialogActivity.INTENT_KEY_TITLE, getResources().getString(R.string.dialog_title_paring_complete));

			if (mDeviceScanMode == DEVICE_SCAN_MODE_AM_UW) {
				intent.putExtra(DialogActivity.INTENT_KEY_MESSAGE, getResources().getString(R.string.dialog_message_pairing_complete_uw302));
			} else {
				intent.putExtra(DialogActivity.INTENT_KEY_MESSAGE, getResources().getString(R.string.dialog_message_paring_complete));
			}

			startActivityForResult(intent, DialogActivity.REQUEST_CODE);


		}
		else {
			// to do Nothing
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == DialogActivity.REQUEST_CODE) {
			Intent intent = new Intent(InstructionActivity.this,
					DashboardActivity.class);

			startActivity(intent);
			finish();
		}
	}
}
