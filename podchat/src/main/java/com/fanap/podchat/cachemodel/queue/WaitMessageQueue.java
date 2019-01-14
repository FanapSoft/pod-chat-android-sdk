package com.fanap.podchat.cachemodel.queue;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class WaitMessageQueue {

    private String uniqueId;

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long typeCode;
    private int messageType;

    private String message;
    private String metadata;
    private String systemMetadata;

    private Long threadVoId;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
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

    public Long getThreadVoId() {
        return threadVoId;
    }

    public void setThreadVoId(Long threadVoId) {
        this.threadVoId = threadVoId;
    }

    public long getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(long typeCode) {
        this.typeCode = typeCode;
    }
}

