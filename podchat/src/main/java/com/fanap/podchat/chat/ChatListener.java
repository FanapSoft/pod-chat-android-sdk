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

public interface ChatListener {


    void onError(String content, ErrorOutPut OutPutError);

    void onGetContacts(String content, OutPutContact outPutContact);

    void onGetHistory(String content, ChatResponse<ResultHistory> history);

    default void onGetThread(String content, ChatResponse<ResultThreads> thread) {

    }

    default void onThreadInfoUpdated(String content) {

    }

    default void onBlock(String content, OutPutBlock outPutBlock) {

    }

    default void onUnBlock(String content, OutPutBlock outPutBlock) {

    }

    default void onSeen(String content, long threadId) {

    }

    default void onDeliver(String content, long threadId) {

    }

    default void onSent(String content) {

    }

    default void onMuteThread(String content, OutPutMute outPutMute) {

    }

    default void onUnmuteThread(String content, OutPutMute outPutUnMute) {

    }

    default void onUserInfo(String content, OutPutUserInfo outPutUserInfo) {

    }

    default void onCreateThread(String content, OutPutThread outPutThread) {

    }

    default void onGetThreadParticipant(String content, OutPutParticipant outPutParticipant) {

    }

    default void onEditedMessage(String content) {

    }

    default void onContactAdded(String content) {

    }

    default void handleCallbackError(Throwable cause) throws Exception {

    }

    default void onRemoveContact(String content) {

    }

    default void onRenameThread(String content, OutPutThread outPutThread) {

    }

    default void onMapSearch(String content, OutPutMapNeshan outPutMapNeshan) {

    }

    default void onMapRouting(String content) {

    }

    default void onNewMessage(String content, OutPutNewMessage outPutNewMessage) {

    }

    default void onDeleteMessage(String content, OutPutDeleteMessage outPutDeleteMessage) {

    }

    default void onUpdateContact(String content) {

    }

    default void onUploadFile(String content) {

    }

    default void onUploadImageFile(String content, FileImageUpload fileImageUpload) {

    }

    default void onSyncContact(String content) {

    }

    default void onSearchContact(String content) {

    }

    default void onThreadAddParticipant(String content, OutPutAddParticipant outPutAddParticipant) {

    }

    default void onThreadRemoveParticipant(String content, OutPutParticipant outPutParticipant) {

    }

    default void onThreadLeaveParticipant(String content, OutPutLeaveThread outPutLeaveThread) {

    }

    void onLastSeenUpdated(String content);

    void onChatState(String state);

    void onGetBlockList(String content, OutPutBlockList outPutBlockList);


    void onUpdateThreadInfo(String threadJson, ChatResponse<ResultThread> chatResponse);

}
