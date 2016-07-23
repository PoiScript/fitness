package smu_bme.beats.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;

    public static final String CREATE_PARA = "create table para ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "date INTEGER, "
            + "spo2 INTEGER, "
            + "bpm INTEGER, "
            + "pace INTEGER)";

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PARA);
        Log.d("DEBUGGING", "Database Create Succeeded");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

