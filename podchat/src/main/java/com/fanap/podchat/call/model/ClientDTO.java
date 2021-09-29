package com.fanap.podchat.call.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanap.podchat.util.Util;

import java.util.Arrays;
import java.util.List;

public class ClientDTO implements Parcelable {
    public static final String VOICE_TOPIC_SUFFIX = "Vo-";
    public static final String VIDEO_TOPIC_SUFFIX = "Vi-";
    private String clientId;
    private String topicReceive;
    private String topicSend;
    private String brokerAddress;
    private String desc;
    private String sendKey;
    private String sendMetaDataTopic;
    private String reciveMetaDataTopic;
    private Boolean mute;
    private Boolean video;

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
        if (topicSend != null && topicSend.startsWith(VIDEO_TOPIC_SUFFIX))
            return topicSend;
        if (topicSend != null && topicSend.startsWith(VOICE_TOPIC_SUFFIX))
            return topicSend.replace(VOICE_TOPIC_SUFFIX, VIDEO_TOPIC_SUFFIX);
        if (topicSend != null)
            return VIDEO_TOPIC_SUFFIX + topicSend;
        return null;
    }

    public String getTopicSendAudio() {
        if (topicSend != null && topicSend.startsWith(VOICE_TOPIC_SUFFIX))
            return topicSend;
        if (topicSend != null && topicSend.startsWith(VIDEO_TOPIC_SUFFIX))
            return topicSend.replace(VIDEO_TOPIC_SUFFIX, VOICE_TOPIC_SUFFIX);
        if (topicSend != null)
            return VOICE_TOPIC_SUFFIX + topicSend;
        return null;
    }

    public String getTopicReceiveVideo() {
        if (topicReceive != null && topicReceive.startsWith(VIDEO_TOPIC_SUFFIX))
            return topicReceive;
        if (topicReceive != null && topicReceive.startsWith(VOICE_TOPIC_SUFFIX))
            return topicReceive.replace(VOICE_TOPIC_SUFFIX, VIDEO_TOPIC_SUFFIX);
        if (topicReceive != null)
            return VIDEO_TOPIC_SUFFIX + topicReceive;
        return null;
    }

    public String getTopicReceiveAudio() {
        if (topicReceive != null && topicReceive.startsWith(VOICE_TOPIC_SUFFIX))
            return topicReceive;
        if (topicReceive != null && topicReceive.startsWith(VIDEO_TOPIC_SUFFIX))
            return topicReceive.replace(VIDEO_TOPIC_SUFFIX, VOICE_TOPIC_SUFFIX);
        if (topicReceive != null)
            return VOICE_TOPIC_SUFFIX + topicReceive;
        return null;
    }

//    public String getBrokerAddress() {
//        return brokerAddress;
//    }

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

    public String getSendMetaDataTopic() {
        return sendMetaDataTopic;
    }

    public void setSendMetaDataTopic(String sendMetaDataTopic) {
        this.sendMetaDataTopic = sendMetaDataTopic;
    }

    public String getReciveMetaDataTopic() {
        return reciveMetaDataTopic;
    }

    public void setReciveMetaDataTopic(String reciveMetaDataTopic) {
        this.reciveMetaDataTopic = reciveMetaDataTopic;
    }

    public Boolean getMute() {
        return mute;
    }

    public void setMute(Boolean mute) {
        this.mute = mute;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
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

        dest.writeString(this.sendMetaDataTopic);
        dest.writeString(this.reciveMetaDataTopic);
//        dest.writeBoolean(this.mute);
//        dest.writeBoolean(this.video);


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
        this.sendMetaDataTopic = in.readString();
        this.reciveMetaDataTopic = in.readString();
//        this.video = in.readBoolean();
//        this.mute = in.readBoolean();


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
