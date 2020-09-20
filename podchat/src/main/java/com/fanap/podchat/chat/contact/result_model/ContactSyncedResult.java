package com.fanap.podchat.chat.contact.result_model;

public class ContactSyncedResult {

    private long userId;

    public ContactSyncedResult(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }
}
