package com.abhaybmicoc.app.utils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.abhaybmicoc.app.R;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DTU {
    // TODO Time...........

    public final static String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public final static String DMY = "dd-MM-yyyy";
    public final static String YMD = "yyyy-MM-dd";
    public final static String ymd = "yyyyMMdd";
    public final static String HMS = "HH:mm:ss";
    public final static String HM = "HH:mm";
    public static final int FLAG_OLD_AND_NEW = 2;
    public static final DateFormat TWELVE_TF = new SimpleDateFormat("hh:mma");
    // Replace with kk:mm if you want 1-24 interval
    public static final DateFormat TWENTY_FOUR_TF = new SimpleDateFormat(
            "HH:mm");
    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static String time;
    public static int currentHour, currentMinute, currentSeconds;
    public static int currentYear, currentMonth, currentDay;
    public static int myFlg = 0;
    public static String cmpftime, cmpttime, cmpfdate, cmptdate, aTime = "";

    public static final int FLAG_ONLY_NEW = 1;
//    public static final int FLAG_OLD_AND_NEW = 2;

    // TODO date..........

    public static String showTimePickerDialog(final Context appContext,
                                              final TextView eStartTime) {

        final Calendar c = Calendar.getInstance();
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        currentMinute = c.get(Calendar.MINUTE);
        currentSeconds = c.get(Calendar.SECOND);
        TimePickerDialog tpd = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minutes) {
                        int hour = hourOfDay;
                        int minute = minutes;
                        String time = "" + hourOfDay + "" + minutes + "00";
                        DTU.time = time;
                        int flg = 0;
                        String strHour, strMinutes, strAMPM;

                        if (hour > 12) {
                            flg = 1;
                            hour = hour - 12;
                            strAMPM = "PM";
                        } else {
                            strAMPM = "AM";
                        }
                        if (hour < 10) {
                            strHour = "0" + hour;
                        } else {
                            strHour = "" + hour;
                        }
                        if (minute < 10) {
                            strMinutes = "0" + minute;
                        } else {
                            strMinutes = "" + minute;
                        }
                        eStartTime
                                .setText(strHour + ":" + strMinutes + strAMPM);

                    }
                }, currentHour, currentMinute, false);
        tpd.show();

        return "";
    }

    public static String showTimePickerDialogNew(final Context appContext,
                                                 final TextView eStartTime) {

        final Calendar c = Calendar.getInstance();
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        currentMinute = c.get(Calendar.MINUTE);
        currentSeconds = c.get(Calendar.SECOND);
        TimePickerDialog tpd = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minutes) {
                        int hour = hourOfDay;
                        int minute = minutes;
                        String time = "" + hourOfDay + "" + minutes + "00";
                        DTU.time = time;
                        int flg = 0;
                        String strHour, strMinutes, strAMPM;

                        if (hour > 12) {
                            flg = 1;
                            hour = hour - 12;
                            strAMPM = "PM";
                        } else {
                            strAMPM = "AM";
                        }
                        if (hour < 10) {
                            strHour = "0" + hour;
                        } else {
                            strHour = "" + hour;
                        }
                        if (minute < 10) {
                            strMinutes = "0" + minute;
                        } else {
                            strMinutes = "" + minute;
                        }

                        eStartTime
                                .setText(strHour + ":" + strMinutes + strAMPM);

                    }
                }, currentHour, currentMinute, false);
        tpd.show();

        return "";
    }

    public static String showTime24HourPickerDialog(final Context appContext, final TextView eStartTime) {

        final Calendar c = Calendar.getInstance();
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        currentMinute = c.get(Calendar.MINUTE);
        currentSeconds = c.get(Calendar.SECOND);
        TimePickerDialog tpd = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minutes) {
                        int hour = hourOfDay;
                        int minute = roundTo5(minutes);
                        if (minute == 60) {
                            minute = 00;
                            hour = hour + 1;
                        }
                        String time = "" + hourOfDay + "" + minutes + "00";
//                        Log.e("time",""+hour + ":" + minute);

                        eStartTime.setText(hour + ":" + minute);

                    }
                }, currentHour, currentMinute, false);
        tpd.show();

        return "";
    }

    public static String showTime24HourPickerDialog(final Context appContext, String inputTime,
                                                    final TextView eStartTime) {

        final Calendar c = Calendar.getInstance();
        String[] spiltedTime = inputTime.split(":");

        currentHour = Integer.parseInt(spiltedTime[0]);
        currentMinute = Integer.parseInt(spiltedTime[1]);
        currentSeconds = 00;
//		currentHour = c.get(Calendar.HOUR_OF_DAY);
//		currentMinute = c.get(Calendar.MINUTE);
//		currentSeconds = c.get(Calendar.SECOND);
        TimePickerDialog tpd = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minutes) {
                        int hour = hourOfDay;
                        int minute = roundTo5(minutes);
                        if (minute == 60) {
                            minute = 0;
                            hour = hour + 1;
                        }
                        String time = "" + hourOfDay + "" + minutes + "00";
                        if (minute < 10) {
                            eStartTime.setText(hour + ":0" + minute);
                        } else {
                            eStartTime.setText(hour + ":" + minute);
                        }

                    }
                }, currentHour, currentMinute, true);
        tpd.show();

        return "";
    }

    public static String showTime24HourPickerDialog(final Context appContext, String inputTime,
                                                    final EditText eStartTime) {

        final Calendar c = Calendar.getInstance();
        String[] spiltedTime = inputTime.split(":");

        currentHour = Integer.parseInt(spiltedTime[0]);
        currentMinute = Integer.parseInt(spiltedTime[1]);
        currentSeconds = 00;
//		currentHour = c.get(Calendar.HOUR_OF_DAY);
//		currentMinute = c.get(Calendar.MINUTE);
//		currentSeconds = c.get(Calendar.SECOND);
        TimePickerDialog tpd = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minutes) {
                        int hour = hourOfDay;
                        int minute = roundTo5(minutes);
                        if (minute == 60) {
                            minute = 0;
                            hour = hour + 1;
                        }
                        String time = "" + hourOfDay + "" + minutes + "00";
                        if (minute < 10) {
                            eStartTime.setText(hour + ":0" + minute);
                        } else {
                            eStartTime.setText(hour + ":" + minute);
                        }

                    }
                }, currentHour, currentMinute, true);
        tpd.show();

        return "";
    }

    public static String showFutureTimePickerDialog(final Context appContext, String inputTime,
                                                    final TextView eStartTime) {

        final Calendar c = Calendar.getInstance();
        String[] spiltedTime = inputTime.split(":");

        currentHour = Integer.parseInt(spiltedTime[0]);
        currentMinute = Integer.parseInt(spiltedTime[1]);
        currentSeconds = 00;
//		currentHour = c.get(Calendar.HOUR_OF_DAY);
//		currentMinute = c.get(Calendar.MINUTE);
//		currentSeconds = c.get(Calendar.SECOND);
        TimePickerDialog tpd = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minutes) {
                        int hour = hourOfDay;
                        int minute = roundTo5(minutes);
                        if (minute == 60) {
                            minute = 0;
                            hour = hour + 1;
                        }

                        String time = "" + hourOfDay + "" + minutes + "00";
                        final Calendar c = Calendar.getInstance();
                        currentHour = c.get(Calendar.HOUR_OF_DAY);
                        currentMinute = c.get(Calendar.MINUTE);
                        currentSeconds = c.get(Calendar.SECOND);

                        if (hour > currentHour) {
                            if (minute < 10) {

                                eStartTime.setText(hour + ":0" + minute);
                            } else {

                                eStartTime.setText(hour + ":" + minute);
                            }

                        } else if (hour == currentHour) {
                            if (minute >= currentMinute) {
                                if (minute < 10) {

                                    eStartTime.setText(hour + ":0" + minute);
                                } else {

                                    eStartTime.setText(hour + ":" + minute);
                                }
                            }
                        } else {
                            Toast.makeText(appContext, "Selected time has already passed!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, currentHour, currentMinute, true);
        tpd.show();

        return "";
    }

    public static String showFutureTimePickerDialog(final Context appContext, String inputTime,
                                                    final EditText eStartTime) {

        final Calendar c = Calendar.getInstance();
        String[] spiltedTime = inputTime.split(":");

        currentHour = Integer.parseInt(spiltedTime[0]);
        currentMinute = Integer.parseInt(spiltedTime[1]);
        currentSeconds = 00;

        TimePickerDialog tpd = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minutes) {
                        int hour = hourOfDay;
                        int minute = roundTo5(minutes);
                        if (minute == 60) {
                            minute = 0;
                            hour = hour + 1;
                        }
                        String time = "" + hourOfDay + "" + minutes + "00";
                        final Calendar c = Calendar.getInstance();
                        currentHour = c.get(Calendar.HOUR_OF_DAY);
                        currentMinute = c.get(Calendar.MINUTE);
                        currentSeconds = c.get(Calendar.SECOND);
                        if (hour > currentHour) {
                            if (minute < 10) {
                                eStartTime.setText(hour + ":0" + minute);
                            } else {
                                eStartTime.setText(hour + ":" + minute);
                            }
                        } else if (hour == currentHour) {
                            if (minute >= currentMinute) {
                                if (minute < 10) {
                                    eStartTime.setText(hour + ":0" + minute);
                                } else {
                                    eStartTime.setText(hour + ":" + minute);
                                }
                            }
                        } else {
                            Toast.makeText(appContext, "Selected time has already passed!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, currentHour, currentMinute, true);
        tpd.show();

        return "";
    }

    public static String showTime24HourPickerDialog(
            final Context appContext, String inputTime, final EditText eStartTime, String strTime) {

        final Calendar c = Calendar.getInstance();
        String[] time = strTime.split(":");
        currentHour = Integer.parseInt(time[0]);
        currentMinute = Integer.parseInt(time[1]);
        currentSeconds = 00;
        TimePickerDialog tpd = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minutes) {
                        int hour = hourOfDay;
                        int minute = minutes;
                        String time = "" + hourOfDay + "" + minutes + "00";

                        eStartTime
                                .setText(hour + ":" + minute);

                    }
                }, currentHour, currentMinute, true);
        tpd.show();

        return "";
    }

    public static String getYYYYMD(String dt) {
        try {
            // Converts dd-mm-yy to mm-dd-yy Added on 05/12/2013
            String dd = "", mm = "", yyyyy = "", time = "";
            if (dt.contains(" ")) {
                String dtarray[] = dt.split(" ");
                dt = dtarray[0];
            }
//            String dtarray[] = dt.split(" ");
//            dt = dtarray[0];// 2015-02-23
//            time = dtarray[1];// 11:30:30

            int i = 0;

            for (String retval : dt.split("-")) {
                if (i == 0)
                    dd = retval;
                else if (i == 1)
                    mm = retval;
                else
                    yyyyy = retval;
                i++;
            }
            return (yyyyy + "-" + mm + "-" + dd).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public static void showTimePickerDialogForFixTime(final Context appContext,
                                                      final TextView eStartTime, final String startTime,
                                                      final String endTime) {
        final Calendar c = Calendar.getInstance();
        currentHour = c.get(Calendar.HOUR_OF_DAY);
        currentMinute = c.get(Calendar.MINUTE);
        currentSeconds = c.get(Calendar.SECOND);
        TimePickerDialog tpd = new TimePickerDialog(appContext,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minutes) {
                        int hour = hourOfDay;
                        int minute = minutes;
                        String time = "" + hourOfDay + "" + minutes + "00";
                        DTU.time = time;
                        int flg = 0;
                        String strHour, strMinutes, strAMPM;

                        if (hour > 12) {
                            flg = 1;
                            hour = hour - 12;
                            strAMPM = "PM";
                        } else {
                            strAMPM = "AM";
                        }
                        if (hour < 10) {
                            strHour = "0" + hour;
                        } else {
                            strHour = "" + hour;
                        }
                        if (minute < 10) {
                            strMinutes = "0" + minute;
                        } else {
                            strMinutes = "" + minute;
                        }
                        eStartTime
                                .setText(strHour + ":" + strMinutes + strAMPM);

                    }
                }, currentHour, currentMinute, false);
        tpd.show();
    }

    public static String getCurrentDateTimeStamp(String format) {

        DateFormat dateFormatter = new SimpleDateFormat(format);
        dateFormatter.setLenient(false);
        Date today = new Date();
        String s = dateFormatter.format(today);
        Log.e("today_Date", "" + s);
        return s;
    }

    public static String getTimeAdd(String format, int addMin) {

        Date today = new Date();
        Calendar calender = Calendar.getInstance();
        calender.setTime(today);
        if (today.getHours() != 23) {
            calender.add(Calendar.MINUTE, addMin);
        }
        today = calender.getTime();

        DateFormat dateFormatter = new SimpleDateFormat(DTU.HM);
        dateFormatter.setLenient(false);
        int roundMin = roundTo5(today.getMinutes());
        if (roundMin >= 60)
            roundMin = 55;
        today.setMinutes(roundMin);
        String s = dateFormatter.format(today);
        //String[] spitedTime;
        return s;
    }

    public static String getTime() {

        Date date = new Date();
        String time = date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();

        return time;
    }

    public static String getDateAdd(String format, int day) {

        Date today = new Date();
        Calendar calender = Calendar.getInstance();
        calender.setTime(today);
        calender.add(Calendar.DATE, day);
        today = calender.getTime();

        DateFormat dateFormatter = new SimpleDateFormat(format);
        dateFormatter.setLenient(false);
        String s = dateFormatter.format(today);
        //String[] spitedTime;
        return s;
    }

    public static String getCurrentDate() {
        try {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int monthOfYear = c.get(Calendar.MONTH);
            int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
            String date_selected = "";

            if ((monthOfYear >= 0 && monthOfYear < 9)
                    && (dayOfMonth > 0 && dayOfMonth < 10))
                date_selected = String.valueOf(year) + "-0"
                        + String.valueOf(monthOfYear + 1) + "-0"
                        + String.valueOf(dayOfMonth);
            else if (monthOfYear >= 0 && monthOfYear < 9)
                date_selected = String.valueOf(year) + "-0"
                        + String.valueOf(monthOfYear + 1) + "-"
                        + String.valueOf(dayOfMonth);
            else if (dayOfMonth > 0 && dayOfMonth < 10)
                date_selected = String.valueOf(year) + "-"
                        + String.valueOf(monthOfYear + 1) + "-0"
                        + String.valueOf(dayOfMonth);
            else
                date_selected = String.valueOf(year) + "-"
                        + String.valueOf(monthOfYear + 1) + "-"
                        + String.valueOf(dayOfMonth);
            return date_selected;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getddmmyyDate(String dt) {
        // Converts mm-dd-yy format to dd-mm-yy Added on 05/12/2013
        String dd = "", mm = "", yy = "";
        int i = 0;
        try {
            for (String retval : dt.split("-")) {
                if (i == 0)
                    yy = retval;
                else if (i == 1)
                    mm = retval;
                else
                    dd = retval;

                i++;
            }
            return (yy + "-" + mm + "-" + dd).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getddmmyyyyDate(String dt, String dateTime) {
        try {
            // Converts mm-dd-yy format to dd-mm-yy Added on 05/12/2013
            String dd = "", mm = "", yy = "", time = "";
            if (dateTime != null || dateTime.equals("dateTime")) {
                String dtarray[] = dt.split(" ");

                dt = dtarray[0];// 2015-02-23
                time = dtarray[1];// 11:30:30

            }

            int i = 0;

            for (String retval : dt.split("-")) {
                if (i == 0)
                    yy = retval;
                else if (i == 1)
                    mm = retval;
                else
                    dd = retval;

                i++;
            }
            return (dd + "-" + mm + "-" + yy).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getyymmddDate(String dt)
    // Converts mm-dd-yy format to dd-mm-yy Added on 05/12/2013
    {
        String dd = "", mm = "", yy = "";
        int i = 0;
        try {
            for (String retval : dt.split("-")) {
                if (i == 0)
                    dd = retval;
                else if (i == 1)
                    mm = retval;
                else
                    yy = retval;
                i++;
            }
            return (yy + "-" + mm + "-" + dd).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void showDatePickerDialog(Context context, final int dateFlg, final EditText dateEditText) {

        // Displays Date picker
        final Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datepicker = new DatePickerDialog(context, /*R.style.datepicker*/AlertDialog.THEME_HOLO_DARK,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int monthOfYear, int dayOfMonth) {
                        int year = selectedYear;
                        int month = monthOfYear;
                        int day = dayOfMonth;

                        if (dateFlg == DTU.FLAG_ONLY_NEW) {
                            if ((year != currentYear)
                                    || (month < currentMonth && year == currentYear)
                                    || (day < currentDay && year == currentYear && month <= currentMonth)) {
                                // showToastShort(appContext, "ECS",
                                // "Please select proper date.");
                                dateEditText.setText(getCurrentDateTimeStamp(""));

                            } else {
                                String date_selected;
                                if ((monthOfYear >= 0 && monthOfYear < 9)
                                        && (dayOfMonth > 0 && dayOfMonth < 10))
                                    date_selected = "0"
                                            + String.valueOf(dayOfMonth) + "-0"
                                            + String.valueOf(monthOfYear + 1)
                                            + "-"
                                            + String.valueOf(selectedYear);
                                else if (monthOfYear >= 0 && monthOfYear < 9)
                                    date_selected = String.valueOf(dayOfMonth)
                                            + "-0"
                                            + String.valueOf(monthOfYear + 1)
                                            + "-"
                                            + String.valueOf(selectedYear);

                                else if (dayOfMonth > 0 && dayOfMonth < 10)
                                    date_selected = "0"
                                            + String.valueOf(dayOfMonth) + "-"
                                            + String.valueOf(monthOfYear + 1)
                                            + "-"
                                            + String.valueOf(selectedYear);

                                else
                                    date_selected = String.valueOf(dayOfMonth)
                                            + "-"
                                            + String.valueOf(monthOfYear + 1)
                                            + "-"
                                            + String.valueOf(selectedYear);

                                dateEditText.setText(date_selected);
                            }
                        } else if (dateFlg == DTU.FLAG_OLD_AND_NEW) {
                            String date_selected;
                            if ((monthOfYear >= 0 && monthOfYear < 9)
                                    && (dayOfMonth > 0 && dayOfMonth < 10))
                                date_selected = "0"
                                        + String.valueOf(dayOfMonth) + "-0"
                                        + String.valueOf(monthOfYear + 1) + "-"
                                        + String.valueOf(selectedYear);
                            else if (monthOfYear >= 0 && monthOfYear < 9)
                                date_selected = String.valueOf(dayOfMonth)
                                        + "-0"
                                        + String.valueOf(monthOfYear + 1) + "-"
                                        + String.valueOf(selectedYear);
                            else if (dayOfMonth > 0 && dayOfMonth < 10)
                                date_selected = "0"
                                        + String.valueOf(dayOfMonth) + "-"
                                        + String.valueOf(monthOfYear + 1) + "-"
                                        + String.valueOf(selectedYear);
                            else
                                date_selected = String.valueOf(dayOfMonth)
                                        + "-" + String.valueOf(monthOfYear + 1)
                                        + "-" + String.valueOf(selectedYear);
                            dateEditText.setText(date_selected);
                        }
                    }
                }, currentYear, currentMonth, currentDay);
        datepicker.show();
    }

    public static void showDatePickerDialog(Context context, final int dateFlg,
                                            final TextView dateTextView) {
        // Displays Date picker
        final Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datepicker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int monthOfYear, int dayOfMonth) {
                        int year = selectedYear;
                        int month = monthOfYear;
                        int day = dayOfMonth;
                        if (dateFlg == DTU.FLAG_ONLY_NEW) {
//                            if ((year != currentYear)
//                                    || (month < currentMonth && year == currentYear)
//                                    || (day < currentDay && year == currentYear && month <= currentMonth)) {
//                                // Toast.makeText(appContext,
//                                // "Please select proper date.",
//                                // Toast.LENGTH_SHORT).show();
//                                dateTextView
//                                        .setText(getCurrentDateTimeStamp(""));
//
//                            } else {
                            String date_selected;
                            if ((monthOfYear >= 0 && monthOfYear < 9)
                                    && (dayOfMonth > 0 && dayOfMonth < 10))
                                date_selected = "0"
                                        + String.valueOf(dayOfMonth) + "-0"
                                        + String.valueOf(monthOfYear + 1)
                                        + "-"
                                        + String.valueOf(selectedYear);

                            else if (monthOfYear >= 0 && monthOfYear < 9)
                                date_selected = String.valueOf(dayOfMonth)
                                        + "-0"
                                        + String.valueOf(monthOfYear + 1)
                                        + "-"
                                        + String.valueOf(selectedYear);

                            else if (dayOfMonth > 0 && dayOfMonth < 10)
                                date_selected = "0"
                                        + String.valueOf(dayOfMonth) + "-"
                                        + String.valueOf(monthOfYear + 1)
                                        + "-"
                                        + String.valueOf(selectedYear);

                            else
                                date_selected = String.valueOf(dayOfMonth)
                                        + "-"
                                        + String.valueOf(monthOfYear + 1)
                                        + "-"
                                        + String.valueOf(selectedYear);

                            dateTextView.setText(date_selected);
                            //  }
                        } else if (dateFlg == DTU.FLAG_OLD_AND_NEW) {
                            String date_selected;
                            if ((monthOfYear >= 0 && monthOfYear < 9)
                                    && (dayOfMonth > 0 && dayOfMonth < 10))
                                date_selected = "0"
                                        + String.valueOf(dayOfMonth) + "-0"
                                        + String.valueOf(monthOfYear + 1) + "-"
                                        + String.valueOf(selectedYear);
                            else if (monthOfYear >= 0 && monthOfYear < 9)
                                date_selected = String.valueOf(dayOfMonth)
                                        + "-0"
                                        + String.valueOf(monthOfYear + 1) + "-"
                                        + String.valueOf(selectedYear);
                            else if (dayOfMonth > 0 && dayOfMonth < 10)
                                date_selected = "0"
                                        + String.valueOf(dayOfMonth) + "-"
                                        + String.valueOf(monthOfYear + 1) + "-"
                                        + String.valueOf(selectedYear);
                            else
                                date_selected = String.valueOf(dayOfMonth)
                                        + "-" + String.valueOf(monthOfYear + 1)
                                        + "-" + String.valueOf(selectedYear);
                            dateTextView.setText(date_selected);
                        }
                    }
                }, currentYear, currentMonth, currentDay);
        datepicker.show();

        datepicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
    }

    public static void showDatePickerDialogYYMMDD(Context context, final int dateFlg,
                                                  final TextView dateTextView) {
        // Displays Date picker
        final Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datepicker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int monthOfYear, int dayOfMonth) {
                        int year = selectedYear;
                        int month = monthOfYear;
                        int day = dayOfMonth;
                        if (dateFlg == DTU.FLAG_ONLY_NEW) {
                            if ((year != currentYear)
                                    || (month < currentMonth && year == currentYear)
                                    || (day < currentDay && year == currentYear && month <= currentMonth)) {
                                // Toast.makeText(appContext,
                                // "Please select proper date.",
                                // Toast.LENGTH_SHORT).show();
                                dateTextView
                                        .setText(getCurrentDateTimeStamp(""));

                            } else {
                                String date_selected;
                                if ((monthOfYear >= 0 && monthOfYear < 9)
                                        && (dayOfMonth > 0 && dayOfMonth < 10))
                                    date_selected = String.valueOf(selectedYear) + "-0"
                                            + String.valueOf(monthOfYear + 1)
                                            + "-"
                                            + "0" + String.valueOf(dayOfMonth);

                                else if (monthOfYear >= 0 && monthOfYear < 9)
                                    date_selected = String.valueOf(selectedYear)
                                            + "-0"
                                            + String.valueOf(monthOfYear + 1)
                                            + "-"
                                            + String.valueOf(dayOfMonth);

                                else if (dayOfMonth > 0 && dayOfMonth < 10)
                                    date_selected = String.valueOf(selectedYear) + "-"
                                            + String.valueOf(monthOfYear + 1)
                                            + "-"
                                            + "0"
                                            + String.valueOf(dayOfMonth);

                                else
                                    date_selected = String.valueOf(selectedYear)
                                            + "-"
                                            + String.valueOf(monthOfYear + 1)
                                            + "-"
                                            + String.valueOf(dayOfMonth);

                                dateTextView.setText(date_selected);
                            }
                        } else if (dateFlg == DTU.FLAG_OLD_AND_NEW) {
                            String date_selected;
                            if ((monthOfYear >= 0 && monthOfYear < 9)
                                    && (dayOfMonth > 0 && dayOfMonth < 10))
                                date_selected = String.valueOf(selectedYear) + "-0"
                                        + String.valueOf(monthOfYear + 1)
                                        + "-"
                                        + "0" + String.valueOf(dayOfMonth);

                            else if (monthOfYear >= 0 && monthOfYear < 9)
                                date_selected = String.valueOf(selectedYear)
                                        + "-0"
                                        + String.valueOf(monthOfYear + 1)
                                        + "-"
                                        + String.valueOf(dayOfMonth);

                            else if (dayOfMonth > 0 && dayOfMonth < 10)
                                date_selected = String.valueOf(selectedYear) + "-"
                                        + String.valueOf(monthOfYear + 1)
                                        + "-"
                                        + "0"
                                        + String.valueOf(dayOfMonth);

                            else
                                date_selected = String.valueOf(selectedYear)
                                        + "-"
                                        + String.valueOf(monthOfYear + 1)
                                        + "-"
                                        + String.valueOf(dayOfMonth);

                            dateTextView.setText(date_selected);
                        }
                    }
                }, currentYear, currentMonth, currentDay);
        datepicker.show();
    }

    public static String convertTo24HoursFormat(String twelveHourTime)
            throws ParseException {
        // Returns date in 24 hour format
        return TWENTY_FOUR_TF.format(TWELVE_TF.parse(twelveHourTime));
    }

    public static String convertTo12HoursFormat(String twentyFourHourTime)
            throws ParseException {
        // Returns date in 24 hour format
        return TWELVE_TF.format(TWENTY_FOUR_TF.parse(twentyFourHourTime));
    }

    public static String getNextToDate(int days) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, days);
        String date_selected = "";
        int year = c.get(Calendar.YEAR);
        int monthOfYear = c.get(Calendar.MONTH);
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);

        if ((monthOfYear >= 0 && monthOfYear < 9)
                && (dayOfMonth > 0 && dayOfMonth < 10))
            date_selected = String.valueOf(year) + "-0"
                    + String.valueOf(monthOfYear + 1) + "-0"
                    + String.valueOf(dayOfMonth);
        else if (monthOfYear >= 0 && monthOfYear < 9)
            date_selected = String.valueOf(year) + "-0"
                    + String.valueOf(monthOfYear + 1) + "-"
                    + String.valueOf(dayOfMonth);
        else if (dayOfMonth > 0 && dayOfMonth < 10)
            date_selected = String.valueOf(year) + "-"
                    + String.valueOf(monthOfYear + 1) + "-0"
                    + String.valueOf(dayOfMonth);
        else
            date_selected = String.valueOf(year) + "-"
                    + String.valueOf(monthOfYear + 1) + "-"
                    + String.valueOf(dayOfMonth);
        Log.i("ECS", "date_selected " + date_selected);

        return date_selected;

    }

    public static String getd_m_Y(String strYMDDate) {
        try {
            // Converts dd-mm-yy to mm-dd-yy Added on 05/12/2013
            String dd = "", mm = "", yyyyy = "", time = "";
            if (strYMDDate.contains(" ")) {
                String dtarray[] = strYMDDate.split(" ");
                strYMDDate = dtarray[0];
            }
//            String dtarray[] = dt.split(" ");
//            dt = dtarray[0];// 2015-02-23
//            time = dtarray[1];// 11:30:30

            int i = 0;

            for (String retval : strYMDDate.split("-")) {
                if (i == 0)
                    yyyyy = retval;
                else if (i == 1)
                    mm = retval;
                else
                    dd = retval;
                i++;
            }
            return (dd + "-" + mm + "-" + yyyyy).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDD_Month_Year(String strYMDDate) {
        try {
            // Converts dd-mm-yy to mm-dd-yy Added on 05/12/2013
            String dd = "", mm = "", yyyyy = "", time = "";
            if (strYMDDate.contains(" ")) {
                String dtarray[] = strYMDDate.split(" ");
                strYMDDate = dtarray[0];
            }
//            String dtarray[] = dt.split(" ");
//            dt = dtarray[0];// 2015-02-23
//            time = dtarray[1];// 11:30:30

            int i = 0;

            for (String retval : strYMDDate.split("-")) {
                if (i == 0)
                    yyyyy = retval;
                else if (i == 1)
                    mm = retval;
                else
                    dd = retval;
                i++;
            }
            return (dd + " " + getMonth(Integer.parseInt(mm)) + " " + yyyyy).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

    public static int roundTo5(double n) {
        return (int) Math.round(n / 5) * 5;
    }

    public static void showDatePickerDialogNew(final Context context, final int dateFlg,
                                               final TextView dateTextView) {
        // Displays Date picker
        final Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH);
        currentDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datepicker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear,
                                          int monthOfYear, int dayOfMonth) {
                        int year = selectedYear;
                        int month = monthOfYear;
                        int day = dayOfMonth;

                            /*if ((year != currentYear)
                                    || (month < currentMonth && year == currentYear)
                                    || (day < currentDay && year == currentYear && month <= currentMonth)) {
                                LTU.TIL(context,"","Please select proper date.");
                                dateTextView
                                        .setText(getCurrentDateTimeStamp(DMY));

                            } else {*/
                        String date_selected;
                        if ((monthOfYear >= 0 && monthOfYear < 9)
                                && (dayOfMonth > 0 && dayOfMonth < 10))
                            date_selected = "0"
                                    + String.valueOf(dayOfMonth) + "-0"
                                    + String.valueOf(monthOfYear + 1)
                                    + "-"
                                    + String.valueOf(selectedYear);

                        else if (monthOfYear >= 0 && monthOfYear < 9)
                            date_selected = String.valueOf(dayOfMonth)
                                    + "-0"
                                    + String.valueOf(monthOfYear + 1)
                                    + "-"
                                    + String.valueOf(selectedYear);

                        else if (dayOfMonth > 0 && dayOfMonth < 10)
                            date_selected = "0"
                                    + String.valueOf(dayOfMonth) + "-"
                                    + String.valueOf(monthOfYear + 1)
                                    + "-"
                                    + String.valueOf(selectedYear);

                        else
                            date_selected = String.valueOf(dayOfMonth)
                                    + "-"
                                    + String.valueOf(monthOfYear + 1)
                                    + "-"
                                    + String.valueOf(selectedYear);

                        String[] finalstr = date_selected.split("-");
                        //   String finalDate = finalstr[2]+"-"+finalstr[1]+"-"+finalstr[0];
                        String finalDate = finalstr[2] + "-" + finalstr[1] + "-" + finalstr[0];
                        dateTextView.setText(finalDate);
                        //}
                    }
                }, currentYear, currentMonth, currentDay);
        datepicker.show();
    }

    public static String formatDate(String date1) {
        if (date1.length() <= 10) {
            date1 = date1 + " 00:00:00";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd, yyyy");
        String date = null;
        try {
            Date d = sdf.parse(date1);
            date = df.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String formatTime(String time) {

        SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa", Locale.US);
        SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");
        String time1 = null;
        try {
            time1 = inFormat.format(outFormat.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time1;
    }

    public static String daysDiffernce(String startDate, String endDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date date_start = null, date_end = null;
        try {
            date_start = simpleDateFormat.parse(startDate);
            date_end = simpleDateFormat.parse(endDate);
            long diff = date_end.getTime() - date_start.getTime();
            float dayCount = (float) diff / (24 * 60 * 60 * 1000);
            int dayCountInt = (int) dayCount;
            //dayCountInt = Math.abs(dayCountInt);
            String sDate = String.valueOf(dayCountInt);
            return sDate;//Integer.parseInt(String.valueOf(dayCount));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String DateDifference(Date startDate, Date endDate) {
        //milliseconds


        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String diff = "";

        if (elapsedDays == 0) {
            diff = elapsedHours + " Hr, " + elapsedMinutes + " Min, " + elapsedSeconds + " Sec.";
        } else if (elapsedDays == 0 && elapsedHours == 0) {
            diff = elapsedMinutes + " Min, " + elapsedSeconds + " Sec";
        } else if (elapsedDays == 0 && elapsedHours == 0 && elapsedMinutes == 0) {
            diff = elapsedSeconds + " Seconds.";
        } else {
            diff = elapsedDays + " Days, " + elapsedHours + " Hr, " + elapsedMinutes + " Min, " + elapsedSeconds + " Sec. ";
        }

        Log.e("days_Log", "" + diff);
        System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        return diff;
    }


}
