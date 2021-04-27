package com.example.chat.application.chatexample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.fanap.podcall.util.CallPermissionHandler;
import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.call.contacts.ContactsFragment;
import com.fanap.podchat.call.contacts.ContactsWrapper;
import com.fanap.podchat.call.login.LoginFragment;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallVO;
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

import java.util.ArrayList;
import java.util.List;


public class CallActivity extends AppCompatActivity implements ChatContract.view, LoginFragment.ILoginInterface {


    private static final String TAG = "CHAT_SDK_CALL";
    public static final long[] VIB_PATTERN = {0, 1000, 1000};
    private String TOKEN = BaseApplication.getInstance().getString(R.string.Farhad_Kheirkhah);
    private final static String Farhad_TOKEN = BaseApplication.getInstance().getString(R.string.Farhad_Kheirkhah);
    private final static String Pooria_TOKEN = BaseApplication.getInstance().getString(R.string.Pooria_Pahlevani);
    private final static String Masoud_TOKEN = BaseApplication.getInstance().getString(R.string.Nadia_Anvari);
    //INTEGRATION

    static int Pooria_ID = 18477;
    static int Masoud_ID = 18476;
    static int Farhad_ID = 18478;

    public final static String POORIA_CID = BaseApplication.getInstance().getString(R.string.ZIZI_FIFI_CONTACT_ID);
    public final static String MASOUD_CID = BaseApplication.getInstance().getString(R.string.ZIZI_JIJI_CONTACT_ID);


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


    private static String sandBoxSSOHost = BaseApplication.getInstance().getString(R.string.sandbox_ssoHost);
    private static String sandBoxServerName = "chat-server";


    private static String sandBoxName = BaseApplication.getInstance().getString(R.string.sandbox_server_name);
    private static String sandBoxSocketAddress = BaseApplication.getInstance().getString(R.string.sandbox_socketAddress);
    private static String sandBoxPlatformHost = BaseApplication.getInstance().getString(R.string.sandbox_platformHost);
    private static String sandBoxFileServer = BaseApplication.getInstance().getString(R.string.sandbox_fileServer);
    private static String podspaceServer = BaseApplication.getInstance().getString(R.string.podspace_file_server_main);


    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;


    private ChatContract.presenter presenter;

    Button buttonCloseHistory, buttonAddCallParticipant,
            buttonStartSandboxCall, buttonRemoveCallParticipant,
            buttonTerminateCall;

    TextView tvStatus, tvCallerName, tvHistory;

    RadioGroup groupCaller;
    RadioGroup groupPartner;
    View callRequestView, inCallView, viewHistory;
    ImageButton buttonRejectCall, buttonAcceptCall, buttonEndCall, buttonMute, buttonSpeaker;
    EditText etSandboxPartnerId, etNewParticipantToAdd;

    FrameLayout frameLayout;
    FloatingActionButton fabContacts;

//    CheckBox checkBoxViewSandBox, checkBoxViewIntegaration;
//    Group groupSandBoxViews, groupIntegartionViews;


    Vibrator vibrator;
    boolean isMute = false;
    boolean isSpeakerOn = false;


    private int partnerId = Masoud_ID;
    private boolean chatReady;
    private boolean isTestMode = false;
//    private boolean isInCall = false;

