package com.fanap.podchat.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PodChatPushNotification extends FirebaseMessagingService {


    private static final String TAG = "CHAT_SDK_NOTIFICATION";

    long userId = 0;

    BroadcastReceiver receiver;

    @Override
    public void onDestroy() {

        unregisterReceiver(receiver);
        receiver = null;
        super.onDestroy();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_SET_USER_ID");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.e(TAG, "On Receive BroadCast");

                userId = intent.getLongExtra("UID", -1);

                Log.e(TAG, "UserId: " + userId);

            }
        };

        registerReceiver(receiver, filter);

    }

    public PodChatPushNotification() {
        super();

        Log.i(TAG, "FCM CONSTRUCTOR");
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {

            Log.i(TAG, "FCM ON GET INSTANCE COMPLETE");

            if (task.isSuccessful()) {

                String token = task.getResult().getToken();

                Log.i(TAG, "FCM ON GET INSTANCE SUCCESS: Token: " + token);


            } else {

                Log.i(TAG, "FCM ON GET INSTANCE Failed cause: " + task.getException());

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
        Log.i(TAG, "FCM ON TOKEN REFRESHED: " + s);

    }
}
