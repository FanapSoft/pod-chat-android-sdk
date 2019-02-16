package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RequestUnBlock extends GeneralRequestObject {
    private long blockId;
    private long userId;
    private long threadId;
    private long contactId;

    RequestUnBlock(@NonNull Builder builder) {
        super(builder);
        this.blockId = builder.blockId;
    }

    public long getBlockId() {
        return blockId;
    }

    public void setBlockId(long blockId) {
        this.blockId = blockId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private long blockId;

        public Builder(long blockId) {
            this.blockId = blockId;
        }

        @NonNull
        public RequestUnBlock build() {
            return new RequestUnBlock(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }
}
