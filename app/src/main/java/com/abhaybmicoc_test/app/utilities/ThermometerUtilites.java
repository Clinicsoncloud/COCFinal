package com.abhaybmicoc_test.app.utilities;

import android.content.Context;

import com.abhaybmicoc_test.app.R;
import com.abhaybmicoc_test.app.entities.Lifetrack_infobean;

import java.text.DecimalFormat;
import java.util.Locale;

public class ThermometerUtilites {
	private static final String THERMOMETER_NAME_LADY = "UT201BLEF";
	private static final String THERMOMETER_NAME_GENERAL = "UT201BLE";
	
	private static final DecimalFormat LADY_FORMAT = new DecimalFormat("0.00");
	private static final DecimalFormat GENERAL_FORMAT = new DecimalFormat("0.0");
	public static final float ERROR_VALUE = 8388607f;
	
	/**
	 * @param data
	 * @return temperature
	 */
	public static final float convertValueFromDegreeUnit(Lifetrack_infobean data) {
		float resultValue = 0;
		if (data != null) {
			resultValue = Float.valueOf(data.getThermometer());
		}
		String defaultUnit = "";
		if (Locale.getDefault().equals(Locale.JAPAN)) {
			defaultUnit = ADSharedPreferences.VALUE_TEMPERATURE_UNIT_C;
		} else {
			defaultUnit = ADSharedPreferences.VALUE_TEMPERATURE_UNIT_F;
		}

		String dataUnit = data.getThermometerUnit();
		String appUnit = ADSharedPreferences.getString(ADSharedPreferences.KEY_TEMPERATURE_UNITS, defaultUnit);
		
		if (appUnit.equalsIgnoreCase(ADSharedPreferences.VALUE_TEMPERATURE_UNIT_C)) {
			if (dataUnit.equalsIgnoreCase(ADSharedPreferences.VALUE_TEMPERATURE_UNIT_C)) {
				resultValue = Float.valueOf(data.getThermometer());
			} else {
				resultValue = 5f / 9f * (Float.valueOf(data.getThermometer()) - 32f);
			}
		} else {
			if (dataUnit.equalsIgnoreCase(ADSharedPreferences.VALUE_TEMPERATURE_UNIT_F)) {
				resultValue = Float.valueOf(data.getThermometer());
			} else {
				resultValue = 9f / 5f * Float.valueOf(data.getThermometer()) + 32f;
			}
		}
		return resultValue;
	}
	
	/**
	 * @param data
	 * @return  DecimalFormat
	 */
	public static final DecimalFormat getDegreeUnitFromThermometerName(Lifetrack_infobean data) {
		DecimalFormat resultFormat = null;
		String thermometerName = "";
		if (data != null) {
			if (data.getThermometerDeviceName() != null) {
				if (data.getThermometerDeviceName().split("_").length == 3) {
					thermometerName = data.getThermometerDeviceName().split("_")[1];
					if (thermometerName.equalsIgnoreCase(THERMOMETER_NAME_GENERAL)) {
						resultFormat = GENERAL_FORMAT;
					} else if (thermometerName.equalsIgnoreCase(THERMOMETER_NAME_LADY)) {
						resultFormat = LADY_FORMAT;
					} else {
					}
				}
			}
		}
		return resultFormat;
	}
	
	/**
	 * @return
	 */
	public static final String getCurrentUnit(Context context) {
		String result;
		String defaultUnit = "";
		if (Locale.getDefault().equals(Locale.JAPAN)) {
			defaultUnit = ADSharedPreferences.VALUE_TEMPERATURE_UNIT_C;
		} else {
			defaultUnit = ADSharedPreferences.VALUE_TEMPERATURE_UNIT_F;
		}

		String appUnit = ADSharedPreferences.getString(ADSharedPreferences.KEY_TEMPERATURE_UNITS, defaultUnit);
		if (appUnit.equalsIgnoreCase(ADSharedPreferences.VALUE_TEMPERATURE_UNIT_C)) {
			result = context.getResources().getString(R.string.thermometer_degree_unit_celsius);
		} else if (appUnit.equalsIgnoreCase(ADSharedPreferences.VALUE_TEMPERATURE_UNIT_F)) {
			result = context.getResources().getString(R.string.thermometer_degree_unit_fahrenheit);
		} else {
			result = context.getResources().getString(R.string.value_error);
		}
		
		return result;
	}
	
	/**
	 * @param data
	 */
	public static final boolean checkErrData(Lifetrack_infobean data) {
		if (data != null) {
			if (data.getThermometerValue() != null && !data.getThermometerValue().isEmpty()) {
				float resultValue = Float.valueOf(data.getThermometerValue());
				if (resultValue != ERROR_VALUE) {
					return true;
				}
			}
		}
		return false;
	}
}
