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

import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallSSLData;
import com.fanap.podchat.util.Util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

public class PodCallAudioCallStreamManager implements SensorEventListener, AudioManager.OnAudioFocusChangeListener, RecordThread.IRecordThread, CallProducer.IRecordThread {

    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final int NUM_OF_THREADS = 6;
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

    ExecutorService executorService;

    private Map<String, CallConsumer> consumerMap = new HashMap<>();

    private CallProducer callProducer;

    private CallSSLData callSSL;
//
//    Thread consumerThread;
//
//    Thread producerThread;

    String brokerAddress;
    String sendKey;
    String receivingTopic;
    String sendingTopic;
    List<String> topicsList;

    Semaphore semaphoreCreateConsumer = new Semaphore(1);

    boolean isGroupCall;

    ArrayList<CallConsumer.IConsumer> consumerCallbacksList = new ArrayList<>();


    PodCallAudioCallStreamManager(Context context) {

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

        executorService = Executors.newFixedThreadPool(NUM_OF_THREADS);

//        executorServiceConsumerCreator = Executors.newSingleThreadExecutor();

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

        createProducerFor(sendingTopic);

    }

    private void createProducerFor(String sendingTopic) {
        callProducer = new CallProducer(this, callSSL, brokerAddress, sendKey, sendingTopic, isWiredHeadset);
        executorService.execute(callProducer);
    }


