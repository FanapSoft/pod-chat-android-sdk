package com.fanap.podchat.requestobject;

import java.util.ArrayList;

public class RequestRole {
    private long id;
    private ArrayList<String> roleTypes;

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

}
