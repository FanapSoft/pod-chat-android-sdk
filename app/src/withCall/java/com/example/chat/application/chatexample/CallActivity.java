package com.example.chat.application.chatexample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.fanap.podcall.util.CallPermissionHandler;
import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.call.contacts.ContactsFragment;
import com.fanap.podchat.call.contacts.ContactsWrapper;
import com.fanap.podchat.call.history.HistoryAdaptor;
import com.fanap.podchat.call.login.LoginFragment;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.example.BuildConfig;
import com.fanap.podchat.example.R;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.util.ChatConstant;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class CallActivity extends AppCompatActivity implements CallContract.view,
        LoginFragment.ILoginInterface,
        ContactsFragment.IContactsFragment {


    private static final String TAG = "CHAT_SDK_CALL";
    public static final long[] VIB_PATTERN = {0, 1000, 1000};


    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private CallContract.presenter presenter;

    Button buttonStartCallById;

    TextView tvStatus, tvCallerName, tvHistory, tvCalleeName, tvVersion;

    View callRequestView, inCallView;
    ConstraintLayout constraintHolder, constraintChild;
    ImageButton buttonRejectCall, buttonAcceptCall, buttonEndCall, buttonMute, buttonSpeaker, buttonStartScreenShare, buttonStartCallRecord, buttonAddCallParticipant,
            imgBtnSwitchCamera;
    EditText etSandboxThreadId, etNewParticipantToAdd;

    FrameLayout frameLayout;
    FloatingActionButton fabContacts;

    boolean enableVibrate = false;
    boolean enableRing = false;
    Vibrator vibrator;
    Ringtone ringtone;

    boolean isMute = false;
    boolean isSpeakerOn = false;

    CallPartnerView localCallPartner,
            remoteCallPartner1,
            remoteCallPartner2,
            remoteCallPartner3,
            remoteCallPartner4;

    private boolean isVideoPaused = false;

    private RecyclerView recyclerViewHistory;
    private ViewSwitcher viewSwitcherRecentCalls;
    ScheduledExecutorService ex;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        init();

        setListeners();

    }

    private void connect() {
        presenter.connect();
    }

    private void setListeners() {

        buttonAcceptCall.setOnClickListener(v -> {
            cancelVib();
            stopRingtone();
            onPreStartCall();
            presenter.acceptIncomingCall();

        });

        buttonRejectCall.setOnClickListener((v) -> {
            cancelVib();
            stopRingtone();
            setInitState();
            presenter.rejectIncomingCall();
        });


        buttonEndCall.setOnClickListener(v -> {
            onCallEnded();
            presenter.endRunningCall();
        });

        buttonEndCall.setOnLongClickListener(v -> {
            terminateCall();
            return true;
        });

        buttonMute.setOnClickListener(v -> {
            vibrate();
            presenter.switchMute();
            toggleMute((ImageButton) v);
        });

        buttonSpeaker.setOnClickListener(v -> {
            vibrate();
            presenter.switchSpeaker();
            toggleSpeaker((ImageButton) v);
        });

        buttonAddCallParticipant.setOnClickListener(v ->
        {
            presenter.addCallParticipant();

        });

        buttonStartCallById.setOnClickListener(v -> {
            presenter.requestP2PCallWithUserId(Integer.parseInt(etSandboxThreadId.getText().toString()));
        });


        fabContacts.setOnClickListener(v -> {
            presenter.getContact();
        });

        imgBtnSwitchCamera.setOnClickListener(v -> {
            scaleIt(v);
            presenter.switchCamera();
        });

        localCallPartner.setOnClickListener(v -> {
            if (isVideoPaused) {
                presenter.resumeVideo();
            } else {
                presenter.pauseVideo();
            }

            isVideoPaused = !isVideoPaused;
        });

        buttonStartScreenShare.setOnClickListener(v -> {
            scaleIt(v);
            presenter.onShareScreenTouched();
        });

        buttonStartCallRecord.setOnClickListener(v -> {
//            scaleIt(v);
//            presenter.onRecordButtonTouched();
            showToast("Not available yet!");
        });


        View.OnClickListener cllPartnersListener = this::swapMainCallPartner;
//        remoteCallPartner1.setOnClickListener(cllPartnersListener);
        remoteCallPartner2.setOnClickListener(cllPartnersListener);
        remoteCallPartner3.setOnClickListener(cllPartnersListener);
        remoteCallPartner4.setOnClickListener(cllPartnersListener);
    }

    private void onPreStartCall() {
        runOnUiThread(() -> {
            callRequestView.setVisibility(View.GONE);
            inCallView.setVisibility(View.VISIBLE);
            buttonStartCallById.setVisibility(View.VISIBLE);
            tvCallerName.setText("");
            fabContacts.hide();
        });
    }

    private void terminateCall() {
        onCallEnded();


        presenter.terminateCall();
    }

    private void swapMainCallPartner(View v) {

        Boolean swapped = (Boolean) v.getTag();

        if (swapped != null && swapped) {
            animatePartnerViewDown(v);
        } else {
            pushPartnerViewUp(v);
        }

    }

    private void pushPartnerViewUp(View v) {
        moveViewToHolder(v);
    }

    private void animatePartnerViewDown(View v) {
        moveToChild(v);
    }

    private void moveViewToHolder(View v) {
        v.setTag(true);
        ViewGroup.LayoutParams vL = v.getLayoutParams();
        vL.width = -1;
        vL.height = -1;
        v.setLayoutParams(vL);
    }

    private void moveToChild(View v) {
        v.setTag(false);
        ViewGroup.LayoutParams vL = v.getLayoutParams();
        vL.width = 0;
        vL.height = 0;
        v.setLayoutParams(vL);
    }


    private void toggleSpeaker(ImageButton v) {

        isSpeakerOn = !isSpeakerOn;

        if (isSpeakerOn) {
            buttonSpeaker.setAlpha(1f);
            buttonSpeaker.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_white_background));
        } else {
            buttonSpeaker.setAlpha(0.6f);
            buttonSpeaker.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
        }

    }

    private void toggleMute(ImageButton v) {

        isMute = !isMute;

        updateMuteButton(isMute);
    }

    private void updateMuteButton(boolean isMute) {

        if (isMute) {
            buttonMute.setAlpha(1f);
            buttonMute.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_white_background));
        } else {
            buttonMute.setAlpha(0.6f);
            buttonMute.setBackground(ContextCompat.getDrawable(this, android.R.color.transparent));
        }
    }

    private void vibrate() {
        if (enableVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));

            } else {
                //deprecated in API 26
                vibrator.vibrate(100);
            }
        }
    }

    private void vibrateE() {
        if (enableVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                vibrator.vibrate(VibrationEffect.createWaveform(VIB_PATTERN, 0));

            } else {
                //deprecated in API 26
                vibrator.vibrate(VIB_PATTERN, 0);
            }
        }
    }

    private void ring() {
        if (enableRing) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone.setVolume(0.1f);
            }
            ringtone.play();
        }
    }

    private boolean stopRingtone() {
        if (ringtone != null) {
            ringtone.stop();
            ringtone = null;
            return true;
        }
        return false;
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


    }


    private void init() {


        viewSwitcherRecentCalls = findViewById(R.id.viewSwitcherRecentCalls);

        presenter = new CallPresenter(this, this, this);

        recyclerViewHistory = findViewById(R.id.recyclerViewHistories);


        buttonAddCallParticipant = findViewById(R.id.buttonAddPartner);

        tvStatus = findViewById(R.id.tvStatus);
        tvCallerName = findViewById(R.id.tvCallerName);
        tvCalleeName = findViewById(R.id.tvCalleeName);
        tvHistory = findViewById(R.id.tvHistory);
        tvVersion = findViewById(R.id.tvVersion);

        callRequestView = findViewById(R.id.viewCallRequest);
        inCallView = findViewById(R.id.viewCall);

        buttonAcceptCall = findViewById(R.id.buttonAccept);
        buttonRejectCall = findViewById(R.id.buttonReject);
        buttonEndCall = findViewById(R.id.buttonEndCall);
        buttonStartCallById = findViewById(R.id.btnSandboxCall);

        buttonStartScreenShare = findViewById(R.id.buttonScreenShare);
        buttonStartCallRecord = findViewById(R.id.buttonStartRecord);


        buttonMute = findViewById(R.id.buttonMute);
        buttonSpeaker = findViewById(R.id.buttonSpeakerOn);

        imgBtnSwitchCamera = findViewById(R.id.imgBtnSwitchCamera);

        etSandboxThreadId = findViewById(R.id.etSandBoxPartnerId);
        etNewParticipantToAdd = findViewById(R.id.etNewParticipant);

        frameLayout = findViewById(R.id.frame_call);
        fabContacts = findViewById(R.id.fabShowContactsList);

        Logger.addLogAdapter(new AndroidLogAdapter());

        localCallPartner = findViewById(R.id.localPartnerCameraView);
        remoteCallPartner1 = findViewById(R.id.remotePartnerView1);
        remoteCallPartner2 = findViewById(R.id.remotePartnerView2);
        remoteCallPartner3 = findViewById(R.id.remotePartnerView3);
        remoteCallPartner4 = findViewById(R.id.remotePartnerView4);

        constraintHolder = findViewById(R.id.constraintHolder);

        List<CallPartnerView> views = new ArrayList<>();
        views.add(remoteCallPartner1);
        views.add(remoteCallPartner2);
        views.add(remoteCallPartner3);
        views.add(remoteCallPartner4);

        presenter.setupVideoCallParam(localCallPartner, views);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        CallInfo callInfo = getIntent().getParcelableExtra(ChatConstant.POD_CALL_INFO);

        Log.e(TAG, "Call info: " + callInfo);

        if (callInfo != null) {
            presenter.setCallInfo(callInfo);
            showInCallView();
        }

        tvVersion.setText("v" + BuildConfig.VERSION_NAME);
    }

    private void runTestCode() {

        inCallView.setVisibility(View.VISIBLE);
        fabContacts.hide();

//        remoteCallPartner4.setVisibility(View.VISIBLE);
//        remoteCallPartner1.setVisibility(View.VISIBLE);
//        remoteCallPartner3.setVisibility(View.VISIBLE);
//        remoteCallPartner2.setVisibility(View.VISIBLE);

//        new Handler().postDelayed(() -> {
//            ViewGroup.LayoutParams p = localCallPartner.getLayoutParams();
//            p.width += (p.width * 100) / 100;
//            p.height += (p.height * 100) / 100;
//            localCallPartner.setLayoutParams(p);
//        }, 20000);

    }

    @Override
    public void switchToRecentCallsLoading() {
        runOnUiThread(()->{
            viewSwitcherRecentCalls.setDisplayedChild(0);
        });
    }

    @Override
    public void showCallRequest(String callerName) {

        vibrateE();
        ring();

        runOnUiThread(() -> {
            callRequestView.setVisibility(View.VISIBLE);
            buttonStartCallById.setVisibility(View.INVISIBLE);
            tvCallerName.setText(" " + callerName);
            fabContacts.hide();
        });

    }

    @Override
    public void onVoiceCallRequestRejected(String callerName) {


        cancelVib();
        stopRingtone();

        runOnUiThread(() -> {
            switchToRecentCallsRecycler();
            buttonStartCallById.setVisibility(View.VISIBLE);
            tvStatus.setText(String.format("%s Rejected Your Call Request", callerName));
        });


    }


    @Override
    public void onVoiceCallStarted(String uniqueId, String clientId) {

        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        cancelVib();

        stopRingtone();

        runOnUiThread(this::showInCallView);


    }

    private void cancelVib() {

        vibrator.cancel();

    }

    private void showInCallView() {
        runOnUiThread(() -> {
            inCallView.setVisibility(View.VISIBLE);
            callRequestView.setVisibility(View.INVISIBLE);
            fabContacts.hide();
            buttonStartCallById.setVisibility(View.INVISIBLE);
            tvCalleeName.setText("");
        });

        AtomicInteger sec = new AtomicInteger();
        sec.set(0);
        AtomicInteger min = new AtomicInteger();
        min.set(0);

        if (ex == null) {
            ex = Executors.newSingleThreadScheduledExecutor();

            ex.scheduleAtFixedRate(
                    () -> runOnUiThread(
                            () -> {
                                if (sec.get() < 10)
                                    tvCalleeName.setText(min.get() + ":0" + sec.getAndIncrement());
                                else if (sec.get() > 9 && sec.get() < 60) {
                                    tvCalleeName.setText(min.get() + ":" + sec.getAndIncrement());
                                } else {
                                    sec.set(0);
                                    tvCalleeName.setText(min.incrementAndGet() + ":00");
                                }
                            }),
                    0,
                    1,
                    TimeUnit.SECONDS);
        }

    }

    private void hideInCallView() {
        runOnUiThread(() -> {
            inCallView.setVisibility(View.GONE);
            callRequestView.setVisibility(View.INVISIBLE);
            fabContacts.show();
            buttonStartCallById.setVisibility(View.VISIBLE);
        });
    }

    private void showRequestCallView() {
        runOnUiThread(() -> {
            inCallView.setVisibility(View.VISIBLE);
            callRequestView.setVisibility(View.INVISIBLE);
            fabContacts.hide();
            buttonStartCallById.setVisibility(View.INVISIBLE);
        });
    }

    private void hideRequestCallView() {
        runOnUiThread(() -> {
            inCallView.setVisibility(View.GONE);
            callRequestView.setVisibility(View.GONE);
            fabContacts.show();
            buttonStartCallById.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onVoiceCallEnded(String uniqueId, long subjectId) {

        onCallEnded();


    }

    private void onCallEnded() {

        setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);

        stopCallTimer();

        runOnUiThread(() -> {

            inCallView.setVisibility(View.INVISIBLE);
            callRequestView.setVisibility(View.INVISIBLE);
//            buttonTestCall.setVisibility(View.VISIBLE);
            fabContacts.show();
            buttonStartCallById.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Call has been ended", Toast.LENGTH_SHORT).show();
            tvStatus.setText("Call has been ended");
            tvCalleeName.setText("");
        });

    }

    private void stopCallTimer() {
        if (ex != null) {
            ex.shutdown();
            ex = null;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onGetCallHistory(List<CallVO> calls) {

        runOnUiThread(() -> {

            fabContacts.show();

            switchToRecentCallsRecycler();

            HistoryAdaptor hAdaptor = new HistoryAdaptor(
                    new ArrayList<>(calls),
                    this, call ->
            {
                tvCalleeName.setText("Calling " + call.getPartnerParticipantVO().getFirstName() + " " + call.getPartnerParticipantVO().getLastName());

                if (call.getPartnerParticipantVO().getContactId() > 0) {
                    presenter.requestP2PCallWithContactId(
                            (int) call.getPartnerParticipantVO().getContactId()
                    );
                } else if (call.getPartnerParticipantVO().getId() > 0) {
                    presenter.requestP2PCallWithUserId(
                            (int) call.getPartnerParticipantVO().getId()
                    );
                }
            }
            );

            recyclerViewHistory.setAdapter(hAdaptor);

            recyclerViewHistory.setLayoutManager(
                    new LinearLayoutManager(this,
                            LinearLayoutManager.VERTICAL,
                            false)
            );

        });

    }

    private void switchToRecentCallsRecycler() {
        viewSwitcherRecentCalls.setDisplayedChild(1);
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
        ring();
        runOnUiThread(() -> {
            Toast.makeText(this, "Group Call from " + callerName, Toast.LENGTH_SHORT).show();
            callRequestView.setVisibility(View.VISIBLE);
            buttonStartCallById.setVisibility(View.INVISIBLE);
            tvCallerName.setText(callerName + " from " + title);
            fabContacts.hide();
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
    public void onTokenReceived(String token) {

        hideFragmentByTag("LFRAG");

        presenter.connect(token);
    }

    private void hideFragmentByTag(String lfrag) {
        Fragment f2 = getSupportFragmentManager().findFragmentByTag(lfrag);
        if (f2 != null) {
            getSupportFragmentManager().
                    beginTransaction()
                    .remove(f2)
                    .commit();
        }
    }

    @Override
    public void onLocalTokenSelected(String token) {

        hideFragmentByTag("LFRAG");
        connect();

    }






    @Override
    public void updateUserInfo(UserInfo user) {


        runOnUiThread(() -> {
            tvStatus.append(" " + "\n" + user.getName());
            tvStatus.append(" " + "\n" + user.getId());
            tvStatus.append(" " + "\n" + "Server name: " + ServerConfig.serverType);

        });


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
    public void callParticipantMuted(CallParticipantVO participant, CallPartnerView partnerView) {
        showToast(participant.getParticipantVO().getFirstName() + " " + participant.getParticipantVO().getLastName() + " is muted now!");
        runOnUiThread(() -> {
            if (partnerView != null)
                partnerView.setDisplayIsMuteIcon(true);
        });
    }

    @Override
    public void callParticipantUnMuted(CallParticipantVO participant, CallPartnerView partnerView) {
        showToast(participant.getParticipantVO().getFirstName() + " " + participant.getParticipantVO().getLastName() + " Is unmuted now!");
        runOnUiThread(() -> {
            if (partnerView != null)
                partnerView.setDisplayIsMuteIcon(false);
        });
    }

    @Override
    public void audioCallMutedByAdmin() {
        vibrate();
        showToast("Call creator muted you!");
        if (localCallPartner != null) {
            localCallPartner.setDisplayIsMuteIcon(true);
        }
        updateMuteButton(true);
    }

    @Override
    public void audioCallUnMutedByAdmin() {
        vibrate();
        showToast("Call creator UnMuted you!");
        if (localCallPartner != null) {
            localCallPartner.setDisplayIsMuteIcon(true);
        }
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
            if (getSupportFragmentManager().findFragmentByTag("CFRAG") == null) {
                fabContacts.hide();

                FrameLayout frameLayout = findViewById(R.id.frame_call);
                frameLayout.setVisibility(View.VISIBLE);
                try {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frame_call, fragment, "CFRAG")
                            .addToBackStack("CFRAG")
                            .commit();
                } catch (Exception e) {
                    fabContacts.show();
                    Log.wtf(TAG, e);
                }
            }
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

        switchToRecentCallsRecycler();

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            fabContacts.show();
            hideFragmentByTag("CFRAG");

            hideFragmentByTag("LFRAG");
        } else
            super.onBackPressed();

    }

    @Override
    protected void onPause() {
        cancelVib();
        stopRingtone();
        presenter.onActivityPaused();
        super.onPause();
    }

    @Override
    protected void onResume() {
        presenter.onResume();
        super.onResume();
    }

    @Override
    public void onLoginNeeded() {


        if (fabContacts != null) {
            fabContacts.hide();
        }

        LoginFragment loginFragment = new LoginFragment();

        if (getSupportFragmentManager().findFragmentByTag("LFRAG") == null)
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_call, loginFragment, "LFRAG")
                    .addToBackStack("LFRAG")
                    .commit();


    }

    @Override
    public void hideFabContactButton() {
        runOnUiThread(() -> fabContacts.hide());
    }

    @Override
    public void onError(String message) {

    }


    @Override
    public void onContactsSelected(ContactsWrapper contact) {
        presenter.onContactSelected(contact);
    }

    @Override
    public void showFabContact() {
        fabContacts.show();
    }

    @Override
    public void updateTvCallee(String txt) {
        tvCalleeName.setText(txt);
    }

    @Override
    public void hideContactsFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            hideFragmentByTag("CFRAG");
        }
    }

    @Override
    public void onScreenIsSharing() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Sharing Started", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onScreenShareEnded() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Sharing Screen Stopped", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onCallParticipantSharedScreen() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Participant start sharing screen", Toast.LENGTH_SHORT).show();
//            int width = remoteCallPartner1.getLayoutParams().width;
//            remoteCallPartner1.getLayoutParams().width = width - ((width * 50) / 100);
//            remoteCallPartner1.animate().translationX(-250f);
//            remoteCallPartner2.setVisibility(View.VISIBLE);
//            remoteCallPartner2.getLayoutParams().width = remoteCallPartner2.getLayoutParams().width - ((remoteCallPartner2.getLayoutParams().width * 50) / 100);
        });
    }

    @Override
    public void onCallParticipantStoppedScreenSharing() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Participant Stopped sharing", Toast.LENGTH_SHORT).show();
