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
package fr.digitbooks.android.examples.chapitre06;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;

public class StrictModeActivity extends Activity {
    
    private static final boolean DEVELOPER_MODE_ENABLED = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEVELOPER_MODE_ENABLED) {
            /*
             * On active ici l'ensemble des détections possibles et on affiche
             * les violations sur les logs standards Android.
             */
            StrictMode.setThreadPolicy(
                    new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
        super.onCreate(savedInstanceState);
    }
    
    @Override
    protected void onDestroy() {
        if (DEVELOPER_MODE_ENABLED) {
            super.onDestroy();
            /*
             * Désactive le StrictMode lorsque cette activité est détruite
             */
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.LAX);
            StrictMode.setVmPolicy(StrictMode.VmPolicy.LAX);
        }
    }

}
