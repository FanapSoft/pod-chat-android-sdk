package com.fanap.podchat.call.audio_call;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.kafkassl.kafkaclient.ConsumerClient;
import com.example.kafkassl.kafkaclient.ProducerClient;
import com.fanap.podchat.R;
import com.fanap.podchat.call.model.CallSSLData;
import com.fanap.podchat.call.result_model.StartCallResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

public class PodAudioCallManager implements PodAudioStreamManager.IPodAudioSendReceiveSync,
        PodAudioStreamManager.IPodAudioListener,
        PodAudioStreamManager.IPodAudioPlayerListener {


    private static final String TAG = "CHAT_SDK_CALL";

    private String GROUP_ID = "0";

    private static final String DEFAULT_TOPIC = "test" + new Date().getTime();

    private String SENDING_TOPIC = DEFAULT_TOPIC;

    private String RECEIVING_TOPIC = DEFAULT_TOPIC;

    private String BROKER_ADDRESS = "172.16.106.158:9093";


    private ProducerClient producerClient;
    private ConsumerClient consumerClient;

    private PodAudioStreamManager.IPodAudioSendReceiveSync sendReceiveSynchronizer;

    private boolean streaming = false;

    private boolean firstByteRecorded = false;

    private boolean firstByteReceived = false;

    private AudioTestClass audioTestClass;
    private PodAudioStreamManager audioStreamManager;
    private Context mContext;

    private CallSSLData callSSLData;
    private boolean isSSL = true;

    public PodAudioCallManager(Context context) {
        audioStreamManager = new PodAudioStreamManager(context);
        sendReceiveSynchronizer = this;
        mContext = context;
        audioTestClass = new AudioTestClass();
    }

    PodAudioCallManager(Context context, String sendingTopic, String receivingTopic, String clientId, String brokerAddress) {

        audioStreamManager = new PodAudioStreamManager(context);
        sendReceiveSynchronizer = this;
        mContext = context;
        audioTestClass = new AudioTestClass();

        this.GROUP_ID = clientId;
        this.SENDING_TOPIC = sendingTopic;
        this.RECEIVING_TOPIC = receivingTopic;
        this.BROKER_ADDRESS = brokerAddress;
    }

    public void testAudio() {


        streaming = true;

        audioStreamManager.initAudioPlayer(new PodAudioStreamManager.IPodAudioPlayerListener() {
            @Override
            public void onPlayStopped() {

            }

            @Override
            public void onAudioPlayError(String cause) {

            }

            @Override
            public void onPlayerReady() {

                audioStreamManager.recordAudio(new PodAudioStreamManager.IPodAudioListener() {
                    @Override
                    public void onByteRecorded(byte[] bytes) {

                        audioStreamManager.playAudio(bytes);
                    }

                    @Override
                    public void onRecordStopped() {

                    }

                    @Override
                    public void onAudioRecordError(String cause) {

                    }

                    @Override
                    public void onRecordRestarted() {

                    }
                });

            }
        });
//        new Thread(() -> audioTestClass.start(mContext)).start();

//        startStream();
    }

    public void testStream(String groupId, String sender, String receiver) {

        GROUP_ID = groupId;

        SENDING_TOPIC = sender;

        RECEIVING_TOPIC = receiver;

        startStream();

    }

    public void startCallStream(StartCallResult result) {

        SENDING_TOPIC = result.getClientDTO().getTopicSend();

        RECEIVING_TOPIC = result.getClientDTO().getTopicReceive();

        GROUP_ID = result.getClientDTO().getClientId();

        BROKER_ADDRESS = result.getClientDTO().getBrokerAddress();

        startStream();

    }

    public void startStream() {


        Log.e(TAG, ">>> Start stream: Sending: " + SENDING_TOPIC
                + " Receiving: " + RECEIVING_TOPIC + " Group Id: " + GROUP_ID);

        streaming = true;

        firstByteRecorded = false;

        configConsumer();

        audioStreamManager.initAudioPlayer(this);

    }

    private void startAudioCallService() {
        Intent i = new Intent(mContext, AudioCallService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(i);
        } else {
            mContext.startService(i);
        }
    }

    private void configConsumer() {

        final Properties consumerProperties = new Properties();

        consumerProperties.setProperty("bootstrap.servers", BROKER_ADDRESS); //9093 تست

        callSSLData = readFiles();

        if (callSSLData != null && isSSL) {

            consumerProperties.setProperty("security.protocol", "SASL_SSL");
            consumerProperties.setProperty("sasl.mechanisms", "PLAIN");
            consumerProperties.setProperty("sasl.username", "rrrr");
            consumerProperties.setProperty("sasl.password", "rrrr");
            consumerProperties.setProperty("ssl.ca.location", callSSLData.getCert().getAbsolutePath());
            consumerProperties.setProperty("ssl.certificate.location", callSSLData.getClient().getAbsolutePath());
            consumerProperties.setProperty("ssl.key.location", callSSLData.getKey().getAbsolutePath());
            consumerProperties.setProperty("ssl.key.password", "masoud");

        }

        consumerProperties.setProperty("group.id", GROUP_ID);

        consumerProperties.setProperty("auto.offset.reset", "end");

        consumerClient = new ConsumerClient(consumerProperties, RECEIVING_TOPIC);

        consumerClient.connect();
    }

    private void configProducer() {

        final Properties producerProperties = new Properties();

        producerProperties.setProperty("bootstrap.servers", BROKER_ADDRESS);

        if (callSSLData != null && isSSL) {

            producerProperties.setProperty("security.protocol", "SASL_SSL");
            producerProperties.setProperty("sasl.mechanisms", "PLAIN");
            producerProperties.setProperty("sasl.username", "rrrr");
            producerProperties.setProperty("sasl.password", "rrrr");
            producerProperties.setProperty("ssl.ca.location", callSSLData.getCert().getAbsolutePath());
            producerProperties.setProperty("ssl.certificate.location", callSSLData.getClient().getAbsolutePath());
            producerProperties.setProperty("ssl.key.location", callSSLData.getKey().getAbsolutePath());
            producerProperties.setProperty("ssl.key.password", "masoud");

        }

        producerClient = new ProducerClient(producerProperties);

        producerClient.connect();
    }

    private CallSSLData readFiles() {


        InputStream inputStream1 =
                mContext.getResources().openRawResource(R.raw.cacert);

        InputStream inputStream2 =
                mContext.getResources().openRawResource(R.raw.client);

        InputStream inputStream3 =
                mContext.getResources().openRawResource(R.raw.client_p);


        OutputStream out1 = null;
        OutputStream out2 = null;
        OutputStream out3 = null;


        try {
            out1 = new FileOutputStream(mContext.getFilesDir() + "/ca-cert");
            out2 = new FileOutputStream(mContext.getFilesDir() + "/client.key");
            out3 = new FileOutputStream(mContext.getFilesDir() + "/client.pem");
            copy(inputStream1, out1);
            copy(inputStream2, out2);
            copy(inputStream3, out3);


            File cert = new File(mContext.getFilesDir() + "/ca-cert");
            File key = new File(mContext.getFilesDir() + "/client.key");
            File client = new File(mContext.getFilesDir() + "/client.pem");

            if (cert.exists() && key.exists() && client.exists()) {

                return new CallSSLData(cert, key, client);
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
//        stopAudioCallService();
    }

    private void stopAudioCallService() {
        Intent i = new Intent(mContext, AudioCallService.class);
        mContext.stopService(i);
    }

    @Override
    public void onConsumerReady() {

        audioStreamManager.recordAudio(this);

    }

    @Override
    public void onFirstBytesRecorded() {

    }

    @Override
    public void onByteRecorded(byte[] bytes) {


        if (!firstByteRecorded) {
            firstByteRecorded = true;
            configProducer();
        }

        //start streaming. if stream started but a disconnection occurred, stop producing until it starts again.
        if (streaming) {

            byte[] consumedBytes = consumerClient.consumingTopic();

            if (consumedBytes.length > 0)
                firstByteReceived = true;

            if (!firstByteReceived) {
                producerClient.produceMessege(bytes, GROUP_ID, SENDING_TOPIC);
            Log.e(TAG, "START STATE SEND GID: " + GROUP_ID + " SEND TO: " + SENDING_TOPIC + " bits: " + Arrays.toString(bytes));
            }

            if (consumedBytes.length > 0) {
                producerClient.produceMessege(bytes, GROUP_ID, SENDING_TOPIC);
                Log.e(TAG, "RUNNING STATE SEND GID: " + GROUP_ID + " SEND TO: " + SENDING_TOPIC + " bits: " + Arrays.toString(bytes));
            }


            audioStreamManager.playAudio(consumedBytes);



//            byte[] consumedBytes = consumerClient.consumingTopic();
//
//            audioStreamManager.playAudio(consumedBytes);
//
//            if (consumedBytes.length > 0) {
//                String x = producerClient.produceMessege(bytes, GROUP_ID, SENDING_TOPIC);
//                Log.e(TAG, "SEND GID: " + GROUP_ID + " SEND TO: " + SENDING_TOPIC + " bits: " + Arrays.toString(bytes));
//            }else {
//                Log.e(TAG,"Waiting for consumer to be ready");
//            }

        }


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

        sendReceiveSynchronizer.onConsumerReady();

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
