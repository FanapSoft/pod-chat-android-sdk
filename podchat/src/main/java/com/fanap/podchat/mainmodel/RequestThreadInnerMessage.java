package com.fanap.podchat.mainmodel;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RequestThreadInnerMessage {

    private String text;
    @SerializedName("messageType")
    private int type;
    private String metadata;
    private String systemMetadata;
    private List<Long> forwardedMessageIds;

    public RequestThreadInnerMessage(Builder builder) {
        this.text = builder.text;
        this.type = builder.type;
        this.metadata = builder.metadata;
        this.systemMetadata = builder.systemMetadata;
        this.forwardedMessageIds = builder.forwardedMessageIds;
    }

    public RequestThreadInnerMessage() {
    }

    public static class Builder {
        private String text;

        @Deprecated
        @SerializedName("messageType")
        private int type;
        private String metadata;
        private String systemMetadata;
        private List<Long> forwardedMessageIds;

        public Builder(String text,int messageType) {
            this.text = text;
            this.type = messageType;
        }

        public Builder(int type) {
            this.type = type;
        }

        public Builder message(String text) {
            this.text = text;
            return this;
        }

        @Deprecated
        @NonNull
        public Builder type(int type) {
            this.type = type;
            return this;
        }

        @NonNull
        public Builder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }

        @NonNull
        public Builder systemMetadata(String systemMetadata) {
            this.systemMetadata = systemMetadata;
            return this;
        }

        @NonNull
        public Builder forwardedMessageIds(List<Long> forwardedMessageIds) {
            this.forwardedMessageIds = forwardedMessageIds;
            return this;
        }

        @NonNull
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

    @Deprecated
    public int getType() {
        return type;
    }

    @Deprecated
    public void setType(int type) {
        this.type = type;
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

    public List<Long> getForwardedMessageIds() {
        return forwardedMessageIds;
    }

    public void setForwardedMessageIds(List<Long> forwardedMessageIds) {
        this.forwardedMessageIds = forwardedMessageIds;
    }
}
