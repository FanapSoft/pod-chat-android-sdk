package com.example.chat.application.chatexample.token;

import com.google.gson.annotations.SerializedName;

public class VerifyRes extends PodResult {


    @SerializedName("result")
    private Result result;


    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result{

        @SerializedName("code")
        private String code;

        @SerializedName("expires_in")
        private int expiresIn;

        public void setCode(String code){
            this.code = code;
        }

        public String getCode(){
            return code;
        }

        public void setExpiresIn(int expiresIn){
            this.expiresIn = expiresIn;
        }

        public int getExpiresIn(){
            return expiresIn;
        }
    }
}
