package com.example.chat.application.chatexample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fanap.podchat.example.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class CallActivity extends AppCompatActivity {

    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
    }







//        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

//
//        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
//        permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//        if (!permissionToRecordAccepted) Log.e(TAG, "NOT ACCEPTED");
////            else recordAudio();
//    }

//    private void recordAudio() {
//
//
//        if (!permissionToRecordAccepted) return;
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//        ParcelFileDescriptor[] descriptors = new ParcelFileDescriptor[0];
//        try {
//            descriptors = ParcelFileDescriptor.createPipe();
//
//            ParcelFileDescriptor parcelRead = new ParcelFileDescriptor(descriptors[0]);
//            ParcelFileDescriptor parcelWrite = new ParcelFileDescriptor(descriptors[1]);
//
//            InputStream inputStream = new ParcelFileDescriptor.AutoCloseInputStream(parcelRead);
//
//            recorder = new MediaRecorder();
//            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            recorder.setOutputFile(parcelWrite.getFileDescriptor());
//
////            recorder = new MediaRecorder();
////            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
////            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
////            recorder.setOutputFile(fileName);
////            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//
//
//            recorder.prepare();
//
//            recorder.start();
//
//
//            int read = 0;
//            byte[] data = new byte[16384];
//
//            while ((read = inputStream.read(data, 0, data.length)) != -1) {
//
//                byteArrayOutputStream.write(data, 0, read);
//
//                byte[] d2 = byteArrayOutputStream.toByteArray();
//            }
//
//            byteArrayOutputStream.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void stopStream() {
//
//        currentlySendingAudio = false;
//
//    }
//
//    private void startStream() {
//
//
//        // the audio recording options
//        final int RECORDING_RATE = 44100;
//        final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
//        final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;
//        int BUFFER_SIZE = AudioRecord.getMinBufferSize(
//                RECORDING_RATE, CHANNEL, FORMAT);
//
//        Log.i(TAG, "Starting the background thread to stream the audio data");
//
//        Thread streamThread = new Thread(() -> {
//
//            Log.d(TAG, "Creating the buffer of size " + BUFFER_SIZE);
//            byte[] buffer = new byte[BUFFER_SIZE];
////            short sData[] = new short[BufferElements2Rec];
////            int BytesPerElement = 2; // 2 bytes in 16bit format
//
////            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
////                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
////                    RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);
//
//            Log.d(TAG, "Creating the AudioRecord");
//            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                    RECORDING_RATE, CHANNEL, FORMAT, BUFFER_SIZE * 10);
//
//            Log.d(TAG, "AudioRecord recording...");
//            recorder.startRecording();
//            currentlySendingAudio = true;
//            while (currentlySendingAudio) {
//
//                int read = recorder.read(buffer, 0, buffer.length);
//
//                Log.e(TAG, "Bytes: " + Arrays.toString(buffer));
//                Log.e(TAG, "READ: " + read);
//
//            }
//
//
//        });
//        streamThread.setName("STREAM THREAD");
//        streamThread.start();
//
//
//    }
}
