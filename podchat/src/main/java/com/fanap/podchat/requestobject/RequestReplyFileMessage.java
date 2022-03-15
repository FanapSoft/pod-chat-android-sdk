package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.net.Uri;
import androidx.annotation.NonNull;

public class RequestReplyFileMessage extends GeneralRequestObject {

    private String messageContent;
    private long threadId;
    private String userGroupHashCode;
    private long messageId;
    private String systemMetaData;
    private Uri fileUri;
    private Activity activity;
    private int messageType;

    private String imageXc;
    private String imageYc;
    private String imageHc;
    private String imageWc;


    public String getImageXc() {
        return imageXc;
    }

    public String getImageYc() {
        return imageYc;
    }

    public String getImageHc() {
        return imageHc;
    }

    public String getImageWc() {
        return imageWc;
    }

    RequestReplyFileMessage(@NonNull Builder builder) {
        super(builder);
        this.systemMetaData = builder.systemMetaData;
        this.messageContent = builder.messageContent;
        this.threadId = builder.threadId;
        this.messageId = builder.messageId;
        this.fileUri = builder.fileUri;
        this.activity = builder.activity;
        this.messageType = builder.messageType;
        this.userGroupHashCode = builder.userGroupHashCode;
        imageHc = builder.imageHc;
        imageWc = builder.imageWc;
        imageXc = builder.imageXc;
        imageYc = builder.imageYc;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String userGroupHashCode;

        private String messageContent;
        private long threadId;
        private long messageId;
        private String systemMetaData;
        private Uri fileUri;
        private Activity activity;
        private int messageType;
        private String imageXc;
        private String imageYc;
        private String imageHc;
        private String imageWc;



        public Builder(String messageContent,
                       long threadId,
                       long messageId,
                       Uri fileUri,
                       Activity activity,
                       int messageType) {
            this.messageContent = messageContent;
            this.threadId = threadId;
            this.messageId = messageId;
            this.fileUri = fileUri;
            this.activity = activity;
            this.messageType = messageType;

        }

        public Builder(String userGroupHashCode, String messageContent, long threadId, long messageId, Uri fileUri, Activity activity, int messageType) {
            this.userGroupHashCode = userGroupHashCode;
            this.messageContent = messageContent;
            this.threadId = threadId;
            this.messageId = messageId;
            this.fileUri = fileUri;
            this.activity = activity;
            this.messageType = messageType;
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
        public Builder messageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        @NonNull
        public Builder systemMetaData(String systemMetaData) {
            this.systemMetaData = systemMetaData;
            return this;
        }


        public Builder setUserGroupHashCode(String userGroupHashCode) {
            this.userGroupHashCode = userGroupHashCode;
            return this;
        }

        @NonNull
        public RequestReplyFileMessage build() {
            return new RequestReplyFileMessage(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }

    }

    public String getUserGroupHashCode() {
        return userGroupHashCode;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

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

    public String getSystemMetaData() {
        return systemMetaData;
    }

    public void setSystemMetaData(String systemMetaData) {
        this.systemMetaData = systemMetaData;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

}
