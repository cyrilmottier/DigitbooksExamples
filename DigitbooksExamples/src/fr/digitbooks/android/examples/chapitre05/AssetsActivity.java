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
package fr.digitbooks.android.examples.chapitre05;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import fr.digitbooks.android.examples.R;

public class AssetsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.assets);

        try {
            InputStream is = getAssets().open("licence.txt");
            // Récupération de la taille de la ressource
            int size = is.available();

            // lecture de la ressource.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Conversion en String.
            String text = new String(buffer);

            // Affichage
            TextView tv = (TextView) findViewById(R.id.licence);
            tv.setText(text);
        } catch (IOException e) {
            // Erreur de lecture de la ressource
        }

    }
}
