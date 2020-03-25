package com.example.chat.application.chatexample.token;

import com.google.gson.annotations.SerializedName;

public class HandShakeRes {

    @SerializedName("result")
    private Result result;

    @SerializedName("referenceNumber")
    private String referenceNumber;

    @SerializedName("count")
    private int count;

    @SerializedName("errorCode")
    private int errorCode;

    @SerializedName("hasError")
    private boolean hasError;

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public boolean isHasError() {
        return hasError;
    }


    public class Result {

        @SerializedName("keyFormat")
        private String keyFormat;

        @SerializedName("keyId")
        private String keyId;


        @SerializedName("publicKey")
        private String publicKey;

        @SerializedName("expire_in")
        private int expireIn;


        @SerializedName("algorithm")
        private String algorithm;

        public void setKeyFormat(String keyFormat) {
            this.keyFormat = keyFormat;
        }

        public String getKeyFormat() {
            return keyFormat;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        public String getKeyId() {
            return keyId;
        }

        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public void setExpireIn(int expireIn) {
            this.expireIn = expireIn;
        }

        public int getExpireIn() {
            return expireIn;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getAlgorithm() {
            return algorithm;
        }
    }
}