package com.fanap.podchat.requestobject;

public class ClearHistoryRequest {

    private long threadId;

    private ClearHistoryRequest(Builder builder){
        this.threadId = builder.threadId;
    }

    public static class Builder{
        private long threadId;

        public Builder (long threadId){
            this.threadId = threadId;
        }

        public ClearHistoryRequest build(){
            return new ClearHistoryRequest(this);
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }


}
