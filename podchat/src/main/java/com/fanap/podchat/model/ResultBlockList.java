package com.fanap.podchat.model;

import com.fanap.podchat.mainmodel.BlockedContact;

import java.util.List;

public class ResultBlockList {

    private List<BlockedContact> contacts;

    public List<BlockedContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<BlockedContact> contacts) {
        this.contacts = contacts;
    }
}
