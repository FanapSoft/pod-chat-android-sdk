package com.fanap.podchat.chat.call.audio_call;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.kafkassl.kafkaclient.ConsumerClient;
import com.example.kafkassl.kafkaclient.ProducerClient;
import com.fanap.podchat.chat.call.result_model.StartCallResult;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

    private boolean firstByteReceived = false;

    private AudioTestClass audioTestClass;
    private PodAudioStreamManager audioStreamManager;
    private Context mContext;

    public PodAudioCallManager(Context context) {
        audioStreamManager = new PodAudioStreamManager(context);
        sendReceiveSynchronizer = this;
        mContext = context;
        audioTestClass = new AudioTestClass();
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

    }

    public void testStream(String groupId, String sender, String receiver) {

        GROUP_ID = groupId;

        SENDING_TOPIC = sender;

        RECEIVING_TOPIC = receiver;

        startStream();

    }

    public void startCallStream(StartCallResult result) {

        SENDING_TOPIC = result.getTopicSend();

        RECEIVING_TOPIC = result.getTopicReceive();

        GROUP_ID = result.getClientId();

        BROKER_ADDRESS = result.getBrokerAddress();

        startStream();

    }

    public void startStream() {

//        startAudioCallService();

        Log.e(TAG, ">>> Start stream: Sending: " + SENDING_TOPIC
                + " Receiving: " + RECEIVING_TOPIC + " Group Id: " + GROUP_ID);

        streaming = true;

        firstByteReceived = false;

        configConsumer();

        audioStreamManager.initAudioPlayer(this);

//        try {
//
//          new Thread(()->{
//
//              AtomicBoolean r = new AtomicBoolean(true);
//
//              while (r.get()) {
//
//
//                  byte[] endBuffer = "salam".getBytes();
//
//                  String x = producerClient.produceMessege(endBuffer, "masoud", SENDING_TOPIC);
//
//                  String result = null;
//
//                  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                      result = new String(consumerClient.consumingTopic(), StandardCharsets.UTF_8);
//                      if (!"salam".equals(result) && result.length() > 0)
//                          Log.e(TAG, "Consume: " + result);
//                  }
//
//                  try {
//                      Thread.sleep(1000);
//                  } catch (InterruptedException e) {
//                      e.printStackTrace();
//                  }
//
//              }
//
//          }).start();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

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

        consumerProperties.setProperty("group.id", GROUP_ID);
        consumerProperties.setProperty("auto.offset.reset", "end");

        consumerClient = new ConsumerClient(consumerProperties, RECEIVING_TOPIC);

        consumerClient.connect();
    }

    private void configProducer() {
        final Properties producerProperties = new Properties();

        producerProperties.setProperty("bootstrap.servers", BROKER_ADDRESS); //9093 تست

        producerClient = new ProducerClient(producerProperties);

        producerClient.connect();
    }

    public void endStream() {
        streaming = false;
//        audioTestClass.stop();
        audioStreamManager.endStream();
//        audioStreamManager.stopRecording();
//        audioStreamManager.stopPlaying();

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

        configProducer();

        Runnable t = () -> {

            Log.e(TAG, "Consume from: " + RECEIVING_TOPIC);

            while (streaming) {
                audioStreamManager.playAudio(consumerClient.consumingTopic());
            }

        };

        Thread r = new Thread(t, "PLAYING");
        audioStreamManager.setPlayingThread(r);
        r.start();

    }

    @Override
    public void onByteRecorded(byte[] bytes) {


        if (!firstByteReceived) {

            firstByteReceived = true;

            sendReceiveSynchronizer.onFirstBytesRecorded();
        }

        if (streaming) {
            String x = producerClient.produceMessege(bytes, GROUP_ID, SENDING_TOPIC);
            Log.e(TAG, "SEND GID: " + GROUP_ID + " SEND TO: " + SENDING_TOPIC + " bits: " + Arrays.toString(bytes));
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
}
