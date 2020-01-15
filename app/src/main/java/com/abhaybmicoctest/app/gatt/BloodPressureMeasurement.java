package com.abhaybmicoctest.app.gatt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import com.abhaybmicoctest.app.base.ADGattService;

import java.util.Calendar;
import java.util.Locale;


public class BloodPressureMeasurement extends ADGattService {
	
	public static Bundle readCharacteristic(BluetoothGattCharacteristic characteristic) {
		
		Bundle bundle = new Bundle();
		int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		String flagString = Integer.toBinaryString(flag);
		int offset=0;
		for(int index = flagString.length(); 0 < index ; index--) {
			String key = flagString.substring(index-1 , index);
			
			if(index == flagString.length()) {
				if(key.equals("0")) {
					// mmHg
					bundle.putString(KEY_UNIT, "mmHg");
				}
				else {
					// kPa 
					bundle.putString(KEY_UNIT, "kPa");
				}
				// Unit
				offset+=1;
				bundle.putFloat(KEY_SYSTOLIC, characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset));
				offset+=2;
				bundle.putFloat(KEY_DIASTOLIC, characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset));
				offset+=2;
				bundle.putFloat(KEY_MEAS_ARTERIAL_PRESSURE, characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset));
				offset+=2;
			}
			else if(index == flagString.length()-1) {
				if(key.equals("1")) {
					// Time Stamp
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
					// 日時が存在しない場合、現在日時を格納する。
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
					// Pulse Rate
					bundle.putFloat(KEY_PULSE_RATE, characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset));
					offset+=2;
				}
			}
			else if(index == flagString.length()-3) {
				// UserID
			}
			else if(index == flagString.length()-4) {
				// Measurement Status Flag
				int statusFalg = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
				String statusFlagString = String.format("%6s",statusFalg).replace(" ", "0");
				for(int i = statusFlagString.length(); 0 < i ; i--) {
					String status = statusFlagString.substring(i-1 , i);
					if(i == statusFlagString.length()) {
						bundle.putInt(KEY_BODY_MOVEMENT_DETECTION, (status.endsWith("1"))? 1 : 0 );
					}
					else if(i == statusFlagString.length() - 1) {
						bundle.putInt(KEY_CUFF_FIT_DETECTION, (status.endsWith("1"))? 1 : 0 );
					}
					else if(i == statusFlagString.length() - 2) {
						bundle.putInt(KEY_IRREGULAR_PULSE_DETECTION, (status.endsWith("1"))? 1 : 0 );
					}
					else if(i == statusFlagString.length() - 3) {
						i--;
						String secondStatus = statusFlagString.substring(i-1 , i);
						if(status.endsWith("1") && secondStatus.endsWith("0")) {
							bundle.putInt(KEY_PULSE_RATE_RANGE_DETECTION, 1);
						}
						else if(status.endsWith("0") && secondStatus.endsWith("1")) {
							bundle.putInt(KEY_PULSE_RATE_RANGE_DETECTION, 2);
						}
						else if(status.endsWith("1") && secondStatus.endsWith("1")) {
							bundle.putInt(KEY_PULSE_RATE_RANGE_DETECTION, 3);
						}
						else {
							bundle.putInt(KEY_PULSE_RATE_RANGE_DETECTION, 0);
						}
					}
					else if(i == statusFlagString.length() - 5) {
						bundle.putInt(KEY_MEASUREMENT_POSITION_DETECTION,(status.endsWith("1"))? 1 : 0);
					}
				}
			}
		}
		return bundle;
	}
}
