package com.fanap.podchat.requestobject;

public class RequestDeliveredMessageList extends BaseRequestObject {
    private long messageId;


    public RequestDeliveredMessageList(Builder builder){
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

        public RequestDeliveredMessageList build() {
            return new RequestDeliveredMessageList(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
