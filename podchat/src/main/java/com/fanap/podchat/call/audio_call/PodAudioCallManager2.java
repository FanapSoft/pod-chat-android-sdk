package com.fanap.podchat.call.audio_call;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.kafkassl.kafkaclient.ConsumerClient;
import com.example.kafkassl.kafkaclient.ProducerClient;
import com.fanap.podchat.call.model.CallSSLData;
import com.fanap.podchat.call.result_model.StartCallResult;
import com.fanap.podchat.util.Util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class PodAudioCallManager2 implements PodAudioStreamManager2.IPodAudioListener, PodAudioStreamManager2.IPodAudioPlayerListener {


    private static final String TAG = "CHAT_SDK_CALL";

    private String SEND_KEY = "0";

    private static final String DEFAULT_TOPIC = "test" + new Date().getTime();

    private String SENDING_TOPIC = DEFAULT_TOPIC;

    private String RECEIVING_TOPIC = DEFAULT_TOPIC;

    private String BROKER_ADDRESS = "172.16.106.158:9093";

    private String SSL_CERT = "";


    private ProducerClient producerClient;
    private ConsumerClient consumerClient;

    private boolean streaming = false;

    private boolean firstByteRecorded = false;

    private boolean firstByteReceived = false;

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

        firstByteRecorded = false;

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

//        InputStream inputStream2 =
//                mContext.getResources().openRawResource(R.raw.client);
//
//        InputStream inputStream3 =
//                mContext.getResources().openRawResource(R.raw.client_p);


        OutputStream out1 = null;
//        OutputStream out2 = null;
//        OutputStream out3 = null;


        try {
            out1 = new FileOutputStream(mContext.getFilesDir() + "/ca-cert");
//            out2 = new FileOutputStream(mContext.getFilesDir() + "/client.key");
//            out3 = new FileOutputStream(mContext.getFilesDir() + "/client.pem");
            copy(inputStream1, out1);
//            copy(inputStream2, out2);
//            copy(inputStream3, out3);


            File cert = new File(mContext.getFilesDir() + "/ca-cert");
//            File key = new File(mContext.getFilesDir() + "/client.key");
//            File client = new File(mContext.getFilesDir() + "/client.pem");

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


}
