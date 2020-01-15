package com.abhaybmicoctest.app.gatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import com.abhaybmicoctest.app.base.ADGattService;
import com.abhaybmicoctest.app.utilities.ADSharedPreferences;

import java.util.Calendar;
import java.util.Locale;

public class ThermometerMeasurement extends ADGattService {
	public static Bundle readCharacteristic(BluetoothGattCharacteristic characteristic) {
		Bundle bundle = new Bundle();
		int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		String flagString = Integer.toBinaryString(flag);
		int offset=0;
		for(int index = flagString.length(); 0 < index ; index--) {
			String key = flagString.substring(index-1 , index);
			
			if(index == flagString.length()) {
				if(key.equals("0")) {
					bundle.putString(KEY_TEMPERATURE_UNIT, ADSharedPreferences.VALUE_TEMPERATURE_UNIT_C);
				}
				else {
					bundle.putString(KEY_TEMPERATURE_UNIT, ADSharedPreferences.VALUE_TEMPERATURE_UNIT_F);
				}
				// Unit
				offset+=1;
				
				// Value
				float value = (float)(characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, offset));
				bundle.putFloat(KEY_TEMPERATURE_VALUE, value);
				offset+=4;
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
					int typeId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
					bundle.putInt(KEY_TEMPERATURE_TYPE, typeId);
					offset+=1;
				}
			}
		}
		return bundle;
	}
}
