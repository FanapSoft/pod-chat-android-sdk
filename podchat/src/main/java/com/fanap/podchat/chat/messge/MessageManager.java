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
import com.fanap.podchat.mainmodel.ResultDeleteMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.DeleteMessageContent;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.model.ResultNewMessage;
import com.fanap.podchat.requestobject.RequestSpam;
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

    public static List<Uploading> getUploadingFromUploadCache(List<UploadingQueueCache> uploadingQueueCaches) {
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


    public static String prepareSpamRequest(String uniqueId, RequestSpam request, String mtypeCode, String token) {
        JsonObject jsonObject;

        long threadId = request.getThreadId();
        String typeCode = request.getTypeCode();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessageType.Constants.SPAM_PV_THREAD);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(CoreConfig.token);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);
        jsonObject.remove("contentCount");
        jsonObject.remove("systemMetadata");
        jsonObject.remove("metadata");
        jsonObject.remove("repliedTo");

        if (Util.isNullOrEmpty(typeCode)) {
            if (Util.isNullOrEmpty(mtypeCode)) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.addProperty("typeCode", CoreConfig.typeCode);
            }
        } else {
            jsonObject.addProperty("typeCode", request.getTypeCode());
        }

        return jsonObject.toString();
    }

    public static ChatResponse<ResultDeleteMessage> prepareDeleteMessageResponse(ChatMessage chatMessage, long messageId) {

        ChatResponse<ResultDeleteMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(chatMessage.getUniqueId());

        ResultDeleteMessage resultDeleteMessage = new ResultDeleteMessage();
        DeleteMessageContent deleteMessage = new DeleteMessageContent();
        deleteMessage.setId(messageId);
        resultDeleteMessage.setDeletedMessage(deleteMessage);
        chatResponse.setResult(resultDeleteMessage);
        chatResponse.setSubjectId(chatMessage.getSubjectId());

        return chatResponse;
    }

    public static ChatResponse<ResultNewMessage> prepareNewMessageResponse(MessageVO newMessage, long threadId, String uniqueId) {
        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
        ResultNewMessage editMessage = new ResultNewMessage();

        editMessage.setMessageVO(newMessage);
        editMessage.setThreadId(threadId);
        chatResponse.setResult(editMessage);
        chatResponse.setUniqueId(uniqueId);
        chatResponse.setSubjectId(threadId);
        return chatResponse;
    }


    public static ChatResponse<ResultNewMessage> preparepublishNewMessagesResponse(MessageVO messageVO, long threadId) {

        ChatResponse<ResultNewMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(messageVO.getUniqueId());
        chatResponse.setHasError(false);
        chatResponse.setErrorCode(0);
        chatResponse.setErrorMessage("");
        ResultNewMessage resultNewMessage = new ResultNewMessage();
        resultNewMessage.setMessageVO(messageVO);
        resultNewMessage.setThreadId(threadId);
        chatResponse.setResult(resultNewMessage);
        chatResponse.setSubjectId(threadId);

        return chatResponse;
    }

    public static String prepareMainHistoryResponse(History history, long threadId, String uniqueId, String typecode, String token) {
        //        long offsets = history.getOffset();

        long fromTime = history.getFromTime();
        long fromTimeNanos = history.getFromTimeNanos();
        long toTime = history.getToTime();
        long toTimeNanos = history.getToTimeNanos();

        String query = history.getQuery();

        JsonObject content = (JsonObject) App.getGson().toJsonTree(history);

        if (history.getLastMessageId() == 0) {
            content.remove("lastMessageId");
        }

        if (history.getFirstMessageId() == 0) {
            content.remove("firstMessageId");
        }

        if (history.getId() <= 0) {
            content.remove("id");
        }

        if (Util.isNullOrEmpty(query)) {
            content.remove("query");
        }

        if (Util.isNullOrEmpty(fromTime)) {
            content.remove("fromTime");
        }

        if (Util.isNullOrEmpty(fromTimeNanos)) {
            content.remove("fromTimeNanos");
        }

        if (Util.isNullOrEmpty(toTime)) {
            content.remove("toTime");
        }

        if (Util.isNullOrEmpty(toTimeNanos)) {
            content.remove("toTimeNanos");
        }

        if (history.getUniqueIds() == null) {

            content.remove("uniqueIds");

        }

        if (history.getMessageType() == 0) {
            content.remove("messageType");
        }

        AsyncMessage chatMessage = new AsyncMessage();
        chatMessage.setContent(content.toString());
        chatMessage.setType(ChatMessageType.Constants.GET_HISTORY);
        chatMessage.setToken(token);
        chatMessage.setTokenIssuer("1");
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);
        chatMessage.setTypeCode(typecode);

        return App.getGson().toJsonTree(chatMessage).toString();
    }

    public static JsonObject prepareSendTextMessageRequest(String textMessage, long threadId, Integer messageType, String jsonSystemMetadata, String uniqueId, String mTypecode, String token) {

        ChatMessage chatMessageQueue = new ChatMessage();
        chatMessageQueue.setContent(textMessage);
        chatMessageQueue.setType(ChatMessageType.Constants.MESSAGE);
        chatMessageQueue.setTokenIssuer("1");
        chatMessageQueue.setToken(token);


        if (jsonSystemMetadata != null) {
            chatMessageQueue.setSystemMetadata(jsonSystemMetadata);
        }

        chatMessageQueue.setUniqueId(uniqueId);
        chatMessageQueue.setTime(1000);
        chatMessageQueue.setSubjectId(threadId);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessageQueue);

        if (Util.isNullOrEmpty(mTypecode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", mTypecode);
        }
        if (!Util.isNullOrEmpty(messageType)) {
            jsonObject.addProperty("messageType", messageType);
        } else {
            jsonObject.remove("messageType");
        }

        return jsonObject;
    }

    //for dericated method
    public static String prepareSpamRequest(String uniqueId, String mtypeCode, long threadId, String token) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessageType.Constants.SPAM_PV_THREAD);
        chatMessage.setTokenIssuer("1");
        chatMessage.setToken(token);
        chatMessage.setUniqueId(uniqueId);
        chatMessage.setSubjectId(threadId);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);

        if (Util.isNullOrEmpty(mtypeCode)) {
            jsonObject.remove("typeCode");
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", mtypeCode);
        }

        String asyncContent = jsonObject.toString();

        return asyncContent;
    }

    public static ChatResponse<ResultDeleteMessage> prepareDeleteMessageResponseForFind(MessageVO msg, String uniqueId , long threadId) {

        ChatResponse<ResultDeleteMessage> chatResponse = new ChatResponse<>();
        chatResponse.setUniqueId(uniqueId);
        ResultDeleteMessage resultDeleteMessage = new ResultDeleteMessage();
        DeleteMessageContent deleteMessage = new DeleteMessageContent();
        deleteMessage.setId(msg.getId());
        resultDeleteMessage.setDeletedMessage(deleteMessage);
        chatResponse.setResult(resultDeleteMessage);
        chatResponse.setSubjectId(threadId);

        return chatResponse;

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
