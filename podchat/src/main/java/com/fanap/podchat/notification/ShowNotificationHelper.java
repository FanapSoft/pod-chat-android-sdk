package com.fanap.podchat.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.request.RequestOptions;
import com.fanap.podchat.R;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.fanap.podchat.notification.PodChatPushNotificationService.TAG;
import static com.fanap.podchat.util.TextMessageType.Constants.FILE;
import static com.fanap.podchat.util.TextMessageType.Constants.LINK;
import static com.fanap.podchat.util.TextMessageType.Constants.PICTURE;
import static com.fanap.podchat.util.TextMessageType.Constants.POD_SPACE_FILE;
import static com.fanap.podchat.util.TextMessageType.Constants.POD_SPACE_PICTURE;
import static com.fanap.podchat.util.TextMessageType.Constants.POD_SPACE_SOUND;
import static com.fanap.podchat.util.TextMessageType.Constants.POD_SPACE_VIDEO;
import static com.fanap.podchat.util.TextMessageType.Constants.POD_SPACE_VOICE;
import static com.fanap.podchat.util.TextMessageType.Constants.SOUND;
import static com.fanap.podchat.util.TextMessageType.Constants.TEXT;
import static com.fanap.podchat.util.TextMessageType.Constants.VIDEO;
import static com.fanap.podchat.util.TextMessageType.Constants.VOICE;

public class ShowNotificationHelper {

    private static final int REQUEST_CODE = 7070;
    private static final String CHANNEL_ID = "PODCHAT";
    private static final String CHANNEL_NAME = "POD_CHAT_CHANNEL";
    private static final String CHANNEL_DESCRIPTION = "Fanap soft podchat notification channel";
    public static final String THREAD_ID = "threadId";
    public static final String MESSAGE_ID = "messageId";
    public static final String SENDER_USER_NAME = "senderUserName";

