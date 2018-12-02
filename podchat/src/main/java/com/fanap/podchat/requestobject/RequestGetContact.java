package com.fanap.podchat.requestobject;

public class RequestGetContact extends BaseRequestObject {

    RequestGetContact(Builder builder) {
        super(builder);
    }

    public static class Builder extends BaseRequestObject.Builder<Builder>{

         public RequestGetContact build(){
           return new RequestGetContact(this);
         }

        @Override
        protected Builder self() {
            return null;
        }
    }
}
