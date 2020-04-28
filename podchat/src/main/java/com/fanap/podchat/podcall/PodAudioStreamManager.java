package com.fanap.podchat.podcall;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;

import com.fanap.podchat.util.Permission;

public class PodAudioStreamManager {

    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    boolean hasPermission = false;


    private IPodAudioListener callback;


    public void recordAudio(@NonNull IPodAudioListener listener) {

        this.callback = listener;

        if (!hasPermission) {

            callback.onErrorOccurred("Need Audio Record Permission");

            return;
        }


//        int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
//                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
        int BytesPerElement = 2; // 2 bytes in 16bit format

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        isRecording = true;


        recordingThread = new Thread(() -> generateBytes(BufferElements2Rec), "AudioRecorder Thread");
        recordingThread.start();
    }


    public void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
        }

        if (callback != null) callback.onRecordStopped();

    }

    public void checkPermission(Activity activity) {
        hasPermission = Permission.Check_RECORD_AUDIO(activity);
    }

    private void generateBytes(int bufferElements2Rec) {
        // Write the output audio in byte

        short[] sData = new short[bufferElements2Rec];


        while (isRecording) {

            // gets the voice output from microphone to byte format
            recorder.read(sData, 0, bufferElements2Rec);

            // // writes the data to file from buffer
            // // stores the voice buffer
            byte[] bytes = short2byte(sData);

            callback.onByteRecorded(bytes);

        }
    }


    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }


    public interface IPodAudioListener {

        void onByteRecorded(byte[] bytes);

        void onRecordStopped();

        void onErrorOccurred(String cause);

    }


}
