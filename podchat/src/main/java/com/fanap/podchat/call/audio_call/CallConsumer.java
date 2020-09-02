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

import java.util.Properties;

public class CallConsumer implements Runnable {

    // Sample rate must be one supported by Opus.
    private static final int SAMPLE_RATE = 12000;

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

        callSSLData = sslData;

        this.consumerCallback = consumerCallback;

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

        short[] outputBuffer = new short[FRAME_SIZE * NUM_CHANNELS];

        try {

            while (playing) {

                byte[] consumedBytes = consumer.consumingTopic();

                if (consumedBytes == null || consumedBytes.length == 0) continue;

                if (!firstBytesReceived) {
                    callOnConsumingStarted();
                }

                int decoded = decoder.decode(consumedBytes, outputBuffer, FRAME_SIZE);

                Log.v(TAG, "Decoded back " + decoded * NUM_CHANNELS * 2 + " bytes");

                if (hasBuiltInAEC)
                    audioTrack.write(outputBuffer, 0, decoded * NUM_CHANNELS);
                else
                    playWithSpeexAEC(outputBuffer, decoded);


            }

        } catch (Exception e) {
            Log.wtf(TAG, e);
        }
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

        consumer = new ConsumerClient(consumerProperties, receivingTopic);

        consumer.connect();
    }

    public void reStart() {

    }


    public interface IConsumer {

        void onFirstBytesReceived();


    }

}