//            DisplayMetrics met = this.getResources().getDisplayMetrics();
//            remoteCallPartner1.getLayoutParams().width = met.widthPixels;
//            remoteCallPartner1.getLayoutParams().height = met.heightPixels / 3;
            remoteCallPartner2.animate().scaleX(0.0f)
                    .scaleY(0.0f)
                    .setInterpolator(new BounceInterpolator())
                    .setDuration(250)
                    .withEndAction(() -> {
                        remoteCallPartner2.setVisibility(View.GONE);
                    });
        });
    }


    @Override
    public void onCallRecordingStarted() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Recording Started", Toast.LENGTH_SHORT).show();
            liveAnim(buttonStartCallRecord);
        });
    }

    private void liveAnim(View v) {
        v.animate()
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setDuration(250)
                .withEndAction(() -> {
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(250)
                            .start();
                    if (v.isActivated())
                        liveAnim(v);
                }).start();
    }

    @Override
    public void onCallRecordingStopped() {
        showToast("Call Recording Stopped...");
        buttonStartCallRecord.setActivated(false);

    }

    @Override
    public void onParticipantStartedRecordingCall(String name) {
        showToast(name + " Started Recording Call");
    }

    @Override
    public void onParticipantStoppedRecordingCall(String name) {
        showToast(name + " Stopped Recording Call");
    }

    @Override
    public void setInitState() {


        runOnUiThread(() -> {
            localCallPartner.setVisibility(View.GONE);
            fabContacts.show();
            hideRequestCallView();
        });

    }

    @Override
    public void showMessage(String msg) {
        showToast(msg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || (keyCode == KeyEvent.KEYCODE_VOLUME_UP) || keyCode == KeyEvent.KEYCODE_POWER) {

            if (!stopRingtone()) {
                return super.onKeyDown(keyCode, event);
            } else {
                cancelVib();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        presenter.handleActivityResult(requestCode, resultCode, data);
    }
}
