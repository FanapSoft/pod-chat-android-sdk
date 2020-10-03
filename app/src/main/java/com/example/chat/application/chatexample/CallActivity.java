package com.example.chat.application.chatexample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.request_model.TerminateCallRequest;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.call.result_model.GetCallHistoryResult;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.util.ChatConstant;
import com.fanap.podchat.util.ChatStateType;
import com.fanap.podchat.util.Util;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.List;


public class CallActivity extends AppCompatActivity implements ChatContract.view {


    private static final String TAG = "CHAT_SDK_CALL";
    public static final long[] VIB_PATTERN = {0, 1000, 1000};
    private String TOKEN = BaseApplication.getInstance().getString(R.string.token_zizi);
    private final static String ZIZI_TOKEN = BaseApplication.getInstance().getString(R.string.token_zizi);
    private final static String FIFI_TOKEN = BaseApplication.getInstance().getString(R.string.token_fifi);
    private final static String JIJI_TOKEN = BaseApplication.getInstance().getString(R.string.token_jiji);

    //INTEGRATION

    static int FIFI_ID = 15507;
    static int JIJI_ID = 15501;
    static int ZIZI_ID = 15510;

    public final static String FIFI_CID = BaseApplication.getInstance().getString(R.string.ZIZI_FIFI_CONTACT_ID);
    public final static String JIJI_CID = BaseApplication.getInstance().getString(R.string.ZIZI_JIJI_CONTACT_ID);


    //NEMATI
//    static int FIFI_ID = 123;
//    static int JIJI_ID = 122;
//    static int ZIZI_ID = 121;

    private static String appId = BaseApplication.getInstance().getString(R.string.integration_appId);
    private static String ssoHost = BaseApplication.getInstance().getString(R.string.integration_ssoHost);
    private static String socketAddress = BaseApplication.getInstance().getString(R.string.integration_socketAddress);


    //integration
    private static String serverName = BaseApplication.getInstance().getString(R.string.integration_serverName);
    private static String name = BaseApplication.getInstance().getString(R.string.integration_serverName);
    private static String platformHost = BaseApplication.getInstance().getString(R.string.integration_platformHost);
    private static String fileServer = BaseApplication.getInstance().getString(R.string.integration_platformHost);
//


    //nemati
//    private static String serverName = BaseApplication.getInstance().getString(R.string.nemati_serverName);
//    private static String name = BaseApplication.getInstance().getString(R.string.nemati_serverName);
//    private static String platformHost = BaseApplication.getInstance().getString(R.string.nemati_platformHost);
//    private static String fileServer = BaseApplication.getInstance().getString(R.string.nemati_fileServer);


//    integration /group

    public static int TEST_THREAD_ID = 7090;


    private static String sandBoxSSOHost = BaseApplication.getInstance().getString(R.string.ssoHost);
    private static String sandBoxServerName = "chat-server";


    private static String sandBoxName = BaseApplication.getInstance().getString(R.string.main_server_name);
    private static String sandBoxSocketAddress = BaseApplication.getInstance().getString(R.string.socketAddress);
    private static String sandBoxPlatformHost = BaseApplication.getInstance().getString(R.string.platformHost);
    private static String sandBoxFileServer = BaseApplication.getInstance().getString(R.string.fileServer);


    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    private ChatContract.presenter presenter;

    Button buttonCall, buttonConnect, buttonTestCall, buttonCloseHistory, buttonAddCallParticipant,
            buttonConnectSandBox, buttonStartSandboxCall, buttonShareLog, buttonRemoveCallParticipant,
            buttonTerminateCall;

    TextView tvStatus, tvCallerName, tvHistory;

    RadioGroup groupCaller;
    RadioGroup groupPartner;
    View callRequestView, inCallView, viewHistory;
    ImageButton buttonRejectCall, buttonAcceptCall, buttonEndCall, buttonGetHistory, buttonMute, buttonSpeaker;
    EditText etGroupId, etSender, etReceiver, etNumberOrOtp, etSandboxPartnerId;
    CheckBox checkBoxSSL,
            checkBoxGroupCall,
            checkboxZiziPartner,
            checkboxJijiPartner,
            checkboxFifiPartner,
            checkboxAddZizi,
            checkboxAddJiji,
            checkboxAddFifi;

//    CheckBox checkBoxViewSandBox, checkBoxViewIntegaration;
//    Group groupSandBoxViews, groupIntegartionViews;


