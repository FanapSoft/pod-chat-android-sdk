package com.fanap.podchat.chat.call;

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
        public Builder setPartnerId(long partnerId) {
            super.setPartnerId(partnerId);
            return this;
        }

        public RejectCallRequest build() {

            return new RejectCallRequest(this);
        }
    }


}
