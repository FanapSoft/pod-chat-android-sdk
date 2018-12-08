package com.fanap.podchat.requestobject;

public class RequestSeenMessage extends GeneralRequestObject {
    private long messageId;
    private long ownerId;

    RequestSeenMessage(Builder builder) {
        super(builder);
        this.messageId = builder.messageId;
        this.ownerId = builder.ownerId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long messageId;
        private long ownerId;

        public Builder messageId(long messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder offset(long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public RequestSeenMessage build() {
            return new RequestSeenMessage(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long count) {
        this.messageId = messageId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long offset) {
        this.ownerId = ownerId;
    }
}
