package com.fanap.podchat.call.audio_call;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.NonNull;

import com.fanap.podchat.call.model.CallSSLData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class PodAudioStreamManager2 implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, RecordThread.IRecordThread, CallConsumer.IConsumer, CallProducer.IRecordThread {

    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    private boolean isRecording = false;
    private boolean isPlaying = false;

    private boolean isSpeakerOn = false;
    private boolean isMute = false;
    private boolean isWiredHeadset = false;
    private boolean isBtHeadset = false;
    private boolean haveAudioFocus;

    private SensorManager mSensorManager = null;
    private Sensor mProximitySensor = null;

    private static final int PROXIMITY_SCREEN_OFF_WAKE_LOCK = 32;
    private PowerManager.WakeLock proximityWakelock;
    private PowerManager.WakeLock cpuWakeLock;
    private boolean isProximityNear;

    private AudioManager audioManager;
    private BluetoothAdapter bluetoothAdapter;

    private Context mContext;

    private IPodAudioListener recordCallback;

    private IPodAudioPlayerListener playerCallback;

    private BroadcastReceiver headsetReceiver;


    private CallConsumer callConsumer;

    private CallProducer callProducer;

    private CallSSLData callSSL;

    Thread consumerThread;

    Thread producerThread;

    String brokerAddress;
    String sendKey;
    String receivingTopic;
    String sendingTopic;
    List<String> topicsList;

    boolean isGroupCall;


    PodAudioStreamManager2(Context context) {

        this.mContext = context;

        audioManager = (AudioManager) mContext.getSystemService(AUDIO_SERVICE);

        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);

        if (audioManager != null) {
            setupAudioManager();
            setupBTAdapter();
        }

        if (mSensorManager != null) {
            setupProximity();
        }

        setupCPUWakeLock();

        setupHeadsetReceiver();
    }

    private void setupCPUWakeLock() {

        cpuWakeLock = ((PowerManager) mContext.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.fanap.podchat:audio_stream");


    }

    private void setupBTAdapter() {

        bluetoothAdapter = audioManager.isBluetoothScoAvailableOffCall() ? BluetoothAdapter.getDefaultAdapter() : null;

    }

    private void setupHeadsetReceiver() {
        headsetReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                updateHeadsetState(intent);
            }
        };

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ACTION_HEADSET_PLUG);

        if (bluetoothAdapter != null) {
            intentFilter.addAction(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
            intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
        }


        mContext.registerReceiver(headsetReceiver, intentFilter);
    }

    private void updateHeadsetState(Intent intent) {

        if (ACTION_HEADSET_PLUG.equals(intent.getAction())) {

            isWiredHeadset = intent.getIntExtra("state", 0) == 1;

            if (isWiredHeadset && proximityWakelock != null && proximityWakelock.isHeld()) {
                proximityWakelock.release();
            }

            if (isWiredHeadset) {
                audioManager.setWiredHeadsetOn(true);
            } else {
                audioManager.setWiredHeadsetOn(false);
            }


        } else if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(intent.getAction())) {

            boolean connected = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, BluetoothProfile.STATE_DISCONNECTED) == BluetoothProfile.STATE_CONNECTED;

            if (connected != isBtHeadset) {

                isBtHeadset = connected;

                if (connected) {
                    switchSpeakerState(false);
                    audioManager.setBluetoothScoOn(true);
                } else {
                    audioManager.setBluetoothScoOn(false);
                }

            }

        } else if (AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED.equals(intent.getAction())) {


            int state = intent.getIntExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, AudioManager.SCO_AUDIO_STATE_DISCONNECTED);

            if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
                audioManager.setSpeakerphoneOn(false);
                audioManager.setBluetoothScoOn(true);
            }


        }

//        boolean newState = state == 1;

