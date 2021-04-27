package com.example.chat.application.chatexample.token;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * Created by Sepehr on 1/25/2017.
 */

public interface ApiInterface {

    @POST("otp/handshake")
    @FormUrlEncoded
    Call<HandShakeRes> handShake(
            @Field("deviceName") String deviceName,
            @Field("deviceOs") String deviceOs,
            @Field("deviceOsVersion") String deviceOsVersion,
            @Field("deviceType") String deviceType,
            @Field("deviceUID") String deviceUID
    );
//    Call<HandShakeRes> handShake(@Field("deviceUid") String deviceid);



    @POST("otp/authorize")
    @FormUrlEncoded
    Call<LoginRes> login(
            @Field("identity") String identity,
            @Header("keyId") String keyId
    );




//    @POST("otp/verify")
//    @FormUrlEncoded
//    Call<VerifyRes> verifyNumber(@Header("keyId") String keyId
//            , @Field("identity") String number
//            , @Field("otp") String verifyCode
//    );

    @POST("otp/verify")
    @FormUrlEncoded
    Call<SSoTokenRes> verifyNumber(@Header("keyId") String keyId
            , @Field("identity") String number
            , @Field("otp") String verifyCode
    );

    @POST("accessToken/")
    @FormUrlEncoded
    Call<SSoTokenRes> getOTPToken(@Field("code") String code);


//    @POST("refreshToken")
    @GET("refresh")
//    @FormUrlEncoded
    Call<SSoTokenRes> refreshToken(
            @Query("refreshToken") String refreshToken
    );

}