package com.fanap.podchat.call.result_model;

public class CallStartResult {

    private String callName;
    private String callImage;


    public CallStartResult(String callName, String callImage) {
        this.callName = callName;
        this.callImage = callImage;
    }


    public String getCallName() {
        return callName;
    }

    public String getCallImage() {
        return callImage;
    }
}
