package com.fanap.podchat.requestobject;

public class RequestLocationMessage extends BaseRequestMapStImage {

    private long threadId;
    private String message;
    private String center;


    public RequestLocationMessage(Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
        this.message = builder.message;
    }

    public static class Builder extends BaseRequestMapStImage.Builder<Builder> {
        private long threadId;
        private String message;

        public Builder threadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public RequestLocationMessage build() {
            return new RequestLocationMessage(this);
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
