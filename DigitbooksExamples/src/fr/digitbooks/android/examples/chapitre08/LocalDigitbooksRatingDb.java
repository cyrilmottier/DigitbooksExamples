/*
 * Copyright (C) 2010   Cyril Mottier & Ludovic Perrier
 *              (http://www.digitbooks.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.digitbooks.android.examples.chapitre08;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class LocalDigitbooksRatingDb {

    private static final String LOG_TAG = LocalDigitbooksRatingDb.class.getSimpleName();

    private static final String DATABASE_NAME = "digitbooks_rating_db";
    private static final int DATABASE_VERSION = 1;

    // Database table
    public static final String TABLE_RATES = "rate";

    // fields
    public static final String KEY_ID = "_id";
    public static final String KEY_RATING = "rating";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_DATE = "date";

    /**
     * Database creation sql statement
     */
    private static final String TABLE_ALERTS_LISTS_CREATE = "create table " + TABLE_RATES + " (" + KEY_ID
            + " integer primary key autoincrement, " + KEY_RATING + " real not null default 0, " + KEY_COMMENT
            + " text, " + KEY_DATE + " long not null)";

    private DatabaseHelper mDbHelper;
    private final Context mContext;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase dataBase) {
            dataBase.execSQL(TABLE_ALERTS_LISTS_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
            Log.w(LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", will destroy all old data");
            dataBase.execSQL("DROP TABLE IF EXISTS " + TABLE_RATES);
            onCreate(dataBase);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public LocalDigitbooksRatingDb(Context ctx) {
        this.mContext = ctx;
    }

    /**
     * Open the my_alerts_db database. If it cannot be opened, try to create a
     * new instance of the database. If it cannot be created, throw an exception
     * to signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public LocalDigitbooksRatingDb open() throws SQLException {
        mDbHelper = new DatabaseHelper(mContext);
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public boolean createRate(float rating, String comment, long date) {
        SQLiteDatabase mDb = mDbHelper.getWritableDatabase();
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_RATING, rating);
        initialValues.put(KEY_COMMENT, comment);
        ;
        initialValues.put(KEY_DATE, date);
        return mDb.insert(TABLE_RATES, null, initialValues) > 0;

    }

    public Cursor fetchAllRates() {
        SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
        return mDb.query(TABLE_RATES, new String[] {
                KEY_ID, KEY_RATING, KEY_COMMENT, KEY_DATE
        }, null, null, null, null, KEY_DATE + " DESC");
    }

    public void dropAllRates() {
        SQLiteDatabase mDb = mDbHelper.getReadableDatabase();
        mDb.delete(TABLE_RATES, null, null);
    }

    public SQLiteDatabase getReadableDatabase() {
        return mDbHelper.getReadableDatabase();
    }
}
