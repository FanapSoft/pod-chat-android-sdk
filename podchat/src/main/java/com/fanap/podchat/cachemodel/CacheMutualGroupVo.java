package com.fanap.podchat.cachemodel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class CacheMutualGroupVo {

    //TODO
    //inviteeId == participantVOId --- One of these can be deleted from db
    @PrimaryKey()
    private long contactId;

    private List<String> threadids;

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public List<String> getThreadids() {
        return threadids;
    }

    public void setThreadids(List<String> threadids) {
        this.threadids = threadids;
    }


}
