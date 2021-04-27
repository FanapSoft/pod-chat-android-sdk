package com.fanap.podchat.chat.thread;

import android.os.Build;
import android.support.annotation.NonNull;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.RoleType;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.chat.thread.request.ChangeThreadTypeRequest;
import com.fanap.podchat.chat.thread.request.CloseThreadRequest;
import com.fanap.podchat.chat.thread.request.GetMutualGroupRequest;
import com.fanap.podchat.chat.thread.request.SafeLeaveRequest;
import com.fanap.podchat.chat.thread.respone.CloseThreadResult;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.localmodel.SetRuleVO;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.ChatMessageContent;
import com.fanap.podchat.mainmodel.ChatThread;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.RemoveParticipant;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.mainmodel.UserRoleVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultLeaveThread;
import com.fanap.podchat.model.ResultSetAdmin;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.requestobject.RequestGetHistory;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.requestobject.RequestLeaveThread;
import com.fanap.podchat.requestobject.RequestRole;
import com.fanap.podchat.requestobject.RequestSetAdmin;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.PodChatException;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.sentry.core.Sentry;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class ThreadManager {

    private static PublishSubject<ChatResponse<ResultCurrentUserRoles>> userRolesSubscriber;

    private static Subscription userRolesSubscription;

    private static PublishSubject<ChatResponse<ResultSetAdmin>> setAdminSubscriber;

    private static Subscription setAdminSubscription;

    private static PublishSubject<ChatResponse<ResultLeaveThread>> leaveThreadSubscriber;

    private static Subscription leaveThreadSubscription;

    private static String requestUniqueId = "";


    public static ChatResponse<CloseThreadResult> handleCloseThreadResponse(ChatMessage chatMessage) {

        ChatResponse<CloseThreadResult> response = new ChatResponse<>();

        CloseThreadResult result = new CloseThreadResult();

        result.setThreadId(chatMessage.getSubjectId());

        response.setResult(result);

        response.setUniqueId(chatMessage.getUniqueId());

        response.setSubjectId(chatMessage.getSubjectId());

        response.setCache(false);

        response.setHasError(false);

        return response;
    }

    public static void onError(ChatMessage chatMessage) {

        try {
            if (isUniqueIdIsValid(chatMessage))
                if (chatMessage.getUniqueId().equals(requestUniqueId)) {
                    requestUniqueId = "";
                    unsubscribe(userRolesSubscription);
                    unsubscribe(setAdminSubscription);
                    unsubscribe(leaveThreadSubscription);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isUniqueIdIsValid(ChatMessage chatMessage) {
        return chatMessage != null && chatMessage.getUniqueId() != null && !chatMessage.getUniqueId().isEmpty();
    }

    public interface ILastMessageChanged {

        void onThreadExistInCache(Thread thread);

        void threadNotFoundInCache();

    }

    public interface IThreadInfoCompleter {

        void onThreadInfoReceived(ChatMessage chatMessage);

    }

    public interface ISafeLeaveCallback {

        void onNormalLeaveThreadNeeded(RequestLeaveThread request, String uniqueId);

        void onGetUserRolesNeeded(RequestGetUserRoles request, String uniqueId);

        void onSetAdminNeeded(RequestSetAdmin request, String uniqueId);

        void onThreadLeftSafely(ChatResponse<ResultLeaveThread> resultLeaveThreadChatResponse, String uniqueId);
    }

    public static String createCloseThreadRequest(CloseThreadRequest request, String uniqueId) throws PodChatException {


        validateThreadId(request, uniqueId);

        AsyncMessage message = new ChatMessage();
        message.setType(ChatMessageType.Constants.CLOSE_THREAD);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setSubjectId(request.getThreadId());
        message.setUniqueId(uniqueId);


        return App.getGson().toJson(message);
    }

    public static String createChangeThreadTypeRequest(ChangeThreadTypeRequest request, String uniqueId) throws PodChatException {

        JsonObject content = new JsonObject();
        content.addProperty("type", request.getType());
        if (request.getUniqname() != null)
            content.addProperty("uniqueName", request.getUniqname());

        AsyncMessage message = new ChatMessage();
        message.setType(ChatMessageType.Constants.CHANGE_THREAD_TYPE);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setContent(content.toString());
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setSubjectId(request.getThreadId());
        message.setUniqueId(uniqueId);


        return App.getGson().toJson(message);
    }

    public static String createMutaulGroupRequest(GetMutualGroupRequest request, String uniqueId) {
        JsonObject content = (JsonObject) App.getGson().toJsonTree(request);
        content.remove("useCache");

        AsyncMessage message = new ChatMessage();
        message.setType(ChatMessageType.Constants.MUTAL_GROUPS);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setContent(content.toString());
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setUniqueId(uniqueId);


        return App.getGson().toJson(message);
    }

    public static void safeLeaveThread(SafeLeaveRequest request, String uniqueId, ISafeLeaveCallback callback) {

        requestUniqueId = uniqueId;
        //if RequestSetAdmin is not set call LeaveThread,
        // else => check admin rules by
        // 1. getUser roles=> if has ownership rules=>
        //set users as admin => 2. setAdmin =>
        // 3. leaveThread
        //

        if (request.getSuccessorParticipantId() == 0) {


            createAndSendNormalLeave(request, uniqueId, callback);

        } else {


            RequestGetUserRoles requestGetUserRoles =
                    new RequestGetUserRoles.Builder()
                            .setThreadId(request.getThreadId())
                            .withNoCache()
                            .build();


            userRolesSubscriber = PublishSubject.create();

            userRolesSubscription = userRolesSubscriber.subscribe(createOnReceiveUserRolesAction(request, uniqueId, callback));


            callback.onGetUserRolesNeeded(requestGetUserRoles, uniqueId);


        }


    }

    private static void createAndSendNormalLeave(SafeLeaveRequest request, String uniqueId, ISafeLeaveCallback callback) {
        RequestLeaveThread.Builder requestLeaveThreadBuilder = new RequestLeaveThread.Builder(request.getThreadId());

        if (!request.clearHistory())
            requestLeaveThreadBuilder.shouldKeepHistory();

        requestLeaveThreadBuilder.typeCode(request.getTypeCode());


        callback.onNormalLeaveThreadNeeded(requestLeaveThreadBuilder.build(), uniqueId);
    }

    private static Action1<ChatResponse<ResultCurrentUserRoles>> createOnReceiveUserRolesAction(SafeLeaveRequest request, String uniqueId, ISafeLeaveCallback callback) {
        return resultCurrentUserRolesChatResponse -> {

            if (userHasOwnershipRolesInThread(resultCurrentUserRolesChatResponse)) {

                setAdminSubscriber = PublishSubject.create();

                setAdminSubscription = setAdminSubscriber.subscribe(createOnAdminSetAction(request, uniqueId, callback));

                unsubscribe(userRolesSubscription);

                ArrayList<String> ownerRoles = new ArrayList<>();
                ownerRoles.add(RoleType.Constants.OWNERSHIP.toUpperCase());
                ownerRoles.add(RoleType.Constants.REMOVE_USER.toUpperCase());
                ownerRoles.add(RoleType.Constants.REMOVE_ROLE_FROM_USER.toUpperCase());
                ownerRoles.add(RoleType.Constants.READ_THREAD.toUpperCase());
                ownerRoles.add(RoleType.Constants.POST_CHANNEL_MESSAGE.toUpperCase());
                ownerRoles.add(RoleType.Constants.EDIT_THREAD.toUpperCase());
                ownerRoles.add(RoleType.Constants.EDIT_MESSAGE_OF_OTHERS.toUpperCase());
                ownerRoles.add(RoleType.Constants.DELETE_MESSAGE_OF_OTHERS.toUpperCase());
                ownerRoles.add(RoleType.Constants.ADD_ROLE_TO_USER.toUpperCase());
                ownerRoles.add(RoleType.Constants.THREAD_ADMIN.toUpperCase());
                ownerRoles.add(RoleType.Constants.ADD_NEW_USER.toUpperCase());


                RequestRole requestRole = new RequestRole();
                requestRole.setId(request.getSuccessorParticipantId());
                requestRole.setRoleTypes(ownerRoles);

                ArrayList<RequestRole> requestRoles = new ArrayList<>();

                requestRoles.add(requestRole);

                RequestSetAdmin requestAddAdmin = new RequestSetAdmin
                        .Builder(request.getThreadId(), requestRoles)
                        .build();


                callback.onSetAdminNeeded(requestAddAdmin, uniqueId);

            } else {
                createAndSendNormalLeave(request, uniqueId, callback);
            }


        };
    }

    private static void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    private static Action1<? super ChatResponse<ResultSetAdmin>> createOnAdminSetAction(SafeLeaveRequest request, String uniqueId, ISafeLeaveCallback callback) {
        return resultSetAdminChatResponse -> {

            leaveThreadSubscriber = PublishSubject.create();

            leaveThreadSubscription = leaveThreadSubscriber.subscribe(createOnLeaveThreadAction(uniqueId, callback));

            unsubscribe(setAdminSubscription);

            createAndSendNormalLeave(request, uniqueId, callback);


        };
    }

    private static Action1<? super ChatResponse<ResultLeaveThread>> createOnLeaveThreadAction(String uniqueId, ISafeLeaveCallback callback) {
        return resultLeaveThreadChatResponse -> {


            unsubscribe(leaveThreadSubscription);

            requestUniqueId = "";

            callback.onThreadLeftSafely(resultLeaveThreadChatResponse, uniqueId);

        };
    }


    public static boolean hasUserRolesSubscriber(ChatResponse<ResultCurrentUserRoles> response) {

        if (response.getUniqueId().equals(requestUniqueId)) {
            if (userRolesSubscriber != null) {
                userRolesSubscriber.onNext(response);
                return true;
            }
        }

        return false;
    }

    public static boolean hasSetAdminSubscriber(ChatResponse<ResultSetAdmin> response) {

        if (response.getUniqueId().equals(requestUniqueId)) {
            if (setAdminSubscriber != null) {
                setAdminSubscriber.onNext(response);
                return true;
            }
        }

        return false;
    }

    public static boolean hasLeaveThreadSubscriber(ChatResponse<ResultLeaveThread> response) {

        if (response.getUniqueId().equals(requestUniqueId)) {
            if (leaveThreadSubscriber != null) {
                leaveThreadSubscriber.onNext(response);
                return true;
            }
        }

        return false;

    }

    private static boolean userHasOwnershipRolesInThread(ChatResponse<ResultCurrentUserRoles> resultCurrentUserRolesChatResponse) {

        if (Util.isNotNullOrEmpty(resultCurrentUserRolesChatResponse.getResult()
                .getRoles())) {

            return resultCurrentUserRolesChatResponse.getResult().getRoles().contains(RoleType.Constants.OWNERSHIP.toUpperCase());
        }

        return false;
    }

    public static Observable<List<Thread>> filterIsNew(boolean isNew, List<Thread> allThreads) {

        if (isNew)
            return Observable.from(allThreads)
                    .filter(t -> t.getUnreadCount() > 0)
                    .toList();

        return Observable.from(allThreads).toList();

    }

    public static Observable<List<Thread>> getByIds(List<Integer> ids, List<Thread> allThreads) {

        try {
            if (Util.isNotNullOrEmpty(ids))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return Observable.from(allThreads)
                            .filter(t -> ids.contains(Math.toIntExact(t.getId())))
                            .toList();
                } else {
                    return Observable.from(allThreads)
                            .filter(t -> ids.contains((int) t.getId()))
                            .toList();
                }
        } catch (Exception e) {
            if (Sentry.isEnabled())
                Sentry.captureException(e);
            return Observable.from(allThreads).toList();
        }

        return Observable.from(allThreads).toList();

    }


    public static Observable<List<Thread>> getByName(String name, List<Thread> allThreads) {

        try {
            if (Util.isNotNullOrEmpty(name)) {
                return Observable.from(allThreads)
                        .filter(t -> t.getTitle().contains(name))
                        .toList();
            } else {
                return Observable.from(allThreads).toList();
            }
        } catch (Exception e) {
            if (Sentry.isEnabled())
                Sentry.captureException(e);
            return Observable.from(allThreads).toList();
        }

    }


    private static void validateThreadId(CloseThreadRequest request, String uniqueId) throws PodChatException {

        if (request.getThreadId() <= 0)
            throw new PodChatException("Invalid Thread Id", uniqueId, CoreConfig.token);

    }

    public static int compareThreads(Thread thread1, Thread thread2) {

        if (thread1.isPin() && thread2.isPin())
            return Long.compare(thread1.getTime(), thread2.getTime());

        return Boolean.compare(thread2.isPin(), thread1.isPin());
    }

    public static List<Thread> sortThreads(List<Thread> unsorted) {
        List<Thread> sorted = new ArrayList<>(unsorted);
        Collections.sort(sorted, ThreadManager::compareThreads);
        return sorted;
    }

//    public static List<Thread> sortThreads(List<Thread> unsorted) {
//        List<Thread> sorted = new ArrayList<>(unsorted);
//        Collections.sort(sorted, ThreadManager::compareThreads);
//        return sorted;
//    }

    public static ChatResponse<ResultLeaveThread> prepareLeaveThreadResponse(ChatMessage chatMessage) {

        ChatResponse<ResultLeaveThread> chatResponse = new ChatResponse<>();

        ResultLeaveThread leaveThread = App.getGson().fromJson(chatMessage.getContent(), ResultLeaveThread.class);

        leaveThread.setThreadId(chatMessage.getSubjectId());
        chatResponse.setErrorCode(0);
        chatResponse.setHasError(false);
        chatResponse.setErrorMessage("");
        chatResponse.setUniqueId(chatMessage.getUniqueId());
        chatResponse.setResult(leaveThread);

        return chatResponse;

    }

    public static String prepareCreateThread(RequestCreateThread request, String uniqueId, String typecode, String token) {

        JsonObject chatMessageContent = (JsonObject) App.getGson().toJsonTree(request);

        if (request instanceof RequestCreatePublicThread) {

            String uniqueName = ((RequestCreatePublicThread) request).getUniqueName();

            chatMessageContent.addProperty("uniqueName", uniqueName);


        }

        AsyncMessage chatMessage = new AsyncMessage();
        chatMessage.setContent(chatMessageContent.toString());
        chatMessage.setType(ChatMessageType.Constants.INVITATION);
        chatMessage.setToken(token);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer("1");
        chatMessage.setTypeCode(Util.isNullOrEmpty(request.getTypeCode()) ? typecode : request.getTypeCode());

        String asyncContent = App.getGson().toJson(chatMessage);

        return asyncContent;

    }

    public static String prepareCreateThread(int threadType,
                                             Invitee[] invitee,
                                             String threadTitle,
                                             String description,
                                             String image
            , String metadata, String uniqueId, String typecode, String token) {


        List<Invitee> invitees = new ArrayList<Invitee>(Arrays.asList(invitee));


        ChatThread chatThread = new ChatThread();
        chatThread.setType(threadType);
        chatThread.setInvitees(invitees);
        chatThread.setTitle(threadTitle);

        JsonObject chatThreadObject = (JsonObject) App.getGson().toJsonTree(chatThread);

        if (Util.isNullOrEmpty(description)) {
            chatThreadObject.remove("description");
        } else {
            chatThreadObject.remove("description");
            chatThreadObject.addProperty("description", description);
        }

        if (Util.isNullOrEmpty(image)) {
            chatThreadObject.remove("image");
        } else {
            chatThreadObject.remove("image");
            chatThreadObject.addProperty("image", image);
        }


        if (Util.isNullOrEmpty(metadata)) {
            chatThreadObject.remove("metadata");

        } else {
            chatThreadObject.remove("metadata");
            chatThreadObject.addProperty("metadata", metadata);
        }

        String contentThreadChat = chatThreadObject.toString();

        ChatMessage chatMessage = getChatMessage(contentThreadChat, uniqueId, typecode, token);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(typecode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", typecode);
        }

        String asyncContent = jsonObject.toString();
        return asyncContent;

    }


    @NonNull
    private static ChatMessage getChatMessage(String contentThreadChat, String uniqueId, String typeCode, String token) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(contentThreadChat);
        chatMessage.setType(ChatMessageType.Constants.INVITATION);
        chatMessage.setToken(token);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTokenIssuer("1");

        if (typeCode != null && !typeCode.isEmpty()) {
            chatMessage.setTypeCode(typeCode);
        } else {
            chatMessage.setTypeCode(typeCode);
        }
        return chatMessage;
    }

    public static String prepareLeaveThreadRequest(long threadId, boolean clearHistory, String uniqueId, String mTypeCode, String token) {
        RemoveParticipant removeParticipant = new RemoveParticipant();

        JsonObject content = new JsonObject();
        content.addProperty("clearHistory", clearHistory);

        removeParticipant.setSubjectId(threadId);
        removeParticipant.setToken(token);
        removeParticipant.setTokenIssuer("1");
        removeParticipant.setUniqueId(uniqueId);
        removeParticipant.setContent(content.toString());
        removeParticipant.setType(ChatMessageType.Constants.LEAVE_THREAD);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(removeParticipant);

        if (Util.isNullOrEmpty(mTypeCode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", mTypeCode);
        }


        return jsonObject.toString();

    }

    public static String prepareThreadInfoFromServer(long threadId, String uniqueId, String typeCode, String mtypeCode, String token) {

        ChatMessageContent chatMessageContent = new ChatMessageContent();

        chatMessageContent.setCount(1);

        chatMessageContent.setOffset(0);

        ArrayList<Integer> threadIds = new ArrayList<>();

        threadIds.add((int) threadId);
        chatMessageContent.setThreadIds(threadIds);
        JsonObject content = (JsonObject) App.getGson().toJsonTree(chatMessageContent);

        AsyncMessage asyncMessage = new AsyncMessage();
        asyncMessage.setContent(content.toString());
        asyncMessage.setType(ChatMessageType.Constants.GET_THREADS);
        asyncMessage.setTokenIssuer("1");
        asyncMessage.setToken(token);
        asyncMessage.setUniqueId(uniqueId);
        asyncMessage.setTypeCode(typeCode != null ? typeCode : mtypeCode);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(asyncMessage);
        return jsonObject.toString();
    }

    public static String prepareRenameThreadRequest(long threadId, String title, String uniqueId, String mTypeCode, String token) {


        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessageType.Constants.RENAME);
        chatMessage.setSubjectId(threadId);
        chatMessage.setContent(title);
        chatMessage.setToken(token);
        chatMessage.setTokenIssuer("1");
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTypeCode(mTypeCode);
        String asyncContent = App.getGson().toJson(chatMessage);
        return asyncContent;
    }

    public static String prepareGetThreadRequest(boolean isNew, int finalCount, long finalOffset, String threadName, ArrayList<Integer> threadIds, long creatorCoreUserId, long partnerCoreUserId, long partnerCoreContactId, String uniqueId, String typeCode, String mtypeCode, String token) {

        ChatMessageContent chatMessageContent = new ChatMessageContent();

        chatMessageContent.setNew(isNew);

        chatMessageContent.setCount(finalCount);

        chatMessageContent.setOffset(finalOffset);

        if (threadName != null) {
            chatMessageContent.setName(threadName);
        }

        JsonObject content;

        if (!Util.isNullOrEmpty(threadIds)) {
            chatMessageContent.setThreadIds(threadIds);
            content = (JsonObject) App.getGson().toJsonTree(chatMessageContent);
        } else {
            content = (JsonObject) App.getGson().toJsonTree(chatMessageContent);
            content.remove("threadIds");
        }

        if (creatorCoreUserId > 0) {
            content.addProperty("creatorCoreUserId", creatorCoreUserId);
        }
        if (partnerCoreUserId > 0) {
            content.addProperty("partnerCoreUserId", partnerCoreUserId);
        }
        if (partnerCoreContactId > 0) {
            content.addProperty("partnerCoreContactId", partnerCoreContactId);
        }

        if (!isNew)
            content.remove("new");

        content.remove("lastMessageId");
        content.remove("firstMessageId");

        AsyncMessage chatMessage = new AsyncMessage();
        chatMessage.setContent(content.toString());
        chatMessage.setType(ChatMessageType.Constants.GET_THREADS);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(token);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTypeCode(typeCode != null ? typeCode : mtypeCode);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);
        jsonObject.remove("subjectId");
        return jsonObject.toString();

    }

    public static String prepareSetRoleRequest(SetRuleVO request, String uniqueId, String mtypecode, String token, String TOKEN_ISSUER) {
        ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();
        for (RequestRole requestRole : request.getRoles()) {
            UserRoleVO userRoleVO = new UserRoleVO();
            userRoleVO.setUserId(requestRole.getId());
            userRoleVO.setRoles(requestRole.getRoleTypes());
            userRoleVOS.add(userRoleVO);
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(App.getGson().toJson(userRoleVOS));
        chatMessage.setSubjectId(request.getThreadId());
        chatMessage.setToken(token);
        chatMessage.setType(ChatMessageType.Constants.SET_ROLE_TO_USER);
        chatMessage.setTokenIssuer(TOKEN_ISSUER);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTypeCode(mtypecode);
        return App.getGson().toJson(chatMessage);

    }

    public static String prepareRemoveRoleRequest(SetRuleVO request, String uniqueId, String mtypecode, String token, String TOKEN_ISSUER) {
        ArrayList<UserRoleVO> userRoleVOS = new ArrayList<>();
        for (RequestRole requestRole : request.getRoles()) {
            UserRoleVO userRoleVO = new UserRoleVO();
            userRoleVO.setUserId(requestRole.getId());
            userRoleVO.setRoles(requestRole.getRoleTypes());
            userRoleVOS.add(userRoleVO);
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(App.getGson().toJson(userRoleVOS));
        chatMessage.setSubjectId(request.getThreadId());
        chatMessage.setToken(token);
        chatMessage.setType(ChatMessageType.Constants.REMOVE_ROLE_FROM_USER);
        chatMessage.setTokenIssuer(TOKEN_ISSUER);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setTypeCode(mtypecode);
        return App.getGson().toJson(chatMessage);
    }

    public static ChatResponse<Thread> handleChangeThreadType(ChatMessage chatMessage) {

        ChatResponse<Thread> response = new ChatResponse<>();
        Thread thread = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<Thread>() {
        }.getType());
        response.setResult(thread);

        response.setUniqueId(chatMessage.getUniqueId());

        response.setCache(false);

        return response;
    }

    public static String prepareGetHIstoryWithUniqueIdsRequest(long threadId, String uniqueId, String[] uniqueIds, String typeCode, String token) {
        RequestGetHistory request = new RequestGetHistory
                .Builder(threadId)
                .offset(0)
                .count(uniqueIds.length)
                .uniqueIds(uniqueIds)
                .build();

        String content = App.getGson().toJson(request);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(ChatMessageType.Constants.GET_HISTORY);
        chatMessage.setToken(token);
        chatMessage.setTokenIssuer("1");
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(typeCode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", typeCode);
        }

        String asyncContent = jsonObject.toString();
        return asyncContent;
    }

    public static class ThreadResponse {
        private List<Thread> threadList;
        private long contentCount;
        private String source;

        public ThreadResponse(List<Thread> threadList, long contentCount, String source) {
            this.threadList = threadList;
            this.contentCount = contentCount;
            this.source = source;
        }

        public List<Thread> getThreadList() {
            return threadList;
        }

        public long getContentCount() {
            return contentCount;
        }

        public String getSource() {
            return source;
        }
    }

}
