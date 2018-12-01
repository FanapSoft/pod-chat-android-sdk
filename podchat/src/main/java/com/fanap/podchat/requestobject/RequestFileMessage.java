package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.net.Uri;

public class RequestFileMessage {
    private Activity activity;
    private long threadId;
    private Uri fileUri;
    private String metaData;
    private int messageType;
    private String description;

     RequestFileMessage(Builder builder) {
        this.setActivity(builder.activity);
        this.setThreadId(builder.threadId);
        this.setFileUri(builder.fileUri);
        this.setMetaData(builder.metaData);
        this.setMessageType(builder.messageType);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public long getThreadId() {
        return threadId;
    }

    public void setThreadId(long threadId) {
        this.threadId = threadId;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
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

    public static class Builder {
        private Activity activity;
        private long threadId;
        private Uri fileUri;
        private String metaData;
        private int messageType;

        public Builder(Activity activity, long threadId, Uri fileUri) {
            this.activity = activity;
            this.threadId = threadId;
            this.fileUri = fileUri;
        }

        public Builder metaData(String metaData) {
            this.metaData = metaData;
            return this;
        }

        public Builder messageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        public RequestFileMessage build() {
            return new RequestFileMessage(this);
        }


    }
}