    Vibrator vibrator;
    boolean isMute = false;
    boolean isSpeakerOn = false;


    private int partnerId = 122;
    private boolean chatReady;
    private boolean isTestMode = false;
    private boolean isInCall = false;


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
                if (checkBoxGroupCall.isChecked()) {
                    presenter.requestGroupCall(checkboxFifiPartner.isChecked(), checkboxZiziPartner.isChecked(), checkboxJijiPartner.isChecked());
                    tvStatus.setText("Starting Group Call");
                } else {
                    presenter.requestCall(partnerId, checkBoxSSL.isChecked());
                    tvStatus.setText(String.format("Calling %s", presenter.getNameById(partnerId)));
                }

            } else
                Toast.makeText(this, "Chat Is Not Ready", Toast.LENGTH_SHORT).show();


        });

        buttonTestCall.setOnClickListener(v -> {

            isTestMode = true;

//            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            if (!etGroupId.getText().toString().isEmpty()
                    && !etSender.getText().toString().isEmpty()
                    && !etReceiver.getText().toString().isEmpty()) {

                presenter.testCall(etGroupId.getText().toString(),
                        etSender.getText().toString(),
                        etReceiver.getText().toString()
                );

            } else presenter.testCall();

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

            onCallEnded();

            if (isTestMode) {

                presenter.endStream();
                inCallView.setVisibility(View.INVISIBLE);
                callRequestView.setVisibility(View.INVISIBLE);
                buttonCall.setVisibility(View.VISIBLE);
//                buttonTestCall.setVisibility(View.VISIBLE);
                buttonConnect.setVisibility(View.VISIBLE);

                isTestMode = false;

            } else {
                presenter.endRunningCall();
            }

        });

        buttonTerminateCall.setOnClickListener(v -> {

            onCallEnded();


            presenter.terminateCall();


        });


        buttonGetHistory.setOnClickListener(v -> presenter.getCallHistory());

        buttonCloseHistory.setOnClickListener(v -> viewHistory.setVisibility(View.INVISIBLE));

        buttonMute.setOnClickListener(v -> {


            vibrate();

            scaleIt(v);

            presenter.switchMute();

            toggleMute((ImageButton) v);

        });

        buttonSpeaker.setOnClickListener(v -> {

            vibrate();

            scaleIt(v);

            presenter.switchSpeaker();

            toggleSpeaker((ImageButton) v);

        });

        buttonAddCallParticipant.setOnClickListener(v -> presenter.addCallParticipant(checkboxAddFifi.isChecked(), checkboxAddJiji.isChecked(), checkboxAddZizi.isChecked()));
        buttonRemoveCallParticipant.setOnClickListener(v -> presenter.removeCallParticipant(checkboxAddFifi.isChecked(), checkboxAddJiji.isChecked(), checkboxAddZizi.isChecked()));

        buttonConnectSandBox.setOnClickListener(v -> {

            presenter.enableAutoRefresh(this, etNumberOrOtp.getText().toString());

            etNumberOrOtp.setText("");

        });

        buttonConnectSandBox.setOnLongClickListener(v -> {
            presenter.logOut();
            etNumberOrOtp.setText("");
            return true;
        });

        buttonStartSandboxCall.setOnClickListener(v -> {
            if (chatReady) {

                if (!etSandboxPartnerId.getText().toString().isEmpty()) {

                    int sandBoxPartnerID = Integer.parseInt(etSandboxPartnerId.getText().toString());

                    presenter.requestCall(sandBoxPartnerID, checkBoxSSL.isChecked());

                    tvStatus.setText("Calling: " + sandBoxPartnerID);
                }

            } else {
                Toast.makeText(this, "Chat is not ready", Toast.LENGTH_SHORT).show();
            }
        });

        buttonShareLog.setOnClickListener(v -> {
            presenter.shareLogs();
        });
