package com.fanap.podchat.chat.thread.public_thread;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class RequestCheckIsNameAvailable extends GeneralRequestObject {

    private String uniqueName;

    private RequestCheckIsNameAvailable(Builder builder) {
        super(builder);
        this.uniqueName = builder.uniqueName;
    }

    public String getUniqueName() {
        return uniqueName;
    }



    public static class Builder extends GeneralRequestObject.Builder{


        private String uniqueName;


        public Builder(String uniqueName) {
            this.uniqueName = uniqueName;
        }

        @Override
        public RequestCheckIsNameAvailable build() {
            return new RequestCheckIsNameAvailable(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
