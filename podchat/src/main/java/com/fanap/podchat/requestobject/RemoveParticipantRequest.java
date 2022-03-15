package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;

import com.fanap.podchat.mainmodel.Invitee;

import java.util.List;

public class RemoveParticipantRequest extends GeneralRequestObject {
    private long threadId;
    private List<Long> participantIds;
    private List<Invitee> invitees;
    RemoveParticipantRequest(@NonNull Builder builder) {
        super(builder);
        this.participantIds = builder.participantIds;
        this.threadId = builder.threadId;
        this.invitees = builder.invitees;
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

    public List<Invitee> getInvitees() {
        return invitees;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {

        private long threadId;
        private List<Long> participantIds;
        private List<Invitee> invitees;
        public Builder(long threadId, List<Long> participantIds) {
            this.participantIds = participantIds;
            this.threadId = threadId;
        }

        public Builder setInvitees(List<Invitee> invitees) {
            this.invitees = invitees;
            return this;
        }


        @NonNull
        @Override
        protected Builder self() {
            return this;
        }

        @NonNull
        public RemoveParticipantRequest build() {
            return new RemoveParticipantRequest(this);
        }
    }

}
