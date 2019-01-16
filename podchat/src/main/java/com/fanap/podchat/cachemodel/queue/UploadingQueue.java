package com.fanap.podchat.cachemodel.queue;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class UploadingQueue {

    private String uniqueId;

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long fileId;

    private long threadVoId;

    private String metadata;

    private String systemMetaData;

    private String mimeType;

    private String fileUri;

    private long messageId;

    private int messageType;

    private String description;

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

    public long getThreadVoId() {
        return threadVoId;
    }

    public void setThreadVoId(long threadVoId) {
        this.threadVoId = threadVoId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSystemMetaData() {
        return systemMetaData;
    }

    public void setSystemMetaData(String systemMetaData) {
        this.systemMetaData = systemMetaData;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }
}
