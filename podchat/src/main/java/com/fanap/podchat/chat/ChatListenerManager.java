package com.fanap.podchat.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.Contacts;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.OutPutNotSeenDurations;
import com.fanap.podchat.model.OutPutParticipant;
import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.OutputSetRoleToUser;
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
import com.fanap.podchat.model.ResultParticipant;
import com.fanap.podchat.model.ResultRemoveContact;
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
            mSyncNeeded = true;
        }
    }

    private List<ChatListener> getSynchronizedListeners() {
        synchronized (mListeners) {
            if (!mSyncNeeded) {
                return mCopiedListeners;
            }

            // Copy mListeners to copiedListeners.
            List<ChatListener> copiedListeners = new ArrayList<>(mListeners.size());

            for (ChatListener listener : mListeners) {
                copiedListeners.add(listener);
            }

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

    public void callOnGetThreadParticipant(OutPutParticipant outPutParticipant) {
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


    public void callOnGetThreadAdmin(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnGetThreadAdmin(content);
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

    public void callOnLastSeenUpdated(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onLastSeenUpdated(content);
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

    public void callOnLogEvent(String logEvent) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnLogEvent(logEvent);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSetRoleToUser(String content, OutputSetRoleToUser outputSetRoleToUser) {


        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.OnSetRule(outputSetRoleToUser);
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
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }


    }
}
