package com.fanap.podchat.call.audio_call;

import android.content.Context;
import android.util.Log;

import com.fanap.podchat.call.model.CallSSLData;
import com.fanap.podchat.util.Util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class PodAudioCallManager2 implements PodAudioStreamManager2.IPodAudioListener, PodAudioStreamManager2.IPodAudioPlayerListener {


    private static final String TAG = "CHAT_SDK_CALL";

    private String SEND_KEY = "0";

    private static final String DEFAULT_TOPIC = "test" + new Date().getTime();

    private String SENDING_TOPIC = DEFAULT_TOPIC;

    private String RECEIVING_TOPIC = DEFAULT_TOPIC;

    private String BROKER_ADDRESS = "46.32.6.186:9092";

    private String SSL_CERT = "";

    private boolean streaming = false;

    private AudioTestClass audioTestClass;
    private PodAudioStreamManager2 audioStreamManager;
    private Context mContext;

    private CallSSLData callSSLData;

    private boolean isSSL = true;

    public PodAudioCallManager2(Context context) {
        audioStreamManager = new PodAudioStreamManager2(context);

        mContext = context;
        audioTestClass = new AudioTestClass();
    }

    PodAudioCallManager2(Context context, String sendingTopic, String receivingTopic, String sendKey, String brokerAddress, String ssl_cert) {

        audioStreamManager = new PodAudioStreamManager2(context);

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

        callSSLData = readFiles();

        audioStreamManager.initAudioPlayer(callSSLData,
                BROKER_ADDRESS,
                SEND_KEY,
                RECEIVING_TOPIC,
                this);

    }


    private CallSSLData readFiles() {

        if (Util.isNullOrEmpty(SSL_CERT)) return null;

        InputStream inputStream1 =
                new ByteArrayInputStream(SSL_CERT.getBytes());

        OutputStream out1 = null;

        try {
            out1 = new FileOutputStream(mContext.getFilesDir() + "/ca-cert");
            copy(inputStream1, out1);


            File cert = new File(mContext.getFilesDir() + "/ca-cert");

            if (cert.exists()) {

                return new CallSSLData(cert, null, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    private void copy(InputStream inputStream1, OutputStream out1) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream1.read(buffer)) != -1) {
            out1.write(buffer, 0, read);
        }
        inputStream1.close();
        inputStream1 = null;
        out1.flush();
        out1.close();
        out1 = null;
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
        Log.e(TAG, "STOPPED");
        streaming = false;
    }

    @Override
    public void onAudioRecordError(String cause) {
        Log.e(TAG, "ERROR " + cause);
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
    public void onPlayStopped() {
        Log.e(TAG, "PLAYING STOPPED");
        streaming = false;
    }

    @Override
    public void onAudioPlayError(String cause) {
        Log.e(TAG, "PLAY ERROR: " + cause);
        streaming = false;
    }

    @Override
    public void onPlayerReady() {

        audioStreamManager.recordAudio(this,
                SENDING_TOPIC);

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
}
