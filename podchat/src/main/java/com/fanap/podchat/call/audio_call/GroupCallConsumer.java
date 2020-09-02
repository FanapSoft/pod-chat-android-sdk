package com.fanap.podchat.call.audio_call;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.audiofx.AcousticEchoCanceler;
import android.util.Log;

import com.example.kafkassl.kafkaclient.ConsumerClient;
import com.fanap.podchat.call.codec.opus.OpusDecoder;
import com.fanap.podchat.call.codec.speexdsp.EchoCanceller;
import com.fanap.podchat.call.model.CallSSLData;

import java.util.ArrayList;
import java.util.Properties;

public class GroupCallConsumer extends CallConsumer implements Runnable {

    // Sample rate must be one supported by Opus.
    private static final int SAMPLE_RATE = 8000;

    // Number of samples per frame is not arbitrary,
    // it must match one of the predefined values, specified in the standard.
    private static final int FRAME_SIZE = 960;

    // 1 or 2
    private static final int NUM_CHANNELS = 1;
    private static final String TAG = "AUDIO_RECORDER";

    private boolean playing = false;
    private EchoCanceller echoCanceller;
    private AudioTrack audioTrack;

    private boolean hasBuiltInAEC;

    private OpusDecoder decoder;

    private ArrayList<ConsumerClient> consumers;

    private CallSSLData callSSLData;

    //consumer properties

    private String brokerAddress;
    private String sendKey;
    private CallConsumer.IConsumer consumerCallback;
    private boolean firstBytesReceived;
    private ArrayList<String> receivingTopics;



    GroupCallConsumer(CallSSLData sslData,
                      String brokerAddress,
                      String sendKey,
                      ArrayList<String> receivingTopics,
                      CallConsumer.IConsumer consumerCallback) {

        callSSLData = sslData;

        this.consumerCallback = consumerCallback;

        this.brokerAddress = brokerAddress;
        this.sendKey = sendKey;

        this.receivingTopics = receivingTopics;

        hasBuiltInAEC = AcousticEchoCanceler.isAvailable();

        connectConsumerClient();

    }

    @Override
    public void run() {


        int minBufSize = initAudioTracker();

        // init opus decoder
        OpusDecoder decoder = initDecoder();

        if (!hasBuiltInAEC) {
            if (echoCanceller == null) {
                echoCanceller = new EchoCanceller();
                echoCanceller.openEcho(SAMPLE_RATE, minBufSize, 1024);
            }
        }


        playAudio(decoder);
    }

    private OpusDecoder initDecoder() {
        if (decoder == null) {
            decoder = new OpusDecoder();
            decoder.init(SAMPLE_RATE, NUM_CHANNELS);
        }
        return decoder;
    }

    private int initAudioTracker() {

        int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);


        if (audioTrack == null)
            // init audio track
            audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                    SAMPLE_RATE,
                    NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufSize,
                    AudioTrack.MODE_STREAM);

        audioTrack.play();

        playing = true;

        return minBufSize;
    }

    private void playAudio(OpusDecoder decoder) {

        try {

            while (playing) {


                for (ConsumerClient consumer : consumers) {

                    byte[] consumedBytes = consumer.consumingTopic();

                    if (consumedBytes == null || consumedBytes.length == 0) continue;

                    if (!firstBytesReceived) {
                        callOnConsumingStarted();
                    }
                    new Thread(() -> play(decoder, consumedBytes)).start();
                }
            }

        } catch (Exception e) {
            Log.wtf(TAG, e);
        }
    }

    private synchronized void play(OpusDecoder decoder, byte[] consumedBytes) {

        short[] outputBuffer = new short[FRAME_SIZE * NUM_CHANNELS];

        int decoded = decoder.decode(consumedBytes, outputBuffer, FRAME_SIZE);

        Log.v(TAG, "Decoded back " + decoded * NUM_CHANNELS * 2 + " bytes");

        if (hasBuiltInAEC)
            audioTrack.write(outputBuffer, 0, decoded * NUM_CHANNELS);
        else
            playWithSpeexAEC(outputBuffer, decoded);

    }

    private void callOnConsumingStarted() {
        firstBytesReceived = true;
        consumerCallback.onFirstBytesReceived();
    }



    private void playWithSpeexAEC(short[] outputBuffer, int decoded) {
        audioTrack.write(echoCanceller.capture(outputBuffer), 0, decoded * NUM_CHANNELS);

        echoCanceller.playback(outputBuffer);
    }

    void stopPlaying() {
        try {

            if (audioTrack != null && audioTrack.getState() != AudioTrack.STATE_UNINITIALIZED) {
                if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {

                    try {
                        audioTrack.stop();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
                audioTrack.release();
            }
            playing = false;
//            if (echoCanceller != null)
//                echoCanceller.closeEcho();
//            if (decoder != null)
//                decoder.close();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void connectConsumerClient() {

        final Properties consumerProperties = new Properties();

        consumerProperties.setProperty("bootstrap.servers", brokerAddress); //9093 تست

        if (callSSLData != null) {

            consumerProperties.setProperty("security.protocol", "SASL_SSL");
            consumerProperties.setProperty("sasl.mechanisms", "PLAIN");
            consumerProperties.setProperty("sasl.username", "rrrr");
            consumerProperties.setProperty("sasl.password", "rrrr");
            consumerProperties.setProperty("ssl.ca.location", callSSLData.getCert().getAbsolutePath());
            consumerProperties.setProperty("ssl.key.password", "masoud");

        }

        consumerProperties.setProperty("auto.commit.enable", "false");

        consumerProperties.setProperty("group.id", sendKey);

        consumerProperties.setProperty("auto.offset.reset", "end");


        for (String receivingTopic :
                receivingTopics) {

            ConsumerClient consumer = new ConsumerClient(consumerProperties, receivingTopic);

            consumers.add(consumer);

            consumer.connect();

        }

    }


}
