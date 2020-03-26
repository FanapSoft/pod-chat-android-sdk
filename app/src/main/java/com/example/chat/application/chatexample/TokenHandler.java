package com.example.chat.application.chatexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.example.chat.application.chatexample.token.ApiInterface;
import com.example.chat.application.chatexample.token.HandShakeRes;
import com.example.chat.application.chatexample.token.LoginRes;
import com.example.chat.application.chatexample.token.SSoTokenRes;
import com.example.chat.application.chatexample.token.VerifyRes;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TokenHandler {

    private static final String BASE_URL = "https://podspace.pod.ir/nzh/drive/oauth2/";
//    private static final String client_id = BaseApplication.getInstance().getString(R.string.business_cid);

    private static final long TIME_OUT = 30;
    private static final String POD_URL = "https://accounts.pod.ir";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";


    private String auth = "";
    private String number = "";
    private ITokenHandler listener;
    private String refreshToken = "";

    private SharedPreferences sharedPreference;

    private Context context;


    private void checkRefreshToken() {

        SharedPreferences a = getDefaultSharedPreferences();

        String refKey = a.getString(REFRESH_TOKEN, "");

        if (refKey != null && !refKey.isEmpty()) {

            refreshToken = refKey;

            refreshToken();
        }


    }


    TokenHandler(Context context) {
        this.context = context;
        checkRefreshToken();
    }

    void addListener(ITokenHandler iTokenHandler) {

        listener = iTokenHandler;
    }

    void handshake(String number) {

        this.number = number;

        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);


        getAPI().handShake(deviceId)
                .enqueue(new Callback<HandShakeRes>() {
                    @Override
                    public void onResponse(Call<HandShakeRes> call, Response<HandShakeRes> response) {

                        Log.i("TOKEN", "handshake done!");


                        try {
                            String keyId = response.body().getResult().getKeyId();
                            auth = keyId;
                            login();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<HandShakeRes> call, Throwable throwable) {

                        Log.i("TOKEN", "handshake failed");

                    }
                });


    }

    private SharedPreferences getDefaultSharedPreferences() {

        if (sharedPreference == null) {
            sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        }

        return sharedPreference;
    }

    private void login() {

        getAPI()
                .login(number, auth)
                .enqueue(new Callback<LoginRes>() {
                    @Override
                    public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {


                        Log.i("OTP", "Login Done!");


                    }

                    @Override
                    public void onFailure(Call<LoginRes> call, Throwable throwable) {

                        Log.i("OTP", "Login Failed");
                    }
                });


    }


    void verifyNumber(String code) {

        getAPI().verifyNumber(auth, number, code)
                .enqueue(new Callback<VerifyRes>() {
                    @Override
                    public void onResponse(Call<VerifyRes> call, Response<VerifyRes> response) {

                        Log.i("OTP", "Number verified!");

                        if (response.body() != null && response.body().getResult() != null)
                            getToken(response.body().getResult().getCode());
                        else Log.i("OTP", "Number verify failed!");


                    }

                    @Override
                    public void onFailure(Call<VerifyRes> call, Throwable throwable) {
                        Log.i("OTP", "Number verify failed!");

                    }
                });


    }


    private void getToken(final String code) {

        getAPI().getOTPToken(code)
                .enqueue(new Callback<SSoTokenRes>() {
                    @Override
                    public void onResponse(Call<SSoTokenRes> call, Response<SSoTokenRes> response) {

                        Log.d("OTP", "get token done!");

                        String accessToken = null;
                        if (response.body() != null) {

                            accessToken = response.body().getResult().getAccessToken();

                            refreshToken = response.body().getResult().getRefreshToken();

                            listener.onGetToken(accessToken);

                            saveToken(refreshToken);

                        } else {

                            Log.e("OTP", "get token failed!");

                        }


                    }

                    @Override
                    public void onFailure(Call<SSoTokenRes> call, Throwable throwable) {

                        Log.e("OTP", "get token failed!");

                    }
                });


    }

    private void saveToken(String refreshToken) {


        SharedPreferences.Editor p = getDefaultSharedPreferences().edit();

        p.putString(REFRESH_TOKEN, refreshToken);

        p.apply();

    }


    public void refreshToken() {

        getAPI()
                .refreshToken(refreshToken)
                .enqueue(new Callback<SSoTokenRes>() {
                    @Override
                    public void onResponse(Call<SSoTokenRes> call, Response<SSoTokenRes> response) {

                        Log.i("OTP", "Token Refreshed");

                        refreshToken = response.body().getResult().getRefreshToken();

                        listener.onTokenRefreshed(response.body().getResult().getAccessToken());

                        saveToken(refreshToken);
                    }

                    @Override
                    public void onFailure(Call<SSoTokenRes> call, Throwable throwable) {

                        Log.i("OTP", "Token refresh failed");

                    }
                });


    }


    private static ApiInterface getAPI() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();


        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            String token = "";

            Request request = original.newBuilder()
                    .header("Access-Token", token)
//                    .header("Client-Id", client_id)
                    .header("Content-Type", "application/json; application/x-www-form-urlencoded; charset=utf-8")
                    .method(original.method(), original.body())
                    .build();

            return chain.proceed(request);
        }).connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ApiInterface.class);

    }


    public interface ITokenHandler {

        void onGetToken(String token);

        void onTokenRefreshed(String token);
    }
}
