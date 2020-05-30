package com.fanap.podchat.notification;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PodChatPushNotificationService extends FirebaseMessagingService {


     static final String TAG = "CHAT_SDK_NOTIFICATION";
    public static final String ACTION_REFRESH = "ACTION_TOKEN_REFRESHED";
    public static final String KEY_TOKEN = "TOKEN";
    public static final String INIT_TOKEN = "INITIAL";
    public static final String REFRESHED_TOKEN = "REFRESHED";

    public PodChatPushNotificationService() {
        super();
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        Log.i(TAG, "Notification Received: ");
//        Log.i(TAG, "Title: " + remoteMessage.getNotification().getTitle());
//        Log.i(TAG, "Body: " + remoteMessage.getNotification().getBody());
        Log.i(TAG, "Message Data: " + remoteMessage.getData());

        PodNotificationManager.handleMessage(this,remoteMessage);
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Log.d(TAG, "ON PUSH MESSAGE SENT");
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
        Log.wtf(TAG, e);
        Log.e(TAG, "On ERROR " + s);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d(TAG, "TOKEN: " + s);
        broadcastNewFCMToken(s);
    }

    private void broadcastNewFCMToken(String s) {
        Intent intent = new Intent(ACTION_REFRESH);
        intent.putExtra(KEY_TOKEN, s);
        sendBroadcast(intent);
    }
}
