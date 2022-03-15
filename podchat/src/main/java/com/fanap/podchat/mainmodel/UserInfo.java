package com.fanap.podchat.mainmodel;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fanap.podchat.chat.user.profile.ChatProfileVO;

@Entity
public class UserInfo {
    @PrimaryKey
    private long id;
    private Long coreUserId;
    private Long lastSeen;
    private boolean sendEnable;
    private boolean receiveEnable;
    private String name;
    private String username;
    private String cellphoneNumber;
    private String email;
    private String image;
    private Boolean contactSynced;

    @Ignore
    private ChatProfileVO chatProfileVO;


    public ChatProfileVO getChatProfileVO() {
        return chatProfileVO;
    }

    public void setChatProfileVO(ChatProfileVO chatProfileVO) {
        this.chatProfileVO = chatProfileVO;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSendEnable() {
        return sendEnable;
    }

    public void setSendEnable(boolean sendEnable) {
        this.sendEnable = sendEnable;
    }

    public boolean isReceiveEnable() {
        return receiveEnable;
    }

    public void setReceiveEnable(boolean receiveEnable) {
        this.receiveEnable = receiveEnable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getContactSynced() {
        return contactSynced;
    }

    public void setContactSynced(Boolean contactSynced) {
        this.contactSynced = contactSynced;
    }

    public Long getCoreUserId() {
        return coreUserId;
    }

    public void setCoreUserId(Long coreUserId) {
        this.coreUserId = coreUserId;
    }

    public Long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(Long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
