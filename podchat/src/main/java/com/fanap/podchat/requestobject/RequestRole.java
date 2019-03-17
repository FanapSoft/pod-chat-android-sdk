package com.fanap.podchat.requestobject;

import java.util.ArrayList;

public class RequestRole {
    private long id;
    private ArrayList<String> roleTypes;
    private String roleOperation;

    public static final String ADD = "add";
    public static final String REMOVE = "remove";


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ArrayList<String> getRoleTypes() {
        return roleTypes;
    }

    public void setRoleTypes(ArrayList<String> roleTypes) {
        this.roleTypes = roleTypes;
    }

    public String getRoleOperation() {
        return roleOperation;
    }

    public void setRoleOperation(String roleOperation) {
        this.roleOperation = roleOperation;
    }
}
