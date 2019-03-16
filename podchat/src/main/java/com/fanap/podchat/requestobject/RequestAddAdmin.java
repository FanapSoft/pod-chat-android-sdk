package com.fanap.podchat.requestobject;

import java.util.ArrayList;

public class RequestAddAdmin {

    private long threadId;
    private long coreUserId;
    private ArrayList<String> roleTypes;

    private RequestAddAdmin(Builder builder){
        this.threadId = builder.threadId;
        this.coreUserId = builder.coreUserId;
        this.roleTypes = builder.roleTypes;
    }

    public static class Builder {
        private long threadId;
        private long coreUserId;
        private ArrayList<String> roleTypes;

        public Builder(long threadId, long coreUserId, ArrayList<String> roleTypes){
           this.threadId = threadId;
           this.coreUserId = coreUserId;
           this.roleTypes = roleTypes;
        }

        public RequestAddAdmin build(){
            return new RequestAddAdmin(this);
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getCoreUserId() {
        return coreUserId;
    }

    public void setCoreUserId(long coreUserId) {
        this.coreUserId = coreUserId;
    }

    public ArrayList<String> getRoleTypes() {
        return roleTypes;
    }

    public void setRoleTypes(ArrayList<String> roleTypes) {
        this.roleTypes = roleTypes;
    }
}
