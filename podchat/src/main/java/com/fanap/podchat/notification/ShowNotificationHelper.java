package com.fanap.podchat.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.fanap.podchat.R;
import com.fanap.podchat.util.Util;

import java.util.Date;
import java.util.Random;

public class ShowNotificationHelper {

    public static void setupNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String name = "CHANNEL_NAME";
            String descriptionText = "Constant.CHANNEL_DESCRIPTION";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Channel Id",
                    name, importance);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            channel.setDescription(descriptionText);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }


    }
    public static void showNotification(String text, Context context, Class classT) {


        Intent intent = new Intent(context.getApplicationContext(), classT);

//                apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(),
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

//        val pendingIntent =
//                PendingIntent.getActivity(
//                        MyApplication.context !!, 1, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )


        Notification notif = new
                NotificationCompat.Builder(
                context.getApplicationContext(),
                "CHANNEL_ID")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("CHANNEL_NAME")
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(new Random().nextInt(), notif);
        }


    }

