package com.fanap.podchat.call.contacts;

import android.os.Parcel;
import android.os.Parcelable;

import com.fanap.podchat.mainmodel.Contact;

public class ContactsWrapper extends Contact implements Parcelable {

    private boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public ContactsWrapper(Contact contact) {
        setId(contact.getId());
        setFirstName(contact.getFirstName());
        setUserId(contact.getUserId());
        setLastName(contact.getLastName());
        setBlocked(contact.getBlocked());
        setCreationDate(contact.getCreationDate());
        setLinkedUser(contact.getLinkedUser());
        setCellphoneNumber(contact.getCellphoneNumber());
        setEmail(contact.getEmail());
        setUniqueId(contact.getUniqueId());
        setNotSeenDuration(contact.getNotSeenDuration());
        setHasUser(contact.isHasUser());
        setCache(contact.isCache());
        setProfileImage(contact.getProfileImage());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.getId());
        dest.writeString(this.getFirstName());
        dest.writeLong(this.getUserId());
        dest.writeString(this.getLastName());
        dest.writeByte(this.getBlocked() ? (byte) 1 : (byte) 0);
        dest.writeLong(this.getCreationDate());
//        dest.writeParcelable(this.getLinkedUser(), flags);
        dest.writeString(this.getCellphoneNumber());
        dest.writeString(this.getEmail());
        dest.writeString(this.getUniqueId());
        dest.writeLong(this.getNotSeenDuration());
        dest.writeByte(this.isHasUser() ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCache() ? (byte) 1 : (byte) 0);
        dest.writeString(this.getProfileImage());
        dest.writeByte(this.isSelected() ? (byte) 1 : (byte) 0);
    }

    public ContactsWrapper() {
    }

    public ContactsWrapper(Parcel in) {
        setId(in.readLong());
        setFirstName(in.readString());
        setUserId(in.readLong());
        setLastName(in.readString());
        setBlocked(in.readByte() != 0);
        setCreationDate(in.readLong());
//        this.linkedUser = in.readParcelable(LinkedUser.class.getClassLoader());
        setCellphoneNumber(in.readString());
        setEmail(in.readString());
        setUniqueId(in.readString());
        setNotSeenDuration(in.readLong());
        setHasUser(in.readByte() != 0);
        setCache(in.readByte() != 0);
        setProfileImage(in.readString());
        setSelected(in.readByte() != 0);
    }

    public static final Parcelable.Creator<ContactsWrapper> CREATOR = new Parcelable.Creator<ContactsWrapper>() {
        @Override
        public ContactsWrapper createFromParcel(Parcel source) {
            return new ContactsWrapper(source);
        }

        @Override
        public ContactsWrapper[] newArray(int size) {
            return new ContactsWrapper[size];
        }
    };
}
