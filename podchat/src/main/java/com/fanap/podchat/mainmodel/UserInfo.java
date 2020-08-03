package com.fanap.podchat.mainmodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.fanap.podchat.chat.user.profile.ChatProfileVO;

@Entity
public class UserInfo {
    @PrimaryKey
    private long id;
    private boolean sendEnable;
    private boolean receiveEnable;
    private String name;
    private String cellphoneNumber;
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
}
