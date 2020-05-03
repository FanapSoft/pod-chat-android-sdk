package com.fanap.podchat.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fanap.podchat.R;

import java.util.Random;

public class ShowNotificationHelper {

    private static final int REQUEST_CODE = 1;
    private static final String CHANNEL_ID = "PODCHAT";
    private static final String CHANNEL_NAME = "POD_CHAT_CHANNEL";
    private static final String CHANNEL_DESCRIPTION = "Fanap soft podchat notification channel";


    public static void setupNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            channel.setDescription(CHANNEL_DESCRIPTION);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

        }


    }

    static void setupNotificationChannel(Context context,
                                         String channelId,
                                         String channelName,
                                         String channelDescription,
                                         Integer notificationImportance
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String id = channelId != null ? channelId : CHANNEL_ID;
            String name = channelName != null ? channelName : CHANNEL_NAME;
            String descriptionText = channelDescription != null ? channelDescription : CHANNEL_DESCRIPTION;
            int importance = notificationImportance != null ? notificationImportance : NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(id, name, importance);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            channel.setDescription(descriptionText);

            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
            else Log.e("CHAT_SDK_NOTIFICATION", "Can't create notification manager instance");

        }


    }

    public static void showNotification(String title,
                                        String text,
                                        Context context,
                                        Class targetClass,
                                        Integer priority,
                                        Integer smallIcon) {


        Intent intent = new Intent(context.getApplicationContext(), targetClass);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context.getApplicationContext(),
                        REQUEST_CODE,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(
                context.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(smallIcon != null ? smallIcon : R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(title != null ? title : CHANNEL_NAME)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setPriority(priority != null ? priority : NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (notificationManager != null) {
            notificationManager.notify(new Random().nextInt(), notification);
        } else Log.e("CHAT_SDK_NOTIFICATION", "Can't create notification manager instance");


    }


}

