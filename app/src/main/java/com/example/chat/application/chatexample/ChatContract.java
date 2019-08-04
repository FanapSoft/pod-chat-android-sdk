package com.example.chat.application.chatexample;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.requestobject.RequestAddAdmin;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestClearHistory;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.requestobject.RequestDeleteMessage;
import com.fanap.podchat.requestobject.RequestDeliveredMessageList;
import com.fanap.podchat.requestobject.RequestFileMessage;
import com.fanap.podchat.requestobject.RequestForwardMessage;
import com.fanap.podchat.requestobject.RequestGetAdmin;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestLocationMessage;
import com.fanap.podchat.requestobject.RequestMapReverse;
import com.fanap.podchat.requestobject.RequestMapStaticImage;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestRemoveParticipants;
import com.fanap.podchat.requestobject.RequestReplyFileMessage;
import com.fanap.podchat.requestobject.RequestReplyMessage;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
import com.fanap.podchat.requestobject.RequestSpam;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.requestobject.RequestThreadInfo;
import com.fanap.podchat.requestobject.RequestUnBlock;
import com.fanap.podchat.requestobject.RequestUpdateContact;
import com.fanap.podchat.requestobject.RetryUpload;

import java.util.ArrayList;
import java.util.List;

public interface ChatContract {

    interface view {

//        default void onRecivedNotification(Notification notification) {

//        }

        default void onGetUserInfo() {
        }

        default void onLogEvent(String log) {
        }

        default void onGetThreadList(String content, ChatResponse<ResultThreads> thread) {
        }

        default void onGetThreadHistory() {
        }

        default void onGetContacts() {
        }

        default void onGetThreadParticipant() {
        }

        default void onSentMessage() {
        }

        default void onGetDeliverMessage() {
        }

        default void onGetSeenMessage() {
        }

        default void onEditMessage() {
        }

        default void onDeleteMessage() {
        }

        default void onCreateThread() {
        }

        default void onMuteThread() {
        }

        default void onUnMuteThread() {
        }

        default void onRenameGroupThread() {
        }

        default void onAddContact() {
        }

        default void onUpdateContact() {
        }

        default void onUploadFile() {
        }

        default void onUploadImageFile() {
        }

        default void onRemoveContact() {
        }

        default void onAddParticipant() {
        }

        default void onRemoveParticipant() {
        }

        default void onLeaveThread() {
        }

        default void onBlock() {
        }

        default void onUnblock() {
        }

        default void onSearchContact() {
        }

        default void onSearchHisory() {
        }

        default void onState(String state) {
        }

        default void ongetBlockList() {
        }

        default void onMapSearch() {
        }

        default void onMapStaticImage(ChatResponse<ResultStaticMapImage> chatResponse) {
        }

        default void onMapRouting() {
        }

        default void onMapReverse() {
        }

        default void onError() {
        }

        default void onSpam() {
        }

        default void onGetThreadAdmin() {
        }
    }

    interface presenter {

        void sendLocationMessage(RequestLocationMessage request);

        void sendLocationMessage(RequestLocationMessage requestLocationMessage, ProgressHandler.sendFileMessage sendFileMessage);


        void isDatabaseOpen();

        void retryUpload(RetryUpload retry, ProgressHandler.sendFileMessage handler);

        void resendMessage(String uniqueId);

        void cancelMessage(String uniqueId);

        void retryUpload(String uniqueId);

        void cancelUpload(String uniqueId);

        void seenMessageList(RequestSeenMessageList requestParam);

        void deliveredMessageList(RequestDeliveredMessageList requestParams);

        void createThreadWithMessage(RequestCreateThread threadRequest);

        String createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
                , String metadata);


        void getThreads(RequestThread requestThread, ChatHandler handler);

        void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName,

