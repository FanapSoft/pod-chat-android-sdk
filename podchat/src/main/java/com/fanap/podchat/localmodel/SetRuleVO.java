package com.fanap.podchat.localmodel;

import com.fanap.podchat.requestobject.RequestRole;

import java.util.ArrayList;

public class SetRuleVO {

    private long threadId;
    private ArrayList<RequestRole> roles;
    private String typeCode;

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public ArrayList<RequestRole> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<RequestRole> roles) {
        this.roles = roles;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}