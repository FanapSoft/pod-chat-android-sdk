package com.fanap.chatcore.model.wrappers;

public class ChatMessage extends AsyncMessage {

    private Integer messageType;
    private Long time;
    private Integer contentCount;
    private String systemMetadata;
    private String metadata;
    private Long repliedTo;


    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Long getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(Long repliedTo) {
        this.repliedTo = repliedTo;
    }

    public Integer getContentCount() {
        return contentCount;
    }

    public void setContentCount(Integer contentCount) {
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
}
