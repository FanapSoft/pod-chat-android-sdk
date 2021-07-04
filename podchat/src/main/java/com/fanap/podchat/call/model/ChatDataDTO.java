package com.fanap.podchat.call.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatDataDTO implements Parcelable {

    private String sendMetaData;
    private String reciveMetaData;
    private String screenShare;

    public String getSendMetaData() {
        return sendMetaData;
    }

    public void setSendMetaData(String sendMetaData) {
        this.sendMetaData = sendMetaData;
    }

    public String getReciveMetaData() {
        return reciveMetaData;
    }

    public void setReciveMetaData(String reciveMetaData) {
        this.reciveMetaData = reciveMetaData;
    }

    public String getScreenShare() {
        return screenShare;
    }

    public void setScreenShare(String screenShare) {
        this.screenShare = screenShare;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sendMetaData);
        dest.writeString(this.reciveMetaData);
        dest.writeString(this.screenShare);
    }

    public ChatDataDTO() {
    }

    protected ChatDataDTO(Parcel in) {
        this.sendMetaData = in.readString();
        this.reciveMetaData = in.readString();
        this.screenShare = in.readString();
    }

    public static final Creator<ChatDataDTO> CREATOR = new Creator<ChatDataDTO>() {
        @Override
        public ChatDataDTO createFromParcel(Parcel source) {
            return new ChatDataDTO(source);
        }

        @Override
        public ChatDataDTO[] newArray(int size) {
            return new ChatDataDTO[size];
        }
    };
}
