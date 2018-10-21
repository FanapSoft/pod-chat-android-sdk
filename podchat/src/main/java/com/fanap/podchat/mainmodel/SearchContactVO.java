package com.fanap.podchat.mainmodel;

import java.util.List;

public class SearchContactVO {
    private Boolean hasError;
    private String referenceNumber;
    private Integer errorCode;
    private Integer count;
    private String ott;
    private List<Contact> result;

    public Boolean getHasError() {
        return hasError;
    }

    public void setHasError(Boolean hasError) {
        this.hasError = hasError;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getOtt() {
        return ott;
    }

    public void setOtt(String ott) {
        this.ott = ott;
    }

    public List<Contact> getResult() {
        return result;
    }

    public void setResult(List<Contact> result) {
        this.result = result;
    }
}
