package com.fanap.podchat.chat.user.profile;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.util.ChatMessageType;
import com.google.gson.JsonObject;

public class UserProfile {






    public static String setProfile(RequestUpdateProfile request,String uniqueId){

        String bio = request.getBio();
        String metadata = request.getMetadata();

        JsonObject content = new JsonObject();
        content.addProperty("bio",bio);
        content.addProperty("metadata",metadata);

        AsyncMessage message = new AsyncMessage();
        message.setContent(content.toString());
        message.setToken(CoreConfig.token);
        message.setType(ChatMessageType.Constants.UPDATE_CHAT_PROFILE);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);

        return App.getGson().toJson(message);
    }



    public static ChatResponse<ResultUpdateProfile> handleOutputUpdateProfile(ChatMessage chatMessage){

        ChatResponse<ResultUpdateProfile> response = new ChatResponse<>();

        ResultUpdateProfile result = App.getGson().fromJson(chatMessage.getContent(),ResultUpdateProfile.class);

        response.setResult(result);

        response.setUniqueId(chatMessage.getUniqueId());

        response.setCache(false);

        return response;


    }

}
