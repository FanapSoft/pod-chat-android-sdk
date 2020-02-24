package com.fanap.podchat.networking;

import com.fanap.podchat.ProgressHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final ProgressHandler.IDownloadFile progressListener;
    private BufferedSource bufferedSource;


    public ProgressResponseBody(ResponseBody responseBody, ProgressHandler.IDownloadFile progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
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

                progressListener.onProgressUpdate(totalBytesRead, responseBody.contentLength(), bytesRead == -1);

                return bytesRead;
            }

        };


    }


    public static OkHttpClient.Builder getOkHttpDownloadClientBuilder(ProgressHandler.IDownloadFile listener) {

        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

        // You might want to increase the timeout
        httpClientBuilder.connectTimeout(20, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(0, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(5, TimeUnit.MINUTES);

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

