package com.fanap.podchat.notification;

import java.io.Serializable;
import java.util.Map;

public class FcmAppUsersVO implements Serializable {

    private String appId;

    //userId => deviceToken
    private Map<String, String> userDeviceTokenMap;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Map<String, String> getUserDeviceTokenMap() {
        return userDeviceTokenMap;
    }

    public void setUserDeviceTokenMap(Map<String, String> userDeviceTokenMap) {
        this.userDeviceTokenMap = userDeviceTokenMap;
    }
}
