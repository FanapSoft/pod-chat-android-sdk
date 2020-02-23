package com.fanap.podchat.mainmodel;

import com.google.gson.annotations.SerializedName;

public class BlockedContact {


    @SerializedName("id")
    private long blockId;

    private long coreUserId;

    private Contact contactVO;


    public BlockedContact(long blockId, long coreUserId, Contact contactVO) {
        this.blockId = blockId;
        this.coreUserId = coreUserId;
        this.contactVO = contactVO;
    }

    public long getBlockId() {
        return blockId;
    }

    public void setBlockId(long blockId) {
        this.blockId = blockId;
    }

    public long getCoreUserId() {
        return coreUserId;
    }

    public void setCoreUserId(long coreUserId) {
        this.coreUserId = coreUserId;
    }

    public Contact getContactVO() {
        return contactVO;
    }

    public void setContactVO(Contact contactVO) {
        this.contactVO = contactVO;
    }
}
