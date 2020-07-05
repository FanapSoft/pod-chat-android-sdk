package com.fanap.podchat.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.request.RequestOptions;
import com.fanap.podchat.R;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static com.fanap.podchat.notification.PodChatPushNotificationService.TAG;

public class ShowNotificationHelper {

    private static final int REQUEST_CODE = 7070;
    private static final String CHANNEL_ID = "PODCHAT";
    private static final String CHANNEL_NAME = "POD_CHAT_CHANNEL";
    private static final String CHANNEL_DESCRIPTION = "Fanap soft podchat notification channel";
    public static final String THREAD_ID = "threadId";
    public static final String MESSAGE_ID = "messageId";
    public static final String SENDER_USER_NAME = "senderUserName";
    public static final String SHOULD_MARK_ALL_AS_READ = "SHOULD_MARK_ALL_AS_READ";


    public static final String ACTION_1 = "action_1";
    private static final int SUMMARY_ID = 1000001;
    private static final String SUMMARY_KEY = "com.fanap.podchat:notification";


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


    static void showSingleNewMessageNotification(
            Map<String, String> notificationData,
            Context context,
            String targetClassName,
            Integer priority,
            Integer smallIcon,
            String channelId) {


        try {


            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String threadName = notificationData.get("threadName");
            String senderName = notificationData.get("MessageSenderName");
            String profileUrl = notificationData.get("senderImage");


            String text = notificationData.get("text");
            String isGroup = notificationData.get("isGroup") != null ? notificationData.get("isGroup") : "false";
            String messageSenderUserName = notificationData.get("MessageSenderUserName");
            String messageId = notificationData.get("messageId");
            String threadId = notificationData.get("threadId");

            if (Util.isNullOrEmpty(messageId)) messageId = "1000";
            if (Util.isNullOrEmpty(threadId)) threadId = "2000";

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

                    Log.d(TAG, "Get profile image failed => " + e.getMessage());
                }

            }


