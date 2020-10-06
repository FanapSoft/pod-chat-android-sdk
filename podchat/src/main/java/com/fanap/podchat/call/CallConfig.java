package com.fanap.podchat.call;

public class CallConfig {

    private String targetActivity;


    public CallConfig(String targetActivity) {
        this.targetActivity = targetActivity;
    }

    public String getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(String targetActivity) {
        this.targetActivity = targetActivity;
    }
}
