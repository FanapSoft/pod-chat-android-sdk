package com.fanap.podchat.requestobject;


public class RequestReplyMessage extends GeneralRequestObject {
    private String messageContent;
    private long threadId;
    private long messageId;
    private String metaData;

     RequestReplyMessage(Builder builder) {
        super(builder);
        this.metaData = builder.metaData;
        this.messageContent = builder.messageContent;
        this.threadId = builder.threadId;
        this.messageId = builder.messageId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String messageContent;
        private long threadId;
        private long messageId;
        private String metaData;

        public Builder(String messageContent, long threadId, long messageId) {
            this.messageContent = messageContent;
            this.threadId = threadId;
            this.messageId = messageId;
        }

        public Builder metaData(String metaData) {
            this.metaData = metaData;
            return this;
        }

        public RequestReplyMessage build() {
            return new RequestReplyMessage(this);
        }

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

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }
}
