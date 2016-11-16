package com.gmail.btheo95.aria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.Date;

/**
 * Created by btheo on 16.11.2016.
 */

public class Database extends SQLiteOpenHelper {

    private static final String TAG = Database.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ariadb.db";

    public static final String PICTURES_TABLE = "pictures_table";
    public static final String PICTURES_TABLE_PATH = "path";
    public static final String PICTURES_TABLE_ID = "id";


    public static final String DATE_TABLE = "date_table";
    public static final String DATE_TABLE_DATE_COLUMN = "date";
    public static final String DATE_TABLE_DATE_ID = "id";

    private Context context;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        Log.v(TAG, "Database.Database()");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.v(TAG, "Database.onCreate()");

        sqLiteDatabase.execSQL("create table " + DATE_TABLE +
                " (" + DATE_TABLE_DATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DATE_TABLE_DATE_COLUMN + " INTEGER)");

        //se intializeaza baza de date cu o data
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE_TABLE_DATE_COLUMN, 1);
        sqLiteDatabase.insert(DATE_TABLE,null ,contentValues);


        sqLiteDatabase.execSQL("create table " + PICTURES_TABLE +
                " (" + PICTURES_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PICTURES_TABLE_PATH + " TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.v(TAG, "Database.onUpgrade()");

    }


    public void deleteDatabase() {
        context.deleteDatabase(DATABASE_NAME);
    }

    public boolean addFile(File file) {
        //TODO: de implementat
        return false;
    }

    public boolean addFiles(File[] files) {
        //TODO: de implementat
        return false;
    }

    public void removeFile(File file) {
        //TODO: de implementat

    }

    public void removeFiles(File[] files) {
        //TODO: de implementat
    }

    public void updateDate() {
        SQLiteDatabase db = this.getWritableDatabase();
        Date currentDate = new Date();

        ContentValues contentValues = new ContentValues();
        String id = "1"; //TODO: sa fac cate o data pentru fiecare server
        contentValues.put(DATE_TABLE_DATE_ID, 1);
        contentValues.put(DATE_TABLE_DATE_COLUMN, currentDate.getTime());
        db.update(DATE_TABLE, contentValues, "ID = ?",new String[] { id });

    }

    public Date getLastDate () {
        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor res = db.rawQuery("select * from "+ DATE_TABLE, null);
        Cursor cursor = db.query(DATE_TABLE,
                new String[] {DATE_TABLE_DATE_COLUMN},
                DATE_TABLE_DATE_ID + " = ?",
                new String[] {"1"},
                null, null, null);
        //db.rawQuery("select * from " + DATE_TABLE, )

        int dateColumnIdex = cursor.getColumnIndex(DATE_TABLE_DATE_COLUMN);


        if (cursor.moveToFirst()) {
            int dateInt = cursor.getInt(dateColumnIdex);
            Date date = new Date(cursor.getInt(dateColumnIdex));
            return date;
        }
        else {
            return new Date(1);
        }

    }
}
