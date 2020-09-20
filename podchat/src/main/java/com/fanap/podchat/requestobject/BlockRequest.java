package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class BlockRequest {
    private long contactId;
    private long userId;
    private long threadId;

    BlockRequest(Builder builder) {
        this.contactId = builder.contactId;
        this.userId = builder.userId;
        this.threadId = builder.threadId;
    }


    public static class Builder {
        private long contactId;
        private long userId;
        private long threadId;

        public Builder contactId(long contactId) {
            this.contactId = contactId;
            return this;
        }

        public Builder userId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder threadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        @NonNull
        public BlockRequest build() {
            return new BlockRequest(this);
        }
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
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
}
