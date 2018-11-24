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
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ErrorOutPut;
import com.fanap.podchat.model.OutPutMapNeshan;
import com.fanap.podchat.model.OutPutThread;
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
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.requestobject.RequestThread;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class ChatPresenter extends ChatAdapter implements ChatContract.presenter {

    private Chat chat;
    private ChatContract.view view;
    private Context context;
    private Activity activity;

    public ChatPresenter(Context context, ChatContract.view view, Activity activity) {
        chat = Chat.init(context);
        chat.addListener(this);
//        chat.isCacheables(true);
        chat.isLoggable(true);
        chat.rawLog(true);
        this.activity = activity;
        this.context = context;
        this.view = view;
    }

    @Override
    public void setToke(String token) {
        chat.setToken(token);
    }

    @Override
    public void connect(String serverAddress, String appId, String severName,
                        String token, String ssoHost, String platformHost, String fileServer, String typeCode) {
        chat.connect(serverAddress, appId, severName, token, ssoHost, platformHost, fileServer, typeCode);
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
//        chat.getThreads(count, offset, threadIds, threadName, handler);
    }

    @Override
    public void getThreadObject(RequestThread requestThread) {
        chat.getThreads(requestThread);
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

        chat.deliveredMessageList(12500);
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
    public void sendTextMessage(String textMessage, long threadId, Integer messageType, String metaData, ChatHandler handler) {
        chat.sendTextMessage(textMessage, threadId, messageType, metaData, handler);
    }

    @Override
    public void replyMessage(String messageContent, long threadId, long messageId, ChatHandler handler) {
        chat.replyMessage(messageContent, threadId, messageId, "meta", handler);
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
    public void editMessage(int messageId, String messageContent, String metaData, ChatHandler handler) {
        chat.editMessage(messageId, messageContent, metaData,  handler);
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
        chat.block(contactId,  handler);
    }

    @Override
    public void unBlock(long contactId, ChatHandler handler) {
        chat.unblock(contactId,  null);
    }

    @Override
    public void spam(long threadId) {
        chat.spam(threadId);
    }

    @Override
    public void getBlockList(Long count, Integer offset, ChatHandler handler) {
        chat.getBlockList(count, offset,  handler);
    }

    @Override
    public void sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String metaData, Integer messageType) {
        chat.sendFileMessage(context, activity, description, threadId, fileUri, metaData, messageType);
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
    public void uploadImage(Activity activity, Uri fileUri) {
        chat.uploadImage(activity, fileUri);
    }

    @Override
    public void uploadFile(Activity activity, Uri uri) {
        chat.uploadFile(activity, uri);
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
    public void onDeliver(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onDeliver(content, chatResponse);
        view.onGetDeliverMessage();
    }

    @Override
    public void onGetThread(String content, ChatResponse<ResultThreads> thread) {
        super.onGetThread(content, thread);
        view.onGetThreadList(content, thread);
    }

    @Override
    public void onThreadInfoUpdated(String content) {
    }

    @Override
    public void onGetContacts(String content, ChatResponse<ResultContact> outPutContact) {
        super.onGetContacts(content, outPutContact);
        Logger.json(content);
        Logger.d(outPutContact);
        view.onGetContacts();
    }

    @Override
    public void onSeen(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onSeen(content, chatResponse);
        view.onGetSeenMessage();
    }

    @Override
    public void onUserInfo(String content, ChatResponse<ResultUserInfo> outPutUserInfo) {
        view.onGetUserInfo();
    }

    @Override
    public void onSent(String content, ChatResponse<ResultMessage> chatResponse) {
        super.onSent(content, chatResponse);
        view.onSentMessage();
    }

    @Override
    public void onError(String content, ErrorOutPut outPutError) {
        super.onError(content, outPutError);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateThread(String content, OutPutThread outPutThread) {
        super.onCreateThread(content, outPutThread);
        view.onCreateThread();
    }

    @Override
    public void onGetThreadParticipant(String content, ChatResponse<ResultParticipant> outPutParticipant) {
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
    public void onMuteThread(String content, ChatResponse<ResultMute> outPutMute) {
        super.onMuteThread(content, outPutMute);
        view.onMuteThread();
    }

    @Override
    public void onUnmuteThread(String content, ChatResponse<ResultMute> outPutMute) {
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
    public void onUploadFile(String content, ChatResponse<ResultFile> response) {
        super.onUploadFile(content, response);
        view.onUploadFile();
    }


    @Override
    public void onUploadImageFile(String content, ChatResponse<ResultImageFile> chatResponse) {
        super.onUploadImageFile(content, chatResponse);
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
    public void onThreadRemoveParticipant(String content, ChatResponse<ResultParticipant> chatResponse) {
        super.onThreadRemoveParticipant(content, chatResponse);
        view.onRemoveParticipant();
    }


    @Override
    public void onDeleteMessage(String content, ChatResponse<ResultDeleteMessage> outPutDeleteMessage) {
        super.onDeleteMessage(content, outPutDeleteMessage);
        view.onDeleteMessage();
    }

    @Override
    public void onThreadLeaveParticipant(String content, ChatResponse<ResultLeaveThread> response) {
        super.onThreadLeaveParticipant(content, response);
        view.onLeaveThread();
    }

    @Override
    public void onChatState(String state) {

    }

    @Override
    public void onNewMessage(String content, ChatResponse<ResultNewMessage> chatResponse) {
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
    public void onGetBlockList(String content, ChatResponse<ResultBlockList> outPutBlockList) {
        super.onGetBlockList(content, outPutBlockList);
        view.ongetBlockList();
    }

    @Override
    public void onSearchContact(String content) {
        super.onSearchContact(content);
        view.onSearchContact();
    }

}
