package com.fanap.podchat.call.audio_call;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;

import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.ClientDTO;
import com.fanap.podchat.notification.ShowNotificationHelper;
import com.fanap.podchat.util.Util;

import static com.fanap.podchat.call.audio_call.PodCallServiceManager.BROKER_ADDRESS;
import static com.fanap.podchat.call.audio_call.PodCallServiceManager.CLIENT_ID;
import static com.fanap.podchat.call.audio_call.PodCallServiceManager.KAFKA_CONFIG;
import static com.fanap.podchat.call.audio_call.PodCallServiceManager.RECEIVING_TOPIC;
import static com.fanap.podchat.call.audio_call.PodCallServiceManager.SENDING_TOPIC;
import static com.fanap.podchat.call.audio_call.PodCallServiceManager.SSL_CONFIG;
import static com.fanap.podchat.call.audio_call.PodCallServiceManager.TARGET_ACTIVITY;
import static com.fanap.podchat.util.ChatConstant.POD_CALL_INFO;

public class AudioCallService extends Service {


    private static final String CHANNEL_NAME = "POD_CHAT_CALL_CHANNEL";
    private static final String DESCRIPTION = "Podchat channel for call";
    private static final int NOTIFICATION_ID = 7007;
    public static final int REQUEST_CODE_OPEN_APP = 7006;
    public static final int REQUEST_CODE_END_CALL = 7005;
    private String CHANNEL_ID = "PODCHAT_CALL";

    private IBinder serviceBinder = new CallBinder();

    private String sendingTopic;
//    private String clientId;
    private String sendKey;
    private String brokerAddress;
    private String receivingTopic;
    private String ssl_key;

    String targetActivity;

    private CallInfo callInfo;

    PodAudioCallManager callManager;

    ICallServiceState callStateCallback;

    public AudioCallService() {
    }

    public void registerCallStateCallback(ICallServiceState callStateCallback) {
        this.callStateCallback = callStateCallback;
    }

    public void unregisterCallStateCallback(ICallServiceState callStateCallback) {
        this.callStateCallback = null;
    }

    @Override
    public void onDestroy() {

        if (callManager != null) {
            callManager.endStream();
            callManager = null;
        }

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


        //prevents to start again
        if (callManager != null)
            return START_NOT_STICKY;

        getIntentData(intent);

        showAudioCallNotification();

        startStreaming();

        return START_NOT_STICKY;
    }

    private boolean isCloseIntent(Intent intent) {
        return intent.getAction() != null &&
                intent.getAction().equals(EndCallReceiver.ACTION_STOP_CALL);
    }

    private void startStreaming() {

        callManager = new PodAudioCallManager(this, sendingTopic, receivingTopic, sendKey, brokerAddress, ssl_key);

        if (Util.isNullOrEmpty(sendingTopic)) {
            callManager.testAudio();
        } else callManager.startStream();
    }

    private void getIntentData(Intent intent) {

        ClientDTO client = intent.getParcelableExtra(KAFKA_CONFIG);

        if(client!=null){
            sendingTopic = client.getTopicSend();
            receivingTopic = client.getTopicReceive();
            sendKey = client.getSendKey();
            brokerAddress = client.getBrokerAddress();
        }


        ssl_key = intent.getStringExtra(SSL_CONFIG);

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

    public void switchMic(boolean isMute) {
        callManager.switchAudioMuteState(isMute);
    }

    public void switchSpeaker(boolean isSpeakerOn) {
        callManager.switchAudioSpeakerState(isSpeakerOn);
    }

    public void endCall() {
        onDestroy();
    }

    public void setSSL(boolean enableSSL) {

        if (callManager != null)
            callManager.setSSL(enableSSL);
    }

    public class CallBinder extends Binder {

        public AudioCallService getService() {

            return AudioCallService.this;

        }
    }

}
