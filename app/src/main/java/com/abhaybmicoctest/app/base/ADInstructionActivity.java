package com.abhaybmicoctest.app.base;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.abhaybmicoctest.app.R;
import com.abhaybmicoctest.app.gatt.ADGattUUID;
import com.abhaybmicoctest.app.gatt.BleConnectService;

import java.lang.reflect.Method;



public class ADInstructionActivity extends Activity {

	protected BleConnectService mBleService;
	private boolean mIsServiceBind = false;
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {

	        mBleService = ((BleConnectService.BleConnectionBinder)service).getService();
	        
	        ADInstructionActivity.this.onServiceConnected();
	    }
	 
	    public void onServiceDisconnected(ComponentName className) {

	    	mBleService = null;

	        ADInstructionActivity.this.onServiceDisconnected();
	    }
	};
	
	protected void doBindService() {
		
	    bindService(new Intent(ADInstructionActivity.this,
	            BleConnectService.class), mConnection, Context.BIND_AUTO_CREATE);
	    mIsServiceBind = true;
	}
	
	protected void doUnbindService() {
	    if (mIsServiceBind) {
	        unbindService(mConnection);
	        mIsServiceBind = false;
	    }
	}
	
	public static final String DEVICE_SCAN_MODE_KEY = "DeviceScanModeKey";
	public static final String DEVICE_SCAN_ADDRESS_KEY = "DeviceScanAddressKey";
	
	public static final int DEVICE_SCAN_MODE_NONE = 0;
	public static final int DEVICE_SCAN_MODE_BP = 1;
	public static final int DEVICE_SCAN_MODE_WS = 2;
	public static final int DEVICE_SCAN_MODE_AM = 3;
	public static final int DEVICE_SCAN_MODE_TM = 4;
	public static final int DEVICE_SCAN_MODE_AM_UW = 9;
	
	protected int mDeviceScanMode = DEVICE_SCAN_MODE_NONE;
	protected ImageView mDialogImageView;
	protected Handler mMainThreadHandler;
	
	private BroadcastReceiver mDevicePairingReceiver ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		registerReceiver(mBleConnectionReceiver, BleConnectService.BleConnectionFilter);
		
		// Checks if BlueTooth is unSupported on the device.
		if (!BleConnectService.isEnableBluetoothFunction(this)) {
			finish();
			return;
		}

		Intent intent = getIntent();
		if(intent == null) {
			finish();
			return;
		}
		
		mDeviceScanMode = intent.getIntExtra(DEVICE_SCAN_MODE_KEY, DEVICE_SCAN_MODE_NONE);
		
		if(mDeviceScanMode == DEVICE_SCAN_MODE_NONE) {
			finish();
			return;
		}
		
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		//UW-302 pairing implementation
		if (mDeviceScanMode == DEVICE_SCAN_MODE_AM_UW) {
			setContentView(R.layout.activity_pairing_dialog);
		} else {
			setContentView(R.layout.weightscale_dialog);
		}


		mDialogImageView = (ImageView) findViewById(R.id.weightscale);
		mMainThreadHandler = new Handler();

		doBindService();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBleConnectionReceiver);
		
		mMainThreadHandler.removeCallbacksAndMessages(null);
		
		doUnbindService();
		
		unRegistDeviceBondStateChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void setDialogImageWithResourceID(int resid) {
		if(mDialogImageView != null) {
			mDialogImageView.setBackgroundResource(resid);
		}
	}
	
	protected void setDialogMessageWithResorceID(int resId) {
			TextView textView = (TextView) findViewById(R.id.device_setup_upper_text);
			textView.setText(resId);
	}
	
	protected void setDialogBackGroundColorWithResorceID(int resId) {
		LinearLayout layout = (LinearLayout) findViewById(R.id.device_setup_layout);
		layout.setBackgroundResource(resId);
	}
	
	
	
	protected void onFindConnectDevice(String address) {
		// OverRide Method
	}
	
	protected void onDevicePairingResult(boolean result) {
		// OverRide Method
	}
	
	protected void onServiceConnected() {
		// OverRide Method
	}
	
	protected void onServiceDisconnected() {
		// OverRide Method
	}
	
	protected void onClickCancel() {
		if(mBleService != null) {
			mBleService.disConnectDevice();
		}
		finish();
	}
	
	protected void unpairDevice(BluetoothDevice device) {
		try {
			Method m = device.getClass()
					.getMethod("removeBond", (Class[]) null);
			m.invoke(device, (Object[]) null);
		} catch (Exception e) {
		}
	}
	
	private void registDeviceBondStateChanged() {
		if(mDevicePairingReceiver != null) {
			return ;
		}
		
		mDevicePairingReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				if(!intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
					return;
				}
				
				Bundle bundle = intent.getExtras();
				if(bundle != null) {
					int bond_state = intent.getExtras().getInt(BluetoothDevice.EXTRA_BOND_STATE);
					if(bond_state == BluetoothDevice.BOND_BONDED) {
						BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						if(mBleService != null) {
							String device_name = device.getName();
							if (device_name.contains("UW-302BLE")) {
						        mBleService.reTrackerServiceDiscovery(device.getAddress());
							} else {
								mBleService.reConnectDevice(device.getAddress());
							}



						}
					}
				}
			}
		};
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		registerReceiver(mDevicePairingReceiver, intentFilter);
	}
	
	private void unRegistDeviceBondStateChanged() {
		if(mDevicePairingReceiver == null) {
			return;
		}
		
		unregisterReceiver(mDevicePairingReceiver);
		mDevicePairingReceiver = null;
	}
	
	private BroadcastReceiver mBleConnectionReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			Bundle bundle = intent.getExtras();
			if(BleConnectService.ACTION_DEVICE_SCAN.equals(action)) {
				String address = bundle.getString(BleConnectService.KEY_DEVICE_ADDRES);
				if(address != null) {
					onFindConnectDevice(address);
				}
			}
			else if(BleConnectService.ACTION_DEVICE_SETUP.equals(action)) {
				boolean isSuccess = bundle.getBoolean(BleConnectService.KEY_RESULT);
				if(mBleService != null) {
					mBleService.disConnectDevice();
				}
				onDevicePairingResult(isSuccess);
			}
			else if(BleConnectService.ACTION_DEVICE_REQUEST_PAIRING.equals(action)) {
				registDeviceBondStateChanged();
			}
			else if(BleConnectService.ACTION_DISCOVERED_SERVICES.equals(action)) {
				//Need to start the separate flow here ACGS-10
				String device_name = bundle.getString(BleConnectService.KEY_DEVICE_NAME);
				if(mBleService != null) {
					if (device_name.contains("UW-302BLE")) {
                      mBleService.setupUW302Pairing();
					} else {
						mBleService.readBufferSizeWrtie();
					}

				}
			}
			else if(BleConnectService.ACTION_READ_CHARACTER.equals(action)) {
				mBleService.writeBufferSize();
				// to do Nothing
			}
			else if(BleConnectService.ACTION_WRITE_CHARACTER.equals(action)) {
				boolean isSuccess = bundle.getBoolean(BleConnectService.KEY_RESULT);
				String uuidString = bundle.getString(BleConnectService.KEY_UUID_STRING);
				String device_name = bundle.getString(BleConnectService.KEY_DEVICE_NAME);
				if(ADGattUUID.DateTime.toString().compareTo(uuidString) == 0) {
					if (device_name.contains("UW-302BLE")) {
                       //Need to send disconnect
						mBleService.disconnectTracker();
					} else {
						if(isSuccess) {
							// Success
							if(mBleService != null) {
								mBleService.setupSuccess();
							}
						}
						else {
						}
					} //End of ACGS-10
				} else {
					mBleService.readBufferSizeR();
				}
			}
		}
	};
}