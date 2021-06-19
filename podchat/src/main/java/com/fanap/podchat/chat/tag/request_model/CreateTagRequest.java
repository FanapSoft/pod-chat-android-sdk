package com.fanap.podchat.chat.tag.request_model;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class CreateTagRequest extends GeneralRequestObject {
    String name;

    CreateTagRequest(CreateTagRequest.Builder builder) {
        super(builder);
        this.name = builder.name;
    }

    public String getName() {
        return name;
    }

    public static class Builder extends GeneralRequestObject.Builder<CreateTagRequest.Builder> {

        String name;

        public Builder(String name) {
            this.name = name;
        }

        public CreateTagRequest build() {
            return new CreateTagRequest(this);
        }

        @Override
        protected CreateTagRequest.Builder self() {
            return this;
        }
    }
}
