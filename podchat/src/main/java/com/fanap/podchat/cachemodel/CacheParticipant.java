package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity()
public class CacheParticipant {
    @PrimaryKey
    private long id;

    private long threadId;

    private String name;
    private String firstName;
    private String lastName;

    private String image;

    private long notSeenDuration;

    private long contactId;
    private long coreUserId;

    private String contactName;
    private String contactFirstName;
    private String contactLastName;

    private boolean sendEnable;
    private boolean receiveEnable;

    private String cellphoneNumber;
    private String email;
    private boolean myFriend;
    private boolean online;
    private boolean blocked;
    private boolean admin;

    private List<String> roles;




    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return super.toString();

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNotSeenDuration() {
        return notSeenDuration;
    }

    public void setNotSeenDuration(long notSeenDuration) {
        this.notSeenDuration = notSeenDuration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactFirstName() {
        return contactFirstName;
    }

    public void setContactFirstName(String contactFirstName) {
        this.contactFirstName = contactFirstName;
    }

    public String getContactLastName() {
        return contactLastName;
    }

    public void setContactLastName(String contactLastName) {
        this.contactLastName = contactLastName;
    }

    public boolean getSendEnable() {
        return sendEnable;
    }

    public void setSendEnable(boolean sendEnable) {
        this.sendEnable = sendEnable;
    }

    public boolean getReceiveEnable() {
        return receiveEnable;
    }

    public void setReceiveEnable(boolean receiveEnable) {
        this.receiveEnable = receiveEnable;
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

    public boolean getMyFriend() {
        return myFriend;
    }

    public void setMyFriend(boolean myFriend) {
        this.myFriend = myFriend;
    }

    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getCoreUserId() {
        return coreUserId;
    }

    public void setCoreUserId(long coreUserId) {
        this.coreUserId = coreUserId;
    }
}
