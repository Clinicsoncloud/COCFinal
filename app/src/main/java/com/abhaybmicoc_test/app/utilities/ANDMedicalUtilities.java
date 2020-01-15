package com.abhaybmicoc_test.app.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.abhaybmicoc_test.app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class ANDMedicalUtilities {
	
	public static final boolean APP_STAND_ALONE_MODE = true;
	

	public synchronized static String FormatDashboardDispDate(Context context, String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return ADDateParser.parseMediumDate(context, date) +" "+ ADDateParser.parseTime(context, date);
	}

	/*
	 * TO DO may be remove in future
	 */

	public static AlertDialog CreateDialog(final Context context, String Message, final Activity activity) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(Message);

		// set dialog message
		alertDialogBuilder
				.setMessage(R.string.dialog_to_exit)
				.setCancelable(false)
				.setPositiveButton(R.string.text_yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent homeIntent = new Intent(
										Intent.ACTION_MAIN);
								homeIntent.addCategory(Intent.CATEGORY_HOME);
								// homeIntent
								// .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
								// );
								context.startActivity(homeIntent);
								activity.finish();

							}
						})
				.setNegativeButton(R.string.text_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		return alertDialog;
	}
}
