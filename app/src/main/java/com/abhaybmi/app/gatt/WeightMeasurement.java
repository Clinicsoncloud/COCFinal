package com.abhaybmi.app.gatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import com.abhaybmi.app.base.ADGattService;
import com.abhaybmi.app.utilities.ADSharedPreferences;

import java.util.Calendar;
import java.util.Locale;


public class WeightMeasurement extends ADGattService {

	public static Bundle readCharacteristic(BluetoothGattCharacteristic characteristic) {
		
		Bundle bundle = new Bundle();
		int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		String flagString = Integer.toBinaryString(flag);
		int offset=0;
		for(int index = flagString.length(); 0 < index ; index--) {
			String key = flagString.substring(index-1 , index);
			
			if(index == flagString.length()) {
				double convertValue = 0;
				if(key.equals("0")) {
					bundle.putString(KEY_UNIT, ADSharedPreferences.VALUE_WEIGHT_SCALE_UNITS_KG);
					convertValue = 0.005f;
				}
				else {
					bundle.putString(KEY_UNIT, ADSharedPreferences.VALUE_WEIGHT_SCALE_UNITS_LBS);
					convertValue = 0.01f;
				}
				// Unit
				offset+=1;
				
				// Value
				double value = (double)(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset)) * convertValue;
				bundle.putDouble(KEY_WEIGHT, value);
				offset+=2;
			}
			else if(index == flagString.length()-1) {
				if(key.equals("1")) {
					
					bundle.putInt(KEY_YEAR, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset));
					offset+=2;
					bundle.putInt(KEY_MONTH, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
					offset+=1;
					bundle.putInt(KEY_DAY, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
					offset+=1;
					
					bundle.putInt(KEY_HOURS, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
					offset+=1;
					bundle.putInt(KEY_MINUTES, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
					offset+=1;
					bundle.putInt(KEY_SECONDS, characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset));
					offset+=1;
				}
				else {
					Calendar calendar = Calendar.getInstance(Locale.getDefault());
					bundle.putInt(KEY_YEAR, calendar.get(Calendar.YEAR));
					bundle.putInt(KEY_MONTH, calendar.get(Calendar.MONTH)+1);
					bundle.putInt(KEY_DAY, calendar.get(Calendar.DAY_OF_MONTH));
					bundle.putInt(KEY_HOURS, calendar.get(Calendar.HOUR));
					bundle.putInt(KEY_MINUTES, calendar.get(Calendar.MINUTE));
					bundle.putInt(KEY_SECONDS, calendar.get(Calendar.SECOND));
				}
			}
			else if(index == flagString.length()-2) {
				if(key.equals("1")) {
					offset+=1;
				}
			}
			else if(index == flagString.length()-3) {
				if(key.equals("1")) {
					// BMI and Height
				}
			}
		}
		return bundle;
	}
}
