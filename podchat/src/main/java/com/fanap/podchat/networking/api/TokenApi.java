package com.fanap.podchat.networking.api;

import android.support.annotation.NonNull;

import com.fanap.podasync.model.DeviceResult;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import rx.Observable;

public interface TokenApi {

    @NonNull
    @GET("/oauth2/grants/devices")
    Observable<Response<DeviceResult>> getDeviceId(@Header("Authorization") String token);
}
