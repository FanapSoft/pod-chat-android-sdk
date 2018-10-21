package com.fanap.podchat.mainmodel;

public class UserInfo  {
    private long id;
    private boolean sendEnable;
    private boolean receiveEnable;
    private String name;
    private String cellphoneNumber;
    private String image;

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
}
