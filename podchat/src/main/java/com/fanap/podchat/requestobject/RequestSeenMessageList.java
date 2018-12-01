package com.fanap.podchat.requestobject;

public class RequestSeenMessageList extends BaseRequestObject {
    private long messageId;


     RequestSeenMessageList(Builder builder){
        super(builder);
        this.messageId = builder.messageId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private long messageId;

        public Builder(long messageId) {
            this.messageId = messageId;
        }

        public RequestSeenMessageList build() {
            return new RequestSeenMessageList(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }



}
