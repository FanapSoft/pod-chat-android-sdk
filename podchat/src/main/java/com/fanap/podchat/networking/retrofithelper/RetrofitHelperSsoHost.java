package com.fanap.podchat.networking.retrofithelper;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * RetrofitHelperSsoHost
 */

public class RetrofitHelperSsoHost {

    private Retrofit.Builder retrofit;

    public RetrofitHelperSsoHost(@NonNull String ssoHost) {

        retrofit = new Retrofit.Builder()
                .baseUrl(ssoHost)
                .client(new OkHttpClient().newBuilder()
                        .connectTimeout(20, TimeUnit.SECONDS) // connect timeout
                        .writeTimeout(20, TimeUnit.SECONDS) // write timeout
                        .readTimeout(20, TimeUnit.SECONDS) // read timeout
                        .addNetworkInterceptor(new HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    public <T> T getService(@NonNull Class<T> tService) {
        return retrofit.build().create(tService);
    }

    public static <T> void singleRequest(Single<Response<T>> single, ApiListener<T> listener) {
        single.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((Response<T> tResponse) -> {
            if (tResponse.isSuccessful()) {
                listener.onSuccess(tResponse.body());
            } else {
                if (tResponse.errorBody() != null) {
                    listener.onServerError(tResponse.errorBody().toString());
                }
            }
        }, listener::onError);
    }

    public static <T> void observerRequest(Observable<Response<T>> observable, ApiListener<T> listener) {
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe((Response<T> tResponse) -> {
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
