package com.fanap.podchat.chat.requestobject;

import android.support.annotation.NonNull;

public class RequestDeleteMessage extends GeneralRequestObject {

    private long messageId;
    private boolean deleteForAll;

    private RequestDeleteMessage(@NonNull Builder builder) {
        super(builder);
        this.deleteForAll = builder.deleteForAll;
        this.messageId = builder.messageId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long messageId;
        private boolean deleteForAll;

        public Builder(long messageId) {
            this.messageId = messageId;
        }

        @NonNull
        public Builder deleteForAll(boolean deleteForAll) {
            this.deleteForAll = deleteForAll;
            return this;
        }

        @NonNull
        public RequestDeleteMessage build() {
            return new RequestDeleteMessage(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }

    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public boolean isDeleteForAll() {
        return deleteForAll;
    }

    public void setDeleteForAll(boolean deleteForAll) {
        this.deleteForAll = deleteForAll;
    }
}
