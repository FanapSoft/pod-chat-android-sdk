package com.fanap.podchat.chat;

import com.fanap.podchat.call.result_model.CallCancelResult;
import com.fanap.podchat.call.result_model.CallCreatedResult;
import com.fanap.podchat.call.result_model.GetCallParticipantResult;
import com.fanap.podchat.call.result_model.MuteUnMuteCallParticipantResult;
import com.fanap.podchat.call.result_model.RemoveFromCallResult;
import com.fanap.podchat.chat.assistant.model.AssistantHistoryVo;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.bot.result_model.CreateBotResult;
import com.fanap.podchat.chat.bot.result_model.DefineBotCommandResult;
import com.fanap.podchat.chat.bot.result_model.GetUserBotsResult;
import com.fanap.podchat.chat.bot.result_model.StartStopBotResult;
import com.fanap.podchat.chat.contact.result_model.ContactSyncedResult;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.call.result_model.CallStartResult;
import com.fanap.podchat.call.result_model.GetCallHistoryResult;
import com.fanap.podchat.call.result_model.CallReconnectResult;
import com.fanap.podchat.call.result_model.CallRequestResult;
import com.fanap.podchat.call.result_model.EndCallResult;
import com.fanap.podchat.call.result_model.JoinCallParticipantResult;
import com.fanap.podchat.call.result_model.LeaveCallResult;
import com.fanap.podchat.chat.messge.ResultUnreadMessagesCount;
import com.fanap.podchat.chat.ping.result.StatusPingResult;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.thread.respone.CloseThreadResult;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.OutPutNotSeenDurations;
import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.OutputSignalMessage;
import com.fanap.podchat.model.ResultAddContact;
import com.fanap.podchat.model.ResultAddParticipant;
import com.fanap.podchat.model.ResultBlock;
import com.fanap.podchat.model.ResultBlockList;
import com.fanap.podchat.model.ResultClearHistory;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.model.ResultLeaveThread;
import com.fanap.podchat.model.ResultMapReverse;
import com.fanap.podchat.model.ResultMessage;
import com.fanap.podchat.model.ResultMute;
import com.fanap.podchat.model.ResultNewMessage;
import com.fanap.podchat.model.ResultNotSeenDuration;
import com.fanap.podchat.model.ResultParticipant;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.chat.pin.pin_thread.model.ResultPinThread;
import com.fanap.podchat.model.ResultRemoveContact;
import com.fanap.podchat.model.ResultSetAdmin;
import com.fanap.podchat.model.ResultSignalMessage;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUpdateContact;
import com.fanap.podchat.model.ResultUserInfo;

import java.util.List;

public interface ChatListener {

    default void onError(String content, ErrorOutPut error) {


    }

    default void onGetContacts(String content, ChatResponse<ResultContact> response) {
    }

    default void onGetHistory(String content, ChatResponse<ResultHistory> history) {
    }

    default void onGetThread(String content, ChatResponse<ResultThreads> thread) {

    }

    default void onThreadInfoUpdated(String content, ChatResponse<ResultThread> response) {

    }

    default void onBlock(String content, ChatResponse<ResultBlock> response) {

    }

    default void onUnBlock(String content, ChatResponse<ResultBlock> response) {

    }

    default void onSeen(String content, ChatResponse<ResultMessage> response) {

    }

    default void onDeliver(String content, ChatResponse<ResultMessage> response) {

    }

    default void onSent(String content, ChatResponse<ResultMessage> response) {

    }

    default void onMuteThread(String content, ChatResponse<ResultMute> response) {

    }

    default void onUnmuteThread(String content, ChatResponse<ResultMute> response) {

    }

    default void onUserInfo(String content, ChatResponse<ResultUserInfo> response) {

    }

    default void onCreateThread(String content, ChatResponse<ResultThread> response) {

    }

    default void onGetThreadParticipant(String content, ChatResponse<ResultParticipant> response) {

    }

    default void onGetThreadParticipant(ChatResponse<ResultParticipant> outPutParticipant) {

    }

    default void onEditedMessage(String content, ChatResponse<ResultNewMessage> response) {

    }

    default void onContactAdded(String content, ChatResponse<ResultAddContact> response) {

    }

    default void handleCallbackError(Throwable cause) throws Exception {

    }

    default void onRemoveContact(String content, ChatResponse<ResultRemoveContact> response) {

    }

    default void onRenameThread(String content, OutPutThread outPutThread) {

    }

    default void onMapSearch(String content, OutPutMapNeshan outPutMapNeshan) {

    }

    default void onMapRouting(String content) {

    }

    default void onNewMessage(String content, ChatResponse<ResultNewMessage> response) {

    }

