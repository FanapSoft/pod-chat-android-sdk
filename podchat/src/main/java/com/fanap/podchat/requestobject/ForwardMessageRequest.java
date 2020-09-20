package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class ForwardMessageRequest extends GeneralRequestObject{

    private long threadId;
    private ArrayList<Long> messageIds;

     ForwardMessageRequest(@NonNull Builder builder){
        super(builder);
        this.threadId = builder.threadId;
        this.messageIds = builder.messageIds;
    }


    public static class Builder extends GeneralRequestObject.Builder<Builder>{
        private long threadId;
        private ArrayList<Long> messageIds;

        public Builder (long threadId,ArrayList<Long> messageIds){
            this.threadId = threadId;
            this.messageIds = messageIds;
        }

        @NonNull
        public ForwardMessageRequest build(){
            return new ForwardMessageRequest(this);
        }

        @NonNull
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

    public ArrayList<Long> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(ArrayList<Long> messageIds) {
        this.messageIds = messageIds;
    }

}
