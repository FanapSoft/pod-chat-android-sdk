package com.fanap.podchat.requestobject;

import android.app.Activity;
import androidx.annotation.NonNull;

public class RetryUpload extends BaseRetryObject {

    private Activity activity;

    RetryUpload(@NonNull Builder builder) {
        super(builder);
        this.activity = builder.activity;
    }

    public static class Builder extends BaseRetryObject.Builder<Builder> {
        private Activity activity;

        @NonNull
        public Builder activity(Activity activity) {
            this.activity = activity;
            return this;
        }

        @NonNull
        public RetryUpload build() {
            return new RetryUpload(this);
        }

        @NonNull
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