    public static final String ACTION_1 = "action_1";
    private static final int SUMMARY_ID = 0;
    private static final String TARGET_CLASS = "T_CLASS";


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
            String text,
            String isGroup,
            String messageSenderUserName,
            String messageId,
            String threadId,
            Context context,
            String targetClassName,
            Integer priority,
            Integer smallIcon,
            String channelId) {


        try {


            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.message_layout);

            RemoteViews viewExpanded = new RemoteViews(context.getPackageName(), R.layout.message_layout_expanded);


            String title = isGroup.equals("true") ? threadName + " - " + senderName : senderName;

            view.setTextViewText(R.id.textViewSenderName, title);
            view.setTextViewText(R.id.textViewContent, text);

            viewExpanded.setTextViewText(R.id.textViewSenderName, title);
            viewExpanded.setTextViewText(R.id.textViewContent, text);


            if (profileUrl != null) {

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

            }


            Class<?> c = Class.forName(targetClassName);
            Intent intent = new Intent(context, c);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra(THREAD_ID, threadId);
            intent.putExtra(MESSAGE_ID, messageId);
            intent.putExtra(SENDER_USER_NAME, messageSenderUserName);

            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context.getApplicationContext(),
                            REQUEST_CODE,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(
                    context.getApplicationContext(), !Util.isNullOrEmpty(channelId) ?
                    channelId : CHANNEL_ID)
                    .setAutoCancel(true)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setContent(view)
                    .setCustomContentView(view)
                    .setCustomBigContentView(viewExpanded)
                    .setContentIntent(pendingIntent)
                    .setGroup(threadId)
                    .setGroupSummary(true)
                    .setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message)
                    .setPriority(priority != null ? priority : NotificationCompat.PRIORITY_DEFAULT);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notifBuilder.setCategory(Notification.CATEGORY_MESSAGE);
            }


            Notification notification = notifBuilder.build();


            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {

//                int notificationId = Util.isNullOrEmpty(threadId) ? new Random().nextInt() : Integer.parseInt(threadId);
                int notificationId = new Random().nextInt();

                notificationManager.notify(notificationId, notification);

            } else Log.e(TAG, "Can't create notification manager instance");


        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Couldn't start activity: " + e.getMessage() + " " + targetClassName);
        }

    }


    static void showGroupNewMessageNotification(
            ArrayList<Map<String, String>> notifications,
            Context context,
            String targetClassName,
            Integer priority,
            Integer smallIcon,
            String channelId) {


        try {


            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


            Map<String, Notification> notificationMap = new HashMap<>();

            NotificationCompat.InboxStyle inbox = new NotificationCompat.InboxStyle();

            NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(!Util.isNullOrEmpty(channelId) ?
                    channelId : CHANNEL_ID);


            String group = "DEFAULT_GROUP";


            for (Map<String, String> notificationData :
                    notifications) {


                String threadName = notificationData.get("threadName");
                String senderName = notificationData.get("MessageSenderName");
                String profileUrl = notificationData.get("senderImage");


                String text = getNotificationText(notificationData.get("text"), notificationData.get("messageType"));
                String isGroup = notificationData.get("isGroup") != null ? notificationData.get("isGroup") : "false";
                String messageSenderUserName = notificationData.get("MessageSenderUserName");
                String messageId = notificationData.get("messageId");
                String threadId = notificationData.get("threadId");

//                group = threadId;

                RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.message_layout);

                RemoteViews viewExpanded = new RemoteViews(context.getPackageName(), R.layout.message_layout_expanded);

                String title = (isGroup != null && isGroup.equals("true")) ? threadName + " - " + senderName : senderName;

                view.setTextViewText(R.id.textViewSenderName, title);
                view.setTextViewText(R.id.textViewContent, text);

                viewExpanded.setTextViewText(R.id.textViewSenderName, title);
                viewExpanded.setTextViewText(R.id.textViewContent, text);


                if (profileUrl != null) {

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

                }

                Intent action1Intent = new Intent();
                action1Intent.putExtra(THREAD_ID, threadId);
                action1Intent.putExtra(MESSAGE_ID, messageId);
                action1Intent.putExtra(SENDER_USER_NAME, messageSenderUserName);
                action1Intent.putExtra(TARGET_CLASS, targetClassName);
                action1Intent.setAction(ACTION_1);

                PendingIntent action1PendingIntent = PendingIntent
                        .getBroadcast(context.getApplicationContext(),
                                Util.isNullOrEmpty(messageId) ? REQUEST_CODE : Integer.parseInt(messageId),
                                action1Intent,
                                PendingIntent.FLAG_CANCEL_CURRENT);


//                PendingIntent pendingIntent = PendingIntent
//                        .getActivity(context.getApplicationContext(),
//                                Util.isNullOrEmpty(messageId) ? REQUEST_CODE : Integer.parseInt(messageId),
//                                intent,
//                                PendingIntent.FLAG_CANCEL_CURRENT);


                NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(
                        context.getApplicationContext(), !Util.isNullOrEmpty(channelId) ?
                        channelId : CHANNEL_ID)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setContent(view)
                        .setCustomContentView(view)
                        .setCustomBigContentView(viewExpanded)
                        .setContentIntent(action1PendingIntent)
                        .setGroup(group)
                        .setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message)
                        .setPriority(priority != null ? priority : NotificationCompat.PRIORITY_DEFAULT);

                Notification notification = notifBuilder.build();

                notificationMap.put(messageId, notification);

                inbox.addLine(title + " " + text);

                messagingStyle.addMessage(text, new Date().getTime(), senderName);

            }


            inbox.setBigContentTitle(!Util.isNullOrEmpty(channelId) ? channelId : CHANNEL_ID);

            inbox.setSummaryText("+" + notificationMap.size() + " پیام جدید");

            Notification summaryBuilder = new NotificationCompat.Builder(
                    context.getApplicationContext(),
                    !Util.isNullOrEmpty(channelId) ? channelId : CHANNEL_ID)
                    .setContentTitle(notificationMap.size() + "+ پیام جدید دارید")
                    .setContentText("شما +" + notificationMap.size() + " پیام خوانده نشده دارید")
                    .setStyle(inbox)
                    .setAutoCancel(true)
                    .setGroup(group)
                    .setGroupSummary(true)
                    .setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message)
                    .setPriority(priority != null ? priority : NotificationCompat.PRIORITY_DEFAULT)
                    .build();

