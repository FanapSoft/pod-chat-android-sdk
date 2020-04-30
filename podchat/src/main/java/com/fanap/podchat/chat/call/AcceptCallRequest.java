package com.fanap.podchat.chat.call;

import com.fanap.podchat.mainmodel.Invitee;

import java.util.List;

public class AcceptCallRequest extends CallRequest {

    private long callId;
    private long creatorId;
    private long partnerId;

    AcceptCallRequest(Builder builder) {
        super(builder);
        this.callId = builder.callId;
        this.creatorId = builder.creatorId;
        this.partnerId = builder.partnerId;
    }

    public long getCallId() {
        return callId;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public static class Builder extends CallRequest.Builder {

        private long callId;
        private long creatorId;
        private long partnerId;

        public Builder(List<Invitee> invitees, int callType, long callId) {
            super(invitees, callType);
            this.callId = callId;
        }

        public Builder setCreatorId(long creatorId) {
            this.creatorId = creatorId;
            return this;
        }

        public Builder setPartnerId(long partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public AcceptCallRequest build() {

            return new AcceptCallRequest(this);
        }


    }
}