//        checkBoxViewSandBox.setOnCheckedChangeListener((buttonView, isChecked) -> groupSandBoxViews.setVisibility(isChecked ? View.VISIBLE : View.GONE));
//
//        checkBoxViewIntegaration.setOnCheckedChangeListener((buttonView, isChecked) -> groupIntegartionViews.setVisibility(isChecked ? View.VISIBLE : View.GONE));

    }

    private void toggleSpeaker(ImageButton v) {

        isSpeakerOn = !isSpeakerOn;

        if (isSpeakerOn) {
            v.setAlpha(1f);
        } else {
            v.setAlpha(0.6f);
        }

    }

    private void toggleMute(ImageButton v) {

        isMute = !isMute;

        if (isMute) {
            v.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mic_on));
        } else {
            v.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mic_off));
        }
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

        } else {
            //deprecated in API 26
            vibrator.vibrate(100);
        }
    }

    private void vibrateE() {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            vibrator.vibrate(VibrationEffect.createWaveform(VIB_PATTERN, 0));
//
//        } else {
//            //deprecated in API 26
//            vibrator.vibrate(VIB_PATTERN, 0);
//        }

    }

    private void scaleIt(View v) {
        v.animate()
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setDuration(250)
                .withEndAction(() ->
                        v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(250)
                                .start())
                .start();
    }

    private void updateViewOnCallReaction() {

        cancelVib();

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

        buttonAddCallParticipant = findViewById(R.id.btnAddCallParticipant);
        buttonRemoveCallParticipant = findViewById(R.id.btnRemoveCallParticipant);

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
        buttonTerminateCall = findViewById(R.id.btnTerminateCall);
        buttonGetHistory = findViewById(R.id.buttonGetHistory);
        buttonConnectSandBox = findViewById(R.id.btnConnectToSandbox);
        buttonStartSandboxCall = findViewById(R.id.btnSandboxCall);


        buttonMute = findViewById(R.id.buttonMute);
        buttonSpeaker = findViewById(R.id.buttonSpeakerOn);

        etGroupId = findViewById(R.id.etGroupId);
        etSender = findViewById(R.id.etSender);
        etReceiver = findViewById(R.id.etReceiver);
        etNumberOrOtp = findViewById(R.id.etOtpNumber);
        etSandboxPartnerId = findViewById(R.id.etSandBoxPartnerId);

        checkBoxSSL = findViewById(R.id.checkboxSSL);
        checkBoxGroupCall = findViewById(R.id.checkboxGroupCall);

        checkboxZiziPartner = findViewById(R.id.checkboxZiziPartner);
        checkboxFifiPartner = findViewById(R.id.checkboxFifiPartner);
        checkboxJijiPartner = findViewById(R.id.checkboxJijiPartner);

        checkboxAddZizi = findViewById(R.id.checkboxAddZizi);
        checkboxAddFifi = findViewById(R.id.checkboxAddFifi);
        checkboxAddJiji = findViewById(R.id.checkboxAddJiji);

        buttonShareLog = findViewById(R.id.btnShareLogs);

//        checkBoxViewSandBox = findViewById(R.id.checkBoxSandBox);
//        checkBoxViewIntegaration = findViewById(R.id.checkboxIntegration);
//
//        groupSandBoxViews = findViewById(R.id.groupSandBox);
//        groupIntegartionViews = findViewById(R.id.groupIntegration);


        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        Logger.addLogAdapter(new AndroidLogAdapter());

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        CallInfo callInfo = getIntent().getParcelableExtra(ChatConstant.POD_CALL_INFO);

        Log.e(TAG, "Call info: " + callInfo);

        if (callInfo != null) {
            presenter.setCallInfo(callInfo);
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

        vibrateE();

        runOnUiThread(() -> {
            callRequestView.setVisibility(View.VISIBLE);
            viewHistory.setVisibility(View.INVISIBLE);
            buttonCall.setVisibility(View.INVISIBLE);
            buttonTestCall.setVisibility(View.INVISIBLE);
            buttonConnectSandBox.setVisibility(View.INVISIBLE);
            buttonStartSandboxCall.setVisibility(View.INVISIBLE);
            tvCallerName.setText(callerName);
        });

    }

    @Override
    public void onVoiceCallRequestRejected(String callerName) {


        cancelVib();

        runOnUiThread(() -> {
            buttonCall.setVisibility(View.VISIBLE);
//            buttonTestCall.setVisibility(View.VISIBLE);
            buttonConnectSandBox.setVisibility(View.VISIBLE);
            buttonStartSandboxCall.setVisibility(View.VISIBLE);
            tvStatus.setText(String.format("%s Rejected Your Call Request", callerName));
        });


    }


    @Override
    public void onVoiceCallStarted(String uniqueId, String clientId) {

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        cancelVib();

        runOnUiThread(this::showInCallView);


    }

    private void cancelVib() {

        vibrator.cancel();

    }

    private void showInCallView() {
        inCallView.setVisibility(View.VISIBLE);
        callRequestView.setVisibility(View.INVISIBLE);
        buttonCall.setVisibility(View.INVISIBLE);
        buttonTestCall.setVisibility(View.INVISIBLE);

        buttonConnectSandBox.setVisibility(View.INVISIBLE);
        buttonStartSandboxCall.setVisibility(View.INVISIBLE);

        isInCall = true;
    }

    @Override
    public void onVoiceCallEnded(String uniqueId, long subjectId) {

        onCallEnded();


    }

    private void onCallEnded() {

        isInCall = false;

        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        runOnUiThread(() -> {
            inCallView.setVisibility(View.INVISIBLE);
            callRequestView.setVisibility(View.INVISIBLE);
            buttonCall.setVisibility(View.VISIBLE);
//            buttonTestCall.setVisibility(View.VISIBLE);
            buttonConnectSandBox.setVisibility(View.VISIBLE);
            buttonStartSandboxCall.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Call has been ended", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onGetCallHistory(ChatResponse<GetCallHistoryResult> response) {

        runOnUiThread(() -> {

            viewHistory.setVisibility(View.VISIBLE);

            String source = Util.parserBoolean(response.isCache()) ? "Cache" : "Server";

            tvHistory.append("\n\n\n===\nSource: " + source + "\n===\n");

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
                    try {
                        tvHistory.append("Call PartnerParticipant: " + call.getPartnerParticipantVO().toString() + "\n");
                    } catch (Exception ignored) {
                    }

                }
            else {
                tvHistory.append("\nNo call history\n");
            }
            tvHistory.append("\n====================\n");
        });

    }

    @Override
    public void onCallReconnect(long callId) {
        runOnUiThread(() -> Toast.makeText(this, "Call with id " + callId + " is reconnecting", Toast.LENGTH_LONG).show());
    }

    @Override
    public void onCallConnect(long callId) {
        runOnUiThread(() -> {

            Toast.makeText(this, "Call with id " + callId + " is connected", Toast.LENGTH_LONG).show();

        });
    }

    @Override
    public void onCallDelivered(CallDeliverResult result) {
        runOnUiThread(() -> {

            Toast.makeText(this, "Call Request Delivered to " + result.getCallParticipantVO().getUserId(), Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public void onGroupVoiceCallRequestReceived(String callerName, String title, List<Participant> participants) {

        vibrateE();

        runOnUiThread(() -> {
            Toast.makeText(this, "Group Call from " + callerName, Toast.LENGTH_SHORT).show();
            callRequestView.setVisibility(View.VISIBLE);
            viewHistory.setVisibility(View.INVISIBLE);
            buttonCall.setVisibility(View.INVISIBLE);
            buttonTestCall.setVisibility(View.INVISIBLE);
            buttonConnectSandBox.setVisibility(View.INVISIBLE);
            buttonStartSandboxCall.setVisibility(View.INVISIBLE);
            tvCallerName.setText(callerName + " from " + title);
        });

    }

    @Override
    public void onCallParticipantLeft(String participant) {

        vibrate();

        runOnUiThread(() -> Toast.makeText(this, participant + " Left ", Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onLogEvent(String log) {
        Logger.json(log);
    }

    @Override
    public void onGetToken(String token) {

        RequestConnect request = new RequestConnect.Builder(
                sandBoxSocketAddress,
                appId,
                sandBoxServerName,
                token,
                sandBoxSSOHost,
                sandBoxPlatformHost,
                sandBoxFileServer
        ).build();

        presenter.connect(request);

    }


    @Override
    public void onGetUserInfo(ChatResponse<ResultUserInfo> outPutUserInfo) {

        long id = outPutUserInfo.getResult().getUser().getId();

        etNumberOrOtp.setText("Your ID is: " + id);


    }

    @Override
    public void onCallParticipantJoined(String participant) {

        vibrate();

        runOnUiThread(() -> Toast.makeText(this, participant + " joined!", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onCallParticipantRemoved(String name) {
        vibrate();

        runOnUiThread(() -> Toast.makeText(this, name + " removed from call!", Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onRemovedFromCall() {
        vibrate();

        runOnUiThread(() -> Toast.makeText(this, "You have been removed from call!", Toast.LENGTH_SHORT).show());
    }
}
