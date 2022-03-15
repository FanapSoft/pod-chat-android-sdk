package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;

public class RequestSeenMessageList extends BaseRequestObject {
    private long messageId;


     RequestSeenMessageList(@NonNull Builder builder){
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

        @NonNull
        public RequestSeenMessageList build() {
            return new RequestSeenMessageList(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }



}
