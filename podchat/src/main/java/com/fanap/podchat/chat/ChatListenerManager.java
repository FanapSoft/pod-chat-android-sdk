package com.fanap.podchat.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanap.podchat.call.result_model.CallCancelResult;
import com.fanap.podchat.call.result_model.CallCreatedResult;
import com.fanap.podchat.call.result_model.CallDeliverResult;
import com.fanap.podchat.call.result_model.CallReconnectResult;
import com.fanap.podchat.call.result_model.CallRequestResult;
import com.fanap.podchat.call.result_model.CallStartResult;
import com.fanap.podchat.call.result_model.CallTimeOutResult;
import com.fanap.podchat.call.result_model.EndCallResult;
import com.fanap.podchat.call.result_model.GetCallHistoryResult;
import com.fanap.podchat.call.result_model.GetCallParticipantResult;
import com.fanap.podchat.call.result_model.JoinCallParticipantResult;
import com.fanap.podchat.call.result_model.LeaveCallResult;
import com.fanap.podchat.call.result_model.MuteUnMuteCallParticipantResult;
import com.fanap.podchat.call.result_model.RemoveFromCallResult;
import com.fanap.podchat.chat.assistant.model.AssistantHistoryVo;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.bot.result_model.CreateBotResult;
import com.fanap.podchat.chat.bot.result_model.DefineBotCommandResult;
import com.fanap.podchat.chat.bot.result_model.GetUserBotsResult;
import com.fanap.podchat.chat.bot.result_model.StartStopBotResult;
import com.fanap.podchat.chat.contact.result_model.ContactSyncedResult;
import com.fanap.podchat.chat.messge.ResultUnreadMessagesCount;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.chat.pin.pin_thread.model.ResultPinThread;
import com.fanap.podchat.chat.ping.result.StatusPingResult;
import com.fanap.podchat.chat.tag.result_model.TagListResult;
import com.fanap.podchat.chat.tag.result_model.TagParticipantResult;
import com.fanap.podchat.chat.tag.result_model.TagResult;
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
import com.fanap.podchat.model.ResultRemoveContact;
import com.fanap.podchat.model.ResultSetAdmin;
import com.fanap.podchat.model.ResultSignalMessage;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUpdateContact;
import com.fanap.podchat.model.ResultUserInfo;

import java.util.ArrayList;
import java.util.List;

public class ChatListenerManager {
    private final List<ChatListener> mListeners = new ArrayList<>();
    private boolean mSyncNeeded = true;
    private List<ChatListener> mCopiedListeners;

    public ChatListenerManager() {
    }

