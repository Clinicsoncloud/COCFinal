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
        db.execSQL(SQLiteQueries.query_TBL_PARAMETERS);
        db.execSQL(SQLiteQueries.query_TBL_PATIENTS);
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
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TableNames.TBL_PARAMETERS);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.TableNames.TBL_PATIENTS);

        onCreate(db);
    }
}