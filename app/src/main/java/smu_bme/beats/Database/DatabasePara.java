package smu_bme.beats.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import smu_bme.beats.Para;

public class DatabasePara {

    DatabaseHelper helper;
    SQLiteDatabase database;
    ContentValues values = new ContentValues();

    public DatabasePara(String name, Context context){
        helper = new DatabaseHelper(context, name, null, 1);
        database = helper.getWritableDatabase();
    }

    public void Insert(Para para){

    }

}