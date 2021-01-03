package com.fanap.podchat.chat.assistant;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.assistant.request_model.DeActiveAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.RegisterAssistantRequest;
import com.fanap.podchat.chat.bot.request_model.CreateBotRequest;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.PodChatException;
import com.google.gson.JsonObject;

public class AssistantManager {

    public static String createRegisterAssistantRequest(RegisterAssistantRequest request,
                                                String uniqueId) {
        String content =App.getGson().toJson(request.getAssistantVos());

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
        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.REACTICVE_ASSISTANT);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(App.getGson().toJson(request));
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);
    }

    public static String createGetAssistantsRequest(GetAssistantRequest request,
                                                String uniqueId) {
        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.GET_ASSISTANTS);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(App.getGson().toJson(request));
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);
    }

}
