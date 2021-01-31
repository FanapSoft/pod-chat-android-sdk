package com.fanap.podchat.call.audio_call;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;

import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.ClientDTO;
import com.fanap.podchat.notification.ShowNotificationHelper;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.List;

import static com.fanap.podchat.call.audio_call.PodCallAudioCallServiceManager.KAFKA_CONFIG;
import static com.fanap.podchat.call.audio_call.PodCallAudioCallServiceManager.SSL_CONFIG;
import static com.fanap.podchat.call.audio_call.PodCallAudioCallServiceManager.TARGET_ACTIVITY;
import static com.fanap.podchat.util.ChatConstant.POD_CALL_INFO;

public class PodCallAudioCallService extends Service {


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

    PodCallAudioCallManager callManager;

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

        callManager = new PodCallAudioCallManager(this, sendingTopic, receivingTopic, sendKey, brokerAddress, ssl_key);

        if (!Util.isNullOrEmpty(sendingTopic)) {
            callManager.startStream();
        }else callManager.testAudio();
    }

    private void getIntentData(Intent intent) {

        ClientDTO client = intent.getParcelableExtra(KAFKA_CONFIG);

        if (client != null) {
            sendingTopic = client.getTopicSendAudio();
            receivingTopic = client.getTopicReceiveAudio();
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

    public void addNewCallParticipant(List<CallParticipantVO> joinedParticipants) {

        if (callManager!=null){
            callManager.addCallParticipant(joinedParticipants);
        }

    }

    public void removeCallParticipant(ArrayList<CallParticipantVO> callParticipantList) {
        if(callManager!=null)
            callManager.removeCallParticipant(callParticipantList);
    }

    public class CallBinder extends Binder {

        public PodCallAudioCallService getService() {

            return PodCallAudioCallService.this;

        }
    }

}
