package com.fanap.podchat.notification;


import static com.fanap.podchat.util.ChatConstant.POD_CALL_INFO;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.fanap.podchat.R;
import com.fanap.podchat.call.audio_call.EndCallReceiver;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.util.Util;

public class CallNotificationHelper {


    public static final String CHANNEL_NAME = "POD_CHAT_CALL_CHANNEL";
    protected static final String DESCRIPTION = "Podchat channel for call";
    public static final int NOTIFICATION_ID = 7007;
    public static final int REQUEST_CODE_OPEN_APP = 7006;
    public static final int REQUEST_CODE_END_CALL = 7005;
    public static final String CHANNEL_ID = "PODCHAT_CALL";


    public static final String CHANNEL_DESCRIPTION = "FanapSoft Call notification channel";
    public static final String THREAD_ID = "threadId";
    public static final String MESSAGE_ID = "messageId";


    public static void setupNotificationChannel(Context context,
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

    public static void showRunningCallNotification(Service context, String targetActivity,
                                                   CallInfo callInfo, String CALL_CHANNEL_ID, int notificationId) {
        Intent closeAction = new Intent(context, EndCallReceiver.class);

        Intent openAppIntent = new Intent(context, context.getClass());

        if (!Util.isNullOrEmpty(targetActivity)) {
            try {
                Class<?> targetClass = Class.forName(targetActivity);
                openAppIntent = new Intent(context, targetClass);
            } catch (ClassNotFoundException e) {
                openAppIntent = new Intent(context, context.getClass());
            }
        }

        if (callInfo != null)
            openAppIntent.putExtra(POD_CALL_INFO, callInfo);

        PendingIntent pendingIntentOpenApp = PendingIntent.getActivity(context,
                REQUEST_CODE_OPEN_APP, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(context,
                REQUEST_CODE_END_CALL, closeAction, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action actionStop = new NotificationCompat.Action(android.R.drawable.ic_menu_close_clear_cancel,
                "پایان تماس", pendingIntentEnd);


        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_in_call_notifiacion);

        if (callInfo != null) {
            remoteViews.setTextViewText(R.id.textViewCallName, callInfo.getCallName());
        }

        remoteViews.setOnClickPendingIntent(R.id.buttonEndCall, pendingIntentEnd);


        Notification notification = new NotificationCompat
                .Builder(context, CALL_CHANNEL_ID)
                .setCustomContentView(remoteViews)
                .setSmallIcon(R.drawable.ic_call)
                .setContentIntent(pendingIntentOpenApp)
                .setOngoing(true)
                .build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForeground(notificationId, notification);
        } else {
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
            managerCompat.notify(notificationId, notification);
        }
    }

}
