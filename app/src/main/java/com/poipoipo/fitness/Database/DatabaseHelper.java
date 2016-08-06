package com.poipoipo.fitness.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    Context mContext;

    public static final String CREATE_BPM = "create table Bpm ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "time INTEGER, "
            + "data INTEGER)";

    public static final String CREATE_TEMP = "create table Temp ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "time INTEGER, "
            + "data INTEGER)";

    public static final String CREATE_LOCATION = "create table Location ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "time INTEGER, "
            + "latitude REAL, "
            + "longitude REAL)";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BPM);
        db.execSQL(CREATE_TEMP);
        db.execSQL(CREATE_LOCATION);
        Log.d("DEBUGGING", "Database Create Succeeded");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

