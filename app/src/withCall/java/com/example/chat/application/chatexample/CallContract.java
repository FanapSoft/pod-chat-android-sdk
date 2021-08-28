package com.example.chat.application.chatexample;

import android.app.Activity;

import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.call.contacts.ContactsFragment;
import com.fanap.podchat.call.contacts.ContactsWrapper;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestGetContact;

import java.util.ArrayList;
import java.util.List;


public interface CallContract {

    interface view {


        default void onGetUserInfo(ChatResponse<ResultUserInfo> outPutUserInfo) {
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


        default void onTokenExpired(){}


        default void onGetToken(String token){}

        default void onVoiceCallRequestReceived(String callerName){}

        default void onVoiceCallRequestRejected(String callerName){}

        default void onVoiceCallEnded(String uniqueId, long subjectId){}

        default void onVoiceCallStarted(String uniqueId, String clientId){}

        default void onGetCallHistory(List<CallVO> calls){}

        default void onCallReconnect(long callId){}

        default void onCallConnect(long callId){}

        default void onCallDelivered(CallDeliverResult result){}

        default void onGroupVoiceCallRequestReceived(String callerName, String title, List<Participant> participants){}

        default void onCallParticipantLeft(String response){}

        default void onCallParticipantJoined(String response){}

        default void onCallParticipantRemoved(String name){}

        default void onRemovedFromCall(){}

        default void updateStatus(String message){}

        default void onThreadClosed(long subjectId){}

        default void onCallCreated(long threadId){}

        default void audioCallMuted(){}
        default void audioCallUnMuted(){}

        default void callParticipantMuted(CallParticipantVO participant){}
        default void callParticipantUnMuted(CallParticipantVO participant){}

        default void audioCallMutedByAdmin(){}
        default void audioCallUnMutedByAdmin(){}


        default void callParticipantCanceledCall(String name){}

        default void hideCallRequest(){}

        default void showContactsFragment(ContactsFragment contactsWrappers){}

        default void updateContactsFragment(ArrayList<ContactsWrapper> contactsWrappers){}

        default void onGetSentryLogs(String logs){}

        default void onChatProfileUpdated(ResultUpdateProfile result){}

        default void onLoginNeeded(){}

        default void onLoadingContactsStarted(){}

        default void setInitState(){}
    }

    interface presenter {

        void enableAutoRefresh(Activity activity,String entry);

        void setToken(String token);

        void connect(String serverAddress, String appId, String severName, String token, String ssoHost
                , String platformHost, String fileServer, String typeCode);

        void connect(RequestConnect requestConnect);


        void getUserInfo(ChatHandler handler);


        void getContact(Integer count, Long offset, ChatHandler handler);


        void logOut();

        void closeChat();

        void getContact(RequestGetContact request);

        void acceptIncomingCall();

        void rejectIncomingCall();

        String getNameById(int partnerId);


        void onStart();

        void onStop();

        void onResume();

        void deliverNotification(String threadId);

        void clearNotifications();

        void endStream();

        void endRunningCall();

        void getCallHistory();

        void switchMute();

        void switchSpeaker();

        void requestGroupCall(boolean fifi, boolean zizi, boolean jiji);

        void addCallParticipant(String username, boolean fifiChecked, boolean jijiChecked, boolean ziziChecked);

        void setCallInfo(CallInfo callInfo);

        void requestMainOrSandboxCall(String query, boolean isGroupCall);

        void requestP2PCallWithP2PThreadId(int threadId);

        void requestP2PCallWithContactId(int contactId);

        void requestP2PCallWithUserId(int userId);

        void terminateCall();

        void removeCallParticipant(String etId, boolean checked, boolean checked1, boolean checked2);

        void getContact();

        void setupVideoCallParam(CallPartnerView localCallPartner, List<CallPartnerView> views);

        void switchCamera();

        void pauseVideo();

        void resumeVideo();
    }
}
