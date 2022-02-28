package com.fanap.podchat.requestobject;

import android.support.annotation.NonNull;

public class RequestConnect extends BaseRequestObject {

    private String socketAddress;
    private String appId;
    private String severName;
    private String token;
    private String ssoHost;
    private String platformHost;
    private String fileServer;
    private String podSpaceServer;
    private String encryptionServer;


    public RequestConnect(@NonNull Builder builder) {
        super(builder);
        this.socketAddress = builder.socketAddress;
        this.appId = builder.appId;
        this.fileServer = builder.fileServer;
        this.platformHost = builder.platformHost;
        this.severName = builder.severName;
        this.token = builder.token;
        this.ssoHost = builder.ssoHost;
        this.podSpaceServer = builder.podSpaceServer;
        this.encryptionServer = builder.encryptionServer;
    }


    public static class Builder extends BaseRequestObject.Builder<Builder> {
        private String podSpaceServer;
        private String socketAddress;
        private String appId;
        private String severName;
        private String token;
        private String ssoHost;
        private String platformHost;
        private String fileServer;
        private String encryptionServer ;



        public Builder(String socketAddress, String appId, String severName, String token, String ssoHost, String platformHost, String fileServer, String podSpaceServer) {
            this.podSpaceServer = podSpaceServer;
            this.socketAddress = socketAddress;
            this.appId = appId;
            this.severName = severName;
            this.token = token;
            this.ssoHost = ssoHost;
            this.platformHost = platformHost;
            this.fileServer = fileServer;
        }

        public Builder(String socketAddress, String appId, String severName, String token, String ssoHost, String platformHost, String fileServer, String podSpaceServer, String encryptionServer) {
            this.podSpaceServer = podSpaceServer;
            this.socketAddress = socketAddress;
            this.appId = appId;
            this.severName = severName;
            this.token = token;
            this.ssoHost = ssoHost;
            this.platformHost = platformHost;
            this.fileServer = fileServer;
            this.encryptionServer = encryptionServer;
        }

        @NonNull
        public RequestConnect build() {
            return new RequestConnect(this);
        }

        @NonNull
        @Override
        protected Builder self() {
            return this;
        }
    }

    public String getEncryptionServer() {
        return encryptionServer;
    }

    public String getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(String socketAddress) {
        this.socketAddress = socketAddress;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSeverName() {
        return severName;
    }

    public void setSeverName(String severName) {
        this.severName = severName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSsoHost() {
        return ssoHost;
    }

    public void setSsoHost(String ssoHost) {
        this.ssoHost = ssoHost;
    }

    public String getPlatformHost() {
        return platformHost;
    }

    public void setPlatformHost(String platformHost) {
        this.platformHost = platformHost;
    }

    public String getFileServer() {
        return fileServer;
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    public String getPodSpaceServer() {
        return podSpaceServer;
    }

    public void setPodSpaceServer(String podSpaceServer) {
        this.podSpaceServer = podSpaceServer;
    }
}
