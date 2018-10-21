package com.fanap.podchat.model;

import java.util.List;

public class AddContacts {
    private Boolean hasError;
    private String referenceNumber;
    private String message;
    private Integer errorCode;
    private Integer count;
    private String ott;
    private List<ResultAddContact> result ;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultAddContact> getResult() {
        return result;
    }

    public void setResult(List<ResultAddContact> result) {
        this.result = result;
    }
}
