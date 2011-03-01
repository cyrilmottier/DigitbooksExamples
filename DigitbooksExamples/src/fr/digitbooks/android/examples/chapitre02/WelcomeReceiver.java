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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WelcomeReceiver extends BroadcastReceiver {

    private static int NOTIFICATION_ID = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(android.R.drawable.stat_notify_error, "Welcome",
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this
        // notification
        PendingIntent contentIntent = PendingIntent
                .getActivity(context, 0, new Intent(context, ClockActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(context, "ClockActivity", "Les exemples Digit books sont installés", contentIntent);

        // Send the notification.
        // We use a string id because it is a unique number. We use it later to
        // cancel.
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

}
