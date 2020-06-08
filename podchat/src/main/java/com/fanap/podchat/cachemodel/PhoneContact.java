package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

@Entity
public class PhoneContact {

    @NonNull
    @PrimaryKey
    private String phoneNumber;

    private String name;
    private String lastName;
    private int version;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        try {
            PhoneContact b = (PhoneContact) obj;

            if (b != null) {
                return this.phoneNumber.equals(b.phoneNumber);
            }
            return false;
        } catch (Exception e) {
            return super.equals(obj);
        }

    }
}
