package com.abhaybmicoc_test.app.gatt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.abhaybmicoc_test.app.entities.DataBase;
import com.abhaybmicoc_test.app.utilities.ANDMedicalUtilities;
import com.abhaybmicoc_test.app.utilities.ScanRecordParser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;


public class BleConnectService extends Service {

    public class BleConnectionBinder extends Binder {
        public BleConnectService getService() {
            return BleConnectService.this;
        }
    }

    private final IBinder mBinder = new BleConnectionBinder();

    public static final String ACTION_DEVICE_SCAN = "com.andmedical.gatt.action.device_scan";
    public static final String ACTION_DEVICE_SETUP = "com.andmedical.gatt.action.device_setup";
    public static final String ACTION_DEVICE_CONNECT = "com.andmedical.gatt.action.device_connect";
    public static final String ACTION_DEVICE_DISCONNECT = "com.andmedical.gatt.action.device_disconnect";
    public static final String ACTION_DEVICE_REQUEST_PAIRING = "com.andmedical.gatt.action.device_request_pairing";
    public static final String ACTION_READ_CHARACTER = "com.andmedical.gatt.action.read_character";
    public static final String ACTION_WRITE_CHARACTER = "com.andmedical.gatt.action.write_character";
    public static final String ACTION_DISCOVERED_SERVICES = "com.andmedical.gatt.action.discovered_services";
    public static final IntentFilter BleConnectionFilter = new IntentFilter() {{
        addAction(ACTION_DEVICE_SCAN);
        addAction(ACTION_DEVICE_SETUP);
        addAction(ACTION_DEVICE_CONNECT);
        addAction(ACTION_DEVICE_DISCONNECT);
        addAction(ACTION_DEVICE_REQUEST_PAIRING);
        addAction(ACTION_READ_CHARACTER);
        addAction(ACTION_WRITE_CHARACTER);
        addAction(ACTION_DISCOVERED_SERVICES);
    }};

    public static final String KEY_DEVICE_ADDRES = "com.andmedical.gatt.DeviceAddressKey";
    public static final String KEY_RESULT = "com.andmedical.gatt.ResultKey";
    public static final String KEY_UUID_STRING = "com.andmedical.gatt.UUIDStringKey";
    public static final String KEY_DEVICE_NAME = "com.andmedical.gatt.DeviceName"; //ACSG-10

    public static final int REQUEST_ENABLE_BT = 200;

    private BluetoothAdapter mBluetoothAdapter;
    protected SetupDevice mSetupDevice;
    protected boolean mIsReserveScan = false;
    private Handler delayHandler;

    ArrayList<String> uwtrackerNotification = new ArrayList<String>(); //ACSG-10
    private DataBase mDataBase; //ACGS-10

