package com.poipoipo.fitness.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import com.poipoipo.fitness.Data.Location;
import com.poipoipo.fitness.Data.Para;

public class DatabasePara {

    public static final String DATABASE_NAME = "Para.db";
    public static final String TABLE_BPM = "Bpm";
    public static final String TABLE_TEMP = "Temp";
    public static final String TABLE_LOCATION = "Location";
    public static final int VERSION = 2;

    SQLiteDatabase database;
    ContentValues values = new ContentValues();
    Cursor cursor;
    List<Para> paras = new ArrayList<>();
    List<Location> locations = new ArrayList<>();

    public DatabasePara(Context context) {
        database = new DatabaseHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
    }

    public void insert(List<Para> list) {
        for (Para para : list) {
            insert(para);
        }
    }

    public void insert(Para para) {
        values.put("date", para.getTime());
        switch (para.getType()) {
            case Para.TYPE_BPM:
                values.put("data", para.getData());
                database.insert(TABLE_BPM, null, values);
                break;
            case Para.TYPE_TEMP:
                values.put("data", para.getData());
                database.insert(TABLE_TEMP, null, values);
                break;
//            case Para.TYPE_SPO2:
//                values.put("data", para.getData());
//                database.insert(TABLE_SPO2, null, values);
//                break;
        }
        values.clear();
    }

    public void deleteAll() {
        database.execSQL("delete from " + TABLE_BPM);
        database.execSQL("delete from " + TABLE_TEMP);
//        database.execSQL("delete from " + TABLE_SPO2);
    }

    public List<Para> query(int type) {
        paras.clear();
        switch (type) {
            case Para.TYPE_BPM:
                cursor = database.query(TABLE_BPM, null, null, null, null, null, null);
                break;
            case Para.TYPE_TEMP:
                cursor = database.query(TABLE_TEMP, null, null, null, null, null, null);
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                Para para = new Para();
                para.setTime(cursor.getInt(cursor.getColumnIndex("date")));
                para.setData(cursor.getInt(cursor.getColumnIndex("data")));
                paras.add(para);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return paras;
    }

    public List<Para> query(int type, int timeMin, int timeMax) {
        String where = "time < ? and time > ?";
        String[] whereValue = {Integer.toString(timeMax), Integer.toString(timeMin)};
        paras.clear();
        switch (type) {
            case Para.TYPE_BPM:
                cursor = database.query(TABLE_BPM, null, where, whereValue, null, null, null);
                break;
            case Para.TYPE_TEMP:
                cursor = database.query(TABLE_TEMP, null, where, whereValue, null, null, null);
                break;
//            case Para.TYPE_SPO2:
//                cursor = database.query(TABLE_SPO2, null, null, null, null, null, null);
//                break;
        }
        if (cursor.moveToFirst()) {
            do {
                Para para = new Para();
                para.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                para.setData(cursor.getInt(cursor.getColumnIndex("data")));
                if (para.getTime() < timeMin || para.getTime() > timeMax) break;
                paras.add(para);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return paras;
    }

    public List<Location> query() {
        locations.clear();
        cursor = database.query(TABLE_LOCATION, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Location location = new Location();
                location.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                location.setLatitude(cursor.getFloat(cursor.getColumnIndex("latitude")));
                location.setLongitude(cursor.getFloat(cursor.getColumnIndex("longitude")));
                locations.add(location);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return locations;
    }

    public List<Location> query(int timeMin, int timeMax) {
        String where = "time < ? and time > ?";
        String[] whereValues = {Integer.toString(timeMax), Integer.toString(timeMin)};
        locations.clear();
        cursor = database.query(TABLE_LOCATION, null, where, whereValues, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Location location = new Location();
                location.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                location.setLatitude(cursor.getFloat(cursor.getColumnIndex("latitude")));
                location.setLongitude(cursor.getFloat(cursor.getColumnIndex("longitude")));
                locations.add(location);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return locations;
    }
}