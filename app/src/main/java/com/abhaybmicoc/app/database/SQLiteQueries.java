package com.abhaybmicoc.app.database;

import com.abhaybmicoc.app.utils.Constant;

public class SQLiteQueries {

    public static final String QUERY_TBL_PARAMETERS = "create table IF NOT EXISTS "
            + Constant.TableNames.PARAMETERS + "("
            + Constant.Fields.PARAMETER_ID + " INTEGER primary key AUTOINCREMENT,"
            + Constant.Fields.PATIENT_ID + " VARCHAR,"
            + Constant.Fields.BMI + " VARCHAR,"
            + Constant.Fields.BMR + " VARCHAR,"
            + Constant.Fields.SUGAR + " VARCHAR,"
            + Constant.Fields.HEIGHT + " VARCHAR,"
            + Constant.Fields.WEIGHT + " VARCHAR,"
            + Constant.Fields.PROTEIN + " VARCHAR,"
            + Constant.Fields.META_AGE + " VARCHAR,"
            + Constant.Fields.BODY_FAT + " VARCHAR,"
            + Constant.Fields.PHYSIQUE + " VARCHAR,"
            + Constant.Fields.GENDER + " VARCHAR,"
            + Constant.Fields.BONE_MASS + " VARCHAR,"
            + Constant.Fields.BODY_WATER + " VARCHAR,"
            + Constant.Fields.PULSE_RATE + " VARCHAR,"
            + Constant.Fields.MUSCLE_MASS + " VARCHAR,"
            + Constant.Fields.HEMOGLOBIN + " VARCHAR,"
            + Constant.Fields.HEALTH_SCORE + " VARCHAR,"
            + Constant.Fields.VISCERAL_FAT + " VARCHAR,"
            + Constant.Fields.BLOOD_OXYGEN + " VARCHAR,"
            + Constant.Fields.TEMPERATURE + " VARCHAR,"
            + Constant.Fields.SKELETAL_MUSCLE + " VARCHAR,"
            + Constant.Fields.FAT_FREE_WEIGHT + " VARCHAR,"
            + Constant.Fields.SUBCUTANEOUS_FAT + " VARCHAR,"
            + Constant.Fields.BLOOD_PRESSURE_SYSTOLIC + " VARCHAR,"
            + Constant.Fields.BLOOD_PRESSURE_DIASTOLIC + " VARCHAR,"
            + Constant.Fields.WEIGHT_RANGE + " VARCHAR,"
            + Constant.Fields.BMI_RANGE + " VARCHAR,"
            + Constant.Fields.BODY_FAT_RANGE + " VARCHAR,"
            + Constant.Fields.SUBCUTANEOUS_FAT_RANGE + " VARCHAR,"
            + Constant.Fields.VISCERAL_FAT_RANGE + " VARCHAR,"
            + Constant.Fields.BODY_WATER_RANGE + " VARCHAR,"
            + Constant.Fields.SKELETAL_MUSCLE_RANGE + " VARCHAR,"
            + Constant.Fields.PROTEIN_RANGE + " VARCHAR,"
            + Constant.Fields.META_AGE_RANGE + " VARCHAR,"
            + Constant.Fields.HEALTH_SCORE_RANGE + " VARCHAR,"
            + Constant.Fields.BMR_RANGE + " VARCHAR,"
            + Constant.Fields.PHYSIQUE_RANGE + " VARCHAR,"
            + Constant.Fields.MUSCLE_MASS_RANGE + " VARCHAR,"
            + Constant.Fields.BONE_MASS_RANGE + " VARCHAR,"
            + Constant.Fields.TEMPERATURE_RANGE + " VARCHAR,"
            + Constant.Fields.BLOOD_PRESSURE_SYSTOLIC_RANGE + " VARCHAR,"
            + Constant.Fields.BLOOD_PRESSURE_DIASTOLIC_RANGE + " VARCHAR,"
            + Constant.Fields.BLOOD_OXYGEN_RANGE + " VARCHAR,"
            + Constant.Fields.PULSE_RATE_RANGE + " VARCHAR,"
            + Constant.Fields.SUGAR_RANGE + " VARCHAR,"
            + Constant.Fields.HEMOGLOBIN_RANGE + " VARCHAR,"
            + Constant.Fields.HEIGHT_RESULT + " VARCHAR,"
            + Constant.Fields.BMI_RESULT + " VARCHAR,"
            + Constant.Fields.BMR_RESULT + " VARCHAR,"
            + Constant.Fields.SUGAR_RESULT + " VARCHAR,"
            + Constant.Fields.WEIGHT_REUSLT + " VARCHAR,"
            + Constant.Fields.PROTEIN_RESULT + " VARCHAR,"
            + Constant.Fields.META_AGE_RESULT + " VARCHAR,"
            + Constant.Fields.BODY_FAT_RESULT + " VARCHAR,"
            + Constant.Fields.PULSE_RATE_RESULT + " VARCHAR,"
            + Constant.Fields.BONE_MASS_RESULT + " VARCHAR,"
            + Constant.Fields.BLOOD_OXYGEN_RESULT + " VARCHAR,"
            + Constant.Fields.BODY_WATER_RESULT + " VARCHAR,"
            + Constant.Fields.HEMOGLOBIN_RESULT + " VARCHAR,"
            + Constant.Fields.MUSCLE_MASS_RESULT + " VARCHAR,"
            + Constant.Fields.TEMPERATURE_RESULT + " VARCHAR,"
            + Constant.Fields.VISCERAL_FAT_RESULT + " VARCHAR,"
            + Constant.Fields.FAT_FREE_WEIGHT_RESULT + " VARCHAR,"
            + Constant.Fields.SUBCUTANEOUS_FAT_RESULT + " VARCHAR,"
            + Constant.Fields.SKELETAL_MUSCLE_RESULT + " VARCHAR,"
            + Constant.Fields.BLOOD_PRESSURE_DIASTOLIC_RESULT + " VARCHAR,"
            + Constant.Fields.BLOOD_PRESSURE_SYSTOLIC_RESULT + " VARCHAR,"
            + Constant.Fields.FATFREERSNGE + " VARCHAR,"

