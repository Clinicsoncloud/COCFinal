package com.abhaybmicoc.app.utils;

public class Constant {
    public enum SlideMenu {
        DASHBOARD,
        DEVICE_SETUP,
        LOGIN
    }

    public static class DatabaseDetails {
        public static final String DATABASE_NAME = "clinics_on_cloud";
        public static final int DATABASE_VERSION = 3;
    }

    public static class Fields {

        public static String internetIntent = "android.net.conn.CONNECTIVITY_CHANGE";

        public static final String TBL_PARAMETERS = "tbl_parameters";

        public final static String UPLOADED_RECORDS_COUNT = "uploaded_records_count";
        public final static String ID = "id";
        public final static String OTP = "otp";
        public final static String TOKEN = "token";
        public final static String KIOSK_ID = "kiosk_id";

        public static final String PARAMETER_ID = "parameter_id";
        public static final String LOCAL_PATIENT_ID = "id";

        public static final String PATIENT_ID = "patient_id";
        public final static String NAME = "name";
        public final static String EMAIL = "email";
        public final static String GENDER = "gender";
        public final static String HEIGHT = "height";
        public final static String WEIGHT = "weight";
        public final static String DATE_OF_BIRTH = "dob";
        public static final String MOBILE_NUMBER = "mobile";

        public final static String BMI = "bmi";
        public final static String BMR = "bmr";
        public final static String SUGAR = "sugar";
        public final static String PROTEIN = "protein";
        public final static String PULSE_RATE = "pulse";
        public final static String BODY_FAT = "body_fat";
        public final static String PHYSIQUE = "physique";
        public final static String META_AGE = "meta_age";
        public final static String BONE_MASS = "bone_mass";
        public final static String BLOOD_OXYGEN = "oxygen";
        public static final String IS_ATHLETE = "is_athlete";
        public final static String HEMOGLOBIN = "hemoglobin";
        public final static String BODY_WATER = "body_water";
        public final static String MUSCLE_MASS = "muscle_mass";
        public final static String TEMPERATURE = "temperature";
        public final static String GLUCOSE_TYPE = "glucose_type";
        public final static String VISCERAL_FAT = "visceral_fat";
        public final static String HEALTH_SCORE = "health_score";
        public final static String BLOOD_GLUCOSE = "BLOOD_GLUCOSE";
        public final static String TEMPRATURE_DATA = "data";
        public final static String SUBCUTANEOUS_FAT = "subcutaneous";
        public final static String FAT_FREE_WEIGHT = "fat_free_weight";
        public final static String SKELETAL_MUSCLE = "skeleton_muscle";
        public final static String BLOOD_PRESSURE_DIASTOLIC = "dialostic";
        public final static String BLOOD_PRESSURE_SYSTOLIC = "blood_pressure";

        public final static String EYE_LEFT_VISION = "eye_left_vision";
        public final static String EYE_RIGHT_VISION = "eye_right_vision";
        public final static String FEEDBACK = "feedback";


        /**
         * constants for the parameter ranges
         */
        public final static String BMI_RANGE = "bmirange";
        public final static String BMR_RANGE = "bmrrange";
        public final static String WEIGHT_RANGE = "weightrange";
        public final static String PROTEIN_RANGE = "proteinrange";
        public final static String BODY_FAT_RANGE = "bodyfatrange";
        public final static String META_AGE_RANGE = "metaagerange";
        public final static String SUGAR_RANGE = "bloodsugarrange";
        public final static String PULSE_RATE_RANGE = "pulserange";
        public static final String IS_ATHLETE_RANGE = "is_athlete";
        public final static String PHYSIQUE_RANGE = "physiquerange";
        public final static String BONE_MASS_RANGE = "bonemassrange";
        public final static String BODY_WATER_RANGE = "bodywaterrange";
        public final static String TEMPERATURE_RANGE = "bodytemprange";
        public final static String GLUCOSE_TYPE_RANGE = "glucose_type";
        public final static String HEMOGLOBIN_RANGE = "hemoglobinrange";
        public final static String MUSCLE_MASS_RANGE = "musclemassrange";
        public final static String SUBCUTANEOUS_FAT_RANGE = "subfatrange";
        public final static String VISCERAL_FAT_RANGE = "visceralfatrange";
        public final static String HEALTH_SCORE_RANGE = "healthscorerange";
        public final static String BLOOD_OXYGEN_RANGE = "pulseoximeterrange";
        public final static String FAT_FREE_WEIGHT_RANGE = "fat_free_weight";
        public final static String SKELETAL_MUSCLE_RANGE = "skeletanmusclerange";
        public final static String BLOOD_PRESSURE_SYSTOLIC_RANGE = "systolicrange";
        public final static String BLOOD_PRESSURE_DIASTOLIC_RANGE = "dialosticrange";
        public final static String EYERANGE = "eyerange";

        /**
         * constant fields for results
         */
        public final static String BMI_RESULT = "bmiresult";
        public final static String BMR_RESULT = "bmrresult";
        public final static String SUGAR_RESULT = "sugarresult";
        public final static String WEIGHT_REUSLT = "weightresult";
        public final static String HEIGHT_RESULT = "heightresult";
        public final static String PROTEIN_RESULT = "proteinresult";
        public final static String BODY_FAT_RESULT = "bodyfatresult";
        public final static String META_AGE_RESULT = "metaageresult";
        public final static String PULSE_RATE_RESULT = "pulseresult";
        public final static String BONE_MASS_RESULT = "bonemassresult";
        public final static String BLOOD_OXYGEN_RESULT = "oxygenresult";
        public final static String BODY_WATER_RESULT = "bodywaterresult";
        public final static String HEMOGLOBIN_RESULT = "hemoglobinresult";
        public final static String MUSCLE_MASS_RESULT = "musclemassresult";
        public final static String TEMPERATURE_RESULT = "temperatureresult";
        public final static String VISCERAL_FAT_RESULT = "visceralfatresult";
        public final static String SUBCUTANEOUS_FAT_RESULT = "subcutaneousresult";
        public final static String FAT_FREE_WEIGHT_RESULT = "fatfreeweightresult";
        public final static String SKELETAL_MUSCLE_RESULT = "skeletonmuscleresult";
        public final static String BLOOD_PRESSURE_SYSTOLIC_RESULT = "systolicresult";
        public final static String BLOOD_PRESSURE_DIASTOLIC_RESULT = "bloodpressureresult";
        public final static String FATFREERSNGE = "fatfreersnge";
        public final static String LEFT_EYERESULT = "eyeleftresult";
        public final static String RIGHT_EYERESULT = "eyerightresult";

        public static final String CREATED_BY = "created_by";
        public static final String UPDATED_BY = "updated_by";
        public static final String DELETED_BY = "deleted_by";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
        public static final String DELETED_AT = "deleted_at";
        public static final String STATUS = "status";

        public static final String IS_UPLOADED = "is_uploaded";
        public static final String IS_COMPLETED = "is_completed";


        public static final String APP_VERSION = "app_version";
        public static final String CLINIC_ID = "clinic_id";
        public static final String IS_TRIAL_MODE = "is_trial_mode";
        public static final String CLIENT_NAME = "client_name";
        public static final String MACHINE_OPERATOR_NAME = "machine_operator_name";
        public static final String INSTALLED_BY = "installed_by";
        public static final String MACHINE_OPERATOR_MOBILE_NUMBER = "machine_operator_mobile_number";
        public static final String INSTALLATION_DATE = "installation_date";
        public static final String ADDRESS = "address";


    }

    public static class TableNames {

        public static final String PARAMETERS = "parameters";
        public static final String PATIENTS = "patients";
    }

}
