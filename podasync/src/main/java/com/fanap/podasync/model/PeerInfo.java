package com.fanap.podasync.model;

/**
 * {@param appId } Id of your application
 * {@param refresh} When the client disconnected by set this true , {@param deviceId} and {@param appId}
 * {@param renew} tracker id of message that received from platform previously
 */
public class PeerInfo {

    private String deviceId;
    private String appId;
    private boolean refresh;
    private boolean renew;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isRenew() {
        return renew;
    }

    public void setRenew(boolean renew) {
        this.renew = renew;
    }
}
