package com.fanap.podchat.call.audio_call;

import android.content.Context;
import android.util.Log;

import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PodCallAudioCallManager implements PodCallAudioCallStreamManager.IPodAudioListener, PodCallAudioCallStreamManager.IPodAudioPlayerListener {


    private static final String TAG = "CHAT_SDK_CALL";

    private String SEND_KEY = "0";

    private static final String DEFAULT_TOPIC = "test" + new Date().getTime();

    private String SENDING_TOPIC = DEFAULT_TOPIC;

    private String RECEIVING_TOPIC = DEFAULT_TOPIC;

    private String BROKER_ADDRESS = "46.32.6.186:9092";

    private String SSL_CERT = "";

    private boolean streaming = false;

    private AudioTestClass audioTestClass;
    private PodCallAudioCallStreamManager audioStreamManager;
    private Context mContext;

    private boolean isSSL = true;

    public PodCallAudioCallManager(Context context) {
        audioStreamManager = new PodCallAudioCallStreamManager(context);

        mContext = context;
        audioTestClass = new AudioTestClass();
    }

    PodCallAudioCallManager(Context context, String sendingTopic, String receivingTopic, String sendKey, String brokerAddress, String ssl_cert) {

        audioStreamManager = new PodCallAudioCallStreamManager(context);

        mContext = context;
        audioTestClass = new AudioTestClass();

        this.SEND_KEY = sendKey;
        this.SENDING_TOPIC = sendingTopic;
        this.RECEIVING_TOPIC = receivingTopic;
        this.BROKER_ADDRESS = brokerAddress;
        this.SSL_CERT = ssl_cert;
    }

    public void startStream() {


        Log.e(TAG, ">>> Start stream: Sending: " + SENDING_TOPIC
                + " Receiving: " + RECEIVING_TOPIC + " Group Id: " + SEND_KEY);

        streaming = true;


        audioStreamManager.initAudioPlayer(
                SSL_CERT,
                BROKER_ADDRESS,
                SEND_KEY,
                RECEIVING_TOPIC,
                this);

    }


    public void endStream() {
        streaming = false;
        audioTestClass.stop();
        audioStreamManager.endStream();
    }


    @Override
    public void onByteRecorded(byte[] bytes) {


    }

    @Override
    public void onRecordStopped() {
        logError("Record STOPPED");
        streaming = false;
    }

    private void logError(String s) {
        Util.logExceptionWithDivider(TAG, s);
    }

    @Override
    public void onAudioRecordError(String cause) {
        logError("Record error " + cause);
        streaming = false;
    }

    @Override
    public void onRecordRestarted() {
        streaming = true;
    }

    @Override
    public void onRecordStarted() {

    }

    @Override
    public void onRecorderImportantEvent(String info) {
        logInfo(info);
    }

    private void logInfo(String info) {
        Util.logWithDivider(TAG, info);
    }

    @Override
    public void onPlayStopped() {
        logError("PLAYING STOPPED");
        streaming = false;
    }

    @Override
    public void onAudioPlayError(String cause) {
        logError("PLAY ERROR: " + cause);
        streaming = false;
    }

    @Override
    public void onPlayerReady() {

        audioStreamManager.recordAudio(this,
                SENDING_TOPIC);

    }

    @Override
    public void onPlayerImportantEvent(String info) {
        logInfo(info);
    }

    public void switchAudioSpeakerState(boolean speakerOn) {

        audioStreamManager.switchSpeakerState(speakerOn);
    }

    public void switchAudioMuteState(boolean isMute) {

        audioStreamManager.switchMuteState(isMute);
    }

    public void setSSL(boolean enableSSL) {
        isSSL = enableSSL;
    }


    public void testAudio() {
        audioTestClass.start(mContext);
    }

    public void addCallParticipant(List<CallParticipantVO> joinedParticipants) {
        if (audioStreamManager != null) {
            audioStreamManager.addCallParticipant(joinedParticipants);
        }
    }


    public void removeCallParticipant(ArrayList<CallParticipantVO> callParticipantsList) {
        if (audioStreamManager != null) {
            audioStreamManager.removeCallParticipant(callParticipantsList);
        }
    }
}
