package com.fanap.podchat.call.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CallInfo implements Parcelable {

    private long partnerId;
    private String partnerName;
    private String partnerImage;
    private long subjectId;

    public CallInfo() {
    }

    public long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(long partnerId) {
        this.partnerId = partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerImage() {
        return partnerImage;
    }

    public void setPartnerImage(String partnerImage) {
        this.partnerImage = partnerImage;
    }

    public long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(long subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.partnerId);
        dest.writeString(this.partnerName);
        dest.writeString(this.partnerImage);
        dest.writeLong(this.subjectId);
    }

    protected CallInfo(Parcel in) {
        this.partnerId = in.readLong();
        this.partnerName = in.readString();
        this.partnerImage = in.readString();
        this.subjectId = in.readLong();
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
}
