package com.fanap.podchat.requestobject;


import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;

import java.io.Serializable;
import java.util.List;


public class RequestCreateThreadWithFile extends RequestCreateThreadWithMessage{

    private RequestUploadFile file;

    RequestCreateThreadWithFile(Builder builder) {
        super(builder);
        this.file = builder.file;

    }

    public RequestUploadFile getFile() {
        return file;
    }

    public void setFile(RequestUploadFile file) {
        this.file = file;
    }

    public RequestCreateThreadWithFile() {
    }

    public RequestCreateThreadWithFile(RequestCreateThreadWithMessage.Builder<?> builder, RequestUploadFile file) {
        super(builder);
        this.file = file;
    }

    public RequestCreateThreadWithFile(BaseRequestObject.Builder<?> builder, RequestUploadFile file) {
        super(builder);
        this.file = file;
    }

    public RequestCreateThreadWithFile(RequestUploadFile file) {
        this.file = file;
    }


    public static class Builder extends RequestCreateThreadWithMessage.Builder<Builder> {
        private RequestUploadFile file;

        public Builder(int type, List<Invitee> invitees, RequestUploadFile file) {
            super(type, invitees);
            this.file = file;
        }

        public Builder(int type, List<Invitee> invitees) {
            super(type, invitees);
        }

        @Override
        public Builder message(RequestThreadInnerMessage message) {
            super.message(message);
            return this;
        }

        @Override
        public Builder title(String title) {
            super.title(title);
            return this;
        }

        @Override
        public Builder description(String description) {
            super.description(description);
            return this;
        }

        @Override
        public Builder image(String image) {
            super.image(image);
            return this;
        }

        @Override
        protected Builder self() {
            super.self();
            return this;
        }

        public RequestCreateThreadWithFile build() {
            return new RequestCreateThreadWithFile(this);
        }
    }
}

