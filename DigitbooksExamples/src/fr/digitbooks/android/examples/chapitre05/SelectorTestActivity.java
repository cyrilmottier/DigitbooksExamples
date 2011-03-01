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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import fr.digitbooks.android.examples.R;

public class SelectorTestActivity extends Activity {

    private TextView mText;
    private Button mButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.selector_color);

        // Création du texte
        mText = (TextView) findViewById(R.id.text);

        // On créé un bouton qui changera l'état actif
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mText.setEnabled(!mText.isEnabled());
                updateTextButton();
            }
        });

        updateTextButton();
    }

    private void updateTextButton() {
        // Met à jour le texte du bouton en fonction de l'état du texte
        mButton.setText(mText.isEnabled() ? "activer texte" : "désactiver texte");
    }
}
