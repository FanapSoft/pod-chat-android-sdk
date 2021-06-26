package com.fanap.podchat.chat.tag.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

import java.util.List;

public class AddTagParticipantRequest extends GeneralRequestObject {

    long tagId;
    List<Long> threadIds;
    AddTagParticipantRequest(AddTagParticipantRequest.Builder builder) {
        super(builder);
        threadIds=builder.threadIds;
        tagId=builder.tagId;
    }

    public long getTagId() {
        return tagId;
    }

    public List<Long> getThreadIds() {
        return threadIds;
    }

    public static class Builder extends GeneralRequestObject.Builder<AddTagParticipantRequest.Builder> {

        long tagId;
        List<Long> threadIds;

        public Builder(long tagId,List<Long> threadIds) {
            this.tagId = tagId;
            this.threadIds=threadIds;
        }

        public AddTagParticipantRequest build() {
            return new AddTagParticipantRequest(this);
        }

        @Override
        protected AddTagParticipantRequest.Builder self() {
            return this;
        }
    }
}
