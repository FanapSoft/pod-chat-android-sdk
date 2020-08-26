package com.fanap.podchat.chat.user.profile;

import android.support.annotation.NonNull;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class UpdateProfileRequest extends GeneralRequestObject {

    private String bio;
    private String metadata;


    public String getBio() {
        return bio;
    }

    public String getMetadata() {
        return metadata;
    }

    public UpdateProfileRequest(Builder builder) {
        super(builder);
        this.bio = builder.bio;
        this.metadata = builder.metadata;
    }


    public static class Builder extends GeneralRequestObject.Builder<Builder> {


        private String bio;
        private String metadata;


        public Builder() {
        }

        public Builder(String bio) {
            this.bio = bio;
        }

        public Builder setMetadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        @NonNull
        public UpdateProfileRequest build() {

            return new UpdateProfileRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

    }
}
