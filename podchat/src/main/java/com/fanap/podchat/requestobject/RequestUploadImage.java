package com.fanap.podchat.requestobject;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

public class RequestUploadImage extends RequestUploadFile {
    private int xC;
    private int yC;
    private int hC;
    private int wC;


    RequestUploadImage(Builder builder) {
        super(builder);
        this.xC = builder.xC;
        this.yC = builder.yC;
        this.hC = builder.hC;
        this.wC = builder.wC;
    }

    public static class Builder extends RequestUploadFile.Builder {

        int xC;
        int yC;
        int hC;
        int wC;


        public Builder(Activity activity, Uri fileUri) {
            super(activity, fileUri);
        }


        public Builder setActivity(Activity activity) {

            super.setActivity(activity);

            return this;
        }

        public Builder setFileUri(Uri fileUri) {
            super.setFileUri(fileUri);
            return this;
        }

        @Override
        public Builder setUserGroupHashCode(String hashCode) {
            super.setUserGroupHashCode(hashCode);
            return this;
        }

        public Builder setxC(int xC) {
            this.xC = xC;
            return this;
        }

        public Builder setyC(int yC) {
            this.yC = yC;
            return this;
        }

        public Builder sethC(int hC) {
            this.hC = hC;
            return this;
        }

        public Builder setwC(int wC) {
            this.wC = wC;
            return this;
        }

        @Override
        public Builder setPublic(boolean isPublic) {
            super.setPublic(isPublic);
            return this;
        }

        @NonNull

        public RequestUploadImage build() {
            return new RequestUploadImage(this);
        }


    }


    public int getxC() {
        return xC;
    }

    public int getyC() {
        return yC;
    }

    public int gethC() {
        return hC;
    }

    public int getwC() {
        return wC;
    }
}
