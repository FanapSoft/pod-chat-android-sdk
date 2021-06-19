package com.fanap.podchat.chat.tag.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

import java.util.List;

public class RemoveTagParticipantRequest extends GeneralRequestObject {

    long tagId;
    List<Long> threadIds;

    RemoveTagParticipantRequest(RemoveTagParticipantRequest.Builder builder) {
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

    public static class Builder extends GeneralRequestObject.Builder<RemoveTagParticipantRequest.Builder> {

        long tagId;
        List<Long> threadIds;

        public Builder(long tagId,List<Long> threadIds) {
            this.tagId = tagId;
            this.threadIds=threadIds;
        }

        public RemoveTagParticipantRequest build() {
            return new RemoveTagParticipantRequest(this);
        }

        @Override
        protected RemoveTagParticipantRequest.Builder self() {
            return this;
        }
    }
}
