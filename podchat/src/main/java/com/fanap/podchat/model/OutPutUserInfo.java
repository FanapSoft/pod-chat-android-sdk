package com.fanap.podchat.model;

public class OutPutUserInfo extends BaseOutPut{

    private GetUserInfoResponse result;

    public GetUserInfoResponse getResult() {
        return result;
    }

    public void setResult(GetUserInfoResponse resultUserInfo) {
        this.result = resultUserInfo;
    }
}
