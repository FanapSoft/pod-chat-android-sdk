package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;

public class RequestMessage {
    private String textMessage;
    private int messageType;
    private String jsonMetaData;
    private long threadId;

    RequestMessage(Builder builder) {
        this.setTextMessage(builder.textMessage);
        this.setThreadId(builder.threadId);
        this.setMessageType(builder.messageType);
        this.setJsonMetaData(builder.jsonMetaData);
    }

    public static class Builder {
        private String textMessage;
        private int messageType;
        private String jsonMetaData;
        private long threadId;

        public Builder(String textMessage, long threadId) {
            this.textMessage = textMessage;
            this.threadId = threadId;
        }

        @NonNull
        public Builder messageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        @NonNull
        public Builder jsonMetaData(String jsonMetaData) {
            this.jsonMetaData = jsonMetaData;
            return this;
        }

        @NonNull
        public RequestMessage build() {
            return new RequestMessage(this);
        }

    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getJsonMetaData() {
        return jsonMetaData;
    }

    public void setJsonMetaData(String jsonMetaData) {
        this.jsonMetaData = jsonMetaData;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }


}
