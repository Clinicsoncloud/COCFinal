package com.abhaybmi.app.utilities;

import android.content.Context;

import java.text.DateFormat;
import java.util.Date;

public class ADDateParser {

	/**
	 * @param context
	 * @param date
	 * @return dateString
	 */
	public static String parseMediumDate(Context context, Date date) {
		String result;
		DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(context);
				
		result = dateFormat.format(date);
		return result;
	}

	/**
	 * @param context
	 * @param date
	 * @return
	 */
	public static String parseTime(Context context, Date date) {
		String result;
		DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(context);
		result = dateFormat.format(date);
		return result;
	}


}
