package com.fanap.podchat.call.request_model;

import com.fanap.podchat.mainmodel.Invitee;

import java.util.List;

public class CallRequest {

    private List<Invitee> invitees;
    private int callType;
    private long subjectId;
    private String title;
    private String image;
    private String description;
    private String metadata;
    private String uniqueName;

    public List<Invitee> getInvitees() {
        return invitees;
    }

    public int getCallType() {
        return callType;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getMetadata() {
        return metadata;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public static final class Builder {
        private List<Invitee> invitees;
        private int callType;
        private long subjectId;
        private String title;
        private String image;
        private String description;
        private String metadata;
        private String uniqueName;

        public Builder() {
        }

        public Builder(List<Invitee> invitees, int callType) {
            this.invitees = invitees;
            this.callType = callType;
        }

        public Builder(long subjectId,int callType) {
            this.callType = callType;
            this.subjectId = subjectId;
        }

        public Builder setInvitees(List<Invitee> invitees) {
            this.invitees = invitees;
            return this;
        }

        public Builder setCallType(int callType) {
            this.callType = callType;
            return this;
        }

        public Builder setSubjectId(long subjectId) {
            this.subjectId = subjectId;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setImage(String image) {
            this.image = image;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setMetadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder setUniqueName(String uniqueName) {
            this.uniqueName = uniqueName;
            return this;
        }

        public CallRequest build() {
            CallRequest callRequest = new CallRequest();
            callRequest.invitees = this.invitees;
            callRequest.title = this.title;
            callRequest.subjectId = this.subjectId;
            callRequest.image = this.image;
            callRequest.description = this.description;
            callRequest.metadata = this.metadata;
            callRequest.callType = this.callType;
            callRequest.uniqueName = this.uniqueName;
            return callRequest;
        }
    }
}
