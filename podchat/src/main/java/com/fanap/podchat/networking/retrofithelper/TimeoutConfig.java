package com.fanap.podchat.networking.retrofithelper;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class TimeoutConfig {

    public OkHttpClient.Builder getClientBuilder() {

        return clientBuilder;
    }

    private OkHttpClient.Builder clientBuilder;

    public TimeoutConfig() {
    }

    private TimeoutConfig(OkHttpClient.Builder clientBuilder) {

        this.clientBuilder = clientBuilder;
    }

    public Builder newConfigBuilder() {

        return new Builder();
    }

    public OkHttpClient getClient() {
        return clientBuilder.build();
    }


    public static class Builder {

        private OkHttpClient.Builder client;

        public Builder() {
            client = new OkHttpClient.Builder();
        }

        public Builder withConnectTimeout(long uploadConnectTimeout, TimeUnit unit) {
            client.connectTimeout(uploadConnectTimeout, unit);
            return this;
        }

        public Builder withWriteTimeout(long uploadWriteTimeout, TimeUnit unit) {
            client.writeTimeout(uploadWriteTimeout, unit);
            return this;
        }

        public Builder withReadTimeout(long uploadReadTimeout, TimeUnit unit) {
            client.readTimeout(uploadReadTimeout, unit);
            return this;
        }

        public OkHttpClient.Builder getClient() {
            return client;
        }


        public TimeoutConfig build() {

            return new TimeoutConfig(client);

        }

    }


}
