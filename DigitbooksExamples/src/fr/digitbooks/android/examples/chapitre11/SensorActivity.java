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
package fr.digitbooks.android.examples.chapitre11;

import java.util.List;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import fr.digitbooks.android.examples.R;
import fr.digitbooks.android.examples.util.Config;

public class SensorActivity extends Activity implements SensorEventListener {

    private static final String LOG_TAG = SensorActivity.class.getSimpleName();

    private SensorManager mSensorManager;

    private BallView mBallsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sensor);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mBallsView = (BallView) findViewById(R.id.ballView);
        mBallsView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        List<Sensor> sensor = mSensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensor.size() > 0) {
            if (Config.INFO_LOGS_ENABLED) {
                // sensor info
                Log.i(LOG_TAG, "Register orientation sensor");
                Log.i(LOG_TAG, "Name " + sensor.get(0).getName());
                Log.i(LOG_TAG, "Max Range " + sensor.get(0).getMaximumRange());
                Log.i(LOG_TAG, "Power " + sensor.get(0).getPower());
                Log.i(LOG_TAG, "Resolution " + sensor.get(0).getResolution());
                Log.i(LOG_TAG, "Type " + sensor.get(0).getType());
                Log.i(LOG_TAG, "Vendor " + sensor.get(0).getVendor());
                Log.i(LOG_TAG, "Version " + sensor.get(0).getVersion());
            }
            mSensorManager.registerListener(this, sensor.get(0), SensorManager.SENSOR_DELAY_GAME);
        } else {
            Log.i(LOG_TAG, "No orientation sensor");
        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {

            float a = event.values[1];
            float b = event.values[2];

            mBallsView.setVelocities(-b / 5, -a / 5);

        }
    }

}
