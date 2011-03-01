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

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DigitbooksRatingProvider extends ContentProvider {

    private static final String AUTHORITY = "fr.digitbooks.android.examples";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static final int RATES = 1;

    private LocalDigitbooksRatingDb mDbHelper;

    private static final UriMatcher sUriMatcher;
    private static HashMap<String, String> sRateProjectionMap;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // on ne permet pas de supprimer
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case RATES:
                return "vnd.android.cursor.dir/vnd.digitbooks.rate";

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // on ne permet pas de créer des notes
        return null;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new LocalDigitbooksRatingDb(getContext()).open();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case RATES:
                qb.setTables(LocalDigitbooksRatingDb.TABLE_RATES);
                qb.setProjectionMap(sRateProjectionMap);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, null);
        // mDbHelper.close();

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    
    

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // on ne permet pas de modifier
        return 0;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "rates", RATES);

        sRateProjectionMap = new HashMap<String, String>();
        sRateProjectionMap.put(LocalDigitbooksRatingDb.KEY_ID, LocalDigitbooksRatingDb.KEY_ID);
        sRateProjectionMap.put(LocalDigitbooksRatingDb.KEY_RATING, LocalDigitbooksRatingDb.KEY_RATING);
        sRateProjectionMap.put(LocalDigitbooksRatingDb.KEY_COMMENT, LocalDigitbooksRatingDb.KEY_COMMENT);
        sRateProjectionMap.put(LocalDigitbooksRatingDb.KEY_DATE, LocalDigitbooksRatingDb.KEY_DATE);
    }

}
