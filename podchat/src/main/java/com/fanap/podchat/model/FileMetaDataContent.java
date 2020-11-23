package com.fanap.podchat.model;

import com.fanap.podchat.chat.App;
import com.google.gson.JsonObject;

public class FileMetaDataContent {
    private String link;
    private String hashCode;
    private String name;
    private long id;
    private String originalName;
    private long size;
    private String mimeType;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public FileMetaDataContent() {
    }

    public FileMetaDataContent(long id, String name, String mimeType,long size) {
        this.id = id;
        this.name = name;
        this.mimeType = mimeType;
        this.size = size;
    }

    //generate metadata for file
    public String getMetaData(){
        MetaDataFile metaDataFile = new MetaDataFile();
        metaDataFile.setFile(this);

        JsonObject metadata = (JsonObject) App.getGson().toJsonTree(metaDataFile);

        metadata.addProperty("name", name);
        metadata.addProperty("id", id);
        metadata.addProperty("fileHash", hashCode);

       return metadata.toString();
    }
}
