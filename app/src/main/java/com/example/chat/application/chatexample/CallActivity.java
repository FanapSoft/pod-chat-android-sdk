package com.example.chat.application.chatexample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.result_model.GetCallHistoryResult;
import com.fanap.podchat.example.R;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.ChatStateType;
import com.fanap.podchat.util.Util;


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

    Button buttonCall, buttonConnect, buttonTestCall, buttonCloseHistory;
    TextView tvStatus, tvCallerName, tvHistory;

    RadioGroup groupCaller;
    RadioGroup groupPartner;
    View callRequestView, inCallView, viewHistory;
    ImageButton buttonRejectCall, buttonAcceptCall, buttonEndCall, buttonGetHistory,buttonMute,buttonSpeaker;
    EditText etGroupId, etSender, etReceiver;


    private int partnerId = 122;
    private boolean chatReady;
    private boolean isTestMode = false;


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

        buttonTestCall.setOnClickListener(v -> {

            isTestMode = true;

            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            if (!etGroupId.getText().toString().isEmpty()
                    && !etSender.getText().toString().isEmpty()
                    && !etReceiver.getText().toString().isEmpty()) {

                presenter.testCall(etGroupId.getText().toString(),
                        etSender.getText().toString(),
                        etReceiver.getText().toString()
                );

            }else presenter.testCall();

            runOnUiThread(() -> {
                showInCallView();
                buttonConnect.setVisibility(View.INVISIBLE);
            });


        });

        buttonTestCall.setOnLongClickListener(v -> {
            presenter.endStream();

            return true;
        });


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


        buttonEndCall.setOnClickListener(v -> {



            if(isTestMode){

                presenter.endStream();
                inCallView.setVisibility(View.INVISIBLE);
                callRequestView.setVisibility(View.INVISIBLE);
                buttonCall.setVisibility(View.VISIBLE);
                buttonTestCall.setVisibility(View.VISIBLE);
                buttonConnect.setVisibility(View.VISIBLE);

                isTestMode = false;

            }else {
                presenter.endRunningCall();
            }



        });


        buttonGetHistory.setOnClickListener(v -> presenter.getCallHistory());

        buttonCloseHistory.setOnClickListener(v -> viewHistory.setVisibility(View.INVISIBLE));

        buttonMute.setOnClickListener(v->presenter.switchMute());

        buttonSpeaker.setOnClickListener(v->presenter.switchSpeaker());
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
        buttonCloseHistory = findViewById(R.id.buttonCloseHistory);
        groupCaller = findViewById(R.id.radioCaller);
        groupPartner = findViewById(R.id.radioPartner);
        tvStatus = findViewById(R.id.tvStatus);
        tvCallerName = findViewById(R.id.tvCallerName);
        tvHistory = findViewById(R.id.tvHistory);
        callRequestView = findViewById(R.id.viewCallRequest);
        inCallView = findViewById(R.id.viewCall);
        viewHistory = findViewById(R.id.viewHistory);
        buttonAcceptCall = findViewById(R.id.buttonAccept);
        buttonRejectCall = findViewById(R.id.buttonReject);
        buttonEndCall = findViewById(R.id.buttonEndCall);
        buttonGetHistory = findViewById(R.id.buttonGetHistory);
        buttonMute = findViewById(R.id.buttonMute);
        buttonSpeaker = findViewById(R.id.buttonSpeakerOn);
        etGroupId = findViewById(R.id.etGroupId);
        etSender = findViewById(R.id.etSender);
        etReceiver = findViewById(R.id.etReceiver);


        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        CallInfo callInfo = getIntent().getParcelableExtra(ChatConstant.POD_CALL_INFO);
        if(callInfo!=null){
            showInCallView();
        }
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
            viewHistory.setVisibility(View.INVISIBLE);
            buttonCall.setVisibility(View.INVISIBLE);
            buttonTestCall.setVisibility(View.INVISIBLE);
            tvCallerName.setText(callerName);
        });

    }

    @Override
    public void onVoiceCallRequestRejected(String callerName) {

        runOnUiThread(() -> {
            buttonCall.setVisibility(View.VISIBLE);
            buttonTestCall.setVisibility(View.VISIBLE);
            tvStatus.setText(String.format("%s Rejected Your Call Request", callerName));
        });


    }


    @Override
    public void onVoiceCallStarted(String uniqueId, String clientId) {

        runOnUiThread(this::showInCallView);


    }

    private void showInCallView() {
        inCallView.setVisibility(View.VISIBLE);
        callRequestView.setVisibility(View.INVISIBLE);
        buttonCall.setVisibility(View.INVISIBLE);
        buttonTestCall.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onVoiceCallEnded(String uniqueId, long subjectId) {

        runOnUiThread(() -> {
            inCallView.setVisibility(View.INVISIBLE);
            callRequestView.setVisibility(View.INVISIBLE);
            buttonCall.setVisibility(View.VISIBLE);
            buttonTestCall.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Call has been ended", Toast.LENGTH_SHORT).show();
        });


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onGetCallHistory(ChatResponse<GetCallHistoryResult> response) {

        runOnUiThread(() -> {

            viewHistory.setVisibility(View.VISIBLE);

            String source = Util.parserBoolean(response.isCache()) ? "Cache" : "Server";

            tvHistory.setText("Source: " + source + "\n");

            tvHistory.append("Content Count: " + response.getResult().getContentCount() + "\n\n");

            tvHistory.append("Has Next: " + response.getResult().isHasNext() + "\n\n");


            if (!Util.isNullOrEmpty(response.getResult().getCallsList()))
                for (CallVO call :
                        response.getResult().getCallsList()) {

                    tvHistory.append("\n====================\n");

                    tvHistory.append("Call id: " + call.getId() + "\n");
                    tvHistory.append("Call CreatorId: " + call.getCreatorId() + "\n");
                    tvHistory.append("Call CreateTime: " + call.getCreateTime() + "\n");
                    tvHistory.append("Call StartTime: " + call.getStartTime() + "\n");
                    tvHistory.append("Call EndTime: " + call.getEndTime() + "\n");
                    tvHistory.append("Call Status: " + call.getStatus() + "\n");
                    tvHistory.append("Call Type: " + call.getType() + "\n");
                    tvHistory.append("Call PartnerParticipant: " + call.getPartnerParticipantVO().toString() + "\n");

                }
            else {
                tvHistory.append("\nNo call history\n");
            }
            tvHistory.append("\n====================\n");
        });

    }

    @Override
    public void onCallReconnect(long callId) {
        Toast.makeText(this, "Call with id " + callId + " is reconnecting", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCallConnect(long callId) {
        Toast.makeText(this, "Call with id " + callId + " is connected", Toast.LENGTH_LONG).show();
    }
}
