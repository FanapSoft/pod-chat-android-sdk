package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fanap.chatcore.model.base.BaseRequestObject;

public class RequestGetContact extends BaseRequestObject {

    RequestGetContact(@NonNull Builder builder) {
        super(builder);
    }

    public static class Builder extends BaseRequestObject.Builder<Builder>{

         @NonNull
         public RequestGetContact build(){
           return new RequestGetContact(this);
         }

        @Nullable
        @Override
        protected Builder self() {
            return this;
        }
    }
}
