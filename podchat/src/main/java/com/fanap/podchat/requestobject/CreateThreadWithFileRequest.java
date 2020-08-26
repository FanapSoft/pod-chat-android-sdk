package com.fanap.podchat.requestobject;


import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.mainmodel.RequestThreadInnerMessage;

import java.util.List;


public class CreateThreadWithFileRequest extends CreateThreadWithMessageRequest {


    private transient UploadFileRequest file;

    CreateThreadWithFileRequest(Builder builder) {
        super(builder);
        this.file = builder.file;

    }

    public UploadFileRequest getFile() {
        return file;
    }

    public void setFile(UploadFileRequest file) {
        this.file = file;
    }

    public CreateThreadWithFileRequest(CreateThreadWithMessageRequest.Builder<?> builder, UploadFileRequest file) {
        super(builder);
        this.file = file;
    }

    public CreateThreadWithFileRequest(BaseRequestObject.Builder<?> builder, UploadFileRequest file) {
        super(builder);
        this.file = file;
    }

    public CreateThreadWithFileRequest(UploadFileRequest file) {
        this.file = file;
    }


    public static class Builder extends CreateThreadWithMessageRequest.Builder<Builder> {
        private UploadFileRequest file;

        public Builder(int type, List<Invitee> invitees, UploadFileRequest file, int messageType) {
            super(type, invitees,messageType);
            this.file = file;
        }

        @Override
        public Builder message(RequestThreadInnerMessage message) {
            super.message(message);
            return this;
        }

        @Override
        public Builder setUploadThreadImageRequest(UploadImageRequest uploadImageRequest) {
            super.setUploadThreadImageRequest(uploadImageRequest);
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

        public CreateThreadWithFileRequest build() {
            return new CreateThreadWithFileRequest(this);
        }
    }
}

