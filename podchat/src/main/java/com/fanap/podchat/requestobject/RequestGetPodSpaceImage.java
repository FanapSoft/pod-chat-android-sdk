package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;

import retrofit2.http.Query;

public class RequestGetPodSpaceImage extends RequestGetPodSpaceFile {

    private String size;
    private Float quality;
    private Boolean crop;

    private RequestGetPodSpaceImage(Builder builder) {
        super(builder);
        this.size = builder.size;
        this.quality = builder.quality;
        this.crop = builder.crop;
    }

    public String getSize() {
        return size;
    }

    public Float getQuality() {
        return quality;
    }

    public Boolean getCrop() {
        return crop;
    }

    public static class Builder extends RequestGetPodSpaceFile.Builder {
        private String size;
        private Float quality;
        private Boolean crop;


        public Builder(String hashCode) {
            super(hashCode);
        }


        @NonNull
        public Builder withNoCache() {
            super.withNoCache();
            return this;
        }


        public Builder setSize(String size) {
            this.size = size;
            return this;
        }

        public Builder setQuality(Float quality) {
            this.quality = quality;
            return this;
        }

        public Builder setCrop(Boolean crop) {
            this.crop = crop;
            return this;
        }

        @NonNull
        public RequestGetPodSpaceImage build() {
            return new RequestGetPodSpaceImage(this);
        }
    }


    @Override
    public String toString() {
        return super.toString() + " >>> " +" RequestGetPodSpaceImage{" +
                "size='" + size + '\'' +
                ", quality=" + quality +
                ", crop=" + crop +
                '}';
    }
}
