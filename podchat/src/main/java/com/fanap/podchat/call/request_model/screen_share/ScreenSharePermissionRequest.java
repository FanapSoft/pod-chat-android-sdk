package com.fanap.podchat.call.request_model.screen_share;

import android.app.Activity;

import com.fanap.podchat.requestobject.GeneralRequestObject;

public class ScreenSharePermissionRequest extends GeneralRequestObject {


    private final Activity activity;
    private final int permissionCode;

    public ScreenSharePermissionRequest(Builder builder) {
        this.activity = builder.activity;
        this.permissionCode = builder.permissionCode;
    }


    public Activity getActivity() {
        return activity;
    }

    public int getPermissionCode() {
        return permissionCode;
    }

    public static class Builder extends GeneralRequestObject.Builder<ScreenSharePermissionRequest.Builder> {

       private final Activity activity;
       private int permissionCode = 101;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setPermissionCode(int permissionCode) {
            this.permissionCode = permissionCode;
            return this;
        }

        @Override
        public Builder typeCode(String typeCode) {
            super.typeCode(typeCode);
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public ScreenSharePermissionRequest build() {
            return new ScreenSharePermissionRequest(this);
        }

    }

}
