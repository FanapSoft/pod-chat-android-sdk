package com.fanap.podchat.chat.tag.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class DeleteTagRequest extends GeneralRequestObject {
    long tagId;

    DeleteTagRequest(DeleteTagRequest.Builder builder) {
        super(builder);
        this.tagId = builder.tagId;
    }

    public long getTagId() {
        return tagId;
    }

    public static class Builder extends GeneralRequestObject.Builder<DeleteTagRequest.Builder> {


        long tagId;

        public Builder(long tagId) {
            this.tagId = tagId;
        }

        public DeleteTagRequest build() {
            return new DeleteTagRequest(this);
        }

        @Override
        protected DeleteTagRequest.Builder self() {
            return this;
        }
    }
}