    @Override
    public void onCreate() {
        super.onCreate();
        delayHandler = new Handler();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothStateChangeReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mBluetoothStateChangeReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public void startScanDevices() {

        if (isEnableBluetoothFunction(this)) {
            BluetoothAdapter adapter = getBluetoothAdapter();
            if (adapter.isEnabled()) {
                adapter.startLeScan(mLeScanCallback);
                mIsReserveScan = false;
            } else {
                mIsReserveScan = true;
                adapter.enable();
            }
        }
    }

    public void stopScanDevice() {
        if (isEnableBluetoothFunction(this)) {
            BluetoothAdapter adapter = getBluetoothAdapter();
            adapter.stopLeScan(mLeScanCallback);
            adapter.cancelDiscovery();
        }
    }

    private final BroadcastReceiver mBluetoothStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (mIsReserveScan) {
                            startScanDevices();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        // to do Nothing
                        break;
                }
            }
        }
    };

    protected void sendMessage(String action, Bundle bundle) {

        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        sendBroadcast(intent);
    }

    protected BluetoothAdapter getBluetoothAdapter() {

        if (mBluetoothAdapter == null) {
            if (Build.VERSION.SDK_INT >= 18) {
                // for API 18
                final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = bluetoothManager.getAdapter();
            } else {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
        }
        return mBluetoothAdapter;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {

            if (ableToScanDevice(device)) {
                if (device.getName().contains("UW-302")) {
                    ScanRecordParser.ScanRecordItem scanRecordItem = ScanRecordParser.getParser().parseString(scanRecord);
                    byte[] manufacturerSpecificData = scanRecordItem.getManufacturerSpecificData();
                    if (manufacturerSpecificData != null
                            && manufacturerSpecificData.length == 2) {
                        Bundle bundle = new Bundle();
                        bundle.putString(KEY_DEVICE_ADDRES, device.getAddress());
                        sendMessage(ACTION_DEVICE_SCAN, bundle);

                    }


                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_DEVICE_ADDRES, device.getAddress());
                    sendMessage(ACTION_DEVICE_SCAN, bundle);
                }

            }
        }
    };

    protected boolean ableToScanDevice(BluetoothDevice device) {
        if (device == null) {
            return false;
        } else {
            String deviceName = device.getName();
            if (deviceName == null) {
                return false;
            } else if (deviceName.contains("UW-302BLE")) {
                return true;
            }
            return (deviceName.contains("A&D"));
        }
    }

    public void connectDevice(String address) {
        if (getBluetoothAdapter() == null || mSetupDevice != null) {
            return;
        }

        BluetoothDevice device = getBluetoothAdapter().getRemoteDevice(address);
        if (device != null) {

            mSetupDevice = new SetupDevice(device);
            mSetupDevice.connect(this);
        }
    }

    public void reConnectDevice(String address) {
        if (getBluetoothAdapter() == null || mSetupDevice == null) {
            return;
        }
        mSetupDevice.reConnect();
        mSetupDevice.discoverServices();
    }

    //ACGS-10
    public void reTrackerServiceDiscovery(String address) {
        if (getBluetoothAdapter() == null || mSetupDevice == null) {
            return;
        }
        mSetupDevice.discoverServices();
    }

    public void disConnectDevice() {
        if (mSetupDevice != null) {

            boolean isSuccess = mSetupDevice.isSetupFinish;
            Bundle bundle = new Bundle();
            bundle.putBoolean(KEY_RESULT, isSuccess);
            sendMessage(ACTION_DEVICE_SETUP, bundle);

            mSetupDevice.disconnect();
            mSetupDevice = null;
        }
    }

    /**
     * Gatt CallBack
     */
    protected final BluetoothGattCallback mBleCallback = new BluetoothGattCallback() {

        /** Tips
         * CallBack内でのGATTへのアクセスは、引数を使用すること。
         * また、メソッドを継承用に外に出している。
         * 今後、OverRideするメソッドを増やすようなら、同様の実装をするように。
         */

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            BleConnectService.this.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            super.onServicesDiscovered(gatt, status);
            String device_name = gatt.getDevice().getName();
            if (device_name.contains("UW-302BLE")) {
                BleConnectService.this.onServicesDiscovered(gatt, status);
            } else {

                delayHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        BleConnectService.this.onServicesDiscovered(gatt, status);
                    }
                }, 1000L);
            }


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            BleConnectService.this.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            BleConnectService.this.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            BleConnectService.this.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt,
                                     BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            BleConnectService.this.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            BleConnectService.this.onDescriptorWrite(gatt, descriptor, status);
        }
    };

    protected void onConnectionStateChange(BluetoothGatt gatt, int status,
                                           int newState) {
        if (mSetupDevice == null) {
            return;
        }

        if (status != BluetoothGatt.GATT_SUCCESS) {
        }

        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            BluetoothDevice device = gatt.getDevice();
            String device_name = device.getName();

            sendMessage(ACTION_DEVICE_DISCONNECT, null);
        } else if (newState == BluetoothProfile.STATE_CONNECTED) {

            BluetoothDevice device = gatt.getDevice();
            int bondstate = device.getBondState();
            if (bondstate != BluetoothDevice.BOND_BONDED) {
                sendMessage(ACTION_DEVICE_REQUEST_PAIRING, null);
            } else {
                mSetupDevice.discoverServices();
            }
        }
    }

    protected void onServicesDiscovered(BluetoothGatt gatt, int status) {
        Bundle bundle = new Bundle();
        BluetoothDevice device = gatt.getDevice();
        String device_name = device.getName();
        bundle.putString(KEY_DEVICE_NAME, device_name);
        sendMessage(ACTION_DISCOVERED_SERVICES, bundle);
    }

    protected void onCharacteristicRead(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic, int status) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_RESULT, (status == BluetoothGatt.GATT_SUCCESS));
        bundle.putString(KEY_UUID_STRING, characteristic.getUuid().toString());
        sendMessage(ACTION_READ_CHARACTER, bundle);
    }

    protected void onCharacteristicWrite(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
        BluetoothDevice device = gatt.getDevice();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_RESULT, (status == BluetoothGatt.GATT_SUCCESS));
        bundle.putString(KEY_UUID_STRING, characteristic.getUuid().toString());
        bundle.putString(KEY_DEVICE_NAME, device.getName());
        sendMessage(ACTION_WRITE_CHARACTER, bundle);
    }

    protected void onCharacteristicChanged(BluetoothGatt gatt,
                                           BluetoothGattCharacteristic characteristic) {
        //Check if this is the case of UW-302 - ACGS-10
        if (characteristic.getUuid().toString().compareTo(ADGattUUID.AndCustomtrackerChar1.toString()) == 0) {
            pairUWTracker(gatt, characteristic);
        }
    }

    protected void onDescriptorRead(BluetoothGatt gatt,
                                    BluetoothGattDescriptor descriptor, int status) {
    }

    public void onDescriptorWrite(BluetoothGatt gatt,
                                  BluetoothGattDescriptor descriptor, int status) {

        if (descriptor.getCharacteristic().getUuid().toString().compareTo(ADGattUUID.AndCustomtrackerChar1.toString()) == 0) {
            if (uwtrackerNotification != null && uwtrackerNotification.size() > 0) {
                String command = uwtrackerNotification.remove(0);
                setNotificationTracker(command, gatt);
            }

        } else if (descriptor.getCharacteristic().getUuid().toString().compareTo(ADGattUUID.AndCustomtrackerService2Char2.toString()) == 0) {
            if (uwtrackerNotification != null && uwtrackerNotification.size() > 0) {
                String command = uwtrackerNotification.remove(0);
                setNotificationTracker(command, gatt);
            }

        }
    }

    public void setupDateTime() {
        if (mSetupDevice != null && mSetupDevice.gatt != null) {
            setDateTimeSetting(mSetupDevice.gatt, Calendar.getInstance());
        }
    }

    protected void setDateTimeSetting(BluetoothGatt gatt, Calendar cal) {

        BluetoothGattService gattService = getGattSearvice(gatt);
        if (gattService != null) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(ADGattUUID.DateTime);
            if (characteristic != null) {
                characteristic = DateTime.writeCharacteristic(characteristic, Calendar.getInstance());
                gatt.writeCharacteristic(characteristic);
            }
        }
    }

    public void readBufferSizeWrtie() {
        if (mSetupDevice != null && mSetupDevice.gatt != null) {
            readBufferW(mSetupDevice.gatt);
        }
    }

    protected void readBufferW(BluetoothGatt gatt) {

        BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomService);
        if (gattService != null) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomCharacteristic);
            if (characteristic != null) {
                byte[] readBufferSize = {
                        (byte) 0x02,
                        (byte) 0x00,
                        (byte) 0xD6
                };
                characteristic.setValue(readBufferSize);
                gatt.writeCharacteristic(characteristic);
            }
        }
    }

    public void readBufferSizeR() {
        if (mSetupDevice != null && mSetupDevice.gatt != null) {
            readBufferR(mSetupDevice.gatt);
        }
    }

    protected void readBufferR(BluetoothGatt gatt) {
        BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomService);
        if (gattService != null) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomCharacteristic);
            gatt.readCharacteristic(characteristic);
        }
    }

    public void writeBufferSize() {
        if (mSetupDevice != null && mSetupDevice.gatt != null) {
            writeBufferW(mSetupDevice.gatt);
        }
    }

    protected void writeBufferW(BluetoothGatt gatt) {
        BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomService);
        if (gattService != null) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomCharacteristic);

            int pktSize = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            // Check data size. Expected data length size is 3.
            // If between write req and read req interval will be very short, fails to read correctly. (Length will be 2.)
            if (pktSize == 3) {
                // Success to read data correctly.
                int bufSize = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);
                if (bufSize == 0) {
                    byte[] setBufferSize = {
                            (byte) 0x03,
                            (byte) 0x01,
                            (byte) 0xA6,
                            (byte) 0x01
                    };
                    characteristic.setValue(setBufferSize);
                    gatt.writeCharacteristic(characteristic);
                } else {
                    // Success and need not to change memory case.
                    setupDateTime();
                }
            } else {
                // Failed read data case.
                setupDateTime();
            }
        }
    }

    //ACGS-10
    protected void setDateTimeSettingTracker(BluetoothGatt gatt, Calendar cal) {

        BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomtrackerService2);//getGattSearvice(gatt);
        if (gattService != null) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(ADGattUUID.DateTime);
            if (characteristic != null) {
                characteristic = DateTime.writeCharacteristic(characteristic, Calendar.getInstance());
                gatt.writeCharacteristic(characteristic);
            }
        }
    }

    //ACGS-10
    public void disconnectTracker() {
        if (mSetupDevice != null && mSetupDevice.gatt != null) {
            disconnectTrackerdevice(mSetupDevice.gatt);
        }
    }

    public void setupUW302Pairing() {
        if (mSetupDevice != null && mSetupDevice.gatt != null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setUW302Pairing(mSetupDevice.gatt);
                }
            }, 500);

