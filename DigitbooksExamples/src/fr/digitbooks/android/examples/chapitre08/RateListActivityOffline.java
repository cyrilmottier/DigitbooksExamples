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

import java.util.Date;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import fr.digitbooks.android.examples.R;
import fr.digitbooks.android.examples.util.Config;
import fr.digitbooks.android.examples.util.Helpers;

public class RateListActivityOffline extends ListActivity implements ViewBinder {

    private static final String LOG_TAG = RateListActivityOffline.class.getSimpleName();

    private static final int FIRST_MENU_ID = Menu.FIRST;

    // activity requestCode
    final static int ADD_RATING = 1;

    // Widget
    private LinearLayout mLoading;
    private TextView mEmptyList;

    // on déclare un champs date pour éviter la création pour chaque ligne de la
    // liste
    private Date date;

    // code pour la déclaration et le lancement de l'animation de progression du
    // chargement
    private AnimationDrawable mAnimation;

    private boolean requestComplete = false;

    public static final String ACTION_REFRESH = "fr.digitbooks.android.examples.ACTION_REFRESH";
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Config.INFO_LOGS_ENABLED) {
                Log.i(LOG_TAG, "Broadcast received");
            }
            String action = intent.getAction();
            if (action.equals(ACTION_REFRESH)) {
                mEmptyList.setVisibility(View.GONE);
                requestComplete = true;
                updateList();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        date = new Date();

        setContentView(R.layout.guestbook_list);

        // Création d'une animation pour faire patienter l'utilisateur
        final ImageView andy = (ImageView) findViewById(R.id.animation);
        andy.setBackgroundResource(R.drawable.andy);
        mAnimation = (AnimationDrawable) andy.getBackground();
        // Démarrage de l'animation
        andy.post(new Runnable() {
            public void run() {
                mAnimation.start();
            }
        });

        // On récupère le widget de chargement
        mLoading = (LinearLayout) findViewById(R.id.loading);
        mEmptyList = (TextView) findViewById(R.id.empty_list);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register for Intent broadcasts
        IntentFilter filter = new IntentFilter(ACTION_REFRESH);
        registerReceiver(mIntentReceiver, filter);
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, "BroadCast registered");
        }
        if (!requestComplete)
            requestRateList();
        else
            updateList();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mIntentReceiver);
        if (Config.INFO_LOGS_ENABLED) {
            Log.d(LOG_TAG, "BroadCast unregistered");
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, FIRST_MENU_ID, 0, "Voter").setIcon(android.R.drawable.ic_menu_add);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!Helpers.isNetworkAvailable(this)) {
            // On affiche pas de menu si pas de connexion internet pour voter
            Toast.makeText(this, R.string.offline_menu, Toast.LENGTH_LONG).show();
            return false;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case FIRST_MENU_ID:
                Intent intent = new Intent(this, RateDigitbooksActivity.class);
                intent.putExtra(RateDigitbooksActivity.RATE_ONCE, false);
                startActivityForResult(intent, ADD_RATING);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestRateList() {
        getListView().setEmptyView(mLoading);
        // On va démarrer le chargement par une barre de chargement
        if (!Helpers.isNetworkAvailable(this)) {
            // Pas de réseau on affiche la liste disponnible dans la base de
            // données local
            Toast.makeText(this, R.string.offline_rates, Toast.LENGTH_LONG).show();
            updateList();
        } else {
            mEmptyList.setVisibility(View.GONE);
            setListAdapter(null);
            // On lance une tâche de fond pour récupérer les notes
            Intent serviceIntent = new Intent(this, RequestDigitBooksRatingService.class);
            startService(serviceIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_RATING:
                if (resultCode == RESULT_OK) {
                    requestRateList();
                }
                break;
            default:
                break;
        }
    }

    private void showMessage(String message) {
        mLoading.setVisibility(View.GONE);
        mEmptyList.setVisibility(View.VISIBLE);
        mEmptyList.setText(message);
        getListView().setEmptyView(mEmptyList);
    }

    private void updateList() {
        LocalDigitbooksRatingDb dbHelper = new LocalDigitbooksRatingDb(getBaseContext());
        dbHelper.open();
        Cursor cursor = dbHelper.fetchAllRates();

        if (cursor != null && cursor.getCount() != 0) {
            dbHelper.close();
            mEmptyList.setVisibility(View.GONE);
            String[] from = new String[] {
                    "rating", "date", "comment"
            };
            int[] to = new int[] {
                    R.id.rating_bar, android.R.id.text1, android.R.id.text2
            };
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.rate_list_item, cursor, from, to);
            adapter.setViewBinder(this);
            setListAdapter(adapter);
            startManagingCursor(cursor);
        } else {
            showMessage(getString(R.string.empty_list));
        }
    }

    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
        final int viewId = view.getId();

        switch (viewId) {
            case R.id.rating_bar:
                float rating = cursor.getFloat(columnIndex);
                ((RatingBar) view).setRating(Float.valueOf(rating));
                return true;

            case android.R.id.text1:
                long timestamp = cursor.getLong(columnIndex);
                date.setTime(timestamp);
                ((TextView) view).setText(DateFormat.format(getString(R.string.date_format), date));
                return true;

            case android.R.id.text2:
                String comment = cursor.getString(columnIndex);
                ((TextView) view).setText(comment);
                return true;

            default:
                return false;
        }
    }

}
