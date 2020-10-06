package com.fanap.podchat.chat.ping.request;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class StatusPingRequest extends GeneralRequestObject {

    private boolean inChat;
    private boolean inThread;
    private boolean inContactsList;

    private long threadId;


    public StatusPingRequest(Builder builder) {
        this.inChat = builder.inChat;
        this.inThread = builder.inThread;
        this.inContactsList = builder.inContactsList;

        this.threadId  = builder.threadId;
    }


    public boolean isInChat() {
        return inChat;
    }

    public boolean isInThread() {
        return inThread;
    }

    public boolean isInContactsList() {
        return inContactsList;
    }

    public long getThreadId() {
        return threadId;
    }


    public static class Builder extends GeneralRequestObject.Builder
    {

        private boolean inChat;
        private boolean inThread;
        private boolean inContactsList;

        private long threadId;

        public Builder inChat() {
            this.inChat = true;
            return this;
        }

        public Builder inThread() {
            this.inThread = true;
            return this;
        }

        public Builder inContactsList() {
            this.inContactsList = true;
            return this;
        }

        public Builder setThreadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        @Override
        public StatusPingRequest build() {
            return new StatusPingRequest(this);
        }

        @Override
        protected GeneralRequestObject.Builder self() {
            return this;
        }
    }

}
