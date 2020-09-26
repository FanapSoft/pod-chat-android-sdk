package com.fanap.podchat.call.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CallInfo implements Parcelable {

    private long partnerId;

    private String callName; //partner name when p2p call and group name when group call.
    private String callImage; //partner image when p2p call and thread image when group call.
    private long callId;

    public CallInfo() {
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getCallImage() {
        return callImage;
    }

    public void setCallImage(String callImage) {
        this.callImage = callImage;
    }

    public long getCallId() {
        return callId;
    }

    public void setCallId(long callId) {
        this.callId = callId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.partnerId);
        dest.writeString(this.callName);
        dest.writeString(this.callImage);
        dest.writeLong(this.callId);
    }

    protected CallInfo(Parcel in) {
        this.partnerId = in.readLong();
        this.callName = in.readString();
        this.callImage = in.readString();
        this.callId = in.readLong();
    }

    public static final Parcelable.Creator<CallInfo> CREATOR = new Parcelable.Creator<CallInfo>() {
        @Override
        public CallInfo createFromParcel(Parcel source) {
            return new CallInfo(source);
        }

        @Override
        public CallInfo[] newArray(int size) {
            return new CallInfo[size];
        }
    };

    @Override
    public String toString() {
        return "CallInfo{" +
                "partnerId=" + partnerId +
                ", callName='" + callName + '\'' +
                ", callImage='" + callImage + '\'' +
                ", subjectId=" + callId +
                '}';
    }
}
