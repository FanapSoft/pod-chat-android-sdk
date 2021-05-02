package com.fanap.podchat.chat.tag;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.bot.result_model.DefineBotCommandResult;
import com.fanap.podchat.chat.tag.request_model.AddTagParticipantRequest;
import com.fanap.podchat.chat.tag.request_model.CreateTagRequest;
import com.fanap.podchat.chat.tag.request_model.DeleteTagRequest;
import com.fanap.podchat.chat.tag.request_model.EditTagRequest;
import com.fanap.podchat.chat.tag.request_model.RemoveTagParticipantRequest;
import com.fanap.podchat.chat.tag.result_model.TagResult;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.mainmodel.MessageVO;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.ResultHistory;
import com.fanap.podchat.util.ChatMessageType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class TagManager {

    public static String createAddTagRequest(CreateTagRequest request,
                                             String uniqueId) {

        JsonObject contentObject = new JsonObject();
        if(request.getName()!=null)
            contentObject.addProperty("name",request.getName());

        String content = App.getGson().toJson(contentObject);
        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.CREATE_TAG);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }

    public static String createEditTagRequest(EditTagRequest request,
                                              String uniqueId) {
        JsonObject contentObject = new JsonObject();
        if(request.getName()!=null)
            contentObject.addProperty("name",request.getName());

        String content = App.getGson().toJson(contentObject);

        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.EDIT_TAG);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setSubjectId(request.getTagId());
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }

    public static String createDeleteTagRequest(DeleteTagRequest request,
                                                String uniqueId) {

        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.DELETE_TAG);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setSubjectId(request.getTagId());
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }

    public static String createAddTagParticipantRequest(AddTagParticipantRequest request,
                                                        String uniqueId) {
        JsonArray participants = new JsonArray();
        for (Long p : request.getThreadIds()) {
            participants.add(p);
        }
        String content = App.getGson().toJson(participants);
        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.ADD_TAG_PARTICIPANT);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setSubjectId(request.getTagId());
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }

    public static String createRemoveTagParticipantRequest(RemoveTagParticipantRequest request,
                                                           String uniqueId) {
        JsonArray participants = new JsonArray();
        for (Long p : request.getThreadIds()) {
            participants.add(p);
        }
        String content = App.getGson().toJson(participants);
        AsyncMessage message = new ChatMessage();

        message.setType(ChatMessageType.Constants.REMOVE_TAG_PARTICIPANT);
        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setSubjectId(request.getTagId());
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }


    public static ChatResponse<TagResult> prepareTagResponse(ChatMessage chatMessage) {

        TagResult tagResult = App.getGson().fromJson(chatMessage.getContent(), TagResult.class);
        ChatResponse<TagResult> finalResponse = new ChatResponse<>();
        finalResponse.setResult(tagResult);
        finalResponse.setUniqueId(chatMessage.getUniqueId());
        finalResponse.setSubjectId(chatMessage.getSubjectId());

        return finalResponse;
    }


}
