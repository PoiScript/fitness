package smu_bme.beats.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bme-lab on 5/6/16.
 */
class SQLiteHelper extends SQLiteOpenHelper {
    private static SQLiteHelper helper;
    private static final String CREATE_DATA = "CREATE TABLE IF NOT EXISTS data ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, "
            + "time TEXT NOT NULL,"
            + "id_date INTEGER,"
            + "bpm INTEGER,"
            + "FOREIGN KEY(id_date) REFERENCES date(id));";
    private static final String CREATE_DATE = "CREATE TABLE IF NOT EXISTS date(" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
            "date TEXT UNIQUE NOT NULL," +
            "avgBPM INTEGER NOT NULL," +
            "pace INTEGER," +
            "lastPace INTEGER," +
            "num INTEGER);";
    private static final String CREATE_SUM_and_Theme = "CREATE TABLE IF NOT EXISTS theme_and_sum("+
            "id INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
            "date TEXT NOT NULL," + // yyyy-MM
            "num INTEGER," +
            "sum INTEGER);";
    private static final String CREATE_INDEX = "CREATE INDEX idx_date ON date (date);CREATE INDEX idx_date ON theme_and_sum (date);";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Beats.db";

    /**
     * Constructor
     */
    private SQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public synchronized static SQLiteHelper getInstance(Context context) {
        if (helper == null) {
            helper = new SQLiteHelper(context);
        }
        return helper;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATA);
        db.execSQL(CREATE_DATE);
        db.execSQL(CREATE_INDEX);
        db.execSQL(CREATE_SUM_and_Theme);
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

