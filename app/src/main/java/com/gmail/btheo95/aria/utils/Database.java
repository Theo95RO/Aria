package com.gmail.btheo95.aria.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gmail.btheo95.aria.model.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by btheo on 16.11.2016.
 */

public class Database extends SQLiteOpenHelper {

    private static final String TAG = Database.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ariadb.db";

    private static final String FILE_TABLE = "file_table";
    private static final String FILE_TABLE_PATH = "path";
    private static final String FILE_TABLE_ID = "id";


    private static final String DATE_TABLE = "date_table";
    private static final String DATE_TABLE_DATE_COLUMN = "date";
    private static final String DATE_TABLE_DATE_ID = "id";

    private static final String SERVER_TABLE = "server_table";
    private static final String SERVER_TABLE_IP = "ip";
    private static final String SERVER_TABLE_PORT = "port";
    private static final String SERVER_TABLE_NAME = "name";
    private static final String SERVER_TABLE_MAC = "mac";
    private static final String SERVER_TABLE_ID = "id";
    private static final String SERVER_TABLE_IS_OPENED = "is_opened";

    private static final String REMOVED_FILES_COUNT_TABLE = "removed_files_count_table";
    private static final String REMOVED_FILES_COUNT_TABLE_VALUE = "removed_files_count_table_value";

    private Context context;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.v(TAG, "Database.onCreate()");

        sqLiteDatabase.execSQL("create table " + DATE_TABLE +
                " (" + DATE_TABLE_DATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DATE_TABLE_DATE_COLUMN + " INTEGER)");

