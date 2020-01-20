package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

import com.fanap.chatcore.model.base.GeneralRequestObject;

public class RequestThreadInfo  extends GeneralRequestObject {
    private String image;
    private long threadId;
    private String name;
    private String description;
    private String metadata;

    RequestThreadInfo (@NonNull Builder builder){
        super(builder);
        this.description = builder.description;
        this.image = builder.image;
        this.metadata = builder.metadata;
        this.name = builder.name;
        this.threadId = builder.threadId;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String image;
        private String name;
        private String description;
        private String metadata;
        private long threadId;

        @NonNull
        public Builder image(String image) {
            this.image = image;
            return this;
        }

        @NonNull
        public Builder threadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        @NonNull
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        @NonNull
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        @NonNull
        public Builder metadat(String metadata) {
            this.metadata = metadata;
            return this;
        }

        @NonNull
        public RequestThreadInfo build(){
            return new RequestThreadInfo(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

}
