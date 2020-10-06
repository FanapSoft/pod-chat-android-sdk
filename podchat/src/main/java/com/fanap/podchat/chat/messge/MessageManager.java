package com.fanap.podchat.chat.messge;

import android.support.annotation.NonNull;

import com.fanap.podchat.cachemodel.queue.Failed;
import com.fanap.podchat.cachemodel.queue.Sending;
import com.fanap.podchat.cachemodel.queue.SendingQueueCache;
import com.fanap.podchat.cachemodel.queue.Uploading;
import com.fanap.podchat.cachemodel.queue.UploadingQueueCache;
import com.fanap.podchat.cachemodel.queue.WaitQueueCache;
import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.History;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.functions.Func1;

public class MessageManager {


    public static String getAllUnreadMessgesCount(RequestGetUnreadMessagesCount request, String uniqueId) {


        JsonObject content = new JsonObject();
        content.addProperty("mute", request.withMuteThreads());

        AsyncMessage message = new AsyncMessage();
        message.setContent(content.toString());
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.ALL_UNREAD_MESSAGE_COUNT);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(!Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : CoreConfig.typeCode);

        JsonObject tmp = (JsonObject) App.getGson().toJsonTree(message);

        tmp.remove("subjectId");

        return tmp.toString();

    }


    public static ChatResponse<ResultUnreadMessagesCount> handleUnreadMessagesResponse(ChatMessage chatMessage) {

        ChatResponse<ResultUnreadMessagesCount> response = new ChatResponse<>();

        ResultUnreadMessagesCount result = new ResultUnreadMessagesCount();

        result.setUnreadsCount(Long.valueOf(chatMessage.getContent()));

        response.setResult(result);

        return response;

    }

    public static ChatResponse<ResultUnreadMessagesCount> handleUnreadMessagesCacheResponse(
            long unreadCount
    ) {


        ChatResponse<ResultUnreadMessagesCount> response = new ChatResponse<>();

        ResultUnreadMessagesCount result = new ResultUnreadMessagesCount();

        result.setUnreadsCount(unreadCount);

        response.setResult(result);

        response.setCache(true);

        return response;


    }

    public static List<MessageVO> sortMessages(List<MessageVO> unsortedMessages) {

        List<MessageVO> sortedMessages = new ArrayList<>(unsortedMessages);

        Collections.sort(sortedMessages, compareMessages());


        return sortedMessages;
    }

    private static Comparator<MessageVO> compareMessages() {

        return (message1, message2) -> Long.compare(message1.getTime(), message2.getTime());
    }

    public static Func1<MessageVO, Boolean> filterByThread(long threadId) {
        return message -> message.getConversation() != null && message.getConversation().getId() == threadId;
    }

    public static Func1<MessageVO, Boolean> filterByMessageType(History request) {
        return messageVO ->
        {
            if (request.getMessageType() > 0) {

                return messageVO.getMessageType() == request.getMessageType();

            } else return true;
        };
    }

    public static Func1<MessageVO, Boolean> filterByQuery(History request) {

        return messageVO -> {

            if (Util.isNotNullOrEmpty(request.getQuery())) {

                if (Util.isNotNullOrEmpty(messageVO.getMessage()))
                    return false;
                else return messageVO.getMessage().contains(request.getQuery());

            }
            return true;
        };

    }

    public static Func1<MessageVO, Boolean> filterByFromTime(History request) {

        return messageVO -> {

            long fromTime = request.getFromTime();

            long fromTimeNanos = request.getFromTimeNanos();

            if (!Util.isNullOrEmpty(fromTime)) {
                long pow = (long) Math.pow(10, 9);
                if (!Util.isNullOrEmpty(fromTimeNanos)) {
                    long timestamp = ((fromTime / 1000) * pow) + fromTimeNanos;
                    return messageVO.getTime() >= timestamp;
                } else {
                    long timestamp = ((fromTime / 1000) * pow);
                    return messageVO.getTime() >= timestamp;
                }
            }

            return true;

        };
    }

    public static Func1<MessageVO, Boolean> filterByToTime(History request) {

        return messageVO -> {

            long fromTime = request.getFromTime();

            long fromTimeNanos = request.getFromTimeNanos();

            if (!Util.isNullOrEmpty(fromTime)) {
                long pow = (long) Math.pow(10, 9);
                if (!Util.isNullOrEmpty(fromTimeNanos)) {
                    long timestamp = ((fromTime / 1000) * pow) + fromTimeNanos;
                    return messageVO.getTime() <= timestamp;
                } else {
                    long timestamp = ((fromTime / 1000) * pow);
                    return messageVO.getTime() <= timestamp;
                }
            }

            return true;

        };
    }

    public static List<Sending> getSendingFromSendingCache(List<SendingQueueCache> listCaches) {
        List<Sending> listQueues = new ArrayList<>();
        for (SendingQueueCache queueCache : listCaches) {
            Sending sending = new Sending();
            sending.setThreadId(queueCache.getThreadId());

            MessageVO messageVO = new MessageVO();
            messageVO.setId(queueCache.getId());
            messageVO.setMessage(queueCache.getMessage());
            messageVO.setMessageType(queueCache.getMessageType());
            messageVO.setMetadata(queueCache.getMetadata());
            messageVO.setSystemMetadata(queueCache.getSystemMetadata());

            sending.setMessageVo(messageVO);

            sending.setUniqueId(queueCache.getUniqueId());

            listQueues.add(sending);
        }
        return listQueues;
    }

    public static List<Failed> getFailedFromWaiting(List<WaitQueueCache> listCaches) {

        List<Failed> listQueues = new ArrayList<>();

        for (WaitQueueCache queueCache : listCaches) {

            Failed failed = new Failed();

            MessageVO messageVO = new MessageVO();

            messageVO.setId(queueCache.getId());
            messageVO.setMessage(queueCache.getMessage());
            messageVO.setMessageType(queueCache.getMessageType());
            messageVO.setMetadata(queueCache.getMetadata());
            messageVO.setSystemMetadata(queueCache.getSystemMetadata());

            failed.setMessageVo(messageVO);
            failed.setThreadId(queueCache.getThreadId());
            failed.setUniqueId(queueCache.getUniqueId());

            listQueues.add(failed);
        }

        return listQueues;
    }

    public static List<Uploading> getUploadingFromUploadCache(List<UploadingQueueCache> uploadingQueueCaches)

    {
        List<Uploading> uploadingQueues = new ArrayList<>();
        for (UploadingQueueCache queueCache : uploadingQueueCaches) {
            Uploading uploadingQueue = new Uploading();

            MessageVO messageVO = new MessageVO();

            messageVO.setId(queueCache.getId());
            messageVO.setMessage(queueCache.getMessage());
            messageVO.setMessageType(queueCache.getMessageType());
            messageVO.setMetadata(queueCache.getMetadata());
            messageVO.setSystemMetadata(queueCache.getSystemMetadata());

            uploadingQueue.setMessageVo(messageVO);
            uploadingQueue.setThreadId(queueCache.getThreadId());
            uploadingQueue.setUniqueId(queueCache.getUniqueId());
            uploadingQueues.add(uploadingQueue);
        }

        return uploadingQueues;
    }

    public static WaitQueueCache getWaitingFromSendingMessage(@NonNull SendingQueueCache sendingQueue) {
        WaitQueueCache waitMessageQueue = new WaitQueueCache();

        waitMessageQueue.setUniqueId(sendingQueue.getUniqueId());
        waitMessageQueue.setId(sendingQueue.getId());
        waitMessageQueue.setAsyncContent(sendingQueue.getAsyncContent());
        waitMessageQueue.setMessage(sendingQueue.getMessage());
        waitMessageQueue.setThreadId(sendingQueue.getThreadId());
        waitMessageQueue.setMessageType(sendingQueue.getMessageType());
        waitMessageQueue.setSystemMetadata(sendingQueue.getSystemMetadata());
        waitMessageQueue.setMetadata(sendingQueue.getMetadata());
        return waitMessageQueue;
    }

    public static SendingQueueCache getSendingFromWaitingMessage(WaitQueueCache waitQueueCache) {
        SendingQueueCache sendingQueueCache = new SendingQueueCache();
        sendingQueueCache.setAsyncContent(waitQueueCache.getAsyncContent());
        sendingQueueCache.setId(waitQueueCache.getId());
        sendingQueueCache.setMessage(waitQueueCache.getMessage());
        sendingQueueCache.setMetadata(waitQueueCache.getMetadata());
        sendingQueueCache.setThreadId(waitQueueCache.getThreadId());
        sendingQueueCache.setUniqueId(waitQueueCache.getUniqueId());
        sendingQueueCache.setSystemMetadata(waitQueueCache.getSystemMetadata());


        return sendingQueueCache;
    }




    public static Boolean hasGap(List<MessageVO> messagesFromCache) {

        for (MessageVO msg : messagesFromCache) {
            if (msg.hasGap()) {
               return true;
            }
        }
        return false;
    }


    public static class HistoryResponse {
        ChatResponse<ResultHistory> response;
        private String source;

        public HistoryResponse(ChatResponse<ResultHistory> response, String source) {
            this.response = response;
            this.source = source;
        }


        public ChatResponse<ResultHistory> getResponse() {
            return response;
        }

        public String getSource() {
            return source;
        }
    }

}
