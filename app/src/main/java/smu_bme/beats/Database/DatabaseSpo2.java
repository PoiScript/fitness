package smu_bme.beats.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import smu_bme.beats.Para;

public class DatabaseSpo2 {

    private static final String name = "Para.db";
    private static final int version = 1;
    SQLiteDatabase database;
    ContentValues values = new ContentValues();

    public DatabaseSpo2(Context context) {
        database = new DatabaseHelper(context, name, null, version).getWritableDatabase();
    }

    public void Insert(Para para) {
        values.put("date", para.getDate());
        values.put("spo2", para.getSpo2());
        values.put("pace", para.getPace());
        values.put("bpm", para.getBpm());
        database.insert(name, null, values);
        values.clear();
    }

    public void Insert(List<Para> list) {
        for (Para para : list) {
            Insert(para);
        }
    }
}