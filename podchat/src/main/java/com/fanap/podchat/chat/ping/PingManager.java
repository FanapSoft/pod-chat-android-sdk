package com.fanap.podchat.chat.ping;


import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.ping.request.StatusPingRequest;
import com.fanap.podchat.chat.ping.result.StatusPingResult;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.PodChatException;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class PingManager {


    public static final int IN_CHAT_ID = 1;
    public static final int IN_THREAD_ID = 2;
    public static final int IN_CONTACTS_LIST_ID = 3;
    public static final int IMPOSSIBLE_STATE_ID = 0;
    public static final String LOCATION = "location";
    public static final String LOCATION_ID = "locationId";


    public static String createStatusPingRequest(StatusPingRequest request, String uniqueId) throws PodChatException {


        validateUpdateStatusRequest(request, uniqueId);

//        Map<String, Long> contentMap = new HashMap<>();

        JsonObject contentObj = new JsonObject();


        long locationId = request.isInChat() ? IN_CHAT_ID : request.isInThread() ? IN_THREAD_ID : request.isInContactsList() ? IN_CONTACTS_LIST_ID : IMPOSSIBLE_STATE_ID;

//        contentMap.put(LOCATION, locationId);
        contentObj.addProperty(LOCATION,locationId);

        if (request.isInThread())
            contentObj.addProperty(LOCATION_ID, request.getThreadId());


        AsyncMessage message = new AsyncMessage();
        message.setContent(contentObj.toString());
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.LOCATION_PING);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(!Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : CoreConfig.typeCode);


        JsonObject requestObj = (JsonObject) App.getGson().toJsonTree(message);

        requestObj.remove("subjectId");

        return requestObj.toString();

    }

    private static void validateUpdateStatusRequest(StatusPingRequest request, String uniqueId) throws PodChatException {


        int trueStates = 0;

        if (request.isInThread())
            trueStates++;
        if (request.isInChat())
            trueStates++;
        if (request.isInContactsList())
            trueStates++;


        if (trueStates > 1)
            throw new PodChatException("Are you in a black hole?", uniqueId, CoreConfig.token);

        if (trueStates == 0)
            throw new PodChatException("Where are you exactly?", uniqueId, CoreConfig.token);

        if (request.isInThread() && request.getThreadId() <= 0)
            throw new PodChatException("Thread id is required in thread location!", uniqueId, CoreConfig.token);


    }


    public static ChatResponse<StatusPingResult> handleOnPingStatusSent(ChatMessage chatMessage) {


        StatusPingResult result = new StatusPingResult();

        ChatResponse<StatusPingResult> response = new ChatResponse<>();

        response.setResult(result);
        response.setCache(false);
        response.setHasError(false);
        response.setUniqueId(chatMessage.getUniqueId());

        return response;

    }
}
