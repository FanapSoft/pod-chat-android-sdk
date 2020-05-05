package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RequestGetPodSpaceFile {
    private String hashCode;
    private boolean useCache = true;

    private RequestGetPodSpaceFile(Builder builder) {
        this.hashCode = builder.hashCode;
        this.useCache = builder.useCache;

    }

    public boolean canUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public static class Builder {
        private String hashCode;
        private boolean useCache = true;


        public Builder(long fileId, String hashCode, boolean downloadable) {
        this.hashCode = hashCode;
        }

        @NonNull
        public Builder withNoCache() {
            this.useCache = false;
            return this;
        }

        @NonNull
        public RequestGetPodSpaceFile build(){
            return new RequestGetPodSpaceFile(this);
        }
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

}
