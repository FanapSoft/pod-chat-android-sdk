package com.fanap.podchat.networking.retrofithelper;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collections;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
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

public class RetrofitHelperPlatformHost {

    private Retrofit.Builder retrofit;
    private Context context;

    @Nullable
    private static OkHttpClient enableTls12OnPreLollipop(Context context) {
        OkHttpClient client = null;
        if (Build.VERSION.SDK_INT < 22) {
            try {

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                        TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init((KeyStore) null);
                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                    throw new IllegalStateException("Unexpected default trust managers:"
                            + Arrays.toString(trustManagers));
                }
                X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[] { trustManager }, null);
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                        .cipherSuites(
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
                        .build();

                client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory, trustManager)
                        .connectionSpecs(Collections.singletonList(spec))
                        .build();

            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }
        return client;
    }


    public RetrofitHelperPlatformHost(@NonNull String platformHost, Context context) {
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl(platformHost)
                .client(new OkHttpClient()
                        .newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY)).build())
//                .client(enableTls12OnPreLollipop(context))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
    }

    public <T> T getService(@NonNull Class<T> tService) {
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

    public static <T> void observerRequest(Observable<Response<T>> observable, RetrofitHelperSsoHost.ApiListener<T> listener) {
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
