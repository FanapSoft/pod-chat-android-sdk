package com.fanap.podchat.requestobject;

import com.fanap.chatcore.model.base.GeneralRequestObject;

public class RequestGetUserRoles extends GeneralRequestObject {

    long threadId;

    RequestGetUserRoles(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
    }


    public long getThreadId() {
        return threadId;
    }



    public static class Builder extends GeneralRequestObject.Builder{

       private long threadId;

        public Builder setThreadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        @Override
        public RequestGetUserRoles build() {
            return new RequestGetUserRoles(this) ;
        }

        @Override
        protected Builder self() {
            return this;
        }
    }


}
