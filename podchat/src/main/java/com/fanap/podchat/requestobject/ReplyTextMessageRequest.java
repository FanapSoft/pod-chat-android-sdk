package com.fanap.podchat.requestobject;


import android.support.annotation.NonNull;

public class ReplyTextMessageRequest extends GeneralRequestObject {
    private String messageContent;
    private long threadId;
    private long messageId;
    private String systemMetaData;
    private int messageType;

    ReplyTextMessageRequest(@NonNull Builder builder) {
        super(builder);
        this.systemMetaData = builder.systemMetaData;
        this.messageContent = builder.messageContent;
        this.threadId = builder.threadId;
        this.messageId = builder.messageId;
        this.messageType = builder.messageType;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String messageContent;
        private long threadId;
        private long messageId;
        private String systemMetaData;
        private int messageType;

        public Builder(String messageContent,
                       long threadId,
                       long messageId,
                       int messageType) {
            this.messageContent = messageContent;
            this.threadId = threadId;
            this.messageId = messageId;
            this.messageType = messageType;
        }

        @NonNull
        public Builder messageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        @NonNull
        public Builder systemMetaData(String systemMetaData) {
            this.systemMetaData = systemMetaData;
            return this;
        }

        @NonNull
        public ReplyTextMessageRequest build() {
            return new ReplyTextMessageRequest(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getSystemMetaData() {
        return systemMetaData;
    }

    public void setSystemMetaData(String systemMetaData) {
        this.systemMetaData = systemMetaData;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

}
