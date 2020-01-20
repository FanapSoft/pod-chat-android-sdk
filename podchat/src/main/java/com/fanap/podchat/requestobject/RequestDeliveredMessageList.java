package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

import com.fanap.chatcore.model.base.BaseRequestObject;

public class RequestDeliveredMessageList extends BaseRequestObject {
    private long messageId;


     RequestDeliveredMessageList(@NonNull Builder builder){
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
        public RequestDeliveredMessageList build() {
            return new RequestDeliveredMessageList(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }

}
