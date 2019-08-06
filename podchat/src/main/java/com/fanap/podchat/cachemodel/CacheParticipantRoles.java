package com.fanap.podchat.cachemodel;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity(primaryKeys = {"id","threadId"})
public class CacheParticipantRoles {

    private long id;

    private long threadId;



    private List<String> roles;


    @Override
    public String toString() {
        return "CacheParticipantRoles{" +
                "id=" + id +
                ", threadId=" + threadId +
                ", roles=" + roles +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }
}
