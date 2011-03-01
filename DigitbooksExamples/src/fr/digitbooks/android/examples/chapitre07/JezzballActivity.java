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
package fr.digitbooks.android.examples.chapitre07;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import fr.digitbooks.android.examples.R;

public class JezzballActivity extends Activity {

    private static final int BUTTON_ALPHA = 255 >> 1;

    private BallsView mBallsView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.jezzball);

        mBallsView = (BallsView) findViewById(R.id.ballsView);
        mBallsView.start();

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.btn_container);
        final int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            viewGroup.getChildAt(i).getBackground().setAlpha(BUTTON_ALPHA);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * Ds que l'Activity devient visible, on redŽmarre l'animation des
         * balles
         */
        mBallsView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
         * Il est inutile d'effectuer les calculs d'animation lorsque
         * l'Activity n'est plus visible.
         */
        mBallsView.stop();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_plus:
                mBallsView.addBall();
                break;
            case R.id.btn_minus:
                mBallsView.removeBall();
                break;
            case R.id.btn_stop:
                mBallsView.stop();
                break;
            case R.id.btn_start:
                mBallsView.start();
                break;
        }
    }
}
