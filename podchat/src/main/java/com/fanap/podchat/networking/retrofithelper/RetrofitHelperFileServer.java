package com.fanap.podchat.networking.retrofithelper;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * RetrofitHelperSsoHost
 */

public class RetrofitHelperFileServer {


    private int TIMEOUT = 120;
    private TimeUnit TIME_UNIT = TimeUnit.MINUTES;
    private Retrofit.Builder retrofit;
    private static TimeoutConfig timeoutConfig;


    /**
     *
     *
     * @param fileServer
     *
     *  ** IMPORTANT ** if you adding network interceptor DON'T forget to increase
     *
     *   the amount of ignoreFirstNumberOfWriteToCalls in ProgressRequestBody
     *
     *
     *
     */

    public RetrofitHelperFileServer(@NonNull String fileServer) {

        OkHttpClient client;

        if(timeoutConfig!=null){

            client = timeoutConfig.getClientBuilder()
                    .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();

        }else {

            client = new OkHttpClient().newBuilder()
//                        .retryOnConnectionFailure(true)
                    .connectTimeout(TIMEOUT, TIME_UNIT) // connect timeout
                    .writeTimeout(TIMEOUT, TIME_UNIT) // write timeout
                    .readTimeout(TIMEOUT, TIME_UNIT) // read timeout
                    .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build();

        }

        retrofit = new Retrofit.Builder()
                .baseUrl(fileServer)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    public static void setTimeoutConfig(TimeoutConfig config){

        if(config!=null){
            timeoutConfig = config;
        }
    }

    public <T> T getService(@NonNull Class<T> tService) {
        return retrofit.build().create(tService);
    }

    public static <T> void request(Single<Response<T>> single, ApiListener<T> listener) {
        single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Response<T> tResponse) -> {
            if (tResponse.isSuccessful()) {
                listener.onSuccess(tResponse.body());
            } else {
                if (tResponse.errorBody() != null) {
                    listener.onServerError(tResponse.errorBody().toString());
                }
            }
        }, listener::onError);
    }

    public interface ApiListener<T> {

        void onSuccess(T t);

        void onError(Throwable throwable);

        void onServerError(String errorMessage);
    }
}
