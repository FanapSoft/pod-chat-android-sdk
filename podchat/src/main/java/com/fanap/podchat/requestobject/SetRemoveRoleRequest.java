package com.fanap.podchat.requestobject;

import java.util.ArrayList;

public class SetRemoveRoleRequest {

    private long threadId;
    private ArrayList<RequestRole> roles;

    private SetRemoveRoleRequest(Builder builder) {
        this.threadId = builder.threadId;
        this.roles = builder.roles;
    }

    public ArrayList<RequestRole> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<RequestRole> roles) {
        this.roles = roles;
    }

    public static class Builder {
        private long threadId;
        private ArrayList<RequestRole> roles;

        public Builder(long threadId, ArrayList<RequestRole> roles) {
            this.threadId = threadId;
            this.roles = roles;
        }

        public SetRemoveRoleRequest build() {
            return new SetRemoveRoleRequest(this);
        }
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }


}
