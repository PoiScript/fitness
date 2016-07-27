package smu_bme.beats.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import smu_bme.beats.Data.Bpm;
import smu_bme.beats.Data.Pace;
import smu_bme.beats.Data.Spo2;

public class TablePara {

    private static final String name = "Data.db";
    private static final int version = 1;
    SQLiteDatabase database;
    ContentValues values = new ContentValues();

    public TablePara(Context context) {
        database = new DatabaseHelper(context, name, null, version).getWritableDatabase();
    }

    public void insertBpm(List<Bpm> list) {
        for (Bpm bpm : list) {
            insertBpm(bpm);
        }
    }

    public void insertBpm(Bpm bpm) {
        values.put("date", bpm.getDate());
        values.put("bpm", bpm.getBpm());
        database.insert(name, null, values);
        values.clear();
    }

    public void insertPace(Pace pace) {
        values.put("date", pace.getDate());
        values.put("pace", pace.getPace());
        database.insert(name, null, values);
        values.clear();
    }

    public void insertPace(List<Pace> list) {
        for (Pace pace : list) {
            insertPace(pace);
        }
    }

    public void insertSpo2(Spo2 spo2) {
        values.put("date", spo2.getDate());
        values.put("spo2", spo2.getSpo2());
        database.insert(name, null, values);
        values.clear();
    }

    public void insertSpo2(List<Spo2> list) {
        for (Spo2 spo2 : list) {
            insertSpo2(spo2);
        }
    }
}