package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;



@Entity
public class CacheBlockedContact {


    private String expireDate;

    @PrimaryKey
    private long blockId;

    private String firstName;

    private String lastName;

    private String nickName;

    private String profileImage;


    @ColumnInfo(name = "coreId")
    private long coreUserId;

    private long contactId;



    @Ignore
    private CacheContact cacheContact;


    public CacheBlockedContact(String expireDate,
                               long blockId,
                               String firstName,
                               String lastName,
                               String nickName,
                               String profileImage,
                               long coreUserId,
                               long contactId,
                               CacheContact cacheContact) {

        this.expireDate = expireDate;
        this.blockId = blockId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.profileImage = profileImage;
        this.coreUserId = coreUserId;
        this.cacheContact = cacheContact;
        this.contactId = contactId;
    }

    public CacheBlockedContact() {
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

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public CacheContact getCacheContact() {
        return cacheContact;
    }

    public void setCacheContact(CacheContact cacheContact) {
        this.cacheContact = cacheContact;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }
}
