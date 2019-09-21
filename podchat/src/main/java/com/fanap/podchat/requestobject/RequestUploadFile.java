package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

public class RequestUploadFile {

    private Activity activity;
    private Uri fileUri;

    RequestUploadFile(Builder builder) {
        this.activity = builder.activity;
        this.fileUri = builder.fileUri;
    }

    public static class Builder {
        private Activity activity;
        private Uri fileUri;

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
}