//			setUW302Pairing(mSetupDevice.gatt);
        }
    }

    protected void disconnectTrackerdevice(BluetoothGatt gatt) {
        BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomtrackerService);
        if (gattService != null) {
            BluetoothGattCharacteristic gatt_characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomtrackerChar1);
            if (gatt_characteristic != null) {
                if (gatt.setCharacteristicNotification(gatt_characteristic, false)) {
                    BluetoothGattDescriptor descriptor = gatt_characteristic.getDescriptor(ADGattUUID.ClientCharacteristicConfiguration);
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
                    }
                }
            }
        }

        //Disabling the other service
        gattService = gatt.getService(ADGattUUID.AndCustomtrackerService2);
        if (gattService != null) {
            BluetoothGattCharacteristic gatt_characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomtrackerService2Char2);
            if (gatt_characteristic != null) {
                if (gatt.setCharacteristicNotification(gatt_characteristic, false)) {
                    BluetoothGattDescriptor descriptor = gatt_characteristic.getDescriptor(ADGattUUID.ClientCharacteristicConfiguration);
                    if (descriptor != null) {
                    }
                }
            }
        }
        //Show the pairing success dialog to the user
        setupSuccess(); //ACGS-16
        byte[] user_profile = {
                (byte) 0x03,
                (byte) 0x01,
                (byte) 0x13,
                (byte) 0x00

        };
        gattService = gatt.getService(ADGattUUID.AndCustomtrackerService);
        if (gattService != null) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomtrackerChar1);
            if (characteristic != null) {
                characteristic.setValue(user_profile);
                gatt.writeCharacteristic(characteristic);
            }
        }
    }

    protected void setUW302Pairing(BluetoothGatt gatt) {
        //Populate the array list here
        uwtrackerNotification.add("notification2");
        uwtrackerNotification.add("writeProfile");
        setNotificationTracker("notification1", gatt);


    }

    protected void setNotificationTracker(String command, BluetoothGatt gatt) {
        if (command.contains("notification1")) {
            BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomtrackerService);
            if (gattService != null) {
                BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomtrackerChar1);
                if (characteristic != null) {
                    if (gatt.setCharacteristicNotification(characteristic, true)) {
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(ADGattUUID.ClientCharacteristicConfiguration);
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
        } else if (command.contains("notification2")) {
            BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomtrackerService2);
            if (gattService != null) {
                BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomtrackerService2Char2);
                if (characteristic != null) {
                    if (gatt.setCharacteristicNotification(characteristic, true)) {
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(ADGattUUID.ClientCharacteristicConfiguration);
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }

        } else if (command.contains("writeProfile")) {
            //Write the 0X11 profile information
            byte[] user_profile = {
                    (byte) 0x0F,
                    (byte) 0x01,
                    (byte) 0x11,
                    (byte) 0x00,
                    (byte) 0x41, // A
                    (byte) 0x6e, // n
                    (byte) 0x64, // d
                    (byte) 0x72, // r
                    (byte) 0x6f, // o
                    (byte) 0x69, // i
                    (byte) 0x64, // d
                    (byte) 0x00, // null
                    (byte) 0x00, // null
                    (byte) 0x00, // null
                    (byte) 0x00, // null
                    (byte) 0x00  // null
            };
            BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomtrackerService);
            if (gattService != null) {
                BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomtrackerChar1);
                if (characteristic != null) {
                    characteristic.setValue(user_profile);
                    gatt.writeCharacteristic(characteristic);
                }
            }

        }
    }

    //ACGS-10
    void pairUWTracker(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        int opcode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2);
        if (opcode == 64) {
            int status = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4);
            if (ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
                mDataBase = new DataBase(getApplicationContext());
                mDataBase.updateTrackerName(mDataBase.getGuestInfo(), gatt.getDevice().getName());
            }
            if (status == 1) {
                //Setup success, now set device type 0x19
                byte[] set_deviceType = {
                        (byte) 0x04,
                        (byte) 0x01,
                        (byte) 0x19,
                        (byte) 0x00,
                        (byte) 0x01
                };
                BluetoothGattService gattService = getGattSearvice(gatt);
                if (gattService != null) {
                    if (characteristic != null) {
                        characteristic.setValue(set_deviceType);
                        gatt.writeCharacteristic(characteristic);
                    }
                }

            }
        } else if (opcode == 25) {
            //Set profile information
            //Write Profile data

            byte[] set_profileData = {
                    (byte) 0x10,
                    (byte) 0x01,
                    (byte) 0x20,
                    (byte) 0x00,
                    (byte) 0xFF,
                    (byte) 0x36,
                    (byte) 0xB0,
                    (byte) 0x06,
                    (byte) 0xA4,
                    (byte) 0x00,
                    (byte) 0x64,
                    (byte) 0x32,
                    (byte) 0x07,
                    (byte) 0xB2,
                    (byte) 0x02,
                    (byte) 0x01,
                    (byte) 0x01
            };
            BluetoothGattService gattService = getGattSearvice(gatt);
            if (gattService != null) {
                if (characteristic != null) {
                    characteristic.setValue(set_profileData);
                    gatt.writeCharacteristic(characteristic);
                }
            }
        } else if (opcode == 32) {
            //Now set date and time
            setDateTimeSettingTracker(gatt, Calendar.getInstance());
        }
    }




    protected static void setNotificationSetting(BluetoothGatt gatt, boolean notificationEnable) {

        BluetoothGattService gattService = getGattSearvice(gatt);

        if (gattService != null) {
            BluetoothGattCharacteristic characteristic = getGattMeasuCharacteristic(gattService);

            if (characteristic != null) {

                // Notification を要求する
                boolean registered = gatt.setCharacteristicNotification(characteristic, notificationEnable);

                // Characteristic の Notification 有効化
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(ADGattUUID.ClientCharacteristicConfiguration);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                gatt.writeDescriptor(descriptor);

                if (registered) {
                    // Characteristics通知設定が成功
                } else {
                    // Characteristics通知設定が失敗
                }
            } else {
                // Error
            }
        } else {
        }
    }

    public void setupSuccess() {
        mSetupDevice.isSetupFinish = true;
        disConnectDevice();
    }

    protected static BluetoothGattService getGattSearvice(BluetoothGatt gatt) {
        BluetoothGattService service = null;
        for (UUID uuid : ADGattUUID.ServicesUUIDs) {
            service = gatt.getService(uuid);
            if (service != null) break;
        }
        return service;
    }

    protected static BluetoothGattCharacteristic getGattMeasuCharacteristic(BluetoothGattService service) {
        BluetoothGattCharacteristic characteristic = null;
        for (UUID uuid : ADGattUUID.MeasuCharacUUIDs) {
            characteristic = service.getCharacteristic(uuid);
            if (characteristic != null) break;
        }
        return characteristic;
    }

    public static boolean isEnableBluetooth(Context context) {
        if (Build.VERSION.SDK_INT >= 18) {
            // for API 18
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            return bluetoothManager.getAdapter().isEnabled();
        } else {
            return BluetoothAdapter.getDefaultAdapter().isEnabled();
        }
    }

    public static boolean isEnableBluetoothFunction(Context context) {
        if (Build.VERSION.SDK_INT >= 18) {
            // for API 18
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            return (bluetoothManager.getAdapter() != null);
        } else {
            return (BluetoothAdapter.getDefaultAdapter() != null);
        }
    }

    public static BluetoothDevice getBluetoothDevice(Context context, String address) {
        if (isEnableBluetoothFunction(context)) {

            if (Build.VERSION.SDK_INT >= 18) {
                // for API 18
                final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
                return bluetoothManager.getAdapter().getRemoteDevice(address);
            } else {
                return BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
            }
        }

        return null;
    }

    public static boolean isPairingDevice(Context context, String address) {
        if (!isEnableBluetoothFunction(context)) {
            return false;
        }

        Set<BluetoothDevice> pairingDevices = null;
        BluetoothDevice device = null;

        if (Build.VERSION.SDK_INT >= 18) {
            // for API 18
            final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            pairingDevices = bluetoothManager.getAdapter().getBondedDevices();
            device = bluetoothManager.getAdapter().getRemoteDevice(address);
        } else {
            pairingDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        }

        if (pairingDevices == null || device == null) {
            return false;
        }

        return pairingDevices.contains(device);
    }

    protected class SetupDevice {

        private final BluetoothDevice device;
        public BluetoothGatt gatt;
        public boolean isSetupFinish = false;

        public SetupDevice(BluetoothDevice device) {
            super();
            this.device = device;
        }

        public boolean discoverServices() {
            return (gatt != null && gatt.discoverServices());
        }

        public void connect(final Context context) {
            if (gatt != null) {
                disconnect();
            }
            String device_name = device.getName();
            if (device_name.contains("UW-302BLE")) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gatt = device.connectGatt(context, true, mBleCallback);
                    }
                }, 500);

            } else {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        gatt = device.connectGatt(context, true, mBleCallback);
                    }
                }, 500);
            }


        }

        public void reConnect() {
            if (gatt != null) {
                gatt.connect();
            }
        }

        public void disconnect() {
            if (gatt != null) {
                gatt.disconnect();
                gatt.close();
                gatt = null;
            }
        }
    }

    //ACGS-10
    public static String byte2hex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }


}
