package com.fanap.podchat.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class PodNotificationManager {

    private static BroadcastReceiver receiver;

    private static String fcmToken;

    public static BroadcastReceiver getBroadcastReceiver() {


        if (receiver == null) {

            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {


                    fcmToken = intent.getStringExtra(PodChatPushNotificationService.KEY_TOKEN);

                }
            };
        }

        return receiver;

    }

    public static IntentFilter getFCMTokenIntentFilter() {
        return new IntentFilter(PodChatPushNotificationService.ACTION_REFRESH);
    }

    private static String getFcmToken() {
        return fcmToken;
    }


}
