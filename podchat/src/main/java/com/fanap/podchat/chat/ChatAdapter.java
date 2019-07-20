package com.fanap.podchat.chat;

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

public class ChatAdapter implements ChatListener {



    @Override
    public void onDeliver(String content, ChatResponse<ResultMessage> chatResponse) {

    }

    @Override
    public void onError(String content, ErrorOutPut error) {
    }

    @Override
    public void onGetContacts(String content,  ChatResponse<ResultContact> outPutContact) {

    }

    @Override
    public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

    }

    @Override
    public void onGetThread(String content, ChatResponse<ResultThreads> thread) {

    }

    @Override
    public void onThreadInfoUpdated(String content, ChatResponse<ResultThread> response) {

    }

    @Override
    public void onBlock(String content, ChatResponse<ResultBlock> outPutBlock) {

    }

    @Override
    public void onUnBlock(String content, ChatResponse<ResultBlock> outPutBlock) {

    }

    @Override
    public void onSeen(String content, ChatResponse<ResultMessage> chatResponse) {

    }

    @Override
    public void onMuteThread(String content, ChatResponse<ResultMute> outPutMute) {

    }

    @Override
    public void onUnmuteThread(String content, ChatResponse<ResultMute> outPutUnMute) {

    }

    @Override
    public void onUserInfo(String content, ChatResponse<ResultUserInfo> outPutUserInfo) {

    }

    @Override
    public void onSent(String content, ChatResponse<ResultMessage> chatResponse) {

    }

    @Override
    public void onCreateThread(String content, ChatResponse<ResultThread> outPutThread) {

    }

    @Override
    public void onGetThreadParticipant(String content, ChatResponse<ResultParticipant> outPutParticipant) {

    }

    @Override
    public void onEditedMessage(String content, ChatResponse<ResultNewMessage> chatResponse) {

    }

    @Override
    public void onUploadImageFile(String content, ChatResponse<ResultImageFile> chatResponse) {

    }

    @Override
    public void onContactAdded(String content,ChatResponse<ResultAddContact> chatResponse) {

    }

    @Override
    public void handleCallbackError(Throwable cause) throws Exception {

    }

    @Override
    public void onRemoveContact(String content, ChatResponse<ResultRemoveContact> response) {

    }

    @Override
    public void onRenameThread(String content, OutPutThread outPutThread) {

    }

    @Override
    public void onMapSearch(String content, OutPutMapNeshan outPutMapNeshan) {

    }

    @Override
    public void onMapRouting(String content) {

    }

    @Override
    public void OnMapReverse(String json, ChatResponse<ResultMapReverse> chatResponse) {

    }

    @Override
    public void OnStaticMap(ChatResponse<ResultStaticMapImage> chatResponse) {

    }

    @Override
    public void OnGetThreadAdmin(String content) {

    }

    @Override
    public void onNewMessage(String content, ChatResponse<ResultNewMessage> outPutNewMessage) {

    }

    @Override
    public void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {

    }

    @Override
    public void onUpdateContact(String content, ChatResponse<ResultUpdateContact> chatResponse) {

    }


    @Override
    public void onUploadFile(String content, ChatResponse<ResultFile> chatResponse) {

    }

    @Override
    public void onSyncContact(String content, ChatResponse<Contacts> chatResponse) {

    }

    @Override
    public void onThreadAddParticipant(String content, ChatResponse<ResultAddParticipant> outPutAddParticipant) {

    }

    @Override
    public void onThreadRemoveParticipant(String content, ChatResponse<ResultParticipant> chatResponse) {

    }

    @Override
    public void onThreadLeaveParticipant(String content, ChatResponse<ResultLeaveThread> response) {

    }

    @Override
    public void onLastSeenUpdated(String content) {

    }

    @Override
    public void onChatState(String state) {

    }

    @Override
    public void onGetBlockList(String content, ChatResponse<ResultBlockList> outPutBlockList) {

    }

    @Override
    public void onUpdateThreadInfo(String threadJson, ChatResponse<ResultThread> chatResponse) {

    }

    @Override
    public void OnDeliveredMessageList(String content, ChatResponse<ResultParticipant> chatResponse) {

    }

    @Override
    public void OnSeenMessageList(String content,ChatResponse<ResultParticipant> chatResponse) {

    }

    @Override
    public void onSearchContact(String content, ChatResponse<ResultContact> chatResponse) {

    }

    @Override
    public void OnNotSeenDuration(OutPutNotSeenDurations resultNotSeen) {

    }

    @Override
    public void OnClearHistory(String content, ChatResponse<ResultClearHistory> chatResponse) {

    }

    @Override
    public void OnSignalMessageReceive(OutputSignalMessage output) {

    }

    @Override
    public void OnSetRule(OutputSetRoleToUser outputSetRoleToUser) {

    }

    @Override
    public void onGetThreadParticipant( OutPutParticipant outPutParticipant) {

    }
}
