package com.fanap.podchat.chat;

import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.FileImageUpload;
import com.fanap.podchat.model.OutPutAddParticipant;
import com.fanap.podchat.model.OutPutBlock;
import com.fanap.podchat.model.OutPutBlockList;
import com.fanap.podchat.model.OutPutContact;
import com.fanap.podchat.model.OutPutDeleteMessage;
import com.fanap.podchat.model.OutPutLeaveThread;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.OutPutMute;
import com.fanap.podchat.model.OutPutNewMessage;
import com.fanap.podchat.model.OutPutParticipant;
import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.OutPutUserInfo;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultHistory;

import java.util.ArrayList;
import java.util.List;

public class ChatListenerManager {
    private final List<ChatListener> mListeners = new ArrayList<>();
    private boolean mSyncNeeded = true;
    private List<ChatListener> mCopiedListeners;

    public ChatListenerManager() {
    }

    public void addListener(ChatListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            mListeners.add(listener);
            mSyncNeeded = true;
        }
    }

    public void addListeners(List<ChatListener> listeners) {
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

    public void removeListener(ChatListener listener) {
        if (listener == null) {
            return;
        }

        synchronized (mListeners) {
            if (mListeners.remove(listener)) {
                mSyncNeeded = true;
            }
        }
    }

    public void removeListeners(List<ChatListener> listeners) {
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

    public void callOnGetThreadHistory(String content, ChatResponse<ResultHistory> history) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetHistory(content, history);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetContacts(String content, OutPutContact outPutContact) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {

                listener.onGetContacts(content, outPutContact);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSentMessage(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSent(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSeenMessage(String content, long threadId) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSeen(content,threadId);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnDeliveryMessage(String content, long threadId) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onDeliver(content,threadId);
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

    public void callOnGetThreadParticipant(String content, OutPutParticipant outPutParticipant) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetThreadParticipant(content, outPutParticipant);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    private void callHandleCallbackError(ChatListener listener, Throwable cause) {
        try {
            listener.handleCallbackError(cause);
        } catch (Throwable t) {
        }
    }

    public void callOnEditedMessage(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onEditedMessage(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnAddContact(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onContactAdded(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRemoveContact(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onRemoveContact(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnMuteThread(String content, OutPutMute outPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onMuteThread(content, outPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUnmuteThread(String content, OutPutMute outPut) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnmuteThread(content, outPut);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUserInfo(String content, OutPutUserInfo outPutUserInfo) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUserInfo(content, outPutUserInfo);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnCreateThread(String content, OutPutThread outPutThread) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onCreateThread(content, outPutThread);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUpdateContact(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUpdateContact(content);
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

    public void callOnNewMessage(String content, OutPutNewMessage outPutNewMessage) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onNewMessage(content, outPutNewMessage);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUploadImageFile(String content, FileImageUpload fileImageUpload ) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUploadImageFile(content,fileImageUpload);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUploadFile(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUploadFile(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSyncContact(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSyncContact(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadAddParticipant(String content, OutPutAddParticipant outPutAddParticipant) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadAddParticipant(content, outPutAddParticipant);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadRemoveParticipant(String content, OutPutParticipant outPutParticipant) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadRemoveParticipant(content, outPutParticipant);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadLeaveParticipant(String content, OutPutLeaveThread outPutLeaveThread) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadLeaveParticipant(content, outPutLeaveThread);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnDeleteMessage(String content, OutPutDeleteMessage outPutDeleteMessage) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onDeleteMessage(content, outPutDeleteMessage);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnThreadInfoUpdated(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onThreadInfoUpdated(content);
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

    public void callOnBlock(String content, OutPutBlock outPutBlock) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onBlock(content, outPutBlock);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUnBlock(String content, OutPutBlock outPutBlock) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUnBlock(content, outPutBlock);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnGetBlockList(String content, OutPutBlockList outPutBlockList) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onGetBlockList(content, outPutBlockList);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnSearchContact(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSearchContact(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnRemovedFromThread(String content) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onSearchContact(content);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }

    public void callOnUpdateThreadInfo(String threadJson, ChatResponse<ResultThread> chatResponse) {
        for (ChatListener listener : getSynchronizedListeners()) {
            try {
                listener.onUpdateThreadInfo(threadJson,chatResponse);
            } catch (Throwable t) {
                callHandleCallbackError(listener, t);
            }
        }
    }
}
