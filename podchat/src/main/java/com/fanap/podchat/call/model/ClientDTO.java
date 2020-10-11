package com.fanap.podchat.call.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class ClientDTO implements Parcelable {
    private String clientId;
    private String topicReceive;
    private String topicSend;
    private String brokerAddress;
    private String desc;
    private String sendKey;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getTopicReceive() {
        return topicReceive;
    }

    public void setTopicReceive(String topicReceive) {
        this.topicReceive = topicReceive;
    }

    public String getTopicSend() {
        return topicSend;
    }

    public void setTopicSend(String topicSend) {
        this.topicSend = topicSend;
    }

    public String getBrokerAddress() {
        return brokerAddress;
    }

    public void setBrokerAddress(String brokerAddress) {
        this.brokerAddress = brokerAddress;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSendKey() {
        return sendKey;
    }

    public void setSendKey(String sendKey) {
        this.sendKey = sendKey;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.clientId);
        dest.writeString(this.topicReceive);
        dest.writeString(this.topicSend);
        dest.writeString(this.brokerAddress);
        dest.writeString(this.desc);
        dest.writeString(this.sendKey);
    }

    public ClientDTO() {
    }

    protected ClientDTO(Parcel in) {
        this.clientId = in.readString();
        this.topicReceive = in.readString();
        this.topicSend = in.readString();
        this.brokerAddress = in.readString();
        this.desc = in.readString();
        this.sendKey = in.readString();
    }

    public static final Parcelable.Creator<ClientDTO> CREATOR = new Parcelable.Creator<ClientDTO>() {
        @Override
        public ClientDTO createFromParcel(Parcel source) {
            return new ClientDTO(source);
        }

        @Override
        public ClientDTO[] newArray(int size) {
            return new ClientDTO[size];
        }
    };
}
