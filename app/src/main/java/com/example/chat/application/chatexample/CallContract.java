package com.example.chat.application.chatexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.fanap.podcall.view.CallPartnerView;
import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.call.contacts.ContactsFragment;
import com.fanap.podchat.call.contacts.ContactsWrapper;
import com.fanap.podchat.call.model.CallInfo;
import com.fanap.podchat.call.model.CallParticipantVO;
import com.fanap.podchat.call.model.CallVO;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.assistant.request_model.DeActiveAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.RegisterAssistantRequest;
import com.fanap.podchat.chat.bot.request_model.CreateBotRequest;
import com.fanap.podchat.chat.bot.request_model.DefineBotCommandRequest;
import com.fanap.podchat.chat.bot.request_model.StartAndStopBotRequest;
import com.fanap.podchat.chat.bot.result_model.CreateBotResult;
import com.fanap.podchat.chat.bot.result_model.DefineBotCommandResult;
import com.fanap.podchat.chat.hashtag.model.RequestGetHashTagList;
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.chat.messge.ResultUnreadMessagesCount;
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread;
import com.fanap.podchat.chat.ping.result.StatusPingResult;
import com.fanap.podchat.chat.thread.public_thread.RequestCheckIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.chat.thread.public_thread.RequestJoinPublicThread;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.requestobject.RemoveParticipantRequest;
import com.fanap.podchat.requestobject.RequestAddContact;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestBlockList;
import com.fanap.podchat.requestobject.RequestClearHistory;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.requestobject.RequestCreateThreadWithFile;
import com.fanap.podchat.requestobject.RequestDeleteMessage;
import com.fanap.podchat.requestobject.RequestDeliveredMessageList;
import com.fanap.podchat.requestobject.RequestFileMessage;
import com.fanap.podchat.requestobject.RequestForwardMessage;
import com.fanap.podchat.requestobject.RequestGetAdmin;
import com.fanap.podchat.requestobject.RequestGetContact;
import com.fanap.podchat.requestobject.RequestGetFile;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestGetImage;
import com.fanap.podchat.requestobject.RequestGetPodSpaceFile;
import com.fanap.podchat.requestobject.RequestGetPodSpaceImage;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.requestobject.RequestLocationMessage;
import com.fanap.podchat.requestobject.RequestMapReverse;
import com.fanap.podchat.requestobject.RequestMapStaticImage;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestReplyFileMessage;
import com.fanap.podchat.requestobject.RequestReplyMessage;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
import com.fanap.podchat.requestobject.RequestSetAdmin;
import com.fanap.podchat.requestobject.RequestSetAuditor;
import com.fanap.podchat.requestobject.RequestSignalMsg;
import com.fanap.podchat.requestobject.RequestSpam;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.requestobject.RequestThreadInfo;
import com.fanap.podchat.requestobject.RequestThreadParticipant;
import com.fanap.podchat.requestobject.RequestUnBlock;
import com.fanap.podchat.requestobject.RequestUpdateContact;
import com.fanap.podchat.requestobject.RetryUpload;

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

        default void onScreenIsSharing(){}

        default void onCallParticipantSharedScreen(){}

        default void onScreenShareEnded(){}

        default void onCallParticipantStoppedScreenSharing(){}

        default void onCallRecordingStarted(){}
        default void onCallRecordingStopped(){}
        default void onParticipantStartedRecordingCall(String name){}
        default void onParticipantStoppedRecordingCall(String name){}
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

        void onShareScreenTouched();

        void handleActivityResult(int requestCode, int resultCode, Intent data);

        void onActivityPaused();

        void onRecordButtonTouched();
    }
}
