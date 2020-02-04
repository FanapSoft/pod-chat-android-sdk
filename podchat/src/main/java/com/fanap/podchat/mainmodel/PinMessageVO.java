package com.fanap.podchat.mainmodel;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class PinMessageVO {

    @PrimaryKey
    private long threadId;
    private long messageId;
    private String text;
    private boolean notifyAll;



    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isNotifyAll() {
        return notifyAll;
    }

    public void setNotifyAll(boolean notifyAll) {
        this.notifyAll = notifyAll;
    }
}
