package com.example.chat.application.chatexample;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.net.Uri;

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.SearchContact;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultThreads;

import java.util.ArrayList;
import java.util.List;

public interface ChatContract {
    interface view {

        default void onGetUserInfo() {
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

        default void ongetBlockList() {
        }

        default void onMapSearch() {
        }

        default void onMapRouting() {
        }

        default void onError() {
        }

        default void onSpam() {
        }
    }

    interface presenter {

        void setToke(String token);

        void connect(String serverAddress, String appId, String severName, String token, String ssoHost
                , String platformHost, String fileServer);

        void mapSearch(String searchTerm, Double latitude, Double longitude);

        void mapRouting(String origin, String destination);

        void getThread(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName, ChatHandler handler);

        void getUserInfo(ChatHandler handler);

        void getHistory(History history, long threadId, ChatHandler handler);

        void searchHistory(NosqlListMessageCriteriaVO messageCriteriaVO, ChatHandler handler);

        void getContact(Integer count, Long offset, ChatHandler handler);

        void createThread(int threadType, Invitee[] invitee, String threadTitle, ChatHandler handler);

        void sendTextMessage(String textMessage, long threadId, String metaData, ChatHandler handler);

        void replyMessage(String messageContent, long threadId, long messageId, ChatHandler handler);

        LiveData<String> getLiveState();

        void muteThread(int threadId, ChatHandler handler);

        void renameThread(long threadId, String title, ChatHandler handler);

        void unMuteThread(int threadId, ChatHandler handler);

        void editMessage(int messageId, String messageContent, String metaData, ChatHandler handler);

        void getThreadParticipant(int count, Long offset, long threadId, ChatHandler handler);

        void addContact(String firstName, String lastName, String cellphoneNumber, String email);

        void removeContact(long id);

        void searchContact(SearchContact searchContact);

        void block(Long contactId, ChatHandler handler);

        void unBlock(long contactId, ChatHandler handler);

        void spam(long threadId);

        void getBlockList(Long count, Integer offset, ChatHandler handler);

        void sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String metaData);

        void syncContact(Activity activity);

        void forwardMessage(long threadId, ArrayList<Long> messageIds);

        void updateContact(int id, String firstName, String lastName, String cellphoneNumber, String email);

        void uploadImage(Context context, Activity activity, Uri fileUri);

        void uploadFile(Context context, Activity activity, String fileUri, Uri uri);

        void seenMessage(int messageId, long ownerId, ChatHandler handler);

        void logOut();

        void removeParticipants(long threadId, List<Long> participantIds, ChatHandler handler);

        void addParticipants(long threadId, List<Long> contactIds, ChatHandler handler);

        void leaveThread(long threadId, ChatHandler handler);

        void updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO, ChatHandler handler);

        void deleteMessage(long messageId, Boolean deleteForAll, ChatHandler handler);

        void uploadImageProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgress handler);
    }
}
