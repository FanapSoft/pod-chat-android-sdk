package com.fanap.podchat.chat.assistant.model;

import com.fanap.podchat.mainmodel.Invitee;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class AssistantVo {

    @SerializedName("assistant")
    Invitee invitees;
    @SerializedName("roleTypes")
    private ArrayList<String> roles;
    @SerializedName("contactType")
    String contactType;

    public Invitee getInvitees() {
        return invitees;
    }

    public void setInvitees(Invitee invitees) {
        this.invitees = invitees;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }
}
