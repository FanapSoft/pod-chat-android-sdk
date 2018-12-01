package com.fanap.podchat.requestobject;

import java.util.ArrayList;

public class RequestForwardMessage extends GeneralRequestObject{

    private long threadId;
    private ArrayList<Long> messageIds;

     RequestForwardMessage(Builder builder){
        super(builder);
        this.threadId = builder.threadId;
        this.messageIds = builder.messageIds;
    }


    public static class Builder extends GeneralRequestObject.Builder{
        private long threadId;
        private ArrayList<Long> messageIds;

        public Builder (long threadId,ArrayList<Long> messageIds){
            this.threadId = threadId;
            this.messageIds = messageIds;
        }

        public RequestForwardMessage build(){
            return new RequestForwardMessage(this);
        }

        @Override
        protected GeneralRequestObject.Builder self() {
            return this;
        }

    }
    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public ArrayList<Long> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(ArrayList<Long> messageIds) {
        this.messageIds = messageIds;
    }

}
