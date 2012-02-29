package ru.myshows.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 06.09.2011
 * Time: 10:10:48
 * To change this template use File | Settings | File Templates.
 */
public class DBAdapter {

    private static final String TAG = "DBAdapter";
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "myshows.db";
    private static String TABLE_IMAGES = "images";

    private static String CREATE_TABLE_IMAGES = "CREATE TABLE " + TABLE_IMAGES + " (showId INTEGER NOT NULL PRIMARY KEY, image BLOB)";
    private static String DROP_TABLE_IMAGES = "DROP TABLE IF EXISTS " + TABLE_IMAGES;


    private Context context;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }


    public DBAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }


    public void insertImage(Integer showId, byte[] imageBytes) {
        System.out.println("Insert image = " + showId);
        open();
        ContentValues values = new ContentValues();
        values.put("showId", showId);
        values.put("image", imageBytes);
        db.insert(TABLE_IMAGES, null, values);
        close();
    }

    public byte[] selectImage(Integer showId) {
        Cursor cursor = null;
        open();
        try {
            cursor = db.query(true, TABLE_IMAGES, null, "showId=?", new String[]{String.valueOf(showId)}, null, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getBlob(1);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        } finally {
            cursor.close();
            close();
        }
        return null;
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {


        private DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            System.out.println("Create db helper!");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            System.out.println("Create tables!");
            sqLiteDatabase.execSQL(CREATE_TABLE_IMAGES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL(DROP_TABLE_IMAGES);
            onCreate(sqLiteDatabase);
        }

    }

}
