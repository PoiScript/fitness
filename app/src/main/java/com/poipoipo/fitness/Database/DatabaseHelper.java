package com.poipoipo.fitness.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

import com.poipoipo.fitness.data.Location;
import com.poipoipo.fitness.data.Para;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    public static final String TABLE_BPM = "Bpm";
    public static final String TABLE_SPO2 = "Spo2";
    public static final String TABLE_TEMP = "Temp";
    public static final String TABLE_LOCATION = "Location";
    public static final String DATABASE_NAME = "Para.db";
    public static final int VERSION = 2;
    private static final String TAG = "DatabaseHelper";
    private final SQLiteDatabase database;
    private final ContentValues values = new ContentValues();
    private final List<Para> paras = new ArrayList<>();
    private final List<Location> locations = new ArrayList<>();
    private Cursor cursor;

    public DatabaseHelper(Context context) {
        database = new DatabaseOpenHelper(context).getWritableDatabase();
    }

    public void insertPara(List<Para> list) {
        for (Para para : list) {
            insertPara(para);
        }
    }

    private void insertPara(Para para) {
        try {
            values.put("time", para.getTime());
            switch (para.getType()) {
                case Para.TYPE_BPM:
                    values.put("data", para.getData());
                    database.insert(TABLE_BPM, null, values);
                    break;
                case Para.TYPE_SPO2:
                    values.put("data", para.getData());
                    database.insert(TABLE_SPO2, null, values);
                    break;
            }
            values.clear();
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    private void insertLocation(Location location) {
        try {
            values.put("time", location.getTime());
            values.put("latitude", location.getLatitude());
            values.put("longitude", location.getLongitude());
            database.insert(TABLE_LOCATION, null, values);
            values.clear();
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
    }

    public void insertLocation(List<Location> locations) {
        for (Location location : locations) {
            insertLocation(location);
        }
    }

    public void deleteAll() {
        database.execSQL("delete from " + TABLE_BPM);
        database.execSQL("delete from " + TABLE_TEMP);
        database.execSQL("delete from " + TABLE_SPO2);
        database.execSQL("delete from " + TABLE_LOCATION);
    }

    public void delete(String table) {
        database.execSQL("delete from " + table);
    }

    public List<Para> queryPara(int type, int timeMin) {
        String where = "time < ? and time > ?";
        String[] whereValue = {Integer.toString(timeMin + 24 * 60 * 60), Integer.toString(timeMin)};
        paras.clear();
        switch (type) {
            case Para.TYPE_BPM:
                cursor = database.query(TABLE_BPM, null, where, whereValue, null, null, "time");
                break;
            case Para.TYPE_SPO2:
                cursor = database.query(TABLE_SPO2, null, where, whereValue, null, null, "time");
                break;
        }
        if (cursor.moveToFirst()) {
            do {
                Para para = new Para(type);
                para.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                para.setData(cursor.getInt(cursor.getColumnIndex("data")));
                paras.add(para);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return paras;
    }

    public Location queryLocation() {
        Location location = new Location();
        cursor = database.query(TABLE_LOCATION, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                location.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                location.setLatitude(cursor.getFloat(cursor.getColumnIndex("latitude")));
                location.setLongitude(cursor.getFloat(cursor.getColumnIndex("longitude")));
                locations.add(location);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return location;
    }

    public List<Location> queryLocation(int timeMin) {
        String where = "time < ? and time > ?";
        String[] whereValue = {Integer.toString(timeMin + 24 * 60 * 60), Integer.toString(timeMin)};
        locations.clear();
        cursor = database.query(TABLE_LOCATION, null, where, whereValue, null, null, null);
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