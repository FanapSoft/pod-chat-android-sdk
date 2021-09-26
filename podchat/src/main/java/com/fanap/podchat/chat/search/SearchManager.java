package com.fanap.podchat.chat.search;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.ListMessageCriteriaVO;
import com.fanap.podchat.mainmodel.NosqlListMessageCriteriaVO;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;

public class SearchManager {

    public static String prepareSearchRequest(NosqlListMessageCriteriaVO messageCriteriaVO, String uniqueId, String typecode, String token) {

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

    // Prepare request for search in all threads
    public static String prepareSearchInThreadsRequest(ListMessageCriteriaVO searchInThreadsVo, String uniqueId, String typecode, String token) {

        String content = App.getGson().toJson(searchInThreadsVo);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent(content);
        chatMessage.setType(ChatMessageType.Constants.SEARCH_IN_ALL_THREADS);
        chatMessage.setToken(token);
        chatMessage.setTokenIssuer("1");
        chatMessage.setUniqueId(uniqueId);

        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(chatMessage);
        jsonObject.remove("subjectId");
        jsonObject.remove("contentCount");
        jsonObject.remove("messageType");
        jsonObject.remove("repliedTo");
        jsonObject.remove("time");
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
