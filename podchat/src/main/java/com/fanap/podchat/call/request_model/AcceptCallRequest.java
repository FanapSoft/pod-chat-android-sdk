package com.fanap.podchat.call.request_model;

import com.fanap.podchat.mainmodel.Invitee;

import java.util.List;

public class AcceptCallRequest extends CallRequest {

    private long callId;
    private long creatorId;
    private List<Invitee> invitees;


    AcceptCallRequest(Builder builder) {
        super(builder);
        this.callId = builder.callId;
        this.creatorId = builder.creatorId;
        this.invitees = builder.invitees;
    }

    @Override
    public List<Invitee> getInvitees() {
        return invitees;
    }

    public long getCallId() {
        return callId;
    }

    public long getCreatorId() {
        return creatorId;
    }


    public static class Builder extends CallRequest.Builder {

        private long callId;
        private long creatorId;
        private List<Invitee> invitees;


        public Builder(List<Invitee> invitees, int callType, long callId) {
            super(invitees, callType);
            this.callId = callId;
            this.invitees = invitees;
        }

        public Builder setCreatorId(long creatorId) {
            this.creatorId = creatorId;
            return this;
        }

        public Builder setCallId(long callId) {
            this.callId = callId;
            return this;
        }

        @Override
        public Builder setInvitees(List<Invitee> invitees) {
            super.setInvitees(invitees);
            return this;
        }

        @Override
        public Builder setCallType(int callType) {
            super.setCallType(callType);
            return this;
        }

        public AcceptCallRequest build() {

            return new AcceptCallRequest(this);
        }


    }
}
