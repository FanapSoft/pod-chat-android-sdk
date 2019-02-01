package com.fanap.podchat.requestobject;

import android.app.Activity;

public class RetryUpload extends BaseRetryObject {

    private Activity activity;

    RetryUpload(Builder builder) {
        super(builder);
        this.activity = builder.activity;
    }

    public static class Builder extends BaseRetryObject.Builder<Builder> {
        private Activity activity;

        public Builder activity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public RetryUpload build() {
            return new RetryUpload(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
