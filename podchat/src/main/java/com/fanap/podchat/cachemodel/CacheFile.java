package com.fanap.podchat.cachemodel;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CacheFile {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String localUri;
    private String url;
    private String hashCode;
    private Float quality;

    public CacheFile(String localUri, String url, String hashCode, Float quality) {
        this.localUri = localUri;
        this.url = url;
        this.hashCode = hashCode;
        this.quality = quality;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocalUri() {
        return localUri;
    }

    public void setLocalUri(String localUri) {
        this.localUri = localUri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;

    }

    public Float getQuality() {
        return quality;
    }

    public void setQuality(Float quality) {
        this.quality = quality;

    }
}