            + Constant.Fields.CREATED_BY + " VARCHAR,"
            + Constant.Fields.UPDATED_BY + " VARCHAR,"
            + Constant.Fields.DELETED_BY + " VARCHAR,"
            + Constant.Fields.CREATED_AT + " VARCHAR,"
            + Constant.Fields.UPDATED_AT + " VARCHAR,"
            + Constant.Fields.DELETED_AT + " VARCHAR,"
            + Constant.Fields.STATUS + " VARCHAR,"
            + Constant.Fields.IS_COMPLETED + " VARCHAR,"
            + Constant.Fields.IS_UPLOADED + " VARCHAR" + ");";


    public static final String QUERY_TBL_PATIENTS = "create table IF NOT EXISTS "
            + Constant.TableNames.PATIENTS + "("
            + Constant.Fields.PATIENT_ID + " INTEGER primary key AUTOINCREMENT,"
            + Constant.Fields.NAME + " VARCHAR,"
            + Constant.Fields.KIOSK_ID + " VARCHAR,"
            + Constant.Fields.EMAIL + " VARCHAR,"
            + Constant.Fields.TOKEN + " VARCHAR,"
            + Constant.Fields.GENDER + " VARCHAR,"
            + Constant.Fields.DATE_OF_BIRTH + " VARCHAR,"
            + Constant.Fields.MOBILE_NUMBER + " VARCHAR,"
            + Constant.Fields.DELETED_BY + " VARCHAR,"
            + Constant.Fields.CREATED_AT + " VARCHAR,"
            + Constant.Fields.DELETED_AT + " VARCHAR,"
            + Constant.Fields.STATUS + " VARCHAR,"
            + Constant.Fields.IS_UPLOADED + " VARCHAR" + ");";

    public static final String QUERY_FEEDBACK = "create table IF NOT EXISTS "
            + Constant.TableNames.FEEDBACK + "("
            + Constant.Fields.ID + " INTEGER primary key AUTOINCREMENT,"
            + Constant.Fields.PARAMETER_ID + " VARCHAR,"
            + Constant.Fields.MOBILE_NUMBER + " VARCHAR,"
            + Constant.Fields.FEEDBACK_VALUE + " VARCHAR,"
            + Constant.Fields.CREATED_AT + " VARCHAR" + ");";

    public static final String QUERY_GET_OFFLINE_DATA = "SELECT patients.patient_id ,patients.name ,patients.kiosk_id ,"
            + "patients.email ,patients.gender ,patients.dob ,patients.mobile,"
            + "parameters.* from `"
            + Constant.TableNames.PATIENTS + "` AS patients LEFT JOIN `"
            + Constant.TableNames.PARAMETERS + "` as parameters "
            + "ON patients.patient_id = parameters.patient_id "
            + "Where parameters.is_completed = 'true'";


    public static final String QUERY_GET_FEEDBACK_DATA = "SELECT * "
            + "from `"
            + Constant.TableNames.FEEDBACK + "`";


    public static String QUERY_GET_LAST_INSERTED_PATIENT_ID = "SELECT " + Constant.Fields.PATIENT_ID
            + " from " + Constant.TableNames.PATIENTS
            + " order by " + Constant.Fields.PATIENT_ID
            + " desc limit 1";

    public static String QUERY_GET_LAST_INSERTED_PARAMETER_ID = "SELECT " + Constant.Fields.PARAMETER_ID
            + " from " + Constant.TableNames.PARAMETERS
            + " order by " + Constant.Fields.PARAMETER_ID
            + " desc limit 1";

}
