package com.fanap.podchat.model;

import com.fanap.podchat.chat.mainmodel.Contact;

public class ResultAddContact {

    private com.fanap.podchat.chat.mainmodel.Contact contact;
    private long contentCount;

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
