package com.fanap.podchat.networking.api;

import com.fanap.podchat.mainmodel.MapNeshan;
import com.fanap.podchat.mainmodel.MapRout;


import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface MapApi {
///v1/search?term={{serachTerm}}&lat={{lat}}&lng={{lng}}
    @GET("v1/search")
    Observable<Response<MapNeshan>> mapSearch(@Header("Api-Key") String apiKey
            , @Query("term") String searchTerm
            , @Query("lat") Double latitude
            , @Query("lng") Double longitude);

    @GET("v1/routing")
    Observable<Response<MapRout>> mapRouting(@Header("Api-Key") String apiKey
            , @Query("origin") String origin
            , @Query("destination") String destination
            , @Query("alternative") boolean alternative
    );
}
