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
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import fr.digitbooks.android.examples.R;
import fr.digitbooks.android.examples.util.Helpers;

public class RateListActivity extends ListActivity {
    private static final String LOG_TAG = RateListActivity.class.getSimpleName();

    protected static final int FIRST_MENU_ID = Menu.FIRST;

    // activity requestCode
    final static int ADD_RATING = 1;

    // Widget
    private LinearLayout mLoading;
    private TextView mEmptyList;

    // on déclare un champs date pour éviter la création pour chaque ligne de la
    // liste
    private Date mDate;

    // code pour la déclaration et le lancement de l'animation de progression du
    // chargement
    private AnimationDrawable mAnimation;

    // champ pour le lancement d'une tâche de fond
    private AsyncTask<Void, Void, List<HashMap<String, String>>> mTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDate = new Date();

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

        requestRateList();
    }

    @Override
    protected void onPause() {
        // isPaused = true;
        // Annuler la tâche en cours d'excution quand l'activité est interrompue

        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
        // BenchMarkActivity.IS_TESTED = false;
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
        if (!Helpers.isNetworkAvailable(this)) {
            showMessage(getString(R.string.no_network));
        } else {
            mEmptyList.setVisibility(View.GONE);
            setListAdapter(null);
            // On va démarrer le chargement par une barre de chargement
            getListView().setEmptyView(mLoading);
            // On lance une tâche de fond pour récupérer les notes
            mTask = new RateListTask().execute();
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

    private class RateListTask extends AsyncTask<Void, Void, List<HashMap<String, String>>> implements ViewBinder {

        private static final int RESPONSE_COUNT = 10;// "";//"?count=1";//
        private static final String RESPONSE_FORMAT = "json";// "xml";//
        private List<HashMap<String, String>> mData;

        @Override
        protected List<HashMap<String, String>> doInBackground(Void... params) {

            // Récupérer la liste des N dernieres note dans un format
            String response = DigibooksRatingApi.requestRatings(RESPONSE_COUNT, RESPONSE_FORMAT);
            GuestbookHandler handler = null;

            // Décomposition du résultat
            if (RESPONSE_FORMAT.equalsIgnoreCase("json")) {
                handler = new DigitbooksGuestbookJson();
            } else if (RESPONSE_FORMAT.equalsIgnoreCase("xml")) {
                handler = new DigitbooksGuestbookXml();
            }

            if (handler != null) {
                try {
                    mData = handler.parse(response);
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error while trying to parse string " + response, e);
                }
            }
            
            return mData;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            if (result == null) {
                showMessage(getString(R.string.error_list));
            } else {
                if (result.size() == 0) {
                    showMessage(getString(R.string.empty_list));
                } else {
                    mEmptyList.setVisibility(View.GONE);
                    String[] from = new String[] {
                            "rating", "date", "content"
                    };
                    int[] to = new int[] {
                            R.id.rating_bar, android.R.id.text1, android.R.id.text2
                    };
                    UnselectableSimpleAdapter adapter = new UnselectableSimpleAdapter(RateListActivity.this, result,
                            R.layout.rate_list_item, from, to);
                    adapter.setViewBinder(this);
                    setListAdapter(adapter);
                }
            }
        }

        public boolean setViewValue(View view, Object data, String textRepresentation) {

            final int viewId = view.getId();

            switch (viewId) {
                case R.id.rating_bar:
                    ((RatingBar) view).setRating(Float.valueOf(textRepresentation));
                    return true;

                case android.R.id.text1:
                    long timestamp = Long.parseLong(textRepresentation);
                    mDate.setTime(timestamp);
                    ((TextView) view).setText(DateFormat.format(getString(R.string.date_format), mDate));
                    return true;

                case android.R.id.text2:
                    ((TextView) view).setText(textRepresentation);
                    return true;

                default:
                    return false;
            }
        }

    }
}
