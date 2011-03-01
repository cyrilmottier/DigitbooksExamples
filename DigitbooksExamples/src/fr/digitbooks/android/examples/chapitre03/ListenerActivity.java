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
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import fr.digitbooks.android.examples.R;

public class ListenerActivity extends Activity {

    private static final int[] DATA = {
            R.id.button1, Color.RED, R.id.button2, Color.GREEN, R.id.button3, Color.BLUE
    };

    private View mLinearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listener);
        mLinearLayout = findViewById(R.id.linearLayout);

        for (int i = 0; i < DATA.length; i += 2) {
            final int index = i;
            findViewById(DATA[index]).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    mLinearLayout.setBackgroundColor(DATA[index + 1]);
                }
            });
        }

    }
}
