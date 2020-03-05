package com.abhaybmicoc.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.abhaybmicoc.app.utils.Constant;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context) {
        super(context, Constant.DatabaseDetails.DATABASE_NAME, null, Constant.DatabaseDetails.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
<<<<<<< HEAD
        db.execSQL(SQLiteQueries.query_TBL_PARAMETERS);
        db.execSQL(SQLiteQueries.query_TBL_PATIENTS);
        db.execSQL(SQLiteQueries.query_TBL_ERROR_LOG);
=======
        db.execSQL(SQLiteQueries.QUERY_TBL_PARAMETERS);
        db.execSQL(SQLiteQueries.QUERY_TBL_PATIENTS);
>>>>>>> feature_refactor
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA automatic_index = off;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
<<<<<<< HEAD
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TableNames.TBL_PARAMETERS);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TableNames.TBL_PATIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TableNames.ERROR_LOGS);
=======
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TableNames.PARAMETERS);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TableNames.PATIENTS);
>>>>>>> feature_refactor

        onCreate(db);
    }
}