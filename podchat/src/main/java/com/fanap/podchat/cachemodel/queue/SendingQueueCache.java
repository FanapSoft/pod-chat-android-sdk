package com.fanap.podchat.cachemodel.queue;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.Thread;

@Entity
public class SendingQueueCache {

    @PrimaryKey(autoGenerate = true)
    private long QueueId;

    private String asyncContent;
    private long threadId;
    private String userGroupHash;

    private String uniqueId;
    private int messageType;

    private long id;
    private long previousId;
    private String message;
    private String metadata;
    private String systemMetadata;

    public long getQueueId() {
        return QueueId;
    }

    public void setQueueId(long id) {
        this.QueueId = QueueId;
    }

    public String getAsyncContent() {
        return asyncContent;
    }

    public void setAsyncContent(String asyncContent) {
        this.asyncContent = asyncContent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPreviousId() {
        return previousId;
    }

    public void setPreviousId(long previousId) {
        this.previousId = previousId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getUserGroupHash() {
        return userGroupHash;
    }

    public void setUserGroupHash(String userGroupHash) {
        this.userGroupHash = userGroupHash;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        try {
            if (obj == null) return false;

            if (this == obj)
                return true;

            return this.id == ((SendingQueueCache) obj).getId();
        } catch (Exception e) {
            return super.equals(obj);
        }

    }
}
