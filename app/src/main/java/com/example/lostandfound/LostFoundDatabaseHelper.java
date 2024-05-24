package com.example.lostandfound;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class LostFoundDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "lost_found_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ADVERTS = "adverts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_POST = "post";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LOCATION = "location";

    private static final String CREATE_TABLE_ADVERTS = "CREATE TABLE " + TABLE_ADVERTS +
            "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_POST + " TEXT," +
            COLUMN_NAME + " TEXT," +
            COLUMN_PHONE + " TEXT," +
            COLUMN_DESCRIPTION + " TEXT," +
            COLUMN_DATE + " TEXT," +
            COLUMN_LOCATION + " TEXT" +
            ")";

    public LostFoundDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ADVERTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADVERTS);
        onCreate(db);
    }

    public long insertAdvert(String post, String name, String phone, String description, String date, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_POST, post);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_LOCATION, location);
        return db.insert(TABLE_ADVERTS, null, values);
    }

    public List<String> getAllAdverts() {
        List<String> adverts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ADVERTS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                String post = cursor.getString(cursor.getColumnIndex(COLUMN_POST));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(COLUMN_PHONE));
                String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                String date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE));
                String location = cursor.getString(cursor.getColumnIndex(COLUMN_LOCATION));

                String advert = id + "," + post + "," + name + "," + phone + "," + description + "," + date + "," + location;
                adverts.add(advert);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return adverts;
    }

    public void deleteAllAdverts() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADVERTS, null, null);
    }

    public int deleteAdvert(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ADVERTS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAdvertById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_POST,
                COLUMN_NAME,
                COLUMN_PHONE,
                COLUMN_DESCRIPTION,
                COLUMN_DATE,
                COLUMN_LOCATION
        };

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        return db.query(
                TABLE_ADVERTS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
    }


}

