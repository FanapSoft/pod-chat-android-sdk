package com.fanap.podchat.chat.messge;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;

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
}
