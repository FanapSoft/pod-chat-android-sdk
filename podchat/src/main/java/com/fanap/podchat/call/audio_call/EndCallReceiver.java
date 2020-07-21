package com.fanap.podchat.call.audio_call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class EndCallReceiver extends BroadcastReceiver {

    public static final String ACTION_STOP_CALL = "ACTION_STOP_CALL";

    public EndCallReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sendEndCallIntent(context);
    }

    private void sendEndCallIntent(Context context) {
        Intent intent1 = new Intent(context, AudioCallService.class);
        intent1.setAction(ACTION_STOP_CALL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent1);
        } else context.startService(intent1);
    }
}