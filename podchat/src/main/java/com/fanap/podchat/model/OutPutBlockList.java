package com.fanap.podchat.model;

public class OutPutBlockList extends BaseOutPut {
    private String referenceNumber;
    private String ott;
    private ResultBlockList result;

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

    public ResultBlockList getResult() {
        return result;
    }

    public void setResult(ResultBlockList result) {
        this.result = result;
    }
}
