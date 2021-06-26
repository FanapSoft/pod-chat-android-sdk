package com.fanap.podchat.chat.tag;

import com.fanap.podchat.cachemodel.CacheTagVo;
import com.fanap.podchat.chat.App;
import com.fanap.podchat.chat.CoreConfig;
import com.fanap.podchat.chat.tag.request_model.AddTagParticipantRequest;
import com.fanap.podchat.chat.tag.request_model.CreateTagRequest;
import com.fanap.podchat.chat.tag.request_model.DeleteTagRequest;
import com.fanap.podchat.chat.tag.request_model.EditTagRequest;
import com.fanap.podchat.chat.tag.request_model.GetTagListRequest;
import com.fanap.podchat.chat.tag.request_model.RemoveTagParticipantRequest;
import com.fanap.podchat.chat.tag.result_model.TagListResult;
import com.fanap.podchat.chat.tag.result_model.TagParticipantResult;
import com.fanap.podchat.chat.tag.result_model.TagResult;
import com.fanap.podchat.mainmodel.AsyncMessage;
import com.fanap.podchat.mainmodel.ChatMessage;
import com.fanap.podchat.model.ChatResponse;
import com.fanap.podchat.model.TagParticipantVO;
import com.fanap.podchat.model.TagVo;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class TagManager {

    public static String createAddTagRequest(CreateTagRequest request,
                                             String uniqueId) {

        JsonObject contentObject = new JsonObject();
        if(request.getName()!=null)
            contentObject.addProperty("name",request.getName());

        String content = App.getGson().toJson(contentObject);
        AsyncMessage message = new ChatMessage();

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

        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setSubjectId(request.getTagId());
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }

    public static String createTagListRequest(GetTagListRequest request,
                                              String uniqueId) {

        AsyncMessage message = new ChatMessage();

        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }

    public static String createRemoveTagParticipantRequest(RemoveTagParticipantRequest request,
                                                           String uniqueId) {
        JsonArray participants = new JsonArray();
        for (Long p : request.getThreadIds()) {
            participants.add(p);
        }
        String content =
                "[{\"threadId\": 8688 }," +
                        "{\"threadId\": 8730 }," +
                        "{\"threadId\": 8729 }]";
//        String content = App.getGson().toJson(participants);
        AsyncMessage message = new ChatMessage();

        message.setToken(CoreConfig.token);
        message.setTokenIssuer(CoreConfig.tokenIssuer);
        message.setTypeCode(request.getTypeCode() != null ? request.getTypeCode() : CoreConfig.typeCode);
        message.setContent(content);
        message.setSubjectId(request.getTagId());
        message.setUniqueId(uniqueId);

        return App.getGson().toJson(message);

    }


    public static ChatResponse<TagResult> prepareTagResponse(ChatMessage chatMessage) {
        TagVo tag = App.getGson().fromJson(chatMessage.getContent(), TagVo.class);
        TagResult tagResult = new TagResult(tag);
        ChatResponse<TagResult> finalResponse = new ChatResponse<>();
        finalResponse.setResult(tagResult);
        finalResponse.setUniqueId(chatMessage.getUniqueId());
        finalResponse.setSubjectId(chatMessage.getSubjectId());

        return finalResponse;
    }

    public static ChatResponse<TagListResult> prepareTagListResponse(ChatMessage chatMessage) {
        TagListResult tagsResult = new TagListResult();
        tagsResult.setTags(getTags(chatMessage));
        ChatResponse<TagListResult> finalResponse = new ChatResponse<>();
        finalResponse.setResult(tagsResult);
        finalResponse.setUniqueId(chatMessage.getUniqueId());
        finalResponse.setSubjectId(chatMessage.getSubjectId());

        return finalResponse;
    }

    private static List<TagVo> getTags(ChatMessage chatMessage) {
        List<TagVo> tags = App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<TagVo>>() {
        }.getType());

        for (TagVo tag : tags) {
            if (!Util.isNullOrEmpty(tag.getTagParticipants())) {
                long unreadCount = 0;
                for (TagParticipantVO tagParticipant : tag.getTagParticipants())
                    if (tagParticipant.getConversationVO() != null)
                        unreadCount = unreadCount + tagParticipant.getConversationVO().getUnreadCount();

                tag.setAllUnreadCount(unreadCount);
            }
        }
        return tags;
    }

    public static ChatResponse<TagParticipantResult> prepareTagParticipantResponse(ChatMessage chatMessage) {

        TagParticipantResult tagParticipantResult = new TagParticipantResult();
        tagParticipantResult.setTagParticipans(App.getGson().fromJson(chatMessage.getContent(), new TypeToken<ArrayList<TagParticipantVO>>() {
        }.getType()));
        ChatResponse<TagParticipantResult> finalResponse = new ChatResponse<>();
        finalResponse.setResult(tagParticipantResult);
        finalResponse.setUniqueId(chatMessage.getUniqueId());
        finalResponse.setSubjectId(chatMessage.getSubjectId());

        return finalResponse;
    }

    private List<TagVo> createTagResponse(List<CacheTagVo> data) {
        return  null;
       // return new ThreadManager.ThreadResponse(data, threadListContentCount, MEMORY);
    }
}
