package com.fanap.podchat.notification;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PodChatPushNotificationService extends FirebaseMessagingService {


    private static final String TAG = "CHAT_SDK_NOTIFICATION";
    public static final String ACTION_REFRESH = "ACTION_TOKEN_REFRESHED";
    public static final String KEY_TOKEN = "TOKEN";

    public PodChatPushNotificationService() {
        super();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {

            Log.i(TAG, "FCM ON GET INSTANCE COMPLETE");

            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                broadcastNewFCMToken(token);
            } else {
                // TODO: 5/10/2020 handle failure
            }


        });


    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
        Log.i(TAG, "Notification Received: ");
        Log.i(TAG, "Title: " + remoteMessage.getNotification().getTitle());
        Log.i(TAG, "Body: " + remoteMessage.getNotification().getBody());
        Log.i(TAG, "Message Data: " + remoteMessage.getData());
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        broadcastNewFCMToken(s);
    }

    private void broadcastNewFCMToken(String s) {
        Intent intent = new Intent(ACTION_REFRESH);
        intent.putExtra(KEY_TOKEN, s);
        sendBroadcast(intent);
    }
}
