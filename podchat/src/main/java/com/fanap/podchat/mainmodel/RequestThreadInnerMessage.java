package com.fanap.podchat.mainmodel;

import java.util.List;

public class RequestThreadInnerMessage {

    private String text;
    private int type;
    private long repliedTo;
    private String metadata;
    private String systemMetadata;
    private String uniqueId;
    private List<Long> forwardedMessageIds;
    private List<String> forwardedUniqueIds;


    public RequestThreadInnerMessage(Builder builder){
        this.text = builder.text;
        this.type = builder.type;
        this.repliedTo = builder.repliedTo;
        this.metadata = builder.metadata;
        this.systemMetadata = builder.systemMetadata;
        this.uniqueId = builder.uniqueId;
        this.forwardedMessageIds = builder.forwardedMessageIds;
        this.forwardedUniqueIds = builder.forwardedUniqueIds;
    }

    public static class Builder {
        private String text;
        private int type;
        private long repliedTo;
        private String metadata;
        private String systemMetadata;
        private String uniqueId;
        private List<Long> forwardedMessageIds;
        private List<String> forwardedUniqueIds;

        public Builder(String text, int type) {
            this.text = text;
            this.type = type;
        }

        Builder repliedTo(long repliedTo) {
            this.repliedTo = repliedTo;
            return this;
        }

        Builder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        Builder systemMetadata(String systemMetadata) {
            this.systemMetadata = systemMetadata;
            return this;
        }

        Builder forwardedMessageIds(List<Long> forwardedMessageIds) {
            this.forwardedMessageIds = forwardedMessageIds;
            return this;
        }

        Builder forwardedUniqueIds(List<String> forwardedUniqueIds) {
            this.forwardedUniqueIds = forwardedUniqueIds;
            return this;
        }

        Builder uniqueId(String uniqueId){
            this.uniqueId = uniqueId;
            return this;
        }

        public RequestThreadInnerMessage build() {
            return new RequestThreadInnerMessage(this);
        }

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getRepliedTo() {
        return repliedTo;
    }

    public void setRepliedTo(long repliedTo) {
        this.repliedTo = repliedTo;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSystemMetadata() {
        return systemMetadata;
    }

    public void setSystemMetadata(String systemMetadata) {
        this.systemMetadata = systemMetadata;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<Long> getForwardedMessageIds() {
        return forwardedMessageIds;
    }

    public void setForwardedMessageIds(List<Long> forwardedMessageIds) {
        this.forwardedMessageIds = forwardedMessageIds;
    }

    public List<String> getForwardedUniqueIds() {
        return forwardedUniqueIds;
    }

    public void setForwardedUniqueIds(List<String> forwardedUniqueIds) {
        this.forwardedUniqueIds = forwardedUniqueIds;
    }
}
