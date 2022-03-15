package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;

public class RequestGetImage {
    private long imageId;
    private String hashCode;
    private boolean downloadable;
    private boolean useCache = true;


    RequestGetImage(Builder builder) {
        this.imageId = builder.imageId;
        this.hashCode = builder.hashCode;
        this.downloadable = builder.downloadable;
        this.useCache = builder.useCache;

    }


    public boolean canUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }


    public static class Builder {
        private long imageId;
        private String hashCode;
        private boolean downloadable;
        private boolean useCache = true;


        public Builder(long imageId, String hashCode, boolean downloadable) {
            this.imageId = imageId;
            this.hashCode = hashCode;
            this.downloadable = downloadable;
        }

        @NonNull
        public Builder withNoCache() {
            this.useCache = false;
            return this;
        }


        @NonNull
        public RequestGetImage build() {
            return new RequestGetImage(this);
        }
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }
}
