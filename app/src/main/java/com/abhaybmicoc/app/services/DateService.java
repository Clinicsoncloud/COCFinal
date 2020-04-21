package com.abhaybmicoc.app.services;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateService {


    public static final String DATE_FORMAT = "dd-MMM-yyyy hh:mm:ss";
    public static final String YYYY_MM_DD_HMS = "yyyy-MM-dd hh:mm:ss";
    public static final String YYYY_MM_DD_T_HMS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * @param dateOfBirth
     * @return
     */
    public static int getAgeFromStringDate(String dateOfBirth) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(dateOfBirth);
        } catch (ParseException e) {
            return 0;
        }

        Calendar calDateOfBirth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        calDateOfBirth.setTime(date);

        int year = calDateOfBirth.get(Calendar.YEAR);
        int month = calDateOfBirth.get(Calendar.MONTH);
        int day = calDateOfBirth.get(Calendar.DAY_OF_MONTH);

        calDateOfBirth.set(year, month + 1, day);

        int age = today.get(Calendar.YEAR) - calDateOfBirth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < calDateOfBirth.get(Calendar.DAY_OF_YEAR))
            age--;

        return age;
    }

    public static String formatDateFromString(String date, String fromDatePattern, String toDatePattern) {
        String formattedDate;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat inputDateFormat = new SimpleDateFormat(fromDatePattern);

        SimpleDateFormat formatter = new SimpleDateFormat(toDatePattern);
        try {
            Date parsedDate = inputDateFormat.parse(date);
            formattedDate = formatter.format(parsedDate);
        } catch (ParseException e) {
            return null;
        }

        return formattedDate;
    }

    public static String getCurrentDateTime(String format) {

        DateFormat dateFormatter = new SimpleDateFormat(format);
        dateFormatter.setLenient(false);
        Date today = new Date();
        String s = dateFormatter.format(today);
        Log.e("today_Date", "" + s);
        return s;
    }

}
