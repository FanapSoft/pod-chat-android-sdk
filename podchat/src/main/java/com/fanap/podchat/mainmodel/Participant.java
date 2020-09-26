package com.fanap.podchat.mainmodel;

import android.support.annotation.Nullable;

import android.support.annotation.NonNull;

import com.fanap.podchat.chat.user.profile.ChatProfileVO;

import java.util.ArrayList;
import java.util.List;

public class Participant {


    private long id;
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

    private Boolean sendEnable;
    private Boolean receiveEnable;

    private String cellphoneNumber;
    private String email;

    private Boolean myFriend;
    private Boolean online;

    private Boolean blocked;
    private Boolean admin;

    private Boolean auditor;

    private List<String> roles;

    private String keyId;

    private String username;

    private ChatProfileVO chatProfileVO;

    public Participant() {
    }

    public Participant(
            long id,
            String name,
            String firstName,
            String lastName,
            String image,
            long notSeenDuration,
            long contactId,
            long coreUserId,
            String contactName,
            String contactFirstName,
            String contactLastName,
            Boolean sendEnable,
            Boolean receiveEnable,
            String cellphoneNumber,
            String email,
            Boolean myFriend,
            Boolean online,
            Boolean blocked,
            Boolean admin,
            Boolean auditor,
            List<String> roles,
            String keyId,
            String username,
            ChatProfileVO chatProfileVO
    ) {

        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        this.notSeenDuration = notSeenDuration;
        this.contactId = contactId;
        this.coreUserId = coreUserId;
        this.contactName = contactName;
        this.contactFirstName = contactFirstName;
        this.contactLastName = contactLastName;
        this.sendEnable = sendEnable;
        this.receiveEnable = receiveEnable;
        this.cellphoneNumber = cellphoneNumber;
        this.email = email;
        this.myFriend = myFriend;
        this.online = online;
        this.blocked = blocked;
        this.admin = admin;
        this.roles = roles;
        this.auditor = auditor;
        this.keyId = keyId;
        this.username = username;
        this.chatProfileVO = chatProfileVO;
    }


    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(ArrayList<String> roles) {
        this.roles = roles;
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

    public long getCoreUserId() {
        return coreUserId;
    }

    public void setCoreUserId(long coreUserId) {
        this.coreUserId = coreUserId;
    }

    public Boolean getAuditor() {
        return auditor;
    }

    public void setAuditor(Boolean auditor) {
        this.auditor = auditor;
    }

    public ChatProfileVO getChatProfileVO() {
        return chatProfileVO;
    }

    public void setChatProfileVO(ChatProfileVO chatProfileVO) {
        this.chatProfileVO = chatProfileVO;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        try {
            if (obj != null) {
                return this.id == ((Participant) obj).getId();
            }else return false;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public String toString() {
        return "Participant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", image='" + image + '\'' +
                ", notSeenDuration=" + notSeenDuration +
                ", contactId=" + contactId +
                ", coreUserId=" + coreUserId +
                ", contactName='" + contactName + '\'' +
                ", contactFirstName='" + contactFirstName + '\'' +
                ", contactLastName='" + contactLastName + '\'' +
                ", sendEnable=" + sendEnable +
                ", receiveEnable=" + receiveEnable +
                ", cellphoneNumber='" + cellphoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", myFriend=" + myFriend +
                ", online=" + online +
                ", blocked=" + blocked +
                ", admin=" + admin +
                ", auditor=" + auditor +
                ", roles=" + roles +
                ", keyId='" + keyId + '\'' +
                ", username='" + username + '\'' +
                ", chatProfileVO=" + (chatProfileVO != null ? chatProfileVO.toString() : "") +
                '}';
    }
}
