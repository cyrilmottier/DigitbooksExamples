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
package fr.digitbooks.android.examples.chapitre06;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.digitbooks.android.examples.R;

public class CorrectListActivity extends Activity {

    /*
     * Il est maintenant possible de mettre cette variable a une forte valeur
     */
    private static final int NUMBER_OF_ROWS = 100;
    private static final String FAKE_TITLE_PREFIX = "Ceci est le titre #";
    private static final String FAKE_SUBTITLE_PREFIX = "Sous-titre de la cellule #";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(new CorrectAdapter());
    }

    private class CorrectAdapter extends BaseAdapter {

        public int getCount() {
            return NUMBER_OF_ROWS;
        }

        public Object getItem(int position) {
            // Méthode non utilisée
            return null;
        }

        public long getItemId(int position) {
            // Méthode non utilisée
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            
            /*
             * On utilise maintenant la "convertView". Cette vue est nulle dans
             * le cas où la ListView n'a aucune vue à réutiliser (au premier
             * affichage) et non nulle sinon
             */
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.subtitle_list_item, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.subtitle_item_title)).setText(FAKE_TITLE_PREFIX + position);
            ((TextView) convertView.findViewById(R.id.subtitle_item_subtitle)).setText(FAKE_SUBTITLE_PREFIX + position);

            return convertView;
        }

    }
}
