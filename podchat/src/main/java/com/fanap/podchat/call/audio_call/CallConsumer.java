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
import java.util.Date;
import java.util.Properties;

public class CallConsumer implements Runnable {

    // Sample rate must be one supported by Opus.
    private static final int SAMPLE_RATE = 16000;

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

    private ConsumerClient consumer;

    private CallSSLData callSSLData;


    //consumer properties

    private String brokerAddress;
    private String sendKey;
    private String receivingTopic;

    private IConsumer consumerCallback;

    private boolean firstBytesReceived;


    CallConsumer(CallSSLData sslData,
                 String brokerAddress,
                 String sendKey,
                 String receivingTopic,
                 IConsumer consumerCallback) {


        this.consumerCallback = consumerCallback;

        consumerCallback.onConsumerImportantEvent("Initial Consumer for topic: " + receivingTopic + " on thread: " + Thread.currentThread().getName());

        callSSLData = sslData;

        this.brokerAddress = brokerAddress;
        this.sendKey = sendKey;
        this.receivingTopic = receivingTopic;

        hasBuiltInAEC = AcousticEchoCanceler.isAvailable();

        connectConsumerClient();

    }

    public CallConsumer() {
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

    private OpusDecoder initDecoder() {
        if (decoder == null) {
            decoder = new OpusDecoder();
            decoder.init(SAMPLE_RATE, NUM_CHANNELS);
        }
        return decoder;
    }

    private void playAudio(OpusDecoder decoder) {

        short[] outputBuffer = new short[FRAME_SIZE * NUM_CHANNELS];

        consumerCallback.onConsumerImportantEvent("Running Consumer with Topic: " + receivingTopic + " on thread " + Thread.currentThread().getName());

        try {

            while (playing) {

                byte[] consumedBytes = consumer.consumingTopic();

                if (consumedBytes == null || consumedBytes.length == 0) continue;

                if (!firstBytesReceived) {
                    callOnConsumingStarted();
                }

                int decoded = decoder.decode(consumedBytes, outputBuffer, FRAME_SIZE);

                audioTrack.write(outputBuffer, 0, decoded * NUM_CHANNELS);

//                if (hasBuiltInAEC)
//                    audioTrack.write(outputBuffer, 0, decoded * NUM_CHANNELS);
//                else
//                    playWithSpeexAEC(outputBuffer, decoded);


            }

        } catch (Exception e) {
            consumerCallback.onConsumerException("Play Error: " + e.getMessage());
        }
    }

    private void callOnConsumingStarted() {
        firstBytesReceived = true;

    }

    private void playWithSpeexAEC(short[] outputBuffer, int decoded) {

        short[] echoCancelled = echoCanceller.capture(outputBuffer);

        audioTrack.write(echoCancelled, 0, decoded * NUM_CHANNELS);

        echoCanceller.playback(outputBuffer);

    }

    void stopPlaying() {
        try {
            playing = false;
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
        } catch (IllegalStateException e) {
            consumerCallback.onConsumerException("Player Stop Exception: " + e.getMessage());
        }
    }

    void stopPlayingAndReleaseDecoder() {
        try {
            playing = false;
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
            if (decoder != null)
                decoder.close();
//            if (echoCanceller != null)
//                echoCanceller.closeEcho();
        } catch (IllegalStateException e) {
            consumerCallback.onConsumerException("Player Stop And Release Decoder Exception: " + e.getMessage());
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

        consumerProperties.setProperty("group.id", sendKey + new Date().getTime());

        consumerProperties.setProperty("auto.offset.reset", "end");

        consumer = new ConsumerClient(consumerProperties, receivingTopic);

        consumer.connect();
    }

    public IConsumer getConsumerCallback() {
        return consumerCallback;
    }

    public interface IConsumer {

        void onFirstBytesReceived();

        void onConsumerException(String cause);

        void onConsumerImportantEvent(String info);

    }

}
