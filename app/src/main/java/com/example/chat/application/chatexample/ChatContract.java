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

import java.util.ArrayList;
import java.util.List;

public interface ChatContract {
    interface view {

        default void onGetUserInfo() { }

        void onGetThreadList();

        void onGetThreadHistory();

        void onGetContacts();

        void onGetThreadParticipant();

        void onSentMessage();

        void onGetDeliverMessage();

        void onGetSeenMessage();

        void onEditMessage();

        void onDeleteMessage();

        void onCreateThread();

        void onMuteThread();

        void onUnMuteThread();

        void onRenameGroupThread();

        void onAddContact();

        void onUpdateContact();

        void onUploadFile();

        void onUploadImageFile();

        void onRemoveContact();

        void onAddParticipant();

        void onRemoveParticipant();

        void onLeaveThread();

        void onBlock();

        void onUnblock();

        void onSearchContact();

        void onSearchHisory();

        void ongetBlockList();

        void onMapSearch();

        void onMapRouting();

        void onError();

        default void onSpam(){};
    }

    interface presenter {

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

        void editMessage(int messageId, String messageContent, ChatHandler handler);

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

        void uploadImageProgress(Context context, Activity activity, Uri fileUri,ProgressHandler.onProgress handler);
    }
}
