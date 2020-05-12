package com.fanap.podchat.notification;

import android.content.Intent;
import android.util.Log;

import com.fanap.podchat.R;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.securepreferences.SecurePreferences;

import java.util.Map;

public class PodChatPushNotificationService extends FirebaseMessagingService {


     static final String TAG = "CHAT_SDK_NOTIFICATION";
    public static final String ACTION_REFRESH = "ACTION_TOKEN_REFRESHED";
    public static final String KEY_TOKEN = "TOKEN";
    public static final String KEY_TYPE = "TOKEN_TYPE";
    public static final String INIT_TOKEN = "INITIAL";
    public static final String REFRESHED_TOKEN = "REFRESHED";

    public PodChatPushNotificationService() {
        super();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {

            Log.i(TAG, "FCM ON GET INSTANCE COMPLETE");

            if (task.isSuccessful()) {
                String token = task.getResult().getToken();
                broadcastNewFCMToken(token, INIT_TOKEN);
            } else {
                // TODO: 5/10/2020 handle failure
            }


        });


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
        broadcastNewFCMToken(s, REFRESHED_TOKEN);
    }

    private void broadcastNewFCMToken(String s, String type) {
        Intent intent = new Intent(ACTION_REFRESH);
        intent.putExtra(KEY_TOKEN, s);
        intent.putExtra(KEY_TYPE, type);
        sendBroadcast(intent);
    }
}
