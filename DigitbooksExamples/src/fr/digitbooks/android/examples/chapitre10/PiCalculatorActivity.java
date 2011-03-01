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

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import fr.digitbooks.android.examples.R;
import fr.digitbooks.android.examples.util.Config;

public class PiCalculatorActivity extends Activity implements PiCalculatingListener {

    private static final String LOG_TAG = PiCalculatorActivity.class.getSimpleName();

    private AsyncTask<Integer, Void, String> task = null;

    Button mLaunchButton;
    ProgressBar mProgress;
    EditText editText;
    RadioGroup mRadioGroup;

    private final static int ALGORITHME_LIMIT = 15000;

    long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pi_calculator_choice);

        mLaunchButton = (Button) findViewById(R.id.button);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        editText = (EditText) findViewById(R.id.number_limit);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);

        mLaunchButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (Config.INFO_LOGS_ENABLED) {
                    Log.i(LOG_TAG, "onClick");
                }
                if (v.getId() == R.id.button) {
                    int limit = 0;
                    if (editText.getText().length() != 0)
                        limit = Integer.parseInt(editText.getText().toString());
                    if (limit > 0 && limit <= ALGORITHME_LIMIT) {
                        final RadioButton javaModeChecked = (RadioButton) findViewById(R.id.mode_java);
                        if (javaModeChecked.isChecked()) {
                            if (Config.INFO_LOGS_ENABLED) {
                                Log.i(LOG_TAG, "Start Java method ");
                            }
                            if (task == null)
                                task = new PiCalculatingJavaTask(PiCalculatorActivity.this).execute(limit);
                        } else {
                            if (Config.INFO_LOGS_ENABLED) {
                                Log.i(LOG_TAG, "Start Ndk method ");
                            }
                            if (task == null)
                                task = new PiCalculatingNdkTask(PiCalculatorActivity.this).execute(limit);
                        }
                    } else {
                        Toast.makeText(PiCalculatorActivity.this, R.string.bad_limit, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    @Override
    protected void onPause() {
        if (task != null)
            task.cancel(true);
        super.onPause();
    }

    public void onCalculatingStart() {
        mLaunchButton.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
        editText.setEnabled(false);
        mRadioGroup.setEnabled(false);
        time = System.currentTimeMillis();
    }

    public void onCalculatingEnd(String result) {
        time = System.currentTimeMillis() - time; // time c'est le nombre de
                                                  // millisecond pris par le
                                                  // calcul
        mLaunchButton.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.INVISIBLE);
        editText.setEnabled(true);
        mRadioGroup.setEnabled(true);
        task = null;

        if (result != null) {
            Date date = new Date();
            date.setTime(time);
            int minute = Integer.parseInt(DateFormat.format("m", date).toString());
            int seconde = Integer.parseInt(DateFormat.format("s", date).toString());
            int totalSeconde = minute * 60 + seconde;
            int millisecond = (int) time - totalSeconde * 1000;
            Intent i = new Intent(this, PiResultActivity.class);
            String formatedTime = totalSeconde + "s " + millisecond + "ms";

            if (Config.INFO_LOGS_ENABLED) {
                Log.i(LOG_TAG, "total time " + formatedTime);
            }
            i.putExtra("time", formatedTime);
            i.putExtra("result", result);
            startActivity(i);

        } else {
            Toast.makeText(PiCalculatorActivity.this, R.string.pi_error, Toast.LENGTH_LONG).show();
        }
    }
}
