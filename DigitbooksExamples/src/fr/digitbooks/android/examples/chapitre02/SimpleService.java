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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class SimpleService extends Service {

    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 1;

    private final Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        showNotification("Service démarré");
        mHandler.postDelayed(new Runnable() {

            public void run() {
                stopSelf();
            }
        }, 50000);
    }

    @Override
    public void onDestroy() {
        // Supprime la notification
        mNotificationManager.cancel(NOTIFICATION_ID);
        // Affichage un message à l'utilisateur
        Toast.makeText(this, "Service arrêté", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Show a notification.
     */
    private void showNotification(String text) {

        final int icon = android.R.drawable.stat_notify_error;
        Notification notification = new Notification(icon, text, System.currentTimeMillis());

        final Intent intent = new Intent(this, SimpleServiceActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notification.setLatestEventInfo(this, "Service simple", text, contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}
