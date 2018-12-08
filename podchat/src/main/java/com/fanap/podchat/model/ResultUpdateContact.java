package com.fanap.podchat.model;

import java.util.List;

public class ResultUpdateContact {
    private List<ResultContacts> contacts ;
    private long contentCount;

    public List<ResultContacts> getContacts() {
        return contacts;
    }

    public void setContacts(List<ResultContacts> contacts) {
        this.contacts = contacts;
    }

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }
}
