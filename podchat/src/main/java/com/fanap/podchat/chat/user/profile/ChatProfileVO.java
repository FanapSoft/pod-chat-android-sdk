package com.fanap.podchat.chat.user.profile;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ChatProfileVO extends ResultUpdateProfile {

    @PrimaryKey
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ChatProfileVO{" +
                "id=" + id +
                ", bio='" + getBio() + '\'' +
                ", metadata='" + getMetadata() + '\'' +
                '}';
    }
}
