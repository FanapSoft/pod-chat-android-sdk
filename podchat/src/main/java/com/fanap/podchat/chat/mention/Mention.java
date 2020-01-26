package com.fanap.podchat.chat.mention;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.mention.model.RequestGetMentionList;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.util.ChatMessageType;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Mention {


    public static String getMentionList(RequestGetMentionList request, String uniqueId) {

        long threadId = request.getThreadId();

        JsonObject criteriaVO = new JsonObject();

        if (request.getAllMentioned() != null && request.getAllMentioned())
            criteriaVO.addProperty("allMentioned", true);
        if (request.getUnreadMentioned() != null && request.getUnreadMentioned())
            criteriaVO.addProperty("unreadMentioned", true);

        AsyncMessage message = new AsyncMessage();
        message.setContent(criteriaVO.toString());
        message.setType(ChatMessageType.Constants.GET_HISTORY);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setUniqueId(uniqueId);
        message.setSubjectId(threadId);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);

        return App.getGson().toJson(message);

    }



    public static ChatResponse<ResultHistory> getMentionListResponse(ChatMessage chatMessage) {

        List<MessageVO> messageVOS = getMessageVOSFromChatMessage(chatMessage);

        ResultHistory resultHistory = new ResultHistory();
        resultHistory.setContentCount(chatMessage.getContentCount());
        resultHistory.setHistory(messageVOS);


        ChatResponse<ResultHistory> finalResponse = new ChatResponse<>();
        finalResponse.setResult(resultHistory);
        finalResponse.setUniqueId(chatMessage.getUniqueId());
        finalResponse.setSubjectId(chatMessage.getSubjectId());

        return finalResponse;
    }


    public static List<MessageVO> getMessageVOSFromChatMessage(ChatMessage chatMessage) {
        return App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<MessageVO>>() {
        }.getType());
    }




}
