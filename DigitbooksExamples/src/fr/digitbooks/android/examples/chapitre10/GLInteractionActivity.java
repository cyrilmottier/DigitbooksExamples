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
package fr.digitbooks.android.examples.chapitre10;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import fr.digitbooks.android.examples.R;

public class GLInteractionActivity extends Activity {

    private GLSurfaceViewInteraction mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gl_interaction);

        mView = (GLSurfaceViewInteraction) findViewById(R.id.opengl_view);

        Button btnUp = (Button) findViewById(R.id.btn_up);
        btnUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mView.queueEvent(mUpRunnable);
            }
        });

        Button btnLeft = (Button) findViewById(R.id.btn_left);
        btnLeft.setOnTouchListener(new TouchHandler(mStartLeftRunnable, mStopLeftRunnable));

        Button btnRight = (Button) findViewById(R.id.btn_right);
        btnRight.setOnTouchListener(new TouchHandler(mStartRightRunnable, mStopRightRunnable));

        Button btnDown = (Button) findViewById(R.id.btn_down);
        btnDown.setOnTouchListener(new TouchHandler(mStartDownRunnable, mStopDownRunnable));
    }

    private class TouchHandler implements OnTouchListener {

        private Runnable mStartRunnable;
        private Runnable mStopRunnable;

        public TouchHandler(Runnable start, Runnable stop) {
            mStartRunnable = start;
            mStopRunnable = stop;
        }

        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();
            switch (action) {
                case KeyEvent.ACTION_DOWN:
                    mView.queueEvent(mStartRunnable);
                    break;
                case KeyEvent.ACTION_UP:
                    mView.queueEvent(mStopRunnable);
                    break;
            }
            return false;
        }

    }

    private final Runnable mUpRunnable = new Runnable() {
        public void run() {
            mView.yCamera -= 0.1f;
        }
    };

    private final Runnable mStartDownRunnable = new Runnable() {
        public void run() {
            mView.moveCameraDown(true);
        }
    };

    private final Runnable mStopDownRunnable = new Runnable() {
        public void run() {
            mView.moveCameraDown(false);
        }
    };

    private final Runnable mStartLeftRunnable = new Runnable() {
        public void run() {
            mView.moveCameraLeft(true);
        }
    };

    private final Runnable mStopLeftRunnable = new Runnable() {
        public void run() {
            mView.moveCameraLeft(false);
        }
    };

    private final Runnable mStartRightRunnable = new Runnable() {
        public void run() {
            mView.moveCameraRight(true);
        }
    };

    private final Runnable mStopRightRunnable = new Runnable() {
        public void run() {
            mView.moveCameraRight(false);
        }
    };

}
