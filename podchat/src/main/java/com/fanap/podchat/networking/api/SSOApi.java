package com.fanap.podchat.networking.api;

import android.support.annotation.NonNull;

import com.fanap.podasync.model.DeviceResult;
import com.fanap.podchat.model.EncResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface SSOApi {

    @NonNull
    @GET("/oauth2/grants/devices")
    Observable<Response<DeviceResult>> getDeviceId(@Header("Authorization") String token);

    @POST("/users/handshake")
    @FormUrlEncoded
    Call<EncResponse> generateEncryptionKey(
            @Header("Authorization") String bearerToken,
            @Field("keyAlgorithm") String keyAlgorithm,
            @Field("keySize") int keySize,
            @Field("renew") boolean renew,
            @Field("validity") long validity
    );


}
