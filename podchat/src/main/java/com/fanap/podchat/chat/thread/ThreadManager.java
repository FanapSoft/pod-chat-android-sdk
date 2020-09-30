package com.fanap.podchat.chat.thread;

import android.os.Build;
import android.support.annotation.NonNull;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.thread.public_thread.RequestCreatePublicThread;
import com.fanap.podchat.mainmodel.AddParticipant;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.ChatMessageContent;
import com.fanap.podchat.mainmodel.ChatThread;
import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.mainmodel.RemoveParticipant;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultLeaveThread;
import com.fanap.podchat.requestobject.RequestAddParticipants;
import com.fanap.podchat.requestobject.RequestCreateThread;
import com.fanap.podchat.util.AsyncAckType;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.InviteType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.sentry.core.Sentry;
import retrofit2.http.PUT;
import rx.Observable;

public class ThreadManager {


    public interface ILastMessageChanged {

        void onThreadExistInCache(Thread thread);

        void threadNotFoundInCache();

    }

    public interface IThreadInfoCompleter {

        void onThreadInfoReceived(ChatMessage chatMessage);

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

    public static String prepareleaveThreadRequest(long threadId, boolean clearHistory, String uniqueId, String mTypeCode, String token) {
        RemoveParticipant removeParticipant = new RemoveParticipant();

        removeParticipant.setSubjectId(threadId);
        removeParticipant.setToken(token);
        removeParticipant.setTokenIssuer("1");
        removeParticipant.setUniqueId(uniqueId);
        removeParticipant.setType(ChatMessageType.Constants.LEAVE_THREAD);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(removeParticipant);

        if (Util.isNullOrEmpty(mTypeCode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", mTypeCode);
        }

        jsonObject.addProperty("clearHistory", clearHistory);


        String asyncContent = jsonObject.toString();
        return asyncContent;

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
