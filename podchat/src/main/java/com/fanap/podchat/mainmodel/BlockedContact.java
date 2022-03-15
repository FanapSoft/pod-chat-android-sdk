package com.fanap.podchat.mainmodel;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class BlockedContact {

    @SerializedName("id")
    private long blockId;

    private String firstName;

    private String lastName;

    private String nickName;

    private String profileImage;

    private long coreUserId;

    private Contact contactVO;

    public BlockedContact(long blockId, String firstName, String lastName, String nickName, String profileImage, long coreUserId, Contact contactVO) {
        this.blockId = blockId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.profileImage = profileImage;
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

    @Nullable
    public Contact getContactVO() {
        return contactVO;
    }

    public void setContactVO(Contact contactVO) {
        this.contactVO = contactVO;
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
}
