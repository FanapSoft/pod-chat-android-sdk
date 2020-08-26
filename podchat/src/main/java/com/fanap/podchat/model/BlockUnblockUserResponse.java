package com.fanap.podchat.model;

import com.fanap.podchat.mainmodel.BlockedContact;
import com.fanap.podchat.mainmodel.Contact;

public class BlockUnblockUserResponse {
    private BlockedContact contact;

    public BlockedContact getContact() {
        return contact;
    }

    public void setContact(BlockedContact contact) {
        this.contact = contact;
    }
}
