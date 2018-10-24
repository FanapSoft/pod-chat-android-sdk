package com.fanap.podchat.networking.retrofithelper;

import android.content.Context;

import com.fanap.podchat.R;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
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

    private OkHttpClient setCertificate(){
        OkHttpClient client = null;
        {
            try {
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                InputStream inputStream = context.getResources().openRawResource(R.raw.certificate); //(.crt)
                Certificate certificate = certificateFactory.generateCertificate(inputStream);
                inputStream.close();
                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", certificate);

                // Create a TrustManager that trusts the CAs in our KeyStore.
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm);
                trustManagerFactory.init(keyStore);

                TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
                X509TrustManager x509TrustManager = (X509TrustManager) trustManagers[0];


                // Create an SSLSocketFactory that uses our TrustManager
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, new TrustManager[]{x509TrustManager}, null);
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                //create Okhttp client

                ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                        .cipherSuites(
                                CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256 ,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
                        .build();

                client = new OkHttpClient.Builder()
//                        .sslSocketFactory(sslSocketFactory, x509TrustManager)
                        .connectionSpecs(Collections.singletonList(spec))
                        .build();
            } catch (CertificateException e) {
                Logger.e(e.getMessage());
            } catch (IOException e) {
                Logger.e(e.getMessage());

            } catch (NoSuchAlgorithmException e) {
                Logger.e(e.getMessage());

            } catch (KeyStoreException e) {
                Logger.e(e.getMessage());
            } catch (KeyManagementException e) {
                Logger.e(e.getMessage());
            }
        }
        return client;
    }



    public RetrofitHelperPlatformHost(String platformHost,Context context) {
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl(platformHost)
//                .client(new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .client(setCertificate())
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
