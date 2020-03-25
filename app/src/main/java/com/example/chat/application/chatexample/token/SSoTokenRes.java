package com.example.chat.application.chatexample.token;

import com.google.gson.annotations.SerializedName;

public class SSoTokenRes extends PodResult {


  @SerializedName("result")
  private
  Result result;


    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

     public class Result{

      @SerializedName("access_token")
      private String accessToken;
      @SerializedName("expires_in")
      private long expiresIn;
      @SerializedName("id_token")
      private String idToken;
      @SerializedName("refresh_token")
      private String refreshToken;
      @SerializedName("scope")
      private String scope;
      @SerializedName("token_type")
      private String tokenType;


      public String getAccessToken() {
          return accessToken;
      }

      public void setAccessToken(String accessToken) {
          this.accessToken = accessToken;
      }

      public long getExpiresIn() {
          return expiresIn;
      }

      public void setExpiresIn(long expiresIn) {

          this.expiresIn = System.currentTimeMillis() + (expiresIn * 1000);
      }

      public String getIdToken() {
          return idToken;
      }

      public void setIdToken(String idToken) {
          this.idToken = idToken;
      }

      public String getRefreshToken() {
          return refreshToken;
      }

      public void setRefreshToken(String refreshToken) {
          this.refreshToken = refreshToken;
      }

      public String getScope() {
          return scope;
      }

      public void setScope(String scope) {
          this.scope = scope;
      }

      public String getTokenType() {
          return tokenType;
      }

      public void setTokenType(String tokenType) {
          this.tokenType = tokenType;
      }

  }
}
