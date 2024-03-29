package com.example.chat.application.chatexample;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.chat.ChatHandler;
import com.fanap.podchat.chat.assistant.request_model.BlockUnblockAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.DeActiveAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantHistoryRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetBlockedAssistantsRequest;
import com.fanap.podchat.chat.assistant.request_model.RegisterAssistantRequest;
import com.fanap.podchat.chat.bot.request_model.CreateBotRequest;
import com.fanap.podchat.chat.bot.request_model.DefineBotCommandRequest;
import com.fanap.podchat.chat.bot.request_model.GetUserBotsRequest;
import com.fanap.podchat.chat.bot.request_model.StartAndStopBotRequest;
import com.fanap.podchat.chat.bot.result_model.CreateBotResult;
import com.fanap.podchat.chat.bot.result_model.DefineBotCommandResult;
import com.fanap.podchat.chat.hashtag.model.RequestGetHashTagList;
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.chat.messge.ResultUnreadMessagesCount;
import com.fanap.podchat.chat.pin.pin_message.model.RequestPinMessage;
import com.fanap.podchat.chat.pin.pin_message.model.ResultPinMessage;
import com.fanap.podchat.chat.pin.pin_thread.model.RequestPinThread;
import com.fanap.podchat.chat.ping.result.StatusPingResult;
import com.fanap.podchat.chat.tag.request_model.AddTagParticipantRequest;
import com.fanap.podchat.chat.tag.request_model.CreateTagRequest;
import com.fanap.podchat.chat.tag.request_model.DeleteTagRequest;
import com.fanap.podchat.chat.tag.request_model.EditTagRequest;
import com.fanap.podchat.chat.tag.request_model.GetTagListRequest;
import com.fanap.podchat.chat.tag.request_model.RemoveTagParticipantRequest;
import com.fanap.podchat.chat.tag.result_model.TagResult;
import com.fanap.podchat.chat.thread.public_thread.RequestCheckIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.chat.thread.public_thread.RequestJoinPublicThread;
import com.fanap.podchat.chat.thread.public_thread.ResultIsNameAvailable;
import com.fanap.podchat.chat.thread.public_thread.ResultJoinPublicThread;
import com.fanap.podchat.chat.thread.request.ChangeThreadTypeRequest;
import com.fanap.podchat.chat.thread.request.GetMutualGroupRequest;
import com.fanap.podchat.chat.user.profile.RequestUpdateProfile;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.chat.messge.SearchSystemMetadataRequest;
import com.fanap.podchat.mainmodel.RequestSearchContact;
import com.fanap.podchat.mainmodel.ThreadInfoVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultContact;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultStaticMapImage;
import com.fanap.podchat.model.ResultThreads;
import com.fanap.podchat.model.ResultUserInfo;
import com.fanap.podchat.requestobject.RemoveParticipantRequest;
import com.fanap.podchat.requestobject.RequestAddContact;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestBlockList;
import com.fanap.podchat.requestobject.RequestClearHistory;
import com.fanap.podchat.requestobject.RequestConnect;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.requestobject.RequestCreateThreadWithFile;
import com.fanap.podchat.requestobject.RequestDeleteMessage;
import com.fanap.podchat.requestobject.RequestDeliveredMessageList;
import com.fanap.podchat.requestobject.RequestFileMessage;
import com.fanap.podchat.requestobject.RequestForwardMessage;
import com.fanap.podchat.requestobject.RequestGetAdmin;
import com.fanap.podchat.requestobject.RequestGetContact;
import com.fanap.podchat.requestobject.RequestGetFile;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestGetImage;
import com.fanap.podchat.requestobject.RequestGetPodSpaceFile;
import com.fanap.podchat.requestobject.RequestGetPodSpaceImage;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.requestobject.RequestLocationMessage;
import com.fanap.podchat.requestobject.RequestMapReverse;
import com.fanap.podchat.requestobject.RequestMapStaticImage;
import com.fanap.podchat.requestobject.RequestMessage;
import com.fanap.podchat.requestobject.RequestReplyFileMessage;
import com.fanap.podchat.requestobject.RequestReplyMessage;
import com.fanap.podchat.requestobject.RequestSeenMessageList;
import com.fanap.podchat.requestobject.RequestSetAdmin;
import com.fanap.podchat.requestobject.RequestSetAuditor;
import com.fanap.podchat.requestobject.RequestSignalMsg;
import com.fanap.podchat.requestobject.RequestSpam;
import com.fanap.podchat.requestobject.RequestThread;
import com.fanap.podchat.requestobject.RequestThreadInfo;
import com.fanap.podchat.requestobject.RequestThreadParticipant;
import com.fanap.podchat.requestobject.RequestUnBlock;
import com.fanap.podchat.requestobject.RequestUpdateContact;
import com.fanap.podchat.requestobject.RetryUpload;

