package com.fanap.podchat.cachemodel;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fanap.podchat.chat.user.profile.ChatProfileVO;
import com.fanap.podchat.mainmodel.Participant;

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
    private boolean auditor;
    private String keyId ;

    private String username;

    private List<String> roles;

    @Ignore
    private ChatProfileVO chatProfileVO;


    public CacheParticipant() {
    }
    
    public CacheParticipant(Participant participant,long threadId) {
        this.id = participant.getId();
        this.threadId = threadId;
        this.name = participant.getName();
        this.firstName = participant. getFirstName();
        this.lastName = participant. getLastName();
        this.image = participant. getImage();
        this.notSeenDuration = participant. getNotSeenDuration();
        this.contactId = participant. getContactId();
        this.coreUserId = participant. getCoreUserId();
        this.contactName = participant. getContactName();
        this.contactFirstName = participant. getContactFirstName();
        this.contactLastName = participant. getContactLastName();
        this.sendEnable = participant. getSendEnable();
        this.receiveEnable = participant. getReceiveEnable();
        this.cellphoneNumber = participant. getCellphoneNumber();
        this.email = participant. getEmail();
        this.myFriend = participant. getMyFriend();
        this.online = participant. getOnline();
        this.blocked = participant. getBlocked();
        this.admin = participant. getAdmin();
        this.auditor = participant. getAuditor();
        this.keyId = participant. getKeyId();
        this.username = participant. getUsername();
        this.roles = participant. getRoles();
        this.chatProfileVO = participant. getChatProfileVO();
        
    }


    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "CacheParticipant{" +
                "id=" + id +
                ", threadId=" + threadId +
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
                '}';
    }

    public boolean isAuditor() {
        return auditor;
    }

    public void setAuditor(boolean auditor) {
        this.auditor = auditor;
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

    public ChatProfileVO getChatProfileVO() {
        return chatProfileVO;
    }

    public void setChatProfileVO(ChatProfileVO chatProfileVO) {
        this.chatProfileVO = chatProfileVO;
    }
}
