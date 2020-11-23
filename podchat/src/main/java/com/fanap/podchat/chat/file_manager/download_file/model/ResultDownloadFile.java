package com.fanap.podchat.chat.file_manager.download_file.model;

import android.net.Uri;

import java.io.File;

public class ResultDownloadFile {

    private File file;

    private Uri uri;

    private String hashCode;

    private long id;

    private boolean fromCache = false;

    public boolean isFromCache() {
        return fromCache;
    }

    public void setFromCache(boolean fromCache) {
        this.fromCache = fromCache;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public void setId(long id) {
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public Uri getUri() {
        return uri;
    }

    public String getHashCode() {
        return hashCode;
    }

    public long getId() {
        return id;
    }
}
