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

public interface TokenApi {

    @NonNull
    @GET("/oauth2/grants/devices")
    Observable<Response<DeviceResult>> getDeviceId(@Header("Authorization") String token);

    @POST("/users/handshake")
    Observable<Response<EncResponse>> getEncryptionKey(
            @Header("Authorization") String token,
            @Part("algorithm") String algorithm,
            @Part("keyBitSize") int keyBitSize
    );
}
