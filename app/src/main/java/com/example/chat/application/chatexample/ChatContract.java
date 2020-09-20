package com.example.chat.application.chatexample;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.bot.request_model.CreateBotRequest;
import com.fanap.podchat.chat.bot.request_model.DefineBotCommandRequest;
import com.fanap.podchat.chat.bot.request_model.StartAndStopBotRequest;
import com.fanap.podchat.chat.bot.result_model.CreateBotResult;
import com.fanap.podchat.chat.bot.result_model.DefineBotCommandResult;
import com.fanap.podchat.chat.mention.model.GetMentionedRequest;
import com.fanap.podchat.chat.messge.ResultUnreadMessagesCount;
import com.fanap.podchat.chat.thread.public_thread.IsPublicThreadNameAvailableRequest;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.chat.thread.public_thread.JoinPublicThreadRequest;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.user.profile.UpdateProfileRequest;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.SearchContactRequest;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.requestobject.AddContactRequest;
import com.fanap.podchat.requestobject.GetBlockedListRequest;
import com.fanap.podchat.requestobject.CreateThreadWithFileRequest;
import com.fanap.podchat.requestobject.GetContactRequest;
import com.fanap.podchat.requestobject.GetFileRequest;
import com.fanap.podchat.requestobject.GetImageRequest;
import com.fanap.podchat.requestobject.RequestGetPodSpaceFile;
import com.fanap.podchat.requestobject.RequestGetPodSpaceImage;
import com.fanap.podchat.requestobject.GetCurrentUserRolesRequest;
import com.fanap.podchat.chat.pin.pin_message.model.PinUnpinMessageRequest;
import com.fanap.podchat.requestobject.SetRemoveRoleRequest;
import com.fanap.podchat.requestobject.AddParticipantsRequest;
import com.fanap.podchat.requestobject.ClearHistoryRequest;
import com.fanap.podchat.requestobject.ConnectRequest;
import com.fanap.podchat.requestobject.CreateThreadRequest;
import com.fanap.podchat.requestobject.DeleteMessageRequest;
import com.fanap.podchat.requestobject.GetMessageDeliveredSeenListRequest;
import com.fanap.podchat.requestobject.FileMessageRequest;
import com.fanap.podchat.requestobject.ForwardMessageRequest;
import com.fanap.podchat.requestobject.GetAllThreadAdminsRequest;
import com.fanap.podchat.requestobject.GetHistoryRequest;
import com.fanap.podchat.requestobject.SendLocationMessageRequest;
import com.fanap.podchat.requestobject.MapReverseRequest;
import com.fanap.podchat.requestobject.MapStaticImageRequest;
import com.fanap.podchat.requestobject.SendTextMessageRequest;
import com.fanap.podchat.chat.pin.pin_thread.model.PinUnpinThreadRequest;
import com.fanap.podchat.requestobject.RemoveParticipantsRequest;
import com.fanap.podchat.requestobject.SendReplyFileMessageRequest;
import com.fanap.podchat.requestobject.ReplyTextMessageRequest;
import com.fanap.podchat.requestobject.RequestSetAuditor;
import com.fanap.podchat.requestobject.RequestSignalMsg;
import com.fanap.podchat.requestobject.SpamPrivateThreadRequest;
import com.fanap.podchat.requestobject.GetThreadsRequest;
import com.fanap.podchat.requestobject.UpdateThreadInfoRequest;
import com.fanap.podchat.requestobject.GetThreadParticipantsRequest;
import com.fanap.podchat.requestobject.UnBlockRequest;
import com.fanap.podchat.requestobject.UpdateContactsRequest;
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

        default void onTokenExpired(){}

        default void onPinMessage(ChatResponse<ResultPinMessage> response){}

        default void onUnPinMessage(ChatResponse<ResultPinMessage> response){}

        default void onGetCurrentUserRoles(ChatResponse<ResultCurrentUserRoles> response){}

        default void onTypingSignalTimeout(long threadId){}

        default void onUniqueNameIsAvailable(ChatResponse<ResultIsNameAvailable> response){}

        default void onJoinPublicThread(ChatResponse<ResultJoinPublicThread> response){}

        default void onGetUnreadsMessagesCount(ChatResponse<ResultUnreadMessagesCount> response){}

        default void onGetToken(String token){}

        default void onBotCreated(ChatResponse<CreateBotResult> response){}

        default void onBotCommandsDefined(ChatResponse<DefineBotCommandResult> response){}

        default void onBotStopped(String botName){}

        default void onBotStarted(String botName){}
    }

    interface presenter {

        void enableAutoRefresh(Activity activity,String entry);

        void sendLocationMessage(SendLocationMessageRequest request);

        void sendLocationMessage(SendLocationMessageRequest requestLocationMessage, ProgressHandler.sendFileMessage sendFileMessage);


        void isDatabaseOpen();

        void retryUpload(RetryUpload retry, ProgressHandler.sendFileMessage handler);

        void resendMessage(String uniqueId);

        void cancelMessage(String uniqueId);

        void retryUpload(String uniqueId);

        void cancelUpload(String uniqueId);

        void seenMessageList(GetMessageDeliveredSeenListRequest requestParam);

        void deliveredMessageList(GetMessageDeliveredSeenListRequest requestParams);

        void createThreadWithMessage(CreateThreadRequest threadRequest);

        String createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
                , String metadata);


        void getThreads(GetThreadsRequest requestThread, ChatHandler handler);

        void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName,

                        long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, ChatHandler handler);

        void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName,boolean isNew, ChatHandler handler);

        void setToken(String token);

        void connect(String serverAddress, String appId, String severName, String token, String ssoHost
                , String platformHost, String fileServer, String typeCode);

        void connect(ConnectRequest requestConnect);

        void mapSearch(String searchTerm, Double latitude, Double longitude);

        void mapRouting(String origin, String destination);

        void mapStaticImage(MapStaticImageRequest request);

        void mapReverse(MapReverseRequest request);

        void getUserInfo(ChatHandler handler);

        void getHistory(History history, long threadId, ChatHandler handler);

        void getHistory(GetHistoryRequest request, ChatHandler handler);

        void searchHistory(NosqlListMessageCriteriaVO messageCriteriaVO, ChatHandler handler);

        void getContact(Integer count, Long offset, ChatHandler handler);

        void createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
                , String metaData, ChatHandler handler);

        void sendTextMessage(String textMessage, long threadId, Integer messageType, String metaData, ChatHandler handler);

        void sendTextMessage(SendTextMessageRequest requestMessage, ChatHandler handler);

        void replyMessage(String messageContent, long threadId, long messageId, Integer messageType, ChatHandler handler);

        void replyFileMessage(SendReplyFileMessageRequest request, ProgressHandler.sendFileMessage handler);

        void replyMessage(ReplyTextMessageRequest request, ChatHandler handler);

        void muteThread(int threadId, ChatHandler handler);

        void renameThread(long threadId, String title, ChatHandler handler);

        void unMuteThread(int threadId, ChatHandler handler);

        void editMessage(int messageId, String messageContent, String metaData, ChatHandler handler);

        void getThreadParticipant(int count, Long offset, long threadId, ChatHandler handler);

        void addContact(String firstName, String lastName, String cellphoneNumber, String email,String username);

        void removeContact(long id);

        void searchContact(SearchContactRequest requestSearchContact);

        void block(Long contactId, Long userId, Long threadId, ChatHandler handler);

        void unBlock(Long blockId, Long userId, Long threadId, Long contactId, ChatHandler handler);

        void unBlock(UnBlockRequest request, ChatHandler handler);

        void spam(long threadId);

        String spam(SpamPrivateThreadRequest requestSpam);

        void getBlockList(Long count, Long offset, ChatHandler handler);

        String sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String metaData, Integer messageType, ProgressHandler.sendFileMessage handler);

        String sendFileMessage(FileMessageRequest requestFileMessage, ProgressHandler.sendFileMessage handler);

        void syncContact(Activity activity);

        void forwardMessage(long threadId, ArrayList<Long> messageIds);

        void forwardMessage(ForwardMessageRequest request);

        void updateContact(int id, String firstName, String lastName, String cellphoneNumber, String email);

        void updateContact(UpdateContactsRequest updateContact);

        void uploadImage(Activity activity, Uri fileUri);

        void uploadFile(Activity activity, Uri uri);

        void seenMessage(int messageId, long ownerId, ChatHandler handler);

        void logOut();

        void removeParticipants(long threadId, List<Long> participantIds, ChatHandler handler);

        void removeParticipants(RemoveParticipantsRequest requestRemoveParticipants, ChatHandler handler);

        void addParticipants(long threadId, List<Long> contactIds, ChatHandler handler);

        void addParticipants(AddParticipantsRequest requestAddParticipants, ChatHandler handler);

        void leaveThread(long threadId, ChatHandler handler);

        void updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO, ChatHandler handler);

        void updateThreadInfo(UpdateThreadInfoRequest request, ChatHandler handler);

        void deleteMessage(ArrayList<Long> messageIds,long threadId, Boolean deleteForAll, ChatHandler handler);

        void deleteMessage(DeleteMessageRequest deleteMessage, ChatHandler handler);

        void uploadImageProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgress handler);

        void uploadFileProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgressFile handler);

        void setAdmin(SetRemoveRoleRequest requestAddAdmin);

        void removeAdminRules(SetRemoveRoleRequest requestAddAdmin);

        void clearHistory(ClearHistoryRequest requestClearHistory);

        void getAdminList(GetAllThreadAdminsRequest requestGetAdmin);

