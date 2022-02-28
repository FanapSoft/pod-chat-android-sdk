package com.fanap.podchat.networking.api;

import android.support.annotation.NonNull;

import com.fanap.podasync.model.DeviceResult;
import com.fanap.podchat.model.EncResponse;
import com.fanap.podchat.model.OutPutDefineSecretKey;
import com.fanap.podchat.model.OutPutGetKey;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface SSOApi {

    @NonNull
    @GET("/oauth2/grants/devices")
    Observable<Response<DeviceResult>> getDeviceId(@Header("Authorization") String token);

    @POST("/handshake/users")
    @FormUrlEncoded
    Call<EncResponse> generateEncryptionKey(
            @Header("Authorization") String bearerToken,
            @Field("keyAlgorithm") String keyAlgorithm,
            @Field("keySize") int keySize,
            @Field("renew") boolean renew,
            @Field("validity") long validity
    );


    @NonNull
    @GET("srv/encryption/keys/{keyId}")
    Observable<Response<OutPutGetKey>> generateEncryptionPrivateKey(@Header("Authorization") String token
            , @Header("X-Encrypt-KeyId") String secretKeyId
            , @Path("keyId") String keyId
            , @Query("keyFormat") String keyFormat
         );


    @NonNull
    @POST("srv/encryption/keys/define")
    @FormUrlEncoded
    Observable<Response<OutPutDefineSecretKey>> defineSecretKey(@Header("Authorization") String token
            , @Field("secretKey") String secretKey
            , @Field("algorithm") String algorithm
    );

}
