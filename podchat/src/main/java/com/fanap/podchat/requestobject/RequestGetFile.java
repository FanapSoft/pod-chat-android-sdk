package com.fanap.podchat.requestobject;

public class RequestGetFile {
    private long fileId;
    private String hashCode;
    private boolean downloadable;

    public RequestGetFile(){

    }

    public static class Builder{

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
