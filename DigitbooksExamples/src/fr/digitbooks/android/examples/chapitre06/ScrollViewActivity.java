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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import fr.digitbooks.android.examples.R;

public class ScrollViewActivity extends Activity {

    private static final int NUMBER_OF_ROWS = 100;
    private static final String FAKE_TITLE_PREFIX = "Ceci est le titre #";
    private static final String FAKE_SUBTITLE_PREFIX = "Sous-titre de la cellule #";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrollview);

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        final LayoutInflater inflater = getLayoutInflater();

        for (int i = 0; i < NUMBER_OF_ROWS; i++) {
            
            View row = (View) inflater.inflate(R.layout.subtitle_list_item, container, false);
            
            ((TextView) row.findViewById(R.id.subtitle_item_title)).setText(FAKE_TITLE_PREFIX + i);
            ((TextView) row.findViewById(R.id.subtitle_item_subtitle)).setText(FAKE_SUBTITLE_PREFIX + i);

            container.addView(row);
        }
    }
}
