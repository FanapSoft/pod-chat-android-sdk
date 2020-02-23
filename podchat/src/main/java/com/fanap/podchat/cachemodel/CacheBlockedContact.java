package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.fanap.podchat.mainmodel.LinkedUser;



@Entity
public class CacheBlockedContact extends CacheContact {

    @PrimaryKey
    private long blockId;

    @ColumnInfo(name = "coreId")
    private long coreUserId;



    public CacheBlockedContact(String expireDate, long id, String firstName, long userId, String lastName, Boolean blocked, long creationDate, LinkedUser linkedUser, String cellphoneNumber, String email, String uniqueId, long notSeenDuration, boolean hasUser, long blockId, long coreUserId) {
        super(expireDate, id, firstName, userId, lastName, blocked, creationDate, linkedUser, cellphoneNumber, email, uniqueId, notSeenDuration, hasUser);
        this.blockId = blockId;
        this.coreUserId = coreUserId;
    }

    @Ignore
    public CacheBlockedContact(String expireDate) {
        super.setExpireDate(expireDate);
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
}
