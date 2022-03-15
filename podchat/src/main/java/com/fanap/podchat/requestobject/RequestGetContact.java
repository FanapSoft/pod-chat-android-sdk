package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RequestGetContact extends BaseRequestObject {
    String username;

    RequestGetContact(@NonNull Builder builder) {
        super(builder);
        if (builder.username != null)
            this.username = builder.username;
    }

    public String getUsername() {
        return username;
    }


    public static class Builder extends BaseRequestObject.Builder<Builder> {
        String username = null;

        @NonNull
        public RequestGetContact build() {
            return new RequestGetContact(this);
        }

        public Builder setUserName(String username) {
            this.username = username;
            return this;
        }

        @Nullable
        @Override
        protected Builder self() {
            return this;
        }
    }
}
