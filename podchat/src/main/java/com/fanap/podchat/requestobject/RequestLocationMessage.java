package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.support.annotation.NonNull;

public class RequestLocationMessage extends BaseRequestMapStImage {

    private long threadId;
    private String userGroupHash;
    private long messageId;
    private String message;
    private String systemMetadata;
    private int messageType;
    private Activity activity;

    public RequestLocationMessage(@NonNull Builder builder) {
        super(builder);
        this.threadId = builder.threadId;
        this.message = builder.message;
        this.messageType = builder.messageType;
        this.activity = builder.activity;
        this.systemMetadata = builder.systemMetadata;
        this.messageId = builder.messageId;
        this.userGroupHash = builder.userGroupHash;

    }

    public static class Builder extends BaseRequestMapStImage.Builder<Builder> {
        private long threadId;
        private String message;
        private int messageType;
        private Activity activity;
        private String systemMetadata;
        private long messageId;
        private String userGroupHash;

        public Builder setUserGroupHash(String userGroupHash) {
            this.userGroupHash = userGroupHash;
            return this;
        }

        @NonNull
        public Builder messageId(long messageId){
            this.messageId = messageId;
            return this;
        }

        @NonNull
        public Builder systemMetadata(String systemMetadata){
            this.systemMetadata = systemMetadata;
            return this;
        }

        @NonNull
        public Builder activity(Activity activity) {
            this.activity = activity;
            return this;
        }

        @NonNull
        public Builder messageType(int messageType) {
            this.messageType = messageType;
            return this;
        }

        @NonNull
        public Builder threadId(long threadId) {
            this.threadId = threadId;
            return this;
        }

        @NonNull
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        @NonNull
        public RequestLocationMessage build() {
            return new RequestLocationMessage(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }


    }

    public String getUserGroupHash() {
        return userGroupHash;
    }

    public long getMessageId() {
        return messageId;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