//        String startSignalMessage(RequestSignalMsg requestSignalMsg);

//        void stopSignalMessage(String uniqueId);

        void getNotSeenDuration(ArrayList<Integer> userIds);

        String startTyping(long threadId);

        boolean stopTyping(String signalUniqueId);

        void stopAllSignalMessages();

        void pinThread(PinUnpinThreadRequest requestPinThread);

        void unPinThread(PinUnpinThreadRequest requestPinThread);

        void setAuditor(RequestSetAuditor requestAddAdmin);

        void removeAuditor(RequestSetAuditor requestAddAdmin);

        void createThreadWithFile(CreateThreadWithFileRequest request, ProgressHandler.sendFileMessage handler);

        void getUserRoles(GetCurrentUserRolesRequest req);

        void pinMessage(PinUnpinMessageRequest requestPinMessage);

        void unPinMessage(PinUnpinMessageRequest requestPinMessage);

        void getMentionList(GetMentionedRequest req);

        void startTyping(RequestSignalMsg req);

        String downloadFile(GetImageRequest requestGetImage, ProgressHandler.IDownloadFile onProgressFile);

        String downloadFile(GetFileRequest requestGetFile, ProgressHandler.IDownloadFile onProgressFile);

        boolean cancelDownload(String downloadingId);

        void getCacheSize();

        void clearDatabaseCache(Chat.IClearMessageCache listener);

        long getStorageSize();

        long getImageFolderSize();

        long getFilesFolderSize();

        boolean clearPictures();

        boolean clearFiles();

        void closeChat();

        void addContact(AddContactRequest request);

        void updateChatProfile(UpdateProfileRequest request);

        void checkIsNameAvailable(IsPublicThreadNameAvailableRequest request);

        void createPublicThread(RequestCreatePublicThread request);

        void joinPublicThread(JoinPublicThreadRequest request);

        void getContact(GetContactRequest request);

        void getBlockList(GetBlockedListRequest request);

        void getThreadParticipant(GetThreadParticipantsRequest request);

        void shareLogs();

        String downloadFile(RequestGetPodSpaceFile rePod, ProgressHandler.IDownloadFile iDownloadFile);

        void onStart();

        void onStop();

        void onResume();

        String downloadFile(RequestGetPodSpaceImage rePod, ProgressHandler.IDownloadFile iDownloadFile);

        String updateThreadInfo(UpdateThreadInfoRequest request);

        String createThread(CreateThreadRequest requestCreateThread);

        void deliverNotification(String threadId);

        void clearNotifications();

        void defineBotCommand(DefineBotCommandRequest request);

        void createBot(CreateBotRequest request);

        void startBot(StartAndStopBotRequest request);

        void stopBot(StartAndStopBotRequest request);

    }
}
