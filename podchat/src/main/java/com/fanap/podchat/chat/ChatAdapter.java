package com.fanap.podchat.chat;

import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.FileImageUpload;
import com.fanap.podchat.model.MessageVO;
import com.fanap.podchat.model.OutPutBlockList;
import com.fanap.podchat.model.OutPutLeaveThread;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.OutPutMute;
import com.fanap.podchat.model.OutPutParticipant;
import com.fanap.podchat.model.OutPutThread;
import com.fanap.podchat.model.OutPutUserInfo;
import com.fanap.podchat.model.ResultAddParticipant;
import com.fanap.podchat.model.ResultBlock;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultMessage;
import com.fanap.podchat.model.ResultThread;
import com.fanap.podchat.model.ResultThreads;

public class ChatAdapter implements ChatListener {
    @Override
    public void onDeliver(String content, ChatResponse<ResultMessage> chatResponse) {

    }

    @Override
    public void onError(String content, ErrorOutPut errorOutPut) {
    }

    @Override
    public void onGetContacts(String contentm,  ChatResponse<ResultContact> outPutContact) {

    }

    @Override
    public void onGetHistory(String content, ChatResponse<ResultHistory> history) {

    }

    @Override
    public void onGetThread(String content, ChatResponse<ResultThreads> thread) {

    }

    @Override
    public void onThreadInfoUpdated(String content) {

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
    public void onMuteThread(String content, OutPutMute outPutMute) {

    }

    @Override
    public void onUnmuteThread(String content, OutPutMute outPutUnMute) {

    }

    @Override
    public void onUserInfo(String content, OutPutUserInfo outPutUserInfo) {

    }

    @Override
    public void onSent(String content, ChatResponse<MessageVO> chatResponse) {

    }

    @Override
    public void onCreateThread(String content, OutPutThread outPutThread) {

    }

    @Override
    public void onGetThreadParticipant(String content, OutPutParticipant outPutParticipant) {

    }

    @Override
    public void onEditedMessage(String content) {

    }

    @Override
    public void onContactAdded(String content) {

    }

    @Override
    public void handleCallbackError(Throwable cause) throws Exception {

    }

    @Override
    public void onRemoveContact(String content) {

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
    public void onNewMessage(String content, ChatResponse<ResultMessage> outPutNewMessage) {

    }

    @Override
    public void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {

    }

    @Override
    public void onUpdateContact(String content) {

    }

    @Override
    public void onUploadFile(String content) {

    }

    @Override
    public void onUploadImageFile(String content, FileImageUpload fileImageUpload) {

    }

    @Override
    public void onSyncContact(String content) {

    }

    @Override
    public void onThreadAddParticipant(String content, ChatResponse<ResultAddParticipant> outPutAddParticipant) {

    }

    @Override
    public void onThreadRemoveParticipant(String content, OutPutParticipant outPutParticipant) {

    }

    @Override
    public void onThreadLeaveParticipant(String content, OutPutLeaveThread outPutLeaveThread) {

    }

    @Override
    public void onLastSeenUpdated(String content) {

    }

    @Override
    public void onChatState(String state) {

    }

    @Override
    public void onGetBlockList(String content, OutPutBlockList outPutBlockList) {

    }

    @Override
    public void onUpdateThreadInfo(String threadJson, ChatResponse<ResultThread> chatResponse) {

    }

    @Override
    public void onSearchContact(String content) {

    }
}
