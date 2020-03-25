package com.example.chat.application.chatexample.token;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


/**
 * Created by Sepehr on 1/25/2017.
 */

public interface ApiInterface {


    String TOURISM_PRE = "/api/cms";
    String STORE_PRE = "/appStore/restAPI/spring/service";


    String POD_PRE = "/nzh/drive/oauth2";

//    @POST(TOURISM_PRE + "/users/handshake")
//    @FormUrlEncoded
//    Call<HandShakeRes> handShake(@Field("device_uid") String deviceid
//            , @Field("device_name") String deviceName
//            , @Field("device_os") String deviceOs
//            , @Field("device_os_version") String deviceOsVersion
//            , @Field("device_type") String deviceType
//            , @Field("businessClientId") String clientId);

    @POST("otp/handshake/")
    @FormUrlEncoded
    Call<HandShakeRes> handShake(@Field("deviceUid") String deviceid);


//    @POST("/oauth2/otp/authorize/{number}")
//    @FormUrlEncoded
//    Call<LoginRes> login(@Header("Authorization") String auth
//            , @Path("number") String number
//            , @Field("client_id") String clientId
//            , @Field("response_type") String responseType
//            , @Field("identityType") String identityType
//            , @Field("scope") String scope
//    );


    @POST("otp/authorize/")
    @FormUrlEncoded
    Call<LoginRes> login(@Field("identity") String number, @Field("keyId") String keyId);


//    @POST("/oauth2/otp/verify/{number}")
//    @FormUrlEncoded
//    Call<VerifyRes> verifyNumber(@Header("Authorization") String auth
//            , @Path("number") String number
//            , @Field("otp") String verifyCode
//    );


    @POST("otp/verify")
    @FormUrlEncoded
    Call<VerifyRes> verifyNumber(@Field("keyId") String keyId
            , @Field("identity") String number
            , @Field("verifyCode") String verifyCode
    );


//    @POST("/oauth2/token")
//    @FormUrlEncoded
//    Call<SSoTokenRes> getOTPToken(@Header("Authorization") String auth
//            , @Field("client_id") String clientId
//            , @Field("grant_type") String grantType
//            , @Field("code") String code
//    );

    @POST("accessToken/")
    @FormUrlEncoded
    Call<SSoTokenRes> getOTPToken(@Field("code") String code);


    //
//    @POST("/oauth2/token")
//    @FormUrlEncoded
//    Call<SSoTokenRes> refreshToken(@Header("Authorization") String auth
//            , @Field("client_id") String clientId
//            , @Field("grant_type") String grantType
//            , @Field("refresh_token") String refreshToken
//    );
    @POST("refreshToken/")
    @FormUrlEncoded
    Call<SSoTokenRes> refreshToken(
            @Field("refreshToken") String refreshToken
    );

}