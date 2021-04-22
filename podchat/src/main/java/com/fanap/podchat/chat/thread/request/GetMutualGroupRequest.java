package com.fanap.podchat.chat.thread.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.requestobject.BaseRequestObject;

public class GetMutualGroupRequest extends BaseRequestObject {
    Invitee user;
    GetMutualGroupRequest(@NonNull Builder builder) {
        super(builder);
        if (builder.user != null)
            this.user = builder.user;
    }

    public Invitee getUser() {
        return user;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        Invitee user = null;

        public Builder(Invitee user) {
            this.user = user;
        }

        @NonNull
        public GetMutualGroupRequest build() {
            return new GetMutualGroupRequest(this);
        }

        @Nullable
        @Override
        protected Builder self() {
            return this;
        }
    }
}
