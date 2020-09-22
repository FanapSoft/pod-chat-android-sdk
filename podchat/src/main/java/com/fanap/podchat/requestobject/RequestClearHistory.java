package com.fanap.podchat.requestobject;

public class RequestClearHistory {

    private long threadId;

    private RequestClearHistory (Builder builder){
        this.threadId = builder.threadId;
    }

    public static class Builder{
        private long threadId;

        public Builder (long threadId){
            this.threadId = threadId;
        }

        public RequestClearHistory build(){
            return new RequestClearHistory(this);
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }


}
