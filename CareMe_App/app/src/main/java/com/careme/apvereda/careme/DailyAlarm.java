package com.careme.apvereda.careme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Esta obra está sujeta a la licencia Reconocimiento-CompartirIgual 4.0 Internacional de
 * Creative Commons. Para ver una copia de esta licencia,
 * visite http://creativecommons.org/licenses/by-sa/4.0/.
 *
 * CareMe, creado por Alejandro Perez Vereda el 29/7/15.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0 International
 * License. To view a copy of this license,
 * visit http://creativecommons.org/licenses/by-sa/4.0/.
 *
 * CareMe, created by Alejandro Perez Vereda on 29/7/15.
 *
 * Contact: aperezvereda@gmail.com
 */

/**
 * Sends a notification to the user when an alarm is received
 */
public class DailyAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_notifications);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_notsmall).setLargeIcon(bitmap)
                .setContentTitle(context.getResources().getText(R.string.notifTittle))
                .setContentText(context.getResources().getText(R.string.notifBody))
                .setVibrate(new long[]{100, 250, 100, 500});
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(context, 0, resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 001;
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

}

