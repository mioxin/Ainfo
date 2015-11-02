package com.gmail.mrmioxin.ainfo;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.HashMap;

/**
 * Created by palchuk on 28.09.2015.
 * Agroinform progect
 * Custom Content Provider for DB
 */
public class DB extends ContentProvider{
    private static final String MY_LOG = "My_log.DBProvider.";
    private static final int DB_VERSION = 1;
    private static HashMap<String, String> sArticlesProjectionMap;
    private static final int ARTICLES = 1;
    private static final int ARTICLES_ID = 2;
    private static final UriMatcher sUriMatcher;
    private dbHelper mDBHelper;
    private SQLiteDatabase mDB;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ContractClass.AUTHORITY, "articles", ARTICLES);
        sUriMatcher.addURI(ContractClass.AUTHORITY, "articles/#", ARTICLES_ID);
        sArticlesProjectionMap = new HashMap<String, String>();
        for (int i = 0; i < ContractClass.Articles.DEFAULT_PROJECTION.length; i++) {
            sArticlesProjectionMap.put(
                    ContractClass.Articles.DEFAULT_PROJECTION[i],
                    ContractClass.Articles.DEFAULT_PROJECTION[i]);
        }
    }

    public class dbHelper extends SQLiteOpenHelper {
        private static final String MY_LOG = "My_log.dbHelper.";
        private static final String DB_NAME = "mydb";
        private static final String DB_TABLE = ContractClass.Articles.TAB_NAME;

//        public static final String KEY_ROWID   = "_id";
//        public static final String KEY_DATE    = "date";
//        public static final String KEY_TITLE   = "title";
//        public static final String KEY_DESCR   = "descr";
//        public static final String KEY_CHECK   = "fk_class_id";
//        public static final String KEY_CONTENT = "content";
//        public static final String KEY_IMG     = "img";
//        public static final String KEY_IMG_SRC = "img_src";
//        public static final String KEY_TAGS    = "tags";

        private static final String DATABASE_CREATE_TABLE_ARTICLES =
                "create table "+DB_TABLE +" (" +
                        ContractClass.Articles._ID   + "primary key autoincrement," +
                        ContractClass.Articles.COLNAME_DATE    + " text," +
                        ContractClass.Articles.COLNAME_TITLE   + " text," +
                        ContractClass.Articles.COLNAME_DESCR   + " text," +
                        ContractClass.Articles.COLNAME_CHECK   + " bool," +
                        ContractClass.Articles.COLNAME_CONTENT + " text," +
                        ContractClass.Articles.COLNAME_IMG     + " int," +
                        ContractClass.Articles.COLNAME_IMG_SRC + " text," +
                        ContractClass.Articles.COLNAME_TAGS    +  " text)";
        private Context ctx;

        dbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            ctx = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(MY_LOG, "--- onCreate database ---");
// создаем таблицу с полями
            db.execSQL(DATABASE_CREATE_TABLE_ARTICLES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.d(MY_LOG, "--- RE Create database ---");

            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_TABLE_ARTICLES);
            onCreate(db);
        }
    }

    /**
     * Implement this to initialize your content provider on startup.
     * This method is called for all registered content providers on the
     * application main thread at application launch time.  It must not perform
     * lengthy operations, or application startup will be delayed.
     * <p/>
     * <p>You should defer nontrivial initialization (such as opening,
     * upgrading, and scanning databases) until the content provider is used
     * (via {@link #query}, {@link #insert}, etc).  Deferred initialization
     * keeps application startup fast, avoids unnecessary work if the provider
     * turns out not to be needed, and stops database errors (such as a full
     * disk) from halting application launch.
     * <p/>
     * <p>If you use SQLite, {@link SQLiteOpenHelper}
     * is a helpful utility class that makes it easy to manage databases,
     * and will automatically defer opening until first use.  If you do use
     * SQLiteOpenHelper, make sure to avoid calling
     * {@link SQLiteOpenHelper#getReadableDatabase} or
     * {@link SQLiteOpenHelper#getWritableDatabase}
     * from this method.  (Instead, override
     * {@link SQLiteOpenHelper#onOpen} to initialize the
     * database when it is first opened.)
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        mDBHelper = new dbHelper(getContext());
        return true;

    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy = null;
        switch (sUriMatcher.match(uri)) {
            case ARTICLES:
                qb.setTables(ContractClass.Articles.TAB_NAME);
                qb.setProjectionMap(sArticlesProjectionMap);
                orderBy = ContractClass.Articles.DEFAULT_SORT_ORDER;
                break;
            case ARTICLES_ID:
                qb.setTables(ContractClass.Articles.TAB_NAME);
                qb.setProjectionMap(sArticlesProjectionMap);
                qb.appendWhere(ContractClass.Articles._ID + "=" + uri.getPathSegments().get(ContractClass.Articles.ARTICLE_ID_PATH_POSITION));
                orderBy = ContractClass.Articles.DEFAULT_SORT_ORDER;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case ARTICLES:
                return ContractClass.Articles.CONTENT_TYPE;
            case ARTICLES_ID:
                return ContractClass.Articles.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(MY_LOG + "Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != ARTICLES ) {
            throw new IllegalArgumentException(MY_LOG + "Invalid URI " + uri);
        }
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        }
        else {
            values = new ContentValues();
        }
        long rowId = -1;
        Uri rowUri = Uri.EMPTY;
        switch (sUriMatcher.match(uri)) {
            case ARTICLES_ID:
                if (values.containsKey(ContractClass.Articles.COLNAME_DATE) == false) {
                    values.put(ContractClass.Articles.COLNAME_DATE, "");
                }
                if (values.containsKey(ContractClass.Articles.COLNAME_TITLE) == false) {
                    values.put(ContractClass.Articles.COLNAME_TITLE, "");
                }
                if (values.containsKey(ContractClass.Articles.COLNAME_DESCR) == false) {
                    values.put(ContractClass.Articles.COLNAME_TITLE, "");
                }
                if (values.containsKey(ContractClass.Articles.COLNAME_CONTENT) == false) {
                    values.put(ContractClass.Articles.COLNAME_TITLE, "");
                }
                if (values.containsKey(ContractClass.Articles.COLNAME_IMG) == false) {
                    values.put(ContractClass.Articles.COLNAME_DATE, 0);
                }
                if (values.containsKey(ContractClass.Articles.COLNAME_IMG_SRC) == false) {
                    values.put(ContractClass.Articles.COLNAME_TITLE, "");
                }
                if (values.containsKey(ContractClass.Articles.COLNAME_TAGS) == false) {
                    values.put(ContractClass.Articles.COLNAME_TITLE, "");
                }
                rowId = db.insert(ContractClass.Articles.TAB_NAME,
                        ContractClass.Articles.COLNAME_DATE,
                        values);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(ContractClass.Articles.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                }
                break;
        }
        return rowUri;

    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String finalWhere;
        int count;
        switch (sUriMatcher.match(uri)) {
            case ARTICLES:
                count = db.delete(ContractClass.Articles.TAB_NAME,where,whereArgs);
                break;
            case ARTICLES_ID:
                finalWhere = ContractClass.Articles._ID + " = " + uri.getPathSegments().get(ContractClass.Articles.ARTICLE_ID_PATH_POSITION);
                if (where != null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.delete(ContractClass.Articles.TAB_NAME,finalWhere,whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;

    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int count;
        String finalWhere;
        String id;
        switch (sUriMatcher.match(uri)) {
            case ARTICLES:
                count = db.update(ContractClass.Articles.TAB_NAME, values, where, whereArgs);
                break;
            case ARTICLES_ID:
                id = uri.getPathSegments().get(ContractClass.Articles.ARTICLE_ID_PATH_POSITION);
                finalWhere = ContractClass.Articles._ID + " = " + id;
                if (where !=null) {
                    finalWhere = finalWhere + " AND " + where;
                }
                count = db.update(ContractClass.Articles.TAB_NAME, values, finalWhere, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;

    }
}
