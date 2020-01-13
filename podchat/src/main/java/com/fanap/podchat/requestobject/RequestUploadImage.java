package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

public class RequestUploadImage extends RequestUploadFile {


    RequestUploadImage(Builder builder) {
       super(builder);
    }

    public static class Builder extends RequestUploadFile.Builder {

        public Builder(Activity activity, Uri fileUri) {
            super(activity,fileUri);
        }


        public Builder setActivity(Activity activity) {

            super.setActivity(activity);

            return this;
        }

        public Builder setFileUri(Uri fileUri) {
            super.setFileUri(fileUri);
            return this;
        }

        @NonNull
        public RequestUploadImage build() {
            return new RequestUploadImage(this);
        }
    }
}
