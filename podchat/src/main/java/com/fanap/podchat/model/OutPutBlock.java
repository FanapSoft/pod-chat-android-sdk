package com.fanap.podchat.model;


public class OutPutBlock extends BaseOutPut {
    private String referenceNumber;
    private String ott;
    private BlockUnblockUserResponse result;

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

    public BlockUnblockUserResponse getResult() {
        return result;
    }

    public void setResult(BlockUnblockUserResponse result) {
        this.result = result;
    }
}
