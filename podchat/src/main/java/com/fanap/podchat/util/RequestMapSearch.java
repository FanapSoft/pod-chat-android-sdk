package com.fanap.podchat.util;

import androidx.annotation.NonNull;

public class RequestMapSearch {
    private String searchTerm;
    private double latitude;
    private double longitude;

    RequestMapSearch(Builder builder) {
        this.searchTerm = builder.searchTerm;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
    }

    public static class Builder {
        private String searchTerm;
        private double latitude;
        private double longitude;

        public Builder(String searchTerm, double latitude, double longitude) {
            this.searchTerm = searchTerm;
            this.latitude = latitude;
            this.longitude = longitude;
        }
        @NonNull
        public RequestMapSearch build(){
            return new RequestMapSearch(this);
        }

    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
