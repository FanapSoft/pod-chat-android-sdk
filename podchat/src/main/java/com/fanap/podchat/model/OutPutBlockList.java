package com.fanap.podchat.model;

public class OutPutBlockList extends BaseOutPut {
    private String referenceNumber;
    private String ott;
    private GetBlockedUserListResponse result;

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getOtt() {
        return ott;
    }

    public void setOtt(String ott) {
        this.ott = ott;
    }

    public GetBlockedUserListResponse getResult() {
        return result;
    }

    public void setResult(GetBlockedUserListResponse result) {
        this.result = result;
    }
}
