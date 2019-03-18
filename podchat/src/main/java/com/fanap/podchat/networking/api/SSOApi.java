package com.fanap.podchat.networking.api;

import android.support.annotation.NonNull;

import com.fanap.podasync.model.DeviceResult;
import com.fanap.podchat.model.EncResponse;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

public interface SSOApi {

    @NonNull
    @GET("/oauth2/grants/devices")
    Observable<Response<DeviceResult>> getDeviceId(@Header("Authorization") String token);

    @POST("/users/handshake")
    Observable<Response<EncResponse>> generateEncryptionKey(
            @Header("Authorization") String bearerToken,
            @Part("keyAlgorithm") String keyAlgorithm,
            @Part("keySize") int keySize,
            @Part("renew") boolean renew,
            @Part("validity") long validity
    );


}