                        long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, ChatHandler handler);

        void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName, ChatHandler handler);

        void setToke(String token);

        void connect(String serverAddress, String appId, String severName, String token, String ssoHost
                , String platformHost, String fileServer, String typeCode);

        void connect(RequestConnect requestConnect);

        void mapSearch(String searchTerm, Double latitude, Double longitude);

        void mapRouting(String origin, String destination);

        void mapStaticImage(RequestMapStaticImage request);

        void mapReverse(RequestMapReverse request);

        void getUserInfo(ChatHandler handler);

        void getHistory(History history, long threadId, ChatHandler handler);

        void getHistory(RequestGetHistory request, ChatHandler handler);

        void searchHistory(NosqlListMessageCriteriaVO messageCriteriaVO, ChatHandler handler);

        void getContact(Integer count, Long offset, ChatHandler handler);

        void createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
                , String metaData, ChatHandler handler);

        void sendTextMessage(String textMessage, long threadId, Integer messageType, String metaData, ChatHandler handler);

        void sendTextMessage(RequestMessage requestMessage, ChatHandler handler);

        void replyMessage(String messageContent, long threadId, long messageId, Integer messageType, ChatHandler handler);

        void replyFileMessage(RequestReplyFileMessage request, ProgressHandler.sendFileMessage handler);

        void replyMessage(RequestReplyMessage request, ChatHandler handler);

        void muteThread(int threadId, ChatHandler handler);

        void renameThread(long threadId, String title, ChatHandler handler);

        void unMuteThread(int threadId, ChatHandler handler);

        void editMessage(int messageId, String messageContent, String metaData, ChatHandler handler);

        void getThreadParticipant(int count, Long offset, long threadId, ChatHandler handler);

        void addContact(String firstName, String lastName, String cellphoneNumber, String email);

        void removeContact(long id);

        void searchContact(SearchContact searchContact);

        void block(Long contactId, Long userId, Long threadId, ChatHandler handler);

        void unBlock(Long blockId, Long userId, Long threadId, Long contactId, ChatHandler handler);

        void unBlock(RequestUnBlock request, ChatHandler handler);

        void spam(long threadId);

        String spam(RequestSpam requestSpam);

        void getBlockList(Long count, Long offset, ChatHandler handler);

        String sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String metaData, Integer messageType, ProgressHandler.sendFileMessage handler);

        void sendFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler);

        void syncContact(Activity activity);

        void forwardMessage(long threadId, ArrayList<Long> messageIds);

        void forwardMessage(RequestForwardMessage request);

        void updateContact(int id, String firstName, String lastName, String cellphoneNumber, String email);

        void updateContact(RequestUpdateContact updateContact);

        void uploadImage(Activity activity, Uri fileUri);

        void uploadFile(Activity activity, Uri uri);

        void seenMessage(int messageId, long ownerId, ChatHandler handler);

        void logOut();

        void removeParticipants(long threadId, List<Long> participantIds, ChatHandler handler);

        void removeParticipants(RequestRemoveParticipants requestRemoveParticipants, ChatHandler handler);

        void addParticipants(long threadId, List<Long> contactIds, ChatHandler handler);

        void addParticipants(RequestAddParticipants requestAddParticipants, ChatHandler handler);

        void leaveThread(long threadId, ChatHandler handler);

        void updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO, ChatHandler handler);

        void updateThreadInfo(RequestThreadInfo request, ChatHandler handler);

        void deleteMessage(ArrayList<Long> messageIds,long threadId, Boolean deleteForAll, ChatHandler handler);

        void deleteMessage(RequestDeleteMessage deleteMessage, ChatHandler handler);

        void uploadImageProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgress handler);

        void uploadFileProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgressFile handler);

        void setAdmin(RequestAddAdmin requestAddAdmin);

        void removeAdminRules(RequestAddAdmin requestAddAdmin);

        void clearHistory(RequestClearHistory requestClearHistory);

        void getAdminList(RequestGetAdmin requestGetAdmin);

//        String startSignalMessage(RequestSignalMsg requestSignalMsg);

//        void stopSignalMessage(String uniqueId);

        void getNotSeenDuration(ArrayList<Integer> userIds);

        String startTyping(long threadId);

        boolean stopTyping(String signalUniqueId);

        void stopAllSignalMessages();
    }
}
