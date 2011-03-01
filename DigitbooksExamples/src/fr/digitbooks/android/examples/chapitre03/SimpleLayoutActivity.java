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
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import fr.digitbooks.android.examples.R;

@SuppressWarnings("all")
public class SimpleLayoutActivity extends Activity {

    private static final int JAVA_METHOD = 0;
    private static final int XML_METHOD = 1;

    private static final int METHOD = JAVA_METHOD;

    /**
     * Méthode appelée lorsque que l'activité vient d'être créée pour la première
     * fois
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (METHOD == JAVA_METHOD) {

            TextView textView = new TextView(this);
            LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
            textView.setLayoutParams(layoutParams);
            textView.setGravity(Gravity.CENTER);
            textView.setText("Bonjour Android !");

            setContentView(textView);

        } else {
            /**
             * Récupère le contenu du fichier res/layout/main.xml, l'interprète
             * et l'affiche à l'écran
             */
            setContentView(R.layout.simple_layout);
        }
    }
}
