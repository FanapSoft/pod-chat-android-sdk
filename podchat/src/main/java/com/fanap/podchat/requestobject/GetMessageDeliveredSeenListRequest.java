package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class GetMessageDeliveredSeenListRequest extends BaseRequestObject {
    private long messageId;


     GetMessageDeliveredSeenListRequest(@NonNull Builder builder){
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
        public GetMessageDeliveredSeenListRequest build() {
            return new GetMessageDeliveredSeenListRequest(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }

}
