package com.codecraft.busutm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Alif Darsim on 19/4/2018.
 */

public class AppsRunNotification {

    NotificationManager NotifyMgr;

    static void BackgroundRunningNotification(Context context) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "M_CH_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Bus UTM")
                .setContentText("Apps is running in background");
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.notify(1, notification);
    }

    static void cancelNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) notificationManager.cancel(1);
    }
}
