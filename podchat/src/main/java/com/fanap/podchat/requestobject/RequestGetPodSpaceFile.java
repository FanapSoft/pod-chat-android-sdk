package com.fanap.podchat.requestobject;

import androidx.annotation.NonNull;

public class RequestGetPodSpaceFile {
    private String hashCode;
    private boolean useCache = true;

    RequestGetPodSpaceFile(Builder builder) {
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


        public Builder( String hashCode) {
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

    @Override
    public String toString() {
        return "RequestGetPodSpaceFile{" +
                "hashCode='" + hashCode + '\'' +
                ", useCache=" + useCache +
                '}';
    }
}
