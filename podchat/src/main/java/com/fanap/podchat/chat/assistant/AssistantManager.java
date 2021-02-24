package com.fanap.podchat.chat.assistant;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.assistant.model.AssistantHistoryVo;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.assistant.request_model.DeActiveAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantHistoryRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantRequest;
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


        message.setType(ChatMessageType.Constants.DEACTICVE_ASSISTANT);
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
        content.addProperty("contactType", request.getTypeCode());
        if ((Long)request.getOffset() != null) {
            content.addProperty("offset", request.getOffset());
        }
        if ((Long)request.getCount() != null) {
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

    public static ChatResponse<List<AssistantVo>> handleAssitantResponse(ChatMessage chatMessage) {

        ChatResponse<List<AssistantVo>> response = new ChatResponse<>();
        List<AssistantVo> result = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<AssistantVo>>() {
        }.getType());

        response.setResult(result);

        response.setUniqueId(chatMessage.getUniqueId());

        response.setCache(false);

        return response;
    }


    public static ChatResponse<List<AssistantHistoryVo>> handleAssitantHistoryResponse(ChatMessage chatMessage) {

        ChatResponse<List<AssistantHistoryVo>> response = new ChatResponse<>();
        List<AssistantHistoryVo> result = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<AssistantHistoryVo>>() {
        }.getType());

        for (AssistantHistoryVo history : result) {
            switch (history.getActionType()) {
                case AsisstantActionType.REGISTER_ASSISTANT:
                    history.setActionName("REGISTER_ASSISTANT");
                    break;
                case AsisstantActionType.ACTIVATE_ASSISTANT:
                    history.setActionName("ACTIVATE_ASSISTANT");
                    break;
                case AsisstantActionType.DIACTIVE_ASSISTANT:
                    history.setActionName("DIACTIVE_ASSISTANT");
                    break;
                case AsisstantActionType.BLOCK_ASSISTANT:
                    history.setActionName("BLOCK_ASSISTANT");
                    break;
            }
        }
        response.setResult(result);

        response.setUniqueId(chatMessage.getUniqueId());

        response.setCache(false);

        return response;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface AsisstantActionType {
        int REGISTER_ASSISTANT = 1;
        int ACTIVATE_ASSISTANT = 2;
        int DIACTIVE_ASSISTANT = 3;
        int BLOCK_ASSISTANT = 4;
    }
}
