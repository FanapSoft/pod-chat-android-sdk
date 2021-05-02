package com.fanap.podchat.chat.tag.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class EditTagRequest extends GeneralRequestObject {
    String name;
    long tagId;
    EditTagRequest(EditTagRequest.Builder builder) {
        super(builder);
        name=builder.name;
        tagId=builder.tagId;
    }

    public String getName() {
        return name;
    }

    public long getTagId() {
        return tagId;
    }

    public static class Builder extends GeneralRequestObject.Builder<EditTagRequest.Builder> {
        String name;
        long tagId;

        public Builder(long tagId, String name) {
            this.name = name;
            this.tagId = tagId;
        }

        public EditTagRequest build() {
            return new EditTagRequest(this);
        }

        @Override
        protected EditTagRequest.Builder self() {
            return this;
        }
    }
}
