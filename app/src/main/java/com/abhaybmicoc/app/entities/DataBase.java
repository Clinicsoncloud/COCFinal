package com.abhaybmicoc.app.entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {

    private static final String REGISTER_TABLE = "aanddmedical_register";

    private static final String ZLIFETRACK_TABLE = "aandmedical_lifetrack";
    private static final String ZWEIGHTTRACK_TABLE = "aandmedical_weighttrack";
    private static final String BPTRACK_TABLE = "aandmedical_bptrack";

    private static final int DATABASE_VERSION = 3; //Earlier version was 2

    private static final String KEY_ID = "_id";
    private static final String KEY_EMAILID = "email_id";
    private static final String KEY_USER_HEIGHT = "UserHeight";
    private static final String KEY_USER_HEIGHT_UNIT = "UserHeightUnit";
    private static final String KEY_USER_SEX = "UserSex";
    private static final String KEY_USER_BIRTHDATE = "UserBirthDate";
    private static final String KEY_USER_TIMEZONE = "UserTimeZone";
    private static final String KEY_USER_UW_TRACKER = "uw_name";

    private static final String LT_DATE = "date";
    private static final String LT_HEART_RATE = "heart_rate";

    private static final String LT_TIME = "time";
    private static final String LT_STEPS = "steps";
    private static final String LT_STEPS_UNITS = "steps_units";
    private static final String LT_TIME_STAMP = "dateTimeStamp";
    private static final String LT_DEVICE_ID = "deviceid";

    private static final String LT_CAL = "cal";
    private static final String LT_CAL_UNITS = "cal_units";

    private static final String LT_MILES = "miles";
    private static final String LT_DISTANCE_MILES = "distance_miles";
    private static final String LT_MILES_UNITS = "miles_units";

    private static final String LT_SLEEP = "sleep";
    private static final String LT_SLEEP_UNITS = "sleep_units";

    private static final String WT_WEIGHT = "weight";
    private static final String WT_WEIGHT_UNITS = "weight_units";

    private static final String PULSE = "pulse";
    private static final String SYSTOLIC = "systolic";
    private static final String DIASTOLIC = "diastolic";
    private static final String PULSE_UNIT = "pulse_unit";
    private static final String SYSTOLIC_UNIT = "systolic_unit";
    private static final String DIASTOLIC_UNIT = "diastolic_unit";
    private static final String IRRWGULAR_PULSE_DETECTION = "irregular_pulse_detection";

    private static final String THERMOMETER_TABLE_NAME = "aandmedical_thermometer";
    private static final String THERMOMETER_DATE = "date";
    private static final String THERMOMETER_TIME = "time";
    private static final String THERMOMETER_TIME_STAMP = "dateTimeStamp";
    private static final String THERMOMETER_VALUE = "thermometer_value";
    private static final String THERMOMETER_UNIT = "thermometer_unit";
    private static final String THERMOMETER_DEVICE_NAME = "davice_name";


    public DataBase(Context context) {
        super(context, "Guest.db", null,
                DATABASE_VERSION);
    }

    public DataBase(Context context, String database_name) {
        super(context, database_name.toLowerCase().trim() + ".db", null,
                DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        createRegisterTable(db);
        createLifeTrackTable(db);
        createWeightTrackTable(db);
        createBPTrackTable(db);
        createThermometerTable(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion == 1 && newVersion == 2) {
            upgradeTable1to2(db);
        } else if (oldVersion == 2 && newVersion == 3) {
            upgradeTable2to3(db);
        } else if (oldVersion == 1 && newVersion == 3) {
            upgradeTable1to3(db);
        }

    }

    //==============================================
    // create table method
    //==============================================

    private void createRegisterTable(SQLiteDatabase db) {
        String sql =
                " create table " + REGISTER_TABLE + "( " +
                        KEY_ID + " integer primary key autoincrement," +
                        KEY_EMAILID + " varchar(256)," +
                        KEY_USER_HEIGHT + " varchar(256), " +
                        KEY_USER_HEIGHT_UNIT + " varchar(256), " +
                        KEY_USER_SEX + " varchar(256)," +
                        KEY_USER_BIRTHDATE + " varchar(256)," +
                        KEY_USER_TIMEZONE + " varchar(256)," +
                        KEY_USER_UW_TRACKER + " varchar(256)" +
                        "  ); ";
        db.execSQL(sql);
    }

    public void deleteBpData(Context context){
        SQLiteDatabase db;
        db = this.getWritableDatabase();
        db.execSQL("delete from "+ BPTRACK_TABLE);
        Toast.makeText(context, "Deleted All bp records", Toast.LENGTH_SHORT).show();
    }



    private void createLifeTrackTable(SQLiteDatabase db) {
        String sql =
                " create table " + ZLIFETRACK_TABLE + "( " +
                        KEY_ID + " integer primary key autoincrement," +
                        LT_DATE + " varchar(256), " +
                        LT_TIME + " varchar(256), " +
                        LT_STEPS + " varchar(256)," +
                        LT_STEPS_UNITS + " varchar(256)," +
                        LT_CAL + " varchar(256)," +
                        LT_CAL_UNITS + " varchar(256)," +
                        LT_MILES + " varchar(256)," +
                        LT_DEVICE_ID + " varchar(256)," +
                        LT_DISTANCE_MILES + " varchar(256), " +
                        LT_MILES_UNITS + " varchar(256)," +
                        LT_SLEEP + " varchar(256)," +
                        LT_HEART_RATE + " varchar(256), " +
                        LT_TIME_STAMP + " varchar(256), " +
                        LT_SLEEP_UNITS + " varchar(256) " +
                        " ); ";
        db.execSQL(sql);
    }

    private void createWeightTrackTable(SQLiteDatabase db) {
        String sql =
                " create table " + ZWEIGHTTRACK_TABLE + "( " +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        LT_DATE + " varchar(256), " +
                        LT_TIME + " varchar(256), " +
                        LT_DEVICE_ID + " varchar(256)," +
                        WT_WEIGHT + " varchar(256), " +
                        LT_TIME_STAMP + " varchar(256), " +
                        WT_WEIGHT_UNITS + " varchar(256) " +
                        " ); ";
        db.execSQL(sql);
    }


    private void createBPTrackTable(SQLiteDatabase db) {
        String sql =
                " create table " + BPTRACK_TABLE + "( " +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        LT_DATE + " varchar(256), " +
                        LT_TIME + " varchar(256), " +
                        PULSE + " varchar(256), " +
                        SYSTOLIC + " varchar(256), " +
                        DIASTOLIC + " varchar(256), " +
                        PULSE_UNIT + " varchar(256), " +
                        LT_DEVICE_ID + " varchar(256)," +
                        SYSTOLIC_UNIT + " varchar(256), " +
                        LT_TIME_STAMP + " varchar(256), " +
                        DIASTOLIC_UNIT + " varchar(256), " +
                        IRRWGULAR_PULSE_DETECTION + " varchar(256) " +
                        " ); ";
        db.execSQL(sql);
    }

    private void createThermometerTable(SQLiteDatabase db) {
        String sql =
                " CREATE TABLE " + THERMOMETER_TABLE_NAME + "( " +
                        KEY_ID + " INTEGER PRIMARY KEY," +
                        THERMOMETER_DATE + " varchar(256), " +
                        THERMOMETER_TIME + " varchar(256), " +
                        THERMOMETER_TIME_STAMP + " varchar(256), " +
                        THERMOMETER_VALUE + " varchar(256), " +
                        THERMOMETER_UNIT + " varchar(256), " +
                        THERMOMETER_DEVICE_NAME + " varchar(256) " +
                        " ); ";
        db.execSQL(sql);
    }


    //==============================================
    // upgrade table method
    //==============================================

    private void upgradeRegisterTable2to3(SQLiteDatabase db) {
        String addUWTrackerNameSql =
                " alter table " + REGISTER_TABLE +
                        " add " + KEY_USER_UW_TRACKER +
                        " varchar(256)";
        db.execSQL(addUWTrackerNameSql);


    }

    private void upgradeTable1to2(SQLiteDatabase db) {
        createThermometerTable(db);
    }


    //Added the UW-302 device name information
    private void upgradeTable2to3(SQLiteDatabase db) {
        upgradeRegisterTable2to3(db);
    }

    private void upgradeTable1to3(SQLiteDatabase db) {
        createThermometerTable(db);
        upgradeRegisterTable2to3(db);

    }

    public RegistrationInfoBean getUserDetailAccount(String emailId) {
        SQLiteDatabase db = null;
        RegistrationInfoBean infoBeanObj = null;
        try {
            db = this.getWritableDatabase();
            String sql = "SELECT * FROM " + REGISTER_TABLE +
                    " WHERE " + KEY_EMAILID + " = ?";
            String[] selectionArgs = new String[]{emailId};
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, selectionArgs);
                if (cursor.moveToFirst()) {
                    do {
                        infoBeanObj = new RegistrationInfoBean();
                        infoBeanObj.setUserHeight(cursor.getString(cursor
                                .getColumnIndex(KEY_USER_HEIGHT)));
                        infoBeanObj.setUserHeightUnit(cursor.getString(cursor
                                .getColumnIndex(KEY_USER_HEIGHT_UNIT)));
                        infoBeanObj.setUserDateBirth(cursor.getString(cursor
                                .getColumnIndex(KEY_USER_BIRTHDATE)));
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
        return infoBeanObj;

    }

    public void entryGuestInfo(RegistrationInfoBean guestInfo) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(KEY_EMAILID, "guest@gmail.com");
            cv.put(KEY_USER_HEIGHT, guestInfo.getUserHeight());
            cv.put(KEY_USER_HEIGHT_UNIT, guestInfo.getUserHeightUnit());
            cv.put(KEY_USER_BIRTHDATE, guestInfo.getUserDateBirth());

            String sql = "SELECT * FROM " + REGISTER_TABLE +
                    " WHERE " + KEY_EMAILID + " = ?";
            String[] selectionArgs = new String[]{"guest@gmail.com"};
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, selectionArgs);
                if (cursor != null && cursor.getCount() != 0) {
                    String whereClause = KEY_EMAILID + " = ?";
                    String[] whereArgs = new String[]{"guest@gmail.com"};
                    db.update(REGISTER_TABLE, cv, whereClause, whereArgs);
                } else {
                    db.insert(REGISTER_TABLE, null, cv);
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }

    public RegistrationInfoBean getGuestInfo() {
        SQLiteDatabase db = null;
        RegistrationInfoBean guestInfo = null;
        try {
            db = this.getWritableDatabase();
            String sql = "SELECT * FROM " + REGISTER_TABLE +
                    " WHERE " + KEY_EMAILID + " = ?";
            String[] selectionArgs = new String[]{"guest@gmail.com"};
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, selectionArgs);
                if (cursor.moveToFirst()) {
                    do {
                        guestInfo = new RegistrationInfoBean();
                        guestInfo.setUserHeight(cursor.getString(cursor
                                .getColumnIndex(KEY_USER_HEIGHT)));
                        guestInfo.setUserHeightUnit(cursor.getString(cursor
                                .getColumnIndex(KEY_USER_HEIGHT_UNIT)));
                        guestInfo.setUserDateBirth(cursor.getString(cursor
                                .getColumnIndex(KEY_USER_BIRTHDATE)));
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
        return guestInfo;

    }



    // Life Tracker entry's
    public void lifetrackentry(ArrayList<Lifetrack_infobean> data) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv;

            for (int i = 0; i < data.size(); i++) {
                cv = new ContentValues();
                cv.put(LT_DATE, data.get(i).getDate());
                cv.put(LT_TIME, data.get(i).getTime());
                cv.put(LT_TIME_STAMP, data.get(i).getDateTimeStamp());

                cv.put(LT_STEPS, data.get(i).getSteps());
                cv.put(LT_STEPS_UNITS, data.get(i).getStepsUnits());

                cv.put(LT_CAL, data.get(i).getCal());
                cv.put(LT_CAL_UNITS, data.get(i).getCalorieUnits());

                cv.put(LT_MILES, data.get(i).getDistance());
                cv.put(LT_MILES_UNITS, data.get(i).getDistanceUnit());
                cv.put(LT_DISTANCE_MILES, data.get(i).getDistanceInMiles());

                cv.put(LT_SLEEP, data.get(i).getSleep());
                cv.put(LT_DEVICE_ID, data.get(i).getDeviceId());

                cv.put(LT_SLEEP_UNITS, data.get(i).getStepsUnits());

                cv.put(LT_HEART_RATE, data.get(i).getHeartRate());

                String sql = "SELECT * FROM " + ZLIFETRACK_TABLE + " WHERE " + LT_TIME_STAMP + " = ?";

                String[] selectionArgs = new String[]{data.get(i).getDateTimeStamp()};
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery(sql, selectionArgs);
                    if (cursor != null && cursor.getCount() != 0) {
                        String whereClause = LT_TIME_STAMP + " = ?";
                        String[] whereArgs = new String[]{data.get(i).getDateTimeStamp()};
                        db.update(ZLIFETRACK_TABLE, cv, whereClause, whereArgs);
                    } else {
                        db.insert(ZLIFETRACK_TABLE, null, cv);
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }

    public void weighttrackentry(ArrayList<Lifetrack_infobean> data) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv;
            for (int i = 0; i < data.size(); i++) {
                cv = new ContentValues();
                cv.put(LT_DATE, data.get(i).getDate());
                cv.put(LT_TIME, data.get(i).getTime());
                cv.put(WT_WEIGHT, data.get(i).getWeight());
                cv.put(WT_WEIGHT_UNITS, data.get(i).getWeightUnit());
                cv.put(LT_TIME_STAMP, data.get(i).getDateTimeStamp());

                cv.put(LT_DEVICE_ID, data.get(i).getDeviceId());

                Cursor cursorTimeStamp = null;
                Cursor cursorTime = null;
                try {
                    String sql = "SELECT * FROM " + ZWEIGHTTRACK_TABLE
                            + " WHERE " + LT_TIME_STAMP + " = ?";
                    String[] selectionArgs = new String[]{data.get(i).getDateTimeStamp()};
                    cursorTimeStamp = db.rawQuery(sql, selectionArgs);

                    sql = "SELECT * FROM " + ZWEIGHTTRACK_TABLE
                            + " WHERE " + LT_TIME + " = ?";
                    selectionArgs = new String[]{data.get(i).getTime()};
                    cursorTime = db.rawQuery(sql, selectionArgs);

                    if (cursorTimeStamp != null && cursorTimeStamp.getCount() != 0
                            && cursorTime != null && cursorTime.getCount() != 0) {
                        String whereClause = LT_TIME_STAMP + " = ?";
                        String[] whereArgs = new String[]{data.get(i).getDateTimeStamp()};
                        db.update(ZWEIGHTTRACK_TABLE, cv, whereClause, whereArgs);
                    } else {
                        db.insert(ZWEIGHTTRACK_TABLE, null, cv);
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (cursorTimeStamp != null) {
                            cursorTimeStamp.close();
                        }
                        if (cursorTime != null) {
                            cursorTime.close();
                        }
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }


    public void bpEntry(ArrayList<Lifetrack_infobean> data) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv;
            for (int i = 0; i < data.size(); i++) {
                cv = new ContentValues();

                cv.put(LT_DATE, data.get(i).getDate());
                cv.put(LT_TIME, data.get(i).getTime());

                cv.put(PULSE, data.get(i).getPulse());
                cv.put(SYSTOLIC, data.get(i).getSystolic());

                cv.put(DIASTOLIC, data.get(i).getDiastolic());
                cv.put(PULSE_UNIT, data.get(i).getPulseUnit());

                cv.put(SYSTOLIC_UNIT, data.get(i).getSystolicUnit());

                cv.put(DIASTOLIC_UNIT, data.get(i).getDiastolicUnit());
                cv.put(LT_TIME_STAMP, data.get(i).getDateTimeStamp());
                cv.put(LT_DEVICE_ID, data.get(i).getDeviceId());
                cv.put(IRRWGULAR_PULSE_DETECTION, data.get(i).getIrregularPulseDetection());

                Cursor cursorTimeStump = null;
                Cursor cursorTime = null;
                try {
                    String sql = "SELECT * FROM " + BPTRACK_TABLE +
                            " WHERE " + LT_TIME_STAMP + " = ?";
                    String[] selectionArgs = new String[]{data.get(i).getDateTimeStamp()};
                    cursorTimeStump = db.rawQuery(sql, selectionArgs);

                    sql = "SELECT * FROM " + BPTRACK_TABLE +
                            " WHERE " + LT_TIME + " = ?";
                    selectionArgs = new String[]{data.get(i).getTime()};
                    cursorTime = db.rawQuery(sql, selectionArgs);

                    if (cursorTimeStump != null && cursorTimeStump.getCount() != 0
                            && cursorTime != null && cursorTime.getCount() != 0) {
                        String whereClause = LT_TIME_STAMP + " = ?";
                        String[] whereArgs = new String[]{data.get(i).getDateTimeStamp()};
                        db.update(BPTRACK_TABLE, cv, whereClause, whereArgs);
                    } else {
                        db.insert(BPTRACK_TABLE, null, cv);
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (cursorTimeStump != null) {
                            cursorTimeStump.close();
                        }
                        if (cursorTime != null) {
                            cursorTime.close();
                        }
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Lifetrack_infobean> getbpDetails() {
        ArrayList<Lifetrack_infobean> lifeList = new ArrayList<Lifetrack_infobean>();

        String selectQuery = "SELECT  * FROM " + BPTRACK_TABLE;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        Lifetrack_infobean lifeDetails = new Lifetrack_infobean();
                        lifeDetails.setKeyidbp(cursor.getInt(cursor
                                .getColumnIndex(KEY_ID)));
                        lifeDetails.setDate(cursor.getString(cursor
                                .getColumnIndex(LT_DATE)));
                        lifeDetails.setTime(cursor.getString(cursor
                                .getColumnIndex(LT_TIME)));
                        lifeDetails.setPulse(cursor.getString(cursor
                                .getColumnIndex(PULSE)));
                        lifeDetails.setSystolic(cursor.getString(cursor
                                .getColumnIndex(SYSTOLIC)));
                        lifeDetails.setDiastolic(cursor.getString(cursor
                                .getColumnIndex(DIASTOLIC)));
                        lifeDetails.setPulseUnit(cursor.getString(cursor
                                .getColumnIndex(PULSE_UNIT)));
                        lifeDetails.setSystolicUnit(cursor.getString(cursor
                                .getColumnIndex(SYSTOLIC_UNIT)));
                        lifeDetails.setDiastolicUnit(cursor.getString(cursor
                                .getColumnIndex(DIASTOLIC_UNIT)));
                        lifeDetails.setDateTimeStamp(cursor.getString(cursor
                                .getColumnIndex(LT_TIME_STAMP)));
                        lifeDetails.setDeviceId(cursor.getString(cursor
                                .getColumnIndex(LT_DEVICE_ID)));
                        // Adding contact to list
                        lifeList.add(lifeDetails);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
        return lifeList;
    }

    public ArrayList<Lifetrack_infobean> getAllWeightDetails() {
        ArrayList<Lifetrack_infobean> weightList = new ArrayList<Lifetrack_infobean>();

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            String sql = "SELECT * FROM " + ZWEIGHTTRACK_TABLE + " ORDER BY " + LT_DATE;

            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, null);
                if (cursor.moveToFirst()) {
                    do {
                        Lifetrack_infobean weightDetails = new Lifetrack_infobean();
                        weightDetails.setKeyidweight(cursor.getInt(cursor
                                .getColumnIndex(KEY_ID)));
                        weightDetails.setDate(cursor.getString(cursor
                                .getColumnIndex(LT_DATE)));
                        weightDetails.setWeight(cursor.getString(cursor
                                .getColumnIndex(WT_WEIGHT)));
                        weightDetails.setTime(cursor.getString(cursor
                                .getColumnIndex(LT_TIME)));
                        weightDetails.setWeightUnit(cursor.getString(cursor
                                .getColumnIndex(WT_WEIGHT_UNITS)));
                        weightDetails.setDateTimeStamp(cursor.getString(cursor
                                .getColumnIndex(LT_TIME_STAMP)));
                        weightDetails.setDeviceId(cursor.getString(cursor
                                .getColumnIndex(LT_DEVICE_ID)));

                        weightList.add(weightDetails);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
        return weightList;
    }

    // ..............................Get blood pressure
    // details.....................

    public ArrayList<Lifetrack_infobean> getAllActivityDetails() {
        ArrayList<Lifetrack_infobean> lifeTrackList = new ArrayList<Lifetrack_infobean>();

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            String sql = "SELECT * FROM " + ZLIFETRACK_TABLE;

            Cursor cursor = null;
            try {

                cursor = db.rawQuery(sql, null);
                if (cursor.moveToFirst()) {
                    do {
                        Lifetrack_infobean lifeTrackDetails = new Lifetrack_infobean();
                        lifeTrackDetails.setKeyidLifeTrack(cursor.getInt(cursor
                                .getColumnIndex(KEY_ID)));
                        lifeTrackDetails.setDate(cursor.getString(cursor
                                .getColumnIndex(LT_DATE)));
                        lifeTrackDetails.setTime(cursor.getString(cursor
                                .getColumnIndex(LT_TIME)));
                        lifeTrackDetails.setSteps(cursor.getString(cursor
                                .getColumnIndex(LT_STEPS)));
                        lifeTrackDetails.setCal(cursor.getString(cursor
                                .getColumnIndex(LT_CAL)));
                        lifeTrackDetails.setDistance(cursor.getString(cursor
                                .getColumnIndex(LT_MILES)));
                        lifeTrackDetails.setSleep(cursor.getString(cursor
                                .getColumnIndex(LT_SLEEP)));
                        lifeTrackDetails.setStepsUnits(cursor.getString(cursor
                                .getColumnIndex(LT_STEPS_UNITS)));
                        lifeTrackDetails.setCalorieUnits(cursor.getString(cursor
                                .getColumnIndex(LT_CAL_UNITS)));
                        lifeTrackDetails.setDistanceUnit(cursor.getString(cursor
                                .getColumnIndex(LT_MILES_UNITS)));
                        lifeTrackDetails.setSleepUnit(cursor.getString(cursor
                                .getColumnIndex(LT_SLEEP_UNITS)));

                        lifeTrackDetails.setHeartRate(cursor.getString(cursor
                                .getColumnIndex(LT_HEART_RATE)));
                        lifeTrackDetails.setDistanceInMiles(cursor.getString(cursor
                                .getColumnIndex(LT_DISTANCE_MILES)));
                        lifeTrackDetails.setDateTimeStamp(cursor.getString(cursor
                                .getColumnIndex(LT_TIME_STAMP)));
                        lifeTrackDetails.setDeviceId(cursor.getString(cursor
                                .getColumnIndex(LT_DEVICE_ID)));
                        // Adding contact to list
                        lifeTrackList.add(lifeTrackDetails);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }

        }

        return lifeTrackList;
    }

    // retrieves all the entries


    //===============================================================================================================
    // thermometer method TODO
    //===============================================================================================================
    public void entryThermometerInfo(ArrayList<Lifetrack_infobean> thermometerInfoList) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv;

            for (int i = 0; i < thermometerInfoList.size(); i++) {
                cv = new ContentValues();
                cv.put(THERMOMETER_DATE, thermometerInfoList.get(i).getDate());
                cv.put(THERMOMETER_TIME, thermometerInfoList.get(i).getTime());
                cv.put(THERMOMETER_TIME_STAMP, thermometerInfoList.get(i).getDateTimeStamp());

                cv.put(THERMOMETER_VALUE, thermometerInfoList.get(i).getThermometerValue());
                cv.put(THERMOMETER_UNIT, thermometerInfoList.get(i).getThermometerUnit());
                cv.put(THERMOMETER_DEVICE_NAME, thermometerInfoList.get(i).getThermometerDeviceName());

                String sql = "SELECT * FROM " + THERMOMETER_TABLE_NAME + " WHERE " + THERMOMETER_TIME_STAMP + " = ?";
                String[] selectionArgs = new String[]{thermometerInfoList.get(i).getDateTimeStamp()};
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery(sql, selectionArgs);
                    if (cursor != null && cursor.getCount() != 0) {
                        String whereClause = THERMOMETER_TIME_STAMP + " = ?";
                        String[] whereArgs = new String[]{thermometerInfoList.get(i).getDateTimeStamp()};
                        db.update(THERMOMETER_TABLE_NAME, cv, whereClause, whereArgs);
                    } else {
                        db.insert(THERMOMETER_TABLE_NAME, null, cv);
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Lifetrack_infobean> getAllThermometerDetails() {
        ArrayList<Lifetrack_infobean> thermometerInfoList = new ArrayList<Lifetrack_infobean>();

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            String sql = "SELECT * FROM " + THERMOMETER_TABLE_NAME;

            Cursor cursor = null;
            try {

                cursor = db.rawQuery(sql, null);
                if (cursor.moveToFirst()) {
                    do {
                        Lifetrack_infobean thermometerInfo = new Lifetrack_infobean();
                        thermometerInfo.setThermometerKeyId(cursor.getInt(cursor
                                .getColumnIndex(KEY_ID)));
                        thermometerInfo.setDate(cursor.getString(cursor.getColumnIndex(THERMOMETER_DATE)));
                        thermometerInfo.setTime(cursor.getString(cursor.getColumnIndex(THERMOMETER_TIME)));
                        thermometerInfo.setDateTimeStamp(cursor.getString(cursor.getColumnIndex(THERMOMETER_TIME_STAMP)));

                        thermometerInfo.setThermometerValue(cursor.getString(cursor.getColumnIndex(THERMOMETER_VALUE)));
                        thermometerInfo.setThermometerUnit(cursor.getString(cursor.getColumnIndex(THERMOMETER_UNIT)));
                        thermometerInfo.setThermometerDeviceName(cursor.getString(cursor.getColumnIndex(THERMOMETER_DEVICE_NAME)));
                        // Adding contact to list
                        thermometerInfoList.add(thermometerInfo);
                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }

        }

        return thermometerInfoList;
    }

    public void updateTrackerName(RegistrationInfoBean guestInfo, String name) {

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(KEY_EMAILID, "guest@gmail.com");
            cv.put(KEY_USER_HEIGHT, guestInfo.getUserHeight());
            cv.put(KEY_USER_HEIGHT_UNIT, guestInfo.getUserHeightUnit());
            cv.put(KEY_USER_BIRTHDATE, guestInfo.getUserDateBirth());
            guestInfo.setUWTrackerName(name);
            cv.put(KEY_USER_UW_TRACKER, guestInfo.getUWTrackerName());

            String sql = "SELECT * FROM " + REGISTER_TABLE +
                    " WHERE " + KEY_EMAILID + " = ?";
            String[] selectionArgs = new String[]{"guest@gmail.com"};
            Cursor cursor = null;
            try {
                cursor = db.rawQuery(sql, selectionArgs);
                if (cursor != null && cursor.getCount() != 0) {
                    String whereClause = KEY_EMAILID + " = ?";
                    String[] whereArgs = new String[]{"guest@gmail.com"};
                    db.update(REGISTER_TABLE, cv, whereClause, whereArgs);
                } else {
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
    }

    public String getTrackerName() {
        SQLiteDatabase db = null;
        String uwName = "";
        try {
            db = this.getWritableDatabase();
            String sql = "SELECT * FROM " + REGISTER_TABLE +
                    " WHERE " + KEY_EMAILID + " = ?";
            String[] selectionArgs = new String[]{"guest@gmail.com"};
            Cursor cursor = null;

            try {
                cursor = db.rawQuery(sql, selectionArgs);
                if (cursor.moveToFirst()) {
                    do {
                        uwName = (cursor.getString(cursor.getColumnIndex(KEY_USER_UW_TRACKER)));

                    } while (cursor.moveToNext());
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (SQLiteException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (db != null) {
                    db.close();
                }
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
        }
        return uwName;

    }

}
