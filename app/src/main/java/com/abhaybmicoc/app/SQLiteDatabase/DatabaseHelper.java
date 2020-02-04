package com.abhaybmicoc.app.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "NamesDB";
    public static final String TABLE_NAME = "parameters";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PATIENTID = "patient_id";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_BMI = "bmi";
    public static final String COLUMN_BMR = "bmr";
    public static final String COLUMN_META_AGE = "meta_age";
    public static final String COLUMN_HEALTH_SCORE = "health_score";
    public static final String COLUMN_PHYSIQUE = "physique";
    public static final String COLUMN_SUBCUTANEOUS = "subcutaneous";
    public static final String COLUMN_VISCERAL_FAT = "visceral_fat";
    public static final String COLUMN_SKELETON_MUSCLE = "skeleton_muscle";
    public static final String COLUMN_BODY_WATER = "body_water";
    public static final String COLUMN_MUSCLE_MASS = "muscle_mass";
    public static final String COLUMN_FAT_FREE_WEIGHT = "fat_free_weight";
    public static final String COLUMN_PROTEIN = "protein";
    public static final String COLUMN_BODY_FAT = "body_fat";
    public static final String COLUMN_BONE_MASS = "bone_mass";
    public static final String COLUMN_BLOOD_PRESSURE = "blood_pressure";
    public static final String COLUMN_BLOOD_DIASTOLIC = "blood_diastolic";
    public static final String COLUMN_OXYGEN = "oxygen";
    public static final String COLUMN_PULSE = "pulse";
    public static final String COLUMN_TEMPRATURE = "temprature";
    public static final String COLUMN_HEMOGLOBIN = "hemoglobin";
    public static final String COLUMN_SUGAR = "sugar";

    /* ranges*/
    public static final String COLUMN_WEIGHTRANGE = "weightrange";
    public static final String COLUMN_BMIRANGE = "bmirange";
    public static final String COLUMN_BMRRANGE = "bmrrange";
    public static final String COLUMN_METAAGERANGE = "metaagerange";
    public static final String COLUMN_HEALTHSCORERANGE = "healthscorerange";
    public static final String COLUMN_PHYSIQUERANGE = "physiquerange";
    public static final String COLUMN_SUBCUTANEOUSRANGE = "subcutaneousrange";
    public static final String COLUMN_VISCERALFATRANGE = "visceralfatrange";
    public static final String COLUMN_SKELETONMUSCLERANGE = "skeletonmusclerange";
    public static final String COLUMN_BODYWATERRANGE = "bodywaterrange";
    public static final String COLUMN_MUSCLEMASSRANGE = "musclemassrange";
    public static final String COLUMN_PROTEINRANGE = "proteinrange";
    public static final String COLUMN_BODYFATRANGE = "bodyfatrange";
    public static final String COLUMN_BONEMASSRANGE = "bonemassrange";
    public static final String COLUMN_SYSTOLICRANGE = "systolicrange";
    public static final String COLUMN_DIASTOLICRANGE = "diastolicrange";
    public static final String COLUMN_OXYGENRANGE = "oxygenrange";
    public static final String COLUMN_PULSERANGE = "pulserange";
    public static final String COLUMN_TEMPRATURERANGE = "tempraturerange";
    public static final String COLUMN_HEMOGLOBINRANGE = "hemoglobinrange";
    public static final String COLUMN_SUGARRANGE = "sugarrange";

    /*RESULTS*/
    public static final String COLUMN_WEIGHTRESULT = "weightresult";
    public static final String COLUMN_BMIRESULT = "bmiresult";
    public static final String COLUMN_BMRRESULT = "bmrresult";
    public static final String COLUMN_METAAGERESULT = "metaageresult";
    public static final String COLUMN_SUBCUTANEOUSRESULT = "suncutaneousresult";
    public static final String COLUMN_VISCERAL_FATRESULT = "visceralfatresult";
    public static final String COLUMN_SKELETON_MUSCLERESULT = "skeletonmuscleresult";
    public static final String COLUMN_BODY_WATERRESULT = "bodywaterresult";
    public static final String COLUMN_MUSCLE_MASSRESULT = "musclemassresult";
    public static final String COLUMN_FAT_FREE_WEIGHTRESULT = "fatfreeresult";
    public static final String COLUMN_PROTEIN_RESULT = "proteinresult";
    public static final String COLUMN_BODY_FATRESULT = "bodyfatresult";
    public static final String COLUMN_BONE_MASSRESULT = "bonemassresult";
    public static final String COLUMN_SYSTOLICRESULT = "systolicresult";
    public static final String COLUMN_DIASTOLICRESULT = "diastolicresult";
    public static final String COLUMN_OXYGENRESULT = "oxygenresult";
    public static final String COLUMN_PULSERESULT = "pulseresult";
    public static final String COLUMN_TEMPRATURERESULT = "tempratureresult";
    public static final String COLUMN_HEMOGLOBINRESULT = "hemoglobinresult";
    public static final String COLUMN_SUGARRESULT = "sugarresult";
    private static final String CREATED_BY = "user";
    private static final String UPDATED_BY = "user";
    private static final String DELETED_BY = "user";
    private static final String CREATED_AT_TIMESTAMP = "user";
    private static final String UPDATED_AT_TIMESTAMP = "user";
    private static final String DELETED_AT_TIMESTAMP = "user";
    private static final String COLUMN_STATUS = "status";

    //database version
    private static final int DB_VERSION = 1;



    public DatabaseHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID +
                "INTEGER PRIMARY KEY AUTOINCREMENT , " + COLUMN_PATIENTID + " INTEGER NULL DEFAULT NULLS " +
                COLUMN_HEIGHT + "VARCHAR" + COLUMN_WEIGHT + "VARCHAR" + COLUMN_GENDER + " VARCHAR "+
                COLUMN_BMI + "VARCHAR" + COLUMN_BMR + "VARCHAR" + COLUMN_META_AGE + "VARCHAR" +
                COLUMN_HEALTH_SCORE + "VARCHAR"+ COLUMN_PHYSIQUE + "VARCHAR" + COLUMN_SUBCUTANEOUS + "VARCHAR" +
                COLUMN_VISCERAL_FAT + "VARCHAR" + COLUMN_SKELETON_MUSCLE + "VARCHAR"+ COLUMN_BODY_WATER + "VARCHAR" +
                COLUMN_MUSCLE_MASS + "VARCHAR" + COLUMN_FAT_FREE_WEIGHT + "VARCHAR" + COLUMN_PROTEIN + "VARCHAR"+
                COLUMN_BODY_FAT + "VARCHAR" + COLUMN_BONE_MASS + "VARCHAR" + COLUMN_BLOOD_PRESSURE + "VARCHAR" +
                COLUMN_BLOOD_DIASTOLIC + "VARCHAR"+ COLUMN_OXYGEN + "VARCHAR" + COLUMN_PULSE + "VARCHAR" +
                COLUMN_TEMPRATURE + "VARCHAR" + COLUMN_HEMOGLOBIN + "VARCHAR" + COLUMN_SUGAR + "VARCHAR" +
                COLUMN_WEIGHTRANGE + "VARCHAR" + COLUMN_BMIRANGE + "VARCHAR" + COLUMN_BMRRANGE +"VARCHAR"+
                COLUMN_METAAGERANGE + "VARCHAR" + COLUMN_HEALTHSCORERANGE + "VARCHAR" + COLUMN_PHYSIQUERANGE +"VARCHAR"+
                COLUMN_SUBCUTANEOUSRANGE + "VARCHAR" + COLUMN_VISCERALFATRANGE + "VARCHAR" + COLUMN_SKELETONMUSCLERANGE + "VARCHAR"+
                COLUMN_BODYWATERRANGE + "VARCHAR" +COLUMN_MUSCLEMASSRANGE +"VARCHAR" +COLUMN_PROTEINRANGE+"VARCHAR"+
                COLUMN_BODY_FATRESULT+"VARCHAR"+COLUMN_BONEMASSRANGE + "VARCHAR"+ COLUMN_SYSTOLICRANGE + "VARCHAR" +
                COLUMN_DIASTOLICRANGE +"VARCHAR" +COLUMN_OXYGENRANGE + "VARCHAR"+ COLUMN_PULSERANGE + "VARCHAR" +
                COLUMN_TEMPRATURERANGE + "VARCHAR" + COLUMN_HEMOGLOBINRANGE + "VARCHAR"+ COLUMN_SUGARRANGE + "VARCHAR" +
                COLUMN_WEIGHTRESULT +"VARCHAR"+COLUMN_BMIRESULT +"VARCHAR"+ COLUMN_BMRRESULT + "VARCHAR" +
                COLUMN_METAAGERESULT+"VARCHAR"+COLUMN_SUBCUTANEOUSRESULT+"VARCHAR"+ COLUMN_VISCERAL_FATRESULT+"VARCHAR"+
                COLUMN_SKELETON_MUSCLERESULT+"VARCHAR"+COLUMN_BODY_WATERRESULT+"VARCHAR"+ COLUMN_MUSCLE_MASSRESULT+"VARCHAR"+
                COLUMN_FAT_FREE_WEIGHTRESULT+"VARCHAR"+COLUMN_PROTEIN_RESULT+"VARCHAR"+ COLUMN_BODY_FATRESULT+"VARCHAR"+
                COLUMN_BONE_MASSRESULT+"VARCHAR"+COLUMN_BONE_MASSRESULT+"VARCHAR"+ COLUMN_SYSTOLICRESULT+"VARCHAR"+
                COLUMN_DIASTOLICRESULT+"VARCHAR"+COLUMN_OXYGENRESULT+"VARCHAR"+ COLUMN_PULSERESULT+"VARCHAR"+
                COLUMN_TEMPRATURERESULT+"VARCHAR"+COLUMN_HEMOGLOBINRESULT+"VARCHAR"+ COLUMN_SUGARRESULT+"VARCHAR"+
                CREATED_BY +"INTEGER NULL DEFAULT NULL,"+
                UPDATED_BY +"INTEGER NULL DEFAULT NULL,"+
                DELETED_BY+ "INTEGER NULL DEFAULT NULL,"+
                CREATED_AT_TIMESTAMP +" NULL DEFAULT NULL,"+
                UPDATED_AT_TIMESTAMP +"NULL DEFAULT NULL,"+
                DELETED_AT_TIMESTAMP+ "NULL DEFAULT NULL,";
                db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /*
     * This method is taking two arguments
     * first one is the name that is to be saved
     * second one is the status
     * 0 means the name is synced with the server
     * 1 means the name is not synced with the server
     * */
    public boolean addName(String name, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

      /*  contentValues.put(COLUMN_PATIENTID, name);
        contentValues.put(COLUMN_STATUS, status);*/


        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    /*
     * this method will give us all the name stored in sqlite
     * */
    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_STATUS + " = 0;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    //region query
    private void query(){
           /*CREATE TABLE parameters (
                id int UNSIGNED NOT NULL AUTO_INCREMENT,
                patient_id int NULL DEFAULT NULL,
                height VARCHAR(255) default null,
                weight VARCHAR(255) default null,
                gender VARCHAR(255) default null,
                bmi VARCHAR(255) default null,
                bmr VARCHAR(255) default null,
                meta_age VARCHAR(255) default null,
                health_score VARCHAR(255) default null,
                physique VARCHAR(255) default null,
                subcutaneous VARCHAR(255) default null,
                visceral_fat VARCHAR(255) default null,
                skeleton_muscle VARCHAR(255) default null,
                body_water VARCHAR(255) default null,
                muscle_mass VARCHAR(255) default null,
                fat_free_weight VARCHAR(255) default null,
                protein VARCHAR(255) default null,
                body_fat VARCHAR(255) default null,
                bone_mass VARCHAR(255) default null,
                blood_pressure VARCHAR(255) default null,
                oxygen VARCHAR(255) default null,
                pulse VARCHAR(255) default null,
                temperature VARCHAR(255) default null,
                hemoglobin VARCHAR(255) default null,
                sugar VARCHAR(255) default null,
                created_by int NULL DEFAULT NULL,
        updated_by int NULL DEFAULT NULL,
        deleted_by int NULL DEFAULT NULL,
        created_at TIMESTAMP NULL DEFAULT NULL,
                updated_at TIMESTAMP NULL DEFAULT NULL,
        deleted_at TIMESTAMP NULL DEFAULT NULL,
                PRIMARY KEY (id)
);*/
    }
    //endregion
}
