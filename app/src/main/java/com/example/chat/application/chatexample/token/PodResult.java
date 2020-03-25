package com.example.chat.application.chatexample.token;

import com.google.gson.annotations.SerializedName;

public class PodResult {


    /**
     * error : invalid_request
     * error_description : Verification code is either wrong or expired
     */

    @SerializedName("error")
    private String error;
    @SerializedName("error_description")
    private String errorDescription;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
