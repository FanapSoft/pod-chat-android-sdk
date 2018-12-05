package com.fanap.podchat.requestobject;

public class RequestBlock {
    private long contactId;

    RequestBlock(Builder builder) {
        this.contactId = builder.contactId;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
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