    CallPartnerView localCallPartner,
            remoteCallPartner1,
            remoteCallPartner2,
            remoteCallPartner3,
            remoteCallPartner4;
    private boolean isVideoPaused = false;


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
                fileServer,
                podspaceServer
        ).build();

        presenter.connect(request);

    }

    private void setListeners() {


//        buttonConnect.setOnClickListener((v) -> connect());
//
//        buttonCall.setOnClickListener(v -> {
//
//            if (chatReady) {
//                showRequestCallView();
//                if (checkBoxGroupCall.isChecked()) {
//                    presenter.requestGroupCall(checkboxFifiPartner.isChecked(), checkboxZiziPartner.isChecked(), checkboxJijiPartner.isChecked());
//                    updateStatus("Starting Group Call");
//                } else {
//                    presenter.requestCall(partnerId, checkBoxSSL.isChecked());
//                    updateStatus(String.format("Calling %s", presenter.getNameById(partnerId)));
//                }
//            } else
//                Toast.makeText(this, "Chat Is Not Ready", Toast.LENGTH_SHORT).show();
//
//
//        });

//        buttonTestCall.setOnClickListener(v -> {
//
//            isTestMode = true;
//
////            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
//            if (!etGroupId.getText().toString().isEmpty()
//                    && !etSender.getText().toString().isEmpty()
//                    && !etReceiver.getText().toString().isEmpty()) {
//
//                presenter.testCall(etGroupId.getText().toString(),
//                        etSender.getText().toString(),
//                        etReceiver.getText().toString()
//                );
//
//            } else presenter.testCall();
//
//            runOnUiThread(() -> {
//                showInCallView();
//                buttonConnect.setVisibility(View.INVISIBLE);
//            });
//
//
//        });

//        buttonTestCall.setOnLongClickListener(v -> {
//            presenter.endStream();
//
//            return true;
//        });



//
        buttonAcceptCall.setOnClickListener(v -> {

            updateViewOnCallReaction();
            presenter.acceptIncomingCall();

        });

        buttonRejectCall.setOnClickListener((v) -> {

            updateViewOnCallReaction();
            presenter.rejectIncomingCall();

        });


        buttonEndCall.setOnClickListener(v -> {


            if (isTestMode) {

                presenter.endStream();
                inCallView.setVisibility(View.INVISIBLE);
                callRequestView.setVisibility(View.INVISIBLE);

                isTestMode = false;

            } else {
                presenter.endRunningCall();
            }
            onCallEnded();

        });

        buttonTerminateCall.setOnClickListener(v -> {

            onCallEnded();


            presenter.terminateCall();


        });



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

        buttonAddCallParticipant.setOnClickListener(v ->
        {

            presenter.addCallParticipant(etNewParticipantToAdd.getText().toString(),
                    false,
                    false,
                    false);

        });
        buttonRemoveCallParticipant.setOnClickListener(v -> presenter.removeCallParticipant(etNewParticipantToAdd.getText().toString(), false, false, false));

        buttonStartSandboxCall.setOnClickListener(v -> {
            if (chatReady) {
                presenter.requestMainOrSandboxCall(etSandboxPartnerId.getText().toString(), false);
            } else {
                Toast.makeText(this, "Chat is not ready", Toast.LENGTH_SHORT).show();
            }
        });



        fabContacts.setOnClickListener(v -> presenter.getContact());

        localCallPartner.setOnClickListener(v -> presenter.switchCamera());
        localCallPartner.setOnClickListener(v -> {
            if (isVideoPaused)
                presenter.resumeVideo();
            else
                presenter.pauseVideo();

            isVideoPaused = !isVideoPaused;
        });
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

        updateMuteButton(isMute);
    }

    private void updateMuteButton(boolean isMute) {

        if (isMute) {
            buttonMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mic_on));
        } else {
            buttonMute.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_mic_off));
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
            buttonStartSandboxCall.setVisibility(View.VISIBLE);
            tvCallerName.setText("");
        });


    }



    private void init() {


        presenter = new ChatPresenter(this, this, this);

        buttonCloseHistory = findViewById(R.id.buttonCloseHistory);

        buttonAddCallParticipant = findViewById(R.id.btnAddCallParticipant);
        buttonRemoveCallParticipant = findViewById(R.id.btnRemoveCallParticipant);

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
        buttonStartSandboxCall = findViewById(R.id.btnSandboxCall);


        buttonMute = findViewById(R.id.buttonMute);
        buttonSpeaker = findViewById(R.id.buttonSpeakerOn);


        etSandboxPartnerId = findViewById(R.id.etSandBoxPartnerId);
        etNewParticipantToAdd = findViewById(R.id.etNewParticipant);

        frameLayout = findViewById(R.id.frame_call);
        fabContacts = findViewById(R.id.fabShowContactsList);

        Logger.addLogAdapter(new AndroidLogAdapter());

        localCallPartner = findViewById(R.id.localPartnerCameraView);
        remoteCallPartner1 = findViewById(R.id.remotePartnerView1);
        remoteCallPartner2 = findViewById(R.id.remotePartnerView2);
        remoteCallPartner3 = findViewById(R.id.remotePartnerView3);
        remoteCallPartner4 = findViewById(R.id.remotePartnerView4);


        List<CallPartnerView> views = new ArrayList<>();

        views.add(remoteCallPartner1);
        views.add(remoteCallPartner2);
        views.add(remoteCallPartner3);
        views.add(remoteCallPartner4);


        if (!CallPermissionHandler.needCameraAndRecordPermission(this)) {
            presenter.setupVideoCallParam(localCallPartner, views);
        }else {
            CallPermissionHandler.requestPermission(this,101);
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        CallInfo callInfo = getIntent().getParcelableExtra(ChatConstant.POD_CALL_INFO);

        Log.e(TAG, "Call info: " + callInfo);

//        connect();
        if (callInfo != null) {
            presenter.setCallInfo(callInfo);
            showInCallView();
        }
    }

    @Override
    public void onState(String state) {

        Log.e(TAG, "STATE: " + state);

        if (state.equals(ChatStateType.ChatSateConstant.CHAT_READY)) {

            chatReady = true;

            runOnUiThread(() -> {
                tvStatus.setText("Chat is Ready :)");
            });

        } else {

            chatReady = false;
            runOnUiThread(() -> {
                tvStatus.setText("Connecting...");
            });


        }


    }

    @Override
    public void onVoiceCallRequestReceived(String callerName) {

        vibrateE();

        runOnUiThread(() -> {
            callRequestView.setVisibility(View.VISIBLE);
            viewHistory.setVisibility(View.INVISIBLE);
            buttonStartSandboxCall.setVisibility(View.INVISIBLE);
            tvCallerName.setText(" " + callerName);
        });

    }

    @Override
    public void onVoiceCallRequestRejected(String callerName) {


        cancelVib();

        runOnUiThread(() -> {
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
        runOnUiThread(() -> {
            inCallView.setVisibility(View.VISIBLE);
            callRequestView.setVisibility(View.INVISIBLE);

            buttonStartSandboxCall.setVisibility(View.INVISIBLE);
        });
    }

    private void hideInCallView() {
        runOnUiThread(() -> {
            inCallView.setVisibility(View.GONE);
            callRequestView.setVisibility(View.INVISIBLE);
            buttonStartSandboxCall.setVisibility(View.VISIBLE);
        });
    }

    private void showRequestCallView() {
        runOnUiThread(() -> {
            inCallView.setVisibility(View.VISIBLE);
            callRequestView.setVisibility(View.INVISIBLE);

            buttonStartSandboxCall.setVisibility(View.INVISIBLE);
        });
    }

    private void hideRequestCallView() {
        runOnUiThread(() -> {
            inCallView.setVisibility(View.GONE);
            callRequestView.setVisibility(View.GONE);
            buttonStartSandboxCall.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onVoiceCallEnded(String uniqueId, long subjectId) {

        onCallEnded();


    }

    private void onCallEnded() {

        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        runOnUiThread(() -> {
            inCallView.setVisibility(View.INVISIBLE);
            callRequestView.setVisibility(View.INVISIBLE);
//            buttonTestCall.setVisibility(View.VISIBLE);
            buttonStartSandboxCall.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Call has been ended", Toast.LENGTH_SHORT).show();
            tvStatus.setText("Call has been ended");
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
        connectToSandbox(token);
    }

    @Override
    public void onTokenReceived(String token) {
        connectToSandbox(token);
    }

    private void connectToSandbox(String token) {

        RequestConnect request = new RequestConnect.Builder(
                sandBoxSocketAddress,
                appId,
                sandBoxServerName,
                token,
                sandBoxSSOHost,
                sandBoxPlatformHost,
                sandBoxFileServer,
                podspaceServer
        ).build();

        presenter.connect(request);
    }


    @Override
    public void onGetUserInfo(ChatResponse<ResultUserInfo> outPutUserInfo) {


        Toast.makeText(this, "Hello " + outPutUserInfo.getResult().getUser().getName() , Toast.LENGTH_SHORT).show();


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

        onCallEnded();

        runOnUiThread(() -> Toast.makeText(this, "You have been removed from call!", Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onCallCreated(long threadId) {
        updateStatus("Call with id " + threadId + " was created");
        showRequestCallView();
    }


    @Override
    public void audioCallMuted() {
        updateMuteButton(true);
    }

    @Override
    public void audioCallUnMuted() {
        updateMuteButton(false);
    }

    @Override
    public void callParticipantMuted(CallParticipantVO participant) {
        showToast(participant.getParticipantVO().getFirstName() + " " + participant.getParticipantVO().getLastName() + " is muted now!");
    }

    @Override
    public void callParticipantUnMuted(CallParticipantVO participant) {
        showToast(participant.getParticipantVO().getFirstName() + " " + participant.getParticipantVO().getLastName() + " Is unmuted now!");

    }

    @Override
    public void audioCallMutedByAdmin() {
        vibrate();
        showToast("Call creator muted you!");
        updateMuteButton(true);
    }

    @Override
    public void audioCallUnMutedByAdmin() {
        vibrate();
        showToast("Call creator unmuted you!");
        updateMuteButton(false);
    }

    @Override
    public void callParticipantCanceledCall(String name) {
        showToast(name + " " + " canceled the call!");
    }

    @Override
    public void hideCallRequest() {

        hideRequestCallView();
        hideInCallView();
        updateStatus("You've accepted call with another device");

    }

    @Override
    public void updateStatus(String message) {
        runOnUiThread(() -> {
            try {
                tvStatus.setText(" " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void showToast(String text) {
        runOnUiThread(() -> Toast.makeText(this, text, Toast.LENGTH_LONG)
                .show());
    }


    @Override
    public void showContactsFragment(ContactsFragment fragment) {


        runOnUiThread(() -> {
            fabContacts.hide();
            if (getSupportFragmentManager().findFragmentByTag("CFRAG") == null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_call, fragment, "CFRAG")
                        .addToBackStack("CFRAG")
                        .commit();
        });

    }

    @Override
    public void updateContactsFragment(ArrayList<ContactsWrapper> contactsWrappers) {

        if (getSupportFragmentManager().findFragmentByTag("CFRAG") != null) {


            ContactsFragment contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentByTag("CFRAG");
            if (contactsFragment != null) {
                contactsFragment.updateList(contactsWrappers);
            }

        }
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            fabContacts.show();
            Fragment f = getSupportFragmentManager().findFragmentByTag("CFRAG");

            if (f != null) {
                getSupportFragmentManager().
                        beginTransaction()
                        .remove(f)
                        .commit();
            }

            Fragment f2 = getSupportFragmentManager().findFragmentByTag("LFRAG");
            if (f2 != null) {
                getSupportFragmentManager().
                        beginTransaction()
                        .remove(f2)
                        .commit();
            }
        } else
            super.onBackPressed();

    }

    @Override
    protected void onPause() {
        presenter.onStop();
        super.onPause();
    }

    @Override
    protected void onResume() {
        presenter.onResume();
        super.onResume();
    }

    @Override
    public void onLoginNeeded() {


        LoginFragment loginFragment = new LoginFragment();

        if (getSupportFragmentManager().findFragmentByTag("LFRAG") == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_call, loginFragment, "LFRAG")
                    .addToBackStack("LFRAG")
                    .commit();


    }


}
