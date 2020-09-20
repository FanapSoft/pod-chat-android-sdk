package com.fanap.podchat.chat.thread.public_thread;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class IsPublicThreadNameAvailableRequest extends GeneralRequestObject {

    private String uniqueName;

    private IsPublicThreadNameAvailableRequest(Builder builder) {
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
        public IsPublicThreadNameAvailableRequest build() {
            return new IsPublicThreadNameAvailableRequest(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

}
