package com.fanap.podchat.model;

import com.fanap.podchat.chat.App;
import com.fanap.podchat.util.Util;
import com.google.gson.JsonObject;

public class FileImageMetaData {
    private long id;
    private String originalName;
    private String link;
    private String hashCode;
    private String name;
    private int actualHeight;
    private int actualWidth;
    private long size;
    private String mimeType;

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

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

    public int getActualHeight() {
        return actualHeight;
    }

    public void setActualHeight(int actualHeight) {
        this.actualHeight = actualHeight;
    }

    public int getActualWidth() {
        return actualWidth;
    }

    public void setActualWidth(int actualWidth) {
        this.actualWidth = actualWidth;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileImageMetaData() {
    }

    public FileImageMetaData(long id, String originalName, String hashCode, String name, int actualHeight, int actualWidth, long size, String mimeType) {
        this.id = id;
        this.originalName = originalName;
        this.hashCode = hashCode;
        this.name = name;
        this.actualHeight = actualHeight;
        this.actualWidth = actualWidth;
        this.size = size;
        this.mimeType = mimeType;

        if (originalName.contains(".")) {
            String editedName = originalName.substring(0, originalName.lastIndexOf('.'));
            this.name = editedName;
        }
    }



    public String getMetaData(boolean isLocation,  String center){
        if (isLocation) {
            MetadataLocationFile locationFile = new MetadataLocationFile();
            MapLocation mapLocation = new MapLocation();

            if (center.contains(",")) {
                String latitude = center.substring(0, center.lastIndexOf(','));
                String longitude = center.substring(center.lastIndexOf(',') + 1, center.length());
                mapLocation.setLatitude(Double.valueOf(latitude));
                mapLocation.setLongitude(Double.valueOf(longitude));
            }

            locationFile.setLocation(mapLocation);
            locationFile.setFile(this);

            JsonObject metaDataWithName = (JsonObject) App.getGson().toJsonTree(locationFile);

            metaDataWithName.addProperty("name", originalName);
            metaDataWithName.addProperty("id", id);
            metaDataWithName.addProperty("fileHash", hashCode);

            return metaDataWithName.toString();

        } else {

            MetaDataImageFile metaData = new MetaDataImageFile();
            metaData.setFile(this);

            JsonObject metaDataWithName = (JsonObject) App.getGson().toJsonTree(metaData);

            metaDataWithName.addProperty("name", originalName);
            metaDataWithName.addProperty("id", id);
            metaDataWithName.addProperty("fileHash", hashCode);

            return metaDataWithName.toString();
        }
    }
}
