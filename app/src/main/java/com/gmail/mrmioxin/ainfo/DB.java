package com.gmail.mrmioxin.ainfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by palchuk on 28.09.2015.
 * Agroinform progect
 */
public class DB {
    private static final String DB_NAME = "mydb";
    private static final String DB_TABLE = "aricle_table";
    private static final int DB_VERSION = 1;
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IMG = "img";
    public static final String COLUMN_DESCR = "descr";

    private final Context mCtx;
    private dbHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DB(Context ctx) {
        mCtx = ctx;
    }
    // открыть подключение
    public void open() {
        mDBHelper = new dbHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }
    // закрыть подключение
    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }
    // получить все данные из таблицы DB_TABLE
    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }
    // добавить запись в DB_TABLE
    public void addRec(String descr, int img) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DESCR, descr);
        cv.put(COLUMN_IMG, img);
        mDB.insert(DB_TABLE, null, cv);
    }
    // удалить запись из DB_TABLE
    public void delRec(long id) {
        mDB.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }
}
