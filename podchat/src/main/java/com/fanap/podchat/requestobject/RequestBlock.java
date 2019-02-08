package com.fanap.podchat.requestobject;

public class RequestBlock {
    private long contactId;
    private long userId;
    private long threadId;

    RequestBlock(Builder builder) {
        this.contactId = builder.contactId;
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

    public static class Builder {
        private long contactId;

        public Builder(long contactId) {
            this.contactId = contactId;
        }

        public RequestBlock build() {
            return new RequestBlock(this);
        }
    }
}
