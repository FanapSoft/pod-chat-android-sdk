package com.example.chat.application.chatexample;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.RestrictTo;
import android.util.Pair;
import android.view.View;

import com.fanap.podcall.audio.AudioCallParam;
import com.fanap.podcall.model.VideoCallParam;
import com.fanap.podcall.screenshare.model.ScreenShareParam;
import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.call.contacts.ContactsFragment;
import com.fanap.podchat.call.contacts.ContactsWrapper;
import com.fanap.podchat.call.history.CallWrapper;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.call.setting.SettingFragment;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.UserInfo;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.requestobject.RequestGetContact;

import java.util.ArrayList;
import java.util.List;


public interface CallContract {

    interface view {


        default void updateUserInfo(UserInfo outPutUserInfo) {
        }

        default void onLogEvent(String log) {
        }

        default void onGetContacts(ChatResponse<ResultContact> outPutContact) {

        }

        default void onAddContact() {
        }


        default void onAddParticipant() {
        }

        default void onRemoveParticipant() {
        }

        default void onLeaveThread() {
        }


        default void onState(String state) {
        }


        default void onError(String message) {
        }


        default void onTokenExpired() {
        }

        default void showCallRequest(String callerName) {
        }

        default void onVoiceCallRequestRejected(String callerName) {
        }

        default void onVoiceCallEnded(String uniqueId, long subjectId) {
        }

        default void onVoiceCallStarted(String uniqueId, String clientId) {
        }

        default void onCallReconnect(long callId) {
        }

        default void onCallConnect(long callId) {
        }

        default void onCallDelivered(CallDeliverResult result) {
        }

        default void onGroupVoiceCallRequestReceived(String callerName, String title, List<Participant> participants) {
        }

        default void onCallParticipantLeft(String response) {
        }

        default void onCallParticipantJoined(String response) {
        }

        default void onCallParticipantRemoved(String name) {
        }

        default void onRemovedFromCall() {
        }

        default void updateStatus(String message) {
        }

        default void onGetCallHistory(List<CallWrapper> calls){}
        default void onThreadClosed(long subjectId) {
        }

        default void onCallCreated(long threadId) {
        }

        default void audioCallMuted() {
        }

        default void audioCallUnMuted() {
        }

        default void callParticipantMuted(CallParticipantVO participant, CallPartnerView partnerView) {
        }

        default void callParticipantUnMuted(CallParticipantVO participant, CallPartnerView partnerView) {
        }

        default void audioCallMutedByAdmin() {
        }

        default void audioCallUnMutedByAdmin() {
        }


        default void callParticipantCanceledCall(String name) {
        }

        default void hideCallRequest() {
        }

        default void showContactsFragment(ContactsFragment contactsWrappers) {
        }

        default void updateContactsFragment(ArrayList<ContactsWrapper> contactsWrappers) {
        }

        default void onGetSentryLogs(String logs) {
        }

        default void onChatProfileUpdated(ResultUpdateProfile result) {
        }

        default void onLoginNeeded() {
        }

        default void hideFabContactButton() {
        }
        default void showFabSetting(){}

        default void hideFabSetting(){}

        default void setInitState() {
        }

        default void onScreenIsSharing() {
        }

        default void onCallParticipantSharedScreen() {
        }

        default void onScreenShareEnded() {
        }

        default void onCallParticipantStoppedScreenSharing() {
        }

        default void onCallRecordingStarted() {
        }

        default void onCallRecordingStopped() {
        }

        default void onParticipantStartedRecordingCall(String name) {
        }

        default void onParticipantStoppedRecordingCall(String name) {
        }

        default void hideContactsFragment() {
        }

        default void showFabContact() {
        }

        default void updateTvCallee(String txt) {
        }

        default void updateTvCaller(String txt) {
        }

        default void showMessage(String msg) {
        }

        default void switchToRecentCallsLoading() {
        }

        default void hideCameraPreview() {
        }

        default void showCameraPreview() {
        }


        default void hideRemoteViews() {
        }

        default void showRemoteViews() {
        }

        default void showVideoCallElements() {
        }

        default void updateCallerImage(String profileUrl) {
        }

        default void updateCallImage(String profileUrl) {
        }


        default void addNewView(CallPartnerView partnerView) {
        }

        default void onGetActiveCalls(List<CallWrapper> calls){}

        default void removeCallItem(CallWrapper call){}

        default void showTurnOffIncomingVideosBtn(){}

        default void hideTurnOnIncomingVideosBtn(){}

        default void hideTurnOffIncomingVideosBtn(){}

        default void showTurnOnIncomingVideosBtn(){}

        default void showSettingFragment(SettingFragment fragment){}

        default void hideSettingFragment(){}
    }

    interface presenter {

        void enableAutoRefresh(Activity activity, String entry);

        void setToken(String token);

        void connect();

        void getUserInfo(ChatHandler handler);

        void getContact(Integer count, Long offset, ChatHandler handler);


        void logOut();

        void closeChat();

        void getContact(RequestGetContact request);

        void acceptIncomingCallWithVideo();

        void acceptIncomingCallWithAudio();

        void rejectIncomingCall();

        void onStart();

        void onStop();

        void onResume();

        void deliverNotification(String threadId);

        void clearNotifications();

        void endStream();

        void endRunningCall();


        void switchMute();

        void switchSpeaker();


        void addCallParticipant();

        void setCallInfo(CallInfo callInfo);

        void requestMainOrSandboxCall(String query, boolean isGroupCall);

        void requestP2PCallWithP2PThreadId(int threadId);

        void requestP2PCallWithContactId(int contactId, int callType);

        void requestP2PCallWithUserId(int userId, int callType);

        void terminateCall();

        void getContact();

        void setupVideoCallParam(CallPartnerView localCallPartner, List<CallPartnerView> views);

        void switchCamera();

        void onShareScreenTouched();

        void handleActivityResult(int requestCode, int resultCode, Intent data);

        void onActivityPaused();

        void onRecordButtonTouched();

        void onContactSelected(ContactsWrapper contact, int callType);

        void addContact(String name, String lastName, String id, int idType);

        void connect(String token);

        void requestAudioCall(CallWrapper call);

        void requestVideoCall(CallWrapper call);

        void rejectIncomingCallWithMessage(String msg);

        void turnOnCamera();

        void turnOffCamera();

        @RestrictTo(RestrictTo.Scope.TESTS)
        void prepareNewView(CallPartnerView partnerView);

        void requestGroupAudioCallByContactId(String callName, ArrayList<Long> selectContactIds);

        void requestGroupVideoCallByContactId(String callName, ArrayList<Long> selectContactIds);

        void turnOnIncomingVideos();

        void turnOffIncomingVideos();


        void showSetting();

        void hideSetting();

        void updateCallConfig(VideoCallParam videoCallParam, AudioCallParam audioCallParam, ScreenShareParam screenShareParam);

        void onCallPartnerViewSelected(CallPartnerView v);
    }
}
