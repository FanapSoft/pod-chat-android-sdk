package com.fanap.podchat.networking.retrofithelper;

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

    private Retrofit.Builder retrofit;

    public RetrofitHelperFileServer(String fileServer) {

        retrofit = new Retrofit.Builder()
                .baseUrl(fileServer)
                .client(new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    public <T> T getService(Class<T> tService) {
        return retrofit.build().create(tService);
    }

    public static <T> void request(Single<Response<T>> single, ApiListener<T> listener) {
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

    public interface ApiListener<T> {

        void onSuccess(T t);

        void onError(Throwable throwable);

        void onServerError(String errorMessage);
    }
}
