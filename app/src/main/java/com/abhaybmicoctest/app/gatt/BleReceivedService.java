package com.abhaybmicoctest.app.gatt;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.abhaybmicoctest.app.MeasuDataManager;
import com.abhaybmicoctest.app.entities.AndMedical_App_Global;
import com.abhaybmicoctest.app.entities.DataBase;
import com.abhaybmicoctest.app.entities.Lifetrack_infobean;
import com.abhaybmicoctest.app.entities.RegistrationInfoBean;
import com.abhaybmicoctest.app.utilities.ADSharedPreferences;
import com.abhaybmicoctest.app.utilities.ANDMedicalUtilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class BleReceivedService extends Service {


    public static final String ACTION_BLE_SERVICE = "jp.co.aandd.andblelink.ble.BLE_SERVICE";
    public static final String TYPE_GATT_CONNECTED = "Connected device";
    public static final String TYPE_GATT_DISCONNECTED = "Disconnected device";
    public static final String TYPE_GATT_ERROR = "Gatt Error";
    public static final String TYPE_GATT_SERVICES_DISCOVERED = "Discovered services";
    public static final String TYPE_CHARACTERISTIC_READ = "Read characteristic";
    public static final String TYPE_CHARACTERISTIC_WRITE = "Write characteristic";
    public static final String TYPE_CHARACTERISTIC_CHANGED = "Characteristic changed";
    public static final String TYPE_DESCRIPTOR_READ = "Read descriptor";
    public static final String TYPE_DESCRIPTOR_WRITE = "Write descriptor";
    public static final String TYPE_INDICATION_VALUE = "Indication Value";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final String EXTRA_VALUE = "EXTRA_VALUE";
    public static final String EXTRA_SERVICE_UUID = "EXTRA_SERVICE_UUID";
    public static final String EXTRA_CHARACTERISTIC_UUID = "EXTRA_CHARACTERISTIC_UUID";
    public static final String EXTRA_STATUS = "EXTRA_STATUS";
    public static final String EXTRA_ADDRESS = "EXTRA_ADDRESS";
    public static final String EXTRA_DEVICE_NAME = "DEVICE_NAME"; //UW-302 implementation


    private static BleReceivedService bleService;
    private BluetoothGatt bluetoothGatt;
    private boolean isConnectedDevice;
    private boolean isBindService;
    private int activityDataCount = 0;
    private int totalactivityLength = 0;
    private Boolean dataPacketEnd = false;
    ByteArrayOutputStream activity_sectordata;
    ArrayList<String> uwtrackerNotification = new ArrayList<String>(); //ACSG-10
    ArrayList<Lifetrack_infobean> activityCompleteDataSet = new ArrayList<Lifetrack_infobean>();
    ArrayList<HashMap> bloodpressureDataSet = new ArrayList<HashMap>();
    ArrayList<HashMap> weightDataSet = new ArrayList<HashMap>();
    ArrayList<String> dateListSet = new ArrayList<String>();
    HashMap<String, ArrayList<Lifetrack_infobean>> dataActivity = new HashMap<String, ArrayList<Lifetrack_infobean>>();
    ArrayList<Lifetrack_infobean> activityCompleteDataSetOpti = new ArrayList<Lifetrack_infobean>();

    private Timer mNotificationTimeOut;
    String currentDateString;

    private DataBase mDataBase; //ACGS-10
    RegistrationInfoBean registerInfo;

    public static BleReceivedService getInstance() {
        return bleService;
    }

    public static BluetoothGatt getGatt() {
        if (bleService != null) {
            return bleService.bluetoothGatt;
        }
        return null;
    }

    private final IBinder binder = new BleReceivedBinder();

    public class BleReceivedBinder extends Binder {
        public BleReceivedService getService() {
            return bleService;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        isBindService = true;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBindService = false;
        return super.onUnbind(intent);
    }

    public BleReceivedService() {
        super();
    }

    @Override
    public void onCreate() {
        if (bleService == null) {
            bleService = this;
        }
    }

    @Override
    public void onDestroy() {
        if (bleService != null) {
            bleService = null;
        }
    }

    public boolean isConnectedDevice() {
        return isConnectedDevice;
    }

    public boolean isBindService() {
        return isBindService;
    }

    public BluetoothManager getBluetoothManager() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        return bluetoothManager;
    }

    public boolean connectDevice(BluetoothDevice device) {
        if (device == null) {
            return false;
        }
        bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
        if (bluetoothGatt == null) {
            return false;
        }
        return true;
    }

    public void test() {
        bluetoothGatt.connect();
    }

    public void disconnectDevice() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt.disconnect();
        bluetoothGatt = null;
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BluetoothDevice device = gatt.getDevice();

            if (status != BluetoothGatt.GATT_SUCCESS) {
                sendBroadcast(TYPE_GATT_ERROR, device, status);
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                isConnectedDevice = true;
                sendBroadcast(TYPE_GATT_CONNECTED, device, status);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                isConnectedDevice = false;
                sendBroadcast(TYPE_GATT_DISCONNECTED, device, status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            BluetoothDevice device = gatt.getDevice();
            sendBroadcast(TYPE_GATT_SERVICES_DISCOVERED, device, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            BluetoothDevice device = gatt.getDevice();
            sendBroadcast(TYPE_CHARACTERISTIC_READ, device, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            BluetoothDevice device = gatt.getDevice();
            //For UW-302 follow a different path
            if (gatt.getDevice().getName().contains("UW-302BLE")) {
                if (characteristic.getUuid().toString().compareTo(ADGattUUID.DateTime.toString()) == 0) {
                    //Now send write request to start receiving data

                    byte[] new_datarequest = {
                            (byte) 0x04,
                            (byte) 0x01,
                            (byte) 0x58,
                            (byte) 0x00,
                            (byte) 0x00 //0X01 for all data. 0x00 for new data
                    };
                    BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomtrackerService);
                    BluetoothGattCharacteristic data_characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomtrackerChar1);
                    if (gattService != null) {
                        if (data_characteristic != null) {
                            data_characteristic.setValue(new_datarequest);
                            gatt.writeCharacteristic(data_characteristic);
                        }
                    } //The data now will come up as characteristic Update

                }
            } else {
                sendBroadcast(TYPE_CHARACTERISTIC_WRITE, device, characteristic, status);
            }

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            BluetoothDevice device = gatt.getDevice();
            sendBroadcast(TYPE_CHARACTERISTIC_CHANGED, device, characteristic, BluetoothGatt.GATT_SUCCESS);
            try {
                parseCharcteristicValue(gatt, characteristic); //ACGS-10
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            BluetoothDevice device = gatt.getDevice();
            sendBroadcast(TYPE_DESCRIPTOR_READ, device, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            BluetoothDevice device = gatt.getDevice();

            //For UW-302 set the other notification , others send out this intent  - ACGS-10
            if (descriptor.getCharacteristic().getUuid().toString().compareTo(ADGattUUID.AndCustomtrackerChar1.toString()) == 0) {
                if (uwtrackerNotification != null && uwtrackerNotification.size() > 0) {
                    String command = uwtrackerNotification.remove(0);
                    setNotificationTracker(command, gatt);

                }
            } else if (descriptor.getCharacteristic().getUuid().toString().compareTo(ADGattUUID.AndCustomtrackerService2Char2.toString()) == 0) {

            } else {
                sendBroadcast(TYPE_DESCRIPTOR_WRITE, device, descriptor, status);
            }


        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            BluetoothDevice device = gatt.getDevice();
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            BluetoothDevice device = gatt.getDevice();
        }
    };

    private void sendBroadcast(String type, BluetoothDevice device, int status) {
        Intent intent = new Intent(ACTION_BLE_SERVICE);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_ADDRESS, device.getAddress());
        intent.putExtra(EXTRA_STATUS, status);
        intent.putExtra(EXTRA_DEVICE_NAME, device.getName());
        sendBroadcast(intent);
    }

    private void sendBroadcast(String type, BluetoothDevice device, BluetoothGattCharacteristic characteristic, int status) {
        Intent intent = new Intent(ACTION_BLE_SERVICE);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_SERVICE_UUID, characteristic.getService().getUuid().toString());
        intent.putExtra(EXTRA_CHARACTERISTIC_UUID, characteristic.getUuid().toString());
        intent.putExtra(EXTRA_VALUE, characteristic.getValue());
        intent.putExtra(EXTRA_STATUS, status);
        sendBroadcast(intent);
    }

    private void sendBroadcast(String type, BluetoothDevice device, BluetoothGattDescriptor descriptor, int status) {
        String serviceUuidString = descriptor.getCharacteristic().getService().getUuid().toString();
        String characteristicUuidString = descriptor.getCharacteristic().getUuid().toString();

        Intent intent = new Intent(ACTION_BLE_SERVICE);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_SERVICE_UUID, serviceUuidString);
        intent.putExtra(EXTRA_CHARACTERISTIC_UUID, characteristicUuidString);
        intent.putExtra(EXTRA_VALUE, descriptor.getValue());
        intent.putExtra(EXTRA_STATUS, status);
        intent.putExtra(EXTRA_DEVICE_NAME, device.getName());
        sendBroadcast(intent);
    }

    private void sendBroadcast(String type, String characteristicUuidString, Bundle bundle) {
        Intent intent = new Intent(ACTION_BLE_SERVICE);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_CHARACTERISTIC_UUID, characteristicUuidString);
        intent.putExtra(EXTRA_VALUE, bundle);
        sendBroadcast(intent);
    }

    public BluetoothGattService getGattSearvice(BluetoothGatt gatt) {
        BluetoothGattService service = null;
        for (UUID uuid : ADGattUUID.ServicesUUIDs) {
            service = gatt.getService(uuid);
            if (service != null) break;
        }
        return service;
    }

    public BluetoothGattCharacteristic getGattMeasuCharacteristic(BluetoothGattService service) {
        BluetoothGattCharacteristic characteristic = null;
        for (UUID uuid : ADGattUUID.MeasuCharacUUIDs) {
            characteristic = service.getCharacteristic(uuid);
            if (characteristic != null) break;
        }
        return characteristic;
    }

    public boolean setIndication(BluetoothGatt gatt, boolean enable) {
        boolean isSuccess = false;
        if (gatt != null) {
            BluetoothGattService service = BleReceivedService.getInstance().getGattSearvice(gatt);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = BleReceivedService.getInstance().getGattMeasuCharacteristic(service);
                if (characteristic != null) {
                    isSuccess = gatt.setCharacteristicNotification(characteristic, enable);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(ADGattUUID.ClientCharacteristicConfiguration);
                    if (enable) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
                    } else {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);

                    }
                } else {
                }
            } else {
            }
        }
        return isSuccess;
    }

    public void parseCharcteristicValue(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) throws IOException {

        if (ADGattUUID.AndCustomWeightScaleMeasurement.equals(characteristic.getUuid())) {
            Bundle valueBundle = AndCustomWeightScaleMeasurement.readCharacteristic(characteristic);
            sendBroadcast(TYPE_INDICATION_VALUE, ADGattUUID.AndCustomWeightScaleMeasurement.toString(), valueBundle);
        } else if (ADGattUUID.BloodPressureMeasurement.equals(characteristic.getUuid())) {
            Bundle valueBundle = BloodPressureMeasurement.readCharacteristic(characteristic);
            sendBroadcast(TYPE_INDICATION_VALUE, ADGattUUID.BloodPressureMeasurement.toString(), valueBundle);
        } else if (ADGattUUID.WeightScaleMeasurement.equals(characteristic.getUuid())) {
            Bundle valueBundle = WeightMeasurement.readCharacteristic(characteristic);
            sendBroadcast(TYPE_INDICATION_VALUE, ADGattUUID.WeightScaleMeasurement.toString(), valueBundle);
        } else if (ADGattUUID.TemperatureMeasurement.equals(characteristic.getUuid())) {
            Bundle valueBundle = ThermometerMeasurement.readCharacteristic(characteristic);
            sendBroadcast(TYPE_INDICATION_VALUE, ADGattUUID.TemperatureMeasurement.toString(), valueBundle);
        } else if (ADGattUUID.AndCustomtrackerChar1.equals(characteristic.getUuid())) {
            dataTransferUWTracker(gatt, characteristic); //ACGS-10
        } else if (ADGattUUID.AndCustomtrackerService2Char2.equals(characteristic.getUuid())) {
            dataTransferUWTracker(gatt, characteristic); //ACGS-10
        }

    }

    public void requestReadFirmRevision() {
        if (bluetoothGatt != null) {
            BluetoothGattService service = bluetoothGatt.getService(ADGattUUID.DeviceInformationService);
            if (service != null) {
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(ADGattUUID.FirmwareRevisionString);
                if (characteristic != null) {
                    bluetoothGatt.readCharacteristic(characteristic);
                }
            }
        }
    }

    public boolean setupDateTime(BluetoothGatt gatt) {
        boolean isSuccess = false;
        if (gatt != null) {
            isSuccess = setDateTimeSetting(gatt, Calendar.getInstance());
        }
        return isSuccess;
    }

    protected boolean setDateTimeSetting(BluetoothGatt gatt, Calendar cal) {
        boolean isSuccess = false;
        BluetoothGattService gattService = getGattSearvice(gatt);
        if (gattService != null) {
            BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(ADGattUUID.DateTime);
            if (characteristic != null) {
                characteristic = DateTime.writeCharacteristic(characteristic, cal);
                isSuccess = gatt.writeCharacteristic(characteristic);
            }
        }
        return isSuccess;
    }

    //UW-302 Functions
    public void setUW302Notfication() {
        if (bluetoothGatt != null) {

            uwtrackerNotification.add("notification2");
            final Handler handler = new Handler();
            mNotificationTimeOut = new Timer();
            mNotificationTimeOut.schedule(new TimerTask() {

                @Override
                public void run() {
                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            onFailReceiveNotification();
                        }
                    });
                }
            }, 3000);
        }
    }

    private void onFailReceiveNotification() {
        //Timer

        if (bluetoothGatt != null) {
            setNotificationTracker("notification1", bluetoothGatt);

            mNotificationTimeOut.schedule(new TimerTask() {
                @Override
                public void run() {
                    setDateTimeSettingTracker(bluetoothGatt, Calendar.getInstance());
                }
            }, 1000);


        }
    }


    private void cancelTimer() {
        if (mNotificationTimeOut != null) {
            mNotificationTimeOut.cancel();
        }
        mNotificationTimeOut = null;
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

        }
    }

    protected void dataTransferUWTracker(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) throws IOException {
        //Check the opcode received
        if (characteristic.getUuid().toString().compareTo(ADGattUUID.AndCustomtrackerChar1.toString()) == 0) {
            int opcode = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2);
            if (opcode == 64) {

                int status = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4);
                //Now write the device name

            } else if (opcode == 88) {
                int requestType = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);
                byte[] activity_data = characteristic.getValue();
                String print_activitydata = byte2hex(activity_data);
                if (requestType == 0) {
                    activityDataCount = 0;
                    totalactivityLength = 0;
                    dataPacketEnd = false;
                    activityCompleteDataSet.clear();
                    activityCompleteDataSetOpti.clear();
                    dateListSet.clear();
                    dataActivity.clear();
                    currentDateString = "";

                    activity_sectordata = new ByteArrayOutputStream(); //Initialize the byte[] array
                    if (!ANDMedicalUtilities.APP_STAND_ALONE_MODE) {
                        String userName = ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_USER_NAME, "");
                        mDataBase = new DataBase(getApplicationContext(), userName);
                        registerInfo = mDataBase.getUserDetailAccount(ADSharedPreferences.getString(ADSharedPreferences.KEY_LOGIN_EMAIL, ""));
                    } else {
                        mDataBase = new DataBase(getApplicationContext());
                        registerInfo = mDataBase.getGuestInfo();
                    }
                } else if (requestType == 1) {
                    //1.Add logic to get the last string and set the last date packet
                    int packetValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6);
                    int lastpacketValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 7);
                    if ((packetValue == 0) && (lastpacketValue == 1)) {
                        dataPacketEnd = true;
                    }
                    if (activityDataCount >= 13) {
                        activityDataCount = 0;
                        totalactivityLength = 0;

                    }
                } else if (requestType == 3) {

                    //Disconnecting the tracker

                    //Call the optimize function to optimize data and then
                    //optimizeActivityData(gatt);
                    optimizeActivityData1(gatt);

                }
            }
        } else if (characteristic.getUuid().toString().compareTo(ADGattUUID.AndCustomtrackerService2Char2.toString()) == 0) {
            //Some function to print the data received
            activityDataCount++;
            byte[] activity_data = characteristic.getValue();
            String print_activitydata = byte2hex(activity_data);
            int length = activity_data.length;

            //Append the byte array to create a 1 min sector data
            activity_sectordata.write(activity_data);

            totalactivityLength = totalactivityLength + length;
            byte[] tempDataArray = new byte[length];
            if ((activityDataCount == 10) && (length == 20)) {
                byte[] next_datarequest = {
                        (byte) 0x03,
                        (byte) 0x01,
                        (byte) 0x58,
                        (byte) 0x01

                };
                BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomtrackerService);
                BluetoothGattCharacteristic data_characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomtrackerChar1);
                if (gattService != null) {
                    if (data_characteristic != null) {
                        data_characteristic.setValue(next_datarequest);
                        gatt.writeCharacteristic(data_characteristic);
                    }
                } //The data now will come up as characteristic Update
            } else if (activityDataCount == 13) {
                byte[] oneMinData = activity_sectordata.toByteArray();//This is the data that will be parsed for activity
                String print_oneMinData = byte2hex(oneMinData);
                activity_sectordata.reset();

                //Parse this sector array
                int sector = oneMinData[0];
                int data_type = oneMinData[1];
                if (sector == 80) { //Header type is 0x50
                    if (data_type == 39) {
                        //Change the array based on the sleep structure
                        int sleep_position = 12;
                        int sleep_count = 1;
                        for (sleep_count = 1; sleep_count <= 20; sleep_count++) {
                            int sleep_status = oneMinData[sleep_position];
                            if (sleep_status == 2) {
                                //Need to write back a different value here
                                oneMinData[sleep_position] = (byte) 0x01;
                            }
                            sleep_position = sleep_position + 12;
                        }
                        String print_oneMinDataSleep = byte2hex(oneMinData);
                        //Logic to get the timestring
                        byte[] time_data = new byte[4];
                        time_data[0] = oneMinData[3];
                        time_data[1] = oneMinData[4];
                        time_data[2] = oneMinData[5];
                        time_data[3] = oneMinData[6];
                        String print_timeData = byte2hex(time_data);


                        int steps = oneMinData[8] & 0xFF;

                        int calories = oneMinData[11] & 0xFF; //ACSG-31
                        //Distance calculation to do later
                        int sleep = oneMinData[12];
                        if (sleep == 0) {
                            sleep = 20; //Means there is sleep in that minute
                        } else {
                            sleep = 0; //Means no sleep in that minute
                        }


                        double distance;
                        String distanceUnit = "mi"; //UW-302 always calculating distance in miles
                        if (registerInfo != null) {
                            String heightUnit = registerInfo.getUserHeightUnit();
                            String height = registerInfo.getUserHeight();
                            double stride_avg = 0;
                            double heightFloat;

                            if ((height == null) || (height == "") || (height == " ") || (height.trim().length() == 0)) {
                                //Get the height of 178 cm in inces and use that to assign value for heightFloat
                                heightFloat = 70.078; //default of 178 cm in inches
                                heightUnit = "in"; //Default height calculated in inches
                            } else {
                                heightFloat = Double.valueOf(height);

                            }

                            if (heightUnit.equalsIgnoreCase("in")) {
                                //The height is in inches
                                double stride_in = heightFloat * 0.413;
                                double stride_ft = 0.08333 * stride_in;
                                stride_avg = 5280 / stride_ft;

                            } else {
                                double string_inches = heightFloat * 0.393701;
                                double stride_in = string_inches * 0.413;
                                double stride_ft = 0.08333 * stride_in;
                                stride_avg = 5280 / stride_ft;

                            }
                            distance = steps / stride_avg;

                        } else {
                            //Get the default height as 178 cm is everything is null
                            distance = steps / 2189.2816; //Calculated using the default height of 178cm
                        }

                        int sleepHours = oneMinData[13];
                        int sleepMin = oneMinData[14];
                        int position = 8;
                        int sleepPosition = 12;
                        int caloriePosition = 11;
                        //Check for timestring and get date
                        if (print_timeData.equalsIgnoreCase("00000000")) {
                        } else {
                            //Logic to calculate time
                            String time_binary = hexToBinary(print_timeData);

                            int size_string = time_binary.length();
                            if (size_string == 33) {
                                time_binary = time_binary.substring(1); //Ignore the 0th index
                            }
                            String last3String = time_binary.substring(29);
                            String first3String = time_binary.substring(0, 3);
                            StringBuilder value = new StringBuilder().append(last3String).append(first3String);
                            String year_binary = value.toString();
                            int year = Integer.parseInt(year_binary, 2);
                            int final_year = 2005 + year;

                            String month_string = time_binary.substring(3, 7);
                            int month = Integer.parseInt(month_string, 2);
                            int final_month = month + 1;

                            String day_string = time_binary.substring(7, 12);
                            int day = Integer.parseInt(day_string, 2);

                            String hour_string = time_binary.substring(12, 17);
                            int hour = Integer.parseInt(hour_string, 2);

                            String min_string = time_binary.substring(17, 23);
                            int min = Integer.parseInt(min_string, 2);
                            //Getting the date and time
                            String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", final_year, final_month, day);
                            String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hour, min);
                            String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", final_year, final_month, day, hour, min, 0);
                            //Calculating the total sleep
                            int sleepHour_inMin = sleepHours * 60;
                            int total_sleep = sleepHour_inMin + sleepMin;

                            //Creating a lifetrak infobean object
                            Lifetrack_infobean lifeTrackTemp_activityData = new Lifetrack_infobean();
                            lifeTrackTemp_activityData.setDate(finaldate);


                            lifeTrackTemp_activityData.setTime(finaltime);
                            long dateValue = convertDateintoMs(finalTimeStamp);
                            lifeTrackTemp_activityData.setDateTimeStamp(String.valueOf(dateValue));
                            lifeTrackTemp_activityData.setSleep(Integer.toString(total_sleep));
                            lifeTrackTemp_activityData.setSleepUnit("min");
                            lifeTrackTemp_activityData.setSteps(Integer.toString(steps));
                            lifeTrackTemp_activityData.setStepsUnits("steps");
                            lifeTrackTemp_activityData.setCal(Integer.toString(calories));
                            lifeTrackTemp_activityData.setCalorieUnits("kcal");
                            lifeTrackTemp_activityData.setDistance(Double.toString(distance));
                            lifeTrackTemp_activityData.setDistanceUnit("mi");
                            lifeTrackTemp_activityData.setDistanceInMiles(Double.toString(distance)); //The distance calculated using UW-302 is always in miles

                            // 脈拍
                            lifeTrackTemp_activityData.setHeartRate(Integer.toString(0));

                            lifeTrackTemp_activityData.setIsSynced("no");
                            // deviceID
                            lifeTrackTemp_activityData.setDeviceId("UW-302");

                            //If the date is present in the ArrayList, add to it else add it as a new entry
                            if (dataActivity.containsKey(finaldate)) {
                                //Add it to the existing hashmap
                                ArrayList<Lifetrack_infobean> minTrackerData = dataActivity.get(finaldate);
                                minTrackerData.add(lifeTrackTemp_activityData);
                                dataActivity.put(finaldate, minTrackerData);

                            } else {
                                //Create a new hashmap for this
                                ArrayList<Lifetrack_infobean> minTrackerData = new ArrayList<Lifetrack_infobean>();
                                minTrackerData.add(lifeTrackTemp_activityData);
                                dataActivity.put(finaldate, minTrackerData);
                            }

                            if (finaldate.equalsIgnoreCase(currentDateString)) {
                            } else {

                                if (!dateListSet.contains(finaldate)) {
                                    dateListSet.add(finaldate);
                                    currentDateString = finaldate;
                                }


                            }

                            //Need to add this hashmap to the Array list
                            activityCompleteDataSet.add(lifeTrackTemp_activityData);

                        }
                        //Decide the structure where we can save activity data
                        //Save that structure into an array list
                        for (int i = 1; i < 20; i++) {
                            position = position + 12;
                            caloriePosition = position + 3;
                            sleepPosition = position + 4;
                            //Get the time data string and if timestring is 00000000, then continue
                            byte[] temptime_data = new byte[4];
                            temptime_data[0] = oneMinData[position - 5];
                            temptime_data[1] = oneMinData[position - 4];
                            temptime_data[2] = oneMinData[position - 3];
                            temptime_data[3] = oneMinData[position - 2];
                            String print_temptimeData = byte2hex(temptime_data);
                            if (print_temptimeData.equalsIgnoreCase("00000000")) {
                                continue;
                            } else {
                                //Else add logic to get the time
                                //Logic to calculate time
                                String temptime_binary = hexToBinary(print_temptimeData);

                                int size_string = temptime_binary.length();
                                if (size_string == 33) {
                                    temptime_binary = temptime_binary.substring(1); //Ignore the 0th index
                                }
                                String last3String = temptime_binary.substring(29);
                                String first3String = temptime_binary.substring(0, 3);
                                StringBuilder value = new StringBuilder().append(last3String).append(first3String);
                                String year_binary = value.toString();
                                int year = Integer.parseInt(year_binary, 2);
                                int final_year = 2005 + year;

                                String month_string = temptime_binary.substring(3, 7);
                                int month = Integer.parseInt(month_string, 2);
                                int final_month = month + 1;

                                String day_string = temptime_binary.substring(7, 12);
                                int day = Integer.parseInt(day_string, 2);

                                String hour_string = temptime_binary.substring(12, 17);
                                int hour = Integer.parseInt(hour_string, 2);

                                String min_string = temptime_binary.substring(17, 23);
                                int min = Integer.parseInt(min_string, 2);
                                int tempSteps = oneMinData[position] & 0xFF;

                                //Logic to calculate distance
                                double tempdistance;
                                String tempdistanceUnit = "mi"; //UW-302 always calculating distance in miles
                                if (registerInfo != null) {
                                    String heightUnit = registerInfo.getUserHeightUnit();
                                    String height = registerInfo.getUserHeight();
                                    double stride_avg = 0;
                                    double heightFloat;
                                    if ((height == null) || (height == "") || (height == " ") || (height.trim().length() == 0)) {
                                        //Get the height of 178 cm in inces and use that to assign value for heightFloat
                                        heightFloat = 70.078; //default of 178 cm in inches
                                        heightUnit = "in"; //Default height calculated in inches
                                    } else {
                                        heightFloat = Double.valueOf(height);

                                    }

                                    if (heightUnit.equalsIgnoreCase("in")) {
                                        //The height is in inches
                                        double stride_in = heightFloat * 0.413;
                                        double stride_ft = 0.08333 * stride_in;
                                        stride_avg = 5280 / stride_ft;

                                    } else {
                                        double string_inches = heightFloat * 0.393701;
                                        double stride_in = string_inches * 0.413;
                                        double stride_ft = 0.08333 * stride_in;
                                        stride_avg = 5280 / stride_ft;

                                    }
                                    tempdistance = tempSteps / stride_avg;

                                } else {
                                    //Get the default height as 178 cm is everything is null
                                    tempdistance = tempSteps / 2189.2816; //Calculated using the default height of 178cm
                                }

                                int tempCalorie = oneMinData[caloriePosition] & 0xFF; //ACSG-31

                                //Calculate distance based on height
                                int tempSleepStatus = oneMinData[sleepPosition];
                                if (tempSleepStatus == 0) {
                                    tempSleepStatus = 20;
                                } else {
                                    tempSleepStatus = 0;
                                }
                                int tempsleepHours = oneMinData[sleepPosition + 1];
                                int tempsleepMin = oneMinData[sleepPosition + 2];

                                steps = steps + tempSteps;
                                calories = calories + tempCalorie;
                                distance = distance + tempdistance;

                                int sleepHour_inMin = tempsleepHours * 60;
                                int total_sleep = sleepHour_inMin + tempsleepMin;

                                String finaldate = String.format(Locale.getDefault(), "%04d-%02d-%02d", final_year, final_month, day);
                                String finaltime = String.format(Locale.getDefault(), "%02d:%02d", hour, min);
                                String finalTimeStamp = String.format(Locale.getDefault(), "%04d-%02d-%02dT%02d:%02d:%02d", final_year, final_month, day, hour, min, 0);

                                Lifetrack_infobean activityDataMap = new Lifetrack_infobean();
                                activityDataMap.setDate(finaldate);


                                activityDataMap.setTime(finaltime);
                                long dateValue = convertDateintoMs(finalTimeStamp);
                                activityDataMap.setDateTimeStamp(String.valueOf(dateValue));
                                activityDataMap.setSleep(Integer.toString(total_sleep));
                                activityDataMap.setSleepUnit("min");
                                activityDataMap.setSteps(Integer.toString(tempSteps));
                                activityDataMap.setStepsUnits("steps");
                                activityDataMap.setCal(Integer.toString(tempCalorie));
                                activityDataMap.setCalorieUnits("kcal");
                                activityDataMap.setDistance(Double.toString(tempdistance));
                                activityDataMap.setDistanceUnit("mi");
                                activityDataMap.setDistanceInMiles(Double.toString(tempdistance)); //The distance calculated using UW-302 is always in miles
                                // 脈拍
                                activityDataMap.setHeartRate(Integer.toString(0));
                                activityDataMap.setIsSynced("no");
                                // deviceID
                                activityDataMap.setDeviceId("UW-302");

                                //If the date is present in the ArrayList, add to it else add it as a new entry
                                if (dataActivity.containsKey(finaldate)) {
                                    //Add it to the existing hashmap
                                    ArrayList<Lifetrack_infobean> minTrackerData = dataActivity.get(finaldate);
                                    minTrackerData.add(activityDataMap);
                                    dataActivity.put(finaldate, minTrackerData);

                                } else {
                                    //Create a new hashmap for this
                                    ArrayList<Lifetrack_infobean> minTrackerData = new ArrayList<Lifetrack_infobean>();
                                    minTrackerData.add(activityDataMap);
                                    dataActivity.put(finaldate, minTrackerData);
                                }

                                if (finaldate.equalsIgnoreCase(currentDateString)) {
                                } else {

                                    if (!dateListSet.contains(finaldate)) {
                                        dateListSet.add(finaldate);
                                        currentDateString = finaldate;
                                    }


                                }


                                activityCompleteDataSet.add(activityDataMap);
                            }


                        }

                    } else if (data_type == 40) { //Case of BP
                        int fw_data_length = oneMinData[51];
                        int sys = 0;
                        int dia = 0;
                        int year = 0;
                        int month = 0;
                        int day = 0;
                        int hour = 0;
                        int minutes = 0;
                        int seconds = 0;
                        int pul = 0;

                        if (fw_data_length == 5) {
                            sys = ((oneMinData[fw_data_length + 84] & 0xff) << 8) | (oneMinData[fw_data_length + 83] & 0xff);
                            dia = ((oneMinData[fw_data_length + 86] & 0xff) << 8) | (oneMinData[fw_data_length + 85] & 0xff);
                            year = ((oneMinData[fw_data_length + 90] & 0xff) << 8) | (oneMinData[fw_data_length + 89] & 0xff);
                            month = oneMinData[fw_data_length + 91];
                            day = oneMinData[fw_data_length + 92];
                            hour = oneMinData[fw_data_length + 93];
                            minutes = oneMinData[fw_data_length + 94];
                            seconds = oneMinData[fw_data_length + 95];
                            pul = ((oneMinData[fw_data_length + 97] & 0xff) << 8) | (oneMinData[fw_data_length + 96] & 0xff);
                        } else {
                            //Case of UA-651
                            sys = ((oneMinData[fw_data_length + 83] & 0xff) << 8) | (oneMinData[fw_data_length + 82] & 0xff);
                            dia = ((oneMinData[fw_data_length + 85] & 0xff) << 8) | (oneMinData[fw_data_length + 84] & 0xff);
                            year = ((oneMinData[fw_data_length + 89] & 0xff) << 8) | (oneMinData[fw_data_length + 88] & 0xff);
                            month = oneMinData[fw_data_length + 90];
                            day = oneMinData[fw_data_length + 91];
                            hour = oneMinData[fw_data_length + 92];
                            minutes = oneMinData[fw_data_length + 93];
                            seconds = oneMinData[fw_data_length + 94];
                            pul = ((oneMinData[fw_data_length + 96] & 0xff) << 8) | (oneMinData[fw_data_length + 95] & 0xff);
                        }

                        //Now adding this data to the hashmap
                        HashMap<String, Object> bpDataMap = new HashMap<String, Object>();
                        bpDataMap.put("systolic", sys);
                        bpDataMap.put("diastolic", dia);
                        bpDataMap.put("pulse", pul);
                        bpDataMap.put("year", year);
                        bpDataMap.put("month", month);
                        bpDataMap.put("day", day);
                        bpDataMap.put("hour", hour);
                        bpDataMap.put("minutes", minutes);
                        bpDataMap.put("seconds", seconds);
                        bpDataMap.put("unit", "mmHg");
                        bloodpressureDataSet.add(bpDataMap);

                    } else if (data_type == 41) {
                        int fw_data_length = oneMinData[51];
                        int index = 52 + fw_data_length + 29;
                        //Getting the weight value
                        int weightValue = ((oneMinData[index + 2] & 0xff) << 8) | (oneMinData[index + 1] & 0xff);
                        //Getting the unit value
                        int unit = oneMinData[index];
                        String unitType = "";
                        //Unit type and converting weight based on unit received
                        double weight = 0;
                        if (unit == 0 || unit == 2) {
                            unitType = ADSharedPreferences.VALUE_WEIGHT_SCALE_UNITS_KG;
                            weightValue = weightValue * 5;
                            double valueFloat = weightValue;
                            weight = valueFloat / 1000; //Final weight value
                        } else if (unit == 1 || unit == 3) {
                            unitType = ADSharedPreferences.VALUE_WEIGHT_SCALE_UNITS_LBS;
                            double valueFloat = weightValue;
                            weight = valueFloat / 100;
                        }

                        //Converting the weight based on the unit received


                        int year = ((oneMinData[index + 4] & 0xff) << 8) | (oneMinData[index + 3] & 0xff);
                        int month = oneMinData[index + 5];
                        int day = oneMinData[index + 6];
                        int hour = oneMinData[index + 7];
                        int minutes = oneMinData[index + 8];
                        int seconds = oneMinData[index + 9];

                        //Time to populate the values
                        HashMap<String, Object> wsDataMap = new HashMap<String, Object>();
                        wsDataMap.put("weight", weight);
                        wsDataMap.put("year", year);
                        wsDataMap.put("month", month);
                        wsDataMap.put("day", day);
                        wsDataMap.put("hour", hour);
                        wsDataMap.put("minutes", minutes);
                        wsDataMap.put("seconds", seconds);
                        wsDataMap.put("unit", unitType);
                        weightDataSet.add(wsDataMap);
                    }
                }

                if (dataPacketEnd) {//Check if this was the last data packet coming in

                    byte[] end_datarequest = {
                            (byte) 0x03,
                            (byte) 0x01,
                            (byte) 0x58,
                            (byte) 0x03

                    };
                    BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomtrackerService);
                    BluetoothGattCharacteristic data_characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomtrackerChar1);
                    if (gattService != null) {
                        if (data_characteristic != null) {
                            data_characteristic.setValue(end_datarequest);
                            gatt.writeCharacteristic(data_characteristic);
                        }
                    }
                } else {
                    byte[] next_datarequest = {
                            (byte) 0x03,
                            (byte) 0x01,
                            (byte) 0x58,
                            (byte) 0x01

                    };
                    BluetoothGattService gattService = gatt.getService(ADGattUUID.AndCustomtrackerService);
                    BluetoothGattCharacteristic data_characteristic = gattService.getCharacteristic(ADGattUUID.AndCustomtrackerChar1);
                    if (gattService != null) {
                        if (data_characteristic != null) {
                            data_characteristic.setValue(next_datarequest);
                            gatt.writeCharacteristic(data_characteristic);
                        }
                    }
                }

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

    public static String byte2hex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
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
                        descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(descriptor);
                    }
                }
            }
        }

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

    protected void optimizeActivityData(BluetoothGatt gatt) {

        for (String test : dateListSet) {
        }

        for (String key : dataActivity.keySet()) {
            ArrayList<Lifetrack_infobean> sim_test = dataActivity.get(key);
        }


        for (Lifetrack_infobean dataTest : activityCompleteDataSet) {
        }
        //Sort out this array
        try {
            MeasuDataManager.sortDate(activityCompleteDataSet, true); //Sorting the given list in ascending order

            //Get the latest inserted activity data from the database
            ArrayList<Lifetrack_infobean> test = MeasuDataManager.sortDate(mDataBase.getAllActivityDetails(), true);
            long latestDateTimeMilli = 0;
            if (test.size() == 0) {
            } else {
                latestDateTimeMilli = Long.valueOf(test.get(test.size() - 1).getDateTimeStamp());
            }


            int pos = 0;
            //Traverse the list to find the position of the date that is greater than the date inserted.

            for (int i = 0; i < activityCompleteDataSet.size(); i++) {
                Lifetrack_infobean tempData = activityCompleteDataSet.get(i);
                long tempDateTimeMilli = Long.valueOf(tempData.getDateTimeStamp());
                if (tempDateTimeMilli > latestDateTimeMilli) {
                    pos = i;
                    break;
                } else {
                    activityCompleteDataSet.remove(i);
                    i = i - 1;
                }
            }
            //Traverse through the for loop starting with the new position
            int totalSteps = 0;
            int totalCalories = 0;
            //int totalSleep = 0;
            double totalDistance = 0;
            int j;
            ArrayList<Lifetrack_infobean> finalDataSet = new ArrayList<Lifetrack_infobean>();
            ArrayList<Lifetrack_infobean> dayValues = new ArrayList<Lifetrack_infobean>();
            for (int s = 0; s < dateListSet.size(); s++) {
                String tempDate = dateListSet.get(s);
                dayValues.clear();
                for (j = 0; j < activityCompleteDataSet.size(); j++) {

                    //Filter the activity list based on the match of the date
                    String datetime_insert = activityCompleteDataSet.get(j).getDate();
                    String timestamp = activityCompleteDataSet.get(j).getDateTimeStamp();
                    if (datetime_insert.equalsIgnoreCase(tempDate)) {
                        Lifetrack_infobean finalDataInfo = new Lifetrack_infobean();
                        finalDataInfo = activityCompleteDataSet.remove(j);
                        dayValues.add(finalDataInfo);
                        j = j - 1;

                    } else {
                    }

                }
                //Add up all values and put it as a final one
                j = 0;
                int totalSleep = 0;
                for (int size = 0; size < dayValues.size(); size++) {
                    int tempSteps = Integer.parseInt(dayValues.get(size).getSteps());
                    totalSteps = totalSteps + tempSteps;
                    int tempCalorie = Integer.parseInt(dayValues.get(size).getCal());
                    totalCalories = tempCalorie + totalCalories;
                    double tempDistance = Double.parseDouble(dayValues.get(size).getDistance());
                    totalDistance = totalDistance + tempDistance;
                    int tempTotalSleep = Integer.parseInt(dayValues.get(size).getSleep());
                    if (tempTotalSleep > totalSleep) {
                        totalSleep = tempTotalSleep; //Always store the greateast value of sleep
                    }

                }
                //Add this cumulative value as one entry
                //Create a new data structure here and send just that
                //To get the time stamp and sleep extract the last item in the list
                Lifetrack_infobean finalDataInfo = new Lifetrack_infobean();
                finalDataInfo.setDeviceId("UW-302");
                finalDataInfo.setIsSynced("no");
                finalDataInfo.setHeartRate(Integer.toString(0));
                finalDataInfo.setDistanceUnit("mi");
                finalDataInfo.setCalorieUnits("kcal");
                finalDataInfo.setStepsUnits("steps");
                finalDataInfo.setSleepUnit("min");
                //Getting date of the last objects
                finalDataInfo.setDate(dayValues.get(dayValues.size() - 1).getDate());
                finalDataInfo.setTime(dayValues.get(dayValues.size() - 1).getTime());
                finalDataInfo.setDateTimeStamp(dayValues.get(dayValues.size() - 1).getDateTimeStamp());
                finalDataInfo.setSleep(Integer.toString(totalSleep));
                finalDataInfo.setCal(Integer.toString(totalCalories));
                finalDataInfo.setSteps(Integer.toString(totalSteps));
                finalDataInfo.setDistance(Double.toString(totalDistance));
                finalDataSet.add(finalDataInfo);

            }

            //Now need to send this to the Dashboard
            mDataBase.lifetrackentry(finalDataSet);
            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_AM, true);

            Bundle valueBundle = new Bundle();
            valueBundle.putSerializable("activity_data", finalDataSet);
            valueBundle.putSerializable("bp_data", bloodpressureDataSet);
            valueBundle.putSerializable("weight_data", weightDataSet);
            sendBroadcast(TYPE_INDICATION_VALUE, ADGattUUID.AndCustomtrackerService.toString(), valueBundle);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void optimizeActivityData1(BluetoothGatt gatt) {

        //Sort out this array
        try {
            for (String test : dateListSet) {
            }
            for (String key : dataActivity.keySet()) {
                ArrayList<Lifetrack_infobean> sim_test = dataActivity.get(key);
            }


            //Get the latest inserted activity data from the database
            ArrayList<Lifetrack_infobean> test = MeasuDataManager.sortDate(mDataBase.getAllActivityDetails(), true);
            long latestDateTimeMilli = 0;
            if (test.size() == 0) {
            } else {
                latestDateTimeMilli = Long.valueOf(test.get(test.size() - 1).getDateTimeStamp());
            }

            //Traverse through the hashmap and sort it
            //Get the latest date time stamp

            ArrayList<Lifetrack_infobean> finalDataSet = new ArrayList<Lifetrack_infobean>();
            for (String key : dataActivity.keySet()) {
                ArrayList<Lifetrack_infobean> sim_test = dataActivity.get(key);
                MeasuDataManager.sortDate(sim_test, true);
                int totalSteps = 0;
                int totalCalories = 0;
                int totalSleep = 0;
                double totalDistance = 0;
                boolean entered = false;
                int lastEnteredDatePosition = 0;
                for (int i = 0; i < sim_test.size(); i++) {
                    Lifetrack_infobean tempData = sim_test.get(i);
                    long tempDateTimeMilli = Long.valueOf(tempData.getDateTimeStamp());
                    if (tempDateTimeMilli <= latestDateTimeMilli) {
                        continue;
                    } else {
                        entered = true;
                        int tempSteps = Integer.parseInt(sim_test.get(i).getSteps());
                        totalSteps = totalSteps + tempSteps;
                        int tempCalorie = Integer.parseInt(sim_test.get(i).getCal());
                        totalCalories = tempCalorie + totalCalories;
                        double tempDistance = Double.parseDouble(sim_test.get(i).getDistance());
                        totalDistance = totalDistance + tempDistance;
                        int tempTotalSleep = Integer.parseInt(sim_test.get(i).getSleep());
                        if (tempTotalSleep > totalSleep) {
                            totalSleep = tempTotalSleep; //Always store the greateast value of sleep
                        }
                    }
                }
                if (entered) {
                    entered = false;
                    Lifetrack_infobean finalDataInfo = new Lifetrack_infobean();
                    finalDataInfo.setDeviceId("UW-302");
                    finalDataInfo.setIsSynced("no");
                    finalDataInfo.setHeartRate(Integer.toString(0));
                    finalDataInfo.setDistanceUnit("mi");
                    finalDataInfo.setCalorieUnits("kcal");
                    finalDataInfo.setStepsUnits("steps");
                    finalDataInfo.setSleepUnit("min");
                    //Getting date of the last objects
                    finalDataInfo.setDate(sim_test.get(sim_test.size() - 1).getDate());
                    finalDataInfo.setTime(sim_test.get(sim_test.size() - 1).getTime());
                    finalDataInfo.setDateTimeStamp(sim_test.get(sim_test.size() - 1).getDateTimeStamp());
                    finalDataInfo.setSleep(Integer.toString(totalSleep));
                    finalDataInfo.setCal(Integer.toString(totalCalories));
                    finalDataInfo.setSteps(Integer.toString(totalSteps));
                    finalDataInfo.setDistance(Double.toString(totalDistance));
                    finalDataSet.add(finalDataInfo);
                }

            }


            for (int k = 0; k < finalDataSet.size(); k++) {
            }


            //Now need to send this to the Dashboard
            mDataBase.lifetrackentry(finalDataSet);
            MeasuDataManager measuDataManager = ((AndMedical_App_Global) getApplication()).getMeasuDataManager();
            measuDataManager.syncMeasudata(MeasuDataManager.MEASU_DATA_TYPE_AM, true);

            Bundle valueBundle = new Bundle();
            valueBundle.putSerializable("activity_data", finalDataSet);
            valueBundle.putSerializable("bp_data", bloodpressureDataSet);
            valueBundle.putSerializable("weight_data", weightDataSet);
            sendBroadcast(TYPE_INDICATION_VALUE, ADGattUUID.AndCustomtrackerService.toString(), valueBundle);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String hexToBinary(String hex) {
        return new BigInteger(hex, 16).toString(2);
    }

    public long convertDateintoMs(String date) {
        long final_birth_date_timestamp = 0;
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date converteddate = null;
        try {
            converteddate = (Date) formatter.parse(date);
            final_birth_date_timestamp = converteddate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return final_birth_date_timestamp;
    }

}