    public void addListener(@Nullable ChatListener listener, boolean log) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            mListeners.add(listener);
            mSyncNeeded = true;
        }
    }

    public void addListeners(@Nullable List<ChatListener> listeners) {
        if (listeners == null) {
            return;
        }

        synchronized (mListeners) {
            for (ChatListener listener : listeners) {
                if (listener == null) {
                    continue;
                }
                mListeners.add(listener);
                mSyncNeeded = true;
            }
        }
    }

    public void removeListener(@Nullable ChatListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            if (mListeners.remove(listener)) {
                mSyncNeeded = true;
            }
        }
    }

    public void removeListeners(@Nullable List<ChatListener> listeners) {
        if (listeners == null) {
            return;
        }

        synchronized (mListeners) {
            for (ChatListener listener : listeners) {
                if (listener == null) {
                    continue;
                }

                if (mListeners.remove(listener)) {
                    mSyncNeeded = true;
                }
            }
        }
    }

    public void clearListeners() {
        synchronized (mListeners) {
            if (mListeners.size() == 0) {
                return;
            }

            mListeners.clear();
            mCopiedListeners.clear();
        }
    }

    public List<ChatListener> getListeners() {
        return mListeners;
    }

    private List<ChatListener> getSynchronizedListeners() {
        synchronized (mListeners) {
            if (!mSyncNeeded) {
                return mCopiedListeners;
            }

            // Copy mListeners to copiedListeners.
            List<ChatListener> copiedListeners = new ArrayList<>(mListeners.size());

            copiedListeners.addAll(mListeners);

            // Synchronize.
            mCopiedListeners = copiedListeners;
            mSyncNeeded = false;

            return copiedListeners;
        }
    }

    public void callOnGetThread(String content, ChatResponse<ResultThreads> thread) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetThread(content, thread);

            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetThreadHistory(String content, ChatResponse<ResultHistory> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetHistory(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetContacts(String content, ChatResponse<ResultContact> outPutContact) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetContacts(content, outPutContact);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSentMessage(String content, ChatResponse<ResultMessage> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSent(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnTagCreated(String content, ChatResponse<TagResult> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onTagCreated(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnTagEdited(String content, ChatResponse<TagResult> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onTagEdited(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnTagParticipantAdded(String content, ChatResponse<TagParticipantResult> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnTagParticipantAdded(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnTagParticipantRemoved(String content, ChatResponse<TagParticipantResult> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnTagParticipantRemoved(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnTagList(String content, ChatResponse<TagListResult> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnTagList(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnTagDeleted(String content, ChatResponse<TagResult> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onTagDeleted(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSeenMessage(String content, ChatResponse<ResultMessage> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSeen(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnDeliveryMessage(String content, ChatResponse<ResultMessage> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onDeliver(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnError(String content, ErrorOutPut errorOutPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onError(content, errorOutPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetThreadParticipant(String content, ChatResponse<ResultParticipant> outPutParticipant) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetThreadParticipant(content, outPutParticipant);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetThreadParticipant(ChatResponse<ResultParticipant> outPutParticipant) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetThreadParticipant(outPutParticipant);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnClearHistory(String content, ChatResponse<ResultClearHistory> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnClearHistory(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


    public void callOnGetThreadAdmin(String content, ChatResponse<ResultParticipant> chatResponse) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetThreadAdmin(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    private void callHandleCallbackError(@NonNull ChatListener listener, Throwable cause) {
        try {
            listener.handleCallbackError(cause);
        } catch (Throwable t) {
        }
    }

    public void callOnEditedMessage(String content, ChatResponse<ResultNewMessage> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onEditedMessage(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnAddContact(String content, ChatResponse<ResultAddContact> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onContactAdded(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRemoveContact(String content, ChatResponse<ResultRemoveContact> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onRemoveContact(content, response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnMuteThread(String content, ChatResponse<ResultMute> outPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onMuteThread(content, outPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUnmuteThread(String content, ChatResponse<ResultMute> outPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnmuteThread(content, outPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUserInfo(String content, ChatResponse<ResultUserInfo> outPutUserInfo) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUserInfo(content, outPutUserInfo);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnCreateThread(String content, ChatResponse<ResultThread> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {

                listener.onCreateThread(content, response);

                listener.onCreateThread(response);

            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUpdateContact(String content, ChatResponse<ResultUpdateContact> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUpdateContact(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRenameThread(String content, OutPutThread outPutThread) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onRenameThread(content, outPutThread);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnNewMessage(String content, ChatResponse<ResultNewMessage> outPutNewMessage) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onNewMessage(content, outPutNewMessage);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUploadImageFile(String content, ChatResponse<ResultImageFile> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUploadImageFile(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUploadFile(String content, ChatResponse<ResultFile> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUploadFile(content, response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSyncContact(String content, ChatResponse<Contacts> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSyncContact(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadAddParticipant(String content, ChatResponse<ResultAddParticipant> outPutAddParticipant) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadAddParticipant(content, outPutAddParticipant);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadRemoveParticipant(String content, ChatResponse<ResultParticipant> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadRemoveParticipant(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadLeaveParticipant(String content, ChatResponse<ResultLeaveThread> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadLeaveParticipant(content, response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnDeleteMessage(String content, ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onDeleteMessage(content, outPutDeleteMessage);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadInfoUpdated(String content, ChatResponse<ResultThread> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadInfoUpdated(content, response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnContactsLastSeenUpdated(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onContactsLastSeenUpdated(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnChatState(String state) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onChatState(state);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnMapSearch(String content, OutPutMapNeshan outPutMapNeshan) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onMapSearch(content, outPutMapNeshan);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnMapRouting(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onMapRouting(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnBlock(String content, ChatResponse<ResultBlock> outPutBlock) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onBlock(content, outPutBlock);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUnBlock(String content, ChatResponse<ResultBlock> outPutBlock) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnBlock(content, outPutBlock);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetBlockList(String content, ChatResponse<ResultBlockList> outPutBlockList) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetBlockList(content, outPutBlockList);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSearchContact(String content, ChatResponse<ResultContact> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSearchContact(content, response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRemovedFromThread(String content, ChatResponse<ResultThread> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnRemovedFromThread(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUpdateThreadInfo(String threadJson, ChatResponse<ResultThread> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUpdateThreadInfo(threadJson, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnDeliveredMessageList(String content, ChatResponse<ResultParticipant> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnDeliveredMessageList(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSeenMessageList(String content, ChatResponse<ResultParticipant> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnSeenMessageList(content, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnMapReverse(String json, ChatResponse<ResultMapReverse> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnMapReverse(json, chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnStaticMap(ChatResponse<ResultStaticMapImage> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnStaticMap(chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    //todo remove it later
    public void callOnLogEvent(String logEvent) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onLogEvent(logEvent);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnLogEvent(String logName, String json) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onLogEvent(logName, json);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSetRoleToUser(String content, ChatResponse<ResultSetAdmin> outputSetRoleToUser) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnSetRule(outputSetRoleToUser);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnRemoveRoleFromUser(String content, ChatResponse<ResultSetAdmin> outputSetRoleToUser) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onRemoveRoleFromUser(outputSetRoleToUser);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnGetNotSeenDuration(OutPutNotSeenDurations content) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnNotSeenDuration(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }


    public void callOnGetSignalMessage(OutputSignalMessage output) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {

                listener.OnSignalMessageReceive(output);

                listener.onSignalMessageReceived(output);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnGetSignalMessage(ChatResponse<ResultSignalMessage> result) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSignalMessageReceived(result);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnPinThread(ChatResponse<ResultPinThread> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onPinThread(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }


    public void callOnUnPinThread(ChatResponse<ResultPinThread> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnPinThread(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnPinMessage(ChatResponse<ResultPinMessage> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onPinMessage(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnUnPinMessage(ChatResponse<ResultPinMessage> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnPinMessage(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnGetUserRoles(ChatResponse<ResultCurrentUserRoles> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetCurrentUserRoles(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnGetMentionList(ChatResponse<ResultHistory> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetMentionList(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }
    public void callOnGetHashTagList(ChatResponse<ResultHistory> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetHashTagList(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnSignalMessageTimeout(long threadId) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onTypingSignalTimeout(threadId);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnLowFreeSpace(long bytesAvailable) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onLowFreeSpace(bytesAvailable);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnContactsLastSeenUpdated(ChatResponse<ResultNotSeenDuration> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onContactsLastSeenUpdated(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


    public void callOnGetAssistantHistory(ChatResponse<List<AssistantHistoryVo>> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetAssistantHistory(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetMutualGroup(String content, ChatResponse<ResultThreads> thread) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetMutualGroups(content,thread);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnAssistantBlocked(ChatResponse<List<AssistantVo>> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onAssistantBlocked(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnAssistantUnBlocked(ChatResponse<List<AssistantVo>> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onAssistantUnBlocked(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnAssistantBlocks(ChatResponse<List<AssistantVo>> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onAssistantBlocks(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnChatProfileUpdated(ChatResponse<ResultUpdateProfile> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onChatProfileUpdated(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }


    public void callOnRegisterAssistant(ChatResponse<List<AssistantVo>> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onRegisterAssistant(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }


    public void callOnDeActiveAssistant(ChatResponse<List<AssistantVo>> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onDeActiveAssistant(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetAssistants(ChatResponse<List<AssistantVo>> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetAssistants(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUniqueNameIsAvailable(ChatResponse<ResultIsNameAvailable> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUniqueNameIsAvailable(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnJoinPublicThread(ChatResponse<ResultJoinPublicThread> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onJoinPublicThread(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnGetUnreadMessagesCount(ChatResponse<ResultUnreadMessagesCount> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetUnreadMessagesCount(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnBotCreated(ChatResponse<CreateBotResult> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onBotCreated(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnBotCommandsDefined(ChatResponse<DefineBotCommandResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onBotCommandsDefined(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnBotStopped(ChatResponse<StartStopBotResult> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onBotStopped(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnBotStarted(ChatResponse<StartStopBotResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onBotStarted(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }
    public void callOnUserBots(ChatResponse<GetUserBotsResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUserBots(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnContactsSynced(ChatResponse<ContactSyncedResult> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onContactsSynced(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnPingStatusSent(ChatResponse<StatusPingResult> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onPingStatusSent(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnCallRequest(ChatResponse<CallRequestResult> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onReceiveCallRequest(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnCallRequestRejected(ChatResponse<CallRequestResult> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallRequestRejected(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnCallVoiceCallStarted(ChatResponse<CallStartResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onVoiceCallStarted(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }
    public void callOnCallTimeOuted(ChatResponse<CallTimeOutResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnCallTimeOuted(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnVoiceCallEnded(ChatResponse<EndCallResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onVoiceCallEnded(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnGetCallHistory(ChatResponse<GetCallHistoryResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onReceiveCallHistory(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnCallReconnectReceived(ChatResponse<CallReconnectResult> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallReconnect(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnCallConnectReceived(ChatResponse<CallReconnectResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallConnect(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnCallRequestDelivered(ChatResponse<CallDeliverResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallDelivered(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnGroupCallRequest(ChatResponse<CallRequestResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onReceiveGroupCallRequest(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnCallParticipantLeft(ChatResponse<LeaveCallResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallParticipantLeft(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnCallParticipantRemoved(ChatResponse<RemoveFromCallResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallParticipantRemoved(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRemovedFromCall(ChatResponse<RemoveFromCallResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onRemovedFromCall(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnCallParticipantJoined(ChatResponse<JoinCallParticipantResult> response) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallParticipantJoined(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnEndCallRequestFromNotification() {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onEndCallRequestFromNotification();
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnThreadClosed(ChatResponse<CloseThreadResult> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadClosed(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnThreadChangeType(ChatResponse<Thread> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadTypeChanged(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnReceiveActiveCallParticipants(ChatResponse<GetCallParticipantResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onActiveCallParticipantsReceived(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnCallCreated(ChatResponse<CallCreatedResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallCreated(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnAudioCallMuted(ChatResponse<MuteUnMuteCallParticipantResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onAudioCallMuted(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }

    public void callOnMutedByAdmin(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onMutedByAdmin(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnCallParticipantMuted(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallParticipantMuted(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnAudioCallUnMuted(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onAudioCallUnMuted(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnUnMutedByAdmin(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnMutedByAdmin(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnCallParticipantUnMuted(ChatResponse<MuteUnMuteCallParticipantResult> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallParticipantUnMuted(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnCallCanceled(ChatResponse<CallCancelResult> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallParticipantCanceledCall(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnAnotherDeviceAcceptedCall() {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onAnotherDeviceAcceptedCall();
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    //todo
    public void addInnerListener(ChatListener listener) {

    }

    public void callOnNoViewToAddNewPartnerError() {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onNoViewToAddNewParticipant();
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnCallParticipantStoppedVideo(ChatResponse<JoinCallParticipantResult> response) {

        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallParticipantStoppedVideo(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }

    }

    public void callOnCallParticipantStartedVideo(ChatResponse<JoinCallParticipantResult> response) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCallParticipantStartedVideo(response);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }


//    public void callOnGetThreadAdmin(String jsonData, OutPutParticipant output) {
//
//        for (ChatListener listener : getSynchronizedListeners()) {
//            try {
//                listener.onGetThreadAdmin(jsonData,output);
//            } catch (Throwable t) {
//                callHandleCallbackError(listener, t);
//            }
//        }
//
//
//    }
//

}