    void initAudioPlayer(
            String sslCert,
            String brokerAddress,
            String sendKey,
            String receivingTopic,
            IPodAudioPlayerListener listener) {

        playerCallback = listener;

        this.brokerAddress = brokerAddress;
        this.receivingTopic = receivingTopic;
        this.sendKey = sendKey;

        initCert(sslCert);

        if (isPlaying) {
            stopPlaying();
        }


        isGroupCall = createTopicList(receivingTopic);

        if (isGroupCall) {
            createAndConnectInitialConsumer();
        } else {
            createConsumerFor(receivingTopic);
        }


        isPlaying = true;

        playerCallback.onPlayerReady();

        mSensorManager.registerListener(this, mProximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private void initCert(String sslCert) {
        try {
            this.callSSL = generateFile(sslCert);
        } catch (Exception e) {
            recordCallback.onAudioRecordError("Generating ssl config file failed! " + e.getMessage());
        }
    }


    private CallSSLData generateFile(String sslCert) throws Exception {

        if (Util.isNullOrEmpty(sslCert)) throw new Exception("SSL could not be empty");

        InputStream inputStream1 =
                new ByteArrayInputStream(sslCert.getBytes());

        OutputStream out1 = null;

        try {
            out1 = new FileOutputStream(mContext.getFilesDir() + "/ca-cert");

            copy(inputStream1, out1);


            File cert = new File(mContext.getFilesDir() + "/ca-cert");

            if (cert.exists()) {

                return new CallSSLData(cert, null, null);

            } else throw new Exception("Could not create ssl file!");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Exception("FileNotFoundException! Could not create ssl file! " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("IOExeption! Could not create ssl file! " + e.getMessage());
        }
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

    private void createAndConnectInitialConsumer() {

        for (String topic :
                topicsList) {
            createConsumerFor(topic);
        }

    }

    private void createConsumerFor(String topic) {

        executorService.execute(() -> {

            try {
                semaphoreCreateConsumer.acquire();


                CallConsumer.IConsumer callback = getCallbackFor(topic);

                CallConsumer callConsumer =
                        new CallConsumer(callSSL, brokerAddress, sendKey, topic, callback);


                consumerCallbacksList.add(callback);

                semaphoreCreateConsumer.release();

                consumerMap.put(topic, callConsumer);

                callConsumer.run();
            } catch (InterruptedException e) {
                playerCallback.onAudioPlayError("creating consumer failed: " + e.getMessage() + " topic: " + topic);
            }


        });
    }

    private CallConsumer.IConsumer getCallbackFor(String topic) {
        return new CallConsumer.IConsumer() {
            @Override
            public void onFirstBytesReceived() {

            }

            @Override
            public void onConsumerException(String cause) {

                playerCallback.onAudioPlayError("Exception in topic player: " + topic + " cause: " + cause);

            }

            @Override
            public void onConsumerImportantEvent(String info) {
                playerCallback.onPlayerImportantEvent("Important event in topic player: " + topic + " =>" + info);
            }
        };
    }

    private boolean createTopicList(String receiving) {

        String[] topics = receiving
                .replace(" ", "")
                .split(",");

        topicsList = new ArrayList<>(Arrays.asList(topics));

        return topicsList.size() > 1;
    }


    private void stopConsumers() {

        try {
            boolean last;
            int counter = 0;
            for (String sendTopic : consumerMap.keySet()) {
                counter++;
                last = counter == consumerMap.size();
                CallConsumer runningConsumer = consumerMap.get(sendTopic);
                if (runningConsumer != null) {
                    if (last) {
                        runningConsumer.stopPlayingAndReleaseDecoder();
                    } else {
                        runningConsumer.stopPlaying();
                    }
                }
            }
            consumerMap.clear();
            consumerCallbacksList.clear();
        } catch (Exception e) {
            e.printStackTrace();
            playerCallback.onAudioPlayError(e.getMessage());
        }
    }

    private void stopPlaying() {

        if (isPlaying) {
            stopConsumers();
            isPlaying = false;
        }

        if (playerCallback != null) playerCallback.onPlayStopped();

        mSensorManager.unregisterListener(this);

    }

    private void stopRecording() {
        // stops the recording activity

        if (isRecording) {
            callProducer.stopRecording();
            isRecording = false;
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
            stopWakeLock();
            clearResources();
            executorService.shutdown();

            mContext.unregisterReceiver(headsetReceiver);

        } catch (Exception e) {
            recordCallback.onRecorderImportantEvent("Exception when ending stream");
        }
    }

    private void clearResources() {


        try {
            callSSL.clear();
        } catch (Exception e) {
            recordCallback.onRecorderImportantEvent("Exception when clearing call resources");
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
                        proximityWakelock.acquire(2 * 60 * 1000);
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
    public void onImportantEvent(String info) {
        recordCallback.onRecorderImportantEvent(info);
    }


    private void runWakeLock() {
        try {
            cpuWakeLock.acquire(2 * 60 * 1000L /*10 minutes*/);
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


    public void removeCallParticipant(CallParticipantVO callParticipant) {

        playerCallback.onPlayerImportantEvent("Participant to remove: +" + callParticipant.getParticipantVO().getName());

        String topic = callParticipant.getSendTopic();

        if (consumerMap.containsKey(topic)) {
            CallConsumer consumer = consumerMap.get(topic);

            if (consumer != null) {
                consumer.stopPlaying();
                if (consumer.getConsumerCallback() != null)
                    consumerCallbacksList.remove(consumer.getConsumerCallback());
            }

            consumerMap.remove(topic);

        }

        playerCallback.onPlayerImportantEvent("Consumer stopped: " + callParticipant.getSendTopic());

    }


    public void addCallParticipant(List<CallParticipantVO> joinedParticipants) {


        for (CallParticipantVO participant :
                joinedParticipants) {

            playerCallback.onPlayerImportantEvent("Participant to add: " + participant.getParticipantVO().getName());

            createConsumerFor(participant.getSendTopic());

            playerCallback.onPlayerImportantEvent("Producer added: " + participant.getSendTopic());
        }


    }

    public interface IPodAudioListener {

        void onByteRecorded(byte[] bytes);

        void onRecordStopped();

        void onAudioRecordError(String cause);

        void onRecordRestarted();

        void onRecordStarted();

        void onRecorderImportantEvent(String info);
    }

    public interface IPodAudioPlayerListener {

        void onPlayStopped();

        void onAudioPlayError(String cause);

        void onPlayerReady();

        void onPlayerImportantEvent(String info);


    }

}
