package com.fanap.podchat.model;


import com.fanap.podchat.chat.mainmodel.Contact;

import java.util.ArrayList;

public class ResultAddContacts {
    private long contentCount;
    private ArrayList<Contact> contacts;

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }
}

