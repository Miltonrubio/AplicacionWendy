package com.example.aplicacionwendy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class Utils {

    public static final String TAG = "El Token es: ";

    public static void showNotifications(Context context, String title, String body, PendingIntent pendingIntent, int notificationId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "com.example.aplicacionwendy");

        builder.setSmallIcon(R.drawable.baseline_notifications_active_24);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(body);  // Usar el texto del cuerpo (body) en lugar del título en el bigTextStyle
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.setSummaryText(title);

        builder.setStyle(bigTextStyle);

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "com.example.aplicacionwendy.id";
            NotificationChannel channel = new NotificationChannel(channelId, "Taller Georgio",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        manager.notify(notificationId, builder.build());
    }

}
