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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import fr.digitbooks.android.examples.R;

public class CatchMeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.imageview);

        final ImageView image = (ImageView) findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_launcher);
        image.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Toast.makeText(CatchMeActivity.this, "Touché", Toast.LENGTH_SHORT).show();
            }
        });

        Animation animation = (Animation) AnimationUtils.loadAnimation(this, R.anim.catchme);

        animation.setAnimationListener(new AnimationListener() {

            public void onAnimationEnd(Animation animation) {
                image.startAnimation(animation);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        image.startAnimation(animation);

    }
}
