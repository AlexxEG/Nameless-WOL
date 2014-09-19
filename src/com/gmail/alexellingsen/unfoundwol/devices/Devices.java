package com.gmail.alexellingsen.unfoundwol.devices;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.gmail.alexellingsen.unfoundwol.DatabaseHandler;

import java.util.ArrayList;
import java.util.List;

public class Devices {

    public static final String TABLE_NAME = "devices";
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_HOST = "host";
    public static final String KEY_PORT = "port";
    public static final String KEY_MAC = "mac";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + KEY_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME + " TEXT, "
            + KEY_HOST + " TEXT, "
            + KEY_PORT + " INTEGER, "
            + KEY_MAC + " TEXT" + ");";

    private static DatabaseHandler _db;

    public static int add(Device device) {
        SQLiteDatabase db = _db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, device.getName());
        values.put(KEY_HOST, device.getHost());
        values.put(KEY_PORT, device.getPort());
        values.put(KEY_MAC, device.getMac());

        int id = (int) db.insert(TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public static void delete(Device device) {
        SQLiteDatabase db = _db.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?",
                new String[]{String.valueOf(device.getID())});
        db.close();
    }

    public static Device find(String name) {
        SQLiteDatabase db = _db.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID, KEY_NAME, KEY_HOST, KEY_PORT, KEY_MAC},
                KEY_NAME + " = ?",
                new String[]{name}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // No device found?
        if (cursor == null)
            return null;

        Device device = new Device(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4)
        );
        device.setID(cursor.getInt(0));

        cursor.close();
        db.close();

        return device;
    }

    public static Device get(int id) {
        SQLiteDatabase db = _db.getReadableDatabase();

        // Find row by id
        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_ID, KEY_NAME, KEY_HOST, KEY_PORT, KEY_MAC},
                KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // No device found?
        if (cursor == null)
            return null;

        Device device = new Device(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3),
                cursor.getString(4)
        );
        device.setID(cursor.getInt(0));

        cursor.close();
        db.close();

        return device;
    }

    public static List<Device> getAll() {
        List<Device> devicesList = new ArrayList<Device>();
        String query = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = _db.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Device device = new Device(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4)
                );
                device.setID(cursor.getInt(0));

                devicesList.add(device);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return devicesList;
    }

    public static int getCount() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = _db.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        cursor.close();
        db.close();

        return count;
    }

    /**
     * Initializes DatabaseHandler, most be called before using class.
     *
     * @param context The Context to use.
     */
    public static void initialize(Context context) {
        _db = new DatabaseHandler(context);
    }

    public static int update(Device device) {
        SQLiteDatabase db = _db.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, device.getName());
        values.put(KEY_HOST, device.getHost());
        values.put(KEY_PORT, device.getPort());
        values.put(KEY_MAC, device.getMac());

        int rows = db.update(TABLE_NAME, values, KEY_ID + " = ?",
                new String[]{String.valueOf(device.getID())});

        db.close();

        return rows;
    }

}