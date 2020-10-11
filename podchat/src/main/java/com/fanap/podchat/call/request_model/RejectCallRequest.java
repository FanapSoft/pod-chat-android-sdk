package com.fanap.podchat.call.request_model;

import com.fanap.podchat.mainmodel.Invitee;

import java.util.List;

public class RejectCallRequest extends AcceptCallRequest {

    private RejectCallRequest(Builder builder) {
        super(builder);
    }


    public static class Builder extends AcceptCallRequest.Builder {


        public Builder(List<Invitee> invitees, int callType, long callId) {
            super(invitees, callType, callId);
        }

        @Override
        public Builder setCreatorId(long creatorId) {
            super.setCreatorId(creatorId);
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

        public RejectCallRequest build() {

            return new RejectCallRequest(this);
        }
    }


}
