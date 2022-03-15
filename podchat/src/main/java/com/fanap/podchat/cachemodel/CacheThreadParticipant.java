package com.fanap.podchat.cachemodel;

import androidx.room.Entity;

/*Link thread and participant together */
@Entity(primaryKeys = {"threadId","participantId"})
public class CacheThreadParticipant {

    private long threadId;
    private long participantId;
    private String expireDate;

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
