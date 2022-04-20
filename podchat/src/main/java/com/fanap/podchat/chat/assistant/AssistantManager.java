package com.fanap.podchat.chat.assistant;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.assistant.model.AssistantHistoryVo;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.assistant.request_model.BlockUnblockAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.DeActiveAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantHistoryRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetBlockedAssistantsRequest;
import com.fanap.podchat.chat.assistant.request_model.RegisterAssistantRequest;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.ChatMessageType;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class AssistantManager {

    public static String createRegisterAssistantRequest(RegisterAssistantRequest request,
                                                        String uniqueId) {
        String content = App.getGson().toJson(request.getAssistantVos());

        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.REGISTER_ASSISTANT);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }


    public static String createDeActiveAssistantRequest(DeActiveAssistantRequest request,

                                                        String uniqueId) {

        String content = App.getGson().toJson(request.getAssistantVos());

        AsyncMessage message = new ChatMessage();


        message.setType(ChatMessageType.Constants.DEACTIVE_ASSISTANT);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);
    }

    public static String createGetAssistantsRequest(GetAssistantRequest request,
                                                    String uniqueId) {
        JsonObject content = new JsonObject();
        content.addProperty("contactType", request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        if ((Long) request.getOffset() != null) {
            content.addProperty("offset", request.getOffset());
        }
        if ((Long) request.getCount() != null) {
            content.addProperty("count", request.getCount());
        } else {
            content.addProperty("count", 50);

        }
        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.GET_ASSISTANTS);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(App.getGson().toJson(content));
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);
    }

    public static String createUnBlockAssistantRequest(BlockUnblockAssistantRequest request,
                                                       String uniqueId) {
        String content = App.getGson().toJson(request.getAssistantVos());

        AsyncMessage message = new ChatMessage();


        message.setType(ChatMessageType.Constants.UNBLOCK_ASSISTANT);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);
    }

    public static String createBlockAssistantsRequest(BlockUnblockAssistantRequest request,
                                                      String uniqueId) {
        String content = App.getGson().toJson(request.getAssistantVos());

        AsyncMessage message = new ChatMessage();


        message.setType(ChatMessageType.Constants.BLOCK_ASSISTANT);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);
    }

    public static String createGetBlockedAssistantsRequest(GetBlockedAssistantsRequest request,
                                                           String uniqueId) {
        JsonObject content = new JsonObject();
        content.addProperty("contactType", request.getTypeCode());
        if ((Long) request.getOffset() != null) {
            content.addProperty("offset", request.getOffset());
        }
        if ((Long) request.getCount() != null) {
            content.addProperty("count", request.getCount());
        } else {
            content.addProperty("count", 50);

        }
        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.GET_BLOCKED_ASSISTANTS);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(App.getGson().toJson(content));
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);
    }

    public static String createGetAssistantHistoryRequest(GetAssistantHistoryRequest request,
                                                          String uniqueId) {
        JsonObject content = new JsonObject();

        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.GET_ASSISTANT_HISTORY);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(App.getGson().toJson(content));
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);
    }

    public static ChatResponse<List<AssistantVo>> handleAssistantResponse(ChatMessage chatMessage) {

        ChatResponse<List<AssistantVo>> response = new ChatResponse<>();
        List<AssistantVo> result = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<AssistantVo>>() {
        }.getType());

        response.setResult(result);

        response.setUniqueId(chatMessage.getUniqueId());

        response.setCache(false);

        return response;
    }


    public static ChatResponse<List<AssistantHistoryVo>> handleAssistantHistoryResponse(ChatMessage chatMessage) {

        ChatResponse<List<AssistantHistoryVo>> response = new ChatResponse<>();
        List<AssistantHistoryVo> result = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<AssistantHistoryVo>>() {
        }.getType());

        for (AssistantHistoryVo history : result) {
            switch (history.getActionType()) {
                case AssistantActionType.REGISTER_ASSISTANT:
                    history.setActionName("REGISTER_ASSISTANT");
                    break;
                case AssistantActionType.ACTIVATE_ASSISTANT:
                    history.setActionName("ACTIVATE_ASSISTANT");
                    break;
                case AssistantActionType.DEACTIVE_ASSISTANT:
                    history.setActionName("DEACTIVE_ASSISTANT");
                    break;
                case AssistantActionType.BLOCK_ASSISTANT:
                    history.setActionName("BLOCK_ASSISTANT");
                    break;
                case AssistantActionType.UNBLOCK_ASSISTANT:
                    history.setActionName("UNBLOCK_ASSISTANT");
                    break;
            }
        }
        response.setResult(result);

        response.setUniqueId(chatMessage.getUniqueId());

        response.setCache(false);

        return response;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface AssistantActionType {
        int REGISTER_ASSISTANT = 1;
        int ACTIVATE_ASSISTANT = 2;
        int DEACTIVE_ASSISTANT = 3;
        int BLOCK_ASSISTANT = 4;
        int UNBLOCK_ASSISTANT = 5;
    }
}
