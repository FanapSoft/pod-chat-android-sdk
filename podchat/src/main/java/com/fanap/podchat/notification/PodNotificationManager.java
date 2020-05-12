package com.fanap.podchat.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.fanap.podchat.R;
import com.fanap.podchat.chat.App;
import com.google.firebase.messaging.RemoteMessage;
import com.securepreferences.SecurePreferences;

import java.util.Map;

import static com.fanap.podchat.chat.Chat.TAG;

public class PodNotificationManager {

    private static IPodNotificationManager listener;

    private static CustomNotificationConfig notificationConfig;

    private static BroadcastReceiver receiver;

    private static String fcmToken;

    public static void registerFCMTokenReceiver(Context context) {

        try {
            listener.onLogEvent("Try to register notification receiver");

            if (receiver == null) {
                listener.onLogEvent("Registering notification receiver");

                receiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        fcmToken = intent.getStringExtra(PodChatPushNotificationService.KEY_TOKEN);
                        String tokenType = intent.getStringExtra(PodChatPushNotificationService.KEY_TYPE);
                        listener.onLogEvent("Notification token refreshed");
                        if (tokenType != null) {
                            if (tokenType.equals(PodChatPushNotificationService.INIT_TOKEN)) {
                                listener.onLogEvent("TYPE: INITIAL TOKEN");
                                listener.onInitialTokenReceived(fcmToken);
                            } else if (tokenType.equals(PodChatPushNotificationService.REFRESHED_TOKEN)) {
                                listener.onLogEvent("TYPE: REFRESHED TOKEN");
                                listener.onTokenRefreshed(fcmToken);
                            }
                        }

                    }
                };


                context.registerReceiver(receiver, getFCMTokenIntentFilter());

                listener.onLogEvent("Notification receiver registered successfully");


            }
        } catch (Exception e) {
            listener.onLogEvent("failed to registering notification receiver");
            listener.onLogEvent(e.getMessage());
        }

    }

    public static void unRegisterReceiver(Context context) {

        try {
            listener.onLogEvent("Try to unregister notification receiver");

            context.unregisterReceiver(receiver);
            receiver = null;
            listener.onLogEvent(" unregister notification receiver done");

        } catch (Exception e) {
            listener.onLogEvent("failed to unregistering notification receiver");
            listener.onLogEvent(e.getMessage());
        }

    }

    public static void listenLogs(IPodNotificationManager mListener) {

        listener = mListener;

    }

    private static IntentFilter getFCMTokenIntentFilter() {
        return new IntentFilter(PodChatPushNotificationService.ACTION_REFRESH);
    }

    private static String getFcmToken() {
        return fcmToken;
    }

    public static void withConfig(CustomNotificationConfig mConfig, Context context) {

        ShowNotificationHelper.setupNotificationChannel(
                mConfig.getTargetActivity(),
                mConfig.getChannelId(),
                mConfig.getChannelName(),
                mConfig.getChannelDescription(),
                mConfig.getNotificationImportance());

        notificationConfig = mConfig;

        saveConfig(mConfig, context);
    }

    private static void saveConfig(CustomNotificationConfig mConfig, Context context) {

        SecurePreferences s = new SecurePreferences(context, "", "chat_prefs.xml");

        SharedPreferences.Editor e = s.edit();

        e.putString("TARGET_ACTIVITY", mConfig.getTargetActivity().getClass().getName());

        e.putInt("ICON", mConfig.getIcon());

        e.putInt("N_IMP", mConfig.getNotificationImportance());

        e.putString("CHANNEL_ID", mConfig.getChannelId());

        e.apply();


    }

    private static void showNotification(Map<String, String> data, Context context) {

        SecurePreferences securePreferences = new SecurePreferences(context, "", "chat_prefs.xml");

        ShowNotificationHelper.showNewMessageNotification(
                data.get("threadName"),
                data.get("senderName"),
                data.get("image"),
                data.get("content"),
                context,
                securePreferences.getString("TARGET_ACTIVITY", ""),
                securePreferences.getInt("N_IMP", NotificationManagerCompat.IMPORTANCE_DEFAULT),
                securePreferences.getInt("ICON", R.drawable.common_google_signin_btn_icon_dark),
                securePreferences.getString("CHANNEL_ID", "")
        );
//        ShowNotificationHelper.showNotification(
//                title,
//                data.get("content"),
//                context,
//                securePreferences.getString("TARGET_ACTIVITY", ""),
//                securePreferences.getInt("N_IMP", NotificationManagerCompat.IMPORTANCE_DEFAULT),
//                securePreferences.getInt("ICON", R.drawable.common_full_open_on_phone));

    }

    static void handleMessage(Context context, RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();

        showNotification(data, context);

    }

    public interface IPodNotificationManager {

        void onLogEvent(String log);

        void onInitialTokenReceived(String token);

        void onTokenRefreshed(String token);

    }

}
