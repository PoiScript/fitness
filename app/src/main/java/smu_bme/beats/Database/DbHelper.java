package smu_bme.beats.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import smu_bme.beats.DbData;

/**
 * Created by gollyrui on 5/4/16.
 */
public class DbHelper {
    //    private final String tableData = "data";
//    private final String tableDate = "date";
    Context context;
    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase db;

    public DbHelper(Context context) {
        this.context = context;
        //TODO init and check
//        sqLiteHelper = sqLiteHelper.getInstance(context);
//        db = sqLiteHelper.getWritableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM theme_and_sum where id = ?;",new String[]{"1"});
//        if(cursor.getCount()==0){
//            db.execSQL("INSERT INTO theme_and_sum (date,sum) VALUES ('theme',0);");
//        }
    }

    public void updatePace(int pace) {
        /** For update pace only
         If the date doesn't exist, then insert into table 'date';
         else updates.*/
        sqLiteHelper = sqLiteHelper.getInstance(context);
        db = sqLiteHelper.getWritableDatabase();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        String date = df.format(Calendar.getInstance().getTime());// new Date()为获取当前系统时间
        int dateId = queryDatePrivate(date);
        if (dateId == 0) {
            db.execSQL("INSERT INTO date (date,avgBPM,num,pace,lastPace) values ('" + date + "',0,0,0,0);");
            db.execSQL("UPDATE date SET pace = " + pace + " WHERE date = '" + date + "';");
        } else {
            /**  To get the delta between current pace in DB and the last pace in DB
             *  If the incoming pace is larger than delta, which indicates that the incoming pace is the continuation of the "current pace" in DB
             *  Otherwise, the incoming pace is of a start.*/

            Cursor cursor = db.rawQuery("SELECT pace,lastPace FROM date WHERE id = ?;", new String[]{dateId+""});
            cursor.moveToFirst();
            int currentPace = cursor.getInt(cursor.getColumnIndex("pace"));
            int delta = currentPace - cursor.getInt(cursor.getColumnIndex("lastPace"));
            if (pace >= delta) {
                int paceSum = currentPace + pace - delta;
                db.execSQL("UPDATE date SET pace = " + paceSum + " WHERE date = '" + date + "' ;");//'$date'
            } else {
                int paceSum = currentPace + pace;
                db.execSQL("UPDATE date SET pace = " + paceSum + ", lastPace = " + currentPace + " WHERE date = '" + date + "' ;");//'$date'
            }
        }
    }

    public DbData queryDate(String date) {
        /** For query. Return a DbData type object with date, avgBPM and pace info.*/
        sqLiteHelper = sqLiteHelper.getInstance(context);
        db = sqLiteHelper.getWritableDatabase();
        DbData out = null;
        Cursor cursor = db.rawQuery("SELECT * FROM date WHERE date = ?;", new String[]{date});
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            out = new DbData(date, cursor.getInt(cursor.getColumnIndex("avgBPM")),
                    cursor.getInt(cursor.getColumnIndex("pace")));
            Log.d(Thread.currentThread().getName(), "query date Not null");
        }
//        db.close();
        return out;
    }

    private int queryDatePrivate(String date) {
        /**To return the id of given date in table 'date'*/
        int id = 0;
        sqLiteHelper = sqLiteHelper.getInstance(context);
        db = sqLiteHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM date WHERE date = ?;", new String[]{date});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        return id;
    }

    public ArrayList<DbData> queryForVisualization(String date) {
        /** Return DbData list with date, time, and instant bpm;*/
        ArrayList<DbData> list = new ArrayList<>();
        sqLiteHelper = sqLiteHelper.getInstance(context);
        db = sqLiteHelper.getWritableDatabase();
        int id = queryDatePrivate(date);
        Cursor cursor = db.rawQuery("SELECT * FROM data WHERE id_date= ?;", new String[]{String.valueOf(id)});
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                DbData dbData = new DbData(date,
                        cursor.getString(cursor.getColumnIndex("time")),
                        cursor.getInt(cursor.getColumnIndex("bpm"))
//                        cursor.getInt(cursor.getColumnIndex("pace"))
                );
                list.add(dbData);
            }
        }
//        db.close();
        return list;
    }

    public int insertData(DbData dbData) {
        /** For insert Data in the type of DbData with date, time and bpm*/
        sqLiteHelper = sqLiteHelper.getInstance(context);
        db = sqLiteHelper.getWritableDatabase();
//        db.execSQL("INSERT INTO ");

        /** Check whether date already exists in table 'date' .*/
        String date = dbData.getDate();
        if (queryDatePrivate(date) > 0) {
            // If exists then update avg and num
            Cursor cursor = db.rawQuery("SELECT * FROM date WHERE date = ?;", new String[]{date});
            cursor.moveToFirst();
            int avg = cursor.getInt(cursor.getColumnIndex("avgBPM"));
            int n = cursor.getInt(cursor.getColumnIndex("num"));
            avg = (avg * n + dbData.getBPM()) / (n + 1);
            n++;
            try {
                db.getVersion();
            } catch (Exception e) {
                db = sqLiteHelper.getWritableDatabase();
            } finally {
                db.execSQL("UPDATE date SET avgBPM = " + avg + ", num = " + n + " WHERE date = '" + date + "';");
            }

        } else {
            /** if not exists, insert info into date with date and avg=current_bpm and num=1*/
            try {
                db.getVersion();
            } catch (Exception e) {
                db = sqLiteHelper.getWritableDatabase();
            } finally {
                db.execSQL("INSERT INTO date (date,avgBPM,num,pace,lastPace) values ('" + date + "'," + dbData.getBPM() + ",1,0,0);");
            }
        }
        int id_date = queryDatePrivate(date);

        /**Insert into data*/
//        Log.d("DEBUGGING", Thread.currentThread().getName()+"-- id_date:"+id_date);
        try {
            db.getVersion();
        } catch (Exception e) {
            db = sqLiteHelper.getWritableDatabase();
        } finally {
            db.execSQL("INSERT INTO data (id_date,time,bpm) values (" + id_date + ",'" + dbData.getTime() + "'," + dbData.getBPM() + ");");
        }
//        db.close();
        return id_date;
    }


}