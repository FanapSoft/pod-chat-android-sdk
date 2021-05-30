package com.fanap.podchat.call.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

import java.util.ArrayList;

public class TurnCallParticipantVideoOffRequest extends GeneralRequestObject {

    private long callId;

    private ArrayList<Long> participantsIds;

    public TurnCallParticipantVideoOffRequest(Builder builder) {
        this.callId = builder.callId;
        this.participantsIds = builder.participantsIds;
    }

    public long getCallId() {
        return callId;
    }

    public ArrayList<Long> getParticipantsIds() {
        return participantsIds;
    }

    public static class Builder extends GeneralRequestObject.Builder<TurnCallParticipantVideoOffRequest.Builder>{

        private long callId;
        private ArrayList<Long> participantsIds;


        public Builder(long callId, ArrayList<Long> participantsIds) {
            this.callId = callId;
            this.participantsIds = participantsIds;
        }

        public Builder setCallId(long callId) {
            this.callId = callId;
            return this;
        }

        public Builder setParticipantsIds(ArrayList<Long> participantsIds) {
            this.participantsIds = participantsIds;
            return this;
        }

        @Override
        public TurnCallParticipantVideoOffRequest build() {
            return new TurnCallParticipantVideoOffRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }


}
