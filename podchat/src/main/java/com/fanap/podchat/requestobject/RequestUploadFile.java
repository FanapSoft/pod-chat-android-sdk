package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

public class RequestUploadFile {


    private Activity activity;
    private Uri fileUri;
    private String userGroupHashCode;
    private boolean isPublic;

    RequestUploadFile(Builder builder) {
        activity = builder.activity;
        fileUri = builder.fileUri;
        userGroupHashCode = builder.userGroupHashCode;
        isPublic = builder.isPublic;
    }


    public String getUserGroupHashCode() {
        return userGroupHashCode;
    }

    public RequestUploadFile() {
    }

    public static class Builder {
        private String userGroupHashCode;
        private Activity activity;
        private Uri fileUri;
        private boolean isPublic = true;


        public Builder(Activity activity, Uri fileUri) {
            this.activity = activity;
            this.fileUri = fileUri;
        }

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setFileUri(Uri fileUri) {
            this.fileUri = fileUri;
            return this;
        }

        public Builder setPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder setUserGroupHashCode(String hashCode) {
            this.userGroupHashCode = hashCode;
            return this;
        }

        @NonNull
        public RequestUploadFile build() {
            return new RequestUploadFile(this);
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public boolean isPublic() {
        return isPublic;
    }
}
