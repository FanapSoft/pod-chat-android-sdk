package com.fanap.podchat.networking;

import android.util.Log;

import com.fanap.podchat.ProgressHandler;
import com.fanap.podchat.chat.Chat;
import com.fanap.podchat.networking.retrofithelper.TimeoutConfig;
import com.fanap.podchat.util.ChatConstant;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ProgressResponseBody extends ResponseBody {

    private static final int CONNECT_TIMEOUT = 20;
    private static final int WRITE_TIMEOUT = 0;
    private static final int READ_TIMEOUT = 5;

    private final ResponseBody responseBody;
    private final ProgressHandler.IDownloadFile progressListener;
    private BufferedSource bufferedSource;
    private static TimeoutConfig timeoutConfig;


    public ProgressResponseBody(ResponseBody responseBody, ProgressHandler.IDownloadFile progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
    }

    public static void setTimeoutConfig(TimeoutConfig config) {
        timeoutConfig = config;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {

        if (bufferedSource == null) {

            bufferedSource = Okio.buffer(source(responseBody.source()));
        }

        return bufferedSource;
    }


    private Source source(Source source) {

        return new ForwardingSource(source) {


            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {

                long bytesRead = super.read(sink, byteCount);

                totalBytesRead += bytesRead != -1 ? bytesRead : 100L;

                if (responseBody.contentLength() < 0) {

//                    progressListener.onError("", ChatConstant.ERROR_DOWNLOAD_FILE,
//                            "");
                    return 0;
                }

                progressListener.onProgressUpdate("", totalBytesRead, responseBody.contentLength());

                final int dl_progress = (int) ((totalBytesRead * 100L) / responseBody.contentLength());

                progressListener.onProgressUpdate("", dl_progress);

                return bytesRead;
            }

        };


    }


    private static OkHttpClient.Builder getOkHttpDownloadClientBuilder(ProgressHandler.IDownloadFile listener) {

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        if (timeoutConfig == null) {

            httpClientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.MINUTES);
        } else {

            try {
                httpClientBuilder = timeoutConfig.getClientBuilder();
            } catch (Exception e) {
                Log.e(Chat.TAG, "Config Error: " + e.getMessage());
                httpClientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(READ_TIMEOUT, TimeUnit.MINUTES);


            }
        }

//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        httpClientBuilder.addInterceptor(logging);

        httpClientBuilder.addInterceptor(chain -> {
            if (listener == null) return chain.proceed(chain.request());

            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .body(new ProgressResponseBody(originalResponse.body(), listener))
                    .build();
        });


        return httpClientBuilder;
    }


    public static Retrofit getDownloadRetrofit(String baseUrl, ProgressHandler.IDownloadFile listener) {


        Gson gson = new GsonBuilder().setLenient().create();
        GsonConverterFactory factory = GsonConverterFactory.create(gson);



        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(factory)
                .client(getOkHttpDownloadClientBuilder(listener).build())
                .build();

    }
}

