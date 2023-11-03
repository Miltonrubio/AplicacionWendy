package com.example.aplicacionwendy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Random;

public class Utils {
    public static final String TAG = "ALPIX";

    public static void showNotification(Context context, String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "com.example.channel");

        builder.setSmallIcon(R.drawable.logo); // Reemplaza "ic_launcher" con el nombre de tu ícono de la aplicación
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "com.example.channel";
            NotificationChannel channel = new NotificationChannel(channelId, "Wendy Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        manager.notify(new Random().nextInt(), builder.build());
    }



}
