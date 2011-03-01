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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import fr.digitbooks.android.examples.R;

public class AsynchronousTaskActivity extends Activity {

    private static final int ASYNC_TASK_METHOD = 0x101;
    private static final int HANDLER_METHOD = 0x102;

    private static final int CURRENT_METHOD = HANDLER_METHOD;

    private static final int SLEEPING_TIME = 2000;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LongTaskStrategy strategy = null;

        switch (CURRENT_METHOD) {
            case HANDLER_METHOD:
                strategy = new HandlerStrategy();
                break;
                
            case ASYNC_TASK_METHOD:
                strategy = new AsyncTaskStrategy();
                break;

            default:
                break;
        }
        
        if (strategy != null) {
            setContentView(R.layout.asynchronous_task);
            strategy.executeTask();
        }
        
    }
    
    private static interface LongTaskStrategy {
        void executeTask();
    }
    
    private class AsyncTaskStrategy implements LongTaskStrategy {

        public void executeTask() {
            (new DumbTask()).execute((Void[]) null);
        }
        
        private class DumbTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... params) {
                executeLongTask();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                onLongTaskExecuted();
            }
        }
    }
    
    private class HandlerStrategy implements LongTaskStrategy {
        
        private static final int DUMP_MESSAGE = 0x1234;
        
        private DumbHandler mHandler;

        public void executeTask() {
            // On commence par créer un Handler. Un Handler exécute
            // l'ensemble des Runnables qu'on lui donne dans le Thread dans
            // lequel il a été créé (ici le main thread)
            mHandler = new DumbHandler();
            new Thread(new Runnable() {
                public void run() {
                    executeLongTask();
                    final Message msg = new Message();
                    msg.what = DUMP_MESSAGE;
                    mHandler.sendMessage(msg);
                }
            }).start();            
        }
        
        private class DumbHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DUMP_MESSAGE:
                        onLongTaskExecuted();
                        break;

                    default:
                        // Do nothing
                        break;
                }
            }
        }
        
    }

    private void executeLongTask() {
        try {
            // C'est ici que la "longue" tâche (accès réseau, accès disque,
            // etc.) s'effectue. Pour la démonstration, nous effectuons
            // simplement une attente
            Thread.sleep(SLEEPING_TIME);
        } catch (InterruptedException e) {
        }
    }

    private void onLongTaskExecuted() {
        // La tâche "longue" a été exécutée. Il est maintenant possible de
        // supprimer le logo de chargement et de remplacer l'indicateur par un
        // résultat.
        findViewById(R.id.progressContainer).setVisibility(View.GONE);
        findViewById(R.id.resultContainer).setVisibility(View.VISIBLE);
    }

}
