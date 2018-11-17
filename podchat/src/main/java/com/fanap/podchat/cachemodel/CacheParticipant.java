package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(indices = {@Index(value = {"order_id"}, unique = true)})
public class CacheParticipant {
    @PrimaryKey
    private long id;

    @ColumnInfo(name = "order_id")
    private long orderId;

    private long threadId;

    private String name;
    private String firstName;
    private String lastName;
    private String image;
    private long notSeenDuration;
    private long contactId;
    private String contactName;
    private String contactFirstName;
    private String contactLastName;
    private Boolean sendEnable;
    private Boolean receiveEnable;
    private String cellphoneNumber;
    private String email;
    private Boolean myFriend;
    private Boolean online;
    private Boolean blocked;
    private Boolean admin;

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

    public Boolean getSendEnable() {
        return sendEnable;
    }

    public void setSendEnable(Boolean sendEnable) {
        this.sendEnable = sendEnable;
    }

    public Boolean getReceiveEnable() {
        return receiveEnable;
    }

    public void setReceiveEnable(Boolean receiveEnable) {
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

    public Boolean getMyFriend() {
        return myFriend;
    }

    public void setMyFriend(Boolean myFriend) {
        this.myFriend = myFriend;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }
}