//        if (newState != isWiredHeadset) {
//
//            isWiredHeadset = newState;
//
//            if (isWiredHeadset && proximityWakelock != null && proximityWakelock.isHeld()) {
//                proximityWakelock.release();
//            }
//
//            if (isWiredHeadset) {
//                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//                audioManager.startBluetoothSco();
//                if (audioManager.isWiredHeadsetOn()) {
//                    audioManager.setWiredHeadsetOn(true);
//
//                } else if (BluetoothProfile.STATE_CONNECTED)
//                    audioManager.setBluetoothScoOn(true);
//            } else {
//                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
//                audioManager.stopBluetoothSco();
//                audioManager.setBluetoothScoOn(false);
//                audioManager.setSpeakerphoneOn(false);
//            }
//
//
////            resetRecorder();
//        }
    }

    private void setupProximity() {
        try {
            mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            proximityWakelock = ((PowerManager) mContext.getSystemService(Context.POWER_SERVICE)).newWakeLock(PROXIMITY_SCREEN_OFF_WAKE_LOCK, "com.fanap.podchat:audio_stream");
        } catch (Exception ignored) {
        }
    }

    private void setupAudioManager() {
        isWiredHeadset = audioManager.isWiredHeadsetOn();
        audioManager.setSpeakerphoneOn(false);
        audioManager.setMicrophoneMute(false);
        audioManager.requestAudioFocus(this, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN);
    }

    private void updateAudioManager() {
        isWiredHeadset = audioManager.isWiredHeadsetOn();
        audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
    }

    void recordAudio(@NonNull IPodAudioListener listener, String sendingTopic) {

        this.recordCallback = listener;
        this.sendingTopic = sendingTopic;

        if (isRecording) {
            stopRecording();
        }

        runWakeLock();

        updateAudioManager();

        callProducer = new CallProducer(this, callSSL, brokerAddress, sendKey, sendingTopic, isWiredHeadset);

        producerThread = new Thread(callProducer);
        producerThread.setName("PRODUCING THREAD");
        producerThread.start();
    }


    void initAudioPlayer(CallSSLData callSSL,
                         String brokerAddress,
                         String sendKey,
                         String receivingTopic,
                         IPodAudioPlayerListener listener) {

        playerCallback = listener;
        this.callSSL = callSSL;

        this.brokerAddress = brokerAddress;
        this.receivingTopic = receivingTopic;
        this.sendKey = sendKey;

        if (isPlaying) {
            stopPlaying();
        }


        isGroupCall = isGroupCall(receivingTopic);

        if (isGroupCall) {

            callConsumer = new GroupCallConsumer(
                    callSSL, brokerAddress, sendKey, new ArrayList<>(topicsList), this);

        }
        callConsumer = new CallConsumer(
                callSSL, brokerAddress, sendKey, receivingTopic, this);

        consumerThread = new Thread(callConsumer);

        consumerThread.setName("CONSUMING THREAD");

        consumerThread.start();

        playerCallback.onPlayerReady();

        mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private boolean isGroupCall(String receiving) {


        String[] topics = receiving.split(",");

        topicsList = Arrays.asList(topics);

        return topicsList.size() > 1;
    }

    void setPlaying() {
        isPlaying = true;
        callConsumer.stopPlaying();
    }


    private void stopPlaying() {

        if (isPlaying) {
            callConsumer.stopPlaying();
            isPlaying = false;
        }

        if (playerCallback != null) playerCallback.onPlayStopped();

        mSensorManager.unregisterListener(this);

    }

    private void stopRecording() {
        // stops the recording activity

        if (isRecording) {
            isRecording = false;
            callProducer.stopRecording();
        }

        if (recordCallback != null) recordCallback.onRecordStopped();

    }

    public void endStream() {

        try {
            if (audioManager != null) {

                audioManager.setMode(AudioManager.MODE_NORMAL);
                audioManager.setMicrophoneMute(false);
                audioManager.setSpeakerphoneOn(false);

                if (haveAudioFocus)
                    audioManager.abandonAudioFocus(this);
            }
            stopRecording();
            stopPlaying();
            mContext.unregisterReceiver(headsetReceiver);
            stopWakeLock();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void switchSpeakerState(boolean isSpeakerOn) {

        if (isSpeakerOn != this.isSpeakerOn) {
            this.isSpeakerOn = isSpeakerOn;
            if (audioManager != null) {
                audioManager.setSpeakerphoneOn(isSpeakerOn);
            }
        }
    }

    void switchMuteState(boolean isMute) {
        if (isMute != this.isMute) {
            this.isMute = isMute;
            if (audioManager != null) {
                audioManager.setMicrophoneMute(isMute);
            }
        }
    }


    private void resetRecorder() {
        if (isRecording) {

            callProducer.stopRecording();

            callProducer.updateHeadsetState(isWiredHeadset);

            producerThread = new Thread(callProducer);
            producerThread.setName("PRODUCING THREAD");
            producerThread.start();

        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        if (isRecording) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {

                if (isWiredHeadset || audioManager.isSpeakerphoneOn()) {
                    return;
                }

                //calc distance from ear

                boolean newIsNear = sensorEvent.values[0] < Math.min(sensorEvent.sensor.getMaximumRange(), 3);

                //detect change

                if (isProximityNear != newIsNear) {

                    isProximityNear = newIsNear;

                    if (isProximityNear) {
                        proximityWakelock.acquire();
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            proximityWakelock.release(1);
                        } else {
                            proximityWakelock.release();
                        }
                    }
                }
            }
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        haveAudioFocus = focusChange == AudioManager.AUDIOFOCUS_GAIN;
    }

    @Override
    public void onRecord(byte[] encoded) {
        recordCallback.onByteRecorded(encoded);
    }

    @Override
    public void onRecorderSet() {
        isRecording = true;
        recordCallback.onRecordStarted();
    }

    @Override
    public void onRecorderError(String cause) {
        recordCallback.onAudioRecordError(cause);
    }

    @Override
    public void onFirstBytesReceived() {


    }

    private void runWakeLock() {
        try {
            cpuWakeLock.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopWakeLock() {
        try {
            cpuWakeLock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface IPodAudioListener {

        void onByteRecorded(byte[] bytes);

        void onRecordStopped();

        void onAudioRecordError(String cause);

        void onRecordRestarted();

        void onRecordStarted();

    }

    public interface IPodAudioPlayerListener {

        void onPlayStopped();

        void onAudioPlayError(String cause);

        void onPlayerReady();

    }

}