import java.util.ArrayList;
import java.util.List;


public interface ChatContract {

    interface view {

//        default void onRecivedNotification(Notification notification) {

//        }

        default void onGetUserInfo(ChatResponse<ResultUserInfo> outPutUserInfo) {
        }

        default void onLogEvent(String log) {
        }

        default void onGetThreadList(String content, ChatResponse<ResultThreads> thread) {
        }

        default void onGetThreadHistory(ChatResponse<ResultHistory> history) {
        }

        default void onGetContacts(ChatResponse<ResultContact> outPutContact) {

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

        default void onError(String message) {
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

        default void pingStatusSent(ChatResponse<StatusPingResult> response){}



        default void updateStatus(String message){}

        default void onThreadClosed(long subjectId){}

        default void onGetSentryLogs(String logs){}

        default void onChatProfileUpdated(ResultUpdateProfile result){}
        default void onTagCreated(TagResult result){}

        default void onLoginNeeded(){}

        default void onLoadingContactsStarted(){}

        default void setInitState(){}
    }

    interface presenter {

        void enableAutoRefresh(Activity activity,String entry);

        void sendLocationMessage(RequestLocationMessage request);

        void sendLocationMessage(RequestLocationMessage requestLocationMessage, ProgressHandler.sendFileMessage sendFileMessage);

        void searchMap(String haram, double lat, double lon);

        void retryUpload(RetryUpload retry, ProgressHandler.sendFileMessage handler);

        void resendMessage(String uniqueId);

        void cancelMessage(String uniqueId);

        void retryUpload(String uniqueId);
        void getSentryLogs();

        void cancelUpload(String uniqueId);

        void getSeenMessageList(RequestSeenMessageList requestParam);

        void deliveredMessageList(RequestDeliveredMessageList requestParams);

        void createThreadWithMessage(RequestCreateThread threadRequest);

        String createThread(int threadType, Invitee[] invitee, String threadTitle, String description, String image
                , String metadata);


        void getThreads(RequestThread requestThread, ChatHandler handler);

        void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName,

                        long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, ChatHandler handler);

        void getThreads(Integer count, Long offset, ArrayList<Integer> threadIds, String threadName,boolean isNew, ChatHandler handler);

        void setToken(String token);

        void connect(String serverAddress, String appId, String severName, String token, String ssoHost
                , String platformHost, String fileServer, String typeCode);

        void connect(RequestConnect requestConnect);

        void mapSearch(String searchTerm, Double latitude, Double longitude);

        void mapRouting(String origin, String destination);

        void mapStaticImage(RequestMapStaticImage request);

        void mapReverse(RequestMapReverse request);

        void getUserInfo(ChatHandler handler);

        void getHistory(History history, long threadId, ChatHandler handler);

        String getHistory(RequestGetHistory request, ChatHandler handler);
        String getHashTagList(RequestGetHashTagList request, ChatHandler handler);

        void searchHistory(SearchSystemMetadataRequest messageCriteriaVO, ChatHandler handler);

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

        void addContact(String firstName, String lastName, String cellphoneNumber, String email,String username);

        void removeContact(long id);

        void searchContact(RequestSearchContact requestSearchContact);

        void block(Long contactId, Long userId, Long threadId, ChatHandler handler);

        void unBlock(Long blockId, Long userId, Long threadId, Long contactId, ChatHandler handler);

        void unBlock(RequestUnBlock request, ChatHandler handler);

        void spam(long threadId);

        String spam(RequestSpam requestSpam);

        void getBlockList(Long count, Long offset, ChatHandler handler);

        String sendFileMessage(Context context, Activity activity, String description, long threadId, Uri fileUri, String metaData, Integer messageType, ProgressHandler.sendFileMessage handler);

        String sendFileMessage(RequestFileMessage requestFileMessage, ProgressHandler.sendFileMessage handler);

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

        void removeParticipants(RemoveParticipantRequest removeParticipantRequest, ChatHandler handler);


        void addParticipants(RequestAddParticipants requestAddParticipants, ChatHandler handler);

        void leaveThread(long threadId, ChatHandler handler);

        void updateThreadInfo(long threadId, ThreadInfoVO threadInfoVO, ChatHandler handler);

        void updateThreadInfo(RequestThreadInfo request, ChatHandler handler);

        void deleteMessage(ArrayList<Long> messageIds,long threadId, Boolean deleteForAll, ChatHandler handler);

        void deleteMessage(RequestDeleteMessage deleteMessage, ChatHandler handler);

        void uploadImageProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgress handler);

