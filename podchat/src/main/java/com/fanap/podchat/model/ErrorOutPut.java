package com.fanap.podchat.model;

public class ErrorOutPut {
    private boolean hasError;
    private String errorMessage;
    private long errorCode;
    private String uniqueId;

    public ErrorOutPut(){}

    public ErrorOutPut(boolean hasError, String errorMessage, long errorCode,String uniqueId) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.hasError = hasError;
        this.uniqueId = uniqueId;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public long getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(long errorCode) {
        this.errorCode = errorCode;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
}
