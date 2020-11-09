package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class GetCallParticipantsRequest extends GeneralRequestObject {

    private long callId;

    public GetCallParticipantsRequest(Builder builder) {
        this.callId = builder.callId;
    }

    public long getCallId() {
        return callId;
    }

    public static class Builder extends GeneralRequestObject.Builder {

        private long callId;

        public Builder setCallId(long callId) {
            this.callId = callId;
            return this;
        }

        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        @Override
        public GetCallParticipantsRequest build() {
            return new GetCallParticipantsRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
