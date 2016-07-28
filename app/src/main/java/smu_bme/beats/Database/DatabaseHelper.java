package smu_bme.beats.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    Context mContext;

    public static final String CREATE_BPM = "create table Bpm ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date INTEGER, "
            + "data INTEGER)";

    public static final String CREATE_PACE = "create table Pace ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date INTEGER, "
            + "data INTEGER)";

    public static final String CREATE_SPO2 = "create table Spo2 ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date INTEGER, "
            + "data INTEGER)";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BPM);
        db.execSQL(CREATE_PACE);
        db.execSQL(CREATE_SPO2);
        Log.d("DEBUGGING", "Database Create Succeeded");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

