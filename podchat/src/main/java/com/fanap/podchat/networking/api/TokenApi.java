package com.fanap.podchat.networking.api;

import com.fanap.podasync.model.DeviceResult;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface TokenApi {

    @GET("/oauth2/grants/devices")
    Observable<Response<DeviceResult>> getDeviceId(@Header("Authorization") String token);
}
