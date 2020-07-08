package com.fanap.podchat.chat.call.audio_call;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.fanap.podchat.R;
import com.fanap.podchat.notification.PodNotificationManager;
import com.fanap.podchat.notification.ShowNotificationHelper;
import com.fanap.podchat.util.Util;

import static com.fanap.podchat.chat.call.audio_call.PodCallServiceManager.BROKER_ADDRESS;
import static com.fanap.podchat.chat.call.audio_call.PodCallServiceManager.CLIENT_ID;
import static com.fanap.podchat.chat.call.audio_call.PodCallServiceManager.RECEIVING_TOPIC;
import static com.fanap.podchat.chat.call.audio_call.PodCallServiceManager.SENDING_TOPIC;

public class AudioCallService extends Service {


    private static final String CHANNEL_NAME = "POD_CHAT_CALL_CHANNEL";
    private static final String DESCRIPTION = "Podchat channel for call";
    private static final int NOTIFICATION_ID = 10;
    private String CHANNEL_ID = "PODCHAT_CALL";

    private IBinder serviceBinder = new CallBinder();

    String sendingTopic;
    String clientId;
    String brokerAddress;
    String receivingTopic;

    PodAudioCallManager callManager;

    public AudioCallService() {
    }


    @Override
    public void onDestroy() {
        callManager.endStream();
        stopForeground(true);
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

        if (intent.getAction() != null && intent.getAction().equals(EndCallReceiver.ACTION_STOP_CALL)) {
            onDestroy();
            return START_NOT_STICKY;
        }

        showAudioCallNotification();
        callManager = new PodAudioCallManager(this);


        sendingTopic = intent.getStringExtra(SENDING_TOPIC);
        receivingTopic = intent.getStringExtra(RECEIVING_TOPIC);
        clientId = intent.getStringExtra(CLIENT_ID);
        brokerAddress = intent.getStringExtra(BROKER_ADDRESS);

        if (Util.isNullOrEmpty(sendingTopic)) {
            callManager.testAudio();
        }


        return START_NOT_STICKY;
    }

    private void showAudioCallNotification() {


        ShowNotificationHelper.setupNotificationChannel(
                this,
                CHANNEL_ID, CHANNEL_NAME, DESCRIPTION,
                NotificationManagerCompat.IMPORTANCE_LOW
        );

        Intent closeAction = new Intent(this, EndCallReceiver.class);

        Intent notificationIntent = new Intent(this, this.getClass());

        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        PendingIntent pendingIntentEnd = PendingIntent.getBroadcast(this,
                5456, closeAction, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Action actionStop = new NotificationCompat.Action(R.drawable.ic_message,
                "پایان", pendingIntentEnd);

        Notification notification = new NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText("Runnuing")
                .setSmallIcon(R.drawable.ic_message)
                .setContentIntent(pendingIntent)
                .addAction(actionStop)
                .setOngoing(true)
                .build();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, notification);
        }


    }

    @Override
    public IBinder onBind(Intent intent) {

        return serviceBinder;
    }

    public void switchMic(boolean isMute) {
        callManager.switchAudioMuteState(isMute);
    }

    public void switchSpeaker(boolean isSpeakerOn) {
        callManager.switchAudioSpeakerState(isSpeakerOn);
    }

    public void endCall() {
        onDestroy();
    }

    public class CallBinder extends Binder {

        public AudioCallService getService() {

            return AudioCallService.this;

        }
    }

}
