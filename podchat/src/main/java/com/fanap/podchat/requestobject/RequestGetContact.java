package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.Invitee;

public class RequestGetContact extends BaseRequestObject {
    Invitee user;
    RequestGetContact(@NonNull Builder builder) {
        super(builder);
        if (builder.user != null)
            this.user = builder.user;
    }

    public Invitee getUser() {
        return user;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        Invitee user = null;

        @NonNull
        public RequestGetContact build() {
            return new RequestGetContact(this);
        }

        public Builder setUserName(Invitee user) {
            this.user = user;
            return this;
        }

        @Nullable
        @Override
        protected Builder self() {
            return this;
        }
    }
}
