package com.fanap.podchat.chat;

import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.ResultAddContact;
import com.fanap.podchat.model.ResultAddParticipant;
import com.fanap.podchat.model.ResultBlock;
import com.fanap.podchat.model.ResultBlockList;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultFile;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultImageFile;
import com.fanap.podchat.model.ResultLeaveThread;
import com.fanap.podchat.model.ResultMessage;
import com.fanap.podchat.model.ResultMute;
import com.fanap.podchat.model.ResultNewMessage;
import com.fanap.podchat.model.ResultParticipant;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUserInfo;

public interface ChatListener {

    void onError(String content, ErrorOutPut OutPutError);

    void onGetContacts(String content, ChatResponse<ResultContact> outPutContact);

    void onGetHistory(String content, ChatResponse<ResultHistory> history);

    default void onGetThread(String content, ChatResponse<ResultThreads> thread) {

    }

    default void onThreadInfoUpdated(String content) {

    }

    default void onBlock(String content, ChatResponse<ResultBlock> outPutBlock) {

    }

    default void onUnBlock(String content, ChatResponse<ResultBlock> outPutBlock) {

    }

    default void onSeen(String content, ChatResponse<ResultMessage> chatResponse) {

    }

    default void onDeliver(String content, ChatResponse<ResultMessage> chatResponse) {

    }

    default void onSent(String content, ChatResponse<ResultMessage> chatResponse) {

    }

    default void onMuteThread(String content, ChatResponse<ResultMute> outPutMute) {

    }

    default void onUnmuteThread(String content, ChatResponse<ResultMute> outPutUnMute) {

    }

    default void onUserInfo(String content, ChatResponse<ResultUserInfo> outPutUserInfo) {

    }

    default void onCreateThread(String content, OutPutThread outPutThread) {

    }

    default void onGetThreadParticipant(String content, ChatResponse<ResultParticipant> outPutParticipant) {

    }

    default void onEditedMessage(String content) {

    }

    default void onContactAdded(String content, ChatResponse<ResultAddContact> chatResponse) {

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

    default void onNewMessage(String content, ChatResponse<ResultNewMessage> outPutNewMessage) {

    }

    default void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {

    }

    default void onUpdateContact(String content) {

    }

    default void onUploadFile(String content, ChatResponse<ResultFile> chatResponse) {

    }

    default void onUploadImageFile(String content, ChatResponse<ResultImageFile> chatResponse) {

    }

    default void onSyncContact(String content) {

    }

    default void onSearchContact(String content) {

    }

    default void onThreadAddParticipant(String content, ChatResponse<ResultAddParticipant> outPutAddParticipant) {

    }

    default void onThreadRemoveParticipant(String content, ChatResponse<ResultParticipant> chatResponse) {

    }

    default void onThreadLeaveParticipant(String content, ChatResponse<ResultLeaveThread> response) {

    }

    void onLastSeenUpdated(String content);

    void onChatState(String state);

    void onGetBlockList(String content, ChatResponse<ResultBlockList> outPutBlockList);

    void onUpdateThreadInfo(String threadJson, ChatResponse<ResultThread> chatResponse);

    void OnDeliveredMessageList(String content, ChatResponse<ResultParticipant> chatResponse);

    void OnSeenMessageList(String content, ChatResponse<ResultParticipant> chatResponse);
}