        //se intializeaza baza de date cu o data
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE_TABLE_DATE_COLUMN, 1);
        sqLiteDatabase.insert(DATE_TABLE, null, contentValues);


        sqLiteDatabase.execSQL("create table " + REMOVED_FILES_COUNT_TABLE +
                " (" + REMOVED_FILES_COUNT_TABLE_VALUE + " INTEGER)");

        contentValues = new ContentValues();
        contentValues.put(REMOVED_FILES_COUNT_TABLE_VALUE, 0);
        sqLiteDatabase.insert(REMOVED_FILES_COUNT_TABLE, null, contentValues);


        sqLiteDatabase.execSQL("create table " + FILE_TABLE +
                " (" + FILE_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FILE_TABLE_PATH + " TEXT)");

        sqLiteDatabase.execSQL("create table " + SERVER_TABLE +
                " (" + SERVER_TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SERVER_TABLE_NAME + " TEXT, " +
                SERVER_TABLE_IP + " TEXT, " +
                SERVER_TABLE_PORT + " TEXT, " +
                SERVER_TABLE_MAC + " TEXT, " +
                SERVER_TABLE_IS_OPENED + " INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "Database.onUpgrade()");

//        if (oldVersion == 1 && newVersion == 2) {
//            sqLiteDatabase.execSQL("create table " + REMOVED_FILES_COUNT_TABLE +
//                    " (" + REMOVED_FILES_COUNT_TABLE_VALUE + " INTEGER)");
//
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(REMOVED_FILES_COUNT_TABLE_VALUE, 0);
//            sqLiteDatabase.insert(REMOVED_FILES_COUNT_TABLE, null, contentValues);
//        }
    }

    public void setServer(Server server) {

        try (SQLiteDatabase db = this.getWritableDatabase()) {

            db.delete(SERVER_TABLE, SERVER_TABLE_ID + " > ?", new String[]{"0"});

            ContentValues contentValues = new ContentValues();
            contentValues.put(SERVER_TABLE_NAME, server.getDeviceName());
            contentValues.put(SERVER_TABLE_IP, server.getIp());
            contentValues.put(SERVER_TABLE_PORT, server.getPort());
            contentValues.put(SERVER_TABLE_MAC, server.getMacAddress());
            contentValues.put(SERVER_TABLE_IS_OPENED, server.isOpened());

            db.insert(SERVER_TABLE, null, contentValues);
        }
    }

    public Server getServer() {

        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery("select * from " + SERVER_TABLE, null)) {

            if (cursor.moveToFirst()) {
                int nameColumnIndex = cursor.getColumnIndex(SERVER_TABLE_NAME);
                int ipColumnIndex = cursor.getColumnIndex(SERVER_TABLE_IP);
                int portColumnIndex = cursor.getColumnIndex(SERVER_TABLE_PORT);
                int macColumnIndex = cursor.getColumnIndex(SERVER_TABLE_MAC);
                int isOpenedColumnIndex = cursor.getColumnIndex(SERVER_TABLE_IS_OPENED);

                String ip = cursor.getString(ipColumnIndex);
                String port = cursor.getString(portColumnIndex);
                String deviceName = cursor.getString(nameColumnIndex);
                boolean isOpened = getBooleanFromShort(cursor.getShort(isOpenedColumnIndex));
                String macAddress = cursor.getString(macColumnIndex);

                return new Server(ip, port, deviceName, isOpened, macAddress);

            } else {
                return null;
            }
        }
    }

    private boolean getBooleanFromShort(short aShort) {
        return aShort != 0;
    }

    public int getRemovedFilesCount() {
        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery("select * from " + REMOVED_FILES_COUNT_TABLE, null)) {

            if (cursor.moveToFirst()) {
                int valueColumnIndex = cursor.getColumnIndex(REMOVED_FILES_COUNT_TABLE_VALUE);
                return cursor.getInt(valueColumnIndex);

            } else {
                return 0;
            }
        }
    }

    public int updateRemovedFilesCount(int newCount) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(REMOVED_FILES_COUNT_TABLE_VALUE, newCount);

            return db.update(REMOVED_FILES_COUNT_TABLE, contentValues, null, null);
        }
    }

    public int incrementRemovedFilesCount() {
        int oldCount = getRemovedFilesCount();
        return updateRemovedFilesCount(++oldCount);
    }

    public void deleteDatabase() {
        context.deleteDatabase(DATABASE_NAME);
    }

    public void addFile(File file) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FILE_TABLE_PATH, file.getPath());
            db.insert(FILE_TABLE, null, contentValues);
        }
    }

    public void addFiles(File[] files) {
        if (null == files) {
            return;
        }
        for (File file : files) {
            addFile(file);
        }
    }

    public void addFiles(List<File> files) {
        if (null == files) {
            return;
        }
        for (File file : files) {
            addFile(file);
        }
    }

    public void removeFile(File file) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(FILE_TABLE, FILE_TABLE_PATH + " = ?", new String[]{file.getPath()});
            incrementRemovedFilesCount();
        }
    }

    public void removeFiles(File[] files) {
        for (File file : files) {
            removeFile(file);
        }
    }

    public void updateDate() {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            Date currentDate = new Date();

            Log.v(TAG, "Database.updateDate() - currentDate(sync): " + currentDate);
            Log.v(TAG, "Database.updateDate() - currentDate(sync): " + currentDate.getTime());
            ContentValues contentValues = new ContentValues();
            //String id = "1"; //TODO: make a date for every server?
            //contentValues.put(DATE_TABLE_DATE_ID, 1);
            contentValues.put(DATE_TABLE_DATE_COLUMN, currentDate.getTime());
            db.update(DATE_TABLE, contentValues, null, null);
        }
    }

    public List<File> getFiles() {
        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery("select " + FILE_TABLE_PATH + " from " + FILE_TABLE, null)) {

            int pathColumnIndex = cursor.getColumnIndex(FILE_TABLE_PATH);

            List<File> files = new ArrayList<>(cursor.getCount());

            int count = 0;
            while (cursor.moveToNext()) {
                File file = new File(cursor.getString(pathColumnIndex));
                files.add(file);
            }

            return files;
        }
    }

    public Date getLastDate() {
        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery("select " + DATE_TABLE_DATE_COLUMN +
                     " from " + DATE_TABLE, null)) {

//            Cursor cursor = db.query(DATE_TABLE,
//                    new String[]{DATE_TABLE_DATE_COLUMN},
//                    DATE_TABLE_DATE_ID + " = ?",
//                    new String[]{"1"},
//                    null, null, null)
            int dateColumnIndex = cursor.getColumnIndex(DATE_TABLE_DATE_COLUMN);

            if (cursor.moveToFirst()) {
                return new Date(cursor.getLong(dateColumnIndex));
            } else {
                return new Date(1);
            }
        }
    }

    public int getNumberOfFiles() {
        try (SQLiteDatabase db = this.getWritableDatabase();
             Cursor cursor = db.rawQuery("SELECT * FROM " + FILE_TABLE + ";", null)) {
            return cursor.getCount();
        }
    }

}
