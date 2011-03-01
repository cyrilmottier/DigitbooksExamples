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
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import fr.digitbooks.android.examples.util.Config;

public class RequestDigitBooksRatingService extends IntentService {

    private static final String LOG_TAG = RequestDigitBooksRatingService.class.getSimpleName();

    private static final int RESPONSE_COUNT = 10;
    private static final String RESPONSE_FORMAT = "json";

    public RequestDigitBooksRatingService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    	//TODO initialiser les variable RESPONSE_COUNT et RESPONSE_FORMAT depuis des extra

        /*
         * La première étape consiste à récupérer les RESPONSE_COUNT dernières
         * notes. Le format de retour utilisé est donné par RESPONSE_FORMAT
         */
        String response = DigibooksRatingApi.requestRatings(RESPONSE_COUNT, RESPONSE_FORMAT);
        GuestbookHandler handler = null;

        // Décomposition du résultat
        if (RESPONSE_FORMAT.equalsIgnoreCase("json")) {
            handler = new DigitbooksGuestbookJson();
        } else if (RESPONSE_FORMAT.equalsIgnoreCase("xml")) {
            handler = new DigitbooksGuestbookXml();
        } else {
            Log.e(LOG_TAG, "response format unknown");
        }

        List<HashMap<String, String>> data = null;
        if (handler != null) {
            try {
                data = handler.parse(response);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error while parsing response", e);
            }
        }

        if (data != null) {
            // Stocker les données en local
            LocalDigitbooksRatingDb dbHelper = new LocalDigitbooksRatingDb(this);
            dbHelper.open();
            // On commence par tout effacer pour créer les doublons
            dbHelper.dropAllRates();
            for (HashMap<String, String> e : data) {
                final float rating = Float.parseFloat(e.get("rating"));
                final String comment = e.get("content");
                final long date = Long.parseLong(e.get("date"));
                dbHelper.createRate(rating, comment, date);
            }
            dbHelper.close();
        }

        final Intent send = new Intent(RateListActivityOffline.ACTION_REFRESH);
        if (Config.INFO_LOGS_ENABLED) {
            Log.i(LOG_TAG, "Broadcast will be sent");
        }
        sendBroadcast(send);
        stopSelf();
    }

}
