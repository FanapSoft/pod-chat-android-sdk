package com.fanap.podchat.chat.call.request_model;

import com.fanap.podchat.mainmodel.Invitee;

import java.util.List;

public class CallRequest {

    private List<Invitee> invitees;
    private int callType;

    CallRequest(Builder builder) {

        this.invitees = builder.invitees;
        this.callType = builder.callType;

    }

    public void setInvitees(List<Invitee> invitees) {
        this.invitees = invitees;
    }

    public List<Invitee> getInvitees() {
        return invitees;
    }

    public int getCallType() {
        return callType;
    }

    public static class Builder {

        private List<Invitee> invitees;
        private int callType;

        public Builder(List<Invitee> invitees, int callType) {
            this.invitees = invitees;
            this.callType = callType;
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
