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
package fr.digitbooks.android.examples.util;

@SuppressWarnings("all")
public class Config {

    private Config() {
    }

    /**
     * Définit si l'exemple du chapitre 8 utilise la sauvegarde distante des
     * donnée ou une sauvegarde locale
     */
    public static final boolean USE_APPLICATIONS_PREFERENCES = true;

    public final static String URL_SERVER_DEBUG = "http://10.0.2.2:8888/";
    public final static String URL_SERVER_PROD = "http://digitbooks.diotasoft.com/";

    /**
     * Adresse du serveur utilisé pour l'exemple du chapitre 8. L'adresse IP
     * 10.0.2.2 référence la machine faisant tourner l'émulateur. Utiliser
     * localhost ne fonctionne pas car cette adresse référence un serveur
     * tournant sur l'émulateur lui-même.
     */
    public final static String URL_SERVER = URL_SERVER_PROD;

    public static final String PREFERENCES = "fr.digibooks.android.preferences";
    public static final String PREFERENCE_RATE_SENT = "fr.digibooks.android.rating.sent";
    public static final String PREFERENCE_COMMENT = "fr.digibooks.android.comment";
    public static final String PREFERENCE_RATING = "fr.digibooks.android.rating";

    /**
     * DEBUG SETTINGS
     */

    private static final int LOG_LEVEL_INFO = 3;
    private static final int LOG_LEVEL_WARNING = 2;
    private static final int LOG_LEVEL_ERROR = 1;
    private static final int LOG_LEVEL_NONE = 0;

    /**
     * Mettre ce flag à LOG_LEVEL_NONE lorsque l'application est compilée pour
     * la production. Cela supprimer l'ensemble des logs du projets.
     */
    private static final int LOG_LEVEL = LOG_LEVEL_NONE;

    public static final boolean INFO_LOGS_ENABLED = (LOG_LEVEL == LOG_LEVEL_INFO);
    public static final boolean WARNING_LOGS_ENABLED = INFO_LOGS_ENABLED || (LOG_LEVEL == LOG_LEVEL_WARNING);
    public static final boolean ERROR_LOGS_ENABLED = (LOG_LEVEL == LOG_LEVEL_ERROR) || WARNING_LOGS_ENABLED;

}