    default void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> response) {

    }

    default void onUpdateContact(String content, ChatResponse<ResultUpdateContact> response) {

    }

    default void onUploadFile(String content, ChatResponse<ResultFile> response) {

    }

    default void onUploadImageFile(String content, ChatResponse<ResultImageFile> response) {

    }

    default void onSyncContact(String content, ChatResponse<Contacts> chatResponse) {

    }

    default void onSearchContact(String content, ChatResponse<ResultContact> response) {

    }

    default void onThreadAddParticipant(String content, ChatResponse<ResultAddParticipant> response) {

    }

    default void onThreadRemoveParticipant(String content, ChatResponse<ResultParticipant> response) {

    }

    default void onThreadLeaveParticipant(String content, ChatResponse<ResultLeaveThread> response) {

    }

    default void OnMapReverse(String json, ChatResponse<ResultMapReverse> response) {
    }

    @Deprecated
    default void onContactsLastSeenUpdated(String content) {
    }

    default void onChatState(String state) {

    }

    default void onGetBlockList(String content, ChatResponse<ResultBlockList> response) {
    }

    default void onUpdateThreadInfo(String threadJson, ChatResponse<ResultThread> response) {
    }

    default void OnDeliveredMessageList(String content, ChatResponse<ResultParticipant> response) {
    }

    default void OnSeenMessageList(String content, ChatResponse<ResultParticipant> response) {
    }

    default void OnStaticMap(ChatResponse<ResultStaticMapImage> response) {
    }

    default void OnRemovedFromThread(String content, ChatResponse<ResultThread> chatResponse) {

    }

    @Deprecated
    default void onLogEvent(String log) {

    }
    default void onLogEvent(String logName, String json){}

    default void OnClearHistory(String content, ChatResponse<ResultClearHistory> chatResponse) {

    }


    default void onGetThreadAdmin(String content, ChatResponse<ResultParticipant> chatResponse) {
    }

    default void OnNotSeenDuration(OutPutNotSeenDurations resultNotSeen) {
    }


    @Deprecated
    default void OnSignalMessageReceive(OutputSignalMessage output) {
    }

    default void onSignalMessageReceived(OutputSignalMessage output) {
    }

    default void onSignalMessageReceived(ChatResponse<ResultSignalMessage> result) {
    }

    default void OnSetRule(ChatResponse<ResultSetAdmin> outputSetRoleToUser) {
    }

    @Deprecated
    default void onGetThreadAdmin(String jsonData){}

    default void onPinThread(ChatResponse<ResultPinThread> response){}

    default void onUnPinThread(ChatResponse<ResultPinThread> response){}

    default void onRemoveRoleFromUser(ChatResponse<ResultSetAdmin> outputSetRoleToUser){}

    default void onPinMessage(ChatResponse<ResultPinMessage> response){}

    default void onUnPinMessage(ChatResponse<ResultPinMessage> response){}

    default void onGetCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> response){}

    default void onGetMentionList(ChatResponse<ResultHistory> response){}
    default void onGetHashTagList(ChatResponse<ResultHistory> response){}

    default void onTypingSignalTimeout(long threadId){}

    default void onLowFreeSpace(long bytesAvailable){}

    default void onContactsLastSeenUpdated(ChatResponse<ResultNotSeenDuration> response){}

    default void onChatProfileUpdated(ChatResponse<ResultUpdateProfile> response){}

    default void onUniqueNameIsAvailable(ChatResponse<ResultIsNameAvailable> response){}

    default void onJoinPublicThread(ChatResponse<ResultJoinPublicThread> response){}

    default void onGetUnreadMessagesCount(ChatResponse<ResultUnreadMessagesCount> response){}

    default void onCreateThread(ChatResponse<ResultThread> response){}

    default void onBotCreated(ChatResponse<CreateBotResult> response){}

    default void onBotCommandsDefined(ChatResponse<DefineBotCommandResult> response){}

    default void onBotStopped(ChatResponse<StartStopBotResult> response){}

    default void onBotStarted(ChatResponse<StartStopBotResult> response){}
    default void onUserBots(ChatResponse<GetUserBotsResult> response){}

    default void onContactsSynced(ChatResponse<ContactSyncedResult> response){}

    default void onPingStatusSent(ChatResponse<StatusPingResult> response){}

    default void onReceiveCallRequest(ChatResponse<CallRequestResult> response){}

    default void onReceiveGroupCallRequest(ChatResponse<CallRequestResult> response){}

    default void onCallRequestRejected(ChatResponse<CallRequestResult> response){}

    default void onVoiceCallStarted(ChatResponse<CallStartResult> response){}

    default void onVoiceCallEnded(ChatResponse<EndCallResult> response){}

    default void onReceiveCallHistory(ChatResponse<GetCallHistoryResult> response){}

    default void onCallReconnect(ChatResponse<CallReconnectResult> response){}

    default void onCallConnect(ChatResponse<CallReconnectResult> response){}

    default void onCallDelivered(ChatResponse<CallDeliverResult> response){}

    default void onCallParticipantLeft(ChatResponse<LeaveCallResult> response){}

    default void onCallParticipantJoined(ChatResponse<JoinCallParticipantResult> response){}

    default void onEndCallRequestFromNotification(){}

    default void onCallParticipantRemoved(ChatResponse<RemoveFromCallResult> response){}

    default void onRemovedFromCall(ChatResponse<RemoveFromCallResult> response){}

    default void onThreadClosed(ChatResponse<CloseThreadResult> response){}
    default void onThreadTypeChanged(ChatResponse<Thread> response){}

    default void onActiveCallParticipantsReceived(ChatResponse<GetCallParticipantResult> response){}

    default void onCallCreated(ChatResponse<CallCreatedResult> response){}

    default void onAudioCallMuted(ChatResponse<MuteUnMuteCallParticipantResult> response){}

    default void onAudioCallUnMuted(ChatResponse<MuteUnMuteCallParticipantResult> response){}

    default void onMutedByAdmin(ChatResponse<MuteUnMuteCallParticipantResult> response){}

    default void onUnMutedByAdmin(ChatResponse<MuteUnMuteCallParticipantResult> response){}

    default void onCallParticipantMuted(ChatResponse<MuteUnMuteCallParticipantResult> response){}

    default void onCallParticipantUnMuted(ChatResponse<MuteUnMuteCallParticipantResult> response){}

    default void onCallParticipantCanceledCall(ChatResponse<CallCancelResult> response){}
    default void onAnotherDeviceAcceptedCall(){}

    default void onRegisterAssistant(ChatResponse<List<AssistantVo>> response){}
    default void onDeActiveAssistant(ChatResponse<List<AssistantVo>> response){}
    default void onGetAssistants(ChatResponse<List<AssistantVo>> response){}
    default void onGetAssistantHistory(ChatResponse<List<AssistantHistoryVo>> response){}
}
