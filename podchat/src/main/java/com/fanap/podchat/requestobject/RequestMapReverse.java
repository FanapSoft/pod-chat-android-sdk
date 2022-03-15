package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;

public class RequestMapReverse {

    private double lat;
    private double lng;

    RequestMapReverse(Builder builder){
        this.lat = builder.lat;
        this.lng = builder.lng;
    }

    public static class Builder{
        private double lat;
        private double lng;

        public Builder (double lat, double lng){
            this.lat = lat;
            this.lng = lng;
        }

        @NonNull
        public RequestMapReverse build(){
            return new RequestMapReverse(this);
        }
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
