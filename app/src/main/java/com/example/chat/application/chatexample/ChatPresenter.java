package com.example.chat.application.chatexample;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatAdapter;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.FileImageUpload;
import com.fanap.podchat.model.MessageVO;
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
import com.fanap.podchat.model.ResultAddParticipant;
import com.fanap.podchat.model.ResultBlock;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultMessage;
import com.fanap.podchat.model.ResultThreads;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class ChatPresenter extends ChatAdapter implements ChatContract.presenter {

    private Chat chat;
    private ChatContract.view view;
    private Context context;

    public ChatPresenter(Context context, ChatContract.view view) {
        chat = Chat.init(context);
        chat.addListener(this);
        chat.isCacheables(false);
        chat.isLoggable(true);

        this.context = context;
        this.view = view;
    }

    @Override
    public void connect(String serverAddress, String appId, String severName,
                        String token, String ssoHost, String platformHost, String fileServer) {
        chat.connect(serverAddress, appId, severName, token, ssoHost, platformHost, fileServer);
    }

    @Override
    public void mapSearch(String searchTerm, Double latitude, Double longitude) {
        chat.mapSearch(searchTerm, latitude, longitude);
    }

    @Override
    public void mapRouting(String origin, String originLng) {
        chat.mapRouting(origin, originLng);
    }

    @Override
    public void getThread(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName, ChatHandler handler) {
        chat.getThreads(count, offset, threadIds, threadName, handler);
    }

    @Override
    public void getUserInfo(ChatHandler handler) {
        chat.getUserInfo(handler);
    }

    @Override
    public void getHistory(History history, long threadId, ChatHandler handler) {
        chat.getHistory(history, threadId, handler);
    }

    @Override
    public void searchHistory(NosqlListMessageCriteriaVO builderListMessage, ChatHandler handler) {
        chat.searchHistory(builderListMessage, handler);
    }

    @Override
    public void getContact(Integer count, Long offset, ChatHandler handler) {
        chat.getContacts(count, offset, handler);
    }

    @Override
    public void createThread(int threadType, Invitee[] invitee, String threadTitle, ChatHandler handler) {
        chat.createThread(threadType, invitee, threadTitle, handler);
    }

    @Override
    public void sendTextMessage(String textMessage, long threadId, String metaData, ChatHandler handler) {
        chat.sendTextMessage(textMessage, threadId, metaData, handler);
    }

    @Override
    public void replyMessage(String messageContent, long threadId, long messageId, ChatHandler handler) {
        chat.replyMessage(messageContent, threadId, messageId, handler);
    }

    @Override
    public LiveData<String> getLiveState() {
        return chat.getState();
    }

    @Override
    public void muteThread(int threadId, ChatHandler handler) {
        chat.muteThread(threadId, handler);
    }

    @Override
    public void renameThread(long threadId, String title, ChatHandler handler) {
        chat.renameThread(threadId, title, handler);
    }

    @Override
    public void unMuteThread(int threadId, ChatHandler handler) {
        chat.unMuteThread(threadId, handler);
    }

    @Override
    public void editMessage(int messageId, String messageContent, ChatHandler handler) {
        chat.editMessage(messageId, messageContent, handler);
    }

    @Override
    public void getThreadParticipant(int count, Long offset, long threadId, ChatHandler handler) {
        chat.getThreadParticipants(count, offset, threadId, handler);
    }

    @Override
    public void addContact(String firstName, String lastName, String cellphoneNumber, String email) {
        chat.addContact(firstName, lastName, cellphoneNumber, email);
    }

    @Override
    public void removeContact(long id) {
        chat.removeContact(id);
    }

    @Override
    public void searchContact(SearchContact searchContact) {
        chat.searchContact(searchContact);
    }

    @Override
    public void block(Long contactId, ChatHandler handler) {
        chat.block(contactId, handler);
    }

    @Override
    public void unBlock(long contactId, ChatHandler handler) {
        chat.unblock(contactId, handler);
    }

    @Override
    public void spam(long threadId) {
        chat.spam(threadId);
    }

    @Override
    public void getBlockList(Long count, Integer offset, ChatHandler handler) {
        chat.getBlockList(count, offset, handler);
    }

    @Override
    public void sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String metaData) {
        chat.sendFileMessage(context, activity, description, threadId, fileUri, metaData);
    }

    @Override
    public void syncContact(Activity activity) {
        chat.syncContact(context, activity);
    }

    @Override
    public void forwardMessage(long threadId, ArrayList<Long> messageIds) {
        chat.forwardMessage(threadId, messageIds);
    }

    @Override
    public void updateContact(int id, String firstName, String lastName, String cellphoneNumber, String email) {
        chat.updateContact(id, firstName, lastName, cellphoneNumber, email);
    }

    @Override
    public void uploadImage(Context context, Activity activity, Uri fileUri) {
        chat.uploadImage(context, activity, fileUri);
    }

    @Override
    public void uploadFile(Context context, Activity activity, String fileUri, Uri uri) {
        chat.uploadFile(context, activity, fileUri, uri);
    }

    @Override
    public void seenMessage(int messageId, long ownerId, ChatHandler handler) {
        chat.seenMessage(messageId, ownerId, handler);
    }

    @Override
    public void logOut() {
        chat.logOutSocket();
    }

    @Override
    public void removeParticipants(long threadId, List<Long> contactIds, ChatHandler handler) {
        chat.removeParticipants(threadId, contactIds, handler);
    }

    @Override
    public void addParticipants(long threadId, List<Long> contactIds, ChatHandler handler) {
        chat.addParticipants(threadId, contactIds, handler);
    }

    @Override
    public void leaveThread(long threadId, ChatHandler handler) {
        chat.leaveThread(threadId, handler);
    }

    @Override
    public void updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO, ChatHandler handler) {
        chat.updateThreadInfo(threadId, threadInfoVO, handler);
    }

    @Override
    public void deleteMessage(long messageId, Boolean deleteForAll, ChatHandler handler) {
        chat.deleteMessage(messageId, deleteForAll, handler);
    }

    @Override
    public void uploadImageProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgress handler) {
        chat.uploadImageProgress(context, activity, fileUri, handler);
    }


    //View
    @Override
    public void onDeliver(String content,ChatResponse<ResultMessage> chatResponse) {
        super.onDeliver(content, chatResponse);
        view.onGetDeliverMessage();
    }

    @Override
    public void onGetThread(String content, ChatResponse<ResultThreads> thread) {
        super.onGetThread(content, thread);
        view.onGetThreadList();
    }

    @Override
    public void onThreadInfoUpdated(String content) {
    }

    @Override
    public void onGetContacts(String content,  ChatResponse<ResultContact> outPutContact) {
        super.onGetContacts(content, outPutContact);
        Logger.json(content);
        view.onGetContacts();
    }

    @Override
    public void onSeen(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onSeen(content, chatResponse);
        view.onGetSeenMessage();
    }

    @Override
    public void onUserInfo(String content, OutPutUserInfo outPutUserInfo) {
        view.onGetUserInfo();
    }

    @Override
    public void onSent(String content,ChatResponse<MessageVO> chatResponse) {
        super.onSent(content,chatResponse);
        view.onSentMessage();
    }

    @Override
    public void onError(String content, ErrorOutPut outPutError) {
        super.onError(content, outPutError);
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateThread(String content, OutPutThread outPutThread) {
        super.onCreateThread(content, outPutThread);
        view.onCreateThread();
    }

    @Override
    public void onGetThreadParticipant(String content, OutPutParticipant outPutParticipant) {
        super.onGetThreadParticipant(content, outPutParticipant);
        view.onGetThreadParticipant();
    }

    @Override
    public void onEditedMessage(String content) {
        super.onEditedMessage(content);
        view.onEditMessage();
    }

    @Override
    public void onGetHistory(String content, ChatResponse<ResultHistory> history) {
        super.onGetHistory(content, history);
        view.onGetThreadHistory();
    }

    @Override
    public void onMuteThread(String content, OutPutMute outPutMute) {
        super.onMuteThread(content, outPutMute);
        view.onMuteThread();
    }

    @Override
    public void onUnmuteThread(String content, OutPutMute outPutMute) {
        super.onUnmuteThread(content, outPutMute);
        view.onUnMuteThread();
    }

    @Override
    public void onRenameThread(String content, OutPutThread outPutThread) {
        super.onRenameThread(content, outPutThread);
        view.onRenameGroupThread();
    }


    @Override
    public void onContactAdded(String content) {
        super.onContactAdded(content);
        view.onAddContact();
    }

    @Override
    public void onUpdateContact(String content) {
        super.onUpdateContact(content);
        view.onUpdateContact();
    }

    @Override
    public void onUploadFile(String content) {
        super.onUploadFile(content);
        view.onUploadFile();
    }

    @Override
    public void onUploadImageFile(String content, FileImageUpload fileImageUpload) {
        super.onUploadImageFile(content, fileImageUpload);
        view.onUploadImageFile();
    }

    @Override
    public void onRemoveContact(String content) {
        super.onRemoveContact(content);
        view.onRemoveContact();
    }

    @Override
    public void onThreadAddParticipant(String content, ChatResponse<ResultAddParticipant> outPutAddParticipant) {
        super.onThreadAddParticipant(content, outPutAddParticipant);
        view.onAddParticipant();
    }

    @Override
    public void onThreadRemoveParticipant(String content, OutPutParticipant outPutParticipant) {
        super.onThreadRemoveParticipant(content, outPutParticipant);
        view.onRemoveParticipant();
    }

    @Override
    public void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {
        super.onDeleteMessage(content, outPutDeleteMessage);
        view.onDeleteMessage();
    }

    @Override
    public void onThreadLeaveParticipant(String content, OutPutLeaveThread outPutLeaveThread) {
        super.onThreadLeaveParticipant(content, outPutLeaveThread);
        view.onLeaveThread();
    }

    @Override
    public void onChatState(String state) {

    }

    @Override
    public void onNewMessage(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onNewMessage(content, chatResponse);
//        outPutNewMessage = JsonUtil.fromJSON(content, OutPutNewMessage.class);
//        MessageVO messageVO = outPutNewMessage.getResult();
//        Participant participant = messageVO.getParticipant();

//        long id = messageVO.getId();
//        chat.seenMessage(id, participant.getId(), new ChatHandler() {
//            @Override
//            public void onSeen(String uniqueId) {
//                super.onSeen(uniqueId);
//            }
//        });
    }

    @Override
    public void onBlock(String content, ChatResponse<ResultBlock> outPutBlock) {
        super.onBlock(content, outPutBlock);
        view.onBlock();
    }

    @Override
    public void onUnBlock(String content, ChatResponse<ResultBlock> outPutBlock) {
        super.onUnBlock(content, outPutBlock);
        view.onUnblock();
    }

    @Override
    public void onMapSearch(String content, OutPutMapNeshan outPutMapNeshan) {
        super.onMapSearch(content, outPutMapNeshan);
        view.onMapSearch();
    }

    @Override
    public void onMapRouting(String content) {
        view.onMapRouting();
    }

    @Override
    public void onGetBlockList(String content, OutPutBlockList outPutBlockList) {
        super.onGetBlockList(content, outPutBlockList);
        view.ongetBlockList();
    }

    @Override
    public void onSearchContact(String content) {
        super.onSearchContact(content);
        view.onSearchContact();
    }

}
