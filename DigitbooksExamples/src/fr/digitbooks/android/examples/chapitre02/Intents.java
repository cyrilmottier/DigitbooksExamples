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
package fr.digitbooks.android.examples.chapitre02;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import fr.digitbooks.android.examples.DigitbooksExamples;
import fr.digitbooks.android.examples.R;
import fr.digitbooks.android.resources.ResourcesActivity;

public class Intents extends Activity implements OnClickListener {

    private static final int[] BUTTONS_IDS = {
            R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intents);

        for (int i = 0; i < BUTTONS_IDS.length; i++) {
            findViewById(BUTTONS_IDS[i]).setOnClickListener(this);
        }
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button1:
                try {
                    final Intent intent1 = new Intent();
                    intent1.setComponent(new ComponentName("com.android.calculator2", "com.android.calculator2.Calculator"));
                    startActivity(intent1);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(this, getString(R.string.calculator_not_found), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button2:
                startActivity(new Intent(this, ResourcesActivity.class));
                break;

            case R.id.button3:
                final Intent intent3 = new Intent();
                intent3.setClass(this, ResourcesActivity.class);
                startActivity(intent3);
                break;

            case R.id.button4:
                final Intent intent4 = new Intent(Intent.ACTION_CALL_BUTTON);
                startActivity(intent4);
                break;

            case R.id.button5:
                final Intent intent5 = new Intent(Intent.ACTION_DIAL);
                intent5.setData(Uri.parse("tel://+33600000000"));
                startActivity(intent5);
                break;

            case R.id.button6:
                final Intent intent6 = new Intent(this, DigitbooksExamples.class);
                intent6.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent6);
                break;

            case R.id.button7:
                final Intent intent7 = new Intent(Intent.ACTION_VIEW);
                intent7.putExtra("address", "+33600000000");
                intent7.putExtra("exit_on_sent", true);
                intent7.putExtra("sms_body", "bonjour andy");
                intent7.setType("vnd.android-dir/mms-sms");
                startActivity(intent7);
                break;

            case R.id.button8:
                try {
                    final Intent intent8 = new Intent(Intent.ACTION_VIEW);
                    intent8.setData(Uri.parse("market://search?q=android"));
                    startActivity(intent8);
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(this, getString(R.string.androidmarket_not_found), Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.button9:
                final Intent intent9 = new Intent(AnActivity.ACTION_AN_ACTIVITY);
                startActivity(intent9);
                break;

        }
    }
}
