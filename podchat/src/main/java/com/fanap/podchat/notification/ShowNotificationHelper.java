package com.fanap.podchat.notification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.request.RequestOptions;
import com.fanap.podchat.R;
import com.fanap.podchat.call.audio_call.EndCallReceiver;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.fanap.podchat.call.audio_call.PodCallAudioCallService.REQUEST_CODE_END_CALL;
import static com.fanap.podchat.call.audio_call.PodCallAudioCallService.REQUEST_CODE_OPEN_APP;
import static com.fanap.podchat.notification.PodChatPushNotificationService.TAG;
import static com.fanap.podchat.util.ChatConstant.POD_CALL_INFO;
import static com.fanap.podchat.util.ChatConstant.POD_PUSH_MESSAGE_ID;
import static com.fanap.podchat.util.ChatConstant.POD_PUSH_SENDER_USER_NAME;
import static com.fanap.podchat.util.ChatConstant.POD_PUSH_THREAD_ID;
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


    private static final String ACTION_1 = "action_1";
    private static final int SUMMARY_ID = 0;


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

            intent.putExtra(POD_PUSH_THREAD_ID, threadId);
            intent.putExtra(POD_PUSH_MESSAGE_ID, messageId);
            intent.putExtra(POD_PUSH_SENDER_USER_NAME, messageSenderUserName);

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

            Intent action1Intent = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_1);

            PendingIntent action1PendingIntent = PendingIntent.getService(context, 0,
                    action1Intent, PendingIntent.FLAG_ONE_SHOT);


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

                group = threadId;

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


                Class<?> c = Class.forName(targetClassName);
                Intent intent = new Intent(context, c);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent.putExtra(POD_PUSH_THREAD_ID, threadId);
                intent.putExtra(POD_PUSH_MESSAGE_ID, messageId);
                intent.putExtra(POD_PUSH_SENDER_USER_NAME, messageSenderUserName);

                PendingIntent pendingIntent = PendingIntent
                        .getActivity(context.getApplicationContext(),
                                REQUEST_CODE,
                                intent,
                                PendingIntent.FLAG_CANCEL_CURRENT);


                NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(
                        context.getApplicationContext(), !Util.isNullOrEmpty(channelId) ?
                        channelId : CHANNEL_ID)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setContent(view)
                        .setCustomContentView(view)
                        .setCustomBigContentView(viewExpanded)
                        .setContentIntent(pendingIntent)
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

            Notification messagesNotif = new NotificationCompat.Builder(
                    context.getApplicationContext(),
                    !Util.isNullOrEmpty(channelId) ? channelId : CHANNEL_ID)
                    .setContentTitle(notificationMap.size() + "+ پیام جدید دارید")
                    .setContentText("شما +" + notificationMap.size() + " پیام خوانده نشده دارید")
                    .setStyle(messagingStyle)
                    .setAutoCancel(true)
                    .setGroup(group)
                    .setGroupSummary(true)
                    .setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message)
                    .setPriority(priority != null ? priority : NotificationCompat.PRIORITY_DEFAULT)
                    .build();


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

                if (notificationMap.size() > 2) {

                    notificationManager.notify(SUMMARY_ID, summaryBuilder);

                }


            } else Log.e(TAG, "Can't create notification manager instance");


        } catch (ClassNotFoundException e) {
            Log.e(TAG, "Couldn't start activity: " + e.getMessage() + " " + targetClassName);
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
            remoteViews.setTextViewText(R.id.textViewCallName, callInfo.getPartnerName());
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


    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "Received notification action: " + action);
            if (ACTION_1.equals(action)) {

                PodNotificationManager.clearNotifications();


            }
        }
    }


}

