package com.fanap.podchat.networking.api;

import android.support.annotation.NonNull;

import com.fanap.podchat.mainmodel.MapNeshan;
import com.fanap.podchat.mainmodel.MapReverse;
import com.fanap.podchat.mainmodel.MapRout;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import rx.Observable;

public interface MapApi {
    @NonNull
    @GET("v1/search")
    Observable<Response<MapNeshan>> mapSearch(@Header("Api-Key") String apiKey
            , @Query("term") String searchTerm
            , @Query("lat") Double latitude
            , @Query("lng") Double longitude);

    @NonNull
    @GET("v1/routing")
    Observable<Response<MapRout>> mapRouting(@Header("Api-Key") String apiKey
            , @Query("origin") String origin
            , @Query("destination") String destination
            , @Query("alternative") boolean alternative
    );

    @NonNull
    @GET("v1/static")
    Observable<ResponseBody> mapStatic(
            @Query("key") String apiKey
            , @Query("messageType") String type
            , @Query("zoom") int zoom
            , @Query(value = "center", encoded = true) String center
            , @Query("width") int width
            , @Query("height") int height
    );

       @NonNull
       @GET("v1/static")
       Call<ResponseBody> mapStaticCall(
            @Query("key") String apiKey
            , @Query("messageType") String type
            , @Query("zoom") int zoom
            , @Query(value = "center", encoded = true) String center
            , @Query("width") int width
            , @Query("height") int height
    );



    @NonNull
    @GET("v1/reverse")
    Observable<Response<MapReverse>> mapReverse(
            @Header("Api-Key") String apiKey
            ,@Query("lat") double lat
            , @Query("lng") double lng
    );
}
