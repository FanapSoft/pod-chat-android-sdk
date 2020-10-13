package com.fanap.podchat.chat.thread;

import android.os.Build;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.RoleType;
import com.fanap.podchat.chat.thread.request.CloseThreadRequest;
import com.fanap.podchat.chat.thread.request.SafeLeaveRequest;
import com.fanap.podchat.chat.thread.respone.CloseThreadResult;
import com.fanap.podchat.chat.user.user_roles.model.ResultCurrentUserRoles;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultLeaveThread;
import com.fanap.podchat.model.ResultSetAdmin;
import com.fanap.podchat.requestobject.RequestGetUserRoles;
import com.fanap.podchat.requestobject.RequestLeaveThread;
import com.fanap.podchat.requestobject.RequestSetAdmin;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.PodChatException;
import com.fanap.podchat.util.Util;

import java.util.ArrayList;
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

        if (chatMessage.getUniqueId().equals(requestUniqueId)) {
            requestUniqueId = "";
            unsubscribe(userRolesSubscription);
            unsubscribe(setAdminSubscription);
            unsubscribe(leaveThreadSubscription);
        }
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

    public static void safeLeaveThread(SafeLeaveRequest request, String uniqueId, ISafeLeaveCallback callback) {

        requestUniqueId = uniqueId;
        //if RequestSetAdmin is not set call LeaveThread,
        // else => check admin rules by
        // 1. getUser roles=> if has ownership rules=>
        //set users as admin => 2. setAdmin =>
        // 3. leaveThread
        //

        if (request.getRequestSetAdmin() == null) {
            callback.onNormalLeaveThreadNeeded(request, uniqueId);
        } else {

            RequestGetUserRoles requestGetUserRoles =
                    new RequestGetUserRoles.Builder()
                            .setThreadId(request.getThreadId())
                            .withNoCache()
                            .build();

            callback.onGetUserRolesNeeded(requestGetUserRoles, uniqueId);

            userRolesSubscriber = PublishSubject.create();

            userRolesSubscription = userRolesSubscriber.subscribe(createOnReceiveUserRolesAction(request, uniqueId, callback));
        }


    }

    private static Action1<ChatResponse<ResultCurrentUserRoles>> createOnReceiveUserRolesAction(SafeLeaveRequest request, String uniqueId, ISafeLeaveCallback callback) {
        return resultCurrentUserRolesChatResponse -> {

            if (userHasOwnershipRolesInThread(resultCurrentUserRolesChatResponse)) {

                callback.onSetAdminNeeded(request.getRequestSetAdmin(), uniqueId);

                setAdminSubscriber = PublishSubject.create();

                setAdminSubscription = setAdminSubscriber.subscribe(createOnAdminSetAction(request, uniqueId, callback));

                unsubscribe(userRolesSubscription);

            } else {
                callback.onNormalLeaveThreadNeeded(request, uniqueId);
            }


        };
    }

    private static void unsubscribe(Subscription subscription) {
        if (!subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    private static Action1<? super ChatResponse<ResultSetAdmin>> createOnAdminSetAction(SafeLeaveRequest request, String uniqueId, ISafeLeaveCallback callback) {
        return resultSetAdminChatResponse -> {

            callback.onNormalLeaveThreadNeeded(request, uniqueId);

            leaveThreadSubscriber = PublishSubject.create();

            leaveThreadSubscription = leaveThreadSubscriber.subscribe(createOnLeaveThreadAction(uniqueId, callback));

            unsubscribe(setAdminSubscription);

        };
    }

    private static Action1<? super ChatResponse<ResultLeaveThread>> createOnLeaveThreadAction(String uniqueId, ISafeLeaveCallback callback) {
        return resultLeaveThreadChatResponse -> {

            callback.onThreadLeftSafely(resultLeaveThreadChatResponse, uniqueId);

            unsubscribe(leaveThreadSubscription);

            requestUniqueId = "";
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

            return resultCurrentUserRolesChatResponse.getResult().getRoles().contains(
                   "THREAD_ADMIN"
            ) &&
                    resultCurrentUserRolesChatResponse.getResult().getRoles().contains(
                            "ADD_RULE_TO_USER"
                    );
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
    }

}
