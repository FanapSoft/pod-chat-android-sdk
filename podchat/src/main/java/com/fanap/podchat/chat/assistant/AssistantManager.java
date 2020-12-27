package com.fanap.podchat.chat.assistant;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.assistant.model.AssistantVo;
import com.fanap.podchat.chat.assistant.request_model.DeActiveAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.GetAssistantRequest;
import com.fanap.podchat.chat.assistant.request_model.RegisterAssistantRequest;
import com.fanap.podchat.chat.bot.request_model.CreateBotRequest;
import com.fanap.podchat.chat.user.profile.ResultUpdateProfile;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.Participant;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.PodChatException;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

        String content =App.getGson().toJson(request.getAssistantVos());

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

        AssistantVo assistantVo = new AssistantVo();
        assistantVo.setContactType(request.getTypeCode());

        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.GET_ASSISTANTS);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(App.getGson().toJson(assistantVo));
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);
    }

        public static ChatResponse<List<AssistantVo>> handleAssitantResponse(ChatMessage chatMessage){

            ChatResponse<List<AssistantVo>> response = new ChatResponse<>();
            List<AssistantVo> result = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<AssistantVo>>() {
            }.getType());

            response.setResult(result);

            response.setUniqueId(chatMessage.getUniqueId());

            response.setCache(false);

            return response;
    }

}
