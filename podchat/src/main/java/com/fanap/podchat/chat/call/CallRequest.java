package com.fanap.podchat.chat.call;

import com.fanap.podchat.mainmodel.Invitee;

import java.util.List;

public class CallRequest {

    private List<Invitee> invitees;
    private int callType;

    public CallRequest(Builder builder) {

        this.invitees = builder.invitees;
        this.callType = builder.callType;

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


        public CallRequest build() {


            return new CallRequest(this);
        }
    }


}
