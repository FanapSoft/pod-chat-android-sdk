package com.fanap.podchat.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.fanap.podchat.R;
import com.fanap.podchat.util.Util;
import com.securepreferences.SecurePreferences;

import java.util.Random;

import static com.fanap.podchat.notification.PodChatPushNotificationService.*;

public class ShowNotificationHelper {

    private static final int REQUEST_CODE = 7070;
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


    static void showNotification(String title,
                                 String text,
                                 Context context,
                                 String targetClassName,
                                 Integer priority,
                                 Integer smallIcon) {


        try {

            Class<?> c = Class.forName(targetClassName);
            Intent intent = new Intent(context, c);
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
            } else Log.e(TAG, "Can't create notification manager instance");


        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Couldn't start activity: " + e.getMessage() + " " + targetClassName);
        }

    }


    static void showNewMessageNotification(
            String threadName,
            String senderName,
            String profileUrl,
            String content,
            Context context,
            String targetClassName,
            Integer priority,
            Integer smallIcon,
            String channelId) {


        try {


            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.message_layout);

            RemoteViews viewExpanded = new RemoteViews(context.getPackageName(), R.layout.message_layout_expanded);

            view.setTextViewText(R.id.textViewSenderName, threadName + " " + senderName);
            view.setTextViewText(R.id.textViewContent, content);

            viewExpanded.setTextViewText(R.id.textViewSenderName, threadName + " " + senderName);
            viewExpanded.setTextViewText(R.id.textViewContent, content);


            try {

                Bitmap bitmap = GlideApp.with(context)
                        .asBitmap()
                        .load(profileUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .submit(512, 512)
                        .get();

                view.setImageViewBitmap(R.id.imageViewProfilePicture, bitmap);
                viewExpanded.setImageViewBitmap(R.id.imageViewProfilePicture, bitmap);


            } catch (Exception e) {
                e.printStackTrace();
            }


            Class<?> c = Class.forName(targetClassName);
            Intent intent = new Intent(context, c);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context.getApplicationContext(),
                            REQUEST_CODE,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);


            Notification notification = new NotificationCompat.Builder(
                    context.getApplicationContext(), !Util.isNullOrEmpty(channelId) ?
                    channelId : CHANNEL_ID)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setContent(view)
                    .setCustomContentView(view)
                    .setCustomBigContentView(viewExpanded)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message)
                    .setPriority(priority != null ? priority : NotificationCompat.PRIORITY_DEFAULT)
                    .build();


            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.notify(new Random().nextInt(), notification);
            } else Log.e(TAG, "Can't create notification manager instance");


        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Couldn't start activity: " + e.getMessage() + " " + targetClassName);
        }

    }


}

