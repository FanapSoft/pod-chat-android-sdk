package com.fanap.podchat.model;

import com.fanap.podchat.mainmodel.Contact;

import java.util.List;

public class ResultBlockList {
    private List<Contact> contacts;

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
