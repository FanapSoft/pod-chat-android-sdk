package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

public class RequestImageMessage extends RequestFileMessage {
    private String imageXc;
    private String imageYc;
    private String imageHc;
    private String imageWc;

    private RequestImageMessage(Builder builder) {
        super(builder);
        imageHc = builder.imageHc;
        imageWc = builder.imageWc;
        imageXc = builder.imageXc;
        imageYc = builder.imageYc;
    }

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

    public static class Builder extends RequestFileMessage.Builder {
        private String imageXc;
        private String imageYc;
        private String imageHc;
        private String imageWc;


        public Builder(Activity activity, long threadId, Uri fileUri, int messageType) {
            super(activity, threadId, fileUri, messageType);
        }

        @NonNull
        public Builder systemMetadata(String systemMetadata) {
            super.systemMetadata(systemMetadata);
            return this;
        }

        @NonNull
        public Builder threadId(long threadId) {
            super.threadId(threadId);
            return this;
        }

        @NonNull
        public Builder setUserGroupHash(String userGroupHash) {
            super.setUserGroupHash(userGroupHash);
            return this;
        }

        @NonNull
        public Builder messageType(int messageType) {
            super.messageType(messageType);
            return this;
        }

        @NonNull
        public Builder description(String description) {
            super.description(description);
            return this;
        }

        @NonNull
        public Builder activity(Activity activity) {
            super.activity(activity);
            return this;
        }

        @NonNull
        public Builder fileUri(Uri uri) {
            super.fileUri(uri);
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
        public RequestImageMessage build() {
            return new RequestImageMessage(this);
        }
    }
}
