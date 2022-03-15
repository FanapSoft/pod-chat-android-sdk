package com.fanap.podchat.cachemodel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fanap.podchat.mainmodel.Thread;
import com.google.gson.annotations.SerializedName;

@Entity
public class CacheTagParticipantVO {

    @PrimaryKey()
    private Long id;

    private Long tagId;

    private boolean active;

    private Long threadId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getThreadId() {
        return threadId;
    }

    public void setThreadId(Long threadId) {
        this.threadId = threadId;
    }
}
