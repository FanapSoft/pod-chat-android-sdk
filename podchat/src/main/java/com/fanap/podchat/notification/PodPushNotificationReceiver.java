package com.fanap.podchat.notification;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arissystem.touca.tmtp.TmtpPushReceiver;
import com.fanap.podchat.util.OnWorkDone;

public class PodPushNotificationReceiver extends TmtpPushReceiver {

    OnWorkDone listener;

    public void setListener(OnWorkDone listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        super.onReceive(context, intent);

        String message = intent.getStringExtra("msg");

        Log.e("CHAT_SDK_NOTIF", "New Notification Received : " + message);

        listener.onWorkDone(message);
    }
}
