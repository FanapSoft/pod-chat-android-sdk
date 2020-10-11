package com.fanap.podchat.call.request_model;

import com.fanap.podchat.mainmodel.Invitee;

import java.util.List;

public class CallRequest {

    private List<Invitee> invitees;
    private int callType;
    private long subjectId;

    CallRequest(Builder builder) {

        this.invitees = builder.invitees;
        this.callType = builder.callType;
        this.subjectId = builder.subjectId;

    }

    public List<Invitee> getInvitees() {
        return invitees;
    }

    public int getCallType() {
        return callType;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public static class Builder {

        private List<Invitee> invitees;
        private int callType;
        private long subjectId;


        public Builder(List<Invitee> invitees, int callType) {
            this.invitees = invitees;
            this.callType = callType;
        }

        public Builder(long subjectId,int callType) {
            this.callType = callType;
            this.subjectId = subjectId;
        }

        public Builder setSubjectId(long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public Builder setInvitees(List<Invitee> invitees) {
            this.invitees = invitees;
            return this;
        }

        public Builder setCallType(int callType) {
            this.callType = callType;
            return this;
        }

        public CallRequest build() {
            return new CallRequest(this);
        }
    }


}
