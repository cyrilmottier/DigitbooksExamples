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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.util.Log;
import fr.digitbooks.android.examples.util.Config;

public class DigibooksRatingApi {

    private static final String LOG_TAG = DigibooksRatingApi.class.getSimpleName();

    private DigibooksRatingApi() {
    }

    public static String requestRatings(int count, String format) {
        String response = null;
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader bufferedReader = null;
        try {
            // Création d'une requête de type GET
            HttpGet httpGet = new HttpGet();
            URI uri = new URI(Config.URL_SERVER + "data?count=" + count + "&format=" + format);
            httpGet.setURI(uri);
            // Création d'une connexion
            AndroidHttpClient httpClient = AndroidHttpClient.newInstance("");
            HttpResponse httpResponse = httpClient.execute(httpGet);
            // Traitement de la réponse
            InputStream inputStream = httpResponse.getEntity().getContent();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1024);
            String readLine = bufferedReader.readLine();
            while (readLine != null) {
                stringBuffer.append(readLine);
                readLine = bufferedReader.readLine();
            }
            httpClient.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "IOException trying to execute request for " + e);
            return null;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    return null;
                }
            }
            response = stringBuffer.toString();
            if(Config.INFO_LOGS_ENABLED) {
            	Log.i(LOG_TAG, "Reponse = " + response);
            }	
        }

        return response;
    }

}
