package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

public class RequestReplyFileMessage extends GeneralRequestObject {

    private String messageContent;
    private long threadId;
    private long messageId;
    private String systemMetaData;
    private Uri fileUri;
    private Activity activity;
    private int messageType;


    RequestReplyFileMessage(@NonNull Builder builder) {
        super(builder);
        this.systemMetaData = builder.systemMetaData;
        this.messageContent = builder.messageContent;
        this.threadId = builder.threadId;
        this.messageId = builder.messageId;
        this.fileUri = builder.fileUri;
        this.activity = builder.activity;
        this.messageType = builder.messageType;
    }

    public static class Builder extends GeneralRequestObject.Builder<Builder> {
        private String messageContent;
        private long threadId;
        private long messageId;
        private String systemMetaData;
        private Uri fileUri;
        private Activity activity;
        private int messageType;


        public Builder(String messageContent, long threadId, long messageId, Uri fileUri, Activity activity) {
            this.messageContent = messageContent;
            this.threadId = threadId;
            this.messageId = messageId;
            this.fileUri = fileUri;
            this.activity = activity;
        }

        @NonNull
        public Builder messageType(int messageType){
            this.messageType = messageType;
            return this;
        }

        @NonNull
        public Builder systemMetaData(String systemMetaData) {
            this.systemMetaData = systemMetaData;
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
