package com.fanap.podchat.chat.thread.public_thread;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.Thread;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class PublicThread {


    public static String checkIfNameIsAvailable(IsPublicThreadNameAvailableRequest request, String uniqueId) {


        AsyncMessage message = new AsyncMessage();
        message.setContent(request.getUniqueName());
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.IS_NAME_AVAILABLE);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(!Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : CoreConfig.typeCode);

        return App.getGson().toJson(message);


    }


    public static ChatResponse<ResultIsNameAvailable> handleIsNameAvailableResponse(ChatMessage chatMessage) {

        String content = chatMessage.getContent();

        ResultIsNameAvailable result = App.getGson().fromJson(content,ResultIsNameAvailable.class);

        ChatResponse<ResultIsNameAvailable> response = new ChatResponse<>();

        response.setResult(result);

        response.setSubjectId(chatMessage.getSubjectId());

        response.setUniqueId(chatMessage.getUniqueId());

        return response;


    }


    public static String joinPublicThread(JoinPublicThreadRequest request, String uniqueId) {


        AsyncMessage message = new AsyncMessage();
        message.setContent(request.getUniqueName());
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.JOIN_THREAD);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(!Util.isNullOrEmpty(request.getTypeCode()) ? request.getTypeCode() : CoreConfig.typeCode);

        JsonObject tmp = (JsonObject) App.getGson().toJsonTree(message);

        tmp.remove("subjectId");

        return tmp.toString();

    }


    public static ChatResponse<ResultJoinPublicThread> handleJoinThread(ChatMessage chatMessage) {


        Thread thread = new Thread();

        try {
            thread = App.getGson().fromJson(chatMessage.getContent(), Thread.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        ResultJoinPublicThread result = new ResultJoinPublicThread();

        result.setThread(thread);

        ChatResponse<ResultJoinPublicThread> response = new ChatResponse<>();

        response.setResult(result);

        return response;

    }


}
