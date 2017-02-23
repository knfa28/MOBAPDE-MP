package com.example.kurt.kitakasama;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kurt.kitakasama.LocalTracker;
import com.example.kurt.kitakasama.LocalUser;

import java.util.ArrayList;

public class MySQLiteModel extends SQLiteOpenHelper{
    
    public MySQLiteModel(Context context) {
        super(context, LocalTracker.TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + LocalTracker.TABLE_NAME + "("
                + LocalTracker.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LocalTracker.COLUMN_TRACKER + " TEXT,"
                + LocalTracker.COLUMN_CONTACT + " TEXT" + ")");

        db.execSQL("CREATE TABLE " + LocalUser.TABLE_NAME + "("
                + LocalUser.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LocalUser.COLUMN_USER + " TEXT,"
                + LocalUser.COLUMN_CONTACT + " TEXT,"
                + LocalUser.COLUMN_NEUTRAL + " TEXT,"
                + LocalUser.COLUMN_NEGATIVE + " TEXT,"
                + LocalUser.COLUMN_EXTREME + " TEXT,"
                + LocalUser.COLUMN_CONFIRM + " TEXT,"
                + LocalUser.COLUMN_CHECK + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long addUser(LocalUser u){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LocalUser.COLUMN_USER, u.getUserName());
        values.put(LocalUser.COLUMN_CONTACT, u.getUserContact());
        values.put(LocalUser.COLUMN_NEUTRAL, u.getNeutralMsg());
        values.put(LocalUser.COLUMN_NEGATIVE, u.getNegativeMsg());
        values.put(LocalUser.COLUMN_EXTREME, u.getExtremeMsg());
        values.put(LocalUser.COLUMN_CONFIRM, u.getConfirmMsg());
        values.put(LocalUser.COLUMN_CHECK, u.getCheckMsg());

        long id = db.insert(LocalUser.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public LocalUser getUser(int id) {
        LocalUser u;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(LocalUser.TABLE_NAME,
                new String[]{LocalUser.COLUMN_ID,
                                LocalUser.COLUMN_USER,
                                LocalUser.COLUMN_CONTACT,
                                LocalUser.COLUMN_NEUTRAL,
                                LocalUser.COLUMN_NEGATIVE,
                                LocalUser.COLUMN_EXTREME,
                                LocalUser.COLUMN_CONFIRM,
                                LocalUser.COLUMN_CHECK},
                " " + LocalTracker.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor.moveToFirst()) {
            u = new LocalUser();

            u.setUserId(Integer.parseInt(cursor.getString(0)));
            u.setUserName(cursor.getString(1));
            u.setUserContact(cursor.getString(2));
            u.setNeutralMsg(cursor.getString(3));
            u.setNegativeMsg(cursor.getString(4));
            u.setExtremeMsg(cursor.getString(5));
            u.setConfirmMsg(cursor.getString(6));
            u.setCheckMsg(cursor.getString(7));
        } else {
            u = null;
        }

        db.close();
        cursor.close();

        return u;
    }

    public int getUserCnt() {
        int count = 0;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + LocalUser.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                count++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return count;
    }

    public long addTracker(LocalTracker t){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LocalTracker.COLUMN_TRACKER, t.getTrackerName());
        values.put(LocalTracker.COLUMN_CONTACT, t.getTrackerContact());

        long id = db.insert(LocalTracker.TABLE_NAME, null, values);
        db.close();

        return id;
    }

    public LocalTracker getTracker(int id) {
        LocalTracker t;
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(LocalTracker.TABLE_NAME,
                new String[]{LocalTracker.COLUMN_ID, LocalTracker.COLUMN_TRACKER, LocalTracker.COLUMN_CONTACT},
                " " + LocalTracker.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor.moveToFirst()) {
            t = new LocalTracker(Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2));
        } else {
            t = null;
        }

        db.close();
        cursor.close();

        return t;
    }

    public ArrayList<LocalTracker> getListTrackers() {
        ArrayList<LocalTracker> localTrackers = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + LocalTracker.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                localTrackers.add(new LocalTracker(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return localTrackers;
    }

    public ArrayList<String> getTrackerContacts() {
        ArrayList<String> contacts = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT contact FROM " + LocalTracker.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                contacts.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return contacts;
    }

    public int getTrackerCnt() {
        int count = 0;

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + LocalTracker.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                count++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return count;
    }

    public Cursor getCursorTrackers() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + LocalTracker.TABLE_NAME, null);

        return cursor;
        //do not close db or cursor
    }

    public int deleteTracker(LocalTracker t) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsAffected = db.delete(LocalTracker.TABLE_NAME,
                LocalTracker.COLUMN_ID + "=?",
                new String[]{String.valueOf(t.getTrackerId())});

        db.close();

        return rowsAffected;
    }

    public int editTracker(LocalTracker t) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LocalTracker.COLUMN_TRACKER, t.getTrackerName());
        values.put(LocalTracker.COLUMN_CONTACT, t.getTrackerContact());

        int rowsAffected = db.update(LocalTracker.TABLE_NAME,
                values,
                " " + LocalTracker.COLUMN_ID + "=?",
                new String[] { String.valueOf(t.getTrackerId()) });

        db.close();

        return rowsAffected;
    }
}
