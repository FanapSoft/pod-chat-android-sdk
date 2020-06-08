package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.fanap.podchat.mainmodel.LinkedUser;

@Entity
public class CacheContact {

    private String expireDate;

    @PrimaryKey
    private long id;
    private String firstName;
    private long userId;
    private String lastName;
    private Boolean blocked;
    private long creationDate;
    private String profileImage;

    @Embedded
    private LinkedUser linkedUser;
    private String cellphoneNumber;
    private String email;
    private String uniqueId;
    private long notSeenDuration;
    private boolean hasUser;


    public CacheContact(
            String expireDate,
            long id,
            String firstName,
            long userId,
            String lastName,
            Boolean blocked,
            long creationDate,
            LinkedUser linkedUser,
            String cellphoneNumber,
            String email,
            String uniqueId,
            long notSeenDuration,
            boolean hasUser,
            String profileImage
    ) {
        this.expireDate = expireDate;
        this.id = id;
        this.firstName = firstName;
        this.userId = userId;
        this.lastName = lastName;
        this.blocked = blocked;
        this.creationDate = creationDate;
        this.linkedUser = linkedUser;
        this.cellphoneNumber = cellphoneNumber;
        this.email = email;
        this.uniqueId = uniqueId;
        this.notSeenDuration = notSeenDuration;
        this.hasUser = hasUser;
        this.profileImage = profileImage;
    }


    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public LinkedUser getLinkedUser() {
        return linkedUser;
    }

    public void setLinkedUser(LinkedUser linkedUser) {
        this.linkedUser = linkedUser;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public long getNotSeenDuration() {
        return notSeenDuration;
    }

    public void setNotSeenDuration(long notSeenDuration) {
        this.notSeenDuration = notSeenDuration;
    }

    public boolean isHasUser() {
        return hasUser;
    }

    public void setHasUser(boolean hasUser) {
        this.hasUser = hasUser;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
