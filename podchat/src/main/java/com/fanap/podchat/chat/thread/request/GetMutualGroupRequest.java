package com.fanap.podchat.chat.thread.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanap.podchat.mainmodel.Invitee;
import com.fanap.podchat.requestobject.BaseRequestObject;

public class GetMutualGroupRequest extends BaseRequestObject {
    Invitee toBeUserVO;
    GetMutualGroupRequest(@NonNull Builder builder) {
        super(builder);
        if (builder.toBeUserVO != null)
            this.toBeUserVO = builder.toBeUserVO;
    }

    public Invitee getUser() {
        return toBeUserVO;
    }

    public static class Builder extends BaseRequestObject.Builder<Builder> {
        Invitee toBeUserVO = null;

        public Builder(Invitee user) {
            this.toBeUserVO = user;
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
