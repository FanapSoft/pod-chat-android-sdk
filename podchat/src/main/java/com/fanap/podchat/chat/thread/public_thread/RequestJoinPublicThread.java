package com.fanap.podchat.chat.thread.public_thread;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class RequestJoinPublicThread extends GeneralRequestObject {


    private String uniqueName;

    public RequestJoinPublicThread(Builder builder) {
        super(builder);
        this.uniqueName = builder.uniqueName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {

        private String uniqueName;


        public Builder(String uniqueName) {
            this.uniqueName = uniqueName;
        }

        public Builder setUniqueName(String uniqueName) {
            this.uniqueName = uniqueName;
            return this;
        }

        @Override
        public RequestJoinPublicThread build() {
            return new RequestJoinPublicThread(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }
}
