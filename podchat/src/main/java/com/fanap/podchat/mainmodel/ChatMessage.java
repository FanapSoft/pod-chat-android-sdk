package com.fanap.podchat.mainmodel;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.util.ChatMessageType;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;

public class ChatMessage extends AsyncMessage {

    private int messageType;
    private long time;
    private int contentCount;
    private String systemMetadata;
    private String metadata;
    private long repliedTo;


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(long repliedTo) {
        this.repliedTo = repliedTo;
    }

    public int getContentCount() {
        return contentCount;
    }

    public void setContentCount(int contentCount) {
        this.contentCount = contentCount;
    }

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }


    public ChatMessage() {
    }

    public ChatMessage(String uniqueId, int type, String content, String token) {
        setType(type);
        setTokenIssuer("1");
        setToken(token);
        setUniqueId(uniqueId);
        setContent(content);
    }

    public JsonObject getJson(String requestParamstypecode, String typecode) {
        JsonObject jsonObject = (JsonObject) App.getGson().toJsonTree(this);

        if (Util.isNullOrEmpty(requestParamstypecode)) {
            if (Util.isNullOrEmpty(typecode)) {
                jsonObject.remove("typeCode");
            } else {
                jsonObject.remove("typeCode");
                jsonObject.addProperty("typeCode", typecode);
            }
        } else {
            jsonObject.remove("typeCode");
            jsonObject.addProperty("typeCode", requestParamstypecode);
        }


        return jsonObject;
    }


}
