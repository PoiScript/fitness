package smu_bme.beats.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import smu_bme.beats.Data.Para;

public class DatabasePara {

    public static final String DATABASE_NAME = "Para.db";
    public static final String TABLE_BPM = "Bpm";
    public static final String TABLE_PACE = "Pace";
    public static final String TABLE_SPO2 = "Spo2";
    public static final int VERSION = 1;

    SQLiteDatabase database;
    ContentValues values = new ContentValues();
    Cursor cursor;
    List<Para> list = new ArrayList<>();

    public DatabasePara(Context context) {
        database = new DatabaseHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
    }

    public void insert(List<Para> list) {
        for (Para para : list) {
            insert(para);
        }
    }

    public void insert(Para para) {
        values.put("date", para.getDate());
        switch (para.getType()) {
            case Para.TYPE_BPM:
                values.put("data", para.getData());
                database.insert(TABLE_BPM, null, values);
                break;
            case Para.TYPE_PACE:
                values.put("data", para.getData());
                database.insert(TABLE_PACE, null, values);
                break;
            case Para.TYPE_SPO2:
                values.put("data", para.getData());
                database.insert(TABLE_SPO2, null, values);
                break;
        }
        values.clear();
    }

    public void deleteAll() {
        database.execSQL("delete from " + TABLE_BPM);
        database.execSQL("delete from " + TABLE_PACE);
        database.execSQL("delete from " + TABLE_SPO2);
    }

    public List<Para> query(int type) {
        switch (type) {
            case Para.TYPE_BPM:
                cursor = database.query(TABLE_BPM, null, null, null, null, null, null);
                break;
            case Para.TYPE_PACE:
                cursor = database.query(TABLE_PACE, null, null, null, null, null, null);
                break;
            case Para.TYPE_SPO2:
                cursor = database.query(TABLE_SPO2, null, null, null, null, null, null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                Para para = new Para();
                para.setDate(cursor.getInt(cursor.getColumnIndex("date")));
                para.setData(cursor.getInt(cursor.getColumnIndex("data")));
                list.add(para);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Para> query(int type, int dataMin, int dataMax) {
        switch (type) {
            case Para.TYPE_BPM:
                cursor = database.query(TABLE_BPM, null, null, null, null, null, null);
                break;
            case Para.TYPE_PACE:
                cursor = database.query(TABLE_PACE, null, null, null, null, null, null);
                break;
            case Para.TYPE_SPO2:
                cursor = database.query(TABLE_SPO2, null, null, null, null, null, null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                Para para = new Para();
                para.setDate(cursor.getInt(cursor.getColumnIndex("date")));
                para.setData(cursor.getInt(cursor.getColumnIndex("data")));
                if (para.getDate() < dataMin || para.getDate() > dataMax) break;
                list.add(para);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}