package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class GetContactRequest extends BaseRequestObject {

    GetContactRequest(@NonNull Builder builder) {
        super(builder);
    }

    public static class Builder extends BaseRequestObject.Builder<Builder>{

         @NonNull
         public GetContactRequest build(){
           return new GetContactRequest(this);
         }

        @Nullable
        @Override
        protected Builder self() {
            return this;
        }
    }
}
