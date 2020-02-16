package com.fanap.podchat.chat.user.profile;

import android.support.annotation.NonNull;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class RequestUpdateProfile extends GeneralRequestObject {

    private String bio;


    public String getBio() {
        return bio;
    }

    public RequestUpdateProfile(Builder builder) {
        super(builder);
        this.bio = builder.bio;
    }


    public static class Builder extends GeneralRequestObject.Builder<Builder> {


        private String bio;

        public Builder(String bio) {
            this.bio = bio;
        }


        @NonNull
        public RequestUpdateProfile build() {

            return new RequestUpdateProfile(this);
        }

        @Override
        protected Builder self() {
            return this;
        }

    }
}
