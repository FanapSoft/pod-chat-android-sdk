package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

public class RequestFileMessage {
    private Activity activity;
    private long threadId;
    private Uri fileUri;
    private String systemMetadata;
    private int messageType;
    private String description;

    RequestFileMessage(Builder builder) {
        this.setActivity(builder.activity);
        this.setThreadId(builder.threadId);
        this.setFileUri(builder.fileUri);
        this.setSystemMetadata(builder.systemMetadata);
        this.setMessageType(builder.messageType);
        this.setDescription(builder.description);
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

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
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
        public String description;
        private Activity activity;
        private long threadId;
        private Uri fileUri;
        private String systemMetadata;
        private int messageType;

        public Builder(Activity activity, long threadId, Uri fileUri) {
            this.activity = activity;
            this.threadId = threadId;
            this.fileUri = fileUri;
        }

        public Builder(Activity activity, long threadId, Uri fileUri, String description, String systemMetadata, int messageType) {
            this.activity = activity;
            this.threadId = threadId;
            this.fileUri = fileUri;
            this.description = description;
            this.systemMetadata = systemMetadata;

            this.messageType = messageType;


        }

        @NonNull
        public Builder systemMetadata(String systemMetadata) {
            this.systemMetadata = systemMetadata;
            return this;
        }

        @NonNull
        public Builder threadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        @NonNull
        public Builder messageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        @NonNull
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        @NonNull
        public Builder activity(Activity activity) {
            this.activity = activity;
            return this;
        }

        @NonNull
        public Builder fileUri(Uri uri) {
            this.fileUri = uri;
            return this;
        }


        @NonNull
        public RequestFileMessage build() {
            return new RequestFileMessage(this);
        }
    }
}
