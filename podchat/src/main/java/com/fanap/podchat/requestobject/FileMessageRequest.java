package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

public class FileMessageRequest {
    private Activity activity;
    private long threadId;
    private Uri fileUri;
    private String systemMetadata;
    private int messageType;
    private String description;
    private String userGroupHash;

    private String imageXc;
    private String imageYc;
    private String imageHc;
    private String imageWc;

    FileMessageRequest(Builder builder) {
        this.setActivity(builder.activity);
        this.setThreadId(builder.threadId);
        this.setFileUri(builder.fileUri);
        this.setSystemMetadata(builder.systemMetadata);
        this.setMessageType(builder.messageType);
        this.setDescription(builder.description);
        this.setUserGroupHash(builder.userGroupHash);
        imageHc = builder.imageHc;
        imageWc = builder.imageWc;
        imageXc = builder.imageXc;
        imageYc = builder.imageYc;
    }

    public String getImageXc() {
        return imageXc;
    }

    public void setImageXc(String imageXc) {
        this.imageXc = imageXc;
    }

    public String getImageYc() {
        return imageYc;
    }

    public void setImageYc(String imageYc) {
        this.imageYc = imageYc;
    }

    public String getImageHc() {
        return imageHc;
    }

    public void setImageHc(String imageHc) {
        this.imageHc = imageHc;
    }

    public String getImageWc() {
        return imageWc;
    }

    public void setImageWc(String imageWc) {
        this.imageWc = imageWc;
    }

    public String getUserGroupHash() {
        return userGroupHash;
    }

    public void setUserGroupHash(String userGroupHash) {
        this.userGroupHash = userGroupHash;
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
        private String description;
        private Activity activity;
        private long threadId;
        private Uri fileUri;
        private String systemMetadata;
        private int messageType;
        private String userGroupHash;
        private String imageXc;
        private String imageYc;
        private String imageHc;
        private String imageWc;

        public Builder(Activity activity,
                       long threadId,
                       Uri fileUri,
                       int messageType) {
            this.activity = activity;
            this.threadId = threadId;
            this.fileUri = fileUri;
            this.messageType = messageType;
        }

        public Builder(Activity activity,
                       long threadId,
                       Uri fileUri,
                       String description,
                       String systemMetadata,
                       int messageType) {

            this.activity = activity;
            this.threadId = threadId;
            this.fileUri = fileUri;
            this.description = description;
            this.systemMetadata = systemMetadata;
            this.messageType = messageType;
        }

        public Builder(String description,
                       Activity activity,
                       long threadId,
                       Uri fileUri,
                       String systemMetadata,
                       int messageType,
                       String userGroupHash) {

            this.description = description;
            this.activity = activity;
            this.threadId = threadId;
            this.fileUri = fileUri;
            this.systemMetadata = systemMetadata;
            this.messageType = messageType;
            this.userGroupHash = userGroupHash;
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
        public Builder setUserGroupHash(String userGroupHash) {
            this.userGroupHash = userGroupHash;
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

        public Builder setImageXc(String imageXc) {
            this.imageXc = imageXc;
            return this;
        }

        public Builder setImageYc(String imageYc) {
            this.imageYc = imageYc;
            return this;

        }

        public Builder setImageHc(String imageHc) {
            this.imageHc = imageHc;
            return this;

        }

        public Builder setImageWc(String imageWc) {
            this.imageWc = imageWc;
            return this;

        }

        @NonNull
        public FileMessageRequest build() {
            return new FileMessageRequest(this);
        }
    }
}
