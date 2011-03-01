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

public class OtherListenerActivity extends Activity {

    private View mLinearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listener);

        mLinearLayout = findViewById(R.id.linearLayout);

        findViewById(R.id.button1).setOnClickListener(mClickHandler);
        findViewById(R.id.button2).setOnClickListener(mClickHandler);
        findViewById(R.id.button3).setOnClickListener(mClickHandler);
    }

    private OnClickListener mClickHandler = new OnClickListener() {

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.button1:
                    mLinearLayout.setBackgroundColor(Color.RED);
                    break;
                case R.id.button2:
                    mLinearLayout.setBackgroundColor(Color.GREEN);
                    break;
                case R.id.button3:
                    mLinearLayout.setBackgroundColor(Color.BLUE);
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
        
    };
}
