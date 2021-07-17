package com.fanap.podchat.call.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ClientDTO implements Parcelable {
    public static final String VOICE_TOPIC_SUFFIX = "Vo-";
    public static final String VIDEO_TOPIC_SUFFIX = "Vi-";
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

    public String getTopicSendVideo() {
        if (topicSend != null && topicSend.endsWith(VIDEO_TOPIC_SUFFIX))
            return topicSend;
        if (topicSend != null && topicSend.endsWith(VOICE_TOPIC_SUFFIX))
            return topicSend.replace(VOICE_TOPIC_SUFFIX,VIDEO_TOPIC_SUFFIX);
        if (topicSend != null)
            return VIDEO_TOPIC_SUFFIX +  topicSend;
        return null;
    }

    public String getTopicSendAudio() {
        if (topicSend != null && topicSend.endsWith(VOICE_TOPIC_SUFFIX))
            return topicSend;
        if (topicSend != null && topicSend.endsWith(VIDEO_TOPIC_SUFFIX))
            return topicSend.replace(VIDEO_TOPIC_SUFFIX,VOICE_TOPIC_SUFFIX);
        if (topicSend != null)
            return  VOICE_TOPIC_SUFFIX + topicSend;
        return null;
    }

    public String getTopicReceiveVideo() {
        if (topicReceive != null && topicReceive.endsWith(VIDEO_TOPIC_SUFFIX))
            return topicReceive;
        if (topicReceive != null && topicReceive.endsWith(VOICE_TOPIC_SUFFIX))
            return topicReceive.replace(VOICE_TOPIC_SUFFIX, VIDEO_TOPIC_SUFFIX);
        if (topicReceive != null)
            return  VIDEO_TOPIC_SUFFIX + topicReceive;
        return null;
    }

    public String getTopicReceiveAudio() {
        if (topicReceive != null && topicReceive.endsWith(VOICE_TOPIC_SUFFIX))
            return topicReceive;
        if (topicReceive != null && topicReceive.endsWith(VIDEO_TOPIC_SUFFIX))
            return topicReceive.replace(VIDEO_TOPIC_SUFFIX, VOICE_TOPIC_SUFFIX);
        if (topicReceive != null)
            return  VOICE_TOPIC_SUFFIX + topicReceive;
        return null;
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
