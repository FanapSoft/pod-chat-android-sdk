package com.fanap.podchat.chat.search;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;

public class SearchManager {

    public static String prepareSearchRequest(NosqlListMessageCriteriaVO messageCriteriaVO,String uniqueId ,String typecode , String token){

        String content = App.getGson().toJson(messageCriteriaVO);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(content);
            chatMessage.setType(ChatMessageType.Constants.GET_HISTORY);
            chatMessage.setToken(token);
            chatMessage.setTokenIssuer("1");
            chatMessage.setUniqueId(uniqueId);
            chatMessage.setSubjectId(messageCriteriaVO.getMessageThreadId());

            JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);

            if (Util.isNullOrEmpty(typecode)) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typecode);
            }

            String asyncContent = jsonObject.toString();
            return asyncContent;

    }
}
