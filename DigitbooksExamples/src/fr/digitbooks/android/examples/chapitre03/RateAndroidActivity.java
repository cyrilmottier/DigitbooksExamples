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
package fr.digitbooks.android.examples.chapitre03;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import fr.digitbooks.android.examples.R;

@SuppressWarnings("unused")
public class RateAndroidActivity extends Activity {

    private static final int LAYOUT_1 = R.layout.rate_android;
    private static final int LAYOUT_2 = R.layout.rate_android_bis;

    private static final int CURRENT_LAYOUT = LAYOUT_2;

    protected Button mButton;
    protected RatingBar mRatingBar;
    protected EditText mEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(CURRENT_LAYOUT);

        // Récupère des références sur les vues de notre layout
        mButton = (Button) findViewById(R.id.button);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mEditText = (EditText) findViewById(R.id.editText);

        // Ajoute un listener au bouton afin d'être notifié des éventuels clics
        // utilisateurs
        mButton.setOnClickListener(new ValidateHandler());
    }

    private class ValidateHandler implements OnClickListener {

        public void onClick(View v) {
            if (v == mButton) {
                // L'utilisateur vient de valider son commentaire. On affiche le
                // résultat à l'écran à l'aide d'un Toast
                final String toastText = getString(R.string.thank_you, mRatingBar.getRating(), mEditText.getText());

                Toast.makeText(RateAndroidActivity.this, toastText, Toast.LENGTH_LONG).show();

                // Et on réinitialise l'interface graphique pour une possible
                // future notation
                mEditText.setText(null);
                mRatingBar.setRating(0);
            }
        }

    }
}
