package com.gmail.btheo95.aria.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.gmail.btheo95.aria.R;
import com.gmail.btheo95.aria.activity.MainActivity;

/**
 * Created by btheo on 13.04.2017.
 */

public class Notifications {

    public static void showNotificationNoWifi(Context context, int id) {
        String title = context.getString(R.string.notification_no_wifi_title);
        String text = context.getString(R.string.notification_no_wifi_content);
        showWarningNotification(context, title, text, id);
    }

    public static void showNotificationServerNotReacheble(Context context, int id) {
        String title = context.getString(R.string.notification_server_not_reacheble_title);
        String text = context.getString(R.string.notification_server_not_reacheble_content);
        showWarningNotification(context, title, text, id);
    }

    public static void showWarningNotification(Context context, String title, String text, int id) {
        android.support.v4.app.NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_error_outline_black_24dp);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification.build());
    }

    public static void showNoServerFoundNotification(Context context, int id) {
        android.support.v4.app.NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_error_outline_black_24dp)
                .setContentTitle(context.getString(R.string.notification_nonexistent_server_title))
                .setContentText(context.getString(R.string.notification_nonexistent_server_content))
                .setContentIntent(getMainActivityPendingIntent(context, R.id.nav_servers))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification.build());
    }

    private static PendingIntent getMainActivityPendingIntent(Context context, Integer navItemId) {

        Intent resultIntent = new Intent(context, MainActivity.class);

        if (navItemId != null) {
            resultIntent.putExtra(MainActivity.KEY_DEFAULT_NAV_ITEM, navItemId);
        }

        return PendingIntent.getActivity(
                context,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    public static void showNotificationUploadingCustomFile(Context context, int id) {
        android.support.v4.app.NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_uploading_custom_file_title))
                .setContentText(context.getString(R.string.notification_uploading_custom_file_content))
                .setProgress(1, 0, true)
                .setSmallIcon(R.drawable.ic_cloud_upload_black_24dp);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification.build());
    }

    public static void showNotificationUploadingCustomFileFinished(Context context, int id) {
        android.support.v4.app.NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_uploading_custom_file_finished_title))
                .setContentText(context.getString(R.string.notification_uploading_custom_file_finished_content))
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_cloud_done_black_24dp);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification.build());
    }

    public static void showNotificationUploadingCustomFileFailed(Context context, int id) {
        String title = context.getString(R.string.notification_uploading_custom_file_failed_title);
        String text = context.getString(R.string.notification_uploading_custom_file_failed_content);
        showWarningNotification(context, title, text, id);
    }
}
