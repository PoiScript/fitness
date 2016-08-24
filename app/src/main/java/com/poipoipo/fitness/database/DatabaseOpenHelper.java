package com.poipoipo.fitness.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseOpenHelper";
    private static final String CREATE_BPM = "create table Bpm ("
            + "time INTEGER PRIMARY KEY, "
            + "data INTEGER)";
    private static final String CREATE_SPO2 = "create table Spo2 ("
            + "time INTEGER PRIMARY KEY, "
            + "data INTEGER)";
    private static final String CREATE_LOCATION = "create table Location ("
            + "time INTEGER PRIMARY KEY, "
            + "latitude REAL, "
            + "longitude REAL)";
    private static final String CREATE_ERROR = "create table Error ("
            + "id INTEGER PRIMARY KEY , "
            + "lat REAL, "
            + "lng REAL)";


    public DatabaseOpenHelper(Context context) {
        super(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BPM);
        db.execSQL(CREATE_SPO2);
        db.execSQL(CREATE_LOCATION);
        Log.d("DEBUGGING", "Database Create Succeeded");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: ");
        if (oldVersion <= 2) db.execSQL(CREATE_ERROR);
        ContentValues values = new ContentValues();
        values.put("lat", 0);
        values.put("lng", 0);
        db.insert("Error", null, values);
    }
}