        void uploadFileProgress(Context context, Activity activity, Uri fileUri, ProgressHandler.onProgressFile handler);

        void setAdmin(RequestSetAdmin requestAddAdmin);

        void removeAdminRules(RequestSetAdmin requestAddAdmin);

        void clearHistory(RequestClearHistory requestClearHistory);

        void getAdminList(RequestGetAdmin requestGetAdmin);

        void getNotSeenDuration(ArrayList<Integer> userIds);

        String startTyping(long threadId);

        boolean stopTyping(String signalUniqueId);

        void stopAllSignalMessages();

        void pinThread(RequestPinThread requestPinThread);

        void unPinThread(RequestPinThread requestPinThread);

        void setAuditor(RequestSetAuditor requestAddAdmin);

        void removeAuditor(RequestSetAuditor requestAddAdmin);

        void createThreadWithFile(RequestCreateThreadWithFile request,ProgressHandler.sendFileMessage handler);

        void getUserRoles(RequestGetUserRoles req);

        void pinMessage(RequestPinMessage requestPinMessage);

        void unPinMessage(RequestPinMessage requestPinMessage);

        void getMentionList(RequestGetMentionList req);

        void startTyping(RequestSignalMsg req);

        String downloadFile(RequestGetImage requestGetImage, ProgressHandler.IDownloadFile onProgressFile);

        String downloadFile(RequestGetFile requestGetFile, ProgressHandler.IDownloadFile onProgressFile);

        boolean cancelDownload(String downloadingId);

        void getCacheSize();

        void clearDatabaseCache(Chat.IClearMessageCache listener);

        long getStorageSize();

        long getImageFolderSize();

        long getFilesFolderSize();

        boolean clearPictures();

        boolean clearFiles();

        void closeChat();

        void addContact(RequestAddContact request);

        void updateChatProfile(RequestUpdateProfile request);

        void checkIsNameAvailable(RequestCheckIsNameAvailable request);

        void createPublicThread(RequestCreatePublicThread request);

        void joinPublicThread(RequestJoinPublicThread request);

        void getContact(RequestGetContact request);

        void getBlockList(RequestBlockList request);

        void getThreadParticipant(RequestThreadParticipant request);

        void shareLogs();

        String downloadFile(RequestGetPodSpaceFile rePod, ProgressHandler.IDownloadFile iDownloadFile);

        void onStart();

        void onStop();

        void onResume();

        String downloadFile(RequestGetPodSpaceImage rePod, ProgressHandler.IDownloadFile iDownloadFile);

        String updateThreadInfo(RequestThreadInfo request);

        String createThread(RequestCreateThread requestCreateThread);

        void deliverNotification(String threadId);

        void clearNotifications();

        void defineBotCommand(DefineBotCommandRequest request);

        void createBot(CreateBotRequest request);

        void startBot(StartAndStopBotRequest request);

        void stopBot(StartAndStopBotRequest request);

        void getUserBots(GetUserBotsRequest request);

        void closeThread(int testThreadId);

        void getContact();

        void registerAssistant(RegisterAssistantRequest request);

        void getAssistants(GetAssistantRequest request);

        void deActiveAssistant(DeActiveAssistantRequest request);

        void getAssistantHistory(GetAssistantHistoryRequest request);

        void blockAssistant(BlockUnblockAssistantRequest request);

        void unBlockAssistant(BlockUnblockAssistantRequest request);

        void getBlocksAssistant(GetBlockedAssistantsRequest request);

        void changeThreadType(ChangeThreadTypeRequest request);


        void createTag(CreateTagRequest request);

        void editTag(EditTagRequest request);

        void deleteTag(DeleteTagRequest request);

        void addTagParticipant(AddTagParticipantRequest request);

        void removeTagParticipant(RemoveTagParticipantRequest request);

       void getTagList(GetTagListRequest request);

        void getMutualGroups(GetMutualGroupRequest request);

    }
}
