package com.fanap.podchat.requestobject;

public class GetCurrentUserRolesRequest extends GeneralRequestObject {

    long threadId;

    GetCurrentUserRolesRequest(Builder builder) {
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
        public GetCurrentUserRolesRequest build() {
            return new GetCurrentUserRolesRequest(this) ;
        }

        @Override
        protected Builder self() {
            return this;
        }
    }


}
