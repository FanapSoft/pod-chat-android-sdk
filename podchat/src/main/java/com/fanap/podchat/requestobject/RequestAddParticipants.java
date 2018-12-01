package com.fanap.podchat.requestobject;

import java.util.List;

public class RequestAddParticipants extends GeneralRequestObject {
    private long threadId;
    private List<Long> contactIds;

    RequestAddParticipants(Builder builder) {
        super(builder);
        this.contactIds = builder.contactIds;
        this.threadId = builder.threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {

        private long threadId;
        private List<Long> contactIds;

        public Builder(long threadId, List<Long> contactIds) {
            this.contactIds = contactIds;
            this.threadId = threadId;
        }

        @Override
        protected Builder self() {
            return null;
        }

        public RequestAddParticipants build() {
            return new RequestAddParticipants(this);
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public List<Long> getContactIds() {
        return contactIds;
    }

    public void setContactIds(List<Long> contactIds) {
        this.contactIds = contactIds;
    }


}
