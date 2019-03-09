package com.fanap.podchat.chat.requestobject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
            return null;
        }
    }
}