            PendingIntent pendingIntent = getTargetActivityPendingIntent(context, targetClassName, messageSenderUserName, messageId, threadId);


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
                    .setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message)
                    .setPriority(priority != null ? priority : NotificationCompat.PRIORITY_DEFAULT);

            Notification notification = notifBuilder.build();

            if (notificationManager != null) {


                int notificationId = 0;
                if (!Util.isNullOrEmpty(messageId)) {
                    notificationId = Integer.parseInt(messageId) > 0 ? Integer.parseInt(messageId) : 1000;
                }

                notificationManager.notify(notificationId, notification);

            } else Log.e(TAG, "Can't create notification manager instance");


        } catch (Exception e) {
            Log.e(TAG, "Couldn't start activity: " + e.getMessage() + " " + targetClassName);
        }

    }

    static void showSampleNotification(Context context) {


        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle("");

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        messagingStyle.setConversationTitle("Group1");

        for (int i = 0; i < 5; i++) {
            inboxStyle.addLine("Title" + i + " Text" + i);
            messagingStyle.addMessage("سلاام" + i, new Date().getTime(), "احمد");
            messagingStyle.addMessage("چه خبر؟" + i * 2, new Date().getTime(), "مهرداد");
        }

        messagingStyle.setConversationTitle("گروه اسکرام چت");

        inboxStyle.setSummaryText("4 پیام جدید");
        inboxStyle.setBigContentTitle("گروه چت 1");


        Intent markAllIntent = new Intent(context, NotificationDismissReceiver.class);
        markAllIntent.putExtra(MESSAGE_ID, "messageId1");
        markAllIntent.putExtra(THREAD_ID, "threadId1");
        PendingIntent pmarkAllIntent = PendingIntent.getBroadcast(context, Integer.parseInt("100012"), markAllIntent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Action action2 = new NotificationCompat.Action(android.R.drawable.ic_input_add,
                "همه رو خوندم", pmarkAllIntent);


        Notification summary = new NotificationCompat.Builder(
                context.getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setStyle(messagingStyle)
                .setSmallIcon(R.drawable.ic_message)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setGroup("DF")
                .setNumber(2)
                .addAction(action2)
                .setContentTitle("3 پیام جدید")
                .setContentText("شما 3 پیام خوانده نشده از گروه چت 1 دارید")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile))
                .build();


        Notification summary3 = new NotificationCompat.Builder(
                context.getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setStyle(messagingStyle)
                .setSmallIcon(R.drawable.ic_message)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setGroup("DF")
                .setNumber(4)
                .addAction(action2)
                .setContentTitle("3 پیام جدید")
                .setContentText("شما 3 پیام خوانده نشده از گروه چت 1 دارید")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile))
                .build();

        Notification summary4 = new NotificationCompat.Builder(
                context.getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setStyle(messagingStyle)
                .setSmallIcon(R.drawable.ic_message)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setGroup("DF")
                .setNumber(1)
                .addAction(action2)
                .setContentTitle("3 پیام جدید")
                .setContentText("شما 3 پیام خوانده نشده از گروه چت 1 دارید")
                .setOnlyAlertOnce(true)
                .setOnlyAlertOnce(true)
                .setSubText("injaaa")
                .setTicker("injaaham")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), android.R.drawable.zoom_plate))
                .build();


        Notification motherSummary = new NotificationCompat.Builder(
                context.getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setSmallIcon(R.drawable.ic_message)
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setGroup("DF")
                .setNumber(2)
                .addAction(action2)
                .setContentTitle("3 پیام جدید")
                .setContentText("شما 3 پیام خوانده نشده از گروه چت 1 دارید")
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        notificationManager.notify(SUMMARY_ID + 1, summary);
        notificationManager.notify(SUMMARY_ID + 9, summary3);
        notificationManager.notify(SUMMARY_ID + 10, summary4);
        notificationManager.notify(SUMMARY_ID, motherSummary);


    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    static void showSampleNotification2(Context context) {


        Notification.MessagingStyle messagingStyle = new Notification.MessagingStyle("");


        for (int i = 0; i < 5; i++) {
            Person person = new Person.Builder()
                    .setName("احمد" + i)
                    .setIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile)))
                    .build();
            messagingStyle.addMessage("سلاام" + i, new Date().getTime(), person);

            Person person2 = new Person.Builder()
                    .setName("مهرداد")
                    .setIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile)))
                    .build();
            messagingStyle.addMessage("چه خبر؟" + i * 2, new Date().getTime(), person2);

        }
        messagingStyle.setConversationTitle("گروه اسکرام چت");


        Intent markAllIntent = new Intent(context, NotificationDismissReceiver.class);
        markAllIntent.putExtra(MESSAGE_ID, "messageId1");
        markAllIntent.putExtra(THREAD_ID, "threadId1");


        PendingIntent pmarkAllIntent = PendingIntent.getBroadcast(context, Integer.parseInt("100012"), markAllIntent,
                PendingIntent.FLAG_ONE_SHOT);


        android.app.RemoteInput remoteInput = new RemoteInput.Builder("KEY_TEXT_REPLY")
                .setLabel("TEST")
                .build();

        Notification.Action action2 = new Notification.Action(R.mipmap.ic_profile,
                "همه رو خوندم", pmarkAllIntent);

        Notification.Action action3 = new Notification.Action.Builder(
                R.mipmap.ic_profile,
                "پاسخ", pmarkAllIntent
        )
                .addRemoteInput(remoteInput)
                .build();


        Notification summary = new Notification.Builder(
                context.getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setStyle(messagingStyle)
                .setSmallIcon(R.drawable.ic_message)
                .setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY)
                .setGroup("DF")
                .setNumber(2)
                .addAction(action2)
                .addAction(action3)
                .setContentTitle("3 پیام جدید")
                .setContentText("شما 3 پیام خوانده نشده از گروه چت 1 دارید")
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile))
                .build();

        messagingStyle.setGroupConversation(true);

        Notification summary3 = new Notification.Builder(
                context.getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setStyle(messagingStyle)
                .setSmallIcon(R.drawable.ic_message)
                .setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY)
                .setGroup("DF")
                .setNumber(4)
                .addAction(action2)
                .setContentTitle("3 پیام جدید")
                .setContentText("شما 3 پیام خوانده نشده از گروه چت 1 دارید")
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .setLargeIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile)))
                .build();


        Notification summary4 = new Notification.Builder(
                context.getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setStyle(messagingStyle)
                .setSmallIcon(R.drawable.ic_message)
                .setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY)
                .setGroup("DF")
                .setNumber(1)
                .addAction(action2)
                .setContentTitle("3 پیام جدید")
                .setContentText("شما 3 پیام خوانده نشده از گروه چت 1 دارید")
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .setSubText("injaaa")
                .setTicker("injaaham")
                .setLargeIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile)))
                .build();


        Notification motherSummary = new Notification.Builder(
                context.getApplicationContext(), CHANNEL_ID)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setSmallIcon(R.drawable.ic_message)
                .setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY)
                .setGroup("DF")
                .setNumber(2)
                .addAction(action2)
                .setContentTitle("3 پیام جدید")
                .setContentText("شما 3 پیام خوانده نشده از گروه چت 1 دارید")
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);

        notificationManager.notify(SUMMARY_ID + 1, summary);
        notificationManager.notify(SUMMARY_ID + 9, summary3);
        notificationManager.notify(SUMMARY_ID + 10, summary4);
        notificationManager.notify(SUMMARY_ID, motherSummary);


    }


    private static PendingIntent getTargetActivityPendingIntent(Context context, String targetClassName, String messageSenderUserName, String messageId, String threadId) throws ClassNotFoundException {
        Class<?> c = Class.forName(targetClassName);
        Intent intent = new Intent(context, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra(THREAD_ID, threadId);
        intent.putExtra(MESSAGE_ID, messageId);
        intent.putExtra(SENDER_USER_NAME, messageSenderUserName);

        return PendingIntent
                .getActivity(context.getApplicationContext(),
                        Integer.parseInt(messageId),
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
    }


    static void showNewMessageNotification(
            Context context,
            String targetClassName,
            Integer priority,
            Integer smallIcon,
            String channelId) {


        try {
            for (String unreadThreadId :
                    PodThreadPushMessages.getUnreadThreads()) {

                ArrayList<PodPushMessage> threadNotifications = PodThreadPushMessages.getNotificationsOfThread(unreadThreadId);

                if (Util.isNotNullOrEmpty(threadNotifications)) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        showNewPushNotificationMessage(
                                context,
                                targetClassName,
                                priority,
                                smallIcon,
                                channelId,
                                unreadThreadId,
                                threadNotifications);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        showNewPushNotificationMessageBeforeP(
                                context,
                                priority,
                                targetClassName,
                                smallIcon,
                                channelId,
                                unreadThreadId,
                                threadNotifications);
                    } else {
                        showNewPushNotificationMessageBeforeN(
                                context,
                                priority,
                                targetClassName,
                                smallIcon,
                                channelId,
                                unreadThreadId,
                                threadNotifications);

                    }
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Error when showing new message notification: " + e.getMessage());
        }


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                summaryAllGroups(context, priority,
                        smallIcon,
                        channelId);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                summaryAllGroupsBeforeO(context, priority,
                        smallIcon,
                        channelId);
            } else {
                summaryAllGroupsBeforeN(context, priority,
                        smallIcon,
                        channelId, targetClassName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error when showing summary notification: " + e.getMessage());
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void summaryAllGroups(Context context, Integer priority, Integer smallIcon, String channelId) {
        Notification summary = new Notification.Builder(
                context.getApplicationContext(), !Util.isNullOrEmpty(channelId) ?
                channelId : CHANNEL_ID)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message)
                .setPriority(priority != null ? priority : Notification.PRIORITY_DEFAULT)
                .setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY)
                .setGroup(SUMMARY_KEY)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(SUMMARY_ID, summary);
    }

    private static void summaryAllGroupsBeforeO(Context context, Integer priority, Integer smallIcon, String channelId) {

        Notification summary = new NotificationCompat.Builder(
                context.getApplicationContext(), !Util.isNullOrEmpty(channelId) ?
                channelId : CHANNEL_ID)
                .setAutoCancel(true)
                .setGroupSummary(true)
                .setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message)
                .setPriority(priority != null ? priority : Notification.PRIORITY_DEFAULT).setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
                .setGroup(SUMMARY_KEY)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(SUMMARY_ID, summary);
    }

    private static void summaryAllGroupsBeforeN(Context context, Integer priority, Integer smallIcon, String channelId, String targetClassName) {


        if (PodThreadPushMessages.getUnreadThreadCount() > 5) {

            Intent markAllIntent = new Intent(context, NotificationDismissReceiver.class);

            markAllIntent.putExtra(SHOULD_MARK_ALL_AS_READ, true);

            PendingIntent pmarkAllIntent = PendingIntent.getBroadcast(context,
                    SUMMARY_ID,
                    markAllIntent,
                    PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Action actionMarkAsReadAll = new NotificationCompat
                    .Action(R.drawable.ic_message,
                    "خوانده شد", pmarkAllIntent);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context.getApplicationContext(), !Util.isNullOrEmpty(channelId) ?
                    channelId : CHANNEL_ID);
            builder.setAutoCancel(true);
            builder.setGroupSummary(true);
            builder.setDeleteIntent(pmarkAllIntent);
            builder.addAction(actionMarkAsReadAll);
            builder.setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message));

            builder.setPriority(priority != null ? priority : Notification.PRIORITY_DEFAULT);
            builder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY);
            builder.setGroup(SUMMARY_KEY);
            builder.setStyle(new NotificationCompat.InboxStyle()
                    .setSummaryText("شما " + PodThreadPushMessages.getUnreadPushMessagesCount() + " پیام خوانده نشده از " + PodThreadPushMessages.getUnreadThreadCount() + " گفتگو دارید")
                    .setBigContentTitle("" + PodThreadPushMessages.getUnreadPushMessagesCount() + " پیام خوانده نشده")
                    .addLine(PodThreadPushMessages.getLastUnreadThreadName() + " " + PodThreadPushMessages.getLastUnreadThreadMessage()));
            builder.setContentText("شما " + PodThreadPushMessages.getUnreadPushMessagesCount() + " پیام خوانده نشده از " + PodThreadPushMessages.getUnreadThreadCount() + " گفتگو دارید");
            builder.setContentTitle("" + PodThreadPushMessages.getUnreadPushMessagesCount() + " پیام خوانده نشده");
            builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
            builder.setOnlyAlertOnce(true);


            try {
                PendingIntent pendingIntent = getTargetActivityPendingIntent(context, targetClassName, PodThreadPushMessages.getLastUnreadThreadSenderUserName(), String.valueOf(PodThreadPushMessages.getLastUnreadMessageId()), String.valueOf(PodThreadPushMessages.getLastUnreadThreadId()));
                builder.setContentIntent(pendingIntent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            Notification summary = builder
                    .build();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            notificationManagerCompat.notify(SUMMARY_ID, summary);

        }

    }


    private static void showNewPushNotificationMessageBeforeP(Context context,
                                                              Integer priority,
                                                              String targetClassName, Integer smallIcon,
                                                              String channelId,
                                                              String threadIdAsKey,
                                                              ArrayList<PodPushMessage> threadNotification) {


        PodPushMessage threadMessageData = threadNotification.get(0);
        String threadName = threadMessageData.getThreadName();
        long threadId = threadMessageData.getThreadId();
        String threadImage = threadMessageData.getThreadImage();
        boolean isGroup = threadMessageData.isGroup();
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(threadName);

        for (PodPushMessage pushMessage :
                threadNotification) {
            messagingStyle.addMessage(pushMessage.getText(), pushMessage.getTime(), pushMessage.getMessageSenderName());

        }

        messagingStyle.setConversationTitle(threadName);


        Intent markAllIntent = new Intent(context, NotificationDismissReceiver.class);
        markAllIntent.putExtra(THREAD_ID, threadId);

        PendingIntent pmarkAllIntent = PendingIntent.getBroadcast(context, Integer.parseInt(threadIdAsKey), markAllIntent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Action actionMarkAsReadAll = new NotificationCompat.Action(R.drawable.ic_message,
                "خوانده شد", pmarkAllIntent);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context.getApplicationContext(), !Util.isNullOrEmpty(channelId) ?
                channelId : CHANNEL_ID);
        builder.setAutoCancel(true);
        builder.setStyle(messagingStyle);
        builder.setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message);
        builder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY);
        builder.setGroup(SUMMARY_KEY);
        builder.addAction(actionMarkAsReadAll);
        builder.setDeleteIntent(pmarkAllIntent);
        builder.setPriority(priority != null ? priority : NotificationCompat.PRIORITY_DEFAULT);
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        builder.setOnlyAlertOnce(true);

        builder.setSubText(threadName);


        if (!isGroup) {
            if (Util.isNotNullOrEmpty(threadImage)) {
                try {
                    Bitmap bitmap = GlideApp.with(context)
                            .asBitmap()
                            .load(threadImage)
                            .apply(RequestOptions.circleCropTransform())
                            .submit(512, 512)
                            .get();
                    builder.setLargeIcon(bitmap);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile));
                }

            } else {
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile));
            }
        } else {

            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_group));

        }


        try {
            PendingIntent pendingIntent = getTargetActivityPendingIntent(context, targetClassName, threadNotification.get(threadNotification.size() - 1).getMessageSenderUserName(), String.valueOf(threadNotification.get(threadNotification.size() - 1).getMessageId()), threadIdAsKey);
            builder.setContentIntent(pendingIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Notification notification = builder
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify((int) threadId, notification);

    }

    private static void showNewPushNotificationMessageBeforeN(Context context,
                                                              Integer priority,
                                                              String targetClassName, Integer smallIcon,
                                                              String channelId,
                                                              String threadIdAsKey,
                                                              ArrayList<PodPushMessage> threadNotification) {


        PodPushMessage threadMessageData = threadNotification.get(0);
        String threadName = threadMessageData.getThreadName();
        long threadId = threadMessageData.getThreadId();
        String threadImage = threadMessageData.getThreadImage();
        boolean isGroup = threadMessageData.isGroup();
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        for (PodPushMessage pushMessage :
                threadNotification) {
            inboxStyle.addLine(pushMessage.getMessageSenderName() + " " + pushMessage.getText());

        }

        inboxStyle.setSummaryText(threadNotification.size() + " پیام جدید از " + threadName);

        inboxStyle.setBigContentTitle(threadName);


        Intent markAllIntent = new Intent(context, NotificationDismissReceiver.class);
        markAllIntent.putExtra(THREAD_ID, threadId);

        PendingIntent pmarkAllIntent = PendingIntent.getBroadcast(context, Integer.parseInt(threadIdAsKey), markAllIntent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Action actionMarkAsReadAll = new NotificationCompat.Action(R.drawable.ic_message,
                "خوانده شد", pmarkAllIntent);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context.getApplicationContext(), !Util.isNullOrEmpty(channelId) ?
                channelId : CHANNEL_ID);
        builder.setAutoCancel(true);
        builder.setContentTitle("" + threadNotification.size() + " پیام جدید از " + threadName);

        builder.setStyle(inboxStyle);
        builder.setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message);
        builder.setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY);
        builder.setGroup(SUMMARY_KEY);
        builder.addAction(actionMarkAsReadAll);
        builder.setDeleteIntent(pmarkAllIntent);
        builder.setPriority(priority != null ? priority : NotificationCompat.PRIORITY_DEFAULT);
        builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        builder.setOnlyAlertOnce(true);

        builder.setSubText(threadName);


        if (!isGroup) {
            if (Util.isNotNullOrEmpty(threadImage)) {
                try {
                    Bitmap bitmap = GlideApp.with(context)
                            .asBitmap()
                            .load(threadImage)
                            .apply(RequestOptions.circleCropTransform())
                            .submit(512, 512)
                            .get();
                    builder.setLargeIcon(bitmap);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile));
                }

            } else {
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_profile));
            }
        } else {
            //TODO add group image
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_group));
        }


        try {
            PendingIntent pendingIntent = getTargetActivityPendingIntent(context, targetClassName, threadNotification.get(threadNotification.size() - 1).getMessageSenderUserName(), String.valueOf(threadNotification.get(threadNotification.size() - 1).getMessageId()), threadIdAsKey);
            builder.setContentIntent(pendingIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Notification notification = builder
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify((int) threadId, notification);

    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    private static void showNewPushNotificationMessage(Context context,
                                                       String targetClassName,
                                                       Integer priority,
                                                       Integer smallIcon,
                                                       String channelId,
                                                       String threadIdAsKey,
                                                       ArrayList<PodPushMessage> threadNotification) {


        PodPushMessage threadMessageData = threadNotification.get(0);
        String threadName = threadMessageData.getThreadName();
        long threadId = threadMessageData.getThreadId();
        boolean isGroup = threadMessageData.isGroup();
        String threadImage = threadMessageData.getThreadImage();


        Notification.MessagingStyle messagingStyle = new Notification.MessagingStyle(threadName);

        for (PodPushMessage pushMessage :
                threadNotification) {

            Person.Builder builder = new Person.Builder();
            builder.setName(pushMessage.getMessageSenderName());
            builder.setKey(pushMessage.getMessageSenderUserName());

            if (Util.isNotNullOrEmpty(pushMessage.getProfileImage())) {

                try {
                    Bitmap bitmap = GlideApp.with(context)
                            .asBitmap()
                            .load(pushMessage.getProfileImage())
                            .apply(RequestOptions.circleCropTransform())
                            .submit(512, 512)
                            .get();

                    builder.setIcon(Icon.createWithBitmap(bitmap));


                } catch (ExecutionException e) {
                    e.printStackTrace();
                    builder.setIcon(Icon.createWithResource(context, R.mipmap.ic_profile));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    builder.setIcon(Icon.createWithResource(context, R.mipmap.ic_profile));
                }

            } else {

                builder.setIcon(Icon.createWithResource(context, R.mipmap.ic_profile));

            }

            Person person = builder
                    .build();

            messagingStyle.addMessage(pushMessage.getText(), pushMessage.getTime(), person);

        }

        messagingStyle.setConversationTitle(threadName);
        messagingStyle.setGroupConversation(isGroup);


        Intent markAllIntent = new Intent(context, NotificationDismissReceiver.class);
        markAllIntent.putExtra(THREAD_ID, threadId);

        PendingIntent pmarkAllIntent = PendingIntent.getBroadcast(context, Math.toIntExact(threadId), markAllIntent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification.Action actionMarkAsReadAll = new Notification.Action(R.drawable.ic_message,
                "خوانده شد", pmarkAllIntent);


        Notification.Builder builder = new Notification.Builder(
                context.getApplicationContext(), !Util.isNullOrEmpty(channelId) ?
                channelId : CHANNEL_ID);
        builder.setAutoCancel(true);
        builder.setStyle(messagingStyle);
        builder.setSmallIcon(smallIcon != null && smallIcon > 0 ? smallIcon : R.drawable.ic_message);
        builder.setGroupAlertBehavior(Notification.GROUP_ALERT_SUMMARY);
        builder.setGroup(SUMMARY_KEY);
        builder.addAction(actionMarkAsReadAll);
        builder.setDeleteIntent(pmarkAllIntent);
        builder.setPriority(priority != null ? priority : Notification.PRIORITY_DEFAULT);
        builder.setCategory(Notification.CATEGORY_MESSAGE);
        builder.setOnlyAlertOnce(true);
        builder.setSubText(threadName);


        // TODO: 7/1/2020 add thread image too

        if (isGroup)
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_group));


//        if(isGroup){
//            if (Util.isNotNullOrEmpty(threadImage)) {
//                try {
//                    Bitmap bitmap = GlideApp.with(context)
//                            .asBitmap()
//                            .load(threadImage)
//                            .apply(RequestOptions.circleCropTransform())
//                            .submit(512, 512)
//                            .get();
//                    builder.setLargeIcon(bitmap);
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_profile));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                    builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_profile));
//                }
//
//            } else {
//                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_profile));
//            }
//        }

        try {
            PendingIntent pendingIntent = getTargetActivityPendingIntent(context, targetClassName, threadNotification.get(threadNotification.size() - 1).getMessageSenderUserName(), String.valueOf(threadNotification.get(threadNotification.size() - 1).getMessageId()), threadIdAsKey);
            builder.setContentIntent(pendingIntent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Notification notification = builder
                .build();


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Math.toIntExact(threadId), notification);


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

    public static class NotificationDismissReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            Log.e(TAG, "Notification dismiss received");


            boolean shouldReadAll = intent.getBooleanExtra(SHOULD_MARK_ALL_AS_READ, false);
            if (shouldReadAll) {
                PodThreadPushMessages.clearMessages();
                dismissOtherNotifications(context);
                return;
            }

            long threadId = intent.getLongExtra(THREAD_ID, -1);
            if (threadId != -1) {
                PodThreadPushMessages.markThreadAsRead(threadId);
                if (PodThreadPushMessages.getUnreadThreads().size() == 0)
                    dismissOtherNotifications(context);
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dismissNotification(context, Math.toIntExact(threadId));
                } else {
                    dismissNotification(context, (int) threadId);
                }
            }

        }
    }


    //application is open. other notifications are not necessary!
    static void dismissOtherNotifications(Context context) {
        NotificationManagerCompat notificationManager =
               NotificationManagerCompat.from(context);
        notificationManager.cancelAll();
        notificationManager.cancel(SUMMARY_ID);
    }

    private static void dismissNotification(Context context, int notificationId) {
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }

}

