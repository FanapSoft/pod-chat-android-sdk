package com.fanap.podchat.call.audio_call;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.util.Log;

import com.example.kafkassl.kafkaclient.ProducerClient;
import com.fanap.podchat.call.codec.opus.OpusEncoder;
import com.fanap.podchat.call.model.CallSSLData;
import com.fanap.podchat.util.Util;

import java.util.Arrays;
import java.util.Properties;

public class CallProducer implements Runnable {


    private AutomaticGainControl agc;
    private NoiseSuppressor ns;
    private AcousticEchoCanceler aec;


    boolean isRecording = false;


    // Sample rate must be one supported by Opus.
    static final int SAMPLE_RATE = 16000;

    // Number of samples per frame is not arbitrary,
    // it must match one of the predefined values, specified in the standard.
    static final int FRAME_SIZE = 960;

    // 1 or 2
    static final int NUM_CHANNELS = 1;
    private static final String TAG = "AUDIO_RECORDER";

    private IRecordThread callback;

    private AudioRecord recorder;

    OpusEncoder encoder;

    private ProducerClient producerClient;
    private CallSSLData callSSLData;
    private String brokerAddress;
    private boolean firstByteRecorded = false;
    private boolean firstByteReceived = false;
    private String sendKey;
    private String sendingTopic;
    private boolean consuming;
    private boolean isHeadset;


    public CallProducer(IRecordThread callback, CallSSLData sslData,
                        String brokerAddress,
                        String sendKey,
                        String sendingTopic,
                        boolean headsetInitialState) {
        this.callback = callback;
        this.callSSLData = sslData;
        this.brokerAddress = brokerAddress;
        this.sendKey = sendKey;
        this.sendingTopic = sendingTopic;
        isHeadset = headsetInitialState;

        connectProducer();

    }

    @Override
    public void run() {

        initial();

        record();

    }

    private void initial() {

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);

        int minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                NUM_CHANNELS == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);

        // initialize audio recorder
        if (recorder == null)
            recorder = new AudioRecord(isHeadset ? MediaRecorder.AudioSource.MIC : MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufSize);

        setConfigs();

        if (encoder == null) {
            // init opus encoder
            encoder = new OpusEncoder();
            encoder.init(SAMPLE_RATE, NUM_CHANNELS, OpusEncoder.OPUS_APPLICATION_VOIP);
        }

        // start
        recorder.startRecording();
        isRecording = true;
        callback.onRecorderSet();
    }

    private void record() {

        short[] inBuffer = new short[FRAME_SIZE * NUM_CHANNELS];

        byte[] encodeBufferTemp = new byte[1024];

        callback.onImportantEvent("Producer Start for topic: " + sendingTopic + " on Thread: " + Thread.currentThread().getName());

        try {

            while (isRecording) {

                int to_read = inBuffer.length;
                int offset = 0;
                while (to_read > 0) {
                    int read = recorder.read(inBuffer, offset, to_read);
                    if (read < 0) {
                        callback.onImportantEvent("Recorder has nothing to read => " + read);
                        break;
                    }
                    to_read -= read;
                    offset += read;
                }

                if (!isRecording) break;

                int encoded = encoder.encode(inBuffer, FRAME_SIZE, encodeBufferTemp);

                byte[] encodedBuffer = Arrays.copyOf(encodeBufferTemp, encoded);

                producerClient.produceMessege(encodedBuffer, sendKey, sendingTopic);

            }
        } catch (Exception e) {
            stopRecording();
            callback.onRecorderError("Error in recording: " + e.getMessage());
        }
    }


    void stopRecording() {
        try {

            isRecording = false;
            if (recorder != null && recorder.getState() != AudioRecord.STATE_UNINITIALIZED) {

                if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {

                    try {
                        recorder.stop();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }

                recorder.release();
            }

            if (agc != null) {
                agc.release();
                agc = null;
            }
            if (ns != null) {
                ns.release();
                ns = null;
            }
            if (aec != null) {
                aec.release();
                aec = null;
            }
//            if (encoder != null)
//                encoder.close();
        } catch (IllegalStateException e) {
            callback.onRecorderError("Error in stopRecording: " + e.getMessage());
        }
    }

    private void setConfigs() {
        try {
            if (AutomaticGainControl.isAvailable()) {
                agc = AutomaticGainControl.create(recorder.getAudioSessionId());
                if (agc != null)
                    agc.setEnabled(true);
            } else {
                callback.onImportantEvent("AutomaticGainControl is not available on this device :(");
            }
        } catch (Throwable x) {
            callback.onImportantEvent("error creating AutomaticGainControl: " + x.getMessage());
        }


        try {
            if (NoiseSuppressor.isAvailable()) {
                ns = NoiseSuppressor.create(recorder.getAudioSessionId());
                if (ns != null) {
                    ns.setEnabled(true);
                }
            } else {
                callback.onImportantEvent("NoiseSuppressor is not available on this device :(");
            }
        } catch (Throwable x) {
            callback.onImportantEvent("error creating NoiseSuppressor: " + x);
        }

        try {
            if (AcousticEchoCanceler.isAvailable()) {
                aec = AcousticEchoCanceler.create(recorder.getAudioSessionId());
                if (aec != null) {
                    aec.setEnabled(true);
                }
            } else {
                callback.onImportantEvent("AcousticEchoCanceler is not available on this device");
            }
        } catch (Throwable x) {
            callback.onImportantEvent("error creating AcousticEchoCanceler: " + x);
        }
    }

    private void connectProducer() {

        final Properties producerProperties = new Properties();

        producerProperties.setProperty("bootstrap.servers", brokerAddress);

        if (callSSLData != null) {

            producerProperties.setProperty("security.protocol", "SASL_SSL");
            producerProperties.setProperty("sasl.mechanisms", "PLAIN");
            producerProperties.setProperty("sasl.username", "rrrr");
            producerProperties.setProperty("sasl.password", "rrrr");
            producerProperties.setProperty("ssl.ca.location", callSSLData.getCert().getAbsolutePath());
            producerProperties.setProperty("ssl.key.password", "masoud");

        }

        producerClient = new ProducerClient(producerProperties);

        producerClient.connect();
    }

    public void updateHeadsetState(boolean isHeadset) {
        this.isHeadset = isHeadset;
//        recorder.stop();
        isRecording = false;
        recorder = null;
    }


    public interface IRecordThread {

        void onRecord(byte[] encoded);

        void onRecorderSet();

        void onRecorderError(String cause);

        void onImportantEvent(String info);

    }
}
