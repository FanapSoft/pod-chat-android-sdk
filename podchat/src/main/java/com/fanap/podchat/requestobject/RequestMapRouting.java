package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;

public class RequestMapRouting {
    private String origin;
    private String destination;

    RequestMapRouting(Builder builder){
        this.origin = builder.origin;
        this.destination = builder.destination;

    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public static class Builder{
        private String origin;
        private String destination;

        public Builder(String origin,String destination){
            this.origin = origin;
            this.destination = destination;
        }

        @NonNull
        public RequestMapRouting build(){
            return new RequestMapRouting(this);
        }
    }

}
