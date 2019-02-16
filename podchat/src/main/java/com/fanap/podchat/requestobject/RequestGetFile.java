package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RequestGetFile {
    private long fileId;
    private String hashCode;
    private boolean downloadable;

     RequestGetFile(Builder builder) {
        this.fileId =  builder.fileId;
        this.hashCode = builder.hashCode;
        this.downloadable = builder.downloadable;
    }

    public static class Builder {
        private long fileId;
        private String hashCode;
        private boolean downloadable;

        public Builder(long fileId, String hashCode, boolean downloadable) {
        this.fileId = fileId;
        this.hashCode = hashCode;
        this.downloadable = downloadable;
        }
        @NonNull
        public RequestGetFile build(){
            return new RequestGetFile(this);
        }
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
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
