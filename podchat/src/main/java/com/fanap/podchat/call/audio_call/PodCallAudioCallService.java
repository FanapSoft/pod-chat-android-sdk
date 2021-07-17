package com.fanap.podchat.call.audio_call;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;

import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.notification.ShowNotificationHelper;


import static com.fanap.podchat.call.audio_call.CallServiceManager.TARGET_ACTIVITY;
import static com.fanap.podchat.util.ChatConstant.POD_CALL_INFO;

public class PodCallAudioCallService extends Service {


    private static final String CHANNEL_NAME = "POD_CHAT_CALL_CHANNEL";
    private static final String DESCRIPTION = "Podchat channel for call";
    private static final int NOTIFICATION_ID = 7007;
    public static final int REQUEST_CODE_OPEN_APP = 7006;
    public static final int REQUEST_CODE_END_CALL = 7005;
    private String CHANNEL_ID = "PODCHAT_CALL";

    private IBinder serviceBinder = new CallBinder();

    String targetActivity;

    private CallInfo callInfo;

    ICallServiceState callStateCallback;

    public PodCallAudioCallService() {
    }

    public void registerCallStateCallback(ICallServiceState callStateCallback) {
        this.callStateCallback = callStateCallback;
    }

    public void unregisterCallStateCallback() {
        this.callStateCallback = null;
    }

    @Override
    public void onDestroy() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
            managerCompat.cancel(NOTIFICATION_ID);
        }


        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //close command
        if (isCloseIntent(intent)) {
            onDestroy();
            if (callStateCallback != null)
                callStateCallback.onEndCallRequested();
            return START_NOT_STICKY;
        }

        getIntentData(intent);

        showAudioCallNotification();

        return START_NOT_STICKY;
    }

    private boolean isCloseIntent(Intent intent) {
        return intent.getAction() != null &&
                intent.getAction().equals(EndCallReceiver.ACTION_STOP_CALL);
    }

    private void getIntentData(Intent intent) {

        targetActivity = intent.getStringExtra(TARGET_ACTIVITY);

        callInfo = intent.getParcelableExtra(POD_CALL_INFO);
    }

    private void showAudioCallNotification() {


        ShowNotificationHelper
                .setupNotificationChannel(
                        this,
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        DESCRIPTION,
                        NotificationManagerCompat.IMPORTANCE_LOW);


        ShowNotificationHelper
                .showRunningCallNotification(
                        this,
                        targetActivity,
                        callInfo,
                        CHANNEL_ID,
                        NOTIFICATION_ID);

    }


    @Override
    public IBinder onBind(Intent intent) {

        return serviceBinder;
    }


    public void endCall() {
        onDestroy();
    }

    public class CallBinder extends Binder {

        public PodCallAudioCallService getService() {

            return PodCallAudioCallService.this;

        }
    }

}
