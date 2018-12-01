package com.fanap.podchat.requestobject;

import java.util.List;

public class RequestRemoveParticipants extends GeneralRequestObject {
    private long threadId;
    private List<Long> participantIds;

    RequestRemoveParticipants(Builder builder) {
        super(builder);
        this.participantIds = builder.participantIds;
        this.threadId = builder.threadId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public List<Long> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<Long> participantIds) {
        this.participantIds = participantIds;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {

        private long threadId;
        private List<Long> participantIds;

        public Builder(long threadId, List<Long> participantIds) {
            this.participantIds = participantIds;
            this.threadId = threadId;
        }

        @Override
        protected Builder self() {
            return null;
        }

        public RequestRemoveParticipants build() {
            return new RequestRemoveParticipants(this);
        }
    }

}
