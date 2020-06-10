package com.example.chat.application.chatexample;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fanap.podchat.example.R;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.util.ChatStateType;


public class CallActivity extends AppCompatActivity implements ChatContract.view {


    private static final String TAG = "CHAT_SDK_CALL";
    private String TOKEN = BaseApplication.getInstance().getString(R.string.token_zizi);
    private final static String ZIZI_TOKEN = BaseApplication.getInstance().getString(R.string.token_zizi);
    private final static String FIFI_TOKEN = BaseApplication.getInstance().getString(R.string.token_fifi);
    private final static String JIJI_TOKEN = BaseApplication.getInstance().getString(R.string.token_jiji);

    static int FIFI_ID = 15507;
    static int JIJI_ID = 15501;
    static int ZIZI_ID = 15510;

    private static String appId = "POD-Chat";

    private static String ssoHost = BaseApplication.getInstance().getString(R.string.integration_ssoHost);
    private static String serverName = "chatlocal";
    private static String name = BaseApplication.getInstance().getString(R.string.integration_serverName);
    private static String socketAddress = BaseApplication.getInstance().getString(R.string.integration_socketAddress);
    private static String platformHost = BaseApplication.getInstance().getString(R.string.integration_platformHost);
    private static String fileServer = BaseApplication.getInstance().getString(R.string.integration_platformHost);
//    integration /group

    public static int TEST_THREAD_ID = 7090;

    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    private ChatContract.presenter presenter;

    Button buttonCall, buttonConnect,buttonTestCall;
    TextView tvStatus;
    TextView tvCallerName;
    RadioGroup groupCaller;
    RadioGroup groupPartner;
    View callRequestView;
    ImageButton buttonAcceptCall;
    ImageButton buttonRejectCall;


    private int partnerId = 122;
    private boolean chatReady;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        init();

        setListeners();


    }

    private void connect() {

        RequestConnect request = new RequestConnect.Builder(
                socketAddress,
                appId,
                serverName,
                TOKEN,
                ssoHost,
                platformHost,
                fileServer
        ).build();

        presenter.connect(request);

    }

    private void setListeners() {


        buttonConnect.setOnClickListener((v) -> connect());

        buttonCall.setOnClickListener(v -> {

            if (chatReady) {
                buttonCall.setVisibility(View.INVISIBLE);
                presenter.requestCall(partnerId);
                tvStatus.setText(String.format("Calling %s", presenter.getNameById(partnerId)));
            } else
                Toast.makeText(this, "Chat Is Not Ready", Toast.LENGTH_SHORT).show();


        });

        buttonTestCall.setOnClickListener(v-> presenter.testCall());


        groupCaller.setOnCheckedChangeListener((group, checkedId) -> {

            Log.e(TAG, "Checked -> " + checkedId);

            updateCaller(checkedId);


        });

        groupPartner.setOnCheckedChangeListener((group, checkedId) -> {

            Log.e(TAG, "Checked -> " + checkedId);

            updatePartner(checkedId);

        });

        buttonAcceptCall.setOnClickListener(v -> {

            updateViewOnCallReaction();
            presenter.acceptIncomingCall();

        });

        buttonRejectCall.setOnClickListener((v) -> {

            updateViewOnCallReaction();
            presenter.rejectIncomingCall();

        });



    }

    private void updateViewOnCallReaction() {

        runOnUiThread(() -> {
            callRequestView.setVisibility(View.GONE);
            buttonCall.setVisibility(View.VISIBLE);
            tvCallerName.setText("");
        });

    }

    private void updatePartner(int checkedId) {

        switch (checkedId) {

            case R.id.radioZiziPartner: {

                Log.e(TAG, "Checked -> zizi");

                partnerId = ZIZI_ID;

                break;
            }
            case R.id.radioFifiPartner: {

                Log.e(TAG, "Checked -> fifi");
                partnerId = FIFI_ID;

                break;
            }
            case R.id.radioJijiPartner: {

                Log.e(TAG, "Checked -> jiji");
                partnerId = JIJI_ID;

                break;
            }


        }


    }

    private void updateCaller(int checkedId) {

        switch (checkedId) {

            case R.id.radioZiziCaller: {

                Log.e(TAG, "Checked -> zizi");

                TOKEN = ZIZI_TOKEN;

                break;
            }
            case R.id.radioFifiCaller: {

                Log.e(TAG, "Checked -> fifi");

                TOKEN = FIFI_TOKEN;

                break;
            }
            case R.id.radioJijiCaller: {

                Log.e(TAG, "Checked -> jiji");

                TOKEN = JIJI_TOKEN;

                break;
            }


        }
    }

    private void init() {


        presenter = new ChatPresenter(this, this, this);
        buttonCall = findViewById(R.id.btnCallRequest);
        buttonConnect = findViewById(R.id.btnConnect);
        buttonTestCall = findViewById(R.id.btnCallTest);
        groupCaller = findViewById(R.id.radioCaller);
        groupPartner = findViewById(R.id.radioPartner);
        tvStatus = findViewById(R.id.tvStatus);
        tvCallerName = findViewById(R.id.tvCallerName);
        callRequestView = findViewById(R.id.viewCallRequest);
        buttonAcceptCall = findViewById(R.id.buttonAccept);
        buttonRejectCall = findViewById(R.id.buttonReject);


    }

    @Override
    public void onState(String state) {

        Log.e(TAG, "STATE: " + state);

        runOnUiThread(() -> tvStatus.setText(state));

        if (state.equals(ChatStateType.ChatSateConstant.CHAT_READY)) {

            chatReady = true;

            runOnUiThread(() -> {
                buttonConnect.setVisibility(View.INVISIBLE);
                buttonCall.setVisibility(View.VISIBLE);
            });


        } else {

            chatReady = false;
            runOnUiThread(() -> {
                buttonConnect.setVisibility(View.VISIBLE);
                buttonCall.setVisibility(View.INVISIBLE);
            });
        }


    }

    @Override
    public void onVoiceCallRequestReceived(String callerName) {

        runOnUiThread(() -> {
            callRequestView.setVisibility(View.VISIBLE);
            buttonCall.setVisibility(View.INVISIBLE);
            tvCallerName.setText(callerName);
        });

    }

    @Override
    public void onVoiceCallRequestRejected(String callerName) {

        runOnUiThread(() -> {
            buttonCall.setVisibility(View.VISIBLE);
            tvStatus.setText(String.format("%s Rejected Your Call Request", callerName));
        });


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