//            Notification messagesNotif = new NotificationCompat.Builder(
//                    context.getApplicationContext(),
//                    !Util.isNullOrEmpty(channelId) ? channelId : CHANNEL_ID)
//                    .setContentTitle(notificationMap.size() + "+ پیام جدید دارید")
//                    .setContentText("شما +" + notificationMap.size() + " پیام خوانده نشده دارید")
//                    .setStyle(messagingStyle)
//                    .setAutoCancel(true)
//                    .setGroup(group)
//                    .setGroupSummary(true)
//                    .setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message)
//                    .setPriority(priority != null ? priority : NotificationCompat.PRIORITY_DEFAULT)
//                    .build();


            if (notificationManager != null) {

                for (String messageId :
                        notificationMap.keySet()) {

                    int notificationId = 0;
                    try {
                        notificationId = Integer.parseInt(messageId);
                    } catch (NumberFormatException e) {
                        notificationId = new Random().nextInt();
                    }

                    notificationManager.notify(notificationId, notificationMap.get(messageId));

                }

                if (notificationMap.size() > 5) {

                    notificationManager.notify(SUMMARY_ID, summaryBuilder);

                }

            } else Log.e(TAG, "Can't create notification manager instance");


        } catch (Exception e) {
            Log.e(TAG, "Couldn't start activity: " + e.getMessage() + " " + targetClassName);
        }

    }

    private static String getNotificationText(String text, String messageType) {

        try {

            if (Util.isNullOrEmpty(messageType) || messageType.equals("null"))
                return text;

            int textMessageType = Integer.parseInt(messageType);

            switch (textMessageType) {

                case TEXT: {
                    return text;
                }

                case POD_SPACE_VOICE:
                case VOICE: {
                    return "صدا فرستاده شد";
                }

                case POD_SPACE_PICTURE:
                case PICTURE: {
                    return "تصویری فرستاده شد";
                }

                case POD_SPACE_FILE:
                case FILE: {
                    return "فایلی فرستاده شد";
                }

                case POD_SPACE_VIDEO:
                case VIDEO: {
                    return "ویدئویی فرستاده شد";
                }
                case POD_SPACE_SOUND:
                case SOUND: {
                    return "فایل صوتی فرستاده شد";
                }

                case LINK: {
                    return "لینکی فرستاده شد";
                }

                default: {
                    return "پیام جدیدی ارسال شد";
                }

            }


        } catch (NumberFormatException e) {
            return "پیام جدیدی ارسال شد";
        }


    }

    public static class NotificationClickReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null)
                if (intent.getAction().equals(ACTION_1)) {

                    String tId = intent.getStringExtra(THREAD_ID);
                    String mId = intent.getStringExtra(MESSAGE_ID);
                    String sNa = intent.getStringExtra(SENDER_USER_NAME);
                    String tCa = intent.getStringExtra("T_CLASS");

                    if (tCa != null)
                        try {
                            Class<?> c = Class.forName(tCa);
                            Intent targetIntent = new Intent(context, c);
                            targetIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            targetIntent.putExtra(THREAD_ID, tId);
                            targetIntent.putExtra(MESSAGE_ID, mId);
                            targetIntent.putExtra(SENDER_USER_NAME, sNa);
                            context.startActivity(targetIntent);
                            PodNotificationManager.clearNotification(mId);

                            dismissOtherNotifications(context);

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }


                }

        }
    }

    //application is open. other notifications are not necessary!
    static void dismissOtherNotifications(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancelAll();
        }
    }
}

