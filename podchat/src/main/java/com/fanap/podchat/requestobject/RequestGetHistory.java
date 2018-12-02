package com.fanap.podchat.requestobject;

public class RequestGetHistory  extends BaseRequestObject{
    private long threadId;
    private String order;
    private long firstMessageId;
    private long lastMessageId;

    RequestGetHistory (Builder builder){
        super(builder);
        this.threadId=builder.threadId;
        this.order = builder.order;
        this.firstMessageId = builder.firstMessageId;
        this.lastMessageId = builder.lastMessageId;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder>{
        private long threadId;
        private String order;
        private long firstMessageId;
        private long lastMessageId;

        public Builder(long threadId){
            this.threadId = threadId;
        }

        public Builder firstMessageId(long firstMessageId){
            this.firstMessageId = firstMessageId;
            return this;
        }
        public Builder lastMessageId(long lastMessageId){
            this.lastMessageId = lastMessageId;
            return this;
        }
        public Builder order(String order){
            this.order = order;
            return this;
        }

        public RequestGetHistory build(){
            return new RequestGetHistory(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public long getFirstMessageId() {
        return firstMessageId;
    }

    public void setFirstMessageId(long firstMessageId) {
        this.firstMessageId = firstMessageId;
    }

    public long getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(long lastMessageId) {
        this.lastMessageId = lastMessageId;
    }
}
